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

import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author Terry Jia
 */
public class StringArrayService extends Service {

	public StringArrayService(File file, String[] possibleValues) {
		super(file);

		_possibleValues = possibleValues;
	}

	@Override
	public String[] getPossibleValues() {
		return _possibleValues;
	}

	@Override
	public void validate(String value) throws Exception {
		for (String possibleValue : _possibleValues) {
			possibleValue = StringEscapeUtils.unescapeJava(possibleValue);

			if (possibleValue.equals(value)) {
				return;
			}
		}

		throw new Exception("\"" + value + "\" is not in possible values.");
	}

	private String[] _possibleValues;

}