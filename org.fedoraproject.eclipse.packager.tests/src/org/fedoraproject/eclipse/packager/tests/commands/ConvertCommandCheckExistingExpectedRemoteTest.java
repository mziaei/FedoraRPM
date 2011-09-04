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

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.egit.core.RepositoryCache;
import org.eclipse.jgit.api.Git;
import org.fedoraproject.eclipse.packager.IFpProjectBits;
import org.fedoraproject.eclipse.packager.api.errors.CommandListenerException;
import org.fedoraproject.eclipse.packager.api.errors.CommandMisconfiguredException;
import org.fedoraproject.eclipse.packager.git.api.ConvertLocalToRemoteCommand;
import org.fedoraproject.eclipse.packager.git.api.errors.LocalProjectConversionFailedException;
import org.fedoraproject.eclipse.packager.git.api.errors.RemoteAlreadyExistsException;
import org.fedoraproject.eclipse.packager.tests.utils.git.GitConvertTestCase;
import org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils;

public class ConvertCommandCheckExistingExpectedRemoteTest
		extends GitConvertTestCase {

	public void testShouldNotThrowExceptionWhenExistingExpectedRemote()
			throws Exception {

		IFpProjectBits projectBits = FedoraPackagerUtils
				.getVcsHandler(getFedoraprojectRoot());

		// Find the local repository
		RepositoryCache repoCache = org.eclipse.egit.core.Activator
				.getDefault().getRepositoryCache();

		Git git = new Git(repoCache.lookupRepository(getiProject()
					.getFile(".git").getLocation().toFile())); //$NON-NLS-1$

		String uri = projectBits.getScmUrl();
		getProject().addRemoteRepository(uri, git);
		ConvertLocalToRemoteCommand convertCmd;
		convertCmd = (ConvertLocalToRemoteCommand) getPackager()
				.getCommandInstance(ConvertLocalToRemoteCommand.ID);
		try {
			convertCmd.call(new NullProgressMonitor());
		} catch (CommandMisconfiguredException e) {
			e.printStackTrace();
		} catch (CommandListenerException e) {
			e.printStackTrace();
		} catch (LocalProjectConversionFailedException e) {
			e.printStackTrace();
		} catch (RemoteAlreadyExistsException e) {
			e.printStackTrace();
		}
	}
}
