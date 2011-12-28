/*******************************************************************************
 * Copyright (c) 2011 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat Inc. - initial API and implementation
 *******************************************************************************/
package org.fedoraproject.eclipse.packager.rpm.api;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.fedoraproject.eclipse.packager.BranchConfigInstance;
import org.fedoraproject.eclipse.packager.FedoraPackagerLogger;
import org.fedoraproject.eclipse.packager.IProjectRoot;
import org.fedoraproject.eclipse.packager.api.LinkedMessageDialog;
import org.fedoraproject.eclipse.packager.rpm.RpmText;
import org.fedoraproject.eclipse.packager.rpm.api.errors.MockNotInstalledException;
import org.fedoraproject.eclipse.packager.rpm.api.errors.UserNotInMockGroupException;
import org.fedoraproject.eclipse.packager.utils.FedoraHandlerUtils;

/**
 * Superclass for mock jobs, which share the same
 * job listener.
 *
 */
public abstract class AbstractMockJob extends Job {

	protected MockBuildResult result;
	protected Shell shell;
	protected IProjectRoot fpr;
	protected BranchConfigInstance bci;
	
	/**
	 * @param name
	 * @param shell 
	 * @param fedoraProjectRoot 
	 * @param bci 
	 */
	public AbstractMockJob(String name, Shell shell, IProjectRoot fedoraProjectRoot, BranchConfigInstance bci) {
		super(name);
		this.shell = shell;
		this.fpr = fedoraProjectRoot;
		this.bci = bci;
	}
	
	/**
	 * 
	 * @return A job listener for the {@code done} event.
	 */
	protected IJobChangeListener getMockJobFinishedJobListener() {
		IJobChangeListener listener = new JobChangeAdapter() {

			// We are only interested in the done event
			@Override
			public void done(IJobChangeEvent event) {
				FedoraPackagerLogger logger = FedoraPackagerLogger
						.getInstance();
				IStatus jobStatus = event.getResult();
				if (jobStatus.getSeverity() == IStatus.CANCEL) {
					// cancelled log this in any case
					logger.logInfo(RpmText.AbstractMockJob_mockCancelledMsg);
					FedoraHandlerUtils.showInformationDialog(shell, fpr
							.getProductStrings().getProductName(),
							RpmText.AbstractMockJob_mockCancelledMsg);
					return;
				}
				// Handle NPE case of the result when user is not in mock group
				// of when mock is not installed. Just return in that case, since
				// The job will show appropriate messages to the user.
				if (jobStatus.getSeverity() == IStatus.INFO
						&& jobStatus.getException() != null
						&& (jobStatus.getException() instanceof UserNotInMockGroupException || jobStatus
								.getException() instanceof MockNotInstalledException)) {
					return;
				}
				if (result.wasSuccessful()) {
					logger.logDebug(NLS.bind(
							RpmText.AbstractMockJob_mockSucceededMsg,
							result.getResultDirectoryPath().getLocation().toFile().getAbsolutePath()));
					showMessageDialog(NLS.bind(
							RpmText.AbstractMockJob_mockSucceededMsgHTML,
							result.getResultDirectoryPath().getFullPath().toOSString()));
				} else {
					logger.logDebug(NLS.bind(
							RpmText.AbstractMockJob_mockFailedMsg,
							result.getResultDirectoryPath().getLocation().toFile().getAbsolutePath()));
					showMessageDialog(NLS.bind(
							RpmText.AbstractMockJob_mockFailedMsgHTML,
							result.getResultDirectoryPath().getFullPath().toOSString()));
				}
			}
		};
		return listener;
	}
	
	/**
	 * Helper method for showing the custom message dialog with a link to the
	 * build result directory.
	 * 
	 * @param msg
	 */
	private void showMessageDialog(String htmlMsg) {
		final LinkedMessageDialog messageDialog = new LinkedMessageDialog(shell, fpr
				.getProductStrings().getProductName(), htmlMsg,
				result.getResultDirectoryPath());
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				messageDialog.open();
			}
		});
	}
}
