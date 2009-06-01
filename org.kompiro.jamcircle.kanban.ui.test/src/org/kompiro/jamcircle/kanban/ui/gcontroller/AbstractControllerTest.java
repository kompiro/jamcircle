package org.kompiro.jamcircle.kanban.ui.gcontroller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.*;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.*;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.junit.*;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.mock.*;
import org.kompiro.jamcircle.kanban.ui.*;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.model.TrashModel;
import org.kompiro.jamcircle.storage.IStatusHandler;

public abstract class AbstractControllerTest {

	private static final int LANE_CREATER_COUNT = 1;
	private static final int TRACHBOX_COUNT = 1;
	public static final int INIT_BOARD_CHIHLDREN_SIZE = TRACHBOX_COUNT;
//		TRACHBOX_COUNT	+ LANE_CREATER_COUNT;

	protected static class TrashMock extends TrashModel {
		public TrashMock() {
			super(new Icon(){
				@Override
				public String getClassType() {
					return TrashModel.class.getName();
				}
			});
		}

		private static final long serialVersionUID = 1L;
		private List<Card> cards = new ArrayList<Card>();

		@Override
		public boolean addCard(Card card) {
			return cards.add(card);
		}

		@Override
		public Card[] getCards() {
			return cards.toArray(new Card[] {});
		}

		@Override
		public boolean isEmpty() {
			return cards.isEmpty();
		}

		@Override
		public boolean removeCard(Card card) {
			return cards.remove(card);
		}

	}

	class LaneMock extends Lane {
		private String status;
		private List<Card> cards = new ArrayList<Card>();
		private int height;
		private int width;

		public LaneMock(String status) {
			this.status = status;
			this.width = 200;
			this.height = 200;
		}

		public String getStatus() {
			return status;
		}

		public int getHeight() {
			return this.height;
		}

		public int getWidth() {
			return this.width;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		@Override
		public boolean addCard(Card card) {
			return cards.add(card);
		}

		@Override
		public boolean removeCard(Card card) {
			return cards.remove(card);
		}

		@Override
		public Card[] getCards() {
			return cards.toArray(new Card[] {});
		}

		@Override
		public boolean containCard(Card card){
			return cards.contains(card);
		}
		

		
		@Override
		public void commitConstraint() {
		}
	}

	private static ScrollingGraphicalViewer viewer;
	private static RootEditPart root;
	protected static Shell shell;

	@BeforeClass
	public static void initialize() throws Exception {
		KanbanUIStatusHandler.addStatusHandler(new IStatusHandler() {

			public void displayStatus(String title, IStatus status) {
				System.out.println(title + ":" + status);
			}

			public void fail(IStatus status, boolean informUser) {
				System.out.println(status);
			}

			public void info(String message) {
				System.out.println(message);
			}

		});
		shell = new Shell();
		viewer = new ScrollingGraphicalViewer() {
			private EditDomain domain;
			{
				domain = new EditDomain();
			}

			public EditDomain getEditDomain() {
				return domain;
			}
		};

		shell.setLayout(new FillLayout());
		// root = new FreeformGraphicalRootEditPart();
		// viewer.setRootEditPart(root);
		root = viewer.getRootEditPart();
		EditDomain editDomain = viewer.getEditDomain();
		CommandStack commandStack = editDomain.getCommandStack();
		commandStack
				.addCommandStackEventListener(new CommandStackEventListenerForDebug());
		openShell();
	}

	@AfterClass
	public static void afterTest() {
		if (shell != null && shell.isVisible() && !shell.isDisposed())
			shell.close();
	}

	protected static void openShell() {
		Composite comp = new Composite(shell, SWT.None);
		comp.setLayout(new FillLayout());
		viewer.createControl(comp);
		shell.setAlpha(0);
		shell.open();
	}

	protected BoardModel board;
	protected BoardEditPart boardPart;
	protected TrashMock trashMock = new TrashMock();

	@Before
	public void init() throws Exception {
		board = new BoardModel(new Board()){
			private static final long serialVersionUID = 4122329778411044605L;
		};
		board.addIcon(trashMock);
		KanbanControllerFactory factory = new KanbanControllerFactory(board);
		viewer.setEditPartFactory(factory);
		viewer.setContents(board);
		assertEquals(1, root.getChildren().size());

		Object obj = root.getChildren().get(0);
		assertTrue(obj instanceof BoardEditPart);
		boardPart = (BoardEditPart) obj;
		assertTrue(boardPart.isActive());
		assertEquals(INIT_BOARD_CHIHLDREN_SIZE, boardPart.getChildren().size());
	}

	protected Map<Object, GraphicalEditPart> getChildlenPartmap(
			GraphicalEditPart parentPart) {
		Map<Object, GraphicalEditPart> partMap = new HashMap<Object, GraphicalEditPart>();
		for (Object o : parentPart.getChildren()) {
			assertTrue(o instanceof GraphicalEditPart);
			GraphicalEditPart part = (GraphicalEditPart) o;
			partMap.put(part.getModel(), part);
		}
		return partMap;
	}

}
