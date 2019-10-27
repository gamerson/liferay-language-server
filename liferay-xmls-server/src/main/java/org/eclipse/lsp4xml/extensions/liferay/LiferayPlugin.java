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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPathExpressionException;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4xml.dom.DOMDocument;
import org.eclipse.lsp4xml.extensions.liferay.diagnostic.LiferayDiagnosticsParticipant;
import org.eclipse.lsp4xml.extensions.references.XMLReferencesManager;
import org.eclipse.lsp4xml.services.extensions.ICompletionParticipant;
import org.eclipse.lsp4xml.services.extensions.IXMLExtension;
import org.eclipse.lsp4xml.services.extensions.XMLExtensionsRegistry;
import org.eclipse.lsp4xml.services.extensions.diagnostics.IDiagnosticsParticipant;
import org.eclipse.lsp4xml.services.extensions.save.ISaveContext;

/**
 * @author Seiphon Wang
 */
public class LiferayPlugin implements IXMLExtension {

	public static boolean match(DOMDocument document) {
		for (String liferayXml : LIFERAY_XMLS) {
			if (document.getDocumentURI().endsWith(liferayXml)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void doSave(ISaveContext context) {

		// TODO Auto-generated method stub

	}

	ICompletionParticipant completionParticipant =
		new LiferayCompletionParticipant();
	IDiagnosticsParticipant diagnosticsParticipant =
		new LiferayDiagnosticsParticipant();

	@Override
	public void start(InitializeParams params, XMLExtensionsRegistry registry) {
		registry.registerCompletionParticipant(completionParticipant);
		registry.registerDiagnosticsParticipant(diagnosticsParticipant);

		try {
			XMLReferencesManager.getInstance(
			).referencesFor(
				LiferayPlugin::match
			).from(
				"//*:servlet-mapping/*:servlet-name/*/*"
			).to(
				"//*[local-name()='servlet']/*[local-name() ='servlet-name']/text()"
			);
		}
		catch (XPathExpressionException xpee) {
			LOGGER.log(
				Level.SEVERE,
				"Error while registering XML references for liferay XML files",
				xpee);
		}
	}

	@Override
	public void stop(XMLExtensionsRegistry registry) {
		registry.unregisterCompletionParticipant(completionParticipant);
		registry.unregisterDiagnosticsParticipant(diagnosticsParticipant);
	}

	private static String[] LIFERAY_XMLS = {
		"service.xml", "portlet.xml", "ext-model-hints.xml", "ext-spring.xml",
		"liferay-display.xml", "liferay-layout-templates.xml",
		"liferay-portlet.xml", "liferay-hook.xml", "liferay-look-and-feel.xml",
		"portlet-model-hints.xml", "liferay-friendly-url-routes.xml",
		"resource-action-mapping.xml"
	};

	private static Logger LOGGER = Logger.getLogger(
		LiferayPlugin.class.getName());

}