package de.tu.darmstadt.es.ui.wizard;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

import org.eclipse.pde.internal.ui.wizards.plugin.NewPluginProjectWizard;

import org.eclipse.ui.INewWizard;


import de.tu.darmstadt.es.xtext.utils.utils.WorkspaceHelper;

@SuppressWarnings("restriction")
public class NeoKappaWizard extends  NewPluginProjectWizard implements INewWizard{

	private String PLUGIN_PROJECT_NATURE_ID = "org.eclipse.pde.PluginNature";
	private String XTEXT_NATURE_ID = "org.eclipse.xtext.ui.shared.xtextNature";
	private String VIATRA_NATURE_ID = "org.eclipse.viatra.query.projectnature";
	private String JAVA_NATURE_ID = "org.eclipse.jdt.core.javanature";	
	
	


	
	@Override
	public boolean performFinish() {
		super.performFinish();
		
		try {
			String projectName = this.fMainPage.getProjectName();
			IProject project = getProject(projectName);			
//			WorkspaceHelper.INSTANCE.addNature(project, PLUGIN_PROJECT_NATURE_ID);
			WorkspaceHelper.INSTANCE.addNature(project, JAVA_NATURE_ID);
			WorkspaceHelper.INSTANCE.addNature(project, XTEXT_NATURE_ID);
			WorkspaceHelper.INSTANCE.addNature(project, VIATRA_NATURE_ID);
//			WorkspaceHelper.INSTANCE.addNeededFoldes(project);
			
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private IProject getProject(String projectName) {
		List <IProject> projects = WorkspaceHelper.INSTANCE.getAllProjectsInWorkspace();
		Optional<IProject> projectMonad = projects.parallelStream().filter(project -> project.getName().equals(projectName)).findFirst();
		return projectMonad.isPresent() ? projectMonad.get() : null;
	}




}
