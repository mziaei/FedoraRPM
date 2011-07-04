package org.fedoraproject.eclipse.packager.fedorarpm.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.swt.widgets.Text;


public class FedoraRPMProjectPageOne extends WizardNewProjectCreationPage {
	private Text containerText;
	private Button packagerExist;
	private Label gitExist;
	private Text gitExistURL;
	private Group grpFedora, grpAccount;
	private Button gitInit;
	private Text gitInitURL;
	private Button gitBrowse;
	private Button btnAccount1;
	private Label lblAccount5;
	private Button btnAccount2;
	private Button btnAccount3;
	private Button btnAccount4;
	private Composite composite;
	/**
	 * Create the wizard.
	 */
	public FedoraRPMProjectPageOne(String pageName) {
		super(pageName);
		setTitle(FedoraRPMMessages.FedoraRPMProject_0); //$NON-NLS-1$
		setDescription(FedoraRPMMessages.FedoraRPMProject_1); //$NON-NLS-1$
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
		Composite container = (Composite) getControl();

		
//		grpFedora = new Group(container, SWT.NONE);
//		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
//		layoutData.horizontalSpan = 3;
//		grpFedora.setLayoutData(layoutData);
//		grpFedora.setFont(container.getFont());
//		grpFedora.setLayout(new GridLayout(1, false));
		
		composite = new Composite(container, SWT.NONE);
		GridData layoutComposite = new GridData(GridData.FILL_HORIZONTAL);
		layoutComposite.horizontalSpan = 3;
		composite.setLayoutData(layoutComposite);
		composite.setLayout(new GridLayout(1, false));
		
		Button btnRadioNew = new Button(composite, SWT.RADIO);
		btnRadioNew.setText(FedoraRPMMessages.FedoraRPMProjectPageOne_btnRadioButton_text_1);

		grpAccount = new Group(composite, SWT.NONE);
		grpAccount.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		grpAccount.setLayout(new GridLayout(1, false));
		grpAccount.setText(FedoraRPMMessages.FedoraRPMProjectPageOne_grpAccountSetup_text);
		grpAccount.setFont(container.getFont());
		
		btnAccount1 = new Button(grpAccount, SWT.CHECK);
		btnAccount1.setText(FedoraRPMMessages.FedoraRPMProjectPageOne_btnCheckButton_text_1);
		
		btnAccount2 = new Button(grpAccount, SWT.CHECK);
		btnAccount2.setText(FedoraRPMMessages.FedoraRPMProjectPageOne_btnCheckButton_text_2);
		
		btnAccount3 = new Button(grpAccount, SWT.CHECK);
		btnAccount3.setText(FedoraRPMMessages.FedoraRPMProjectPageOne_btnCheckButton_1_text);
		
		btnAccount4 = new Button(grpAccount, SWT.CHECK);
		btnAccount4.setText(FedoraRPMMessages.FedoraRPMProjectPageOne_btnCheckButton_2_text);
		
		lblAccount5 = new Label(grpAccount, SWT.NONE);
		lblAccount5.setText(FedoraRPMMessages.FedoraRPMProjectPageOne_lblNewLabel_1_text_1);
		new Label(grpAccount, SWT.NONE);
		
		Button btnRadioExist = new Button(composite, SWT.RADIO);
		btnRadioExist.setText(FedoraRPMMessages.FedoraRPMProjectPageOne_btnRadioButton_1_text_1);
		

		
//		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
//		layoutData.horizontalSpan = 3;
//		fGroup.setLayoutData(layoutData);
//		fGroup.setLayout(new GridLayout(3, false));
//		fGroup.setText(FedoraRPMMessages.FedoraRPMProject_2);
//		
//		GridData textLayout = new GridData(SWT.FILL, SWT.CENTER, true, false);
//		
//		gitInit = createButton(fGroup, SWT.CHECK, FedoraRPMMessages.FedoraRPMProject_3);
//		gitInitURL = createText(fGroup, SWT.BORDER | SWT.SINGLE);
//		gitInitURL.setLayoutData(textLayout);
//		gitBrowse = createButton(fGroup, SWT.PUSH, "Local repository...");
//		
//		packagerExist = createButton(fGroup, SWT.CHECK, FedoraRPMMessages.FedoraRPMProject_4);
//		packagerExist.setLayoutData(layoutData);
//		gitExist = createLabel(fGroup, SWT.NONE, FedoraRPMMessages.FedoraRPMProject_5);
//		gitExist.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
//		gitExistURL = createText(fGroup, SWT.BORDER | SWT.SINGLE);
//		gitExistURL.setLayoutData(textLayout);
//		
//		packagerExist.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				selectControl();
//			}
//		});
//		
//		selectControl();
		setControl(container);
		
	}
}
