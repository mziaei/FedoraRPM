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
package org.fedoraproject.eclipse.packager.api;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.part.ISetSelectionTarget;
/**
 * 
 * A simple dialog capable of rendering a link to a resource in the Project Explorer.
 *
 */
public class LinkedMessageDialog extends MessageDialog {

	private IResource resource;
	private String pseudoHTML;
	
	/**
	 * @param parentShell
	 *            The parent shell
	 * @param dialogTitle
	 *            The dialog title
	 * @param dialogMessagePseudoHtml
	 *            A message string of a form similar to:
	 * 
	 *            <pre>
	 * &lt;form&gt;&lt;p&gt;Some other text&lt;a&gt;Test Link&lt;/a&gt;&lt;/p&gt;&lt;/form&gt;
	 * </pre>
	 * 
	 *            In the above example {@code Test Link} would be rendered as a
	 *            link and when clicked would expand the resource as specified
	 *            by linkedResource in the Project Explorer.
	 * @param linkedResource
	 *            The resource which should be expanded when clicked on the
	 *            link.
	 * 
	 */
	public LinkedMessageDialog(Shell parentShell, String dialogTitle, String dialogMessagePseudoHtml, IResource linkedResource) {
		super(parentShell, dialogTitle, null, "", //$NON-NLS-1$
				MessageDialog.INFORMATION, new String[] { IDialogConstants.OK_LABEL }, 0);
		this.resource = linkedResource;
		this.pseudoHTML = dialogMessagePseudoHtml;
	}
	
	@Override
	protected Control createCustomArea(Composite parent) {
		FormText test = new FormText(parent, SWT.NONE);
		test.setText(pseudoHTML, true, true);
		final MessageDialog self = this;
		test.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				IViewPart view;
				try {
					view = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().showView(IPageLayout.ID_PROJECT_EXPLORER);
					if (view instanceof ISetSelectionTarget) {
						ISelection selection = new StructuredSelection(resource);
						((ISetSelectionTarget) view).selectReveal(selection);
					}
				} catch (PartInitException e) {
					e.printStackTrace();
				}
				self.close();
			}
		});
		return test;
	}

}
