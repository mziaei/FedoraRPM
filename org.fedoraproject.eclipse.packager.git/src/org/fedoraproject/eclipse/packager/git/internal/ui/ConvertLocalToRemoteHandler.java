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
package org.fedoraproject.eclipse.packager.git.internal.ui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.egit.core.RepositoryCache;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.fedoraproject.eclipse.packager.FedoraPackagerLogger;
import org.fedoraproject.eclipse.packager.FedoraPackagerText;
import org.fedoraproject.eclipse.packager.FedoraSSLFactory;
import org.fedoraproject.eclipse.packager.IProjectRoot;
import org.fedoraproject.eclipse.packager.PackagerPlugin;
import org.fedoraproject.eclipse.packager.api.FedoraPackagerAbstractHandler;
import org.fedoraproject.eclipse.packager.api.errors.InvalidProjectRootException;
import org.fedoraproject.eclipse.packager.utils.FedoraHandlerUtils;
import org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils;
import org.eclipse.egit.core.op.BranchOperation;
import org.eclipse.egit.core.op.FetchOperation;
import org.eclipse.egit.core.op.MergeOperation;

/**
 * Handler to convert the local project Git repository to a working directory of
 * the main package's remote Git repository.
 * 
 */
public class ConvertLocalToRemoteHandler extends FedoraPackagerAbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		final Shell shell = getShell(event);
		final FedoraPackagerLogger logger = FedoraPackagerLogger.getInstance();
		final IProjectRoot fedoraProjectRoot;
		IResource eventResource = FedoraHandlerUtils.getResource(event);
		try {
			fedoraProjectRoot = FedoraPackagerUtils
					.getProjectRoot(eventResource);
		} catch (InvalidProjectRootException e) {
			logger.logError(FedoraPackagerText.invalidFedoraProjectRootError, e);
			FedoraHandlerUtils.showErrorDialog(shell, "Error", //$NON-NLS-1$
					FedoraPackagerText.invalidFedoraProjectRootError);
			return null;
		}
		Job job = new Job(fedoraProjectRoot.getProductStrings()
				.getProductName()) {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {

				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						String message = null;
						String dst = null;
						final IProject project = fedoraProjectRoot.getProject();
						// retrieve FAS username
						String fasUserName = FedoraSSLFactory.getInstance()
								.getUsernameFromCert();

						// Find repo we've just created and set gitRepo
						RepositoryCache repoCache = org.eclipse.egit.core.Activator
								.getDefault().getRepositoryCache();
						Repository[] repo = repoCache.getAllRepositories();
						for (Repository repository : repo) {
							if (repository.getWorkTree().getName()
									.equals(project.getName())) {
								RemoteConfig config = null;
								try {
									config = new RemoteConfig(repository
											.getConfig(), "origin"); //$NON-NLS-1$
									URIish uri = new URIish(
											"git://pkgs.fedoraproject.org/eclipse-mercurial.git"); //$NON-NLS-1$
									config.addURI(uri);

									dst = Constants.R_REMOTES
											+ config.getName();
									RefSpec refSpec = new RefSpec();
									refSpec = refSpec.setForceUpdate(true);
									refSpec = refSpec
											.setSourceDestination(
													Constants.R_HEADS + "*", dst + "/*"); //$NON-NLS-1$ //$NON-NLS-2$

									config.addFetchRefSpec(refSpec);
									config.update(repository.getConfig());
									repository.getConfig().save();

								} catch (URISyntaxException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}

								FetchOperation fetch = new FetchOperation(
										repository, config, 0, false);
								try {
									fetch.run(monitor);
								} catch (InvocationTargetException e) {
									e.printStackTrace();
								}
								try {
									String branchname = repository.getBranch();
									repository.getAllRefs();
									Ref ref = repository
											.getRef(Constants.MASTER);

								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								try {
									BranchOperation branch = new BranchOperation(
											repository, dst + Constants.MASTER);
									branch.execute(monitor);

									branch = new BranchOperation(repository,
											Constants.R_HEADS
													+ Constants.MASTER);
									branch.execute(monitor);

									MergeOperation merge = new MergeOperation(
											repository, dst + Constants.MASTER);
									merge.execute(monitor);

								} catch (CoreException e) {
									e.printStackTrace();
								}
							}
						}
						// set the project property to main fedora packager's
						// property
						try {
							project.setPersistentProperty(
									PackagerPlugin.PROJECT_PROP, "true"); //$NON-NLS-1$
							project.setPersistentProperty(
									PackagerPlugin.PROJECT_LOCAL_PROP,
									null);
							message = NLS
									.bind(FedoraPackagerText.ConvertToGitHandler_ListHeader,
											project.getName());
							project.refreshLocal(IResource.DEPTH_INFINITE,
									monitor);
						} catch (CoreException e) {
							logger.logError(e.getMessage(), e);
							FedoraHandlerUtils
									.showErrorDialog(
											shell,
											FedoraPackagerText.ConvertToGitHandler_Error,
											NLS.bind(
													FedoraPackagerText.ConvertToGitHandler_TrackAddingFailure,
													project.toString(),
													e.getMessage()));
						}
						FedoraHandlerUtils
								.showInformationDialog(
										shell,
										FedoraPackagerText.ConvertToGitHandler_NotificationTitle,
										message);

					}
				});

				return Status.OK_STATUS;
			}
		};

		// Suppress UI progress reporting. This is done by sub-jobs within.
		job.setSystem(true);
		job.schedule();
		return null;
	}
}

