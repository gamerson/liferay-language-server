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

import java.io.File;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import org.w3c.dom.Node;

/**
 * @author Seiphon Wang
 */
public class CustomLSPServiceXMLNameService extends XMLService {

	public CustomLSPServiceXMLNameService(File file, String... args) {
		super(file, args);
	}

	@Override
	public void validate(String value) throws Exception {
		value = StringEscapeUtils.unescapeJava(value);

		Node node = getDOMNode();

		String tagName = node.getNodeName();

		if ("entity".equals(tagName)) {
			if ("name".equals(getAttributeName())) {
				Matcher matcher = _classNamePattern.matcher(value);

				if (!matcher.matches()) {
					throw new Exception(
						"\"" + value + "\" is not a valid Class Name.");
				}
			}
		}
		else if ("column".equals(tagName)) {
			if ("name".equals(getAttributeName())) {
				Matcher matcher = _fieldNamePattern.matcher(value);

				if (!matcher.matches()) {
					throw new Exception(
						"\"" + value + "\" is not a valid Field Name.");
				}
			}
		}
	}

	private static Pattern _classNamePattern = Pattern.compile(
		"^[_A-Z][_a-zA-Z0-9]*");
	private static Pattern _fieldNamePattern = Pattern.compile(
		"^[_a-zA-Z][_a-zA-Z0-9]*$");

}