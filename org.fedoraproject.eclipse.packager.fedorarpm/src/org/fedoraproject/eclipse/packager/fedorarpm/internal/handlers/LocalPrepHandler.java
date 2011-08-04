package org.fedoraproject.eclipse.packager.fedorarpm.internal.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.fedoraproject.eclipse.packager.FedoraPackagerLogger;
import org.fedoraproject.eclipse.packager.FedoraPackagerPreferencesConstants;
import org.fedoraproject.eclipse.packager.FedoraPackagerText;
import org.fedoraproject.eclipse.packager.IProjectRoot;
import org.fedoraproject.eclipse.packager.PackagerPlugin;
import org.fedoraproject.eclipse.packager.api.DownloadSourceCommand;
import org.fedoraproject.eclipse.packager.api.DownloadSourcesJob;
import org.fedoraproject.eclipse.packager.api.FedoraPackager;
import org.fedoraproject.eclipse.packager.api.FedoraPackagerAbstractHandler;
import org.fedoraproject.eclipse.packager.api.errors.CommandListenerException;
import org.fedoraproject.eclipse.packager.api.errors.CommandMisconfiguredException;
import org.fedoraproject.eclipse.packager.api.errors.FedoraPackagerCommandInitializationException;
import org.fedoraproject.eclipse.packager.api.errors.FedoraPackagerCommandNotFoundException;
import org.fedoraproject.eclipse.packager.api.errors.InvalidProjectRootException;
import org.fedoraproject.eclipse.packager.fedorarpm.FedoraRPMProjectRoot;
import org.fedoraproject.eclipse.packager.fedorarpm.utils.FedoraRPMPackagerUtils;
import org.fedoraproject.eclipse.packager.rpm.RPMPlugin;
import org.fedoraproject.eclipse.packager.rpm.RpmText;
import org.fedoraproject.eclipse.packager.rpm.api.RpmBuildCommand;
import org.fedoraproject.eclipse.packager.rpm.api.RpmBuildCommand.BuildType;
import org.fedoraproject.eclipse.packager.rpm.api.errors.RpmBuildCommandException;
import org.fedoraproject.eclipse.packager.utils.FedoraHandlerUtils;
import org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils;
import org.eclipse.linuxtools.rpm.core.RPMProject;
import org.eclipse.linuxtools.rpm.core.RPMProjectLayout;
//import org.eclipse.linuxtools.rpm.ui.IRPMUIConstants.BuildType;
import org.eclipse.linuxtools.rpm.ui.RPMExportOperation;
import org.eclipse.swt.widgets.Shell;



public class LocalPrepHandler extends FedoraPackagerAbstractHandler {


	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		
//		// using linuxtools.rpm.ui package
		
//		IResource eventResource = FedoraHandlerUtils.getResource(event);
//		RPMExportOperation rpmExport;
//		try {
//			rpmExport = new RPMExportOperation(new RPMProject(FedoraRPMPackagerUtils.getProject(eventResource),
//					RPMProjectLayout.FLAT), BuildType.ALL);
//			rpmExport.setUser(true);
//			rpmExport.schedule();
//
//		} catch (CoreException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
		
		
		
		
		
		// Using fedoraproject.rpm
		
//		final Shell shell = getShell(event);
//		final IProjectRoot fedoraRPMProjectRoot;

//		final IProject fedoraRPMProject = FedoraRPMPackagerUtils.getProject(eventResource);   //none IProjectRoot

		final Shell shell = getShell(event);
		final FedoraPackagerLogger logger = FedoraPackagerLogger.getInstance();
		final IProjectRoot fedoraRPMProjectRoot;

