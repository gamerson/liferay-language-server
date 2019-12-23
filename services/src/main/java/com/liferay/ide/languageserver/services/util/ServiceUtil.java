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

package com.liferay.ide.languageserver.services.util;

import java.io.File;

/**
 * @author Seiphon Wang
 */
public class ServiceUtil {

	public static File getRootFile(File file) {
		File parent = file.getParentFile();

		File buildGradleFile = new File(parent, "build.gradle");

		if (buildGradleFile.exists()) {
			return parent;
		}

		return getRootFile(parent);
	}

}
