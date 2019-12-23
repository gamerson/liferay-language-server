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

import org.w3c.dom.Node;

/**
 * @author Seiphon Wang
 */
public class XMLService extends Service {

	public XMLService(File file, String... args) {
		super(file, args);
	}

	public String getAttributeName() {
		return _attributeName;
	}

	public Node getDOMNode() {
		return _node;
	}

	public void setAttributeName(String name) {
		_attributeName = name;
	}

	public void setDOMNode(Node node) {
		_node = node;
	}

	private String _attributeName;
	private Node _node;

}