// /**
// * Determine the Git clone URL in the following order:
// * <ol>
// * <li>Use the Git base URL as set by the preference (if any) or</li>
// * <li>Check if ~/.fedora.cert is present, and if so retrieve the user name
// * from it.</li>
// * <li>If all else fails, or anonymous checkout is specified,
// * construct an anonymous clone URL</li>
// * </ol>
// *
// * @return The full clone URL based on the package name.
// */
// private String getGitCloneURL(String fasUserName, String packageName) {
//		String gitBaseURL = "pkgs.fedoraproject.org/"; //$NON-NLS-1$;
// // if (gitBaseURL != null && !page.getCloneAnonymousButtonChecked()) {
// if (gitBaseURL != null) {
// return GitUtils.getFullGitURL(gitBaseURL, packageName);
// // } else if (!fasUserName.equals(FedoraSSL.UNKNOWN_USER) &&
// !page.getCloneAnonymousButtonChecked()) {
// } else if (!fasUserName.equals(FedoraSSL.UNKNOWN_USER)) {
// return GitUtils.getFullGitURL(
// GitUtils.getAuthenticatedGitBaseUrl(fasUserName),
// packageName);
// } else {
// // anonymous
// return GitUtils.getFullGitURL(GitUtils.getAnonymousGitBaseUrl(),
// packageName);
// }
// }

// NewRemoteDialog nrd = new NewRemoteDialog(shell, repository);
// nrd.open();
// if (nrd.getPushMode())
// SimpleConfigurePushDialog.getDialog(shell, repository,
// nrd.getName()).open();
// else
// SimpleConfigureFetchDialog.getDialog(shell, repository,
// nrd.getName()).open();

// SelectUriWizard wiz = new SelectUriWizard(true);
// wiz.getShell().open();

// SimpleConfigureFetchDialog.getDialog(shell, repository).open();

// String srcName = getGitCloneURL(fasUserName, project.getName());

// // run the fetch command
// FetchCommand command = new FetchCommand(repository);
// command.setRemote("origin");
// command.setProgressMonitor(null);
// command.setTagOpt(TagOpt.FETCH_TAGS);
// command.setTimeout(0);
// if (credentialsProvider != null)
// command.setCredentialsProvider(credentialsProvider);
//
// List<RefSpec> specs = calculateRefSpecs(dst);
// command.setRefSpecs(specs);
//
// command.call();
// config.addFetchRefSpec(s);

// FedoraPackagerGitFetchOperation fetch =
// new FedoraPackagerGitFetchOperation(repository);
// try {
//
// fetch.setFetchURI(getGitCloneURL()).setPackageName(project.getName());
// } catch (URISyntaxException e1) {
// // TODO Auto-generated catch block
// e1.printStackTrace();
// }

