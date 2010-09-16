/*******************************************************************************
 * Copyright (c) 2010 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat Inc. - initial API and implementation
 *******************************************************************************/
package org.fedoraproject.eclipse.packager.bodhi;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.fedoraproject.eclipse.packager.FedoraProjectRoot;
import org.fedoraproject.eclipse.packager.IFpProjectBits;
import org.fedoraproject.eclipse.packager.handlers.CommonHandler;
import org.fedoraproject.eclipse.packager.handlers.FedoraHandlerUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Handler for pushing Bodhi updates.
 */
public class BodhiNewHandler extends CommonHandler {

	protected IBodhiNewDialog dialog;
	protected IUserValidationDialog authDialog;
	protected IBodhiClient bodhi;

	
	@Override
	public Object execute(final ExecutionEvent e) throws ExecutionException {
		final FedoraProjectRoot fedoraProjectRoot = FedoraHandlerUtils
				.getValidRoot(e);
		final IFpProjectBits projectBits = FedoraHandlerUtils.getVcsHandler(fedoraProjectRoot);
		Job job = new Job(Messages.bodhiNewHandler_jobName) { //$NON-NLS-1$
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask(Messages.bodhiNewHandler_createUpdateMsg, 
						IProgressMonitor.UNKNOWN);
				monitor.subTask(Messages.bodhiNewHandler_checkTagMsg);
				try {
					String tag = FedoraHandlerUtils.makeTagName(fedoraProjectRoot);
					String branchName = projectBits.getCurrentBranchName();

					// ensure branch is tagged properly before proceeding
					if (!projectBits.needsTag() || projectBits.isVcsTagged(fedoraProjectRoot, tag)) {
						if (monitor.isCanceled()) {
							throw new OperationCanceledException();
						}
						monitor.subTask(Messages.bodhiNewHandler_querySpecFileMsg);
						String clog = getClog(fedoraProjectRoot);
						String bugIDs = findBug(clog);
						String buildName = getBuildName(fedoraProjectRoot);
						String release = getReleaseName(fedoraProjectRoot);

						if (monitor.isCanceled()) {
							throw new OperationCanceledException();
						}
						// if debugging, want to use stub
						if (!debug) {
							dialog = new BodhiNewDialog(shell, buildName,
									release, bugIDs, clog);
							Display.getDefault().syncExec(new Runnable() {
								@Override
								public void run() {
									dialog.open();
								}						
							});
						}

						if (dialog.getReturnCode() == Window.OK) {
							String type = dialog.getType();
							String request = dialog.getRequest();
							String bugs = dialog.getBugs();
							String notes = dialog.getNotes();

							String cachedUsername = retrievePreference("username"); //$NON-NLS-1$
							String cachedPassword = null;
							if (cachedUsername != null) {
								cachedPassword = retrievePreference("password"); //$NON-NLS-1$
							}
							if (cachedPassword == null) {
								cachedUsername = System.getProperty("user.name"); //$NON-NLS-1$
								cachedPassword = ""; //$NON-NLS-1$
							}

							if (monitor.isCanceled()) {
								throw new OperationCanceledException();
							}
							if (!debug) {
								authDialog = new UserValidationDialog(
										shell, BodhiClient.BODHI_URL, cachedUsername,
										cachedPassword,
										Messages.bodhiNewHandler_updateLoginMsg,
								"icons/bodhi-icon-48.png"); //$NON-NLS-1$
								Display.getDefault().syncExec(new Runnable() {
									@Override
									public void run() {
										authDialog.open();
									}							
								});
							}
							if (authDialog.getReturnCode() != Window.OK) {
								// Canceled
								return Status.CANCEL_STATUS;
							}

							String username = authDialog.getUsername();
							String password = authDialog.getPassword();

							IStatus result = newUpdate(buildName, release,
									type, request, bugs, notes, username,
									password, monitor);


							String message = result.getMessage();
							for (IStatus child : result.getChildren()) {
								message += "\n" + child.getMessage(); //$NON-NLS-1$
							}

							// success
							if (result.isOK()) {
								FedoraHandlerUtils.handleOK(message, true);

								if (authDialog.getAllowCaching()) {
									storeCredentials(username, password);
								}
							} else {
								FedoraHandlerUtils.handleError(message);
							}
							return result;
						}
						else {
							return Status.CANCEL_STATUS;
						}
					} else {
						return FedoraHandlerUtils.handleError(NLS.bind(Messages.bodhiNewHandler_notCorrectTagFail, branchName, tag));
					}
				} catch (CoreException e) {
					e.printStackTrace();
					return FedoraHandlerUtils.handleError(e);
				}
			}
		};
		job.setUser(true);
		job.schedule();
		return null;
	}
	
	
	/**
	 * Get Bodhi release name of the current branch.
	 * 
	 * @param projectRoot
	 * @return The release name.
	 * @throws CoreException
	 */
	public String getReleaseName(FedoraProjectRoot projectRoot) throws CoreException {
		IFpProjectBits projectBits = FedoraHandlerUtils.getVcsHandler(projectRoot);
		return projectBits.getCurrentBranchName().replaceAll("-", "");
	}

	/**
	 * Get Bodhi build name from spec file.
	 * 
	 * @param projectRoot
	 * @return The build name as specified in the spec file.
	 * @throws CoreException
	 */
	public String getBuildName(FedoraProjectRoot projectRoot) throws CoreException {
		return FedoraHandlerUtils.rpmQuery(projectRoot, "NAME") + "-" //$NON-NLS-1$ //$NON-NLS-2$
		+ FedoraHandlerUtils.rpmQuery(projectRoot, "VERSION") + "-" //$NON-NLS-1$ //$NON-NLS-2$
		+ FedoraHandlerUtils.rpmQuery(projectRoot, "RELEASE"); //$NON-NLS-1$
	}

	/**
	 * Get the Bodhi client.
	 * 
	 * @return The bodhi client.
	 */
	public IBodhiClient getBodhi() {
		return bodhi;
	}

	/**
	 * Set Bodhi client instance.
	 * 
	 * @param bodhi
	 */
	public void setBodhi(IBodhiClient bodhi) {
		this.bodhi = bodhi;
	}

	/**
	 * Get user validation dialog.
	 * 
	 * @return The user validation dialog.
	 */
	public IUserValidationDialog getAuthDialog() {
		return authDialog;
	}

	/**
	 * Set user validation dialog.
	 * 
	 * @param authDialog
	 */
	public void setAuthDialog(IUserValidationDialog authDialog) {
		this.authDialog = authDialog;
	}

	/**
	 * Get the Bodhi UI dialog for pushing updates.
	 *  
	 * @return The UI dialog.
	 */
	public IBodhiNewDialog getDialog() {
		return dialog;
	}

	/**
	 * Set the Bodhi UI dialog.
	 * 
	 * @param dialog
	 */
	public void setDialog(IBodhiNewDialog dialog) {
		this.dialog = dialog;
	}

	private void storeCredentials(String username, String password) {
		ISecurePreferences node = getBodhiNode();
		if (node != null) {
			try {
				node.put("username", username, false); //$NON-NLS-1$
				node.put("password", password, true); //$NON-NLS-1$
			} catch (StorageException e) {
				e.printStackTrace();
				FedoraHandlerUtils.handleError(e);
			}
		}
	}

	private String retrievePreference(String pref) {
		ISecurePreferences node = getBodhiNode();
		if (node == null)
			return null;
		try {
			String username = node.get(pref, null);
			if (username != null) {
				return username;
			}
		} catch (StorageException e) {
			e.printStackTrace();
			FedoraHandlerUtils.handleError(e);
		}
		return null;
	}

	protected IStatus newUpdate(String buildName, String release, String type,
			String request, String bugs, String notes, String username,
			String password, IProgressMonitor monitor) {
		IStatus status;

		try {
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			if (!debug) {
				monitor.subTask(Messages.bodhiNewHandler_connectToBodhi);
				bodhi = new BodhiClient();
			}

			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			// Login
			monitor.subTask(Messages.bodhiNewHandler_loginBodhi);
			JSONObject result = bodhi.login(username, password);
			if (result.has("message")) { //$NON-NLS-1$
				throw new IOException(result.getString("message")); //$NON-NLS-1$
			}

			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			// create new update
			monitor.subTask(Messages.bodhiNewHandler_sendNewUpdate);
			result = bodhi.newUpdate(buildName, release, type, request, bugs,
					notes, result.getString("_csrf_token"));
			status = new MultiStatus(BodhiPlugin.PLUGIN_ID, IStatus.OK, result
					.getString("tg_flash"), null); //$NON-NLS-1$
			if (result.has("update")) { //$NON-NLS-1$
				((MultiStatus) status).add(new Status(IStatus.OK,
						BodhiPlugin.PLUGIN_ID, result.getString("update"))); //$NON-NLS-1$
			}
			
			// Logout
			monitor.subTask(Messages.bodhiNewHandler_logoutMsg);
			bodhi.logout();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			status = FedoraHandlerUtils.handleError(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			status = FedoraHandlerUtils.handleError(e.getMessage());
		} catch (ParseException e) {
			e.printStackTrace();
			status = FedoraHandlerUtils.handleError(e.getMessage());
		} catch (JSONException e) {
			e.printStackTrace();
			status = FedoraHandlerUtils.handleError(e.getMessage());
		}

		return status;
	}

	private String findBug(String clog) {
		String bugs = ""; //$NON-NLS-1$
		Pattern p = Pattern.compile("#([0-9]*)"); //$NON-NLS-1$
		Matcher m = p.matcher(clog);
		while (m.find()) {
			bugs += m.group() + ","; //$NON-NLS-1$
		}
		return bugs.length() > 0 ? bugs.substring(0, bugs.length() - 1) : bugs;
	}

	private ISecurePreferences getBodhiNode() {
		ISecurePreferences preferences = SecurePreferencesFactory.getDefault();
		if (preferences == null)
			return null;

		try {
			return preferences.node("bodhi"); //$NON-NLS-1$
		} catch (IllegalArgumentException e) {
			return null; // invalid path
		}
	}

}
