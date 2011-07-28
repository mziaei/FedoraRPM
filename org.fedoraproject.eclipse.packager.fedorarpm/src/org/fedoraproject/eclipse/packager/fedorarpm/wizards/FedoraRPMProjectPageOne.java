package org.fedoraproject.eclipse.packager.fedorarpm.wizards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

public class FedoraRPMProjectPageOne extends WizardNewProjectCreationPage {
	private Label lblNoteGit;
	
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

		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		lblNoteGit = new Label(container, SWT.NONE);
		lblNoteGit.setText(FedoraRPMMessages.FedoraRPMProjectPageOne_lblNoteGit);
		lblNoteGit.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
		GridData layoutData = new GridData();
        lblNoteGit.setLayoutData(layoutData);
        
		setControl(container);
	}



}