// UIJob job = new UIJob(fedoraProjectRoot.getProductStrings().getProductName())
// {
// @Override
// public IStatus runInUIThread(IProgressMonitor monitor) {
// final IProject project = fedoraProjectRoot.getProject();
// Display.getDefault().syncExec(new Runnable() {
// @Override
// public void run() {
// // Find repo we've just created and set gitRepo
// RepositoryCache repoCache = org.eclipse.egit.core.Activator
// .getDefault().getRepositoryCache();
// Repository[] repo = repoCache.getAllRepositories();
// for (Repository repository : repo) {
// if (repository.getWorkTree().getName().equals(project.getName())) {
// RevWalk rw = new RevWalk(repository);
// ObjectId id;
// try {
// id = repository.resolve(repository.getFullBranch());
// String commitId = rw.parseCommit(id).name();
//
// } catch (AmbiguousObjectException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// } catch (IOException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
//
// NewRemoteDialog nrd = new NewRemoteDialog(shell, repository);
// nrd.open();
// if (nrd.getPushMode())
// SimpleConfigurePushDialog.getDialog(shell, repository,
// nrd.getName()).open();
// else
// SimpleConfigureFetchDialog.getDialog(shell, repository,
// nrd.getName()).open();
// }
// }
// }
// });
// return Status.OK_STATUS;
// }
//
// };
// job.setUser(true);
// job.schedule();
// return null;

// Job job = new Job(fedoraProjectRoot.getProductStrings().getProductName()) {
//
// @Override
// protected IStatus run(IProgressMonitor monitor) {
// String message = null;
// final IProject project = fedoraProjectRoot.getProject();
// Display.getDefault().syncExec(new Runnable() {
// @Override
// public void run() {
// // Find repo we've just created and set gitRepo
// RepositoryCache repoCache = org.eclipse.egit.core.Activator
// .getDefault().getRepositoryCache();
// Repository[] repo = repoCache.getAllRepositories();
// for (Repository repository : repo) {
// if (repository.getWorkTree().getName().equals(project.getName())) {
// NewRemoteDialog nrd = new NewRemoteDialog(shell, repository);
// nrd.open();
// // if (nrd.open() != Window.OK) {
// // }
//
// if (nrd.getPushMode())
// SimpleConfigurePushDialog.getDialog(shell, repository,
// nrd.getName()).open();
// else
// SimpleConfigureFetchDialog.getDialog(shell, repository,
// nrd.getName()).open();
// }
// }
// }
// });
//
// return Status.OK_STATUS;
// }
// };
//
// // Suppress UI progress reporting. This is done by sub-jobs within.
// job.setSystem(true);
// job.schedule();

// try {
// Job addRemoteJob = new
// AddRemoteGit(fedoraProjectRoot.getProductStrings().getProductName(),
// fedoraProjectRoot, shell, project, event);
// addRemoteJob.setUser(true);
// addRemoteJob.schedule();

// message = NLS.bind
// (LocalFedoraPackagerText.ConvertToGitHandler_ListHeader,
// projectName);
// ConfigureRemoteCommand crc = new ConfigureRemoteCommand();
// crc.execute(event);

// NewRemoteDialog nrd = new NewRemoteDialog(getShell(event), repository);
// if (nrd.open() != Window.OK)
// return null;
// String projectName = project.getName();
// project.setPersistentProperty(
//		PackagerPlugin.PROJECT_PROP, "true"); //$NON-NLS-1$
// project.setPersistentProperty(
// LocalFedoraPackagerPlugin.PROJECT_PROP, null);
// fedoraProjectRoot.getProject().refreshLocal
// (IResource.DEPTH_INFINITE, monitor);
// } catch (CoreException e) {
// logger.logError(e.getMessage(), e);
// FedoraHandlerUtils.showErrorDialog(shell,
// LocalFedoraPackagerText.ConvertToGitHandler_Error,
// NLS.bind(
// LocalFedoraPackagerText.ConvertToGitHandler_TrackAddingFailure,
// project.toString(),e.getMessage()));
// }
// FedoraHandlerUtils.showInformationDialog(shell,
// LocalFedoraPackagerText.ConvertToGitHandler_NotificationTitle,
// message);