package org.fedoraproject.eclipse.packager.fedorarpm.internal.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.fedoraproject.eclipse.packager.api.FedoraPackagerAbstractHandler;
import org.fedoraproject.eclipse.packager.fedorarpm.utils.FedoraRPMPackagerUtils;
import org.fedoraproject.eclipse.packager.rpm.RpmText;
import org.fedoraproject.eclipse.packager.rpm.api.RpmBuildCommand;
import org.fedoraproject.eclipse.packager.rpm.api.RpmBuildCommand.BuildType;
import org.fedoraproject.eclipse.packager.utils.FedoraHandlerUtils;
import org.eclipse.linuxtools.rpm.core.RPMProject;
import org.eclipse.linuxtools.rpm.core.RPMProjectLayout;
//import org.eclipse.linuxtools.rpm.ui.IRPMUIConstants.BuildType;
import org.eclipse.linuxtools.rpm.ui.RPMExportOperation;



public class LocalPrepHandler extends FedoraPackagerAbstractHandler {

	
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
//		final Shell shell = getShell(event);
//		final IProjectRoot fedoraRPMProjectRoot;
		
		IResource eventResource = FedoraHandlerUtils.getResource(event);
//		try {
//			fedoraRPMProjectRoot = FedoraRPMPackagerUtils.getProjectRoot(eventResource);
//		} catch (InvalidProjectRootException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
//		try {
//			RPMProject project = new RPMProject(FedoraRPMPackagerUtils.getProject(eventResource),
//					RPMProjectLayout.FLAT);
//		} catch (CoreException e) {
//			e.printStackTrace();
//		} catch (InvalidProjectRootException e) {
//			e.printStackTrace();
//		} 
//		
//		RPMExportOperation rpmExport;
//		try {
//			rpmExport = new RPMExportOperation(new RPMProject(FedoraRPMPackagerUtils.getProject(eventResource),
//					RPMProjectLayout.FLAT), BuildType.ALL);
//			rpmExport.setUser(true);
//			rpmExport.schedule();

//		} catch (CoreException e) {
//			e.printStackTrace();
//		}
		// Run the export
			
			
			
			// Need to nest jobs into this job for it to show up properly in the UI
			// in terms of progress
			Job job = new Job(FedoraRPMPackagerUtils.getProject(eventResource).getName()) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						monitor.beginTask(
								RpmText.PrepHandler_prepareSourcesForBuildMsg,
								IProgressMonitor.UNKNOWN);					
						RpmBuildCommand prepCommand = new RpmBuildCommand();
						prepCommand.buildType(BuildType.PREP);
					} finally {
						monitor.done();
					}
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		return null;
	}


}


