package abbot.swt.eclipse.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.intro.IIntroConstants;
import org.eclipse.ui.part.FileEditorInput;

import abbot.swt.script.Condition;
import abbot.swt.tester.ShellTester;
import abbot.swt.tester.WidgetTester;
import abbot.swt.utilities.Displays;
import abbot.swt.utilities.Wait;
import abbot.swt.utilities.Displays.Result;

/**
 * Workbench-related utilities.
 * 
 * @author Gary Johnston
 * @author Kevin Dale
 */
public class WorkbenchUtilities {

	/**
	 * Gets the workbench. This is just a convenience method and is equivalent to
	 * {@link PlatformUI#getWorkbench()}.
	 * 
	 * @return the {@link IWorkbench}
	 */
	public static IWorkbench getWorkbench() {
		return PlatformUI.getWorkbench();
	}

	/**
	 * Gets the {@link Display} of the current workbench.
	 * 
	 * @return the {@link Display} of the current workbench
	 */
	public static Display getDisplay() {
		return getWorkbench().getDisplay();
	}

	public static IWorkbenchWindow getActiveWindow() {
		return getActiveWindow(false);
	}

	public static IWorkbenchWindow getActiveWindow(boolean activate) {

		// Get the active workbench window.
		final IWorkbench workbench = getWorkbench();
		Display display = workbench.getDisplay();
		IWorkbenchWindow window = (IWorkbenchWindow) Displays.syncExec(
				display,
				new Result<IWorkbenchWindow>() {
					public IWorkbenchWindow result() {
						return workbench.getActiveWorkbenchWindow();
					}
				});

		// If there isn't one and activate was specified then activate the first visible workbench
		// window (if any).
		if (window == null && activate) {
			final IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
			window = (IWorkbenchWindow) Displays.syncExec(display, new Result<IWorkbenchWindow>() {
				public IWorkbenchWindow result() {
					for (IWorkbenchWindow window : windows) {
						Shell shell = window.getShell();
						if (shell.isVisible()) {
							shell.setActive();
							return window;
						}
					}
					return null;
				}
			});
		}

		return window;
	}

	public static Shell getActiveShell() {
		return getActiveShell(false);
	}

	public static Shell getActiveShell(boolean activate) {
		return getActiveWindow(activate).getShell();
	}

	public static IWorkbenchPage getActivePage() {
		return getActivePage(true);
	}

	public static IWorkbenchPage getActivePage(boolean activate) {
		IWorkbenchWindow window = getActiveWindow(activate);
		return window.getActivePage();
	}

