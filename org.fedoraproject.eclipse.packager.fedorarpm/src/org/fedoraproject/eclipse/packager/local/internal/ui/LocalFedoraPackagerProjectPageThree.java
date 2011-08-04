/*******************************************************************************
 * Copyright (c) 2010-2011 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat Inc. - initial API and implementation
 *******************************************************************************/
package org.fedoraproject.eclipse.packager.local.internal.ui;

import java.io.File;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fedoraproject.eclipse.packager.local.LocalFedoraPackagerPlugin;
import org.fedoraproject.eclipse.packager.local.LocalFedoraPackagerText;
import org.fedoraproject.eclipse.packager.api.FileDialogRunable;


public class LocalFedoraPackagerProjectPageThree extends WizardPage {

	private static final String PLAIN = "plain"; //$NON-NLS-1$
	private static final String SRPM = "*.src.rpm"; //$NON-NLS-1$
	private static final String[] STUBBY = new String[]{"feature.xml", "pom.xml"};
	
	private Button btnCheckStubby;
	private Button btnStubbyBrowse;
	private Button btnCheckSrpm;
	private Button btnSrpmBrowse;
	private Button btnCheckPlain;
	private Label lblSrpm;
	private Text textStubby;
	private Text textSrpm;
	private Combo comboStubby;
	
	private String projectType = PLAIN;
	private File externalFile = null;
	private boolean canFinish = false;

	/**
	 * Create the wizard.
	 */
	public LocalFedoraPackagerProjectPageThree(String pageName) {
		super(pageName);
		setTitle(LocalFedoraPackagerText.LocalFedoraPackager_title);
		setDescription(LocalFedoraPackagerText.LocalFedoraPackager_description);
		LocalFedoraPackagerPlugin.getImageDescriptor(LocalFedoraPackagerText.LocalFedoraPackager_image);
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
		btnCheckStubby.setText(LocalFedoraPackagerText.LocalFedoraPackager_PageThree_btnCheckStubby);
		GridData layoutData = new GridData();
		layoutData.horizontalSpan = 3;
		btnCheckStubby.setLayoutData(layoutData);

		btnCheckStubby.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectControl();
			}
		});

		comboStubby = new Combo(container, SWT.READ_ONLY);
	    comboStubby.setItems( STUBBY );
	    comboStubby.select(0);
		layoutData = new GridData();
		layoutData.horizontalIndent = 25;
		comboStubby.setLayoutData(layoutData);

		textStubby = new Text(container, SWT.BORDER | SWT.SINGLE);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
        textStubby.setLayoutData(layoutData);

		btnStubbyBrowse = new Button(container, SWT.PUSH);
		btnStubbyBrowse.setText(LocalFedoraPackagerText.LocalFedoraPackager_PageThree_btnStubbyBrowse);

		btnStubbyBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int comboIndex = comboStubby.getSelectionIndex();
				fileDialog(STUBBY[comboIndex], textStubby, LocalFedoraPackagerText.LocalFedoraPackager_IWizard_Stubby);
			}
		});


		btnCheckSrpm = new Button(container, SWT.RADIO);
		btnCheckSrpm.setText(LocalFedoraPackagerText.LocalFedoraPackager_PageThree_btnCheckSrpm);
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
		lblSrpm.setText(LocalFedoraPackagerText.LocalFedoraPackager_PageThree_lblSrpm);
		layoutData = new GridData();
		layoutData.horizontalIndent = 25;
		lblSrpm.setLayoutData(layoutData);

		textSrpm = new Text(container, SWT.BORDER | SWT.SINGLE);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
        textSrpm.setLayoutData(layoutData);

		btnSrpmBrowse = new Button(container, SWT.PUSH);
		btnSrpmBrowse.setText(LocalFedoraPackagerText.LocalFedoraPackager_PageThree_btnSrpmBrowse);

		btnSrpmBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fileDialog(SRPM, textSrpm, LocalFedoraPackagerText.LocalFedoraPackager_IWizard_SRpm);
			}
		});

		btnCheckPlain = new Button(container, SWT.RADIO);
		btnCheckPlain.setText(LocalFedoraPackagerText.LocalFedoraPackager_PageThree_btnCheckPlain);
		layoutData = new GridData();
		layoutData.horizontalSpan = 3;
		btnCheckPlain.setLayoutData(layoutData);

		btnCheckPlain.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectControl();
			}
		});
		selectControl();
		setPageComplete(canFinish);
		setControl(container);
	}
	
	/**
	 * Runs the filaDialog and sets the project type and externalFile 
	 * to be passed to project creator
	 *
	 * @param String
	 *  			filter for the fileDialog
	 * @param Text 
	 *   			text box for file location
	 * @param String
	 * 				type of the project that user selected
	 */
	private void fileDialog(String filter, Text text, String projectType) {
		FileDialogRunable fdr = new FileDialogRunable(filter, 
				LocalFedoraPackagerText.LocalFedoraPackager_IWizard_fileDialog + 
				filter + LocalFedoraPackagerText.LocalFedoraPackager_IWizard_file);
		getShell().getDisplay().syncExec(fdr);
		String filePath = fdr.getFile();		
		text.setText(filePath.toString());
		
		this.externalFile = new File(filePath);
		this.projectType = projectType;	
		
		canFinish = true;
		setPageComplete(true);
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

	@Override
	public boolean canFlipToNextPage() {
//		return (projectType.equals(PLAIN));
		return (btnCheckPlain.getSelection());
	}
	
	/**
	 * Sets the enabled properties based on the selected button
	 */
	protected void selectControl() {
		if(btnCheckStubby.getSelection()){
		    comboStubby.setEnabled(true);
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
		    comboStubby.setEnabled(false);
		    textStubby.setEnabled(false);
		    btnStubbyBrowse.setEnabled(false);
		}
		else {
		    comboStubby.setEnabled(false);
		    textStubby.setEnabled(false);
		    btnStubbyBrowse.setEnabled(false);
		    lblSrpm.setEnabled(false);
		    textSrpm.setEnabled(false);
		    btnSrpmBrowse.setEnabled(false);
		}
	}

}
