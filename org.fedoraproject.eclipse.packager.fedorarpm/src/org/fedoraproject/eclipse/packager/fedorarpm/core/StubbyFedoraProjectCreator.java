package org.fedoraproject.eclipse.packager.fedorarpm.core;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.linuxtools.rpmstubby.*;

public class StubbyFedoraProjectCreator {
	/**
	 * Crates base of the project using rpm stubby.
	 * 
	 * @param file the external xml file from file system
	 * @param project the base of the project
	 * @param monitor Progress monitor to report back status
	 */
	public void create(File externalFile, IProject project, IProgressMonitor monitor) {
		IFile stubby = project.getFile(externalFile.getName());
		SpecfileWriter specfileWriter = new SpecfileWriter();
		specfileWriter.write(stubby);
	}
}
