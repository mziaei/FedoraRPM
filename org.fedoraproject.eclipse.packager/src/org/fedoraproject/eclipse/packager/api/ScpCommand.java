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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jsch.ui.UserInfoPrompter;
import org.eclipse.ui.PlatformUI;
import org.fedoraproject.eclipse.packager.FedoraPackagerLogger;
import org.fedoraproject.eclipse.packager.FedoraPackagerText;
import org.fedoraproject.eclipse.packager.FedoraSSL;
import org.fedoraproject.eclipse.packager.api.errors.CommandListenerException;
import org.fedoraproject.eclipse.packager.api.errors.CommandMisconfiguredException;
import org.fedoraproject.eclipse.packager.api.errors.ScpFailedException;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import org.eclipse.jsch.internal.core.IConstants;
import org.eclipse.jsch.internal.core.JSchCorePlugin;
import org.eclipse.jsch.internal.core.PreferenceInitializer;

/**
 * A class used to execute a {@code Scp} command. It has setters for all
 * supported options and arguments of this command and a
 * {@link #call(IProgressMonitor)} method to finally execute the command. Each
 * instance of this class should only be used for one invocation of the command
 * (means: one call to {@link #call(IProgressMonitor)})
 */
@SuppressWarnings("restriction")
public class ScpCommand extends FedoraPackagerCommand<ScpResult> {

	/**
	 * The unique ID of this command.
	 */
	public static final String ID = "ScpCommand"; //$NON-NLS-1$
	private static final String FEDORAHOST  = "fedorapeople.org"; //$NON-NLS-1$
	private static final String PUBLIC_HTML = "public_html"; //$NON-NLS-1$
	private static final String REMOTE_DIR  = "fpe-rpm-review"; //$NON-NLS-1$

	private String fasAccount;
	private String specFile;
	private String srpmFile;

	private boolean scpconfirmed;
	private String fileScpConfirm;

	final static FedoraPackagerLogger logger = FedoraPackagerLogger.getInstance();

	/*
	 * Implementation of the {@code ScpCommand}.
	 *
	 * @param monitor
	 *
	 * @throws CommandMisconfiguredException If the command was not properly
	 * configured when it was called.
	 *
	 * @throws CommandListenerException If some listener detected a problem.
	 *
	 * @throws ScpFailedException if .src.rpm file does not exist to be copied
	 *
	 * @return The result of this command.
	 */
	@Override
	public ScpResult call(IProgressMonitor monitor)
			throws CommandMisconfiguredException, CommandListenerException, ScpFailedException {
		try {
			callPreExecListeners();
		} catch (CommandListenerException e) {
			if (e.getCause() instanceof CommandMisconfiguredException) {
				// explicitly throw the specific exception
				throw (CommandMisconfiguredException) e.getCause();
			}
			throw e;
		}

		JSch jsch = new JSch();

	    IPreferencesService service = Platform.getPreferencesService();
	    String ssh_home = service.getString(JSchCorePlugin.ID,
	        IConstants.KEY_SSH2HOME, PreferenceInitializer.SSH_HOME_DEFAULT, null);
		String ssh_keys = service.getString(JSchCorePlugin.ID,
	        IConstants.KEY_PRIVATEKEY, "id_rsa", null); //$NON-NLS-1$

	    String[] ssh_key = ssh_keys.split(","); //$NON-NLS-1$

	    String privateKeyFile = ssh_home.concat("/").concat(ssh_key[1]); //$NON-NLS-1$

		try {
			if (privateKeyFile != null) {
				jsch.addIdentity(privateKeyFile);
			}

			Session session;
			session = jsch.getSession(fasAccount, FEDORAHOST, 22);

			UserInfo userInfo = session.getUserInfo();
			if (userInfo == null || userInfo.getPassword() == null) {
				@SuppressWarnings("unused")
				UserInfoPrompter userInfoPrompt = new UserInfoPrompter(session);
			}

			session.setConfig("StrictHostKeyChecking", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			session.connect();

			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}

			if (srpmFile.isEmpty()) {
				throw new ScpFailedException(FedoraPackagerText.ScpCommand_filesToScpMissing);
			}

			// create the 'fpe-rpm-review' directory in public_html if it doesn't exist
			// return false if srpm file already exists
			boolean scpOk = checkRemoteDir(session);

			if (scpOk) {
				copyFileToRemote(specFile, session);
				copyFileToRemote(srpmFile, session);
			}

			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}

			session.disconnect();

		} catch (Exception e) {
			if (e instanceof OperationCanceledException) {
				throw ((OperationCanceledException) e);
			} else {
				throw new ScpFailedException(e.getMessage(), e);
			}
		}

		ScpResult result = new ScpResult(specFile, srpmFile);

		// Call post-exec listeners
		callPostExecListeners();
		result.setSuccessful(true);
		setCallable(false);

