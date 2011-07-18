package org.fedoraproject.eclipse.packager.fedorarpm.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FedoraRPMProjectPageTwo extends WizardPage {
	private Group grpAccount;
	private Button btnNewMaintainer;
	private Button btnExistingMaintainer;
	private Label lblNoteGit;
	private Label lblCheckIntroduce;
	private Label lblCheckInitial;
	private Label lblCheckBugzilla;
	private Label lblCheckFAS;
	private Text textFAS;

	/**
	 * Create the wizard.
	 */
	public FedoraRPMProjectPageTwo(String pageName) {
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

		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		grpAccount = new Group(container, SWT.NONE);
		grpAccount.setLayout(new GridLayout(2, false));
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		grpAccount.setLayoutData(layoutData);
		grpAccount.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_grpAccountSetup);

		btnExistingMaintainer = new Button(grpAccount, SWT.RADIO);
		btnExistingMaintainer.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_btnRadioExistMaintainer);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 2;
		btnExistingMaintainer.setLayoutData(layoutData);

		btnNewMaintainer = new Button(grpAccount, SWT.RADIO);
		btnNewMaintainer.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_btnRadioNewMaintainer);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 2;
		btnNewMaintainer.setLayoutData(layoutData);

		lblCheckFAS = new Label(grpAccount, SWT.NONE);
		lblCheckFAS.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_lblCheckFAS);
		Label lblBrowseFAS = new Label(grpAccount, SWT.NONE);
		lblBrowseFAS.setText("Create FAS account");

        textFAS = new Text(grpAccount, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData();
        gd.widthHint = 100;
        gd.horizontalSpan = 2;
        gd.horizontalIndent = 45;
        textFAS.setLayoutData(gd);
        setLayout(lblCheckFAS);

		lblCheckBugzilla = new Label(grpAccount, SWT.NONE);
		lblCheckBugzilla.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_lblCheckBugzilla);
        setLayout(lblCheckBugzilla);
		lblBrowseFAS = new Label(grpAccount, SWT.NONE);
		lblBrowseFAS.setText("Create FAS account");

		lblCheckInitial = new Label(grpAccount, SWT.NONE);
		lblCheckInitial.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_lblCheckInitial);
        setLayout(lblCheckInitial);
		lblBrowseFAS = new Label(grpAccount, SWT.NONE);
		lblBrowseFAS.setText("Create FAS account");

		lblCheckIntroduce = new Label(grpAccount, SWT.NONE);
		lblCheckIntroduce.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_lblCheckIntroduce);
        setLayout(lblCheckIntroduce);
		lblBrowseFAS = new Label(grpAccount, SWT.NONE);
		lblBrowseFAS.setText("Create FAS account");

		lblNoteGit = new Label(grpAccount, SWT.NONE);
		lblNoteGit.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_lblNoteGit);
		setLayout(lblNoteGit);

		btnNewMaintainer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectControl();
			}
		});

		btnExistingMaintainer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectControl();
			}
		});

		textFAS.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String name = textFAS.getText();
				setPageComplete(name.length() > 0);
			}
		});

		selectControl();
		setPageComplete(false);
		setControl(container);
	}


	protected void selectControl() {
		if(btnNewMaintainer.getSelection()){
		    lblCheckBugzilla.setEnabled(true);
		    lblCheckFAS.setEnabled(true);
		    lblCheckInitial.setEnabled(true);
		    lblCheckIntroduce.setEnabled(true);
		    lblNoteGit.setEnabled(true);
		    textFAS.setEnabled(true);
		    setPageComplete(false);
		}
		else {
		    lblCheckBugzilla.setEnabled(false);
		    lblCheckFAS.setEnabled(false);
		    lblCheckInitial.setEnabled(false);
		    lblCheckIntroduce.setEnabled(false);
		    lblNoteGit.setEnabled(false);
		    textFAS.setEnabled(false);
		    setPageComplete(true);
		}
	}

	protected void setLayout(Label label) {
        GridData layout = new GridData();
        layout.horizontalIndent = 20;
        label.setLayoutData(layout);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
//	@Override
//	public boolean canFlipToNextPage() {
////		if (btnExistingMaintainer.getSelection()) {
//		if (textFAS.getCharCount() != 0 || btnExistingMaintainer.getSelection()
//			|| isPageComplete()) {
//			return true;
//		}
//		return false;
////		return super.canFlipToNextPage();
//	}
//
//	@Override
//	public boolean isPageComplete() {
//		return btnExistingMaintainer.getSelection() || textFAS.getCharCount() != 0;
//	}
}
