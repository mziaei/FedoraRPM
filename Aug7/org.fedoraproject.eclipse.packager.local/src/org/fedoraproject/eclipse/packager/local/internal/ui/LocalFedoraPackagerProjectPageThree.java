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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fedoraproject.eclipse.packager.local.LocalFedoraPackagerText;
import org.fedoraproject.eclipse.packager.api.FileDialogRunable;

public class LocalFedoraPackagerProjectPageThree extends WizardPage {

	private static final String PLAIN = "plain"; //$NON-NLS-1$
	private static final String SRPM = "*.src.rpm"; //$NON-NLS-1$
	private static final String[] STUBBY = new String[]{"feature.xml", "pom.xml"};

	private Group grpSpec;
	private Button btnCheckStubby;
	private Button btnStubbyBrowse;
	private Button btnCheckSrpm;
	private Button btnSrpmBrowse;
	private Button btnCheckPlain;
	private Label lblSrpm;
	private Text textStubby;
	private Text textSrpm;
	private Combo comboStubby;

	private String projectType = "";
	private File externalFile = null;
	private boolean pageCanFinish;

	/**
	 * Create the wizard.
	 */
	public LocalFedoraPackagerProjectPageThree(String pageName) {
		super(pageName);
		setTitle(LocalFedoraPackagerText.LocalFedoraPackager_title);
		setDescription(LocalFedoraPackagerText.LocalFedoraPackager_description);
		setImageDescriptor(ImageDescriptor.createFromFile(getClass(),
				LocalFedoraPackagerText.LocalFedoraPackager_image));
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

		grpSpec = new Group(container, SWT.NONE);
		grpSpec.setLayout(new GridLayout(3, false));
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		grpSpec.setLayoutData(layoutData);
		grpSpec.setText(LocalFedoraPackagerText.LocalFedoraPackager_PageThree_grpSpec);

		btnCheckStubby = new Button(grpSpec, SWT.RADIO);
		btnCheckStubby.setText(LocalFedoraPackagerText.LocalFedoraPackager_PageThree_btnCheckStubby);
		layoutData = new GridData();
		layoutData.horizontalSpan = 3;
		btnCheckStubby.setLayoutData(layoutData);

		btnCheckStubby.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectControl();
				setPageStatus(false, false);
			}
		});

		comboStubby = new Combo(grpSpec, SWT.READ_ONLY);
	    comboStubby.setItems( STUBBY );
	    comboStubby.select(0);
		layoutData = new GridData();
		layoutData.horizontalIndent = 25;
		comboStubby.setLayoutData(layoutData);

		textStubby = new Text(grpSpec, SWT.BORDER | SWT.SINGLE);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
        textStubby.setLayoutData(layoutData);

		btnStubbyBrowse = new Button(grpSpec, SWT.PUSH);
		btnStubbyBrowse.setText(LocalFedoraPackagerText.LocalFedoraPackager_PageThree_btnStubbyBrowse);

		btnStubbyBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int comboIndex = comboStubby.getSelectionIndex();
				fileDialog(STUBBY[comboIndex], textStubby, LocalFedoraPackagerText.LocalFedoraPackager_IWizard_Stubby);
				if (textStubby.getText() != null) {
					setPageStatus(true, true);
				}
			}
		});

		btnCheckSrpm = new Button(grpSpec, SWT.RADIO);
		btnCheckSrpm.setText(LocalFedoraPackagerText.LocalFedoraPackager_PageThree_btnCheckSrpm);
		layoutData = new GridData();
		layoutData.horizontalSpan = 3;
		btnCheckSrpm.setLayoutData(layoutData);

		btnCheckSrpm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectControl();
				setPageStatus(false, false);
			}
		});

		lblSrpm = new Label(grpSpec, SWT.NONE);
		lblSrpm.setText(LocalFedoraPackagerText.LocalFedoraPackager_PageThree_lblSrpm);
		layoutData = new GridData();
		layoutData.horizontalIndent = 25;
		lblSrpm.setLayoutData(layoutData);

		textSrpm = new Text(grpSpec, SWT.BORDER | SWT.SINGLE);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
        textSrpm.setLayoutData(layoutData);

		btnSrpmBrowse = new Button(grpSpec, SWT.PUSH);
		btnSrpmBrowse.setText(LocalFedoraPackagerText.LocalFedoraPackager_PageThree_btnSrpmBrowse);

		btnSrpmBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fileDialog(SRPM, textSrpm, LocalFedoraPackagerText.LocalFedoraPackager_IWizard_SRpm);
				if (textSrpm.getText() != null) {
					setPageStatus(true, true);
				}
			}
		});

		btnCheckPlain = new Button(grpSpec, SWT.RADIO);
		btnCheckPlain.setText(LocalFedoraPackagerText.LocalFedoraPackager_PageThree_btnCheckPlain);
		layoutData = new GridData();
		layoutData.horizontalSpan = 3;
		btnCheckPlain.setLayoutData(layoutData);

		btnCheckPlain.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectControl();
				projectType = PLAIN;
				externalFile = null;
				setPageStatus(true, false);
			}
		});
		selectControl();
		setPageStatus(false, false);
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
	 * If Finish button can be enabled, return true
	 *
	 * @return pageCanFinish
	 */
	public boolean pageCanFinish() {
		return pageCanFinish;
	}

	/**
	 * Sets the status of page
	 *
	 * @param pageIsComplete, next or finish can be enabled
	 * @param pageCanFinish, finish can be enabled
	 */
	private void setPageStatus(boolean pageIsComplete, boolean pageCanFinish) {
		this.pageCanFinish = pageCanFinish;
		setPageComplete(pageIsComplete);
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
			textSrpm.setText("");
		}
		else if(btnCheckSrpm.getSelection()) {
		    lblSrpm.setEnabled(true);
		    textSrpm.setEnabled(true);
		    btnSrpmBrowse.setEnabled(true);
		    comboStubby.setEnabled(false);
		    textStubby.setEnabled(false);
		    btnStubbyBrowse.setEnabled(false);
			textStubby.setText("");
		}
		else {
		    comboStubby.setEnabled(false);
		    textStubby.setEnabled(false);
		    btnStubbyBrowse.setEnabled(false);
		    lblSrpm.setEnabled(false);
		    textSrpm.setEnabled(false);
		    btnSrpmBrowse.setEnabled(false);
			textStubby.setText("");
			textSrpm.setText("");
		}
	}

}
