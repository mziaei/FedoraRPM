package org.fedoraproject.eclipse.packager.fedorarpm.wizards;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;


public class FedoraRPMProjectPageOne extends WizardNewProjectCreationPage {
	
	/**
	 * Create the wizard.
	 */
	public FedoraRPMProjectPageOne(String pageName) {
		super(pageName);
		setTitle(FedoraRPMMessages.FedoraRPMProject_title);
		setDescription(FedoraRPMMessages.FedoraRPMProject_description);
		FedorarpmPlugin.getImageDescriptor(FedoraRPMMessages.FedoraRPMProject_image);

	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		Composite container = (Composite) getControl();

		setControl(container);
	}



}
