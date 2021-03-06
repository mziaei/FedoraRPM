/*******************************************************************************
 * Copyright (c) 2010-2011 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat Inc. - initial API and implementation
 *******************************************************************************/
package org.fedoraproject.eclipse.packager.local.api;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
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
import org.fedoraproject.eclipse.packager.local.LocalFedoraPackagerPlugin;
import org.fedoraproject.eclipse.packager.local.LocalFedoraPackagerText;
import org.fedoraproject.eclipse.packager.local.internal.ui.LocalFedoraPackagerPageFour;

public class LocalFedoraPackagerProjectCreator {
	private static final String GITIGNORE = ".gitignore"; //$NON-NLS-1$
	private static final String PROJECT = ".project"; //$NON-NLS-1$
	private static final String SPEC = ".spec"; //$NON-NLS-1$

	private static final String FEATURE = "feature.xml"; //$NON-NLS-1$
	private static final String POM = "pom.xml"; //$NON-NLS-1$

	private Repository repository;
	private Git git;

	/**
	 * Creates git repository inside the base project also adds the
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
	public void create(String projectType, File externalFile, LocalFedoraPackagerPageFour pageFour,
			IProject project, IProgressMonitor monitor) throws IOException,
			NoFilepatternException, NoHeadException, NoMessageException,
			ConcurrentRefUpdateException, JGitInternalException,
			WrongRepositoryStateException, CoreException {

		if (projectType.equals(LocalFedoraPackagerText.LocalFedoraPackagerPageThree_Stubby)) {
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

		} else if (projectType.equals(LocalFedoraPackagerText.LocalFedoraPackagerPageThree_SRpm)) {
			RPMProject rpmProject = new RPMProject(project,
					RPMProjectLayout.FLAT);
			rpmProject.importSourceRPM(externalFile);
		} else {
			final String projectName = project.getName();
			final String fileName = projectName + ".spec";
			final InputStream contentInputStream = new ByteArrayInputStream(pageFour.getContent().getBytes());
			final IFile file = project.getFile(new Path(fileName));
			try {
				InputStream stream = contentInputStream;
				if (file.exists()) {
					file.setContents(stream, true, true, monitor);
				} else {
					file.create(stream, true, monitor);
				}
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		File directory = createLocalGitRepo(project);

		// add contents to the git repository
		addContentToGitRepo(directory);

		// Set persistent property so that we know when to show the context
		// menu item.
		 project.setPersistentProperty (LocalFedoraPackagerPlugin.PROJECT_PROP,
							"true" /* unused value */); //$NON-NLS-1$

		ConnectProviderOperation connect = new ConnectProviderOperation(project);
		connect.execute(null);
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
		git.commit().setMessage(LocalFedoraPackagerText.LocalFedoraPackagerProjectCreator_FirstCommit)
				.call();
	}

}