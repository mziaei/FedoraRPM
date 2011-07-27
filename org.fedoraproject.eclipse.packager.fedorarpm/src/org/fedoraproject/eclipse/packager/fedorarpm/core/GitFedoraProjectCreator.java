package org.fedoraproject.eclipse.packager.fedorarpm.core;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.util.FileUtils;

public class GitFedoraProjectCreator {
	
	private static final String GITIGNORE = ".gitignore";
	private static final String SOURCES = "sources";
	private static final String PROJECT = ".project";
	
	private Repository repository;
	
	/**
	 * Creates a project with the given name in the given location.
	 * 
	 * @param project the base of the project
	 * @param monitor Progress monitor to report back status
	 * @throws NoFilepatternException 
	 * @throws IOException 
	 * @throws WrongRepositoryStateException 
	 * @throws JGitInternalException 
	 * @throws ConcurrentRefUpdateException 
	 * @throws NoMessageException 
	 * @throws NoHeadException 
	 */
	public void create(IProject project, IProgressMonitor monitor) throws IOException, NoFilepatternException, 
			NoHeadException, NoMessageException, ConcurrentRefUpdateException, 
			JGitInternalException, WrongRepositoryStateException {

		try {
			File directory = new File(project.getLocation().toString() + "/.git");
			FileUtils.mkdirs(directory, true);
			directory.getCanonicalFile();
			
			repository = new FileRepository(directory);
			repository.create();
			
			addContentToGitRepo();


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addContentToGitRepo()throws IOException, NoFilepatternException, 
			NoHeadException, NoMessageException, ConcurrentRefUpdateException, 
			JGitInternalException, WrongRepositoryStateException {
		createFile(GITIGNORE);
		createFile(SOURCES);

		Git git = new Git(repository);
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
		File file = new File(repository.getWorkTree(), fileName);
		FileUtils.createNewFile(file);
	}

}
