package org.fedoraproject.eclipse.packager.fedorarpm.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.fedoraproject.eclipse.packager.fedorarpm.core.FedoraRPMProjectCreator;

public class FedoraRPMProjectWizard extends Wizard implements INewWizard {

	private static final String PAGE_ONE = "PageOne";
	private static final String PAGE_TWO = "PageTwo";
	private static final String PAGE_THREE = "PageThree";

	private FedoraRPMProjectPageOne pageOne;
	private FedoraRPMProjectPageTwo pageTwo;
	private FedoraRPMProjectPageThree pageThree;

	public FedoraRPMProjectWizard() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	/* (non-Javadoc)
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
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		try {
			WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
				@Override
				protected void execute(IProgressMonitor monitor) {
					try {
						createProject(monitor != null ? monitor
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

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		if (getContainer().getCurrentPage() == pageThree) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Creates a new instance of the FedoraRPM project.
	 *
	 * @param monitor
	 * @throws WrongRepositoryStateException 
	 * @throws JGitInternalException 
	 * @throws ConcurrentRefUpdateException 
	 * @throws NoMessageException 
	 * @throws NoHeadException 
	 */
	protected void createProject(IProgressMonitor monitor) throws NoHeadException, 
				NoMessageException, ConcurrentRefUpdateException, 
				JGitInternalException, WrongRepositoryStateException {
		FedoraRPMProjectCreator fedoraRPMProjectCreator = new FedoraRPMProjectCreator();
		fedoraRPMProjectCreator.create(pageOne.getProjectName(), pageOne.getLocationPath(), monitor);
	}

}
