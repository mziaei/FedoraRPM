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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.fedoraproject.eclipse.packager.FedoraPackagerLogger;
import org.fedoraproject.eclipse.packager.FedoraPackagerText;
import org.fedoraproject.eclipse.packager.IProjectRoot;
import org.fedoraproject.eclipse.packager.PackagerPlugin;
import org.fedoraproject.eclipse.packager.api.FedoraPackager;
import org.fedoraproject.eclipse.packager.api.FedoraPackagerAbstractHandler;
import org.fedoraproject.eclipse.packager.api.ScpCommand;
import org.fedoraproject.eclipse.packager.api.ScpResult;
import org.fedoraproject.eclipse.packager.api.errors.CommandListenerException;
import org.fedoraproject.eclipse.packager.api.errors.CommandMisconfiguredException;
import org.fedoraproject.eclipse.packager.api.errors.FedoraPackagerCommandInitializationException;
import org.fedoraproject.eclipse.packager.api.errors.FedoraPackagerCommandNotFoundException;
import org.fedoraproject.eclipse.packager.api.errors.InvalidProjectRootException;
import org.fedoraproject.eclipse.packager.utils.FedoraHandlerUtils;
import org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils;

/**
 * Class responsible for copying selected .spec and .src.rpm files from local to
 * remote (fedorapeople.org) It will provide a list of files to be sent and let
 * the user choose the latest version needed to be reviewed
 */
public class ScpHandler extends FedoraPackagerAbstractHandler {
	ScpCommand scpCmd;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final Shell shell = getShell(event);
		final FedoraPackagerLogger logger = FedoraPackagerLogger.getInstance();
		final IProjectRoot localfedoraProjectRoot;
		try {
			IResource eventResource = FedoraHandlerUtils.getResource(event);
			localfedoraProjectRoot = FedoraPackagerUtils
					.getProjectRoot(eventResource);
		} catch (InvalidProjectRootException e) {
			logger.logError(FedoraPackagerText.invalidFedoraProjectRootError, e);
			FedoraHandlerUtils.showErrorDialog(shell, "Error", //$NON-NLS-1$
					FedoraPackagerText.invalidFedoraProjectRootError);
			return null;
		}

		final FedoraPackager packager = new FedoraPackager(
				localfedoraProjectRoot);
		try {
			// Get ConvertLocalToRemoteCommand from Fedora packager
			// registry
			scpCmd = (ScpCommand) packager
					.getCommandInstance(ScpCommand.ID);
		} catch (FedoraPackagerCommandNotFoundException e) {
			logger.logError(e.getMessage(), e);
			FedoraHandlerUtils.showErrorDialog(shell,
					localfedoraProjectRoot.getProductStrings()
							.getProductName(), e.getMessage());
			return null;
		} catch (FedoraPackagerCommandInitializationException e) {
			logger.logError(e.getMessage(), e);
			FedoraHandlerUtils.showErrorDialog(shell,
					localfedoraProjectRoot.getProductStrings()
							.getProductName(), e.getMessage());
			return null;
		}

		String message = FedoraPackagerText.ScpHandler_ListHeader;

		final ListSelectionDialog lsd;
		List<IFile> projectFiles = new ArrayList<IFile>();
		IResource[] members = null;
		try {
			members = localfedoraProjectRoot.getProject().members();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < members.length; i++) {
			if (members[i] instanceof IFile
					&& (members[i].getName().endsWith(".spec") || //$NON-NLS-1$
							members[i].getName().endsWith(".src.rpm"))) { //$NON-NLS-1$
				projectFiles.add((IFile) members[i]);
			}
		}

		IFile[] fileSet =  projectFiles.toArray(new IFile[] {});

		lsd = new ListSelectionDialog(
				shell, fileSet, new ArrayContentProvider(),
				new WorkbenchLabelProvider(),
				"Select the proper .spec and .src.rpm files to be sent to fedorapeople.org:");
		int buttonCode = lsd.open();
		if (buttonCode == Window.OK){
			for (Object selected: lsd.getResult()){
				String selectedFile = ((IFile) selected).getName();
				scpCmd.setFilesToSCP(selectedFile);
			}
		}

		// Do the converting
		Job job = new Job(FedoraPackagerText.ScpHandler_taskName) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask(FedoraPackagerText.ScpHandler_taskName,
						IProgressMonitor.UNKNOWN);

//				final ScpCommand scpCmd;
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
		return null;
	}

}
