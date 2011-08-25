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
package org.fedoraproject.eclipse.packager.api;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.fedoraproject.eclipse.packager.IFpProjectBits;
import org.fedoraproject.eclipse.packager.IProjectRoot;
import org.fedoraproject.eclipse.packager.api.errors.CommandListenerException;
import org.fedoraproject.eclipse.packager.api.errors.CommandMisconfiguredException;
import org.fedoraproject.eclipse.packager.api.errors.DownloadFailedException;
import org.fedoraproject.eclipse.packager.api.errors.FedoraPackagerCommandInitializationException;
import org.fedoraproject.eclipse.packager.api.errors.SourcesUpToDateException;
import org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils;

/**
 * A class used to execute a {@code download sources} command. It has setters
 * for all supported options and arguments of this command and a
 * {@link #call(IProgressMonitor)} method to finally execute the command. Each
 * instance of this class should only be used for one invocation of the command
 * (means: one call to {@link #call(IProgressMonitor)})
 * 
 */
public class ConvertLocalToRemoteCommand extends
		FedoraPackagerCommand<ConvertLocalResult> {

	private IProjectRoot localFedoraProjectRoot;
	
	/**
	 * The unique ID of this command.
	 */
	public static final String ID = "ConvertLocalToRemoteCommand"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fedoraproject.eclipse.packager.api.FedoraPackagerCommand#initialize
	 * (org.fedoraproject.eclipse.packager.FedoraProjectRoot)
	 */
	@Override
	public void initialize(IProjectRoot projectRoot)
			throws FedoraPackagerCommandInitializationException {
		super.initialize(projectRoot);
		this.localFedoraProjectRoot = projectRoot;
	}

	/**
	 * Implementation of the {@code DownloadSourcesCommand}.
	 * 
	 * @param monitor
	 *            The main progress monitor. Each file to download is executed
	 *            as a subtask.
	 * @throws SourcesUpToDateException
	 *             If the source files are already downloaded and up-to-date.
	 * @throws CommandMisconfiguredException
	 *             If the command was not properly configured when it was
	 *             called.
	 * @throws DownloadFailedException
	 *             If the download of some source failed.
	 * @throws CommandListenerException
	 *             If some listener detected a problem.
	 * @return The result of this command.
	 */
	@Override
	public ConvertLocalResult call(IProgressMonitor monitor)
			throws SourcesUpToDateException, DownloadFailedException,
			CommandMisconfiguredException, CommandListenerException {
		try {
			callPreExecListeners();
		} catch (CommandListenerException e) {
			if (e.getCause() instanceof CommandMisconfiguredException) {
				// explicitly throw the specific exception
				throw (CommandMisconfiguredException) e.getCause();
			}
			throw e;
		}

		IFpProjectBits projectBits = FedoraPackagerUtils
				.getVcsHandler(localFedoraProjectRoot);
		
		projectBits.addRemoteOrigin();
		String name = projectBits.getCurrentBranchName();
		String name1 = projectBits.getDist();
		String name2 = projectBits.getRawCurrentBranchName();
		boolean ok = projectBits.hasLocalChanges(localFedoraProjectRoot);


		
//		String branchName = projectBits.getCurrentBranchName();
//		String url = projectBits.getScmUrl();
//		
//		boolean ok = projectBits.hasLocalChanges(localFedoraProjectRoot);
//		Git git = projectBits.getGit();
//		FetchCommand fetch = git.fetch();
//		fetch.setRemote("origin"); //$NON-NLS-1$
//		fetch.setTimeout(0);
		
//		String dst = null;
//		RemoteConfig config = null;
//		String uriString = null;
//		URIish uri = null;
//
//		// retrieve FAS username
//		String fasUserName = FedoraSSLFactory.getInstance()
//				.getUsernameFromCert();
//
//		// Find repo we've just created and set gitRepo
//		RepositoryCache repoCache = org.eclipse.egit.core.Activator
//				.getDefault().getRepositoryCache();
//		Repository[] repo = repoCache.getAllRepositories();
//		for (Repository repository : repo) {
//			if (repository.getWorkTree().getName().equals(packageName)) {
////				uriString = getGitFetchURL(packageName, fasUserName);
//				try {
//					uri = new URIish(uriString);
//					config = new RemoteConfig(repository.getConfig(), "origin"); //$NON-NLS-1$
//					config.addURI(uri);
//
//					dst = Constants.R_REMOTES + config.getName();
//					RefSpec refSpec = new RefSpec();
//					refSpec = refSpec.setForceUpdate(true);
//					refSpec = refSpec.setSourceDestination(Constants.R_HEADS
//							+ "*", dst + "/*"); //$NON-NLS-1$ //$NON-NLS-2$
//
//					config.addFetchRefSpec(refSpec);
//					config.update(repository.getConfig());
//					repository.getConfig().save();
//
//				} catch (URISyntaxException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//				FetchOperation fetch = new FetchOperation(repository, config,
//						0, false);
//				try {
//					fetch.run(monitor);
//				} catch (InvocationTargetException e) {
//					try {
//						throw new InvocationTargetException(e);
//					} catch (InvocationTargetException e1) {
//						// TODO -> fix this in handler add the message
//						e1.printStackTrace();
//					}
//				}
//			}
//		}

		ConvertLocalResult result = new ConvertLocalResult();

		// Call post-exec listeners
		callPostExecListeners();
		result.setSuccessful(true);
		setCallable(false);
		return result;
	}

	@Override
	protected void checkConfiguration() throws CommandMisconfiguredException {
		// We are good to go with the defaults. No-Op.
	}
}
