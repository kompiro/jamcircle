package org.kompiro.jamcircle.kanban.ui.dialog;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.kompiro.jamcircle.kanban.ui.dialog.LaneEditDialog.*;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.Test;
import org.kompiro.jamcircle.scripting.ScriptTypes;


public class LaneEditDialogTest  {

	@Test
	public void show() throws Throwable {
		Shell shell = null;
		final LaneEditDialog dialog = new LaneEditDialog(shell,
				"initialize_title",
				"initialize_script",
				ScriptTypes.JRuby,
				null);
		Throwable[] ex = new Throwable[1];
		new DialogBotThread(dialog,ex ,new Runnable() {
			
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				SWTBot bot = new SWTBot(dialog.getShell());
				SWTBotText titleTextWidget = bot.textWithId(ID_STATUS);
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

		assertThat(dialog.getStatus(),is("modified"));
		assertThat(dialog.getScript(),is("modified_script"));
		assertThat(dialog.getScriptType(),is(ScriptTypes.JavaScript));
	}

	
	public static void main(String[] args) throws Exception {
		Shell shell = new Shell();
		URL resource = LaneEditDialogTest.class.getResource("trac_bullet.png");
		String asciiString = resource.getFile();
		File file = new File(asciiString);
		LaneEditDialog dialog = new LaneEditDialog(shell,
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest",
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest",
				ScriptTypes.JRuby,
				file);
		dialog.open();
	}

}
