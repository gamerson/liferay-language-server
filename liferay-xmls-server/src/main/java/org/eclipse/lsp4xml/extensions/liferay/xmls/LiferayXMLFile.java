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

package org.eclipse.lsp4xml.extensions.liferay.xmls;

import com.liferay.ide.languageserver.services.BooleanService;
import com.liferay.ide.languageserver.services.JavaPackagesService;
import com.liferay.ide.languageserver.services.Service;
import com.liferay.ide.languageserver.services.StringArrayService;
import com.liferay.ide.languageserver.services.XMLElementsService;
import com.liferay.ide.languageserver.services.XMLService;

import java.io.File;
import java.io.InputStream;

import java.lang.reflect.Constructor;

import java.nio.charset.StandardCharsets;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import org.eclipse.lsp4xml.extensions.liferay.LiferayLSPFile;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Seiphon Wang
 */
public abstract class LiferayXMLFile extends LiferayLSPFile {

	public LiferayXMLFile(File file, String storageFile) {
		super(file);

		if (!match()) {
			return;
		}

		Class<?> clazz = getClass();

		try (InputStream inputStream = clazz.getResourceAsStream(storageFile)) {
			String source = IOUtils.toString(
				inputStream, StandardCharsets.UTF_8);

			JSONObject jsonObject = new JSONObject(source);

			JSONArray tagsJSONArray = jsonObject.getJSONArray("tags");

			scanJSONArray(tagsJSONArray, "");
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.toString());
		}
	}

	public Map<String, Service> getValidatableElementsMap() {
		return _validatableElementsMap;
	}

	public Map<String, Service> getCompletableElementsMap() {
		return _completableElementsMap;
	}

	@Override
	public abstract boolean match();

	public void scanJSONArray(JSONArray tagsJSONArray, String prefix) {
		for (Object tagObject : tagsJSONArray) {
			JSONObject tagJSONObject = (JSONObject)tagObject;

			JSONArray attributesArray = tagJSONObject.getJSONArray(
				"attributes");

			String tagName = tagJSONObject.getString("tag");

			for (Object attributeObject : attributesArray) {
				JSONObject attributeJSONObject = (JSONObject)attributeObject;

				String attributeName = attributeJSONObject.getString(
					"attribute");

				String attributeValues = attributeJSONObject.getString("value");

				boolean validateValues = attributeJSONObject.getBoolean(
					"validateValues");

				Service valueService = null;

				if (attributeValues != null) {
					if (attributeValues.equals("boolean")) {
						valueService = new BooleanService(getFile());
					}
					else if (attributeValues.equals("java-package")) {
						valueService = new JavaPackagesService(getFile());
					}
					else if (attributeValues.equals("xml-elements")) {
						String valueServiceAttrs = attributeJSONObject.getString("valueServiceAttrs");

						String[] attrs = valueServiceAttrs.split(",");

						valueService = new XMLElementsService(getFile(), attrs);
					}
					else if (attributeValues.startsWith("CustomLSP")) {
						String className =
							"com.liferay.ide.languageserver.services." +
								"custom." + attributeValues;

						String[] attrs = new String[0];

						try {
							String valueServiceAttrs = attributeJSONObject.getString("valueServiceAttrs");

							if (valueServiceAttrs != null) {
								attrs = valueServiceAttrs.split(",");
							}
						}
						catch (Exception e) {
						}

						try {
							Class<?> serviceClass = Class.forName(className);

							Constructor<?> constructor =
								serviceClass.getConstructor(File.class, String[].class);

							if (attributeValues.startsWith("CustomLSPServiceXML")) {
								valueService =
									(XMLService)constructor.newInstance(
										getFile(), attrs);
							}
							else {
								valueService = (Service)constructor.newInstance(
									getFile(), attrs);
							}

						}
						catch (Exception e) {
							LOGGER.log(Level.SEVERE, e.toString());
						}
					}
					else {
						String[] possibleValues = attributeValues.split(",");

						valueService = new StringArrayService(
							getFile(), possibleValues);
					}
				}

				if (validateValues) {
					_validatableElementsMap.put(
						prefix + "/" + tagName + "[@" + attributeName + "]",
						valueService);
				}

				_completableElementsMap.put(
						prefix + "/" + tagName + "/@" + attributeName, valueService);
			}

			JSONArray childTagsArray = tagJSONObject.getJSONArray("childTags");

			if (childTagsArray != null) {
				scanJSONArray(childTagsArray, prefix + "/" + tagName);
			}
		}
	}

	private static final Logger LOGGER = Logger.getLogger(
		LiferayXMLFile.class.getName());

	private Map<String, Service> _validatableElementsMap = new HashMap<>();
	private Map<String, Service> _completableElementsMap = new HashMap<>();

}