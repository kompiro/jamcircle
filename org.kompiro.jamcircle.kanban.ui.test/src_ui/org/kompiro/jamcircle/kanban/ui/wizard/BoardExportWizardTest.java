package org.kompiro.jamcircle.kanban.ui.wizard;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kompiro.jamcircle.kanban.ui.dialog.DialogBotThread;
import org.kompiro.jamcircle.kanban.ui.wizard.BoardExportWizard;

@RunWith(SWTBotJunit4ClassRunner.class)
public class BoardExportWizardTest {

	@Test
	public void show() throws Throwable {
		Shell parentShell = new Shell();
		IWizard newWizard = new BoardExportWizard();
		final WizardDialog dialog = new WizardDialog(parentShell, newWizard);
		Throwable[] ex = new Throwable[1];
		new DialogBotThread(dialog, ex, new Runnable() {

			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				SWTBot bot = new SWTBot(dialog.getShell());
				bot.button("Cancel").click();
			}
		}).start();
		dialog.open();
		if (ex[0] != null)
			throw ex[0];

	}

	public static void main(String[] args) {
		final Shell parentShell = new Shell();
		IWizard newWizard = new BoardExportWizard();
		WizardDialog dialog = new WizardDialog(parentShell, newWizard);
		dialog.open();
	}

}
