/*******************************************************************************
 * Copyright (c) 2010-2011 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat Inc. - initial API and implementation
 *******************************************************************************/
package org.fedoraproject.eclipse.packager.tests.utils;

import org.eclipse.jface.window.Window;

public class BodhiNewDialogStub {
	
	public BodhiNewDialogStub() {
	}
	
	public String getBugs() {
		return "";
	}

	public String getBuildName() {
		return "";
	}

	public String getNotes() {
		//TODO fix this properly
		return null;
	}

	public String getRelease() {
		//TODO fix this properly
		return "";
	}

	public String getRequest() {
		return "testing";
	}

	public String getType() {
		return "bugfix";
	}

	public int getReturnCode() {
		return Window.OK;
	}

	public int open() {
		return Window.OK;
	}

}
