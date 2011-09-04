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

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.egit.core.RepositoryCache;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.fedoraproject.eclipse.packager.IFpProjectBits;
import org.fedoraproject.eclipse.packager.PackagerPlugin;
import org.fedoraproject.eclipse.packager.git.api.ConvertLocalToRemoteCommand;
import org.fedoraproject.eclipse.packager.tests.utils.git.GitConvertTestCase;
import org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils;

public class ConvertLocalToRemoteCommandTest extends GitConvertTestCase {

	public void testConvertCommand() throws Exception {
		boolean localRefsOk = false;

		ConvertLocalToRemoteCommand convertCmd;
		convertCmd = (ConvertLocalToRemoteCommand) getPackager()
				.getCommandInstance(ConvertLocalToRemoteCommand.ID);
		convertCmd.call(new NullProgressMonitor());

		// Make sure the property of the project changed
		// from local to main fedorapackager
		assertTrue(getiProject().getPersistentProperty(
				PackagerPlugin.PROJECT_PROP).equals("true")); //$NON-NLS-1$
		assertNull(getiProject().getPersistentProperty(
				PackagerPlugin.PROJECT_LOCAL_PROP));

		// Make sure the url for remote repository is correct
		IFpProjectBits projectBits = FedoraPackagerUtils
				.getVcsHandler(getFedoraprojectRoot());
		assertTrue(projectBits.getScmUrl().contains(
				"pkgs.fedoraproject.org/alchemist.git")); //$NON-NLS-1$

		RepositoryCache repoCache = org.eclipse.egit.core.Activator
				.getDefault().getRepositoryCache();

		// Find the local repository in the project location
		Git git = new Git(repoCache.lookupRepository(getiProject()
				.getFile(".git").getLocation().toFile())); //$NON-NLS-1$

		// Make sure the current checked out branch is master
		assertTrue(git.getRepository().getBranch().equals("master")); //$NON-NLS-1$

		// Check a random branch (f10),
		// and make sure both remote and local version of it exists
		List<Ref> remoteRefs = git.branchList().setListMode(ListMode.REMOTE)
				.call();
		assertTrue(remoteRefs
				.toString()
				.contains(
						"refs/remotes/origin/f10=2f12e50b9860dc05cf3ac09c1cd82b45fae28637")); //$NON-NLS-1$

		Map<String, Ref> localRefs = git.getRepository().getRefDatabase()
				.getRefs(Constants.R_REFS);
		for (Ref refValue : localRefs.values()) {
			if (refValue.toString().contains(
					"refs/heads/f10=2f12e50b9860dc05cf3ac09c1cd82b45fae28637")) { //$NON-NLS-1$
				localRefsOk = true;
			}
		}
		assertTrue(localRefsOk);
	}
}
