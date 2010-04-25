package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import static org.eclipse.swtbot.eclipse.gef.view.finder.widgets.EditPartOfType.editPartOfType;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.view.finder.widgets.*;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.junit.*;
import org.junit.runner.RunWith;
import org.kompiro.jamcircle.kanban.ui.KanbanPerspective;
import org.kompiro.jamcircle.kanban.ui.KanbanView;
import org.kompiro.jamcircle.kanban.ui.model.*;

@RunWith(SWTBotJunit4ClassRunner.class)
public class EditPartBotSmokeTest{
	
	private static final long TIMEOUT = 10 * 1000;
	private SWTBotGefView view;
	private SWTGefViewBot bot;

	@Before
	public void before() throws Exception {
		System.setProperty(SWTBotPreferenceConstants.KEY_TIMEOUT,"1000");
		bot = new SWTGefViewBot();
		SWTBotView viewById;
		try {
			viewById = bot.viewById("org.eclipse.ui.internal.introview");
			viewById.close();
		} catch (WidgetNotFoundException e) {
		}
		bot.perspectiveById(KanbanPerspective.ID).activate();
		SWTBotView activeView = bot.viewById(KanbanView.ID);
		activeView.getReference().getView(true);
		activeView.show();
		view = bot.createView(activeView.getReference(),bot);
	}
	
	@After
	public void after() throws Exception {
	}
	
	@Test
	public void iconsExist() throws Exception {
		view.getEditPart(BoardSelecterModel.NAME).focus();
		view.getEditPart(InboxIconModel.NAME).focus();
		view.getEditPart(LaneCreaterModel.NAME).focus();
		trashPart().focus();
	}
	
	@Test
	public void laneExist() {
		List<SWTBotGefViewEditPart> laneParts = getLaneViewParts();
		assertThat(laneParts.size(),is(not(0)));

		assertThat(lanePart(0).getLaneModel().getStatus(),is("ToDo"));
		assertThat(lanePart(1).getLaneModel().getStatus(),is("In Progress"));
		assertThat(lanePart(2).getLaneModel().getStatus(),is("Done"));
	}
	
	@Test
	public void laneMove() throws Exception {
		view.drag(editLanePart(1), 700, 0);
	}
	
	@Test
	public void laneStatusChange() throws Exception {
		lanePart(0).getLaneModel().setStatus("Hello");
		view.getEditPart("Hello").focus();
	}
	
	@Test
	public void laneIconized() throws Exception {
		SWTBotGefViewEditPart target = getLaneViewParts().get(2);
		((LaneEditPart)target.part()).getLaneModel().setIconized(true);
		view.drag(target, 10, 200);
	}

	@Test
	@Ignore
	public void cardTest() throws Exception {
		createCard();
		
		// simulated Delete Request 
		// (why: can't execute any shortcut request during test stage.it'll execute after test stage like these command)
		// bot.activeShell().pressShortcut(KeyStroke.getInstance(IKeyLookup.DEL_NAME));
		GroupRequest request = new GroupRequest();
		request.setType(RequestConstants.REQ_ORPHAN_CHILDREN);
		request.setEditParts(getCardViewParts().get(0).part());
		((BoardEditPart)getViewBoardPart().part()).getCommand(request).execute();
		assertThat(getCardViewParts().size(),is(0));

		moveCard();
	}

	private void moveCard() throws Exception {
		createCard();
		assertThat(getCardViewParts().size(),is(1));
		view.drag(getCardViewParts().get(0), 100, 200);
		createCardOnLane();
		assertThat(getCardViewParts().size(),is(2));
	}
	
	public void createCard() throws Exception {
		SWTBotGefViewEditPart viewBoardPart = getViewBoardPart();
		assertThat(viewBoardPart.part(),instanceOf(BoardEditPart.class));
		waitForActiveShell();
		viewBoardPart.doubleClick(new Point(1000,200));
		waitForActiveShell();
		assertThat(getCardViewParts().size(),is(1));
	}

	private SWTBotGefViewEditPart getViewBoardPart() {
		SWTBotGefViewEditPart viewBoardPart = view.editParts(editPartOfType(BoardEditPart.class)).get(0);
		return viewBoardPart;
	}

	public void createCardOnLane() throws Exception {
		SWTBotGefViewEditPart viewBoardPart = getLaneViewParts().get(0);
		waitForActiveShell();
		viewBoardPart.doubleClick();
		waitForActiveShell();
	}

	@Test
	@Ignore
	public void createLane() throws Exception {
		int oldSize = getLaneViewParts().size();
		view.getEditPart(LaneCreaterModel.NAME).doubleClick();
		assertThat(getLaneViewParts().size(),is(oldSize + 1));
	}
	
	private SWTBotGefViewEditPart editLanePart(int index){
		List<SWTBotGefViewEditPart> laneParts = view.editParts(editPartOfType(LaneEditPart.class));
		return laneParts.get(index);		
	}

	private LaneEditPart lanePart(int index) {
		List<SWTBotGefViewEditPart> laneParts = view.editParts(editPartOfType(LaneEditPart.class));
		return (LaneEditPart)laneParts.get(index).part();
	}


	private SWTBotGefViewEditPart trashPart() {
		List<SWTBotGefViewEditPart> trashParts = view.editParts(EditPartOfType.editPartOfType(TrashEditPart.class));
		assertThat(trashParts.size(), is(1));
		SWTBotGefViewEditPart trashPart = trashParts.get(0);
		return trashPart;
	}

	@Test
	public void moveBoardSelect() throws Exception {
		view.drag(BoardSelecterModel.NAME, 200, 0);
		SWTBotGefViewEditPart target = view.getEditPart(BoardSelecterModel.NAME);
		EditPart part = target.part();
		assertThat(part,is(notNullValue()));
		Object targetModel = part.getModel();
		assertThat(targetModel,instanceOf(BoardSelecterModel.class));
		BoardSelecterModel model = (BoardSelecterModel)targetModel;
		assertThat(model.getLocation(),is(new Point(200,0)));
	}

	@Test
	public void moveINBOX() throws Exception {
		view.drag(InboxIconModel.NAME, 300, 0);
		SWTBotGefViewEditPart target = view.getEditPart(InboxIconModel.NAME);
		EditPart part = target.part();
		assertThat(part,is(notNullValue()));
		Object targetModel = part.getModel();
		assertThat(targetModel,instanceOf(InboxIconModel.class));
		InboxIconModel model = (InboxIconModel)targetModel;
		assertThat(model.getLocation(),is(new Point(300,0)));
	}
	
	private List<SWTBotGefViewEditPart> getLaneViewParts() {
		return view.editParts(editPartOfType(LaneEditPart.class));
	}

	private List<SWTBotGefViewEditPart> getCardViewParts() {
		return view.editParts(editPartOfType(CardEditPart.class));
	}

	private void waitForActiveShell() {
		bot.waitUntil(Conditions.waitForShell(is(getActiveShell())), TIMEOUT);
	}

	private Shell getActiveShell() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}
	
}
