package org.fedoraproject.eclipse.packager.fedorarpm.core;

import java.io.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

public class FedoraRPMProjectCreator {
	private InputStream source;

	/**
	 * Creates a project with the given name in the given location.
	 * @param projectName The name of the project.
	 * @param projectPath The parent location of the project.
	 * @param monitor Progress monitor to report back status.
	 */
	public void create(String projectName, IPath projectPath,
			IProgressMonitor monitor) {
		try {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IProject project = root.getProject(projectName);
			IProjectDescription description = ResourcesPlugin.getWorkspace()
					.newProjectDescription(project.getName());
			if (!Platform.getLocation().equals(projectPath))
				description.setLocation(projectPath);
//			description
//					.setNatureIds(new String[] { RPMProjectNature.RPM_NATURE_ID });
			project.create(description, monitor);
			monitor.worked(2);
			project.open(monitor);

			project.getFolder("temp").create(true, true, monitor);
			project.getFile("sources").create(null, true, monitor);
			project.getFile(".git").create(null, true, monitor);
			writeFile("test", "testing");
			
			//project.getFile(".gitignore").create(getFile("test"), true, monitor);

			
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
    /**
     * Write file.
     *
     * @param filename the filename
     * @param content the content
     */
    public void writeFile(final String filename, final String content) {
        try {

            FileWriter fstream = new FileWriter(filename);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(content);
            // Close the output stream
            out.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage()); //$NON-NLS-1$
        }

    }
    
    /**
     * Get file.
     *
     * @param filename the filename
     */
    public InputStream writeFile(final String filename) {
    	InputStream is = null;
        try {

            FileWriter fstream = new FileWriter(filename);
            BufferedWriter out = new BufferedWriter(fstream);

            // Close the output stream
            out.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage()); //$NON-NLS-1$
        }
        return is;

    }

}
