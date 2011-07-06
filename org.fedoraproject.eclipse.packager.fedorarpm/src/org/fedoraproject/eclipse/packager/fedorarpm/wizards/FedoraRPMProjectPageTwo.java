package org.fedoraproject.eclipse.packager.fedorarpm.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FedoraRPMProjectPageTwo extends WizardPage {
	private Composite composite;
	private Group group;
	private Button btnNewMaintainer;
	private Button btnExistingMaintainer;
	private Label lblNoteGit;
	private Button btnCheckIntroduce;
	private Button btnCheckInitial;
	private Button btnCheckBugzilla;
	private Button btnCheckFAS;

	/**
	 * Create the wizard.
	 */
	public FedoraRPMProjectPageTwo(String pageName) {
		super(pageName);
//		setTitle(FedoraRPMMessages.FedoraRPMProject_title); //$NON-NLS-1$
//		setDescription(FedoraRPMMessages.FedoraRPMProject_description); //$NON-NLS-1$
//		setImageDescriptor(ImageDescriptor.createFromFile(getClass(),
//			"/icons/fedora48x48.png")); //$NON-NLS-1$
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {
//		composite= new Composite(parent, SWT.NONE);
//
//		group = new Group(composite, SWT.NONE);
//		group.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_grpAccountSetup);
////		group.setBounds(20, 72, 558, 188);
//
//		btnCheckFAS = new Button(group, SWT.CHECK);
////		btnCheckFAS.setBounds(10, 30, 375, 25);
//		btnCheckFAS.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_btnCheckFAS);
//
//		btnCheckBugzilla = new Button(group, SWT.CHECK);
////		btnCheckBugzilla.setBounds(10, 60, 375, 25);
//		btnCheckBugzilla.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_btnCheckBugzilla);
//
//		btnCheckInitial = new Button(group, SWT.CHECK);
//		btnCheckInitial.setBounds(10, 90, 375, 25);
//		btnCheckInitial.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_btnCheckInitial);
//
//		btnCheckIntroduce = new Button(group, SWT.CHECK);
//		btnCheckIntroduce.setBounds(10, 120, 375, 25);
//		btnCheckIntroduce.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_btnCheckIntroduce);
//
//		lblNoteGit = new Label(group, SWT.NONE);
//		lblNoteGit.setBounds(20, 150, 365, 21);
//		lblNoteGit.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_lblNoteGit);
//
//		btnExistingMaintainer = new Button(composite, SWT.RADIO);
//		btnExistingMaintainer.setBounds(10, 10, 153, 25);
//		btnExistingMaintainer.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_btnRadioExistMaintainer);
//
//		btnNewMaintainer = new Button(composite, SWT.RADIO);
//		btnNewMaintainer.setBounds(10, 41, 153, 25);
//		btnNewMaintainer.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_btnRadioNewMaintainer);

//		setControl(composite);

        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
         layout.numColumns = 2;
         composite.setLayout(layout);
         setControl(composite);
         new Label(composite,SWT.NONE).setText("First Name");
         Text firstNameText = new Text(composite,SWT.NONE);
         new Label(composite,SWT.NONE).setText("Last Name");
         Text secondNameText = new Text(composite,SWT.NONE);

	}
}
