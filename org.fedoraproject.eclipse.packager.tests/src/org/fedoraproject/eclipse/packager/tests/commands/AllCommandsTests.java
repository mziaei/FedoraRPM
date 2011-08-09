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
package org.fedoraproject.eclipse.packager.tests.commands;

import org.junit.runners.Suite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	FedoraPackagerCommandTest.class,
	DownloadSourceCommandTest.class,
	UploadSourceCommandTest.class,
	KojiBuildCommandTest.class,
	RpmBuildCommandTest.class,
	RpmEvalCommandTest.class,
	SCMMockBuildCommandGitTest.class,
	MockBuildCommandTest.class
})

public class AllCommandsTests {
	// empty
}
