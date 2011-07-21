package org.fedoraproject.eclipse.packager.fedorarpm.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.eclipse.swt.widgets.Text;


public class FedoraRPMProjectPageThree extends WizardPage {
	private Button btnCheckFeature;
	private Button btnFeatureBrowse;
	private Label lblFeature;
	private Text textFeature;

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

		selectControl();
		setControl(container);
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
