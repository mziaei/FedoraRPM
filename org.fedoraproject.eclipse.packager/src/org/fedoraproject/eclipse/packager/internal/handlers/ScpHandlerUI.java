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
package org.fedoraproject.eclipse.packager.internal.handlers;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;
import org.fedoraproject.eclipse.packager.FedoraPackagerLogger;
import org.fedoraproject.eclipse.packager.FedoraPackagerText;
import org.fedoraproject.eclipse.packager.PackagerPlugin;
import org.fedoraproject.eclipse.packager.api.ScpCommand;
import org.fedoraproject.eclipse.packager.api.ScpResult;
import org.fedoraproject.eclipse.packager.api.errors.CommandListenerException;
import org.fedoraproject.eclipse.packager.api.errors.CommandMisconfiguredException;
import org.fedoraproject.eclipse.packager.internal.ui.ScpDialog;
import org.fedoraproject.eclipse.packager.utils.FedoraHandlerUtils;

/**
 *
 *
 */
public class ScpHandlerUI {

	private Shell shell;
	private List<IFile> resources;
	private static FedoraPackagerLogger logger;


	/**
	 * Constructs a CommitUI object
	 * @param shell
	 *            Shell to use for UI interaction. Must not be null.
	 * @param projectFiles
	 * 			  resources exists in the selected project
	 * @param logger
	 */
	public ScpHandlerUI(Shell shell, List<IFile> projectFiles, FedoraPackagerLogger logger) {
		this.shell = shell;
		this.resources = projectFiles;
		this.logger = logger;
	}


	/**
	 * Performs scp command
	 */
	public void scp() {
		ScpDialog scpDialog = new ScpDialog(shell);

		if (scpDialog.open() != IDialogConstants.OK_ID)
			return;

		final ScpCommand scpCmd = null;
		performScp(scpCmd);

	}


	/**
	 * @param scpCmd
	 */
	public static void performScp(final ScpCommand scpCmd) {
		// Do the scp
		Job job = new Job(FedoraPackagerText.ScpHandler_taskName) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				ScpResult result;


				try {
					result = scpCmd.call(monitor);
					// String message = null;
					// message = NLS
					// .bind(FedoraPackagerGitText.ConvertLocalToRemoteHandler_information,
					// localfedoraProjectRoot.getPackageName());
					// String finalMessage =
					// result.getHumanReadableMessage(message);
					// FedoraHandlerUtils
					// .showInformationDialog(
					// shell,
					// FedoraPackagerGitText.ConvertLocalToRemoteHandler_notificationTitle,
					// finalMessage);
					return Status.OK_STATUS;

				} catch (CommandMisconfiguredException e) {
					logger.logError(e.getMessage(), e);
					return FedoraHandlerUtils.errorStatus(
							PackagerPlugin.PLUGIN_ID, e.getMessage(), e);
				} catch (CommandListenerException e) {
					logger.logError(e.getMessage(), e);
					return FedoraHandlerUtils.errorStatus(
							PackagerPlugin.PLUGIN_ID, e.getMessage(), e);
					// } catch (LocalProjectConversionFailedException e) {
					// logger.logError(e.getCause().getMessage(), e);
					// return FedoraHandlerUtils
					// .errorStatus(
					// PackagerPlugin.PLUGIN_ID,
					// NLS.bind(
					// FedoraPackagerGitText.ConvertLocalToRemoteHandler_failToConvert,
					// localfedoraProjectRoot
					// .getPackageName(), e
					// .getCause().getMessage()));
				}

			}
			};
			job.setUser(true);
			job.schedule();

	}
}
