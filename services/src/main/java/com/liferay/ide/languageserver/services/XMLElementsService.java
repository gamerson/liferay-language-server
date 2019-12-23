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

package com.liferay.ide.languageserver.services;

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.liferay.ide.languageserver.services.util.ServiceUtil;

/**
 * @author Seiphon Wang
 */
public class XMLElementsService extends Service {

	private String[] _args;

	public XMLElementsService(File file, String... args) {
		super(file);

		_args = args;
	}

	public String[] getPossibleValues() {
		File rootFile = ServiceUtil.getRootFile(getFile());

		File targetFile = new File(rootFile, _args[0]);

		String xpathValue = _args[1];

		XPathFactory xpathFactory = XPathFactory.newInstance();

		XPath xpath = xpathFactory.newXPath();

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

		String[] possibleValues = new String[0];

		try {
			DocumentBuilder bulider = builderFactory.newDocumentBuilder();

			Document doc = bulider.parse(new FileInputStream(targetFile));

			XPathExpression compile = xpath.compile(xpathValue);

			NodeList nodes = (NodeList)compile.evaluate(doc, XPathConstants.NODESET);

			possibleValues = new String[nodes.getLength()];

			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);

				possibleValues[i] = node.getNodeValue();
			}
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.toString());
		}

		return possibleValues;
	}

	private static final Logger LOGGER = Logger.getLogger(XMLElementsService.class.getName());
}
