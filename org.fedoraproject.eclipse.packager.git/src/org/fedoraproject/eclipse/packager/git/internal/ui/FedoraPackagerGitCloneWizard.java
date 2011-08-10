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
package org.fedoraproject.eclipse.packager.git.internal.ui;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.egit.core.op.ConnectProviderOperation;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.errors.NoRemoteRepositoryException;
import org.eclipse.jgit.errors.NotSupportedException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.fedoraproject.eclipse.packager.FedoraSSL;
import org.fedoraproject.eclipse.packager.FedoraSSLFactory;
import org.fedoraproject.eclipse.packager.PackagerPlugin;
import org.fedoraproject.eclipse.packager.QuestionMessageDialog;
import org.fedoraproject.eclipse.packager.git.Activator;
import org.fedoraproject.eclipse.packager.git.FedoraPackagerGitCloneOperation;
import org.fedoraproject.eclipse.packager.git.FedoraPackagerGitText;
import org.fedoraproject.eclipse.packager.git.GitPreferencesConstants;
import org.fedoraproject.eclipse.packager.git.GitUtils;


/**
 * Wizard to checkout package content from Fedora Git.
 *
 */
public class FedoraPackagerGitCloneWizard extends Wizard implements IImportWizard {

	private SelectModulePage page;
	private IStructuredSelection selection;
	private String fasUserName;

	/**
	 * Creates the wizards and sets that it needs progress monitor.
	 */
	public FedoraPackagerGitCloneWizard() {
		super();
		// Set title of wizard window
		setWindowTitle(FedoraPackagerGitText.FedoraPackagerGitCloneWizard_wizardTitle);
		// required to show progress info of clone job
		setNeedsProgressMonitor(true);
		// retrieve FAS username
		this.fasUserName = FedoraSSLFactory.getInstance().getUsernameFromCert();
	}

	@Override
	public void addPages() {
		// get Fedora username from cert
		page = new SelectModulePage(fasUserName);
		addPage(page);
		page.init(selection);
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
	
	@Override
	public boolean performFinish() {
		try {
			IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
			// Bail out if project already exists
			IResource project = wsRoot.findMember(new Path(page.getPackageName()));
			if (project != null && project.exists()) {
				final String confirmOverwriteProjectMessage = NLS
						.bind(FedoraPackagerGitText.FedoraPackagerGitCloneWizard_confirmOverwirteProjectExists,
								project.getName());
				if (!confirmOverwriteQuestion(confirmOverwriteProjectMessage)) {
					return performCancel();
				} else {
					// delete project
					project.delete(true, null);
				}
			}
			// Make sure to be created directory does not exist or is
			// empty
			File newDir = new File(wsRoot.getLocation().toOSString() +
			IPath.SEPARATOR + page.getPackageName() );
			if (newDir.exists() && newDir.isDirectory()) {
				String contents[] = newDir.list();
				if (contents.length != 0) {
					// ask for confirmation before we overwrite
					final String confirmOverwriteQuestion = NLS
							.bind(FedoraPackagerGitText.FedoraPackagerGitCloneWizard_filesystemResourceExistsQuestion,
									page.getPackageName());
					if (!confirmOverwriteQuestion(confirmOverwriteQuestion)) {
						// bail
						return performCancel();
					}
				}
			}

			// prepare the clone op
			final FedoraPackagerGitCloneOperation cloneOp = new FedoraPackagerGitCloneOperation();
			cloneOp.setCloneURI(getGitCloneURL()).setPackageName(page.getPackageName());
			// Make sure we report a nice error if repo not found
			try {
				// Perform clone in ModalContext thread with progress
				// reporting on the wizard.
				getContainer().run(true, true, new IRunnableWithProgress() {
					@Override
					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						try {
							cloneOp.run(monitor);
						} catch (IOException e) {
							throw new InvocationTargetException(e);
						} catch (IllegalStateException e) {
							throw new InvocationTargetException(e);
						} catch (CoreException e) {
							throw new InvocationTargetException(e);
						}
						if (monitor.isCanceled())
							throw new InterruptedException();
					}
				});
			} catch (InvocationTargetException e) {
				// if repo wasn't found make this apparent
				if (e.getTargetException().getCause() instanceof NoRemoteRepositoryException || 
						e.getTargetException().getCause() instanceof InvalidRemoteException) {
					// Refuse to clone, give user a chance to correct
					final String errorMessage = NLS
							.bind(FedoraPackagerGitText.FedoraPackagerGitCloneWizard_repositoryNotFound,
									page.getPackageName());
					cloneFailChecked(errorMessage);
					return false; // let user correct
				} else if (e.getTargetException().getCause().getCause() != null && 
							e.getTargetException().getCause().getCause().getMessage() == "Auth fail"){ //$NON-NLS-1$
					cloneFailChecked(FedoraPackagerGitText.FedoraPackagerGitCloneWizard_authFail);
					return false;
				// Caused by: org.eclipse.jgit.errors.NotSupportedException: URI not supported: ssh:///jeraal@alkldal.test.comeclipse-callgraph.git
				} else if (e.getTargetException().getCause() instanceof NotSupportedException || 
							e.getTargetException().getCause() instanceof TransportException) {
					final String errorMessage = NLS
					.bind(FedoraPackagerGitText.FedoraPackagerGitCloneWizard_badURIError,
							Activator.getStringPreference(GitPreferencesConstants.PREF_CLONE_BASE_URL));
					cloneFailChecked(errorMessage);
					return false; // let user correct
				}
				throw e;
			}
			IProject newProject = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(page.getPackageName());
			newProject.create(null);
			newProject.open(null);
			// Set persistent property so that we know when to show the context
			// menu item.
			newProject.setPersistentProperty(PackagerPlugin.PROJECT_PROP,
					"true" /* unused value */); //$NON-NLS-1$
			ConnectProviderOperation connect = new ConnectProviderOperation(
					newProject);
			connect.execute(null);

			// Add new project to working sets, if requested
			IWorkingSet[] workingSets = page.getWorkingSets();
			if (workingSets.length > 0) {
				PlatformUI.getWorkbench().getWorkingSetManager().addToWorkingSets(newProject, workingSets);
			}

			// Finally ask if the Fedora Packaging perspective should be opened
			// if not already open.
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			IPerspectiveDescriptor perspective = window.getActivePage().getPerspective();
			if (!perspective.getId().equals(PackagerPlugin.FEDORA_PACKAGING_PERSPECTIVE_ID)) {
				if (shouldOpenPerspective()) {
					// open the perspective
					workbench.showPerspective(PackagerPlugin.FEDORA_PACKAGING_PERSPECTIVE_ID, window);
				}
			}
			return true;
		} catch (InterruptedException e) {
			MessageDialog.openInformation(getShell(), FedoraPackagerGitText.FedoraPackagerGitCloneWizard_cloneFail, 
					FedoraPackagerGitText.FedoraPackagerGitCloneWizard_cloneCancel);
			return false;
		} catch (Exception e) {
			org.fedoraproject.eclipse.packager.git.Activator.handleError(
					FedoraPackagerGitText.FedoraPackagerGitCloneWizard_cloneFail, e, true);
			return false;
		}
	}
	