		return result;
	}

	/**
	 * check the public_html and create the remote directory if it doesn't exist
	 * If it exists, make sure the files to copy don't already exist
	 *
	 * @param session
	 *            of the current operation
	 * @throws ScpFailedException
	 *
	 */
	private boolean checkRemoteDir(Session session) throws ScpFailedException {
		boolean dirFound = false;
		boolean fileFound = false;
		boolean rc = false;

		Channel channel;

		try {
			channel = session.openChannel("sftp"); //$NON-NLS-1$

			channel.connect();
			ChannelSftp channelSftp = (ChannelSftp) channel;

			// check if the remote directory exists
			// if not, create the proper directory in public_html
			Vector existDir = channelSftp.ls(PUBLIC_HTML);
			Iterator it = existDir.iterator();
			while (it.hasNext() && !dirFound) {
				LsEntry entry = (LsEntry) it.next();
				String dirName = entry.getFilename();
				if (dirName.equals(REMOTE_DIR))
					dirFound = true;
			}
			if (!dirFound)
				channelSftp.mkdir(PUBLIC_HTML + IPath.SEPARATOR + REMOTE_DIR);

			// check if the files to scp already exist in the remote directory
			// if yes, ask for confirmation
			Vector existFile = channelSftp.ls(PUBLIC_HTML + IPath.SEPARATOR + REMOTE_DIR );
			it = existFile.iterator();
			while (it.hasNext() && !fileFound) {
				LsEntry entry = (LsEntry) it.next();
				String fileName = entry.getFilename();
				if (fileName.equals(srpmFile))
					fileFound = true;
			}
			if (fileFound) {
				fileScpConfirm = FedoraPackagerText.ScpCommand_filesToScpExist;
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						scpconfirmed = MessageDialog.openConfirm
								(null, FedoraPackagerText.ScpCommand_notificationTitle,
										fileScpConfirm.concat("\n *" + srpmFile)); //$NON-NLS-1$
					}
				});
				rc = scpconfirmed;
			}
			else {
				rc = true;
			}

			channel.disconnect();

		} catch (Exception e) {
			throw new ScpFailedException(e.getMessage(), e);
		}

		return rc;

	}

	/**
	 * Copies the localFile to remote location at remoteFile
	 *
	 * @param fileToScp
	 *            to be copied remotely
	 * @param session
	 *            of the current operation
	 * @throws ScpFailedException
	 *
	 */
	private void copyFileToRemote(String fileToScp, Session session) throws ScpFailedException {
		FileInputStream fis = null;

		// exec 'scp -t remoteFile' remotely
		String remoteFile = PUBLIC_HTML + IPath.SEPARATOR + REMOTE_DIR
				+ IPath.SEPARATOR + fileToScp;
		String command = "scp -p -t " + remoteFile; //$NON-NLS-1$

		Channel channel;
		try {
			channel = session.openChannel("exec"); //$NON-NLS-1$
			((ChannelExec) channel).setCommand(command);

			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();

			channel.connect();

			if (checkAck(in) != 0) {
				throw new ScpFailedException(FedoraPackagerText.ScpCommand_filesToScpNonReadable);
			}

			// send "C0644 filesize filename", where filename should not include
			// '/'
			String localFile = projectRoot.getProject().getLocation()
					.toString()
					+ IPath.SEPARATOR + fileToScp;
			long filesize = (new File(localFile)).length();
			command = "C0644 " + filesize + " "; //$NON-NLS-1$ //$NON-NLS-2$
			if (localFile.lastIndexOf('/') > 0) {
				command += localFile.substring(localFile.lastIndexOf('/') + 1);
			} else {
				command += localFile;
			}
			command += "\n"; //$NON-NLS-1$

			out.write(command.getBytes());
			out.flush();
			if (checkAck(in) != 0) {
				throw new ScpFailedException(FedoraPackagerText.ScpCommand_filesToScpNonReadable);
			}

			// send a content of localFile
			fis = new FileInputStream(localFile);
			byte[] buf = new byte[1024];
			while (true) {
				int len = fis.read(buf, 0, buf.length);
				if (len <= 0)
					break;
				out.write(buf, 0, len); // out.flush();
			}
			fis.close();
			fis = null;

			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			if (checkAck(in) != 0) {
				throw new ScpFailedException
					(FedoraPackagerText.ScpCommand_filesToScpNonReadable);
			}
			out.close();

			channel.disconnect();

		} catch (Exception e) {
			throw new ScpFailedException(e.getMessage(), e);
		}
	}

	@Override
	protected void checkConfiguration() throws CommandMisconfiguredException {
		// We are good to go with the defaults. No-Op.

	}

	/**
	 * @param fasAccount
	 *            sets the FAS account
	 * @return this instance
	 * @throws ScpFailedException
	 */
	public ScpCommand setFasAccount(String fasAccount) throws ScpFailedException {
		this.fasAccount = fasAccount;
		if (fasAccount == FedoraSSL.UNKNOWN_USER) {
			throw new ScpFailedException
				(FedoraPackagerText.ScpHandler_fasAccountMissing);
		}
		return this;
	}

	/**
	 * @param specFile
	 */
	public void setSpecFile(String specFile) {
		this.specFile = specFile;
	}

	/**
	 * @param srpmFile
	 */
	public void setSrpmFile(String srpmFile) {
		this.srpmFile = srpmFile;
	}

	/*
	 *
	 * @param in
	 *
	 * @throws IOException
	 *
	 * @return 0 if successful
	 */
	static int checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success, 1 for error,
		// 2 for fatal error, -1
		if (b == 0)
			return b;
		if (b == -1)
			return b;

		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { // error
				logger.logError(sb.toString());
			}
			if (b == 2) { // fatal error
				logger.logError(sb.toString());
			}
		}
		return b;
	}

}