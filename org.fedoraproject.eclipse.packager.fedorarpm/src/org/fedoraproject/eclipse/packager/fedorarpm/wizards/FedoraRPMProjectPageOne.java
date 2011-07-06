package org.fedoraproject.eclipse.packager.fedorarpm.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;


public class FedoraRPMProjectPageOne extends WizardNewProjectCreationPage {

	/**
	 * Create the wizard.
	 */
	public FedoraRPMProjectPageOne(String pageName) {
		super(pageName);
		setTitle(FedoraRPMMessages.FedoraRPMProject_title); //$NON-NLS-1$
		setDescription(FedoraRPMMessages.FedoraRPMProject_description); //$NON-NLS-1$
		setImageDescriptor(ImageDescriptor.createFromFile(getClass(),
			"/icons/fedora48x48.png")); //$NON-NLS-1$
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);

		setControl(parent);

	}
}
