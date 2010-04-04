package org.kompiro.jamcircle.kanban.ui.widget;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.kompiro.jamcircle.kanban.ui.widget.BoardListTableViewer.ID_BOARD_LIST;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.ui.Messages;


public class BoardListTableViewerTest {

	@Test
	public void show() throws Exception {
		Shell shell = new Shell();
		shell.setLayout(new FillLayout());

		BoardListTableViewer viewer = new BoardListTableViewer(shell);
		List<Board> boards = new ArrayList<Board>();
		for (int i = 0; i < 10; i++) {
			Board board = mock(Board.class);
			when(board.getTitle()).thenReturn(format(Messages.BoardListTableViewer_mock_name,i));
			boards.add(board);
		}
		viewer.setInput(boards.toArray(new Board[] {}));
		shell.pack();
		shell.open();
		SWTBot bot = new SWTBot(shell);
		SWTBotTable table = bot.tableWithId(ID_BOARD_LIST);
		assertThat(table.cell(0, 0),is("0"));
		assertThat(table.cell(1, 1),is("Board List 1"));
	}
	
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		BoardListTableViewer viewer = new BoardListTableViewer(shell);
		List<Board> boards = new ArrayList<Board>();
		for (int i = 0; i < 10; i++) {
			Board board = mock(Board.class);
			when(board.getTitle()).thenReturn(format(Messages.BoardListTableViewer_mock_name,i));
			when(board.getID()).thenReturn(i);
			boards.add(board);
		}
		viewer.setInput(boards.toArray(new Board[] {}));
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

}
