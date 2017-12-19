package de.tu.darmstadt.es.xtext.utils.utils;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;

public class WorkspaceHelper {

	public final static WorkspaceHelper INSTANCE = new WorkspaceHelper();
	
	private final String SRC = "src";
	
	private WorkspaceHelper() {
		
	}
	
	/**
	 * Returns the list of all projects in the workspace
	 */
	public List<IProject> getAllProjectsInWorkspace() {
		return Arrays.asList(ResourcesPlugin.getWorkspace().getRoot().getProjects());
	}
	
	public boolean projectExist(String projectName) {
		return getAllProjectsInWorkspace().parallelStream().anyMatch(project -> projectName.equalsIgnoreCase(project.getName()));
	}
	
	public IProject createEmptyProject(String projectName) throws CoreException {
		IProgressMonitor progressMonitor = new NullProgressMonitor();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		project.create(progressMonitor);
		project.open(progressMonitor);
		
		return project;
	}
	
	public void addNature(IProject project, String natureID) throws CoreException {
		IProjectDescription description = project.getDescription();

		String[] natures = description.getNatureIds();
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = natureID;

		// validate the natures
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IStatus status = workspace.validateNatureSet(newNatures);

		// only apply new nature, if the status is ok
		if (status.getCode() == IStatus.OK) {
		    description.setNatureIds(newNatures);
		    project.setDescription(description, null);
		}
	}
	
	public IFolder getSrcFolder(IProject project) {
		return project.getFolder(SRC);
	}
	
	public void addNeededFoldes(IProject project) throws CoreException{
		
		
	}
	
}
