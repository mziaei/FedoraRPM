package org.fedoraproject.eclipse.packager.fedorarpm.wizards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

public class FedoraRPMProjectPageOne extends WizardNewProjectCreationPage {

	/**
	 * Create the wizard.
	 */
	public FedoraRPMProjectPageOne() {
		super("wizardPage");
		setTitle("Wizard Page title");
		setDescription("Wizard Page description");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
	}

}
