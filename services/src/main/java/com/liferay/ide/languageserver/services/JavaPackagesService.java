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

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author Seiphon Wang
 */
public class JavaPackagesService extends Service {

	public JavaPackagesService(File file) {
		super(file);
	}

	@Override
	public String[] getPossibleValues() {
		File projectRootFile = _getProjectRootFile(getFile());

		File javaSourceFolder = new File(projectRootFile, "src/main/java");

		Set<String> packageNames = new HashSet<>();

		if ((javaSourceFolder != null) && javaSourceFolder.exists()) {
			_scanProjectPackages(
				javaSourceFolder, javaSourceFolder, packageNames);
		}

		return packageNames.toArray(new String[0]);
	}

	@Override
	public void validate(String value) throws Exception {
		value = StringEscapeUtils.unescapeJava(value);

		for (String possibleValue : getPossibleValues()) {
			possibleValue = StringEscapeUtils.unescapeJava(possibleValue);

			if (possibleValue.equals(value)) {
				Matcher matcher = _pakageNamePattern.matcher(value);

				if (matcher.matches()) {
					return;
				}

				throw new Exception(
					"\"" + value + "\" is not a valid Package path.");
			}
		}

		throw new Exception("\"" + value + "\" is not in possible values.");
	}

	private String _getPackageName(File dir, File prefixFile) {
		String prefixPath = prefixFile.getAbsolutePath();
		String path = dir.getAbsolutePath();

		if (path.length() == prefixPath.length()) {
			return null;
		}

		String packageName = path.substring(prefixPath.length() + 1);

		String os = System.getProperty("os.name");

		if (os.toLowerCase().startsWith("win")) {
			packageName = packageName.replace("\\", ".");
		}
		else {
			packageName = packageName.replace("/", ".");
		}

		return packageName;
	}

	private File _getProjectRootFile(File file) {
		File parentFile = file.getParentFile();

		if (parentFile == null) {
			return null;
		}

		File[] childFiles = parentFile.listFiles();

		for (File childFile : childFiles) {
			if (childFile.isFile() &&
				"build.gradle".equals(childFile.getName())) {

				return parentFile;
			}
		}

		return _getProjectRootFile(parentFile);
	}

	private void _scanProjectPackages(
		File dir, File prefixFile, Set<String> packageNames) {

		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				String packageName = _getPackageName(dir, prefixFile);

				if (packageName != null) {
					packageNames.add(packageName);
				}

				_scanProjectPackages(file, prefixFile, packageNames);
			}
		}
	}

	private static Pattern _pakageNamePattern = Pattern.compile(
		"^([_a-z][_a-zA-Z0-9]*)([.]([_a-zA-Z][_a-zA-Z0-9]*))*");

}