		IResource eventResource = FedoraHandlerUtils.getResource(event);
		try {
			fedoraRPMProjectRoot = FedoraRPMPackagerUtils.getProjectRoot(eventResource);
		} catch (InvalidProjectRootException e) {
			logger.logError(FedoraPackagerText.invalidFedoraProjectRootError, e);
			FedoraHandlerUtils.showErrorDialog(shell, "Error", //$NON-NLS-1$
					FedoraPackagerText.invalidFedoraProjectRootError);
			return null;
		}
		FedoraPackager fp = new FedoraPackager(fedoraRPMProjectRoot);
		final RpmBuildCommand prepCommand;
//		final DownloadSourceCommand download;
		try {
//			// need to get sources for an SRPM build
//			download = (DownloadSourceCommand) fp
//					.getCommandInstance(DownloadSourceCommand.ID);
			// get RPM build command in order to produce an SRPM
			prepCommand = (RpmBuildCommand) fp
					.getCommandInstance(RpmBuildCommand.ID);
		} catch (FedoraPackagerCommandNotFoundException e) {
			logger.logError(e.getMessage(), e);
			FedoraHandlerUtils.showErrorDialog(shell,
					fedoraRPMProjectRoot.getProductStrings().getProductName(), e.getMessage());
			return null;
		} catch (FedoraPackagerCommandInitializationException e) {
			logger.logError(e.getMessage(), e);
			FedoraHandlerUtils.showErrorDialog(shell,
					fedoraRPMProjectRoot.getProductStrings().getProductName(), e.getMessage());
			return null;
		}
		

		// Need to nest jobs into this job for it to show up properly in the UI
		// in terms of progress
		Job job = new Job(fedoraRPMProjectRoot.getProductStrings().getProductName()) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				// Make sure we have sources locally
//				final String downloadUrlPreference = PackagerPlugin
//				.getStringPreference(FedoraPackagerPreferencesConstants.PREF_LOOKASIDE_DOWNLOAD_URL);
//				Job downloadSourcesJob = new DownloadSourcesJob(
//						RpmText.PrepHandler_downloadSourcesForPrep,
//						download, fedoraProjectRoot, shell, downloadUrlPreference, true);
//				downloadSourcesJob.setUser(true);
//				downloadSourcesJob.schedule();
//				try {
//					// wait for download job to finish
//					downloadSourcesJob.join();
//				} catch (InterruptedException e1) {
//					throw new OperationCanceledException();
//				}
//				if (!downloadSourcesJob.getResult().isOK()) {
//					// bail if something failed
//					return downloadSourcesJob.getResult();
//				}
				// Do the prep job
				Job prepJob = new Job(fedoraRPMProjectRoot.getProductStrings().getProductName()) {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						try {
							monitor.beginTask(
									RpmText.PrepHandler_prepareSourcesForBuildMsg,
									IProgressMonitor.UNKNOWN);
							List<String> nodeps = new ArrayList<String>(1);
							nodeps.add(RpmBuildCommand.NO_DEPS);
//							RpmBuildCommand prepCommand = new RpmBuildCommand();
								prepCommand.buildType(BuildType.PREP)
										.flags(nodeps).call(monitor);
						} catch (CommandMisconfiguredException e) {
							e.printStackTrace();
						} catch (CommandListenerException e) {
							e.printStackTrace();
						} catch (RpmBuildCommandException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} finally {
							monitor.done();
						}
						return Status.OK_STATUS;
					}
				};
				prepJob.setUser(true);
				prepJob.schedule();
				try {
					// wait for job to finish
					prepJob.join();
				} catch (InterruptedException e1) {
					throw new OperationCanceledException();
				}
				return prepJob.getResult();
			}

		};
		job.setSystem(true); // suppress UI. That's done in encapsulated jobs.
		job.schedule();
		return null;
	}

}

		// not sure yet
//		try {
//		fedoraRPMProjectRoot = FedoraRPMPackagerUtils.getProjectRoot(eventResource);
//	} catch (InvalidProjectRootException e1) {
//		// TODO Auto-generated catch block
//		e1.printStackTrace();
//	}

//	try {
//		RPMProject project = new RPMProject(FedoraRPMPackagerUtils.getProject(eventResource),
//				RPMProjectLayout.FLAT);
//	} catch (CoreException e) {
//		e.printStackTrace();
//	} catch (InvalidProjectRootException e) {
//		e.printStackTrace();
//	}
//

//// Need to nest jobs into this job for it to show up properly in the UI
//// in terms of progress
//Job job = new Job(FedoraRPMPackagerUtils.getProject(eventResource).getName()) {
//
//	@Override
//	protected IStatus run(IProgressMonitor monitor) {
//		try {
//			monitor.beginTask(
//					RpmText.PrepHandler_prepareSourcesForBuildMsg,
//					IProgressMonitor.UNKNOWN);
//			RpmBuildCommand prepCommand = new RpmBuildCommand();
//			prepCommand.buildType(BuildType.PREP);
//		} finally {
//			monitor.done();
//		}
//		return Status.OK_STATUS;
//	}
//};
//job.setUser(true);
//job.schedule();
//return null;
//}