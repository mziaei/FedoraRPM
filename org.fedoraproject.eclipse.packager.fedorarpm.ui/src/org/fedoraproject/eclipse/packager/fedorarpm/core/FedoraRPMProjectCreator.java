package org.fedoraproject.eclipse.packager.fedorarpm.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.util.FileUtils;

public class FedoraRPMProjectCreator {
	private IWorkspaceRoot root;
	private IProject project;
	private IProjectDescription description;
	private static int testCount;
	private final File trash = new File(new File("target"), "trash");
	private final List<Repository> toClose = new ArrayList<Repository>();
	
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Initialize a local git repository in project location
	 * @throws IOException 
	 */
	private void createLocalGitRepo(IProgressMonitor monitor) throws IOException {	
		File directory = createGitDirectory(project.getName());		
		InitCommand command = new InitCommand();
		command.setDirectory(directory);
		command.setBare(false);
		Repository repository = command.call().getRepository();
		addRepoToClose(repository);
				
//		String[] gitStrcuture = {".git", ".git/objects", ".git/refs", 
//				".git/objects/info", ".git/objects/pack", ".git/refs/heads", ".git/refs/tags"};
//		createFolders(gitStrcuture, monitor);
//		createFile("HEAD", ".git/", "ref: refs/heads/master\n", monitor);
//		createFile(".gitignore", "", "temp\n", monitor);
	}
	
	/**
	 * Creates a unique directory for a test
	 *
	 * @param name
	 *            a subdirectory
	 * @return a unique directory for a test
	 * @throws IOException
	 */
	protected File createGitDirectory(String name) throws IOException {
		
		String gitdirName = root.getLocation().toString() + "/" + name;
		
		File directory = new File(gitdirName);
		FileUtils.mkdirs(directory);
		return directory.getCanonicalFile();
	}
	
	public void addRepoToClose(Repository r) {
		toClose.add(r);
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
