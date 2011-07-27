package org.fedoraproject.eclipse.packager.fedorarpm.wizards;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
import org.fedoraproject.eclipse.packager.fedorarpm.core.SRPM;


public class FedoraRPMProjectPageThree extends WizardPage {
	private Button btnCheckFeature;
	private Button btnFeatureBrowse;
	private Button btnCheckSrpm;
	private Button btnSrpmBrowse;
	private Label lblFeature;
	private Label lblSrpm;
	private Text textFeature;
	private Text textSrpm;
	private boolean isSrpmProject;
	private boolean isFeatureProject;
	private SRPM srpm;
	private File srpmFile;
	
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

		btnCheckFeature = new Button(container, SWT.RADIO);
		btnCheckFeature.setText(FedoraRPMMessages.FedoraRPMProjectPageThree_btnCheckFeature);
		GridData layoutData = new GridData();
		layoutData.horizontalSpan = 3;
		btnCheckFeature.setLayoutData(layoutData);
		
		btnCheckFeature.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectControl();
			}
		});
		
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

		btnFeatureBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.SAVE);
				dialog.setText("Select File");
				dialog.setFilterExtensions(new String[] { "*.xml" });
				String filePath = dialog.open();
				textSrpm.setText(filePath.toString());	
				
				IPath fileIPath = new Path(filePath);
				IFile featureFile = ResourcesPlugin.getWorkspace().getRoot().getFile(fileIPath);
					
			}
		}); 


		btnCheckSrpm = new Button(container, SWT.RADIO);
		btnCheckSrpm.setText(FedoraRPMMessages.FedoraRPMProjectPageThree_btnCheckSrpm);
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
				FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.SAVE);
				dialog.setText("Select File");
				dialog.setFilterExtensions(new String[] { "*.src.rpm" });
				String filePath = dialog.open();
				textSrpm.setText(filePath.toString());	
				
				srpmFile = new File(filePath);
				IPath fileIPath = new Path(filePath);
				IFile srpmFile = ResourcesPlugin.getWorkspace().getRoot().getFile(fileIPath);
				
				srpm = new SRPM();
//				srpm.setSrpmFile(srpmFile);
				isSrpmProject = true;
			}
		}); 
		
		
		selectControl();
		setControl(container);
	}
	
	public boolean isSrpmProject() {
		return isSrpmProject;
	}
//		SRPMFedoraProjectCreator project = new SRPMFedoraProjectCreator();
////		project.
//
	
	public SRPM getSRPM() {
		return srpm;
	}
	
	public File getSrpmFile() {
		return srpmFile;
	}
	
	protected void selectControl() {
		if(btnCheckFeature.getSelection()){
		    lblFeature.setEnabled(true);
		    textFeature.setEnabled(true);
		    btnFeatureBrowse.setEnabled(true);
		    lblSrpm.setEnabled(false);
		    textSrpm.setEnabled(false);
		    btnSrpmBrowse.setEnabled(false);
		}
		else if(btnCheckSrpm.getSelection()) {
		    lblSrpm.setEnabled(true);
		    textSrpm.setEnabled(true);
		    btnSrpmBrowse.setEnabled(true);
		    lblFeature.setEnabled(false);
		    textFeature.setEnabled(false);
		    btnFeatureBrowse.setEnabled(false);
		}
		else {
		    lblFeature.setEnabled(false);
		    textFeature.setEnabled(false);
		    btnFeatureBrowse.setEnabled(false);
		    lblSrpm.setEnabled(false);
		    textSrpm.setEnabled(false);
		    btnSrpmBrowse.setEnabled(false);
		}
	}

}
