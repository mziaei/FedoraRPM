package org.fedoraproject.eclipse.packager.fedorarpm.core;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.egit.core.op.ConnectProviderOperation;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.ui.PlatformUI;

public class SRPMFedoraProjectCreator {
	private IWorkspaceRoot root;
	private IProject project;
	private IProjectDescription description;
	
	/**
	 * Creates a project with the given name in the given location.
	 * @param projectName The name of the project.
	 * @param projectPath The parent location of the project.
	 * @param monitor Progress monitor to report back status.
	 * @throws WrongRepositoryStateException 
	 * @throws JGitInternalException 
	 * @throws ConcurrentRefUpdateException 
	 * @throws NoMessageException 
	 * @throws NoHeadException 
	 */
	public void create(String projectName, IPath projectPath, IProgressMonitor monitor) {

			root = ResourcesPlugin.getWorkspace().getRoot();
			project = root.getProject(projectName);
			description = ResourcesPlugin.getWorkspace()
					.newProjectDescription(project.getName());
			if (!Platform.getLocation().equals(projectPath))
				description.setLocation(projectPath);
			try {
				project.create(description, monitor);
				monitor.worked(2);   //? TODO - Do we need this
				project.open(monitor);  //?TODO - Do we need this
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

