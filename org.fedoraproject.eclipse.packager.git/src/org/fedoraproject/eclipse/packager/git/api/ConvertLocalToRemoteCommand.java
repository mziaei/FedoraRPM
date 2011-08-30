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
package org.fedoraproject.eclipse.packager.git.api;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.fedoraproject.eclipse.packager.IFpProjectBits;
import org.fedoraproject.eclipse.packager.IProjectRoot;
import org.fedoraproject.eclipse.packager.PackagerPlugin;
import org.fedoraproject.eclipse.packager.api.FedoraPackagerCommand;
import org.fedoraproject.eclipse.packager.api.errors.CommandListenerException;
import org.fedoraproject.eclipse.packager.api.errors.CommandMisconfiguredException;
import org.fedoraproject.eclipse.packager.api.errors.FedoraPackagerCommandInitializationException;
import org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils;

/**
 * A class used to execute a {@code convert local to remote} command. It has
 * setters for all supported options and arguments of this command and a
 * {@link #call(IProgressMonitor)} method to finally execute the command. Each
 * instance of this class should only be used for one invocation of the command
 * (means: one call to {@link #call(IProgressMonitor)})
 * 
 */
public class ConvertLocalToRemoteCommand extends
		FedoraPackagerCommand<ConvertLocalResult> {

	private IProjectRoot localFedoraProjectRoot;
	private IProject localFedoraProject;

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
		this.localFedoraProject = localFedoraProjectRoot.getProject();
	}

	/**
	 * Implementation of the {@code ConvertLocalToRemoteCommand}.
	 * 
	 * @param monitor
	 * @throws CommandMisconfiguredException
	 *             If the command was not properly configured when it was
	 *             called.
	 * @throws CommandListenerException
	 *             If some listener detected a problem.
	 * @return The result of this command.
	 */
	@Override
	public ConvertLocalResult call(IProgressMonitor monitor)
			throws CommandMisconfiguredException, CommandListenerException {
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

		projectBits.addRemoteRepository();

		// set the project property to main fedora packager's
		// property
		try {
			localFedoraProject.setPersistentProperty(
					PackagerPlugin.PROJECT_PROP, "true"); //$NON-NLS-1$
			localFedoraProject.setPersistentProperty(
					PackagerPlugin.PROJECT_LOCAL_PROP, null);
			// TODO
			// String message = NLS
			// .bind(FedoraPackagerText.ConvertToGitHandler_ListHeader,
			// localFedoraProject.getName());
			localFedoraProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
