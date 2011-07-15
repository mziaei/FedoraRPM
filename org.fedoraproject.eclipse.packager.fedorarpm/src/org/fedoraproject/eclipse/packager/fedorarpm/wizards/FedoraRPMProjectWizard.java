package org.fedoraproject.eclipse.packager.fedorarpm.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.fedoraproject.eclipse.packager.fedorarpm.core.*;;

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
		// TODO Auto-generated method stub

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

	@Override
	public boolean performFinish() {
		try {
			WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
				@Override
				protected void execute(IProgressMonitor monitor) {
					createProject(monitor != null ? monitor
							: new NullProgressMonitor());
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

	protected void createProject(IProgressMonitor monitor) {
		FedoraRPMProjectCreator fedoraRPMProjectCreator = new FedoraRPMProjectCreator();
		fedoraRPMProjectCreator.create(pageOne.getProjectName(), pageOne.getLocationPath(), monitor);
	}

}
