package org.fedoraproject.eclipse.packager.fedorarpm.wizards;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
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

public class FedoraRPMProjectPageTwo extends WizardPage {
	private Group grpAccount;
	private Button btnNewMaintainer;
	private Button btnExistingMaintainer;
	private Label lblTextFAS;
	private Label lblNoteGit;
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
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		grpAccount = new Group(container, SWT.NONE);
		grpAccount.setLayout(new GridLayout(3, false));
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		grpAccount.setLayoutData(layoutData);
		grpAccount.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_grpAccountSetup);

		btnExistingMaintainer = new Button(grpAccount, SWT.RADIO);
		btnExistingMaintainer.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_btnRadioExistMaintainer);
		layoutData = new GridData();
		layoutData.horizontalSpan = 3;
		btnExistingMaintainer.setLayoutData(layoutData);

		btnNewMaintainer = new Button(grpAccount, SWT.RADIO);
		btnNewMaintainer.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_btnRadioNewMaintainer);
		layoutData = new GridData();
		layoutData.horizontalSpan = 3;
		btnNewMaintainer.setLayoutData(layoutData);

        linkFAS = new Link(grpAccount, SWT.NONE);
        linkFAS.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_linkFAS);
        setLayout(linkFAS);

		lblTextFAS = new Label(grpAccount, SWT.NONE);
		lblTextFAS.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_lblTextFAS);
        layoutData = new GridData();
        layoutData.horizontalIndent = 45;
        lblTextFAS.setLayoutData(layoutData);
        textFAS = new Text(grpAccount, SWT.BORDER | SWT.SINGLE);
        layoutData = new GridData();
        layoutData.horizontalSpan = 2;
        layoutData.widthHint = 100;
        textFAS.setLayoutData(layoutData);


		linkBugzilla = new Link(grpAccount, SWT.NONE);
		linkBugzilla.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_linkBugzilla);
        setLayout(linkBugzilla);

		linkInitial = new Link(grpAccount, SWT.NONE);
		linkInitial.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_linkInitial);
        setLayout(linkInitial);

		linkIntroduce = new Link(grpAccount, SWT.NONE);
		linkIntroduce.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_linkIntroduce);
        setLayout(linkIntroduce);

		lblNoteGit = new Label(grpAccount, SWT.NONE);
		lblNoteGit.setText(FedoraRPMMessages.FedoraRPMProjectPageTwo_lblNoteGit);
        layoutData = new GridData();
        layoutData.horizontalIndent = 20;
        layoutData.horizontalSpan = 3;
        lblNoteGit.setLayoutData(layoutData);

        addListener(linkFAS, FedoraRPMMessages.FedoraRPMProjectPageTwo_urlFAS);
        addListener(linkBugzilla, FedoraRPMMessages.FedoraRPMProjectPageTwo_urlBugzilla);
        addListener(linkInitial, FedoraRPMMessages.FedoraRPMProjectPageTwo_urlInitial);
        addListener(linkIntroduce, FedoraRPMMessages.FedoraRPMProjectPageTwo_urlIntroduce);
        		
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
			      IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
			      support.getExternalBrowser().openURL(new URL(url));
			    } catch (Exception e) {
	
			    }          
			  }
	      });
//      GridDataFactory.fillDefaults().grab(true, false).hint(100, SWT.DEFAULT).applyTo(moreInfoLink);
	}

	protected void selectControl() {
		if(btnNewMaintainer.getSelection()){
		    linkBugzilla.setEnabled(true);
		    linkFAS.setEnabled(true);
		    linkInitial.setEnabled(true);
		    linkIntroduce.setEnabled(true);
		    lblNoteGit.setEnabled(true);
		    textFAS.setEnabled(true);
		    lblTextFAS.setEnabled(true);			
		}
		else {
		    linkBugzilla.setEnabled(false);
		    linkFAS.setEnabled(false);
		    linkInitial.setEnabled(false);
		    linkIntroduce.setEnabled(false);
		    lblNoteGit.setEnabled(false);
		    textFAS.setEnabled(false);
		    lblTextFAS.setEnabled(false);
		}
	}

	private void setLayout(Link link) {
        GridData layout = new GridData();
        layout.horizontalIndent = 20;
        layout.horizontalSpan = 3;
        link.setLayoutData(layout);
	}
	
	private boolean checkPageComplete() {
    	return (btnExistingMaintainer.getSelection()) || (textFAS.getText().length() > 0);
	}
}


