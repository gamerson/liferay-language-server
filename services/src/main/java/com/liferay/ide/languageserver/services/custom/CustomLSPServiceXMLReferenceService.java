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

import com.liferay.ide.languageserver.services.XMLService;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Seiphon Wang
 */
public class CustomLSPServiceXMLReferenceService extends XMLService {

	public CustomLSPServiceXMLReferenceService(File file, String... args) {
		super(file, args);
	}

	@Override
	public String[] getPossibleValues() {
		_getValuesFromResource();

		if (_possibleValues != null) {
			return _possibleValues;
		}

		File parentFile = _getParentFile(getFile().getParentFile());

		List<String> result = new ArrayList<>();

		if ((parentFile != null) && parentFile.exists()) {
			result = _getValuesFromScanning(parentFile);
		}

		result.addAll(_getValuesFromResource());

		_possibleValues = new String[result.size()];

		result.toArray(_possibleValues);

		return _possibleValues;
	}

	private static void _findServiceXMLFiles(File dir) {
		for (File file : dir.listFiles()) {
			if (file.isFile() && "service.xml".equals(file.getName())) {
				_serviceXMLFiles.add(file);

				continue;
			}

			if ((file.listFiles() == null) || "src".equals(file.getName()) ||
				"bin".equals(file.getName()) ||
				"build".equals(file.getName()) ||
				".settings".equals(file.getName())) {

				continue;
			}

			_findServiceXMLFiles(file);
		}

		return;
	}

	private File _getParentFile(File parentFile) {
		File retVal = null;

		if (parentFile.isDirectory() &&
			"modules".equals(parentFile.getName())) {

			return parentFile;
		}

		if (parentFile.getParentFile() != null) {
			retVal = _getParentFile(parentFile.getParentFile());
		}

		return retVal;
	}

	private List<String> _getValuesFromResource() {
		Class<?> clazz = getClass();

		BufferedReader bufferedReader = null;

		List<String> result = new ArrayList<>();

		try (InputStream inputStream = clazz.getResourceAsStream(
				"/reference-package.properties")) {

			bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				result.add(line);
			}
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.toString());
		}

		return result;
	}

	private List<String> _getValuesFromScanning(File parentFile) {
		List<String> result = new ArrayList<>();

		_serviceXMLFiles.clear();

		_findServiceXMLFiles(parentFile);

		DocumentBuilderFactory doucumentBuilderfactory =
			DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder documentBuilder =
				doucumentBuilderfactory.newDocumentBuilder();

			for (File file : _serviceXMLFiles) {
				if (file.exists()) {
					Document document = documentBuilder.parse(file);

					NodeList serviceBuilders = document.getElementsByTagName(
						"service-builder");

					Node serviceBuilder = serviceBuilders.item(0);

					NamedNodeMap serviceBuilderAttributes =
						serviceBuilder.getAttributes();

					Node packageattribute =
						serviceBuilderAttributes.getNamedItem("package-path");

					String packagePath = packageattribute.getNodeValue();

					NodeList entities = document.getElementsByTagName("entity");

					String entityNames = "";

					for (int i = 0; i < entities.getLength(); i++) {
						Node entity = entities.item(i);

						NamedNodeMap entityAttributes = entity.getAttributes();

						Node nameAttribute = entityAttributes.getNamedItem(
							"name");

						String entityName = nameAttribute.getNodeValue();

						entityNames = entityNames + entityName + ",";
					}

					entityNames = entityNames.substring(
						0, entityNames.lastIndexOf(","));

					result.add(packagePath + "=" + entityNames);
				}
			}
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.toString());
		}

		HashSet<String> hashSet = new HashSet<>(result);

		result.clear();

		result.addAll(hashSet);

		return result;
	}

	private static final Logger LOGGER = Logger.getLogger(
		CustomLSPServiceXMLReferenceService.class.getName());

	private static String[] _possibleValues;
	private static List<File> _serviceXMLFiles = new ArrayList<>();

}