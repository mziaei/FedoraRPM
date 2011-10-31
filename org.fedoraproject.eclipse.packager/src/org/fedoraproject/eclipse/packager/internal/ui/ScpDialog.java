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
package org.fedoraproject.eclipse.packager.internal.ui;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 *
 */
public class ScpDialog extends TitleAreaDialog {

	CheckboxTableViewer filesViewer;

	/**
	 * @param parentShell
	 */
	public ScpDialog(Shell parentShell) {
		super(parentShell);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Control createContents(Composite parent) {
		// TODO Auto-generated method stub
		return super.createContents(parent);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
	    // Create new composite as container
	    final Composite area = new Composite(parent, SWT.NULL);
	    // We use a grid layout and set the size of the margins
	    final GridLayout gridLayout = new GridLayout();
	    gridLayout.marginWidth = 15;
	    gridLayout.marginHeight = 10;
	    area.setLayout(gridLayout);
	    // Now we create the list widget
	    List list = new List(area, SWT.BORDER | SWT.MULTI);
	    // We define a minimum width for the list
	    final GridData gridData = new GridData();
	    gridData.widthHint = 200;
	    list.setLayoutData(gridData);
//		Composite container = (Composite) super.createDialogArea(parent);
//		TableViewer viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
//				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
//		createColumns(parent, viewer);
//		final Table table = viewer.getTable();
//		table.setHeaderVisible(true);
//		table.setLinesVisible(true);
//
//		Table resourcesTable = toolkit.createTable(filesArea, SWT.H_SCROLL
//				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI | SWT.CHECK);
//		resourcesTable.setData(FormToolkit.KEY_DRAW_BORDER,
//				FormToolkit.TREE_BORDER);
//		resourcesTable.setLayoutData(GridDataFactory.fillDefaults()
//				.hint(600, 200).grab(true, true).create());
//
//		resourcesTable.addSelectionListener(new CommitItemSelectionListener());
//
//		resourcesTable.setHeaderVisible(true);
//		TableColumn statCol = new TableColumn(resourcesTable, SWT.LEFT);
//		statCol.setText(UIText.CommitDialog_Status);
//		statCol.setWidth(150);
//		statCol.addSelectionListener(new HeaderSelectionListener(
//				CommitItem.Order.ByStatus));
//
//		TableColumn resourceCol = new TableColumn(resourcesTable, SWT.LEFT);
//		resourceCol.setText(UIText.CommitDialog_Path);
//		resourceCol.setWidth(415);
//		resourceCol.addSelectionListener(new HeaderSelectionListener(
//				CommitItem.Order.ByFile));
//
//		filesViewer = new CheckboxTableViewer(resourcesTable);
//		new TableViewerColumn(filesViewer, statCol)
//				.setLabelProvider(new CommitStatusLabelProvider());
//		new TableViewerColumn(filesViewer, resourceCol)
//				.setLabelProvider(new CommitPathLabelProvider());
//		ColumnViewerToolTipSupport.enableFor(filesViewer);
//		filesViewer.setContentProvider(ArrayContentProvider.getInstance());
//		filesViewer.setUseHashlookup(true);
//		filesViewer.addFilter(new CommitItemFilter());
//		filesViewer.setInput(items.toArray());
//		filesViewer.getTable().setMenu(getContextMenu());
//		// TODO Auto-generated method stub
//		return super.createDialogArea(parent);
	}



}
