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

package com.liferay.ide.languageserver.services;

import java.io.File;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Terry Jia
 */
public class FolderService extends Service {

	public FolderService(File file) {
		super(file);
	}

	@Override
	public String[] getPossibleValues() {
		File parentFile = getFile().getParentFile();

		String[] files = parentFile.list();

		return Stream.of(
			files
		).map(
			fileName -> new File(parentFile, fileName)
		).filter(
			File::isDirectory
		).map(
			File::getName
		).collect(
			Collectors.toList()
		).toArray(
			new String[0]
		);
	}

	@Override
	public void validate(String value) throws Exception {
		File file = new File(getFile().getParentFile(), value);

		if (!file.exists()) {
			throw new Exception(file + " does not exist.");
		}
	}

}