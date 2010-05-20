package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;

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
import org.kompiro.jamcircle.debug.IStatusHandler;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.model.mock.Board;
import org.kompiro.jamcircle.kanban.model.mock.Icon;
import org.kompiro.jamcircle.kanban.ui.CommandStackEventListenerForDebug;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.model.TrashModel;

public abstract class AbstractEditPartTest {

//	private static final int LANE_CREATER_COUNT = 1;
	private static final int TRACHBOX_COUNT = 1;
	public static final int INIT_BOARD_CHIHLDREN_SIZE = TRACHBOX_COUNT;
//		TRACHBOX_COUNT	+ LANE_CREATER_COUNT;

	/**
	 * Jump asyncrouns execution because these are not testable.
	 */
	private final class JumpAsyncBoardModel extends BoardModel {
		private static final long serialVersionUID = 4122329778411044605L;

		private JumpAsyncBoardModel(
				org.kompiro.jamcircle.kanban.model.Board board) {
			super(board);
		}

		@Override
		public boolean addCard(Card card) {
			return boardEntity.addCard(card);
		}

		@Override
		public boolean removeCard(Card card) {
			return boardEntity.removeCard(card);
		}
		
	}

	protected static class TrashMock extends TrashModel {
		public TrashMock() {
			super(new Icon(){
				@Override
				public String getClassType() {
					return TrashModel.class.getName();
				}
			},null);
		}

		private static final long serialVersionUID = 1L;
		private List<Card> cards = new ArrayList<Card>();
		private List<Lane> lanes = new ArrayList<Lane>();

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
			return cards.isEmpty() && lanes.isEmpty();
		}

		@Override
		public boolean removeCard(Card card) {
			return cards.remove(card);
		}
		
		@Override
		public boolean addLane(org.kompiro.jamcircle.kanban.model.Lane lane) {
			return lanes.add(lane);
		}
		
		@Override
		public boolean removeLane(Lane lane) {
			return lanes.remove(lane);
		}
		
		@Override
		public Lane[] getLanes() {
			return lanes.toArray(new Lane[]{});
		}

	}

	class LaneMock extends org.kompiro.jamcircle.kanban.model.mock.Lane {
		static final int INIT_WIDTH = 200;
		static final int INIT_HEIGHT = 200;

		private String status;
		private List<Card> cards = new ArrayList<Card>();
		private int height;
		private int width;

		public LaneMock(String status) {
			this.status = status;
			this.width = INIT_WIDTH;
			this.height = INIT_HEIGHT;
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
		public void commitConstraint(Object bounds) {
		}
	}

	protected static ScrollingGraphicalViewer viewer;
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
//		shell.open();
	}

	protected BoardModel board;
	protected BoardEditPart boardPart;
	protected TrashMock trashMock = new TrashMock();
	private Board boardEntity;

	@Before
	public void init() throws Exception {
		boardEntity = new Board();
		board = new JumpAsyncBoardModel(boardEntity);
		boardEntity.addPropertyChangeListener(board);

		board.addIcon(trashMock);
		KanbanUIEditPartFactory factory = new KanbanUIEditPartFactory(board,new IPropertyChangeDelegatorForTest());
		viewer.setEditPartFactory(factory);
		viewer.setContents(board);
		assumeThat(root.getChildren().size(), is(1));

		Object obj = root.getChildren().get(0);
		assertTrue(obj instanceof BoardEditPart);
		boardPart = (BoardEditPart) obj;
		assumeTrue(boardPart.isActive());
		assumeThat(boardPart.getChildren().size(), is(INIT_BOARD_CHIHLDREN_SIZE));
	}
	
	@Test
	public void testname() throws Exception {
		
		
		
	}

	@After
	public void after() throws Exception {
		boardEntity.removePropertyChangeListener(board);		
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
