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

import java.io.File;

/**
 * @author Seiphon Wang
 */
public class ModelHintsXMLFile extends LiferayXMLFile {

	public ModelHintsXMLFile(File file) {
		super(file, "/model-hints.json");
	}

	@Override
	public boolean match() {
		String fileName = getFile().getName();

		return fileName.contains("model-hints.xml");
	}

}