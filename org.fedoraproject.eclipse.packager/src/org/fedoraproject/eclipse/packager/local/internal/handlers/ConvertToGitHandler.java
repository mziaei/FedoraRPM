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
package org.fedoraproject.eclipse.packager.local.internal.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.fedoraproject.eclipse.packager.FedoraPackagerLogger;
import org.fedoraproject.eclipse.packager.FedoraPackagerText;
import org.fedoraproject.eclipse.packager.IProjectRoot;
import org.fedoraproject.eclipse.packager.PackagerPlugin;
import org.fedoraproject.eclipse.packager.api.FedoraPackagerAbstractHandler;
import org.fedoraproject.eclipse.packager.api.errors.InvalidProjectRootException;
import org.fedoraproject.eclipse.packager.local.LocalFedoraPackagerPlugin;
import org.fedoraproject.eclipse.packager.local.LocalFedoraPackagerText;
import org.fedoraproject.eclipse.packager.utils.FedoraHandlerUtils;
import org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils;
import org.eclipse.egit.ui.internal.repository.tree.command.ConfigureRemoteCommand;

/**
 * Handler to convert the local project Git repository
 * to a working directory of the main package's remote
 * Git repository. 
 * 
 */
@SuppressWarnings("restriction")
public class ConvertToGitHandler extends FedoraPackagerAbstractHandler {

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
		Job job = new Job(fedoraProjectRoot.getProductStrings().getProductName()) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				String message = null;
				IProject project = fedoraProjectRoot.getProject();
				try {
					String projectName = project.getName();
					project.setPersistentProperty(
							PackagerPlugin.PROJECT_PROP, "true"); //$NON-NLS-1$
					project.setPersistentProperty(
							LocalFedoraPackagerPlugin.PROJECT_PROP, null);
					fedoraProjectRoot.getProject().refreshLocal
							(IResource.DEPTH_INFINITE, monitor);
//					message = NLS.bind
//							(LocalFedoraPackagerText.ConvertToGitHandler_ListHeader, 
//									projectName);
					ConfigureRemoteCommand crc = new ConfigureRemoteCommand();
					crc.execute(event);
				} catch (CoreException e) {
					logger.logError(e.getMessage(), e);
					FedoraHandlerUtils.showErrorDialog(shell,
							LocalFedoraPackagerText.ConvertToGitHandler_Error,
							NLS.bind(
								LocalFedoraPackagerText.ConvertToGitHandler_TrackAddingFailure,
								project.toString(),e.getMessage()));
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				FedoraHandlerUtils.showInformationDialog(shell,
						LocalFedoraPackagerText.ConvertToGitHandler_NotificationTitle,
							message);

				return Status.OK_STATUS;
			}

		};
		
		// Suppress UI progress reporting. This is done by sub-jobs within.
		job.setSystem(true);
		job.schedule();

		return null;
	}
}