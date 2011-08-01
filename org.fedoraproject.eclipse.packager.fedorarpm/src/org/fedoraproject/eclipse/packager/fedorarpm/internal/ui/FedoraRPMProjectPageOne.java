package org.fedoraproject.eclipse.packager.fedorarpm.internal.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.fedoraproject.eclipse.packager.fedorarpm.FedoraRPMText;
import org.fedoraproject.eclipse.packager.fedorarpm.FedorarpmPlugin;

public class FedoraRPMProjectPageOne extends WizardNewProjectCreationPage {
	private Label lblNoteGit;

	/**
	 * Create the wizard.
	 */
	public FedoraRPMProjectPageOne(String pageName) {
		super(pageName);
		setTitle(FedoraRPMText.FedoraRPMProject_title);
		setDescription(FedoraRPMText.FedoraRPMProject_description);
		FedorarpmPlugin.getImageDescriptor(FedoraRPMText.FedoraRPMProject_image);
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
		lblNoteGit.setText(FedoraRPMText.FedoraRPMProjectPageOne_lblNoteGit);
		lblNoteGit.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
		GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
        lblNoteGit.setLayoutData(layoutData);

		setControl(container);
	}



}
