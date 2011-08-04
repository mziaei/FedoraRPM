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
package org.fedoraproject.eclipse.packager.fedorarpm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.linuxtools.rpm.ui.editor.parser.Specfile;
import org.eclipse.linuxtools.rpm.ui.editor.parser.SpecfilePackage;
import org.eclipse.linuxtools.rpm.ui.editor.parser.SpecfileParser;
import org.fedoraproject.eclipse.packager.ILookasideCache;
import org.fedoraproject.eclipse.packager.IProductStrings;
import org.fedoraproject.eclipse.packager.IProjectRoot;
import org.fedoraproject.eclipse.packager.ProductStringsNonTranslatable;
import org.fedoraproject.eclipse.packager.SourcesFile;
import org.fedoraproject.eclipse.packager.api.errors.FedoraPackagerExtensionPointException;
import org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils.ProjectType;
import org.fedoraproject.eclipse.packager.utils.RPMUtils;

/**
 * This class is representing a root directory for a Fedora package in a given
 * branch. It can be a folder in the cvs case or a project in the git case.
 * 
 */
public class FedoraRPMProjectRoot implements IProjectRoot {
	
	private IContainer rootContainer;
	private ProjectType type;
	private IProductStrings productStrings;


	/**
	 * Default no-arg constructor. Required for instance creation via
	 * reflections.
	 */
	public FedoraRPMProjectRoot() {
		// nothing
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.fedoraproject.eclipse.packager.IProjectRoot#initialize(org.eclipse.core.resources.IContainer, org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils.ProjectType)
	 */
	@Override
	public void initialize(IContainer container, ProjectType type) throws FedoraPackagerExtensionPointException {
		this.rootContainer = container;
		assert type != null;
		this.type = type;
		this.productStrings = new ProductStringsNonTranslatable(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.fedoraproject.eclipse.packager.IProjectRoot#getContainer()
	 */
	@Override
	public IContainer getContainer() {
		return rootContainer;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.fedoraproject.eclipse.packager.IProjectRoot#getProject()
	 */
	@Override
	public IProject getProject() {
		return this.rootContainer.getProject();
	}

	/*
	 * (non-Javadoc)
	 * @see org.fedoraproject.eclipse.packager.IProjectRoot#getPackageName()
	 */
	@Override
	public String getPackageName() {
		return this.getSpecfileModel().getName();
	}

	/*
	 * (non-Javadoc)
	 * @see org.fedoraproject.eclipse.packager.IProjectRoot#getSpecFile()
	 */
	@Override
	public IFile getSpecFile() {
		try {
			for (IResource resource : rootContainer.members()) {
				if (resource instanceof IFile) {
					String ext = ((IFile) resource).getFileExtension();
					if (ext != null && ext.equals("spec")) //$NON-NLS-1$
						return (IFile) resource;
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.fedoraproject.eclipse.packager.IProjectRoot#getSpecfileModel()
	 */
	@Override
	public Specfile getSpecfileModel() {
		SpecfileParser parser = new SpecfileParser();
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					getSpecFile().getContents()));
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n"); //$NON-NLS-1$
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		Specfile specfile = parser.parse(sb.toString());
		return specfile;
	}

	/*
	 * (non-Javadoc)
	 * @see org.fedoraproject.eclipse.packager.IProjectRoot#getProjectType()
	 */
	@Override
	public ProjectType getProjectType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see org.fedoraproject.eclipse.packager.IProjectRoot#getIgnoreFile()
	 */
	@Override
	public IFile getIgnoreFile() {
		String ignoreFileName = null;
		switch (type) {
		case GIT:
			ignoreFileName = ".gitignore"; //$NON-NLS-1$
			break;
		default:
			break;
		}
		assert ignoreFileName != null;
		IFile ignoreFile = getFileMember(ignoreFileName);
		// If not existent, return a IFile handle from the container
		if (ignoreFile == null) {
			return this.rootContainer.getFile(new Path(ignoreFileName));
		}
		assert ignoreFile != null;
		return ignoreFile;
	}

	/*
	 * (non-Javadoc)
	 * @see org.fedoraproject.eclipse.packager.IProjectRoot#getSupportedProjectPropertyNames()
	 */
	@Override
	public QualifiedName[] getSupportedProjectPropertyNames() {
		return new QualifiedName[] { FedorarpmPlugin.PROJECT_PROP };
	}

	/**
	 * Find the ignore file in the root container if any.
	 * 
	 * @param ignoreFileName The name of the VCS ignore file.
	 * 
	 * @return The file handle if found. {@code null} otherwise.
	 */
	private IFile getFileMember(String ignoreFileName) {
		IResource resource = rootContainer.findMember(ignoreFileName);
		if (resource instanceof IFile) {
			return ((IFile) resource);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.fedoraproject.eclipse.packager.IProjectRoot#getPackageNVRs()
	 */
	@Override
	public String[] getPackageNVRs() {
		String version = null, release = null;
		try {
			version = RPMUtils.rpmQuery(this, "VERSION"); //$NON-NLS-1$
			release = RPMUtils.rpmQuery(this, "RELEASE"); //$NON-NLS-1$
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> rawNvrs = new ArrayList<String>();
		for (SpecfilePackage p: getSpecfileModel().getPackages().getPackages()) {
			rawNvrs.add(p.getFullPackageName() + "-" + version + "-" + release); //$NON-NLS-1$ //$NON-NLS-2$
		}
		String[] nvrs = rawNvrs.toArray(new String[]{});
		Arrays.sort(nvrs);
		return nvrs;
	}

	@Override
	public SourcesFile getSourcesFile() {
		return null;
	}

	@Override
	public ILookasideCache getLookAsideCache() {
		return null;
	}

	@Override
	public IProductStrings getProductStrings() {
		return this.productStrings;
	}

}
