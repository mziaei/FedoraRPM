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

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.eclipse.core.resources.ResourcesPlugin;
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
import com.jcraft.jsch.UIKeyboardInteractive;
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

		FileInputStream fis = null;

		JSch jsch = new JSch();

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		FileDialogRunable fileDialog = new FileDialogRunable(null,
				"Choose your private key (e.g. ~/.ssh/id_rsa)");
		shell.getDisplay().syncExec(fileDialog);
		String filePath = fileDialog.getFile();

		try {
			if (filePath != null) {
				jsch.addIdentity(filePath);
			}

			Session session;
			session = jsch.getSession(fasAccount, "fedorapeople.org", 22); //$NON-NLS-1$

			UserInfo userInfo = session.getUserInfo();
			if (userInfo == null || userInfo.getPassword() == null) {
				UserInfoPrompter userInfoPrompt = new UserInfoPrompter(session);
				boolean passphrase = userInfoPrompt
						.promptPassphrase(fasAccount);
			}

			session.setConfig("StrictHostKeyChecking", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			session.connect();

			// exec 'scp -t rfile' remotely
			String command = "scp -p -t " + ResourcesPlugin.getWorkspace().getRoot().getProject("helloworld").getLocation().toString() + "/" + srpmFile; //$NON-NLS-1$ //$NON-NLS-2$
			Channel channel = session.openChannel("exec"); //$NON-NLS-1$
			((ChannelExec) channel).setCommand(command);

			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();

			channel.connect();

			if (checkAck(in) != 0) {
				System.exit(0);
			}

			String lfile = "public_html/" + srpmFile;

			// send "C0644 filesize filename", where filename should not include
			// '/'
			long filesize = (new File(lfile)).length();
			command = "C0644 " + filesize + " "; //$NON-NLS-1$ //$NON-NLS-2$
			if (lfile.lastIndexOf('/') > 0) {
				command += lfile.substring(lfile.lastIndexOf('/') + 1);
			} else {
				command += lfile;
			}
			command += "\n";
			out.write(command.getBytes());
			out.flush();
			if (checkAck(in) != 0) {
				System.exit(0);
			}

			// send a content of lfile
			fis = new FileInputStream(lfile);
			byte[] buf = new byte[1024];
			while (true) {
				int len = fis.read(buf, 0, buf.length);
				if (len <= 0)
					break;
				out.write(buf, 0, len); // out.flush();
			}
			fis.close();
			fis = null;
			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			if (checkAck(in) != 0) {
				System.exit(0);
			}
			out.close();

			channel.disconnect();
			session.disconnect();

			System.exit(0);

		} catch (JSchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ScpResult result = new ScpResult();

		// Call post-exec listeners
		callPostExecListeners();
		result.setSuccessful(true);
		setCallable(false);
		return result;
	}

	@Override
	protected void checkConfiguration() throws CommandMisconfiguredException {
		if (this.specFile == null || this.srpmFile == null) {
			throw new IllegalStateException(
					FedoraPackagerText.SpecCommand_FilesToScpUnspecified);
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
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
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

// if (config == null)
// config = OpenSshConfig.get(FS.DETECTED);
//
// final OpenSshConfig.Host hc = config.lookup(host);
// host = hc.getHostName();
// UserInfo ui = new MyUserInfo();
//
//
// if (port <= 0)
// port = hc.getPort();
// if (user == null)
// user = hc.getUser();

// if (pass != null)
// session.setPassword(pass);
// final String strictHostKeyCheckingPolicy = hc
// .getStrictHostKeyChecking();
// if (strictHostKeyCheckingPolicy != null)
// session.setConfig("StrictHostKeyChecking",
// strictHostKeyCheckingPolicy);
// final String pauth = hc.getPreferredAuthentications();
// if (pauth != null)
// session.setConfig("PreferredAuthentications", pauth);
//
// UserInfo userInfo = session.getUserInfo();
// if (!hc.isBatchMode()
// && (userInfo == null || userInfo.getPassword() == null))

// new UserInfoPrompter(session);
// if (!session.isConnected())
// session.connect();

// UserInfo userInfo = session.getUserInfo();
// if (userInfo == null || userInfo.getPassword() == null) {
// UserInfoPrompter userInfoPrompt = new UserInfoPrompter(session);
// boolean passphrase = userInfoPrompt.promptPassphrase(fasAccount);
// }