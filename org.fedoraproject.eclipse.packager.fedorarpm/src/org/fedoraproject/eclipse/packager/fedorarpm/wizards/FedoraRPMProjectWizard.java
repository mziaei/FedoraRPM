package org.fedoraproject.eclipse.packager.fedorarpm.wizards;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class FedoraRPMProjectWizard extends Wizard implements INewWizard {
	private static final String PAGE_ONE = "PageOne";
	private static final String PAGE_TWO = "PageTwo";

	private FedoraRPMProjectPageOne pageOne;
	private FedoraRPMProjectPageTwo pageTwo;

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
	}

	@Override
	public boolean performFinish() {
//		try {
//			WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
//				@Override
//				protected void execute(IProgressMonitor monitor) {
//					createProject(monitor != null ? monitor
//							: new NullProgressMonitor());
//				}
//			};
//			getContainer().run(false, true, op);
//		} catch (InvocationTargetException x) {
//			return false;
//		} catch (InterruptedException x) {
//			return false;
//		}
//		return true;
		return false;
	}

	protected void createProject(IProgressMonitor monitor) {
//		RPMProjectCreator rpmProjectCreator = new RPMProjectCreator();
//		rpmProjectCreator.create(pageOne.getProjectName(), pageOne.getLocationPath(), monitor);
	}

}
