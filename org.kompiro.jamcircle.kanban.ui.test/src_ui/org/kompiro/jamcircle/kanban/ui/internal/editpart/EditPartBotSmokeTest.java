package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.kompiro.swtbot.extension.eclipse.gef.finder.EditPartOfType.editPartOfType;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.jface.bindings.keys.IKeyLookup;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.*;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferenceConstants;
import org.eclipse.swtbot.swt.finder.waits.WaitForObjectCondition;
import org.eclipse.ui.PlatformUI;
import org.hamcrest.Matcher;
import org.junit.*;
import org.junit.runner.RunWith;
import org.kompiro.jamcircle.kanban.ui.KanbanPerspective;
import org.kompiro.jamcircle.kanban.ui.KanbanView;
import org.kompiro.jamcircle.kanban.ui.model.*;
import org.kompiro.jamcircle.kanban.ui.util.IMonitorDelegator;

@RunWith(SWTBotJunit4ClassRunner.class)
public class EditPartBotSmokeTest {

	public class WaitForObjectConditionExtension extends WaitForObjectCondition<Object> {
		private SWTBotGefViewer viewer;

		public WaitForObjectConditionExtension(SWTBotGefViewer viewer, Matcher<Object> matcher) {
			super(matcher);
			this.viewer = viewer;
		}

		public String getFailureMessage() {
			return "Could not find edit part in selected edit part list"; //$NON-NLS-1$
		}

		@Override
		protected List<Object> findMatches() {
			List<Object> result = new ArrayList<Object>();
			SWTBotGefEditPart target = viewer.getEditPart(TrashModel.NAME);
			if (matcher.matches(target.part())) {
				result.add(target);
			}
			return result;
		}
	}

	private static final long TIMEOUT = 10 * 1000;
	private static SWTGefBot bot;
	private static IMonitorDelegator backup;
	private static SWTBotGefViewer viewer;
	private static SWTBotGefView view;

	@BeforeClass
	public static void before() throws Exception {
		backup = KanbanView.getDelegator();
		KanbanView.setDelegator(new IMonitorDelegator.DirectExecute());

		System.setProperty(SWTBotPreferenceConstants.KEY_TIMEOUT, "10000");
		bot = new SWTGefBot();
		SWTBotView viewById;
		viewById = bot.activeView();
		try {
			viewById.close();
		} catch (WidgetNotFoundException e) {
		}
		bot.perspectiveById(KanbanPerspective.ID).activate();
		SWTBotView kanbanView = bot.viewById(KanbanView.ID);
		kanbanView.show();
		view = bot.gefView(kanbanView.getTitle());
		viewer = view.getSWTBotGefViewer();
	}

	@AfterClass
	public static void after() throws Exception {
		KanbanView.setDelegator(backup);
	}

	@Test
	public void iconsExist() throws Exception {
		viewer.getEditPart(BoardSelecterModel.NAME).focus();
		viewer.getEditPart(InboxIconModel.NAME).focus();
		viewer.getEditPart(LaneCreaterModel.NAME).focus();
		bot.waitUntil(new WaitForObjectConditionExtension(viewer, instanceOf(TrashEditPart.class)));
		trashPart().focus();
	}

	@Test
	public void laneExist() {
		List<SWTBotGefEditPart> laneParts = getLaneViewParts();
		assertThat(laneParts.size(), is(not(0)));

		assertThat(lanePart(0).getLaneModel().getStatus(), is("ToDo"));
		assertThat(lanePart(1).getLaneModel().getStatus(), is("In Progress"));
		assertThat(lanePart(2).getLaneModel().getStatus(), is("Done"));
	}

	@Test
	public void laneMove() throws Exception {
		viewer.drag(editLanePart(1), 700, 0);
	}

	@Test
	public void laneStatusChange() throws Exception {
		lanePart(0).getLaneModel().setStatus("Hello");
		viewer.getEditPart("Hello").focus();
	}

	@Test
	public void laneIconized() throws Exception {
		SWTBotGefEditPart target = getLaneViewParts().get(2);
		((LaneEditPart) target.part()).getLaneModel().setIconized(true);
		viewer.drag(target, 10, 200);
	}

