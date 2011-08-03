package org.fedoraproject.eclipse.packager.fedorarpm.internal.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.fedoraproject.eclipse.packager.api.FedoraPackagerAbstractHandler;

public class LocalPrepHandler extends FedoraPackagerAbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(),
				"Eclipse Fedora Packager Local project plug-in",
				"Hello, Eclipse world");
		return null;
	}


}
