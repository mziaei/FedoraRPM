/*******************************************************************************
 * Copyright (c) 2010 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat Inc. - initial API and implementation
 *******************************************************************************/
package org.fedoraproject.eclipse.packager.oldtests;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.fedoraproject.eclipse.packager.oldtests.utils.AbstractTest;
import org.fedoraproject.eclipse.packager.rpm.LocalBuildHandler;

public class LocalBuildTest extends AbstractTest {
	String[] arches = {"noarch", "i386", "i586", "i686", "x86_64", "ia64", "s390", "s390x", "ppc", "ppc64", "pseries", "ppc64pseries", "iseries", "ppc64iseries", "athlon", "alpha", "alphaev6", "sparc", "sparc64", "sparcv9", "sparcv9v", "sparc64v", "i164", "mac", "sh", "mips", "geode"};
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		handler = new LocalBuildHandler();
		handler.setDebug(true);
		Shell aShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		handler.setShell(aShell);
		handler.execute(null);
		handler.waitForJob();
	}

	public void testBuildFolder() throws Exception {
		branch.refreshLocal(IResource.DEPTH_INFINITE, null);
		IFolder buildFolder = null;
		for (String arch : arches) {
			buildFolder = (IFolder) branch.findMember(arch);
			if (buildFolder != null) {
				break;
			}
		}
		assertNotNull(buildFolder);
		assertTrue(buildFolder.members().length > 0);
	}
}
