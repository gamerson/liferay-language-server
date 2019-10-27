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

package org.eclipse.lsp4xml.extensions.liferay;

import com.liferay.ide.languageserver.services.Service;
import com.liferay.ide.languageserver.services.XMLService;
import com.liferay.ide.languageserver.services.util.XpathUtil;

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
import javax.xml.xpath.XPathFactory;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4xml.dom.DOMDocument;
import org.eclipse.lsp4xml.dom.DOMElement;
import org.eclipse.lsp4xml.extensions.liferay.xmls.LiferayXMLFile;
import org.eclipse.lsp4xml.extensions.liferay.xmls.ModelHintsXMLFile;
import org.eclipse.lsp4xml.extensions.liferay.xmls.ServiceXMLFile;
import org.eclipse.lsp4xml.services.extensions.CompletionParticipantAdapter;
import org.eclipse.lsp4xml.services.extensions.ICompletionRequest;
import org.eclipse.lsp4xml.services.extensions.ICompletionResponse;
import org.w3c.dom.NodeList;

/**
 * @author Seiphon Wang
 */
public class LiferayCompletionParticipant extends CompletionParticipantAdapter {

	@Override
	public void onAttributeValue(
			String valuePrefix, ICompletionRequest request,
			ICompletionResponse response)
		throws Exception {

		DOMElement parentElement = 	request.getNode(
	).isElement() ? (DOMElement)request.getNode() : null;

		if (parentElement == null) {
			return;
		}

		DOMDocument document = request.getXMLDocument();

		URI uri = new URI(document.getDocumentURI());

		File file = new File(uri);

		_liferayXMLFile = _getCurrentLiferayXMLFile(file);

		String CurrentAttributeName = request.getCurrentAttributeName();

		Map<String, Service> elementMap = _liferayXMLFile.getCompletableElementsMap();

		XPathFactory xpathFactory = XPathFactory.newInstance();

		XPath xpath = xpathFactory.newXPath();

		String xpathString = XpathUtil.getXPath(request.getNode()) + "/@" + request.getCurrentAttributeName();

		for (String key : elementMap.keySet()) {
			if (key.equals(xpathString)) {

				try {
					XPathExpression expression = xpath.compile(key);

					Object results = expression.evaluate(document, XPathConstants.NODESET);

					NodeList nodes = (NodeList)results;

					if (nodes.getLength() < 1) {
						continue;
					}

					Service valueService = elementMap.get(key);

					if (valueService instanceof XMLService) {
						XMLService xmlService = (XMLService)valueService;

						xmlService.setDOMNode(request.getNode());

						xmlService.setAttributeName(CurrentAttributeName);
					}

					String[] values = valueService.getPossibleValues();

					for (String value : values) {
						CompletionItem item = new CompletionItem(value);

						item.setFilterText(request.getFilterForStartTagName(value));

						item.setKind(CompletionItemKind.Text);

						item.setTextEdit(
							new TextEdit(request.getReplaceRange(), value));

						item.setInsertTextFormat(InsertTextFormat.PlainText);

						response.addCompletionItem(item, true);
					}
				}
				catch (Exception e) {
					LOGGER.log(Level.SEVERE, e.toString());
				}
			}
		}
	}

	@Override
	public void onTagOpen(
			ICompletionRequest request, ICompletionResponse response)
		throws Exception {
	}

	private LiferayXMLFile _getCurrentLiferayXMLFile(File file) {
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

	private static final Logger LOGGER = Logger.getLogger(
			LiferayCompletionParticipant.class.getName());

	private static LiferayXMLFile _liferayXMLFile;

}