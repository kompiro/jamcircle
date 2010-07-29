package org.kompiro.jamcircle.kanban.ui.wizard;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swtbot.jface.WithWizard;
import org.eclipse.swtbot.jface.WizardDialogTestRunner;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kompiro.jamcircle.kanban.ui.Messages;

@RunWith(WizardDialogTestRunner.class)
@WithWizard(BoardImportWizard.class)
public class BoardImportWizardTest {

	private SWTBot bot;
	private BoardImportWizard wizard;

	private SWTBotShell target;

	@Before
	public void before() {
		target = bot.shell(Messages.BoardImportWizard_title);
		target.activate();
	}

	@Test
	public void show() throws Throwable {
		target.isOpen();

		bot.button(IDialogConstants.CANCEL_LABEL).click();
		assertThat(Conditions.shellCloses(target).test(), is(true));
	}

}
