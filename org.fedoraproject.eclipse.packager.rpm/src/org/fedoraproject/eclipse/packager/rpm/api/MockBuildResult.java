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
package org.fedoraproject.eclipse.packager.rpm.api;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Result of a call to {@link MockBuildCommand}.
 */
public class MockBuildResult extends Result {

	private IContainer resultDir;
	
	private boolean success;
	
	/**
	 * The MockBuildResult(String[], IResource) constructor should be used instead.
	 * 
	 * @param cmdList The command list used for the mock call.
	 * @param resultDir The directory where mock build result logs reside.
	 */
	public MockBuildResult(String[] cmdList, String resultDir) {
		super(cmdList);
		IWorkspace workspace= ResourcesPlugin.getWorkspace();    
		IPath resultLocation= Path.fromOSString(resultDir); 
		IContainer dir = workspace.getRoot().getContainerForLocation(resultLocation);
		this.resultDir = dir;
		// will be set to false by an observer if there was an error
		this.success = true;
	}
	
	/**
	 * @param cmdList
	 *            The command list used for the mock call.
	 * @param resultDir
	 *            The directory where mock build result logs reside.
	 */
	public MockBuildResult(String[] cmdList, IContainer resultDir) {
		super(cmdList);
		this.resultDir = resultDir;
		// will be set to false by an observer if there was an error
		this.success = true;
	}
	
	/**
	 * 
	 */
	public void setFailure() {
		this.success = false;
	}
	
	/**
	 * 
	 */
	public void setSuccess() {
		this.success = true;
	}
	
	/**
	 *
	 * @return The relative path to the directory containing mock build results.
	 */
	public IContainer getResultDirectoryPath() {
		return this.resultDir;
	}
	
	/* (non-Javadoc)
	 * @see org.fedoraproject.eclipse.packager.api.ICommandResult#wasSuccessful()
	 */
	@Override
	public boolean wasSuccessful() {
		return success;
	}

}
