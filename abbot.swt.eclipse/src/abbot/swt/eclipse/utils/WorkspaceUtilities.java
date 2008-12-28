package abbot.swt.eclipse.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import junit.framework.Assert;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import abbot.swt.eclipse.EclipsePlugin;

public class WorkspaceUtilities extends Assert {

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public static IWorkspaceRoot getWorkspaceRoot() {
		return getWorkspace().getRoot();
	}

	public static IProject getProject(String name) {
		return getWorkspaceRoot().getProject(name);
	}

	public static IProject createProject(String name) throws CoreException {
		return createProject(getProject(name));
	}

	public static IProject createProject(IProject project) throws CoreException {
		if (project.exists())
			project.delete(true, true, null);
		project.create(null);
		project.open(null);
		assertTrue(project.isAccessible());
		assertTrue(project.isSynchronized(IResource.DEPTH_INFINITE));
		return project;
	}

	public static void deleteProject(String name) throws CoreException {
		getProject(name).delete(true, true, null);
	}

	public static IFile getFile(String fullPath) {
		return getFile(new Path(fullPath));
	}

	public static IFile getFile(IPath fullPath) {
		return getWorkspaceRoot().getFile(fullPath);
	}

	public static IFile createFile(String projectName, String fileName, InputStream source)
			throws CoreException {
		IProject project = getProject(projectName);
		IFile file = project.getFile(fileName);
		if (file.exists())
			file.setContents(source, true, false, null);
		else
			file.create(source, true, null);
		assertTrue(file.isAccessible());
		assertTrue(file.isSynchronized(IResource.DEPTH_INFINITE));
		return file;
	}

	public static IFile createFile(String projectName, String fileName, String resourceName)
			throws CoreException {
		InputStream source = WorkspaceUtilities.class.getResourceAsStream(resourceName);
		return createFile(projectName, fileName, source);
	}

	public static void unzip(IContainer target, InputStream stream) throws CoreException {

		// Create the zip input stream.
		// Note: we override ZipInputStream.close() as a no-op in order to prevent
		// calls to IFile.create(InputStream, boolean, IProgressMonitor) from really
		// closing it. We'll close the underlying InputStream ourselves in the
		// finally block.
		ZipInputStream zipStream = new ZipInputStream(stream) {
			public void close() throws IOException {}
		};

		try {
			for (;;) {

				// Get the next
				ZipEntry entry = zipStream.getNextEntry();
				if (entry == null)
					break;

				// If it's a directory, create the corresponding target folder.
				// If it's a file, create or overwrite it with the entry's contents.
				IPath path = new Path(entry.getName());
				if (entry.isDirectory()) {
					IFolder folder = target.getFolder(path);
					if (!folder.exists())
						createFolder(folder);
				} else {
					IFile file = target.getFile(path);
					IContainer container = file.getParent();
					if (!container.exists()) {
						switch (container.getType()) {
							case IResource.FOLDER:
								createFolder((IFolder) container);
								break;
							case IResource.PROJECT:
								createProject((IProject) container);
								break;
							default:
								error("invalid container: " + container, null);
						}
					}
					if (file.exists())
						file.setContents(zipStream, true, false, null);
					else
						file.create(zipStream, true, null);
				}

			}
		} catch (IOException e) {
			error("unzip failed", e);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				error("close failed", e);
			}
		}
	}

	private static void createFolder(IFolder folder) throws CoreException {
		IPath fullPath = folder.getFullPath();
		int n = fullPath.segmentCount();
		IContainer parent = folder.getProject();
		for (int i = 1; i < n; i++) {
			IPath subPath = new Path(fullPath.segment(i));
			IFolder subfolder = parent.getFolder(subPath);
			if (!subfolder.exists())
				subfolder.create(true, true, null);
			parent = subfolder;
		}
	}

	private static void error(String message, Exception exception) throws CoreException {
		if (message == null)
			message = exception.getMessage();
		if (message == null)
			message = "error";
		throw new CoreException(new Status(IStatus.ERROR, EclipsePlugin.getId(), 0, message,
				exception));
	}
}
