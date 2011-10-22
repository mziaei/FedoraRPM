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
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
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


//		Button open = new Button (shell, SWT.PUSH);
//		open.setText ("Prompt for a String");
//		open.addSelectionListener (new SelectionAdapter () {
//			public void widgetSelected (SelectionEvent e) {
				final Shell dialog = new Shell (shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
				dialog.setText("Dialog Shell");
				FormLayout formLayout = new FormLayout ();
				formLayout.marginWidth = 10;
				formLayout.marginHeight = 10;
				formLayout.spacing = 10;
				dialog.setLayout (formLayout);

				Label label = new Label (dialog, SWT.NONE);
				label.setText ("Type a String:");
				FormData data = new FormData ();
				label.setLayoutData (data);

				Button cancel = new Button (dialog, SWT.PUSH);
				cancel.setText ("Cancel");
				data = new FormData ();
				data.width = 60;
				data.right = new FormAttachment (100, 0);
				data.bottom = new FormAttachment (100, 0);
				cancel.setLayoutData (data);
				cancel.addSelectionListener (new SelectionAdapter () {
					public void widgetSelected (SelectionEvent e) {
						System.out.println("User cancelled dialog");
						dialog.close ();
					}
				});

				final Text text = new Text (dialog, SWT.BORDER);
				data = new FormData ();
				data.width = 200;
				data.left = new FormAttachment (label, 0, SWT.DEFAULT);
				data.right = new FormAttachment (100, 0);
				data.top = new FormAttachment (label, 0, SWT.CENTER);
				data.bottom = new FormAttachment (cancel, 0, SWT.DEFAULT);
				text.setLayoutData (data);

				Button ok = new Button (dialog, SWT.PUSH);
				ok.setText ("OK");
				data = new FormData ();
				data.width = 60;
				data.right = new FormAttachment (cancel, 0, SWT.DEFAULT);
				data.bottom = new FormAttachment (100, 0);
				ok.setLayoutData (data);
				ok.addSelectionListener (new SelectionAdapter () {
					public void widgetSelected (SelectionEvent e) {
						System.out.println ("User typed: " + text.getText ());
						dialog.close ();
					}
				});

				dialog.setDefaultButton (ok);
				dialog.pack ();
				dialog.open ();
//			}
//		});


//		final ListSelectionDialog lsd;
//		List<IFile> projectFiles = new ArrayList<IFile>();
//		IResource[] members = null;
//		try {
//			members = localfedoraProjectRoot.getProject().members();
//		} catch (CoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		for (int i = 0; i < members.length; i++) {
//			if (members[i] instanceof IFile
//					&& (members[i].getName().endsWith(".spec") || //$NON-NLS-1$
//							members[i].getName().endsWith(".src.rpm"))) { //$NON-NLS-1$
//				projectFiles.add((IFile) members[i]);
//			}
//		}
//
//		IFile[] fileSet =  projectFiles.toArray(new IFile[] {});
//
//		lsd = new ListSelectionDialog(
//				shell, fileSet, new ArrayContentProvider(),
//				new WorkbenchLabelProvider(),
//				"Select the proper .spec and .src.rpm files to be sent to fedorapeople.org:");
//		int buttonCode = lsd.open();
//		if (buttonCode == Window.OK){
//			for (Object selected: lsd.getResult()){
//				String selectedFile = ((IFile) selected).getName();
//				scpCmd.setFilesToSCP(selectedFile);
//			}
//		}
//		scpCmd.setFasAccount(localfedoraProjectRoot.getProject().)

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
