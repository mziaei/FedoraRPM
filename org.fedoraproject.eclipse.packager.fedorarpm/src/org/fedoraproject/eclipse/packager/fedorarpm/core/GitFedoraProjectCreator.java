package org.fedoraproject.eclipse.packager.fedorarpm.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
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
	public void create(IProject project, IWorkspaceRoot root, IProgressMonitor monitor, String type) throws 
			IOException, NoFilepatternException, NoHeadException, NoMessageException, 
			ConcurrentRefUpdateException, JGitInternalException, WrongRepositoryStateException {

		try {
			File directory = createLocalGitRepo(project);
			
			// add the name of the source to the sources file
			// if it's an srpm generated project
			if (type.equals("srpm")) {
				addSources(directory, project, monitor);
			}
			else {
				IFile sources = project.getFile(SOURCES);
				sources.create(null, false, monitor);
			}

			// add new and existing contents to the git repository
			addContentToGitRepo(project, monitor, directory);
			
			ConnectProviderOperation connect = new ConnectProviderOperation(project);
			connect.execute(null);

			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().showView(
							"org.eclipse.egit.ui.RepositoriesView"); //$NON-NLS-1$


		} catch (CoreException e) {
			e.printStackTrace();
		}
	}


	private void addSources(File directory, IProject project, IProgressMonitor monitor) 
			throws CoreException {
		IFile sources = project.getFile(SOURCES);
		
		File[] files = directory.listFiles();
		for (File file : files) {
			String name = file.getName();
			if (name.contains(".tar")) {
				InputStream content = new ByteArrayInputStream(name.getBytes());
				sources.create(content, false, monitor);
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
	 * Adds existing and new contents to the Git repository and 
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
	private void addContentToGitRepo(IProject project, IProgressMonitor monitor, File directory)throws 
			IOException, NoFilepatternException, NoHeadException, NoMessageException, 
			ConcurrentRefUpdateException, JGitInternalException, WrongRepositoryStateException, CoreException {
				
		File[] files = directory.listFiles();
		for (File file : files) {
			String name = file.getName();

			if (name.contains(".spec") || (name.contains(".sh") 
					|| (name.contains(".patch")))) {
				git.add().addFilepattern(name).call();
			}

			if (name.equalsIgnoreCase(GITIGNORE) || name.equalsIgnoreCase(SOURCES) 
					|| name.equalsIgnoreCase(PROJECT)) {
				git.add().addFilepattern(name).call();
			}			
		}	

		// do the first commit
		git.commit().setMessage("first init").call();		
	}

}
