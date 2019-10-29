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

package com.liferay.ide.languageserver.properties;

import com.liferay.ide.languageserver.services.Service;

/**
 * @author Terry Jia
 */
public class PropertyPair {

	public String getCommennt() {
		return _comment;
	}

	public String getKey() {
		return _key;
	}

	public Service getValue() {
		return _value;
	}

	public void setComment(String comment) {
		_comment = comment;
	}

	public void setKey(String key) {
		_key = key;
	}

	public void setValue(Service value) {
		_value = value;
	}

	private String _comment;
	private String _key;
	private Service _value;

}