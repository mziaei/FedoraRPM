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
package org.fedoraproject.eclipse.packager.local;

import org.eclipse.osgi.util.NLS;

public class LocalFedoraPackagerText extends NLS {
	private static final String BUNDLE_NAME = "org.fedoraproject.eclipse.packager.local.localfedorapackagertext"; //$NON-NLS-1$

	//Local Fedora Packager Project-wizards-main
	/****/ public static String LocalFedoraPackager_title;
	/****/ public static String LocalFedoraPackager_description;
	/****/ public static String LocalFedoraPackager_image;
	/****/ public static String LocalFedoraPackager_IWizard_SRpm;
	/****/ public static String LocalFedoraPackager_IWizard_Stubby;
	/****/ public static String LocalFedoraPackager_IWizard_Plain;
	//Local Fedora Packager Project-wizards-page one
	/****/ public static String LocalFedoraPackager_PageOne_lblNoteGit;
	//Local Fedora Packager Project-wizards-page Two
	/****/ public static String LocalFedoraPackager_PageTwo_linkFAS;
	/****/ public static String LocalFedoraPackager_PageTwo_urlFAS;
	/****/ public static String LocalFedoraPackager_PageTwo_lblTextFAS;
	/****/ public static String LocalFedoraPackager_PageTwo_linkBugzilla;
	/****/ public static String LocalFedoraPackager_PageTwo_urlBugzilla;
	/****/ public static String LocalFedoraPackager_PageTwo_linkInitial;
	/****/ public static String LocalFedoraPackager_PageTwo_urlInitial;
	/****/ public static String LocalFedoraPackager_PageTwo_linkIntroduce;
	/****/ public static String LocalFedoraPackager_PageTwo_urlIntroduce;
	/****/ public static String LocalFedoraPackager_PageTwo_btnRadioNewMaintainer;
	/****/ public static String LocalFedoraPackager_PageTwo_btnRadioExistMaintainer;
	/****/ public static String LocalFedoraPackager_PageTwo_grpAccountSetup;
	//Local Fedora Packager Project-wizards-page Three
	/****/ public static String LocalFedoraPackager_PageThree_btnCheckStubby;
	/****/ public static String LocalFedoraPackager_PageThree_lblStubby;
	/****/ public static String LocalFedoraPackager_PageThree_btnStubbyBrowse;
	/****/ public static String LocalFedoraPackager_PageThree_btnCheckSrpm;
	/****/ public static String LocalFedoraPackager_PageThree_lblSrpm;
	/****/ public static String LocalFedoraPackager_PageThree_btnSrpmBrowse;
	/****/ public static String LocalFedoraPackager_PageThree_btnCheckPlain;
	/****/ public static String LocalFedoraPackager_IWizard_fileDialog;
	/****/ public static String LocalFedoraPackager_IWizard_file;

	//Local Fedora Packager Project-api
	/****/ public static String LocalFedoraPackager_api_FirstCommit;

	//Local Fedora Packager Project-api-errors
	/****/ public static String LocalFedoraPackagerUtils_invalidLocalProjectRootError;
	/****/ public static String invalidLocalFedoraProjectRootError;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, LocalFedoraPackagerText.class);
	}

}
