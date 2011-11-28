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

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;

/**
 * Result of a call to {@link MockBuildCommand}.
 */
public class MockBuildResult extends Result {

	private String resultDir;
	
	private boolean success;
	
	/**
	 * FIXME: Make the resultDir an IResource.
	 * 
	 * @param cmdList
	 * @param resultDir The directory specified to put mock build results into.
	 */
	public MockBuildResult(String[] cmdList, String resultDir) {
		super(cmdList);
		File absPath = new File(resultDir);
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
	public String getResultDirectoryPath() {
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
