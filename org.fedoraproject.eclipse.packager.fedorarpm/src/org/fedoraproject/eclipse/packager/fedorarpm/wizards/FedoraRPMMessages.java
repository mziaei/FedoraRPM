package org.fedoraproject.eclipse.packager.fedorarpm.wizards;

import org.eclipse.osgi.util.NLS;

public class FedoraRPMMessages extends NLS {
	private static final String BUNDLE_NAME = "org.fedoraproject.eclipse.packager.fedorarpm.wizards.fedorarpmtext"; //$NON-NLS-1$
	
	//FedoraRPMProject-wizards
	public static String FedoraRPMProject_title;
	public static String FedoraRPMProject_description;
	public static String FedoraRPMProject_image;
	public static String FedoraRPMProjectIWizard_SRpm;
	public static String FedoraRPMProjectIWizard_Stubby;
	public static String FedoraRPMProjectIWizard_Plain;
	public static String FedoraRPMProjectPageOne_lblNoteGit;
	public static String FedoraRPMProjectPageTwo_linkFAS;
	public static String FedoraRPMProjectPageTwo_urlFAS;
	public static String FedoraRPMProjectPageTwo_lblTextFAS;
	public static String FedoraRPMProjectPageTwo_linkBugzilla;
	public static String FedoraRPMProjectPageTwo_urlBugzilla;
	public static String FedoraRPMProjectPageTwo_linkInitial;
	public static String FedoraRPMProjectPageTwo_urlInitial;
	public static String FedoraRPMProjectPageTwo_linkIntroduce;
	public static String FedoraRPMProjectPageTwo_urlIntroduce;
	public static String FedoraRPMProjectPageTwo_btnRadioNewMaintainer;
	public static String FedoraRPMProjectPageTwo_btnRadioExistMaintainer;
	public static String FedoraRPMProjectPageTwo_grpAccountSetup;
	public static String FedoraRPMProjectPageThree_btnCheckStubby;
	public static String FedoraRPMProjectPageThree_lblStubby;
	public static String FedoraRPMProjectPageThree_btnStubbyBrowse;
	public static String FedoraRPMProjectPageThree_btnCheckSrpm;
	public static String FedoraRPMProjectPageThree_lblSrpm;
	public static String FedoraRPMProjectPageThree_btnSrpmBrowse;

	//FedoraRPMProject-api
	public static String FedoraRPMProject_api_FirstCommit; 
	public static String FedoraRPMProject_api_Gitignore;
	public static String FedoraRPMProject_api_Project;
	public static String FedoraRPMProject_api_SpecExtention;
	public static String FedoraRPMProject_api_FeatureFile;
	public static String FedoraRPMProject_api_PomFile;
	
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, FedoraRPMMessages.class);
	}

}
