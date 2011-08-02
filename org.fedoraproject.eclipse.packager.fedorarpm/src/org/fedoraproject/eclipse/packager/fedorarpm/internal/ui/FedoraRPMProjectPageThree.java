package org.fedoraproject.eclipse.packager.fedorarpm.internal.ui;

import java.io.File;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fedoraproject.eclipse.packager.fedorarpm.FedoraRPMText;
import org.fedoraproject.eclipse.packager.fedorarpm.FedorarpmPlugin;


public class FedoraRPMProjectPageThree extends WizardPage {
	private static final String SRPM = "*.src.rpm"; //$NON-NLS-1$
	private static final String FEATURE = "feature.xml"; //$NON-NLS-1$
	private static final String POM = "pom.xml"; //$NON-NLS-1$

	private Button btnCheckStubby;
	private Button btnStubbyBrowse;
	private Button btnCheckSrpm;
	private Button btnSrpmBrowse;
	private Label lblStubby;
	private Label lblSrpm;
	private Text textStubby;
	private Text textSrpm;
	
	private String projectType = "plain";
	private File externalFile = null;

	/**
	 * Create the wizard.
	 */
	public FedoraRPMProjectPageThree(String pageName) {
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

		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;

		btnCheckStubby = new Button(container, SWT.RADIO);
		btnCheckStubby.setText(FedoraRPMText.FedoraRPMProjectPageThree_btnCheckStubby);
		GridData layoutData = new GridData();
		layoutData.horizontalSpan = 3;
		btnCheckStubby.setLayoutData(layoutData);

		btnCheckStubby.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectControl();
			}
		});

		lblStubby = new Label(container, SWT.NONE);
		lblStubby.setText(FedoraRPMText.FedoraRPMProjectPageThree_lblStubby);
		layoutData = new GridData();
		layoutData.horizontalIndent = 25;
		lblStubby.setLayoutData(layoutData);

		textStubby = new Text(container, SWT.BORDER | SWT.SINGLE);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
        textStubby.setLayoutData(layoutData);

		btnStubbyBrowse = new Button(container, SWT.PUSH);
		btnStubbyBrowse.setText(FedoraRPMText.FedoraRPMProjectPageThree_btnStubbyBrowse);

		btnStubbyBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fileDialogRunner(FEATURE, textStubby, FedoraRPMText.FedoraRPMProjectIWizard_Stubby);
			}
		});


		btnCheckSrpm = new Button(container, SWT.RADIO);
		btnCheckSrpm.setText(FedoraRPMText.FedoraRPMProjectPageThree_btnCheckSrpm);
		layoutData = new GridData();
		layoutData.horizontalSpan = 3;
		btnCheckSrpm.setLayoutData(layoutData);

		btnCheckSrpm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectControl();
			}
		});

		lblSrpm = new Label(container, SWT.NONE);
		lblSrpm.setText(FedoraRPMText.FedoraRPMProjectPageThree_lblSrpm);
		layoutData = new GridData();
		layoutData.horizontalIndent = 25;
		lblSrpm.setLayoutData(layoutData);

		textSrpm = new Text(container, SWT.BORDER | SWT.SINGLE);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
        textSrpm.setLayoutData(layoutData);

		btnSrpmBrowse = new Button(container, SWT.PUSH);
		btnSrpmBrowse.setText(FedoraRPMText.FedoraRPMProjectPageThree_btnSrpmBrowse);

		btnSrpmBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fileDialogRunner(SRPM, textSrpm, FedoraRPMText.FedoraRPMProjectIWizard_SRpm);
			}

		});


		selectControl();
		setControl(container);
	}
	
	private void fileDialogRunner(String filter, Text text, String type) {
		FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.SAVE);
		dialog.setText("Select File");
		dialog.setFilterExtensions(new String[] {filter});
		String filePath = dialog.open();
		text.setText(filePath.toString());
		
		externalFile = new File(filePath);
		projectType = type;

		
	}
	
	/**
	 * Return the external file to the user's selected file
	 *
	 * @return File
	 */
	public File getExternalFile() {
		return externalFile;
	}
	
	/**
	 * Return the type of the project based on the user's selection
	 *
	 * @return String
	 *            type of the populated project
	 */
	public String getProjectType() {
		return projectType;
	}

	/**
	 * Set the type of the project and the uploaded file
	 *
	 * @param String path for the file
	 * @param String type of the populated project
	 */
	public void setProject(String filePath, String type) {
		externalFile = new File(filePath);
		projectType = type;
	}
	
	/**
	 * Sets the enabled properties based on the selected button
	 */
	protected void selectControl() {
		if(btnCheckStubby.getSelection()){
		    lblStubby.setEnabled(true);
		    textStubby.setEnabled(true);
		    btnStubbyBrowse.setEnabled(true);
		    lblSrpm.setEnabled(false);
		    textSrpm.setEnabled(false);
		    btnSrpmBrowse.setEnabled(false);
		}
		else if(btnCheckSrpm.getSelection()) {
		    lblSrpm.setEnabled(true);
		    textSrpm.setEnabled(true);
		    btnSrpmBrowse.setEnabled(true);
		    lblStubby.setEnabled(false);
		    textStubby.setEnabled(false);
		    btnStubbyBrowse.setEnabled(false);
		}
		else {
		    lblStubby.setEnabled(false);
		    textStubby.setEnabled(false);
		    btnStubbyBrowse.setEnabled(false);
		    lblSrpm.setEnabled(false);
		    textSrpm.setEnabled(false);
		    btnSrpmBrowse.setEnabled(false);
		}
	}

}
