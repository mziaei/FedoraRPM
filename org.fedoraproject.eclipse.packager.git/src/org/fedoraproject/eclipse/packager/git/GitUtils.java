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
package org.fedoraproject.eclipse.packager.git;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.InvalidMergeHeadsException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.fedoraproject.eclipse.packager.FedoraSSL;
import org.fedoraproject.eclipse.packager.FedoraSSLFactory;
import org.fedoraproject.eclipse.packager.git.api.errors.LocalProjectConversionFailedException;

/**
 * Utility class for Fedora Git related things.
 */
public class GitUtils {

	/**
	 * @param gitBaseUrl
	 * @param packageName
	 * @return The full clone URL for the given package.
	 */
	public static String getFullGitURL(String gitBaseUrl, String packageName) {
		return gitBaseUrl + packageName + GitConstants.GIT_REPO_SUFFIX;
	}

	/**
	 * @return The anonymous base URL to clone from.
	 */
	public static String getAnonymousGitBaseUrl() {
		return GitConstants.ANONYMOUS_PROTOCOL
				+ GitPreferencesConstants.DEFAULT_CLONE_BASE_URL;
	}

	/**
	 * @param username
	 * @return The SSH base URL to clone from.
	 */
	public static String getAuthenticatedGitBaseUrl(String username) {
		return GitConstants.AUTHENTICATED_PROTOCOL + username
				+ GitConstants.USERNAME_SEPARATOR
				+ GitPreferencesConstants.DEFAULT_CLONE_BASE_URL;
	}

	/**
	 * Determine the default Git base URL for cloning. Based on ~/.fedora.cert
	 * 
	 * @return The default Git base URL for cloning.
	 */
	public static String getDefaultGitBaseUrl() {
		// Figure out if we have an anonymous or a FAS user
		String user = FedoraSSLFactory.getInstance().getUsernameFromCert();
		String gitURL;
		if (!user.equals(FedoraSSL.UNKNOWN_USER)) {
			gitURL = GitUtils.getAuthenticatedGitBaseUrl(user);
		} else {
			gitURL = GitUtils.getAnonymousGitBaseUrl();
		}
		return gitURL;
	}

	/**
	 * Create local branches based on existing remotes (uses the JGit API).
	 * 
	 * @param git
	 * @param monitor
	 * @throws CoreException
	 */
	public static void createLocalBranches(Git git, IProgressMonitor monitor)
			throws CoreException {
		monitor.beginTask(FedoraPackagerGitText.FedoraPackagerGitCloneWizard_createLocalBranchesJob,
				IProgressMonitor.UNKNOWN);
		try {
			// get a list of remote branches
			ListBranchCommand branchList = git.branchList();
			branchList.setListMode(ListMode.REMOTE); // want all remote branches
			List<Ref> remoteRefs = branchList.call();
			for (Ref remoteRef : remoteRefs) {
				String name = remoteRef.getName();
				int index = (Constants.R_REMOTES + "origin/").length(); //$NON-NLS-1$
				// Remove "refs/remotes/origin/" part in branch name
				name = name.substring(index);
				// Use "f14"-like branch naming
				if (name.endsWith("/" + Constants.MASTER)) { //$NON-NLS-1$
					index = name.indexOf("/" + Constants.MASTER); //$NON-NLS-1$
					name = name.substring(0, index);
				}
				// Create all remote branches, except "master"
				if (!name.equals(Constants.MASTER)) {
					CreateBranchCommand branchCreateCmd = git.branchCreate();
					branchCreateCmd.setName(name);
					// Need to set starting point this way in order for tracking
					// to work properly. See:
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=333899
					branchCreateCmd.setStartPoint(remoteRef.getName());
					// Add remote tracking config in order to not confuse
					// fedpkg
					branchCreateCmd.setUpstreamMode(SetupUpstreamMode.TRACK);
					branchCreateCmd.call();
				}
			}
		} catch (JGitInternalException e) {
			e.printStackTrace();
		} catch (RefAlreadyExistsException e) {
			e.printStackTrace();
		} catch (RefNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidRefNameException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds the corresponding remote repository as the default name 'origin' to
	 * the existing local repository (uses the JGit API)
	 * 
	 * @param git
	 * @param uri
	 * @param monitor
	 * @throws LocalProjectConversionFailedException 
	 */
	public static void addRemoteRepository(Git git, String uri,
			IProgressMonitor monitor) throws LocalProjectConversionFailedException {
		monitor.beginTask(FedoraPackagerGitText.FedoraPackagerGitCloneWizard_createLocalBranchesJob,
				IProgressMonitor.UNKNOWN);
		RemoteConfig config;
		try {
			config = new RemoteConfig(git.getRepository().getConfig(), "origin"); //$NON-NLS-1$
			config.addURI(new URIish(uri));
			String dst = Constants.R_REMOTES + config.getName();
			RefSpec refSpec = new RefSpec();
			refSpec = refSpec.setForceUpdate(true);
			refSpec = refSpec.setSourceDestination(
					Constants.R_HEADS + "*", dst + "/*"); //$NON-NLS-1$ //$NON-NLS-2$

			config.addFetchRefSpec(refSpec);
			config.update(git.getRepository().getConfig());
			git.getRepository().getConfig().save();

			// fetch all the remote branches,
			// create corresponding branches locally and merge them
			FetchCommand fetch = git.fetch();
			fetch.setRemote("origin"); //$NON-NLS-1$
			fetch.setTimeout(0);
			fetch.setRefSpecs(refSpec);
			fetch.call();

		} catch (URISyntaxException e) {
			throw new LocalProjectConversionFailedException(e.getCause().getMessage(), e);
		} catch (JGitInternalException e) {
			throw new LocalProjectConversionFailedException(e.getCause().getMessage(), e);
		} catch (InvalidRemoteException e) {
			throw new LocalProjectConversionFailedException(e.getCause().getMessage(), e);
		} catch (IOException e) {
			throw new LocalProjectConversionFailedException(e.getCause().getMessage(), e);
		}
	}

	/**
	 * Merges remote HEAD with local HEAD (uses the JGit API)
	 * 
	 * @param git
	 * @param monitor
	 * @throws LocalProjectConversionFailedException 
	 */
	public static void mergeLocalRemoteBranches(Git git,
			IProgressMonitor monitor) throws LocalProjectConversionFailedException {
		monitor.beginTask(FedoraPackagerGitText.FedoraPackagerGitCloneWizard_createLocalBranchesJob,
				IProgressMonitor.UNKNOWN);
		MergeCommand merge = git.merge();
		try {
			merge.include(git.getRepository().getRef(
					Constants.R_REMOTES + "origin/" + Constants.MASTER)); //$NON-NLS-1$
		} catch (IOException e) {
			throw new LocalProjectConversionFailedException(e.getCause().getMessage(), e);
		}

		try {
			merge.call();
		} catch (NoHeadException e) {
			throw new LocalProjectConversionFailedException(e.getCause().getMessage(), e);
		} catch (ConcurrentRefUpdateException e) {
			throw new LocalProjectConversionFailedException(e.getCause().getMessage(), e);
		} catch (CheckoutConflictException e) {
			throw new LocalProjectConversionFailedException(e.getCause().getMessage(), e);
		} catch (InvalidMergeHeadsException e) {
			throw new LocalProjectConversionFailedException(e.getCause().getMessage(), e);
		} catch (WrongRepositoryStateException e) {
			throw new LocalProjectConversionFailedException(e.getCause().getMessage(), e);
		} catch (NoMessageException e) {
			throw new LocalProjectConversionFailedException(e.getCause().getMessage(), e);
		}

	}
}
