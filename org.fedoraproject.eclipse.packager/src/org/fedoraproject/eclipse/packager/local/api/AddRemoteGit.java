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
package org.fedoraproject.eclipse.packager.local.api;

import java.net.MalformedURLException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.egit.core.RepositoryCache;
import org.eclipse.egit.ui.internal.dialogs.NewRemoteDialog;
import org.eclipse.egit.ui.internal.fetch.SimpleConfigureFetchDialog;
import org.eclipse.egit.ui.internal.push.SimpleConfigurePushDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.fedoraproject.eclipse.packager.FedoraPackagerLogger;
import org.fedoraproject.eclipse.packager.FedoraPackagerText;
import org.fedoraproject.eclipse.packager.IProjectRoot;

/**
 * Job for downloading sources.
 */
@SuppressWarnings("restriction")
public class AddRemoteGit extends Job {

	private IProjectRoot fedoraProjectRoot;
	private FedoraPackagerLogger logger;
	private Shell shell;
	private IProject project;
//	private String downloadUrlPreference = null;

	/**
	 * @param jobName
	 * @param fedoraProjectRoot
	 * @param shell
	 *            A valid shell.
	 * @param project
	 */
	public AddRemoteGit(String jobName, IProjectRoot fedoraProjectRoot,
			Shell shell, IProject project) {
		super(jobName);
		this.fedoraProjectRoot = fedoraProjectRoot;
		this.logger = FedoraPackagerLogger.getInstance();
		this.project = project;
		this.shell = shell;
//		this.downloadUrlPreference = downloadUrlPreference;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Add remote git repository", //$NON-NLS-1$
				IProgressMonitor.UNKNOWN);
//
//		try {
			// Find repo we've just created and set gitRepo
			RepositoryCache repoCache = org.eclipse.egit.core.Activator
			.getDefault().getRepositoryCache();
			Repository[] repo = repoCache.getAllRepositories();
			for (Repository repository : repo) {
				if (repository.getWorkTree().getName().equals(project.getName())) {
					NewRemoteDialog nrd = new NewRemoteDialog(shell, repository);
					if (nrd.open() != Window.OK)
						return null;
					if (nrd.getPushMode())
						SimpleConfigurePushDialog.getDialog(shell, repository,
								nrd.getName()).open();
					else
						SimpleConfigureFetchDialog.getDialog(shell, repository,
								nrd.getName()).open();
					return Status.OK_STATUS;
				}
			}
//			if (downloadUrlPreference != null) {
//				// Only set URL explicitly if set in preferences. Lookaside
//				// cache falls back to the default URL if not set.
//				download.setDownloadURL(downloadUrlPreference);
//			}
//			logger.logDebug(NLS.bind(FedoraPackagerText.callingCommand,
//					DownloadSourceCommand.class.getName()));
//			download.call(monitor);
//		} catch (final SourcesUpToDateException e) {
//			logger.logDebug(e.getMessage(), e);
//			if (!suppressSourcesUpToDateInfo) {
//				FedoraHandlerUtils
//						.showInformationDialog(shell,
//								fedoraProjectRoot.getProductStrings().getProductName(),
//								e.getMessage());
//			}
//			return Status.OK_STATUS;
//		} catch (DownloadFailedException e) {
//			logger.logError(e.getMessage(), e);
//			return FedoraHandlerUtils.errorStatus(PackagerPlugin.PLUGIN_ID,
//					e.getMessage(), e);
//		} catch (CommandMisconfiguredException e) {
//			// This shouldn't happen, but report error anyway
//			logger.logError(e.getMessage(), e);
//			return FedoraHandlerUtils.errorStatus(PackagerPlugin.PLUGIN_ID,
//					e.getMessage(), e);
//		} catch (CommandListenerException e) {
//			if (e.getCause() instanceof InvalidCheckSumException) {
//				String message = e.getCause().getMessage();
//				logger.logError(message, e.getCause());
//				return FedoraHandlerUtils.errorStatus(PackagerPlugin.PLUGIN_ID,
//						message, e.getCause());
//			}
//			logger.logError(e.getMessage(), e);
//			return FedoraHandlerUtils.errorStatus(PackagerPlugin.PLUGIN_ID,
//					e.getMessage(), e);
//		} catch (MalformedURLException e) {
//			// setDownloadUrl failed
//			logger.logError(e.getMessage(), e);
//			return FedoraHandlerUtils.errorStatus(PackagerPlugin.PLUGIN_ID,
//					e.getMessage(), e);
//		} finally {
//			monitor.done();
//		}
		return Status.OK_STATUS;
	}

}
