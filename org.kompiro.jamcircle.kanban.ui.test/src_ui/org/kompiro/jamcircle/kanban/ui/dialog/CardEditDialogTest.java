package org.kompiro.jamcircle.kanban.ui.dialog;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withId;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.kompiro.jamcircle.kanban.ui.dialog.CardEditDialog.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.text.DateFormat;
import java.util.*;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotList;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kompiro.jamcircle.kanban.model.Card;


@RunWith(SWTBotJunit4ClassRunner.class)
public class CardEditDialogTest {

	CardEditDialog dialog;
	private SWTBot bot;
	protected Date targetDate = new Date();
	private DateFormat df = DateFormat.getDateInstance();
	
	@Before
	public void before() throws Exception {
		Shell shell = null;
		Card card = mock(Card.class);
		when(card.getSubject()).thenReturn("initialized_subject");
		when(card.getContent()).thenReturn("initialized_content");

		ArrayList<File> files = new ArrayList<File>();

		File file1 = mock(File.class);
		when(file1.getName()).thenReturn("file1.txt");
		files.add(file1);

		File file2 = mock(File.class);
		when(file2.getName()).thenReturn("file2.jpg");
		files.add(file2);

		File file3 = mock(File.class);
		when(file3.getName()).thenReturn("file3.png");
		files.add(file3);

		when(card.getFiles()).thenReturn(files);
		
		dialog = new CardEditDialog(shell, card);		
	}
	
	@Test
	public void show() throws Throwable {
		Throwable[] ex = new Throwable[1];

		new DialogBotThread(dialog,ex,new Runnable() {
			
			public void run() {
				waitForOpenDialog();
				bot = new SWTBot(dialog.getShell());
				SWTBotText titleTextWidget = bot.textWithId(ID_SUBJECT);
				assertThat(titleTextWidget.getText(),is("initialized_subject"));
				
				SWTBotText scriptTextWidget = bot.textWithId(ID_CONTENT);
				assertThat(scriptTextWidget.getText(),is("initialized_content"));

				bot.cTabItemWithId(ID_CONTENTS_BROWSER_TAB).activate();
				Widget browserTarget = bot.widget(withId(ID_CONTENTS_BROWSER));
				assertThat(browserTarget,is(instanceOf(Browser.class)));

				final Browser browser = (Browser) browserTarget;
				String browserString = UIThreadRunnable.syncExec(new Result<String>() {
					public String run() {
						return browser.getText();
					}
				});
				assertThat(browserString,is(not("<HTML><BODY></BODY></HTML>")));
				bot.cTabItemWithId(ID_CONTENT_TAB).activate();

				
				assertThat(df.format(bot.dateTimeWithId(ID_DUE_DATE).getDate()), is(df.format(new Date())));
				bot.cTabItemWithId(ID_FILES_TAB).activate();

				SWTBotList fileList = bot.listWithId(ID_FILE_LIST);
				String[] items = fileList.getItems();
				assertThat(items[0],is("file1.txt"));
				assertThat(items[1],is("file2.jpg"));
				assertThat(items[2],is("file3.png"));
								
				bot.cTabItemWithId(ID_DUE_TAB).activate();

				bot.button("OK").click();
			}
			
		}).start();
		dialog.open();
		if(ex[0] != null) throw ex[0];
	}
	
	@Test
	public void modify() throws Throwable {
		
		Throwable[] ex = new Throwable[1];

		new DialogBotThread(dialog,ex,new Runnable() {
			
			public void run() {
				waitForOpenDialog();
				bot = new SWTBot(dialog.getShell());
				SWTBotText titleTextWidget = bot.textWithId(ID_SUBJECT);
				titleTextWidget.setText("modified");
				
				SWTBotText scriptTextWidget = bot.textWithId(ID_CONTENT);
				scriptTextWidget.setText("modified_script");

				bot.cTabItemWithId(ID_CONTENTS_BROWSER_TAB).activate();
				
				Widget browserTarget = bot.widget(withId(ID_CONTENTS_BROWSER));
				final Browser browser = (Browser) browserTarget;
				String browserString = UIThreadRunnable.syncExec(new Result<String>() {
					public String run() {
						return browser.getText();
					}
				});
				assertThat(browserString,is(not("<HTML><BODY></BODY></HTML>")));
				bot.dateTimeWithId(ID_DUE_DATE).setDate(targetDate);
				bot.cTabItemWithId(ID_FILES_TAB).activate();

				SWTBotList fileList = bot.listWithId(ID_FILE_LIST);
				fileList.select(1);
				
				bot.buttonWithId(ID_DELETE_BUTTON).click();
				assertThat(fileList.itemCount(),is(2));
				
				bot.cTabItemWithId(ID_DUE_TAB).activate();

				bot.button("OK").click();
			}
			
		}).start();
		dialog.open();
		if(ex[0] != null) throw ex[0];		
		assertThat(dialog.getSubjectText(),is("modified"));
		assertThat(dialog.getContentText(),is("modified_script"));
		String dialogDate = df.format(dialog.getDueDate());
		assertThat(dialogDate,is(df.format(targetDate)));
		assertThat(dialog.getFiles().size(),is(2));
	}

	
	private void waitForOpenDialog() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
	}

	public static void main(String[] args) {
		Shell shell = new Shell();
		List<File> files = new ArrayList<File>();
		files.add(new File(System.getProperty("user.home")));
		files.add(new File(System.getProperty("user.home")));
		files.add(new File(System.getProperty("user.home")));
		files.add(new File(System.getProperty("user.home")));
		files.add(new File(System.getProperty("user.home")));
		files.add(new File(System.getProperty("user.home")));
		Card card = new org.kompiro.jamcircle.kanban.model.mock.Card();
		card.setSubject("testtesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest");
		card.setContent("testtesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest");
		for(File file : files){
			card.addFile(file);
		}
		card.setDueDate(new Date());
		CardEditDialog dialog = new CardEditDialog(shell,card);
		dialog.open();
		System.out.println(dialog.getFiles());
	}

}
