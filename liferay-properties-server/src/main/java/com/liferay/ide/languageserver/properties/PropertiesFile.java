/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.languageserver.properties;

import com.liferay.ide.languageserver.LiferayLSPFile;
import com.liferay.ide.languageserver.services.BooleanService;
import com.liferay.ide.languageserver.services.FolderService;
import com.liferay.ide.languageserver.services.Service;
import com.liferay.ide.languageserver.services.StringArrayService;

import java.io.File;
import java.io.InputStream;

import java.lang.reflect.Constructor;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Terry Jia
 */
public abstract class PropertiesFile extends LiferayLSPFile {

	public PropertiesFile(File file, String[] storageFiles) {
		super(file);

		Class<?> clazz = getClass();

		for (String storageFile : storageFiles) {
			try (InputStream in = clazz.getResourceAsStream(storageFile)) {
				String source = IOUtils.toString(in, StandardCharsets.UTF_8);

				JSONObject jsonObject = new JSONObject(source);

				_checkPossibleKeys = jsonObject.getBoolean("checkPossibleKeys");

				JSONArray keyJSONArray = jsonObject.getJSONArray("keys");

				for (Object keyObject : keyJSONArray) {
					PropertyPair propertyPair = new PropertyPair();

					JSONObject keyJSONObject = (JSONObject)keyObject;

					String key = keyJSONObject.getString("key");

					try {
						boolean validateValues = keyJSONObject.getBoolean(
							"validateValues");

						if (validateValues) {
							_checkPossibleValueKeys.add(key);
						}
					}
					catch (Exception e) {
					}

					propertyPair.setKey(key);

					try {
						String comment = keyJSONObject.getString("comment");

						propertyPair.setComment(comment);
					}
					catch (Exception e) {
					}

					try {
						String value = keyJSONObject.getString("values");

						if ((value != null) && !value.equals("")) {
							Service valueService = null;

							if (value.equals("folder")) {
								valueService = new FolderService(getFile());
							}
							else if (value.equals("boolean") ||
									 value.equals("true") ||
									 value.equals("false")) {

								valueService = new BooleanService(getFile());
							}
							else if (value.startsWith("CustomLSP")) {
								String className =
									"com.liferay.ide.languageserver.services.custom." +
										value;

								Class<?> serviceClass = Class.forName(
									className);

								Constructor constructor =
									serviceClass.getConstructor(File.class);

								valueService = (Service)constructor.newInstance(
									getFile());
							}
							else {
								String[] possibleValues = value.split(",");

								valueService = new StringArrayService(
									getFile(), possibleValues);
							}

							propertyPair.setValue(valueService);
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}

					_propertyPairs.add(propertyPair);
				}
			}
			catch (Exception e) {
			}
		}
	}

	public boolean checkPossibleKeys() {
		return _checkPossibleKeys;
	}

	public List<String> checkPossibleValueKeys() {
		return _checkPossibleValueKeys;
	}

	public List<PropertyPair> getProperties() {
		return _propertyPairs;
	}

	public abstract boolean match();

	private boolean _checkPossibleKeys = false;
	private List<String> _checkPossibleValueKeys = new ArrayList<>();
	private List<PropertyPair> _propertyPairs = new ArrayList<>();

}