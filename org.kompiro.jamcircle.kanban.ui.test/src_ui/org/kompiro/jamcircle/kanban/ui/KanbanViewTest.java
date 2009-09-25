package org.kompiro.jamcircle.kanban.ui;


public class KanbanViewTest {

//	private FigureCanvasTester tester;
//
//	@Before
//	public void init() throws Exception{
//		WorkbenchUtilities.hideWelcomeView();
//		IWorkbenchWindow window = WorkbenchUtilities.getActiveWindow(true);
//		Shell shell = window.getShell();
//		shell.setSize(1024, 800);
//		shell.setLocation(0, 0);
//		WorkbenchUtilities.getWorkbench().showPerspective("org.kompiro.jamcircle.kanban.ui.perspective.kanban", window);
//		tester = new FigureCanvasTester(1,1);
//		tester.setAutoDelay(1000);
//	}
//
//	@AfterClass
//	public static void finish() throws Exception{
//		KanbanService service = KanbanUIActivator.getDefault().getKanbanService();
//		service.deleteAllCards();
//	}
//
//	private boolean finished = false;
//	
//	@Test
//	public void addCard() throws Exception {
//		final Display display = Display.findDisplay(Thread.currentThread());
//
//		new Thread(new Runnable() {
//			public void run() {
//				tester.mouseMove(850, 200);
//				tester.doubleClick(850, 200);
//				finished = true;
//			}
//		}).start();
//		while (!finished) {
//			if (!display.readAndDispatch()) {
//				display.sleep();
//			}
//		}
//		GraphicalViewer viewer = getGraphicalViewer();
//		RootEditPart rootEditPart = GEFWorkbenchUtilities.getRootEditPart(viewer);
//		BoardEditPart boardPart = (BoardEditPart) rootEditPart.getContents();
//		assertTrue(boardPart instanceof BoardEditPart);
//		assertSame(1,boardPart.getBoardModel().getCards().length);
//	}
//
//	@Test
//	public void moveCard() throws Exception {
//		new Thread(new Runnable() {
//			public void run() {
//				tester.mouseMove(850, 400);
//				tester.doubleClick(850, 400);
//				tester.actionDragDrop(860, 410, 660, 410, SWT.BUTTON1);
//				tester.sleep(2000);
//				finished = true;
//			}
//		}).start();
//		while (!finished) {
//			if (!Display.getDefault().readAndDispatch()) {
//				Display.getDefault().sleep();
//			}
//		}
//		GraphicalViewer viewer = getGraphicalViewer();
//		RootEditPart rootEditPart = GEFWorkbenchUtilities.getRootEditPart(viewer);
//		BoardEditPart boardPart = (BoardEditPart) rootEditPart.getContents();
//		assertTrue(boardPart instanceof BoardEditPart);
//		assertSame(1,boardPart.getBoardModel().getLane(2).getCards().length);
//	}
//	
//	@Test
//	public void moveCardToEveryLane() throws Exception {
//		final Display display = Display.findDisplay(Thread.currentThread());
//
//		new Thread(new Runnable() {
//			public void run() {
//				tester.mouseMove(850, 600);
//				tester.doubleClick(850, 600);
//				tester.actionDragDrop(860, 610, 660, 210, SWT.BUTTON1);
//				tester.actionDragDrop(670, 220, 460, 250, SWT.BUTTON1);
//				tester.actionDragDrop(470, 260, 260, 310, SWT.BUTTON1);
//				tester.sleep(2000);
//				finished = true;
//			}
//		}).start();
//		while (!finished) {
//			if (!display.readAndDispatch()) {
//				display.sleep();
//			}
//		}
//		GraphicalViewer viewer = getGraphicalViewer();
//		RootEditPart rootEditPart = GEFWorkbenchUtilities.getRootEditPart(viewer);
//		BoardEditPart boardPart = (BoardEditPart) rootEditPart.getContents();
//		assertTrue(boardPart instanceof BoardEditPart);
//		assertSame(1,boardPart.getBoardModel().getLane(0).getCards().length);
//	}
//	
//	private GraphicalViewer getGraphicalViewer() {
//		return (GraphicalViewer) WorkbenchUtilities.getWorkbenchWindow().getActivePage().getActivePart().getAdapter(GraphicalViewer.class);
//	}

}
