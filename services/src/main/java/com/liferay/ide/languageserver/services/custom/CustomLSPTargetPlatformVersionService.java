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

package com.liferay.ide.languageserver.services.custom;

import com.liferay.ide.languageserver.services.StringArrayService;

import java.io.File;
import java.io.InputStream;

import java.nio.file.Files;

import java.util.Properties;

/**
 * @author Terry Jia
 */
public class CustomLSPTargetPlatformVersionService extends StringArrayService {

	public CustomLSPTargetPlatformVersionService(File file) {
		super(
			file,
			new String[] {
				"7.0.6", "7.1.0", "7.1.1", "7.1.2", "7.1.3", "7.2.0"
			});
	}

	@Override
	public void validate(String value) throws Exception {
		File bladeBlade = new File(
			getFile().getParentFile(), ".blade.properties");

		if (!bladeBlade.exists()) {
			return;
		}

		Properties properties = new Properties();

		try (InputStream in = Files.newInputStream(bladeBlade.toPath())) {
			properties.load(in);

			String liferayVersionDefault = properties.getProperty(
				"liferay.version.default");

			if (!value.startsWith(liferayVersionDefault)) {
				throw new Exception(
					"Version " + value + " does not match with " +
						liferayVersionDefault + " in .blade.properties");
			}
		}
	}

}