package org.fedoraproject.eclipse.packager.fedorarpm.internal.ui;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.linuxtools.rpm.ui.editor.wizards.SpecfileNewWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.fedoraproject.eclipse.packager.fedorarpm.FedoraRPMText;
import org.fedoraproject.eclipse.packager.fedorarpm.FedorarpmPlugin;

public class FedoraRPMProjectPageFour extends SpecfileNewWizardPage {


	/**
	 * Create the wizard.
	 */
	public FedoraRPMProjectPageFour(String pageName, ISelection selection) {
		super(selection);
		setTitle(FedoraRPMText.FedoraRPMProject_title);
		setDescription(FedoraRPMText.FedoraRPMProject_description);
		FedorarpmPlugin
				.getImageDescriptor(FedoraRPMText.FedoraRPMProject_image);

	}

	/**
	 * Create contents of the wizard.
	 *
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
	}

}