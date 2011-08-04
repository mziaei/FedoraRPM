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
package org.fedoraproject.eclipse.packager.fedorarpm.internal.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.fedoraproject.eclipse.packager.fedorarpm.LocalFedoraPackagerText;
import org.fedoraproject.eclipse.packager.fedorarpm.FedorarpmPlugin;

public class FedoraRPMProjectPageOne extends WizardNewProjectCreationPage {
	private Label lblNoteGit;

	/**
	 * Create the wizard.
	 */
	public FedoraRPMProjectPageOne(String pageName) {
		super(pageName);
		setTitle(LocalFedoraPackagerText.LocalFedoraPackager_title);
		setDescription(LocalFedoraPackagerText.LocalFedoraPackager_description);
		FedorarpmPlugin
				.getImageDescriptor(LocalFedoraPackagerText.LocalFedoraPackager_image);
	}

	/**
	 * Create contents of the wizard.
	 *
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		Composite container = (Composite) getControl();

		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		lblNoteGit = new Label(container, SWT.NONE);
		lblNoteGit.setText(LocalFedoraPackagerText.LocalFedoraPackager_PageOne_lblNoteGit);
		lblNoteGit.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_BLUE));
		// GridData layoutData = new GridData(); // puts the git note on the
		// left
		GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		lblNoteGit.setLayoutData(layoutData);

		setControl(container);
	}
}
