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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.liferay.ide.languageserver.services.StringArrayService;
import com.liferay.ide.languageserver.services.XMLElementsService;
import com.liferay.ide.languageserver.services.XMLService;

/**
 * @author Seiphon Wang
 */
public class CustomLSPServiceXMLFinderReturnTypeService extends XMLService {

	private StringArrayService _stringArrayService;
	private XMLElementsService _xmlElementsService;

	public CustomLSPServiceXMLFinderReturnTypeService(File file, String... args) {
		super(file, args);

		_stringArrayService = new StringArrayService(file, new String[] {"Collection"});
		_xmlElementsService = new XMLElementsService(file, args);
	}

	public String[] getPossibleValues() {
		String[] s1 = _stringArrayService.getPossibleValues();
		String[] s2 = _xmlElementsService.getPossibleValues();

		List<String> list = new ArrayList<>();

		for (String s : s1) {
			list.add(s);
		}

		for (String s : s2) {
			list.add(s);
		}

		return list.toArray(new String[0]);
	}
}
