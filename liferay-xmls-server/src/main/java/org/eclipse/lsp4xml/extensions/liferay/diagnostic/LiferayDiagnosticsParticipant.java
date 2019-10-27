/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eclipse.lsp4xml.extensions.liferay.diagnostic;

import com.liferay.ide.languageserver.services.Service;
import com.liferay.ide.languageserver.services.XMLService;

import java.io.File;

import java.net.URI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;
import org.eclipse.lsp4xml.dom.DOMAttr;
import org.eclipse.lsp4xml.dom.DOMDocument;
import org.eclipse.lsp4xml.extensions.liferay.LiferayPlugin;
import org.eclipse.lsp4xml.extensions.liferay.xmls.LiferayXMLFile;
import org.eclipse.lsp4xml.extensions.liferay.xmls.ModelHintsXMLFile;
import org.eclipse.lsp4xml.extensions.liferay.xmls.ServiceXMLFile;
import org.eclipse.lsp4xml.services.extensions.diagnostics.IDiagnosticsParticipant;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Seiphon Wang
 */
public class LiferayDiagnosticsParticipant implements IDiagnosticsParticipant {

	@Override
	public void doDiagnostics(
		DOMDocument xmlDocument, List<Diagnostic> diagnostics,
		CancelChecker monitor) {

		if (!LiferayPlugin.match(xmlDocument)) {
			return;
		}

		try {
			URI uri = new URI(xmlDocument.getDocumentURI());

			File file = new File(uri);

			_liferayXMLFile = getCurrentLiferayXMLFile(file);
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.toString());
		}

		if (_liferayXMLFile == null) {
			return;
		}

		Map<String, Service> elementMap = _liferayXMLFile.getValidatableElementsMap();

		XPathFactory xpathFactory = XPathFactory.newInstance();

		XPath xpath = xpathFactory.newXPath();

		for (String key : elementMap.keySet()) {
			String attributeName = key.substring(
				key.indexOf("[@") + 2, key.indexOf("]"));

			try {
				XPathExpression expression = xpath.compile(key);

				Object results = expression.evaluate(
					xmlDocument, XPathConstants.NODESET);

				NodeList nodes = (NodeList)results;

				if (nodes.getLength() < 1) {
					continue;
				}

				for (int i = 0; i < nodes.getLength(); i++) {
					Node node = nodes.item(i);

					NamedNodeMap attributes = node.getAttributes();

					if (attributes.getLength() < 1) {
						continue;
					}

					Node att = attributes.getNamedItem(attributeName);

					Service valueService = elementMap.get(key);

					if (valueService instanceof XMLService) {
						XMLService xmlService = (XMLService)valueService;

						xmlService.setDOMNode(node);

						xmlService.setAttributeName(attributeName);
					}

					try {
						valueService.validate(att.getNodeValue());
					}
					catch (Exception e) {
						createDiagnostic(diagnostics, (DOMAttr)att, e);
					}
				}
			}
			catch (XPathExpressionException xpee) {
			}
		}
	}

	public LiferayXMLFile getCurrentLiferayXMLFile(File file) {
		List<LiferayXMLFile> liferayXMLFiles = new ArrayList<>();

		if ((_liferayXMLFile != null) && _liferayXMLFile.match()) {
			return _liferayXMLFile;
		}

		liferayXMLFiles.add(new ServiceXMLFile(file));
		liferayXMLFiles.add(new ModelHintsXMLFile(file));

		for (LiferayXMLFile liferayXMLFile : liferayXMLFiles) {
			if (liferayXMLFile.match()) {
				return liferayXMLFile;
			}
		}

		return null;
	}

	private static void createDiagnostic(
		List<Diagnostic> diagnostics, DOMAttr attribute, Exception e) {

		Diagnostic diagnostic = new Diagnostic();

		diagnostic.setSeverity(DiagnosticSeverity.Warning);

		diagnostic.setRange(
			new Range(
				new Position(0, attribute.getStart()),
				new Position(0, attribute.getEnd())));

		diagnostic.setMessage(e.getMessage());

		diagnostic.setSource("xml");

		diagnostics.add(diagnostic);
	}

	private static final Logger LOGGER = Logger.getLogger(
		LiferayDiagnosticsParticipant.class.getName());

	private static LiferayXMLFile _liferayXMLFile;

}