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
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringEscapeUtils;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author Seiphon Wang
 */
public class CustomLSPServiceXMLReferencePackagePathService
	extends CustomLSPServiceXMLReferenceService {

	public CustomLSPServiceXMLReferencePackagePathService(File file, String... args) {
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
				Node attribute = attributes.getNamedItem("entity");

				if (attribute != null) {
					String entityName = attribute.getNodeValue();

					if (entityName.length() > 0) {
						return _getPossibleValuesByEntityName(entityName);
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

		throw new Exception("\"" + value + "\" is not in possible values.");
	}

	private String[] _getAllPossibleValues() {
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

			Set<Object> packagePathSet = properties.keySet();

			possibleValues = new String[packagePathSet.size()];

			List<Object> packagePathList = new ArrayList<>(packagePathSet);

			packagePathList.toArray(possibleValues);
		}
		catch (IOException ioe) {
			LOGGER.log(Level.SEVERE, ioe.toString());
		}

		return possibleValues;
	}

	private String[] _getPossibleValuesByEntityName(String entityName) {
		String[] possibleValueMap = super.getPossibleValues();

		List<String> possibleValueList = new ArrayList<>();

		for (String valueMap : possibleValueMap) {
			String[] keyValue = valueMap.split("=");

			keyValue[keyValue.length - 1].split(",");

			String[] values = keyValue[keyValue.length - 1].split(",");

			for (String value : values) {
				if (value.equals(entityName)) {
					possibleValueList.add(keyValue[0]);

					break;
				}
			}
		}

		String[] possibleValues = new String[possibleValueList.size()];

		possibleValueList.toArray(possibleValues);

		return possibleValues;
	}

	private static final Logger LOGGER = Logger.getLogger(
		CustomLSPServiceXMLReferencePackagePathService.class.getName());

}