package org.fedoraproject.eclipse.packager.fedorarpm.internal.ui;

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
import org.fedoraproject.eclipse.packager.fedorarpm.api.FedoraRPMProjectCreator;

public class FedoraRPMProjectWizard extends Wizard implements INewWizard {

	private static final String PAGE_ONE = "PageOne"; //$NON-NLS-1$
	private static final String PAGE_TWO = "PageTwo"; //$NON-NLS-1$
	private static final String PAGE_THREE = "PageThree"; //$NON-NLS-1$
	private static final String PAGE_FOUR = "PageFour"; //$NON-NLS-1$

	private FedoraRPMProjectPageOne pageOne;
	private FedoraRPMProjectPageTwo pageTwo;
	private FedoraRPMProjectPageThree pageThree;
	private FedoraRPMProjectPageFour pageFour;

	private IWorkspaceRoot root;
	private IProject project;
	private IProjectDescription description;
	private ISelection selection;

	public FedoraRPMProjectWizard() {
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
		pageOne = new FedoraRPMProjectPageOne(PAGE_ONE);
		addPage(pageOne);
		pageTwo = new FedoraRPMProjectPageTwo(PAGE_TWO);
		addPage(pageTwo);
		pageThree = new FedoraRPMProjectPageThree(PAGE_THREE);
		addPage(pageThree);
		pageFour = new FedoraRPMProjectPageFour(PAGE_FOUR, selection);		
		addPage(pageFour);		
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
	@Override
	public boolean canFinish() {
		return pageThree.isPageComplete() || pageFour.isPageComplete();
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
		
		FedoraRPMProjectCreator fedoraRPMProjectCreator = new FedoraRPMProjectCreator();
		fedoraRPMProjectCreator.create(pageThree.getProjectType(), pageThree.getExternalFile(), project, monitor);
	}

}
