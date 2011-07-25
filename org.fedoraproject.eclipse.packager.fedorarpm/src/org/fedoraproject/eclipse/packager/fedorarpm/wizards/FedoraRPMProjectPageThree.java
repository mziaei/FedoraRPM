package org.fedoraproject.eclipse.packager.fedorarpm.wizards;

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


public class FedoraRPMProjectPageThree extends WizardPage {
	private Button btnCheckFeature;
	private Button btnFeatureBrowse;
	private Button btnCheckSrpm;
	private Button btnSrpmBrowse;
	private Label lblFeature;
	private Label lblSrpm;
	private Text textFeature;
	private Text textSrpm;

	/**
	 * Create the wizard.
	 */
	public FedoraRPMProjectPageThree(String pageName) {
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

		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;

		btnCheckFeature = new Button(container, SWT.CHECK);
		btnCheckFeature.setText(FedoraRPMMessages.FedoraRPMProjectPageThree_btnCheckFeature);
		GridData layoutData = new GridData();
		layoutData.horizontalSpan = 3;
		btnCheckFeature.setLayoutData(layoutData);
		
		lblFeature = new Label(container, SWT.NONE);
		lblFeature.setText(FedoraRPMMessages.FedoraRPMProjectPageThree_lblFeature);
		layoutData = new GridData();
		layoutData.horizontalIndent = 25;
		lblFeature.setLayoutData(layoutData);
		
		textFeature = new Text(container, SWT.BORDER | SWT.SINGLE);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
        textFeature.setLayoutData(layoutData);
        
		btnFeatureBrowse = new Button(container, SWT.PUSH);
		btnFeatureBrowse.setText(FedoraRPMMessages.FedoraRPMProjectPageThree_btnFeatureBrowse);

		btnCheckFeature.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectControl();
			}
		});

		btnCheckSrpm = new Button(container, SWT.CHECK);
		btnCheckSrpm.setText(FedoraRPMMessages.FedoraRPMProjectPageThree_btnCheckSrpm);
		layoutData = new GridData();
		layoutData.horizontalSpan = 3;
		btnCheckSrpm.setLayoutData(layoutData);
		
		lblSrpm = new Label(container, SWT.NONE);
		lblSrpm.setText(FedoraRPMMessages.FedoraRPMProjectPageThree_lblSrpm);
		layoutData = new GridData();
		layoutData.horizontalIndent = 25;
		lblSrpm.setLayoutData(layoutData);
		
		textSrpm = new Text(container, SWT.BORDER | SWT.SINGLE);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
        textSrpm.setLayoutData(layoutData);
        
		btnSrpmBrowse = new Button(container, SWT.PUSH);
		btnSrpmBrowse.setText(FedoraRPMMessages.FedoraRPMProjectPageThree_btnSrpmBrowse);
		
		btnSrpmBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell());
				dialog.setText("Select File");
				dialog.setFilterExtensions(new String[] { "*.src.rpm" });
				String filePath = dialog.open();
				if (filePath != null) {
					File file = new File(filePath);
					if (file.isFile()) {
						displayFiles(new String[] {file.toString()});
					}
					else {
						displayFiles(file.list());
					}
				}
			}
		}); 
		
		
		selectControl();
		setControl(container);
	}
	
	private void displayFiles(String[] files) {
		for (int i = 0; files != null && i < files.length; i++) {
			textSrpm.setText(files[i]);
			textSrpm.setEditable(true);
		}
	}

	
	protected void selectControl() {
		if(btnCheckFeature.getSelection()){
		    lblFeature.setEnabled(true);
		    textFeature.setEnabled(true);
		    btnFeatureBrowse.setEnabled(true);
		}
		else {
		    lblFeature.setEnabled(false);
		    textFeature.setEnabled(false);
		    btnFeatureBrowse.setEnabled(false);
		}
	}

}
