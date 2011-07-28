package org.fedoraproject.eclipse.packager.fedorarpm.core;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.linuxtools.rpm.core.RPMProject;
import org.eclipse.linuxtools.rpm.core.RPMProjectLayout;

public class SRPMFedoraProjectCreator {
	
	/**
	 * Creates a project with the given name in the given location.
	 * 
	 * @param file the external srpm file from file system
	 * @param project the base of the project
	 * @param monitor Progress monitor to report back status
	 */
	public void create(File srpmFile, IProject project, IProgressMonitor monitor) {
		
		try {			
			RPMProject rpmProject = new RPMProject(project, RPMProjectLayout.FLAT);
			rpmProject.importSourceRPM(srpmFile);				
			
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}

