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

package com.liferay.ide.languageserver.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Terry Jia
 * @author Seiphon Wang
 */
public class FileUtil {

	public static String[] readLinesFromFile(File file) {
		return readLinesFromFile(file, false);
	}

	public static String[] readLinesFromFile(
		File file, boolean includeNewlines) {

		if (!file.exists()) {
			return null;
		}

		List<String> lines = new ArrayList<>();

		try (FileReader fileReader = new FileReader(file)) {
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				StringBuffer contents = new StringBuffer(line);

				if (includeNewlines) {
					contents.append(System.getProperty("line.separator"));
				}

				lines.add(contents.toString());
			}
		}
		catch (Exception e) {
		}

		return lines.toArray(new String[0]);
	}

	public static String[] readLinesFromString(String content) {
		return readLinesFromString(content, false);
	}

	public static String[] readLinesFromString(
		String content, boolean includeNewlines) {

		if (content.isEmpty()) {
			return null;
		}

		List<String> lines = new ArrayList<>();

		try (InputStreamReader inputStreamReader = new InputStreamReader(
				new ByteArrayInputStream(content.getBytes()))) {

			BufferedReader bufferedReader = new BufferedReader(
				inputStreamReader);

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				StringBuffer contents = new StringBuffer(line);

				if (includeNewlines) {
					contents.append(System.getProperty("line.separator"));
				}

				lines.add(contents.toString());
			}
		}
		catch (Exception e) {
		}

		return lines.toArray(new String[0]);
	}

}