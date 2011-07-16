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
		layout.numColumns = 3;
		layout.verticalSpacing = 9;


		btnCheckFeature = new Button(container, SWT.CHECK);
		btnCheckFeature.setText(FedoraRPMMessages.FedoraRPMProjectPageThree_btnCheckFeature);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		btnCheckFeature.setLayoutData(gd);

		lblFeature = new Label(container, SWT.NONE);
		lblFeature.setText(FedoraRPMMessages.FedoraRPMProjectPageThree_lblFeature);

		textFeature = new Text(container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        textFeature.setLayoutData(gd);

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
