package de.tu.darmstadt.es.PatternMatchingEngine.viatra.modelconverter;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;

import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.patternmatchingengine.MainClassTemplate;
import de.tu.darmstadt.es.xtext.utils.utils.ResourceUtil;
import de.tu.darmstadt.es.xtext.utils.utils.WorkspaceHelper;

public class JavaFileCreator {
	private MainClassTemplate template;
	private URI uri;
	
	
	public JavaFileCreator(MainClassTemplate template, URI uri) {
		this.template = template;
		this.uri = uri;
	}
	
	public void createMainFile() throws CoreException {
		IProject project = WorkspaceHelper.INSTANCE.getProjectByURI(uri);
		URI mainFileURI = ResourceUtil.getInstance().createURIFromOtherURI(uri, project.getName(), template.getRelativePathOfFolderInProject(), "Main.java");
		IPath path =WorkspaceHelper.INSTANCE.getPathByURI(project, mainFileURI).makeRelativeTo(project.getLocation());
		IFile mainClassFile = project.getFile(path);
		IFolder parentFolder =(IFolder) mainClassFile.getParent();
		
		if(!parentFolder.exists()) {
			parentFolder.getLocation().toFile().mkdirs();
		}
		
		final ByteArrayInputStream source = new ByteArrayInputStream(template.getTemplateText().getBytes());
		
		if(mainClassFile.exists()){
			mainClassFile.setContents(source, IFile.FORCE | IFile.KEEP_HISTORY, null);
		}
		else {
			mainClassFile.create(source, true, null);
		}
	}
}
