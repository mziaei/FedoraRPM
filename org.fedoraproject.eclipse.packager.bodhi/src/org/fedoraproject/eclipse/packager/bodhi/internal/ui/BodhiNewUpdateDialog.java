package org.fedoraproject.eclipse.packager.bodhi.internal.ui;
import java.util.Arrays;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.fedoraproject.eclipse.packager.bodhi.api.PushUpdateCommand;
import org.fedoraproject.eclipse.packager.bodhi.api.PushUpdateCommand.RequestType;
import org.fedoraproject.eclipse.packager.bodhi.api.PushUpdateCommand.UpdateType;


/**
 * UI dialog corresponding to the Web form of:
 * https://admin.fedoraproject.org/updates/new/
 */
public class BodhiNewUpdateDialog extends AbstractBodhiDialog {

	protected int result;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Text txtComment;
	private Text txtBugs;
	private Text txtStableKarmaThreshold;
	private Text txtUnstableKarmaThreshold;
	private Button btnEnableKarmaAutomatism;
	private Button btnSuggestReboot;
	private Combo comboType;
	private Combo comboRequest;
	private List listBuilds;
	private Button btnCloseBugs;
	// Data fields in order to be able to pre-fill some fields
	private String[] buildsData;
	private String bugsData;
	private String commentData;
	private RequestType requestTypeData;
	private UpdateType updateTypeData;
	private boolean suggestRebootData;
	private boolean closeBugsData;
	private boolean enableKarmaAutomatismData;
	private int stableKarmaThresholdData;
	private int unstableKarmaThresholdData;

	/**
	 * Create the dialog and pre-fill it with some data
	 * 
	 * @param parent The parent shell
	 * @param builds The initial list of builds
	 * @param bugs The comma/space separated list of builds
	 * @param comment The update comment (i.e. notice)
	 */
	public BodhiNewUpdateDialog(Shell parent, String[] builds, String bugs, String comment) {
		super(parent, SWT.MODELESS);
		setText("Create New Bodhi Update");
		this.buildsData = builds;
		this.bugsData = bugs;
		this.commentData = comment;
	}

