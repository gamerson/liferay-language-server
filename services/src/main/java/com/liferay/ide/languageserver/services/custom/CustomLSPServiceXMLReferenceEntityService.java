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

package com.liferay.ide.languageserver.services.custom;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringEscapeUtils;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author Seiphon Wang
 */
public class CustomLSPServiceXMLReferenceEntityService
	extends CustomLSPServiceXMLReferenceService {

	public CustomLSPServiceXMLReferenceEntityService(File file, String... args) {
		super(file, args);
	}

	@Override
	public String[] getPossibleValues() {
		try {
			Node node = getDOMNode();

			if (node == null) {
				return super.getPossibleValues();
			}

			NamedNodeMap attributes = node.getAttributes();

			if (attributes != null) {
				Node attribute = attributes.getNamedItem("package-path");

				if (attribute != null) {
					String packagePath = attribute.getNodeValue();

					if (packagePath.length() > 0) {
						return _getPossibleValuesByPackagePath(packagePath);
					}
				}
			}
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.toString());
		}

		return _getAllPossibleValues();
	}

	@Override
	public void validate(String value) throws Exception {
		value = StringEscapeUtils.unescapeJava(value);

		for (String possibleValue : getPossibleValues()) {
			possibleValue = StringEscapeUtils.unescapeJava(possibleValue);

			if (possibleValue.equals(value)) {
				return;
			}
		}

		throw new Exception("\"" + value + "\" is not found in package path");
	}

	private String[] _getAllPossibleValues() {
		String[] possibleValueMap = super.getPossibleValues();

		List<String> possibleValueList = new ArrayList<>();

		for (String value : possibleValueMap) {
			value = value.substring(value.indexOf("=") + 1);

			String[] values = value.split(",");

			possibleValueList.addAll(Arrays.asList(values));
		}

		String[] possibleValues = new String[possibleValueList.size()];

		possibleValueList.toArray(possibleValues);

		return possibleValues;
	}

	private String[] _getPossibleValuesByPackagePath(String packagePath) {
		String[] possibleValueMap = super.getPossibleValues();

		String possibleValueString = "";

		for (String value : possibleValueMap) {
			possibleValueString =
				possibleValueString + value + System.lineSeparator();
		}

		InputStream inputStream = new ByteArrayInputStream(
			possibleValueString.getBytes());

		Properties properties = new Properties();

		String[] possibleValues = new String[0];

		try {
			properties.load(inputStream);

			String values = properties.getProperty(packagePath);

			if (values != null) {
				possibleValues = values.split(",");
			}
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.toString());
		}

		return possibleValues;
	}

	private static final Logger LOGGER = Logger.getLogger(
		CustomLSPServiceXMLReferenceEntityService.class.getName());

}