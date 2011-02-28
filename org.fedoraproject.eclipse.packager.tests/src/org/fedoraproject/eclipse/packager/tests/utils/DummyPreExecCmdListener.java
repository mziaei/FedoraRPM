package org.fedoraproject.eclipse.packager.tests.utils;

import org.fedoraproject.eclipse.packager.api.FedoraPackagerCommand;
import org.fedoraproject.eclipse.packager.api.ICommandListener;
import org.fedoraproject.eclipse.packager.api.errors.CommandListenerException;

/**
 * Fixture for
 * {@link FedoraPackagerCommand#addCommandListener(ICommandListener)} testing.
 * 
 */
public class DummyPreExecCmdListener implements ICommandListener {

	public static final String EXCEPTION_MSG = "preExecTest";
	
	public void preExecution() throws CommandListenerException {
		// throw some arbitrary exception (wrapped in CmdListEx)
		throw new CommandListenerException(new IllegalStateException(EXCEPTION_MSG));
	}

	public void postExecution() throws CommandListenerException {
		// nothing
	}

}