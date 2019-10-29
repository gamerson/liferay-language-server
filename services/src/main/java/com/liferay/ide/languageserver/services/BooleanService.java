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

/**
 * @author Terry Jia
 */
public class BooleanService extends Service {

	public BooleanService(File file) {
		super(file);
	}

	@Override
	public String[] getPossibleValues() {
		return new String[] {Boolean.TRUE.toString(), Boolean.FALSE.toString()};
	}

	@Override
	public void validate(String value) throws Exception {
		if (!value.equalsIgnoreCase("true") &&
			!value.equalsIgnoreCase("false")) {

			throw new Exception("only boolean values are accepted");
		}
	}

}