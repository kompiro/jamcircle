package org.kompiro.jamcircle.kanban.ui.dialog;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kompiro.jamcircle.scripting.ScriptTypes;


@RunWith(SWTBotJunit4ClassRunner.class)
public class BoardEditDialogTest {
	
	@Test
	public void show() throws Exception {
		Shell shell = null;
		final BoardEditDialog dialog = new BoardEditDialog(shell,
				"initialize_title",
				"initialize_script",
				ScriptTypes.JRuby);
		new Thread(new Runnable() {
			
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				SWTBot bot = new SWTBot(dialog.getShell());
				SWTBotText titleTextWidget = bot.textInGroup("Title");
				assertThat(titleTextWidget.getText(),is("initialize_title"));
				titleTextWidget.setText("modified");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}

				SWTBotText scriptTextWidget = bot.textInGroup("script");
				scriptTextWidget.setText("modified_script");
				bot.button("OK").click();
				assertThat(dialog.getTitle(),is("modified"));
				assertThat(dialog.getScript(),is("modified_script"));
			}
		}).start();
		dialog.open();
	}
	
}
