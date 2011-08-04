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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.linuxtools.rpm.ui.editor.wizards.SpecfileNewWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.fedoraproject.eclipse.packager.fedorarpm.LocalFedoraPackagerText;
import org.fedoraproject.eclipse.packager.fedorarpm.FedorarpmPlugin;

public class FedoraRPMProjectPageFour extends SpecfileNewWizardPage {


	/**
	 * Create the wizard.
	 */
	public FedoraRPMProjectPageFour(String pageName, ISelection selection) {
		super(selection);
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
	}

}