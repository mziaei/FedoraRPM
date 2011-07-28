package org.fedoraproject.eclipse.packager.fedorarpm.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
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

public class GitFedoraProjectCreator {
	
	private static final String GITIGNORE = ".gitignore";
	private static final String SOURCES = "sources";
	private static final String PROJECT = ".project";
	
	private Repository repository;
	private Git git;
	
	/**
	 * Creates a project with the given name in the given location.
	 * 
	 * @param IProject the base of the project
	 * @param monitor Progress monitor to report back status
	 * @throws NoFilepatternException 
	 * @throws WrongRepositoryStateException 
	 * @throws JGitInternalException 
	 * @throws ConcurrentRefUpdateException 
	 * @throws NoMessageException 
	 * @throws NoHeadException 
	 */
	public void create(IProject project, IWorkspaceRoot root, IProgressMonitor monitor) throws 
			IOException, NoFilepatternException, NoHeadException, NoMessageException, 
			ConcurrentRefUpdateException, JGitInternalException, WrongRepositoryStateException {

		try {
			File directory = createLocalGitRepo(project);
			createFile(GITIGNORE);
			createFile(SOURCES);
			add
//			addNewFiles(project, root);
//			addExistingContent(directory);
			createContentInGitRepo(project, monitor, directory);
			
			ConnectProviderOperation connect = new ConnectProviderOperation(project);
			connect.execute(null);

			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().showView(
							"org.eclipse.egit.ui.RepositoriesView"); //$NON-NLS-1$


		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private void addExistingContent(File directory) throws NoFilepatternException {
		File[] files = directory.listFiles();
		for (File file : files) {
			String name = file.getName();
			if (name.contains(".spec") || (name.contains(".sh") || (name.contains(".patch")))) {
				git.add().addFilepattern(file.getName()).call();
			}
		}		
	}

	/**
	 * Initialize a local git repository in project location
	 *
	 * @param IProject the base of the project
	 * @throws IOException
	 * @return File directory of the git repository
	 */
	private File createLocalGitRepo(IProject project) throws IOException {	
		File directory = new File(project.getLocation().toString());
		FileUtils.mkdirs(directory, true);
		directory.getCanonicalFile();
		
		InitCommand command = new InitCommand();
		command.setDirectory(directory);
		command.setBare(false);
		repository = command.call().getRepository();
		
		git = new Git(repository);
		return directory;
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
	 * @throws CoreException 
	 */
	private void createContentInGitRepo(IProject project, IProgressMonitor monitor, File directory)throws 
			IOException, NoFilepatternException, NoHeadException, NoMessageException, 
			ConcurrentRefUpdateException, JGitInternalException, WrongRepositoryStateException, CoreException {
		
		File[] files = directory.listFiles();
		for (File file : files) {
			String name = file.getName();

			if (name.contains(".spec") || (name.contains(".sh") 
					|| (name.contains(".patch")))) {
				git.add().addFilepattern(name).call();
			}
			if (name.contains(".tar")) {
				InputStream content = new ByteArrayInputStream(name.getBytes());
				project.getFile(SOURCES).appendContents(content, false, false, monitor);
			}
			if (name.equalsIgnoreCase(GITIGNORE) || name.equalsIgnoreCase(SOURCES) 
					|| name.equalsIgnoreCase(PROJECT)) {
				git.add().addFilepattern(name).call();
			}
			
		}	
		
		
		
//		git.add().addFilepattern(GITIGNORE).call();
//		git.add().addFilepattern(SOURCES).call();
		
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
		File file = new File(repository.getWorkTree(), fileName);
		FileUtils.createNewFile(file);
	}

}