	public static IEditorPart getActiveEditor() {
		IWorkbenchWindow window = getActiveWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null)
				return page.getActiveEditor();
		}
		return null;
	}

	public static interface EditorReferenceMatcher {
		boolean matches(IEditorReference reference);
	}

	public static interface EditorMatcher {
		boolean matches(IEditorPart editor);
	}

	public static IEditorReference findEditorReference(EditorReferenceMatcher matcher) {
		for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
			for (IWorkbenchPage page : window.getPages()) {
				for (IEditorReference reference : page.getEditorReferences()) {
					if (matcher.matches(reference))
						return reference;
				}
			}
		}
		return null;
	}

	public static IEditorPart findEditor(EditorReferenceMatcher matcher, boolean restore) {
		IEditorReference reference = findEditorReference(matcher);
		if (reference != null)
			return reference.getEditor(restore);
		return null;
	}

	public static IEditorPart findEditor(final EditorMatcher matcher, final boolean restore) {
		return findEditor(new EditorReferenceMatcher() {
			public boolean matches(IEditorReference reference) {
				IEditorPart editor = reference.getEditor(restore);
				return editor != null && matcher.matches(editor);
			}

		}, restore);
	}

	/**
	 * Gets an open editor by its editor ID. If there are more than one then the first one happened
	 * upon will be returned.
	 * 
	 * @param editorId
	 *            the editor ID
	 * @return the {@link IEditorPart} of the editor that was found, or <code>null</code> if none.
	 */
	public static IEditorPart findEditor(final String editorId) {
		return findEditor(new EditorReferenceMatcher() {
			public boolean matches(IEditorReference editor) {
				return (editorId.equals(editor.getId()));
			}
		}, true);
	}

	/**
	 * Gets an open editor based on the file it's editing.
	 * 
	 * @param file
	 *            the {@link IFile} being edited
	 * @return the {@link IEditorPart} of the editor that was found, or <code>null</code> if none.
	 */
	public static IEditorPart findEditor(final IFile file) throws PartInitException {
		IWorkbenchPage page = getActivePage();
		IEditorInput input = new FileEditorInput(file);
		return page.findEditor(input);
	}

	/**
	 * Default number of milliseconds to wait for an editor to open. Value is 60000 (60 seconds).
	 */
	public static final long EDITOR_OPEN_WAIT_TIMEOUT = 60000L;
	
	/**
	 * Wait up to 60 sec. for an editor to open on a specified file.
	 * @param file an {@link IFile}
	 * @return the {@link IEditorInput} of the editor that opened
	 */
	public static IEditorPart waitEditorOpen(final IFile file) {
		return waitEditorOpen(file, EDITOR_OPEN_WAIT_TIMEOUT);
	}

	/**
	 * Wait up to a specified amount of time for an editor to open on a specified file.
	 * @param file an {@link IFile}
	 * @param timeout the maximum amount of time to wait (in milliseconds)
	 * @return the {@link IEditorInput} of the editor that opened
	 */
	public static IEditorPart waitEditorOpen(final IFile file, long timeout) {
		final EditorMatcher matcher = new EditorMatcher() {
			public boolean matches(IEditorPart editor) {
				// If editor has finished being initialized...
				if (editor.getEditorSite() != null) {
					IEditorInput input = editor.getEditorInput();
					if (input instanceof IFileEditorInput)
						return file.equals(((IFileEditorInput) input).getFile());
				}
				return false;
			}
		};
		final IEditorPart[] editor = new IEditorPart[1];
		WidgetTester.getWidgetTester().wait(new Condition() {
			public boolean test() {
				editor[0] = findEditor(matcher, false);
				return editor[0] != null;
			}
		}, timeout);
		return editor[0];
	}

	/**
	 * Activates a specified editor.
	 * 
	 * @param editor
	 *            the {@link IEditorPart} to be activated
	 */
	public static void activateEditor(final IEditorPart editor) {
		IEditorSite site = editor.getEditorSite();
		final IWorkbenchPage page = site.getPage();
		Display display = site.getShell().getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				page.activate(editor);
			}
		});
		if (editor != page.getActiveEditor())
			throw new RuntimeException("could not activate " + editor);
	}

	public static IEditorPart openEditor(IFile file, String editorId) throws PartInitException {
		IWorkbenchPage page = getActivePage();
		assert page != null;
		IEditorPart editor = IDE.openEditor(page, file, editorId, true);
		assert editor != null;
		return editor;
	}
	
	public static void waitEditorClose(final IEditorPart editor) {
		Wait.wait(new Condition() {
			public boolean test() {
				return null == findEditor(new EditorMatcher() {
					public boolean matches(IEditorPart candidate) {
						return candidate == editor;
					}

				}, false);
			}
		}, 30000);
	}

	public static IEditorPart openEditor(final IFile file) throws PartInitException {
		final IWorkbenchPage page = getActivePage();
		assert page != null;
		final PartInitException[] exception = new PartInitException[1];
		final IEditorPart[] editor = new IEditorPart[1];
		getDisplay().syncExec(new Runnable() {
			public void run() {
				try {
					editor[0] = IDE.openEditor(page, file, true, true);
				} catch (PartInitException e) {
					exception[0] = e;
				}
			}
		});
		if (exception[0] != null)
			throw exception[0];
		assert editor[0] != null;
		return editor[0];
	}

	public static IWorkbenchWindow getWorkbenchWindow() {
		return (IWorkbenchWindow) Displays.syncExec(getDisplay(), new Result() {
			public Object result() {
				return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			}
		});
	}

	/**
	 * Brings the workbench to the top of the drawing order so that it receives keyboard focus.
	 */
	public static void bringWorkbenchToFront() {
		Shell workbenchShell = getWorkbenchWindow().getShell();
		bringToFront(workbenchShell);
	}

	/**
	 * Brings the passed Shell to the top of the drawing order so that it receives keyboard focus.
	 */
	public static void bringToFront(final Shell shell) {
		ShellTester tester = (ShellTester) WidgetTester.getTester(shell);
		tester.forceActive(shell);
		tester.actionFocus(shell);
		tester.waitForIdle();
	}

	public static void hideViewNamed(final String partName) {
		final IWorkbenchPage page = getActivePage(true);
		Displays.syncExec(new Runnable() {
			public void run() {
				IViewReference[] references = page.getViewReferences();
				for (IViewReference reference : references) {
					if (reference.getPartName().equals(partName)) {
						page.hideView(reference);
						return;
					}
				}
			}
		});
	}

	public static void hideView(String viewId) {
		final IWorkbenchPage page = getActivePage(true);
		final IViewReference reference = page.findViewReference(viewId);
		if (reference != null)
			Displays.syncExec(new Runnable() {
				public void run() {
					page.hideView(reference);
				}
			});
	}

	public static void hideWelcomeView() {
		hideView(IIntroConstants.INTRO_VIEW_ID);
	}

}
