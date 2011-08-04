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
package org.fedoraproject.eclipse.packager.fedorarpm.utils;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.fedoraproject.eclipse.packager.FedoraPackagerLogger;
import org.fedoraproject.eclipse.packager.IProjectRoot;
import org.fedoraproject.eclipse.packager.api.errors.FedoraPackagerExtensionPointException;
import org.fedoraproject.eclipse.packager.api.errors.InvalidProjectRootException;
import org.fedoraproject.eclipse.packager.fedorarpm.FedoraRPMProjectRoot;
import org.fedoraproject.eclipse.packager.fedorarpm.LocalFedoraPackagerText;
import org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils;

/**
 * Utility class for Local Fedora Packager.
 */
public class FedoraRPMPackagerUtils extends FedoraPackagerUtils {	

	/* 
	 * @see org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils
	 *   #isValidFedoraProjectRoot(IContainer resource)
	 */
	protected static boolean isValidFedoraProjectRoot(IContainer resource) {
		// FIXME: Determine rpm package name from a persistent property. In
		// future the project name might not be equal to the RPM package name.
		IFile specFile = resource.getFile(new Path(resource.getProject()
				.getName() + ".spec")); //$NON-NLS-1$
		if (specFile.exists()) {
			return true;
		}
		return false;
	}
	
	/* 
	 * @see org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils
	 *   #getProjectRoot(IResource resource)
	 */
	public static IProjectRoot getProjectRoot(IResource resource)
			throws InvalidProjectRootException {
		IContainer candidate = null;
		if (resource instanceof IFolder || resource instanceof IProject) {
			candidate = (IContainer) resource;
		} else if (resource instanceof IFile) {
			candidate = resource.getParent();
		}
		ProjectType type = getProjectType(candidate);
		if (candidate != null && isValidFedoraProjectRoot(candidate)
				&& type != null) {
			try {
				return instantiateProjectRoot(candidate, type);
			} catch (FedoraPackagerExtensionPointException e) {
				FedoraPackagerLogger logger = FedoraPackagerLogger.getInstance();
				logger.logError(e.getMessage(), e);
				throw new InvalidProjectRootException(e.getMessage());
			}
		} else {
			throw new InvalidProjectRootException(LocalFedoraPackagerText.LocalFedoraPackager_Utils_invalidProjectRootError);
		}
	}

	/* 
	 * @see org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils
	 *   #instantiateProjectRoot(IContainer container, ProjectType type)
	 */
	private static IProjectRoot instantiateProjectRoot(IContainer container, ProjectType type)
			throws FedoraPackagerExtensionPointException {

			IProjectRoot projectRoot = new FedoraRPMProjectRoot();
			// Do initialization
			projectRoot.initialize(container, type);
			return projectRoot;
	}
	
}
