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
import org.eclipse.egit.core.op.ConnectProviderOperation;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.util.FileUtils;
import org.eclipse.ui.PlatformUI;

public class FedoraRPMProjectCreator {
	private IWorkspaceRoot root;
	private IProject project;
	private IProjectDescription description;
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
			project.create(description, monitor);
			monitor.worked(2);   //? TODO - Do we need this
			project.open(monitor);  //?TODO - Do we need this
			
			createLocalGitRepo(monitor);
			project.getFolder("temp").create(true, true, monitor);			
			createFile("sources", null, monitor);
			createFile(".gitignore", "/temp", monitor);
			
			ConnectProviderOperation connect = new ConnectProviderOperation(
					project);
			connect.execute(null);

			// Finally show the Git Repositories view for convenience
			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().showView(
							"org.eclipse.egit.ui.RepositoriesView"); //$NON-NLS-1$

		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize a local git repository in project location
	 *
	 * @param IProgressMonitor
	 * @throws IOException
	 */
	private void createLocalGitRepo(IProgressMonitor monitor) throws IOException {	
		String gitdirName = project.getLocation().toString();
		File directory = new File(gitdirName);
		FileUtils.mkdirs(directory, true);
		directory.getCanonicalFile();
		
		InitCommand command = new InitCommand();
		command.setDirectory(directory);
		command.setBare(false);
		Repository repository = command.call().getRepository();
		addRepoToClose(repository);
	}
	
	public void addRepoToClose(Repository r) {
		toClose.add(r);
	}
	
	/**
	 * Creates ifiles
	 */
	private void createFile(String fileName, String content, IProgressMonitor monitor) {
		IFile file = project.getFile(fileName);
		try {
			file.create(addContentStream(content), false, monitor);
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
