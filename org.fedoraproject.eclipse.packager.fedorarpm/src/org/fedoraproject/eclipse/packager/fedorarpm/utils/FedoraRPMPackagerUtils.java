package org.fedoraproject.eclipse.packager.fedorarpm.utils;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils;


public class FedoraRPMPackagerUtils extends FedoraPackagerUtils {
	

	
	public static IProject getProject(IResource resource){
		IContainer candidate = null;
		if (resource instanceof IFolder || resource instanceof IProject) {
			candidate = (IContainer) resource;
		} else if (resource instanceof IFile) {
			candidate = resource.getParent();
		}
//		ProjectType type = getProjectType(candidate);
//		if (candidate != null && type != null) {
			return candidate.getProject();
		// TODO - Fix the error
//		} else {
//			throw new InvalidProjectRootException(FedoraPackagerText.FedoraPackagerUtils_invalidProjectRootError);
//		}
	}

	
}
