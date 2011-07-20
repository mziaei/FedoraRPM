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
	private IWorkspaceRoot root;
	private IProject project;
	private IProjectDescription description;

	/**
	 * Creates a project with the given name in the given location.
	 * @param projectName The name of the project.
	 * @param projectPath The parent location of the project.
	 * @param monitor Progress monitor to report back status.
	 */
	public void create(String projectName, IPath projectPath,
			IProgressMonitor monitor) {
		try {
			root = ResourcesPlugin.getWorkspace().getRoot();
			project = root.getProject(projectName);
			description = ResourcesPlugin.getWorkspace()
					.newProjectDescription(project.getName());
			if (!Platform.getLocation().equals(projectPath))
				description.setLocation(projectPath);
//			description
//					.setNatureIds(new String[] { RPMProjectNature.RPM_NATURE_ID });
			project.create(description, monitor);
			monitor.worked(2);   //? TODO
			project.open(monitor);  //?TODO
			
			createLocalGitRepo(monitor);
			project.getFolder("temp").create(true, true, monitor);
			createFile("sources", "", null, monitor);

		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize a local git repository in project location
	 */
	private void createLocalGitRepo(IProgressMonitor monitor) {	
		String[] gitStrcuture = {".git", ".git/objects", ".git/refs", 
				".git/objects/info", ".git/objects/pack", ".git/refs/heads", ".git/refs/tags"};
		createFolders(gitStrcuture, monitor);
		createFile("HEAD", ".git/", "ref: refs/heads/master\n", monitor);
		createFile(".gitignore", "", "temp\n", monitor);
	}
	
	/**
	 * Creates ifiles
	 */
	private void createFile(String fileName, String path, String content, IProgressMonitor monitor) {
		IFile file = project.getFile(path + fileName);
		try {
			file.create(addContentStream(content), false, monitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * Creates ifolders
	 */
	private void createFolders(String[] foldersName, IProgressMonitor monitor) {
		try {
			for (int i = 0; i < foldersName.length; i++) {
				project.getFolder(foldersName[i]).create(true, true, monitor);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}		
	}


	/**
	 * Initialize file contents
	 */
	private InputStream addContentStream(String content) {
		if (content == null) {
			return new ByteArrayInputStream(new byte[0]);
		}
		return new ByteArrayInputStream(content.getBytes());
	}

}
