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
package org.fedoraproject.eclipse.packager.local.internal.ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.fedoraproject.eclipse.packager.local.api.LocalFedoraPackagerProjectCreator;

public class LocalFedoraPackagerProjectWizard extends Wizard implements INewWizard {


	private static final String PAGE_ONE = "PageOne"; //$NON-NLS-1$
	private static final String PAGE_TWO = "PageTwo"; //$NON-NLS-1$
	private static final String PAGE_THREE = "PageThree"; //$NON-NLS-1$
	private static final String PAGE_FOUR = "PageFour"; //$NON-NLS-1$

	private LocalFedoraPackagerProjectPageOne pageOne;
	private LocalFedoraPackagerProjectPageTwo pageTwo;
	private LocalFedoraPackagerProjectPageThree pageThree;
	private LocalFedoraPackagerProjectPageFour pageFour;

	private IWorkspaceRoot root;
	private IProject project;
	private IProjectDescription description;
	private ISelection selection;

	public LocalFedoraPackagerProjectWizard() {
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setNeedsProgressMonitor(true);
		this.selection = selection;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		pageOne = new LocalFedoraPackagerProjectPageOne(PAGE_ONE);
		addPage(pageOne);
//		pageTwo = new LocalFedoraPackagerProjectPageTwo(PAGE_TWO);
//		addPage(pageTwo);
		pageThree = new LocalFedoraPackagerProjectPageThree(PAGE_THREE);
		addPage(pageThree);
//		pageFour = new LocalFedoraPackagerProjectPageFour(PAGE_FOUR);
//		addPage(pageFour);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		try {
			WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
				@Override
				protected void execute(IProgressMonitor monitor) {
					try {
						createBaseProject(monitor != null ? monitor
								: new NullProgressMonitor());
						createMainProject(monitor != null ? monitor
								: new NullProgressMonitor());
					} catch (NoHeadException e) {
						e.printStackTrace();
					} catch (NoMessageException e) {
						e.printStackTrace();
					} catch (ConcurrentRefUpdateException e) {
						e.printStackTrace();
					} catch (JGitInternalException e) {
						e.printStackTrace();
					} catch (WrongRepositoryStateException e) {
						e.printStackTrace();
					} catch (NoFilepatternException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			};
			getContainer().run(false, true, op);
		} catch (InvocationTargetException x) {
			return false;
		} catch (InterruptedException x) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.wizard.wizard#canFinish()
	 */
//	@Override
//	public boolean canFinish() {
//		return (getContainer().getCurrentPage() == pageThree && pageThree.pageCanFinish())
//				|| getContainer().getCurrentPage() == pageFour;
//	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page instanceof LocalFedoraPackagerProjectPageThree) {
			LocalFedoraPackagerProjectPageThree page_three = (LocalFedoraPackagerProjectPageThree) page;
			if (page_three.pageCanFinish()) {
				pageFour = new LocalFedoraPackagerProjectPageFour(PAGE_FOUR);
				addPage(pageFour);
				return null;
			}
		}

		return super.getNextPage(page);
	}

	/**
	 * Creates the base of the project.
	 *
	 * @param IProgressMonitor
	 *            Progress monitor to report back status
	 */
	protected void createBaseProject(IProgressMonitor monitor) {
		root = ResourcesPlugin.getWorkspace().getRoot();
		project = root.getProject(pageOne.getProjectName());
		description = ResourcesPlugin.getWorkspace().newProjectDescription(
				pageOne.getProjectName());
		if (!Platform.getLocation().equals(pageOne.getLocationPath())) {
			description.setLocation(pageOne.getLocationPath());
		}
		try {
			project.create(description, monitor);
			project.open(monitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new instance of the FedoraRPM project.
	 *
	 * @param IProgressMonitor
	 *            Progress monitor to report back status
	 * @throws WrongRepositoryStateException
	 * @throws JGitInternalException
	 * @throws ConcurrentRefUpdateException
	 * @throws NoMessageException
	 * @throws NoHeadException
	 * @throws IOException
	 * @throws NoFilepatternException
	 * @throws CoreException
	 */
	protected void createMainProject(IProgressMonitor monitor)
			throws NoHeadException, NoMessageException,	ConcurrentRefUpdateException,
			JGitInternalException, WrongRepositoryStateException, NoFilepatternException,
			IOException, CoreException {

		if (pageThree.getProjectType().equals("plain")) {
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

		LocalFedoraPackagerProjectCreator fedoraRPMProjectCreator = new LocalFedoraPackagerProjectCreator();
		fedoraRPMProjectCreator.create(pageThree.getProjectType(), pageThree.getExternalFile(), project, monitor);
	}

}
