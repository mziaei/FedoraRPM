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
package org.fedoraproject.eclipse.packager.bodhi.internal.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.fedoraproject.eclipse.packager.IProjectRoot;
import org.fedoraproject.eclipse.packager.api.UnpushedChangesListener;
import org.fedoraproject.eclipse.packager.api.errors.CommandListenerException;
import org.fedoraproject.eclipse.packager.api.errors.UnpushedChangesException;

/**
 * Job checking for unpushed changes.
 *
 */
public class UnpushedChangesJob implements IRunnableWithProgress {

	// Check for unpushed changes
	private boolean unpushedChanges = false;
	private IProjectRoot fedoraProjectRoot;
	private String jobName;
	
	/**
	 * @param name
	 * @param fedoraProjectRoot
	 */
	public UnpushedChangesJob(String name, IProjectRoot fedoraProjectRoot) {
		this.jobName = name;
		this.fedoraProjectRoot = fedoraProjectRoot;
	}
				
	@Override
	public void run(IProgressMonitor monitor) {
		UnpushedChangesListener unpushedChangesListener = new UnpushedChangesListener(
				fedoraProjectRoot, monitor);
		monitor.beginTask(this.jobName, 30);
		try {
			unpushedChangesListener.preExecution();
		} catch (CommandListenerException e) {
			if (e.getCause() instanceof UnpushedChangesException) {
				this.unpushedChanges = true;
			}
		}
		monitor.done();
	}
	
	/**
	 * @return {@code true} if there were unpushed changes, {@code false}
	 *         otherwise.
	 */
	public boolean isUnpushedChanges() {
		return this.unpushedChanges;
	}
				
}
