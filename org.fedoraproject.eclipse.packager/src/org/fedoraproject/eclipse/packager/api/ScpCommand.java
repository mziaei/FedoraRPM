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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jsch.ui.UserInfoPrompter;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.fedoraproject.eclipse.packager.FedoraPackagerText;
import org.fedoraproject.eclipse.packager.api.errors.CommandListenerException;
import org.fedoraproject.eclipse.packager.api.errors.CommandMisconfiguredException;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/**
 * A class used to execute a {@code Scp} command. It has setters for all
 * supported options and arguments of this command and a
 * {@link #call(IProgressMonitor)} method to finally execute the command. Each
 * instance of this class should only be used for one invocation of the command
 * (means: one call to {@link #call(IProgressMonitor)})
 */
public class ScpCommand extends FedoraPackagerCommand<ScpResult> {

	/**
	 * The unique ID of this command.
	 */
	public static final String ID = "ScpCommand"; //$NON-NLS-1$
	private static final String FEDORAHOST = "fedorapeople.org"; //$NON-NLS-1$

	private String fasAccount;
	private String specFile;
	private String srpmFile;

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
	 * @return The result of this command.
	 */
	@Override
	public ScpResult call(IProgressMonitor monitor)
			throws CommandMisconfiguredException, CommandListenerException {
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

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		FileDialogRunable fileDialog = new FileDialogRunable(null,
				FedoraPackagerText.ScpCommand_ChoosePrivateKey);
		shell.getDisplay().syncExec(fileDialog);
		String filePath = fileDialog.getFile();

		try {
			if (filePath != null) {
				jsch.addIdentity(filePath);
			}

			Session session;
			session = jsch.getSession(fasAccount, FEDORAHOST, 22);

			UserInfo userInfo = session.getUserInfo();
			if (userInfo == null || userInfo.getPassword() == null) {
				UserInfoPrompter userInfoPrompt = new UserInfoPrompter(session);
			}

			session.setConfig("StrictHostKeyChecking", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			session.connect();

			copyFileToRemote(srpmFile, session);
			copyFileToRemote(specFile, session);

			session.disconnect();

			System.exit(0);

		} catch (JSchException e) {
			e.printStackTrace();
		}

		ScpResult result = new ScpResult();

		// Call post-exec listeners
		callPostExecListeners();
		result.setSuccessful(true);
		setCallable(false);
		return result;
	}

	/**
	 * Copies the localFile to remote location at remoteFile
	 *
	 * @param fileName to be copied remotely
	 * @param session of the current operation
	 *
	 */
	private void copyFileToRemote(String fileName, Session session) {
		FileInputStream fis = null;

		// exec 'scp -t remoteFile' remotely
		String remoteFile = "public_html" + IPath.SEPARATOR + srpmFile; //$NON-NLS-1$
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
				System.exit(0);
			}

			// send "C0644 filesize filename", where filename should not include
			// '/'
			String localFile = projectRoot.getProject().getLocation().toString()
					+ IPath.SEPARATOR + srpmFile;
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
				System.exit(0);
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
				System.exit(0);
			}
			out.close();

			channel.disconnect();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void checkConfiguration() throws CommandMisconfiguredException {
		if (this.specFile == null || this.srpmFile == null) {
			throw new IllegalStateException(
					FedoraPackagerText.ScpCommand_FilesToScpUnspecified);
		}
	}

	/**
	 * @param fasAccount
	 *            sets the FAS account
	 * @return this instance
	 */
	public ScpCommand setFasAccount(String fasAccount) {
		this.fasAccount = fasAccount;
		return this;
	}

	/**
	 * @param specFile
	 *            sets the .spec file to scp
	 * @return this instance
	 */
	public ScpCommand setSpecFileToScp(String specFile) {
		this.specFile = specFile;
		return this;
	}

	/**
	 * @param srpmFile
	 *            sets the .src.rpm file to scp
	 * @return this instance
	 */
	public ScpCommand setSrpmFileToScp(String srpmFile) {
		this.srpmFile = srpmFile;
		return this;
	}

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
				System.out.print(sb.toString());
			}
			if (b == 2) { // fatal error
				System.out.print(sb.toString());
			}
		}
		return b;
	}
}