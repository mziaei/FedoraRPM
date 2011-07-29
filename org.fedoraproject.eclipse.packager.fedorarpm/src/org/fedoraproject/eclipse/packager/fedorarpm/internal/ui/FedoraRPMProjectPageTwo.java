package org.fedoraproject.eclipse.packager.fedorarpm.internal.ui;

import java.net.URL;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.fedoraproject.eclipse.packager.fedorarpm.FedoraRPMText;
import org.fedoraproject.eclipse.packager.fedorarpm.FedorarpmPlugin;

public class FedoraRPMProjectPageTwo extends WizardPage {
	private Group grpAccount;
	private Button btnNewMaintainer;
	private Button btnExistingMaintainer;
	private Label lblTextFAS;
	private Link linkIntroduce;
	private Link linkInitial;
	private Link linkBugzilla;
	private Link linkFAS;
	private Text textFAS;

	/**
	 * Create the wizard.
	 */
	public FedoraRPMProjectPageTwo(String pageName) {
		super(pageName);
		setTitle(FedoraRPMText.FedoraRPMProject_title);
		setDescription(FedoraRPMText.FedoraRPMProject_description);
		FedorarpmPlugin
				.getImageDescriptor(FedoraRPMText.FedoraRPMProject_image);
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		grpAccount = new Group(container, SWT.NONE);
		grpAccount.setLayout(new GridLayout(3, false));
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		grpAccount.setLayoutData(layoutData);
		grpAccount
				.setText(FedoraRPMText.FedoraRPMProjectPageTwo_grpAccountSetup);

		btnExistingMaintainer = new Button(grpAccount, SWT.RADIO);
		btnExistingMaintainer
				.setText(FedoraRPMText.FedoraRPMProjectPageTwo_btnRadioExistMaintainer);
		layoutData = new GridData();
		layoutData.horizontalSpan = 3;
		btnExistingMaintainer.setLayoutData(layoutData);

		btnNewMaintainer = new Button(grpAccount, SWT.RADIO);
		btnNewMaintainer
				.setText(FedoraRPMText.FedoraRPMProjectPageTwo_btnRadioNewMaintainer);
		layoutData = new GridData();
		layoutData.horizontalSpan = 3;
		btnNewMaintainer.setLayoutData(layoutData);

		linkFAS = new Link(grpAccount, SWT.NONE);
		linkFAS.setText(FedoraRPMText.FedoraRPMProjectPageTwo_linkFAS);
		setLayout(linkFAS);

		lblTextFAS = new Label(grpAccount, SWT.NONE);
		lblTextFAS.setText(FedoraRPMText.FedoraRPMProjectPageTwo_lblTextFAS);
		layoutData = new GridData();
		layoutData.horizontalIndent = 45;
		lblTextFAS.setLayoutData(layoutData);
		textFAS = new Text(grpAccount, SWT.BORDER | SWT.SINGLE);
		layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		layoutData.widthHint = 100;
		textFAS.setLayoutData(layoutData);

		linkBugzilla = new Link(grpAccount, SWT.NONE);
		linkBugzilla
				.setText(FedoraRPMText.FedoraRPMProjectPageTwo_linkBugzilla);
		setLayout(linkBugzilla);

		linkInitial = new Link(grpAccount, SWT.NONE);
		linkInitial.setText(FedoraRPMText.FedoraRPMProjectPageTwo_linkInitial);
		setLayout(linkInitial);

		linkIntroduce = new Link(grpAccount, SWT.NONE);
		linkIntroduce
				.setText(FedoraRPMText.FedoraRPMProjectPageTwo_linkIntroduce);
		setLayout(linkIntroduce);

		addListener(linkFAS, FedoraRPMText.FedoraRPMProjectPageTwo_urlFAS);
		addListener(linkBugzilla,
				FedoraRPMText.FedoraRPMProjectPageTwo_urlBugzilla);
		addListener(linkInitial,
				FedoraRPMText.FedoraRPMProjectPageTwo_urlInitial);
		addListener(linkIntroduce,
				FedoraRPMText.FedoraRPMProjectPageTwo_urlIntroduce);

		btnNewMaintainer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectControl();
			}
		});

		btnExistingMaintainer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(checkPageComplete());
			}
		});

		textFAS.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setPageComplete(checkPageComplete());
			}
		});

		selectControl();
		setPageComplete(checkPageComplete());
		setControl(container);
	}

	private void addListener(Link link, final String url) {
		link.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				try {
					IWorkbenchBrowserSupport support = PlatformUI
							.getWorkbench().getBrowserSupport();
					support.getExternalBrowser().openURL(new URL(url));
				} catch (Exception e) {

				}
			}
		});
	}

	/**
	 * Sets the enabled properties based on the selected button
	 */
	protected void selectControl() {
		if (btnNewMaintainer.getSelection()) {
			linkBugzilla.setEnabled(true);
			linkFAS.setEnabled(true);
			linkInitial.setEnabled(true);
			linkIntroduce.setEnabled(true);
			textFAS.setEnabled(true);
			lblTextFAS.setEnabled(true);
		} else {
			linkBugzilla.setEnabled(false);
			linkFAS.setEnabled(false);
			linkInitial.setEnabled(false);
			linkIntroduce.setEnabled(false);
			textFAS.setEnabled(false);
			lblTextFAS.setEnabled(false);
		}
	}

	/**
	 * Sets the layout for Link widgets
	 * 
	 * @param Link
	 */
	private void setLayout(Link link) {
		GridData layout = new GridData();
		layout.horizontalIndent = 20;
		layout.horizontalSpan = 3;
		link.setLayoutData(layout);
	}

	/**
	 * Check if the page is ready to move to the next page
	 * 
	 * @return boolean
	 */
	private boolean checkPageComplete() {
		return (btnExistingMaintainer.getSelection())
				|| (textFAS.getText().length() > 0);
	}
}
