package abbot.swt.eclipse.tester;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.internal.WorkbenchMessages;

import abbot.swt.finder.generic.Matcher;
import abbot.swt.finder.matchers.WidgetClassMatcher;
import abbot.swt.script.Condition;
import abbot.swt.tester.ItemPath;
import abbot.swt.tester.ShellTester;
import abbot.swt.tester.TreeTester;

public class WizardTester extends DialogTester {

	public static final WizardTester Default = new WizardTester();

	public static final String WIZARD_SELECTION_PAGE_TITLE = WorkbenchMessages.NewWizardSelectionPage_description;

	public IWizardPage actionClickNext(Shell wizardShell) {
		return actionClickNext(wizardShell, getNextPage(wizardShell).getTitle());
	}

	public IWizardPage actionClickNext(Shell wizardShell, String nextPageTitle) {
		actionClickButton(wizardShell, IDialogConstants.NEXT_LABEL);
		return waitPage(wizardShell, nextPageTitle);
	}

	public IWizardPage actionClickBack(Shell wizardShell) {
		return actionClickBack(wizardShell, getPreviousPage(wizardShell).getTitle());
	}

	public IWizardPage actionClickBack(Shell wizardShell, String previousPageTitle) {
		actionClickButton(wizardShell, IDialogConstants.BACK_LABEL);
		return waitPage(wizardShell, previousPageTitle);
	}

	public void actionClickFinish(Shell wizardShell) {
		actionClickButton(wizardShell, IDialogConstants.FINISH_LABEL);
	}

	public void actionClickCancel(Shell wizardShell) {
		actionClickButton(wizardShell, IDialogConstants.CANCEL_LABEL);
	}

	protected IWizardPage waitPage(final Shell wizardShell, final String pageTitle) {
		final IWizardPage[] page = new IWizardPage[1];
		ShellTester.getShellTester().wait(new Condition() {
			public boolean test() {
				page[0] = getCurrentPage(wizardShell);
				return pageTitle.equals(page[0].getTitle());
			};
		}, 5000);
		return page[0];
	}

	public IWizardPage getCurrentPage(Shell wizardShell) {
		return getWizard(wizardShell).getCurrentPage();
	}

	public IWizardPage getNextPage(Shell wizardShell) {
		return getCurrentPage(wizardShell).getNextPage();
	}

	public IWizardPage getPreviousPage(Shell wizardShell) {
		return getCurrentPage(wizardShell).getPreviousPage();
	}

	public WizardDialog getWizard(Shell wizardShell) {
		return (WizardDialog) ShellTester.getShellTester().getData(wizardShell);
	}

	public Control getCurrentControl(Shell wizardShell) {
		return getCurrentPage(wizardShell).getControl();
	}

	public Widget findCurrent(Shell wizardShell, Matcher<Widget> matcher) {
		return find(getCurrentControl(wizardShell), matcher);
	}

	public void actionSelectWizard(Shell wizardShell, ItemPath wizardPath, String wizardPageTitle) {

		if (!WIZARD_SELECTION_PAGE_TITLE.equals(getCurrentPage(wizardShell).getTitle()))
			throw new RuntimeException("wrong page");

		// Find the wizard tree.
		Tree wizardTree = (Tree) findCurrent(wizardShell, new WidgetClassMatcher(Tree.class));

		// Select the specified project wizard.
		TreeTester.getTreeTester().actionClickItem(wizardTree, wizardPath);

		// Hit "Next" and wait for the next wizard page to appear.
		actionClickNext(wizardShell, wizardPageTitle);
		// TODO - Do we really need the caller to pass us the wizardpagetitle?
		// If the pages get populated when we select the wizard, then no.
		// Need to find out when the wizard pages get created (or figure out how to wait for
		// that after we hit "Next"). Fun stuff...
	}

	public IWizardPage actionGoToPage(Shell wizardShell, String targetPageTitle) {
		int currentPageIndex = getCurrentPageIndex(wizardShell);
		int targetPageIndex = getPageIndex(wizardShell, targetPageTitle);
		IWizardPage page = null;
		if (currentPageIndex < targetPageIndex) {
			do {
				page = actionGoToNextPage(wizardShell);
			} while (!targetPageTitle.equals(page.getTitle()));
		} else if (currentPageIndex < targetPageIndex) {
			do {
				page = actionGoToBackPage(wizardShell);
			} while (!targetPageTitle.equals(page.getTitle()));
		}
		return page;
	}

	public IWizardPage actionGoToNextPage(Shell wizardShell) {
		IWizardPage page = getCurrentPage(wizardShell).getNextPage();
		actionClickNext(wizardShell, page.getTitle());
		return page;
	}

	public IWizardPage actionGoToBackPage(Shell wizardShell) {
		IWizardPage page = getCurrentPage(wizardShell).getPreviousPage();
		actionClickBack(wizardShell, page.getTitle());
		return page;
	}

	private int getCurrentPageIndex(Shell wizardShell) {
		IWizardPage currentPage = getCurrentPage(wizardShell);
		IWizardPage[] pages = currentPage.getWizard().getPages();
		for (int i = 0; i < pages.length; i++) {
			if (pages[i] == currentPage)
				return i;
		}
		throw new RuntimeException("no current page");
	}

	private int getPageIndex(Shell wizardShell, String pageTitle) {
		IWizardPage[] pages = getCurrentPage(wizardShell).getWizard().getPages();
		for (int i = 0; i < pages.length; i++) {
			if (pageTitle.equals(pages[i].getTitle()))
				return i;
		}
		throw new RuntimeException("no such page: " + pageTitle);
	}

}
