package org.fedoraproject.eclipse.packager.fedorarpm.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
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
import org.eclipse.linuxtools.rpm.core.RPMProject;
import org.eclipse.linuxtools.rpm.core.RPMProjectLayout;
import org.eclipse.linuxtools.rpmstubby.SpecfileWriter;
import org.eclipse.linuxtools.rpmstubby.StubbyPomGenerator;
import org.eclipse.ui.PlatformUI;
import org.fedoraproject.eclipse.packager.fedorarpm.FedoraRPMText;

public class FedoraRPMProjectCreator {
	private static final String GITIGNORE = ".gitignore"; //$NON-NLS-1$
	private static final String PROJECT = ".project"; //$NON-NLS-1$
	private static final String SPEC = ".spec"; //$NON-NLS-1$

	private static final String FEATURE = "feature.xml"; //$NON-NLS-1$
	private static final String POM = "pom.xml"; //$NON-NLS-1$

	private static final String EGIT_REPOSITORIESVIEW = "org.eclipse.egit.ui.RepositoriesView"; //$NON-NLS-1$

	private Repository repository;
	private Git git;

	/**
	 * Creates git repository inside the base project also adds the base
	 * contents and the existing contents to the git repo.
	 *
	 * @param File
	 *            the external xml file uploaded from file system
	 * @param IProject
	 *            the base of the project
	 * @param IProgressMonitor
	 *            Progress monitor to report back status
	 * @param String
	 *            type of the porject
	 * @throws IOException
	 * @throws NoFilepatternException
	 * @throws WrongRepositoryStateException
	 * @throws JGitInternalException
	 * @throws ConcurrentRefUpdateException
	 * @throws NoMessageException
	 * @throws NoHeadException
	 * @throws CoreException
	 */
	public void create(String projectType, File externalFile, IProject project, IProgressMonitor monitor) throws IOException,
			NoFilepatternException, NoHeadException, NoMessageException,
			ConcurrentRefUpdateException, JGitInternalException,
			WrongRepositoryStateException, CoreException {

		if (projectType.equals(FedoraRPMText.FedoraRPMProjectIWizard_Stubby)) {
			IFile stubby = project.getFile(externalFile.getName());
			stubby.create(new FileInputStream(externalFile), false, monitor);

			String fileName = externalFile.getName();

			if (fileName.equals(FEATURE)) {
				SpecfileWriter specfileWriter = new SpecfileWriter();
				specfileWriter.write(stubby);
			}

			if (fileName.equals(POM)) {
				StubbyPomGenerator generator = new StubbyPomGenerator(stubby);
				generator.writeContent(stubby.getProject().getName());
			}
		}

		if (projectType.equals(FedoraRPMText.FedoraRPMProjectIWizard_SRpm)) {
			RPMProject rpmProject = new RPMProject(project,
					RPMProjectLayout.FLAT);
			rpmProject.importSourceRPM(externalFile);

		}

		File directory = createLocalGitRepo(project);

		// add new and existing contents to the git repository
		addContentToGitRepo(directory);

		// Set persistent property so that we know when to show the context
		// menu item.
//		 project.setPersistentProperty(PackagerPlugin.PROJECT_PROP,
//							"true" /* unused value */); //$NON-NLS-1$

		ConnectProviderOperation connect = new ConnectProviderOperation(project);
		connect.execute(null);

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.showView(EGIT_REPOSITORIESVIEW);
	}

	/**
	 * Initialize a local git repository in project location
	 *
	 * @param IProject
	 *            the base of the project
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
	 * Adds existing and new contents to the Git repository and does the first
	 * commit
	 *
	 * @param File
	 *            directory of the git repository
	 * @throws NoFilepatternException
	 * @throws IOException
	 * @throws WrongRepositoryStateException
	 * @throws JGitInternalException
	 * @throws ConcurrentRefUpdateException
	 * @throws NoMessageException
	 * @throws NoHeadException
	 * @throws CoreException
	 */
	private void addContentToGitRepo(File directory) throws IOException,
			NoFilepatternException, NoHeadException, NoMessageException,
			ConcurrentRefUpdateException, JGitInternalException,
			WrongRepositoryStateException, CoreException {

		for (File file : directory.listFiles()) {
			String name = file.getName();

			if (name.contains(SPEC)) {
				git.add().addFilepattern(name).call();
			}

			if (name.equals(GITIGNORE) || name.equals(PROJECT)) {
				git.add().addFilepattern(name).call();
			}
		}

		// do the first commit
		git.commit().setMessage(FedoraRPMText.FedoraRPMProject_api_FirstCommit)
				.call();
	}

}