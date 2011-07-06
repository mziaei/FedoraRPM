package org.fedoraproject.eclipse.packager.fedorarpm.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
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
	private Composite composite;
	private Group grpAccount;
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
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		layout.verticalSpacing = 9;

		grpAccount = new Group(container, SWT.NONE);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 5;
		grpAccount.setLayoutData(layoutData);
		grpAccount.setLayout(new GridLayout(1, false));
		grpAccount.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_grpAccountSetup);
		
		btnExistingMaintainer = new Button(grpAccount, SWT.RADIO);
		btnExistingMaintainer.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_btnRadioExistMaintainer);
		
		btnNewMaintainer = new Button(grpAccount, SWT.RADIO);
		btnNewMaintainer.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_btnRadioNewMaintainer);
		
		btnCheckFAS = new Button(grpAccount, SWT.CHECK);
		btnCheckFAS.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_btnCheckFAS);
        setLayout(btnCheckFAS);
        
		btnCheckBugzilla = new Button(grpAccount, SWT.CHECK);
		btnCheckBugzilla.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_btnCheckBugzilla);
        setLayout(btnCheckBugzilla);
        
		btnCheckInitial = new Button(grpAccount, SWT.CHECK);
		btnCheckInitial.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_btnCheckInitial);
        setLayout(btnCheckInitial);
        
		btnCheckIntroduce = new Button(grpAccount, SWT.CHECK);
		btnCheckIntroduce.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_btnCheckIntroduce);
        setLayout(btnCheckIntroduce);
        
		lblNoteGit = new Label(grpAccount, SWT.NONE);
		lblNoteGit.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_lblNoteGit);
	    
		btnNewMaintainer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectControl();
			}
		});
		
		
		selectControl();
		setControl(container);
		
	}
	
	protected void selectControl() {
		if(btnNewMaintainer.getSelection()){
		    btnCheckBugzilla.setEnabled(true);
		    btnCheckFAS.setEnabled(true);
		    btnCheckInitial.setEnabled(true);
		    btnCheckIntroduce.setEnabled(true);
		    lblNoteGit.setEnabled(true);
		}
		else {
		    btnCheckBugzilla.setEnabled(false);
		    btnCheckFAS.setEnabled(false);
		    btnCheckInitial.setEnabled(false);
		    btnCheckIntroduce.setEnabled(false);
		    lblNoteGit.setEnabled(false);
		}
	}
	
	protected void setLayout(Button button) {
        GridData layout = new GridData();
        layout.horizontalIndent = 20;
        button.setLayoutData(layout);
	}

}
