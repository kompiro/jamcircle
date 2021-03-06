package org.kompiro.jamcircle.kanban.ui.dialog;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.kompiro.jamcircle.kanban.ui.dialog.BoardEditDialog.*;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kompiro.jamcircle.scripting.ScriptTypes;


@RunWith(SWTBotJunit4ClassRunner.class)
public class BoardEditDialogTest {
	
	@Test
	public void show() throws Throwable {
		Shell shell = null;
		final BoardEditDialog dialog = new BoardEditDialog(shell,
				"initialize_title",
				"initialize_script",
				ScriptTypes.JRuby);
		Throwable[] ex = new Throwable[1];
		new DialogBotThread(dialog,ex ,new Runnable() {
			
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				SWTBot bot = new SWTBot(dialog.getShell());
				SWTBotText titleTextWidget = bot.textWithId(ID_TITLE);
				assertThat(titleTextWidget.getText(),is("initialize_title"));
				titleTextWidget.setText("modified");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}

				SWTBotText scriptTextWidget = bot.textWithId(ID_SCRIPT);
				scriptTextWidget.setText("modified_script");
				
				SWTBotCombo scriptTypeWidget = bot.comboBoxWithId(ID_SCRIPT_TYPE);
				assertThat(scriptTypeWidget.selection(),is("JRuby"));
				scriptTypeWidget.setSelection(0);
				bot.button("OK").click();
			}
		}).start();
		dialog.open();
		if(ex[0] != null) throw ex[0];

		assertThat(dialog.getTitle(),is("modified"));
		assertThat(dialog.getScript(),is("modified_script"));
		assertThat(dialog.getScriptType(),is(ScriptTypes.JavaScript));
	}
	
	public static void main(String[] args) {
		Shell shell = new Shell();
		BoardEditDialog dialog = new BoardEditDialog(shell,
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest",
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest",
				ScriptTypes.JRuby);
		dialog.open();
	}

	
}
