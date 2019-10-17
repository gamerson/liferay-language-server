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