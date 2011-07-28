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
	private Button btnCheckStubby;
	private Button btnStubbyBrowse;
	private Button btnCheckSrpm;
	private Button btnSrpmBrowse;
	private Label lblStubby;
	private Label lblSrpm;
	private Text textStubby;
	private Text textSrpm;
	private boolean isSrpmProject;
	private boolean isStubbyProject;
	private File srpmFile;
	private File StubbyFile;
	
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

		btnCheckStubby = new Button(container, SWT.RADIO);
		btnCheckStubby.setText(FedoraRPMMessages.FedoraRPMProjectPageThree_btnCheckStubby);
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
		lblStubby.setText(FedoraRPMMessages.FedoraRPMProjectPageThree_lblStubby);
		layoutData = new GridData();
		layoutData.horizontalIndent = 25;
		lblStubby.setLayoutData(layoutData);
		
		textStubby = new Text(container, SWT.BORDER | SWT.SINGLE);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
        textStubby.setLayoutData(layoutData);
        
		btnStubbyBrowse = new Button(container, SWT.PUSH);
		btnStubbyBrowse.setText(FedoraRPMMessages.FedoraRPMProjectPageThree_btnStubbyBrowse);

		btnStubbyBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.SAVE);
				dialog.setText("Select File");
				dialog.setFilterExtensions(new String[] { "*.xml" });
				String filePath = dialog.open();
				textStubby.setText(filePath.toString());	
				
				StubbyFile = new File(filePath);
				isStubbyProject = true;
					
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
				isSrpmProject = true;
			}
		}); 
		
		
		selectControl();
		setControl(container);
	}
	
	public boolean isSrpmProject() {
		return isSrpmProject;
	}
	
	public File getSrpmFile() {
		return srpmFile;
	}
	
	public boolean isStubbyProject() {
		return isStubbyProject;
	}
	
	public File getStubbyFile() {
		return StubbyFile;
	}
	
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
