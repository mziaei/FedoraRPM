package org.fedoraproject.eclipse.packager.fedorarpm.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.fedoraproject.eclipse.packager.FedoraPackagerLogger;
import org.fedoraproject.eclipse.packager.FedoraPackagerText;
import org.fedoraproject.eclipse.packager.IProjectRoot;
import org.fedoraproject.eclipse.packager.PackagerPlugin;
import org.fedoraproject.eclipse.packager.api.errors.FedoraPackagerExtensionPointException;
import org.fedoraproject.eclipse.packager.api.errors.InvalidProjectRootException;
import org.fedoraproject.eclipse.packager.fedorarpm.FedoraRPMProjectRoot;
import org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils;
import org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils.ProjectType;


public class FedoraRPMPackagerUtils extends FedoraPackagerUtils {
	

	public static IProjectRoot getProjectRoot(IResource resource)
			throws InvalidProjectRootException {
		IContainer candidate = null;
		if (resource instanceof IFolder || resource instanceof IProject) {
			candidate = (IContainer) resource;
		} else if (resource instanceof IFile) {
			candidate = resource.getParent();
		}
		ProjectType type = getProjectType(candidate);
//		if (candidate != null && type != null) {
		try {
			return instantiateProjectRoot(candidate, type);
		} catch (FedoraPackagerExtensionPointException e) {
			FedoraPackagerLogger logger = FedoraPackagerLogger.getInstance();
			logger.logError(e.getMessage(), e);
			throw new InvalidProjectRootException(e.getMessage());
		}
		// TODO - Fix the error
//		} else {
//			throw new InvalidProjectRootException(FedoraPackagerText.FedoraPackagerUtils_invalidProjectRootError);
//		}
	}

	/**
	 * Instatiate a project root instance using the projectRoot extension point.
	 * @param type 
	 * @param container 
	 * 
	 * @return the newly created instance
	 * @throws FedoraPackagerExtensionPointException 
	 */
	private static IProjectRoot instantiateProjectRoot(IContainer container, ProjectType type)
			throws FedoraPackagerExtensionPointException {
//		IExtensionPoint projectRootExtension = Platform.getExtensionRegistry()
//				.getExtensionPoint(PackagerPlugin.PLUGIN_ID,
//						PROJECT_ROOT_EXTENSIONPOINT_NAME);
//		if (projectRootExtension != null) {
//			List<IProjectRoot> projectRootList = new ArrayList<IProjectRoot>();
//			for (IConfigurationElement projectRoot : projectRootExtension
//					.getConfigurationElements()) {
//				if (projectRoot.getName().equals(PROJECT_ROOT_ELEMENT_NAME)) {
//					// found extension point element
//					try {
//						IProjectRoot root = (IProjectRoot) projectRoot
//								.createExecutableExtension(PROJECT_ROOT_CLASS_ATTRIBUTE_NAME);
//						assert root != null;
//						projectRootList.add(root);
//					} catch (IllegalStateException e) {
//						throw new FedoraPackagerExtensionPointException(
//								e.getMessage(), e);
//					} catch (CoreException e) {
//						throw new FedoraPackagerExtensionPointException(
//								e.getMessage(), e);
//					}
//				}
//			}
//			// We need at least one project root
//			if (projectRootList.size() == 0) {
//				throw new FedoraPackagerExtensionPointException(NLS.bind(
//						FedoraPackagerText.extensionNotFoundError,
//						PROJECT_ROOT_EXTENSIONPOINT_NAME));
//			}
//			// Get the best matching project root
//			IProjectRoot projectRoot = findBestMatchingProjectRoot(projectRootList, container);
//			if (projectRoot == null) {
//				// can't continue
//				throw new FedoraPackagerExtensionPointException(NLS.bind(
//						FedoraPackagerText.extensionNotFoundError,
//						PROJECT_ROOT_EXTENSIONPOINT_NAME));
//			}
			IProjectRoot projectRoot = new FedoraRPMProjectRoot();
			// Do initialization
			projectRoot.initialize(container, type);
			return projectRoot;
//		}
//		throw new FedoraPackagerExtensionPointException(NLS.bind(
//				FedoraPackagerText.extensionNotFoundError,
//				PROJECT_ROOT_EXTENSIONPOINT_NAME));
	}

	
}
