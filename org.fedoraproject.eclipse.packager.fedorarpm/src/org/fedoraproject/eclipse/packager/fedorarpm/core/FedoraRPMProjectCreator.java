package org.fedoraproject.eclipse.packager.fedorarpm.core;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.core.op.ConnectProviderOperation;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.util.FileUtils;
import org.eclipse.ui.PlatformUI;

public class FedoraRPMProjectCreator {
	private static final String GITIGNORE = ".gitignore";
	private static final String SOURCES = "sources";
	private static final String PROJECT = ".project";
	
	private IProject gitProject;
	private Repository gitRepo;
	private String gitDirectoryPath;
	
	/**
	 * Creates a project with the given name in the given location.
	 * @param projectName The name of the project.
	 * @param projectPath The parent location of the project.
	 * @param monitor Progress monitor to report back status.
	 * @throws WrongRepositoryStateException 
	 * @throws JGitInternalException 
	 * @throws ConcurrentRefUpdateException 
	 * @throws NoMessageException 
	 * @throws NoHeadException 
	 */
	public void create(IProject project,
			IProgressMonitor monitor) throws NoHeadException, NoMessageException, 
			ConcurrentRefUpdateException, JGitInternalException, WrongRepositoryStateException {
		try {
			gitProject = project;
//			root = ResourcesPlugin.getWorkspace().getRoot();
//			project = root.getProject(projectName);
//			description = ResourcesPlugin.getWorkspace()
//					.newProjectDescription(project.getName());
//			if (!Platform.getLocation().equals(projectPath))
//				description.setLocation(projectPath);
//			project.create(description, monitor);
//			monitor.worked(2);   //? TODO - Do we need this
//			project.open(monitor);  //?TODO - Do we need this
			
			createLocalGitRepo();			
			addContentToGitRepo();
			
			// Set persistent property so that we know when to show the context
			// menu item.
//			project.setPersistentProperty(PackagerPlugin.PROJECT_PROP,
//					"true" /* unused value */); //$NON-NLS-1$

			ConnectProviderOperation connect = new ConnectProviderOperation(project);
			connect.execute(null);

			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().showView(
							"org.eclipse.egit.ui.RepositoriesView"); //$NON-NLS-1$

		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoFilepatternException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initialize a local git repository in project location
	 *
	 * @throws IOException
	 */
	private void createLocalGitRepo() throws IOException {	
		gitDirectoryPath = gitProject.getLocation().toString();
		File directory = new File(gitDirectoryPath);
		FileUtils.mkdirs(directory, true);
		directory.getCanonicalFile();
		
		InitCommand command = new InitCommand();
		command.setDirectory(directory);
		command.setBare(false);
		gitRepo = command.call().getRepository();
	}
	
	
	/**
	 * Adds new contents to the Git repository and 
	 *      does the first commit
	 *      
	 * @throws NoFilepatternException 
	 * @throws IOException 
	 * @throws WrongRepositoryStateException 
	 * @throws JGitInternalException 
	 * @throws ConcurrentRefUpdateException 
	 * @throws NoMessageException 
	 * @throws NoHeadException 
	 */
	private void addContentToGitRepo() throws IOException, NoFilepatternException, 
			NoHeadException, NoMessageException, ConcurrentRefUpdateException, 
			JGitInternalException, WrongRepositoryStateException {
		createFile(GITIGNORE);
		createFile(SOURCES);

		Git git = new Git(gitRepo);
		git.add().addFilepattern(GITIGNORE).call();
		git.add().addFilepattern(SOURCES).call();
		git.add().addFilepattern(PROJECT).call();
		git.commit().setMessage("first init").call();
	}

	/**
	 * Creates predefined files in the project location
	 *    
	 * @param fileName name of the file
	 * @param content contents of the file
	 * @throws IOException 	 
	 */
	private void createFile(String fileName) throws IOException {
		File file = new File(gitRepo.getWorkTree(), fileName);
		FileUtils.createNewFile(file);
	}	

}
