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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jgit.transport.CredentialsProviderUserInfo;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.util.FS;
import org.eclipse.jsch.core.IJSchService;
import org.eclipse.jsch.ui.UserInfoPrompter;
import org.fedoraproject.eclipse.packager.FedoraPackagerText;
import org.fedoraproject.eclipse.packager.api.errors.CommandListenerException;
import org.fedoraproject.eclipse.packager.api.errors.CommandMisconfiguredException;

import org.eclipse.jsch.ui.UserInfoPrompter;
import com.jcraft.jsch.*;

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
	private OpenSshConfig config;
	private String host = "fedorapeople.org";
	private int port = 22;

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
		Session session;

		try {

			session = jsch.getSession(fasAccount, host, port); //$NON-NLS-1$

			// username and password will be given via UserInfo interface.
			UserInfo ui = new MyUserInfo();
			// ui.promptPassword(fasAccount);
			session.setUserInfo(ui);

			session.connect(); // //*** This is where I'm getting error for
								// authentication

			Channel channel = session.openChannel("shell");

			channel.setInputStream(System.in);
			channel.setOutputStream(System.out);

			channel.connect();
			// // exec 'scp -t rfile' remotely
			// String command = "scp -p -t " + srpmFile;
			// Channel channel = session.openChannel("exec");
			// ((ChannelExec) channel).setCommand(command);
			//
			// // get I/O streams for remote scp
			// OutputStream out = channel.getOutputStream();
			// InputStream in = channel.getInputStream();
			//
			// channel.connect();
			//
			// if (checkAck(in) != 0) {
			// System.exit(0);
			// }

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

	public static class MyUserInfo implements UserInfo, UIKeyboardInteractive {
		@Override
		public String getPassword() {
			return null;
		}

		@Override
		public boolean promptYesNo(String str) {
			Object[] options = { "yes", "no" };
			int foo = JOptionPane.showOptionDialog(null, str, "Warning",
					JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
					null, options, options[0]);
			return foo == 0;
		}

		String passphrase;
		JTextField passphraseField = (JTextField) new JPasswordField(20);

		@Override
		public String getPassphrase() {
			return passphrase;
		}

		@Override
		public boolean promptPassphrase(String message) {
			Object[] ob = { passphraseField };
			int result = JOptionPane.showConfirmDialog(null, ob, message,
					JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				passphrase = passphraseField.getText();
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean promptPassword(String message) {
			return true;
		}

		@Override
		public void showMessage(String message) {
			JOptionPane.showMessageDialog(null, message);
		}

		final GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0);
		private Container panel;

		@Override
		public String[] promptKeyboardInteractive(String destination,
				String name, String instruction, String[] prompt, boolean[] echo) {
			panel = new JPanel();
			panel.setLayout(new GridBagLayout());

			gbc.weightx = 1.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.gridx = 0;
			panel.add(new JLabel(instruction), gbc);
			gbc.gridy++;

			gbc.gridwidth = GridBagConstraints.RELATIVE;

			JTextField[] texts = new JTextField[prompt.length];
			for (int i = 0; i < prompt.length; i++) {
				gbc.fill = GridBagConstraints.NONE;
				gbc.gridx = 0;
				gbc.weightx = 1;
				panel.add(new JLabel(prompt[i]), gbc);

				gbc.gridx = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.weighty = 1;
				if (echo[i]) {
					texts[i] = new JTextField(20);
				} else {
					texts[i] = new JPasswordField(20);
				}
				panel.add(texts[i], gbc);
				gbc.gridy++;
			}

			if (JOptionPane.showConfirmDialog(null, panel, destination + ": "
					+ name, JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
				String[] response = new String[prompt.length];
				for (int i = 0; i < prompt.length; i++) {
					response[i] = texts[i].getText();
				}
				return response;
			} else {
				return null; // cancel
			}
		}
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