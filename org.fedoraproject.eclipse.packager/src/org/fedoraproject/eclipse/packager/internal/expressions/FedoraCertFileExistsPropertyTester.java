/*******************************************************************************
 * Copyright (c) 2010-2012 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat Inc. - initial API and implementation
 *******************************************************************************/
package org.fedoraproject.eclipse.packager.internal.expressions;

import java.io.File;

import org.eclipse.core.expressions.PropertyTester;
import org.fedoraproject.eclipse.packager.FedoraSSL;

/**
 * Custom property tester which checks for existence of ~/.fedora.cert. This is
 * useful for enablement tests for context menu items of the "Fedora Packager"
 * context menu.
 *
 */
public class FedoraCertFileExistsPropertyTester extends PropertyTester {

	/*
	 *  receiver == the IResource object which gets enriched 
	 *  expectedValue == the value we expect. Should be either true or false.
	 *                   If it's false, this is a no-op.
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (expectedValue instanceof Boolean && ((Boolean) expectedValue).booleanValue()) {
			File fedoraCert = new File(FedoraSSL.DEFAULT_CERT_FILE);
			return fedoraCert.exists();
		}
		return false;
	}

}
