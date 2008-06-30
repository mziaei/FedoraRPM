/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Red Hat - initial API and implementation
 *******************************************************************************/

package org.eclipse.linuxtools.rpm.ui.editor.parser;


public class SpecfileTag extends SpecfileElement {
	
	static final int INT = 0;
	static final int STRING = 1;
	int tagType;
	
	String stringValue;
	int intValue;
	
	public SpecfileTag(String name, String value, Specfile specfile) {
		setName(name);
		this.stringValue = value;
		this.tagType = STRING;
		super.setSpecfile(specfile);
	}
	
	public String getStringValue() {
		if (tagType == INT) {
			return Integer.toString(intValue);
		}
		return resolve(stringValue);
	}
	public void setStringValue(String value) {
		this.stringValue = value;
	}
	
	public SpecfileTag(String name, int value, Specfile specfile) {
		setName(name);
		this.intValue = value;
		this.tagType = INT;
		super.setSpecfile(specfile);
	}
	
	public int getIntValue() {
		return intValue;
	}
	
	public void setIntValue(int value) {
		this.intValue = value;
	}
	
	@Override
	public String toString() {
		if (tagType == INT) {
			return getName() + ": " + getIntValue();
		}
		String tagValue = getStringValue();
		if ((tagValue != null) && (tagValue.length() > 0) && (tagValue.indexOf("%") > 0)) {
			return getName() + ": " + super.resolve(tagValue);
		}
		return getName() + ": " + getStringValue();
	}

	public int getTagType() {
		return tagType;
	}

	public void setTagType(int tagType) {
		this.tagType = tagType;
	}
	
}