	/**
	 * Prompt for confirmation if a resource exists, either the project already exists,
	 * or a folder exists in the workspace and would conflict with the newly created project.
	 * 
	 * @param errorMessage
	 * @return {@code true} if the user confirmed, {@code false} otherwise.
	 */
	private boolean confirmOverwriteQuestion(String errorMessage) {
		QuestionMessageDialog op = new QuestionMessageDialog(FedoraPackagerGitText.FedoraPackagerGitCloneWizard_confirmDialogTitle,
				errorMessage,
				getShell());
		Display.getDefault().syncExec(op);
		return op.isOkPressed();
	}

	/**
	 * Opens error dialog with provided reason in error message.
	 * 
	 * @param errorMsg The error message to use.
	 */
	private void cloneFailChecked(String errorMsg) {
		ErrorDialog
		.openError(
				getShell(),
				getWindowTitle() + FedoraPackagerGitText.FedoraPackagerGitCloneWizard_problem,
				FedoraPackagerGitText.FedoraPackagerGitCloneWizard_cloneFail,
				new Status(
						IStatus.ERROR,
						org.fedoraproject.eclipse.packager.git.Activator.PLUGIN_ID,
						0, errorMsg, null));
	}

	/**
	 * Determine the Git clone URL in the following order:
	 * <ol>
	 * <li>Use the Git base URL as set by the preference (if any) or</li>
	 * <li>Check if ~/.fedora.cert is present, and if so retrieve the user name
	 * from it.</li>
	 * <li>If all else fails, or anonymous checkout is specified,
	 * construct an anonymous clone URL</li>
	 * </ol>
	 * 
	 * @return The full clone URL based on the package name.
	 */
	private String getGitCloneURL() {
		String gitBaseURL = Activator
				.getStringPreference(GitPreferencesConstants.PREF_CLONE_BASE_URL);
		if (gitBaseURL != null && !page.getCloneAnonymousButtonChecked()) {
			return GitUtils.getFullGitURL(gitBaseURL, page.getPackageName());
		} else if (!fasUserName.equals(FedoraSSL.UNKNOWN_USER) && !page.getCloneAnonymousButtonChecked()) {
			return GitUtils.getFullGitURL(
					GitUtils.getAuthenticatedGitBaseUrl(fasUserName),
					page.getPackageName());
		} else {
			// anonymous
			return GitUtils.getFullGitURL(GitUtils.getAnonymousGitBaseUrl(),
					page.getPackageName());
		}
	}
	
	/**
	 * Ask if Fedora Packager perspective should be opened.
	 */
	private boolean shouldOpenPerspective() {
		QuestionMessageDialog op = new QuestionMessageDialog(
				FedoraPackagerGitText.FedoraPackagerGitCloneWizard_switchPerspectiveQuestionTitle,
				FedoraPackagerGitText.FedoraPackagerGitCloneWizard_switchPerspectiveQuestionMsg,
				getShell());
		Display.getDefault().syncExec(op);
		return op.isOkPressed();
	}
}
