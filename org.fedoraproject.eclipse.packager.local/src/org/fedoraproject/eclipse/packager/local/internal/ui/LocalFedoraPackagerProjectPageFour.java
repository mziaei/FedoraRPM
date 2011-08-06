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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.linuxtools.rpm.ui.editor.wizards.Messages;
import org.eclipse.linuxtools.rpm.ui.editor.wizards.SpecfileNewWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.fedoraproject.eclipse.packager.local.LocalFedoraPackagerPlugin;
import org.fedoraproject.eclipse.packager.local.LocalFedoraPackagerText;

public class LocalFedoraPackagerProjectPageFour extends SpecfileNewWizardPage {
//	private IContainer container;


	/**
	 * Create the wizard.
	 */
	public LocalFedoraPackagerProjectPageFour(String pageName) {
		super(null);
		setTitle(LocalFedoraPackagerText.LocalFedoraPackager_title);
		setDescription(LocalFedoraPackagerText.LocalFedoraPackager_description);
		LocalFedoraPackagerPlugin
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


//	public void setProject(IWizardContainer container) {
////		initialize();
//	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */
//	private void initialize(container) {
////		if (selection != null && selection.isEmpty() == false
////				&& selection instanceof IStructuredSelection) {
////			IStructuredSelection ssel = (IStructuredSelection) selection;
////			if (ssel.size() > 1)
////				return;
////			Object obj = ssel.getFirstElement();
////			if (obj instanceof IResource) {
////				IContainer container;
//				if (container instanceof IContainer)
//					container = (IContainer)container;
//				else
//					container = ((IResource) obj).getParent();
//				projectText.setText(container.getFullPath().toString());
//			}
//		}
//		setDefaultValues();
//	}
//
//	/**
//	 * Ensures that both text fields are set.
//	 */
//	private void dialogChanged() {
//		IResource container = ResourcesPlugin.getWorkspace().getRoot()
//				.findMember(new Path(getProjectName()));
//		String fileName = getFileName();
////		if (getProjectName().length() == 0) {
////			updateStatus(Messages.SpecfileNewWizardPage_22);
////			return;
////		}
//		if (container == null
//				|| (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
//			updateStatus(Messages.SpecfileNewWizardPage_23);
//			return;
//		}
//		if (!container.isAccessible()) {
//			updateStatus(Messages.SpecfileNewWizardPage_24);
//			return;
//		}
//		if (fileName.length() == 0) {
//			updateStatus(Messages.SpecfileNewWizardPage_25);
//			return;
//		}
//
//		/*
//		 * Current RPM doc content (4.4.2): Names must not include whitespace
//		 * and may include a hyphen '-' (unlike version and releasetags). Names
//		 * should not include any numeric operators ('<', '>','=') as future
//		 * versions of rpm may need to reserve characters other than '-'.
//		 */
//		String packageName = super.getNameText().getText();
//		if (packageName.indexOf(" ") != -1 || packageName.indexOf("<") != -1 //$NON-NLS-1$ //$NON-NLS-2$
//				|| packageName.indexOf(">") != -1 || packageName.indexOf("=") != -1) { //$NON-NLS-1$ //$NON-NLS-2$
//			updateStatus(Messages.SpecfileNewWizardPage_26
//					+ Messages.SpecfileNewWizardPage_27);
//			return;
//		}
//		super.setNameText(ResourcesPlugin.getWorkspace().toString());
//
////		if (versionText.getText().indexOf("-") > -1) { //$NON-NLS-1$
////			updateStatus(Messages.SpecfileNewWizardPage_28);
////			return;
////		}
//
//		updateStatus(null);
//	}
//	private void updateStatus(String message) {
//		setErrorMessage(message);
//		setPageComplete(message == null);
//	}

}