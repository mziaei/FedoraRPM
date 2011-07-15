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
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		layout.verticalSpacing = 9;

		grpAccount = new Group(container, SWT.NONE);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		grpAccount.setLayoutData(layoutData);
		grpAccount.setLayout(new GridLayout(1, false));
		grpAccount.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_grpAccountSetup);
		
		btnExistingMaintainer = new Button(grpAccount, SWT.RADIO);
		btnExistingMaintainer.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_btnRadioExistMaintainer);
		
		btnNewMaintainer = new Button(grpAccount, SWT.RADIO);
		btnNewMaintainer.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_btnRadioNewMaintainer);
		
		lblCheckFAS = new Label(grpAccount, SWT.CHECK);
		lblCheckFAS.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_lblCheckFAS);
        textFAS = new Text(grpAccount, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL);
        gd.horizontalIndent = 30;
        textFAS.setLayoutData(gd);
        setLayout(lblCheckFAS);
        
		lblCheckBugzilla = new Label(grpAccount, SWT.NONE);
		lblCheckBugzilla.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_lblCheckBugzilla);
        setLayout(lblCheckBugzilla);
        
		lblCheckInitial = new Label(grpAccount, SWT.NONE);
		lblCheckInitial.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_lblCheckInitial);
        setLayout(lblCheckInitial);
        
		lblCheckIntroduce = new Label(grpAccount, SWT.NONE);
		lblCheckIntroduce.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_lblCheckIntroduce);
        setLayout(lblCheckIntroduce);
        
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
		    lblCheckBugzilla.setEnabled(true);
		    lblCheckFAS.setEnabled(true);
		    lblCheckInitial.setEnabled(true);
		    lblCheckIntroduce.setEnabled(true);
		    lblNoteGit.setEnabled(true);
		    textFAS.setEnabled(true);
		}
		else {
		    lblCheckBugzilla.setEnabled(false);
		    lblCheckFAS.setEnabled(false);
		    lblCheckInitial.setEnabled(false);
		    lblCheckIntroduce.setEnabled(false);
		    lblNoteGit.setEnabled(false);
		    textFAS.setEnabled(false);
		}
	}
	
	protected void setLayout(Label label) {
        GridData layout = new GridData();
        layout.horizontalIndent = 20;
        label.setLayoutData(layout);
	}

}
