package org.fedoraproject.eclipse.packager.fedorarpm.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

public class FedoraRPMProjectCreator {
	private InputStream source;

	/**
	 * Creates a project with the given name in the given location.
	 * @param projectName The name of the project.
	 * @param projectPath The parent location of the project.
	 * @param monitor Progress monitor to report back status.
	 */
	public void create(String projectName, IPath projectPath,
			IProgressMonitor monitor) {
		try {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IProject project = root.getProject(projectName);
			IProjectDescription description = ResourcesPlugin.getWorkspace()
					.newProjectDescription(project.getName());
			if (!Platform.getLocation().equals(projectPath))
				description.setLocation(projectPath);
//			description
//					.setNatureIds(new String[] { RPMProjectNature.RPM_NATURE_ID });
			project.create(description, monitor);
			monitor.worked(2);
			project.open(monitor);

			project.getFolder("temp").create(true, true, monitor);

			IFile sourcesFile = project.getFile("/sources");
			String contents = "";
			InputStream source = new ByteArrayInputStream(contents.getBytes());
			sourcesFile.create(source, false, monitor);

			IFile gitignoreFile = project.getFile("/.gitignore");
			contents = "temp\n";
			source = new ByteArrayInputStream(contents.getBytes());
			gitignoreFile.create(source, false, monitor);

		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

}