	/**
	 * Open the dialog.
	 * @return the result. Either Window.OK or Window.Cancel
	 */
	public int open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		// result set in handleForm or performCancel
		return result;
	}
	
	/**
	 * @return The list of builds, which were selected.
	 */
	public String[] getBuilds() {
		// since we require at least one build to be selected,
		// this should always return a non-empty list
		return this.buildsData;
	}
	
	private void setBuilds() {
		this.buildsData = listBuilds.getSelection();
	}
	
	/**
	 * @return A comma separated list of bugs, et. al.
	 */
	public String getBugs() {
		// since we do validation on this field it should contain
		// a reasonable string at this point.
		return this.bugsData;
	}
	
	private void setBugs() {
		this.bugsData = txtBugs.getText();
	}

	/**
	 * 
	 * @return The update comment/note.
	 */
	public String getComment() {
		return this.commentData;
	}
	
	private void setComment() {
		this.commentData = txtComment.getText();
	}
	
	/**
	 * 
	 * @return The suggest reboot selection.
	 */
	public boolean isSuggestReboot() {
		return this.suggestRebootData;
	}
	
	private void setSuggestReboot() {
		this.suggestRebootData = btnSuggestReboot.getSelection();
	}
	
	/**
	 * 
	 * @return The close bugs selection.
	 */
	public boolean isCloseBugs() {
		return this.closeBugsData;
	}
	
	private void setCloseBugs() {
		this.closeBugsData = btnCloseBugs.getSelection();
	}
	
	/**
	 * 
	 * @return The karma automatism selection.
	 */
	public boolean isKarmaAutomatismEnabled() {
		return this.enableKarmaAutomatismData;
	}
	
	private void setKarmaAutomatismEnabled() {
		this.enableKarmaAutomatismData = btnEnableKarmaAutomatism.getSelection();
	}
	
	/**
	 * 
	 * @return The selected request type.
	 */
	public RequestType getRequestType() {
		return this.requestTypeData;
	}
	
	private void setRequestType() {
		this.requestTypeData = null;
	}
	
	/**
	 * 
	 * @return The selected update type.
	 */
	public UpdateType getUpdateType() {
		return this.updateTypeData;
	}
	
	private void setUpdateType() {
		this.updateTypeData = null;
	}
	
	/**
	 * 
	 * @return The entered stable karma threshold.
	 */
	public int getStableKarmaThreshold() {
		return this.stableKarmaThresholdData;
	}
	
	private void setStableKarmaThreshold() {
		// since we attempted to parse an integer from
		// the string while validating it, this should
		// not throw a NumberFormatException
		this.stableKarmaThresholdData = Integer.parseInt(txtStableKarmaThreshold.getText());
	}
	
	/**
	 * 
	 * @return The entered unstable karma threshold.
	 */
	public int getUnstableKarmaThreshold() {
		return this.unstableKarmaThresholdData;
	}
	
	private void setUnstableKarmaThreshold() {
		// since we attempted to parse an integer from
		// the string while validating it, this should
		// not throw a NumberFormatException
		this.unstableKarmaThresholdData = Integer.parseInt(txtUnstableKarmaThreshold.getText());
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM);
		shell.setSize(686, 655);
		shell.setText("Create Bew Bodhi Update");
		shell.setLayout(null);
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setBackground(getColor(SWT.COLOR_WHITE));
		scrolledComposite.setBounds(0, 0, 683, 682);
		formToolkit.adapt(scrolledComposite);
		formToolkit.paintBordersFor(scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		Composite composite = new Composite(scrolledComposite, SWT.NONE);
		composite.setBackground(getColor(SWT.COLOR_WHITE));
		formToolkit.adapt(composite);
		formToolkit.paintBordersFor(composite);
		
		Composite labelComposite = new Composite(composite, SWT.NONE);
		labelComposite.setBackground(getColor(SWT.COLOR_WHITE));
		labelComposite.setBounds(10, 10, 206, 601);
		formToolkit.adapt(labelComposite);
		formToolkit.paintBordersFor(labelComposite);
		labelComposite.setLayout(null);
		
		Label lblPackage = new Label(labelComposite, SWT.RIGHT);
		lblPackage.setBounds(131, 40, 70, 21);
		lblPackage.setBackground(getColor(SWT.COLOR_WHITE));
		formToolkit.adapt(lblPackage, true, true);
		lblPackage.setText("Package");
		
		Label lblType = new Label(labelComposite, SWT.RIGHT);
		lblType.setBounds(131, 122, 70, 21);
		lblType.setBackground(getColor(SWT.COLOR_WHITE));
		formToolkit.adapt(lblType, true, true);
		lblType.setText("Type");
		
		Label lblRequest = new Label(labelComposite, SWT.RIGHT);
		lblRequest.setBounds(131, 159, 70, 21);
		lblRequest.setBackground(getColor(SWT.COLOR_WHITE));
		formToolkit.adapt(lblRequest, true, true);
		lblRequest.setText("Request");
		
		Label lblBugs = new Label(labelComposite, SWT.RIGHT);
		lblBugs.setBounds(131, 199, 70, 21);
		lblBugs.setBackground(getColor(SWT.COLOR_WHITE));
		formToolkit.adapt(lblBugs, true, true);
		lblBugs.setText("Bugs");
		
		Label lblNotes = new Label(labelComposite, SWT.SHADOW_NONE | SWT.RIGHT);
		lblNotes.setBounds(131, 243, 70, 21);
		lblNotes.setBackground(getColor(SWT.COLOR_WHITE));
		formToolkit.adapt(lblNotes, true, true);
		lblNotes.setText("Notes");
		
		Label lblStableKarma = new Label(labelComposite, SWT.WRAP | SWT.RIGHT);
		lblStableKarma.setBackground(getColor(SWT.COLOR_WHITE));
		lblStableKarma.setBounds(57, 462, 149, 44);
		formToolkit.adapt(lblStableKarma, true, true);
		lblStableKarma.setText("Threshold for pushing to stable");
		
		Label lblUnstableKarma = new Label(labelComposite, SWT.WRAP | SWT.RIGHT);
		lblUnstableKarma.setBounds(113, 513, 93, 41);
		formToolkit.adapt(lblUnstableKarma, true, true);
		lblUnstableKarma.setText("Threshold for unpushing");
		
		Composite valuesComposite = new Composite(composite, SWT.NONE);
		valuesComposite.setBackground(getColor(SWT.COLOR_WHITE));
		valuesComposite.setBounds(222, 10, 459, 601);
		formToolkit.adapt(valuesComposite);
		formToolkit.paintBordersFor(valuesComposite);
		valuesComposite.setLayout(null);
		
		btnEnableKarmaAutomatism = new Button(valuesComposite, SWT.CHECK);
		btnEnableKarmaAutomatism.setToolTipText("Enable update request automation based on user feedback");
		btnEnableKarmaAutomatism.setSelection(PushUpdateCommand.DEFAULT_KARMA_AUTOMATISM);
		btnEnableKarmaAutomatism.setBounds(7, 432, 196, 25);
		formToolkit.adapt(btnEnableKarmaAutomatism, true, true);
		btnEnableKarmaAutomatism.setText("Enable Karma Automatism");
		
		btnSuggestReboot = new Button(valuesComposite, SWT.CHECK);
		btnSuggestReboot.setToolTipText("Recommend that the user restarts the machine after installing this update");
		btnSuggestReboot.setBounds(7, 403, 166, 25);
		btnSuggestReboot.setSelection(PushUpdateCommand.DEFAULT_SUGGEST_REBOOT);
		formToolkit.adapt(btnSuggestReboot, true, true);
		btnSuggestReboot.setText("Suggest Reboot");
		
		txtComment = new Text(valuesComposite, SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		txtComment.setToolTipText("Some details about this update that will appear in the notice. Example: This is an update that fixes problems with **connecting to a share**.The following things *break*:*Browsing with `gnome-app-install`*Emailing");
		txtComment.setText(this.commentData);
		txtComment.setBounds(10, 243, 439, 152);
		formToolkit.adapt(txtComment, true, true);
		
		txtBugs = new Text(valuesComposite, SWT.NONE);
		txtBugs.setToolTipText("A comma or space separated list of bugs or aliases. For example: #1234, 789 CVE-2008-0001");
		txtBugs.setText(this.bugsData);
		txtBugs.setBounds(10, 197, 193, 31);
		formToolkit.adapt(txtBugs, true, true);
		
		comboType = new Combo(valuesComposite, SWT.READ_ONLY);
		comboType.setItems(new String[] {"bugfix", "enhancement", "security", "newpackage"});
		comboType.setBounds(9, 116, 196, 33);
		formToolkit.adapt(comboType);
		formToolkit.paintBordersFor(comboType);
		
		comboRequest = new Combo(valuesComposite, SWT.READ_ONLY);
		comboRequest.setItems(new String[] {"Testing", "Stable", "None"});
		comboRequest.setBounds(9, 155, 196, 33);
		formToolkit.adapt(comboRequest);
		formToolkit.paintBordersFor(comboRequest);
		
		txtStableKarmaThreshold = new Text(valuesComposite, SWT.NONE);
		txtStableKarmaThreshold.setToolTipText("The threshold for automatically pushing this update to stable");
		txtStableKarmaThreshold.setText(String.valueOf(PushUpdateCommand.DEFAULT_STABLE_KARMA_THRESHOLD));
		txtStableKarmaThreshold.setBounds(10, 470, 40, 31);
		formToolkit.adapt(txtStableKarmaThreshold, true, true);
		
		txtUnstableKarmaThreshold = new Text(valuesComposite, SWT.NONE);
		txtUnstableKarmaThreshold.setToolTipText("The threshold for automatically unpushing an unstable update ");
		txtUnstableKarmaThreshold.setText(String.valueOf(PushUpdateCommand.DEFAULT_UNSTABLE_KARMA_THRESHOLD));
		txtUnstableKarmaThreshold.setBounds(10, 522, 40, 31);
		formToolkit.adapt(txtUnstableKarmaThreshold, true, true);
		
		listBuilds = new List(valuesComposite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		listBuilds.setToolTipText("Select the list of builds you'd like to push as an update. Use CTRL+Click to select multiple builds.");
		listBuilds.setItems(this.buildsData);
		listBuilds.setBounds(10, 40, 439, 70);
		formToolkit.adapt(listBuilds, true, true);
		
		Button btnAddBuild = new Button(valuesComposite, SWT.NONE);
		btnAddBuild.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.CR) {
					AddNewBuildDialog newBuildDialog = new AddNewBuildDialog(shell);
					if (newBuildDialog.open() == Window.OK) {
						mergeAndUpdateBuilds(newBuildDialog.getBuilds());
					}
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// ignore, event doesn't seem to be fired
			}
		});
		btnAddBuild.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				// nothing
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				AddNewBuildDialog newBuildDialog = new AddNewBuildDialog(shell);
				if (newBuildDialog.open() == Window.OK) {
					mergeAndUpdateBuilds(newBuildDialog.getBuilds());
				}
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// nothing
			}
		});
		btnAddBuild.setToolTipText("Add another build to the list of builds above.");
		btnAddBuild.setBounds(375, 116, 74, 33);
		formToolkit.adapt(btnAddBuild, true, true);
		btnAddBuild.setText("Add Build");
		
		btnCloseBugs = new Button(valuesComposite, SWT.CHECK);
		btnCloseBugs.setToolTipText("Automatically close bugs when this update is pushed as stable");
		btnCloseBugs.setSelection(true);
		btnCloseBugs.setBounds(209, 200, 242, 25);
		formToolkit.adapt(btnCloseBugs, true, true);
		btnCloseBugs.setText("Close bugs when update is stable");
		
		Button btnSaveUpdate = new Button(valuesComposite, SWT.NONE);
		btnSaveUpdate.setBounds(9, 568, 94, 33);
		btnSaveUpdate.forceFocus();
		btnSaveUpdate.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				// nothing
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				// handle form input
				handleFormInput();
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// nothing
			}
		});
		btnSaveUpdate.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				// handle form input if return key is pressed on the
				// save update button
				if (e.character == SWT.CR) {
					handleFormInput();
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// nothing, this event does not seem to get triggered
			}
		});
		formToolkit.adapt(btnSaveUpdate, true, true);
		btnSaveUpdate.setText("Save Update");
		
		Button btnCancel = new Button(valuesComposite, SWT.NONE);
		btnCancel.setBounds(109, 568, 64, 33);
		btnCancel.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				// nothing
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				performCancel();
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// nothing
			}
		});
		btnCancel.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				// Cancel only if return key is pressed on the cancel button
				if (e.character == SWT.CR) {
					performCancel();
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// nothing, this event does not seem to get triggered
			}
		});
		formToolkit.adapt(btnCancel, true, true);
		btnCancel.setText("Cancel");
		
		lblError = new Label(valuesComposite, SWT.NONE);
		lblError.setForeground(getColor(SWT.COLOR_RED));
		lblError.setBounds(10, 10, 439, 21);
		formToolkit.adapt(lblError, true, true);
		// set the tab order when tabbing through controls using the
		// keyboard
		valuesComposite.setTabList(new Control[] { listBuilds, btnAddBuild,
				comboType, comboRequest, txtBugs, btnCloseBugs, txtComment,
				btnSuggestReboot, btnEnableKarmaAutomatism,
				txtStableKarmaThreshold, txtUnstableKarmaThreshold,
				btnSaveUpdate, btnCancel });
		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}

	/*
	 * (non-Javadoc)
	 * @see org.fedoraproject.eclipse.packager.bodhi.internal.ui.AbstractBodhiDialog#validateForm()
	 */
	@Override
	protected boolean validateForm() {
		String bugs = txtBugs.getText();
		// need to have at least one build selected
		if (listBuilds.getSelectionCount() == 0) {
			setValidationError("At least one build needs to be selected");
			return false;
		}
		if (!bugs.equals("") && !bugs.matches("[0-9]+(,[0-9]+)*")) { //$NON-NLS-1$ //$NON-NLS-2$
			setValidationError("Invalid bug list format");
			return false;
		}
		// require stable karma to be a reasonable number
		try {
			Integer.parseInt(txtStableKarmaThreshold.getText());
		} catch (NumberFormatException e) {
			setValidationError("Stable karma threshold must be a number");
			return false;
		}
		// require unstable karma to be a reasonable number
		try {
			Integer.parseInt(txtUnstableKarmaThreshold.getText());
		} catch (NumberFormatException e) {
			setValidationError("Unstable karma threshold must be a number");
			return false;
		}
		// values seem to be good, set data fields
		setDataFields();
		return true;
	}
	
	/**
	 * Set all data fields from UI widgets.
	 * 
	 * pre: validation passed
	 * post: public getters are functional
	 */
	private void setDataFields() {
		setBugs();
		setBuilds();
		setCloseBugs();
		setComment();
		setKarmaAutomatismEnabled();
		setRequestType();
		setStableKarmaThreshold();
		setSuggestReboot();
		setUnstableKarmaThreshold();
		setUpdateType();
	}
	
	/**
	 * Update builds list after AddNewBuildDialog closed.
	 * 
	 * @param additionalBuilds
	 */
	private void mergeAndUpdateBuilds(String[] additionalBuilds) {
		String[] newBuilds = Arrays.copyOf(listBuilds.getItems(),
				listBuilds.getItems().length + additionalBuilds.length);
		for (int i = listBuilds.getItems().length, j = 0; i < listBuilds
				.getItems().length + additionalBuilds.length; i++, j++) {
			newBuilds[i] = additionalBuilds[j];
		}
		listBuilds.setItems(newBuilds);
		listBuilds.redraw();
	}
}