	@Test
	@Ignore
	public void cardTest() throws Exception {
		createCard();

		// simulated Delete Request
		// (why: can't execute any shortcut request during test stage.it'll
		// execute after test stage like these command)
		//
		bot.activeShell().pressShortcut(KeyStroke.getInstance(IKeyLookup.DEL_NAME));
		GroupRequest request = new GroupRequest();
		request.setType(RequestConstants.REQ_ORPHAN_CHILDREN);
		request.setEditParts(getCardViewParts().get(0).part());
		((BoardEditPart) getViewBoardPart().part()).getCommand(request).execute();
		assertThat(getCardViewParts().size(), is(0));

		moveCard();
	}

	private void moveCard() throws Exception {
		createCard();
		assertThat(getCardViewParts().size(), is(1));
		viewer.drag(getCardViewParts().get(0), 100, 200);
		createCardOnLane();
		assertThat(getCardViewParts().size(), is(2));
	}

	public void createCard() throws Exception {
		SWTBotGefEditPart viewBoardPart = getViewBoardPart();
		assertThat(viewBoardPart.part(), instanceOf(BoardEditPart.class));
		waitForActiveShell();
		viewer.doubleClick(1000, 200);
		waitForActiveShell();
		assertThat(getCardViewParts().size(), is(1));
	}

	private SWTBotGefEditPart getViewBoardPart() {
		SWTBotGefEditPart viewBoardPart =
				viewer.editParts(editPartOfType(BoardEditPart.class)).get(0);
		return viewBoardPart;
	}

	public void createCardOnLane() throws Exception {
		SWTBotGefEditPart viewBoardPart = getLaneViewParts().get(0);
		waitForActiveShell();
		viewBoardPart.doubleClick();
		waitForActiveShell();
	}

	@Test
	public void createLane() throws Exception {
		int oldSize = getLaneViewParts().size();
		viewer.getEditPart(LaneCreaterModel.NAME).doubleClick();
		assertThat(getLaneViewParts().size(), is(oldSize + 1));
	}

	private SWTBotGefEditPart editLanePart(int index) {
		List<SWTBotGefEditPart> laneParts =
				viewer.editParts(editPartOfType(LaneEditPart.class));
		return laneParts.get(index);
	}

	private LaneEditPart lanePart(int index) {
		List<SWTBotGefEditPart> laneParts =
				viewer.editParts(editPartOfType(LaneEditPart.class));
		return (LaneEditPart) laneParts.get(index).part();
	}

	private SWTBotGefEditPart trashPart() {
		return viewer.getEditPart(TrashModel.NAME);
	}

	@Test
	public void moveBoardSelect() throws Exception {
		viewer.drag(BoardSelecterModel.NAME, 200, 0);
		SWTBotGefEditPart target = viewer.getEditPart(BoardSelecterModel.NAME);
		EditPart part = target.part();
		assertThat(part, is(notNullValue()));
		Object targetModel = part.getModel();
		assertThat(targetModel, instanceOf(BoardSelecterModel.class));
		BoardSelecterModel model = (BoardSelecterModel) targetModel;
		assertThat(model.getLocation(), is(new Point(200, 0)));
	}

	@Test
	public void moveINBOX() throws Exception {
		viewer.drag(InboxIconModel.NAME, 300, 0);
		SWTBotGefEditPart target = viewer.getEditPart(InboxIconModel.NAME);
		EditPart part = target.part();
		assertThat(part, is(notNullValue()));
		Object targetModel = part.getModel();
		assertThat(targetModel, instanceOf(InboxIconModel.class));
		InboxIconModel model = (InboxIconModel) targetModel;
		assertThat(model.getLocation(), is(new Point(300, 0)));
	}

	private List<SWTBotGefEditPart> getLaneViewParts() {
		return viewer.editParts(editPartOfType(LaneEditPart.class));
	}

	private List<SWTBotGefEditPart> getCardViewParts() {
		return viewer.editParts(editPartOfType(CardEditPart.class));
	}

	private void waitForActiveShell() {
		bot.waitUntil(Conditions.waitForShell(is(getActiveShell())), TIMEOUT);
	}

	private Shell getActiveShell() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}

}
