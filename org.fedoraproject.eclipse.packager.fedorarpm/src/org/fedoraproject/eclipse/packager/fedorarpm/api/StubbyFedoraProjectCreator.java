package org.fedoraproject.eclipse.packager.fedorarpm.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.linuxtools.rpmstubby.*;
import org.fedoraproject.eclipse.packager.fedorarpm.wizards.FedoraRPMMessages;

public class StubbyFedoraProjectCreator {
	/**
	 * Crates base of the project using rpm stubby.
	 * 
	 * @param file the external xml file uploaded from file system
	 * @param project the base of the project
	 * @param monitor Progress monitor to report back status
	 * @throws CoreException 
	 * @throws FileNotFoundException 
	 */
	public void create(File externalFile, IProject project, IProgressMonitor monitor) 
			throws FileNotFoundException, CoreException {
		
		IFile stubby = project.getFile(externalFile.getName());
		stubby.create(new FileInputStream(externalFile), false, monitor);
		
		String fileName = externalFile.getName();
		
		if (fileName.equals(FedoraRPMMessages.FedoraRPMProject_api_FeatureFile)) {
			SpecfileWriter specfileWriter = new SpecfileWriter();
			specfileWriter.write(stubby);
		}
		
		if (fileName.equals(FedoraRPMMessages.FedoraRPMProject_api_PomFile)) {
			StubbyPomGenerator generator = new StubbyPomGenerator(stubby);
			try {
				generator.writeContent(stubby.getProject().getName());
			} catch (CoreException e) {
				StubbyLog.logError(e);
			}
		}
	}
}
