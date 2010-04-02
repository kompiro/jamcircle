package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.view.finder.widgets.*;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.*;
import org.junit.runner.RunWith;
import org.kompiro.jamcircle.kanban.ui.KanbanPerspective;
import org.kompiro.jamcircle.kanban.ui.KanbanView;
import org.kompiro.jamcircle.kanban.ui.model.BoardSelecterModel;
import org.kompiro.jamcircle.kanban.ui.model.InboxIconModel;

@RunWith(SWTBotJunit4ClassRunner.class)
public class EditPartBotSmokeTest{
	
	private SWTBotGefView view;

	@Before
	public void before() throws Exception {
		SWTGefViewBot bot = new SWTGefViewBot();
		bot.perspectiveById(KanbanPerspective.ID).activate();
		SWTBotView activeView = bot.viewById(KanbanView.ID);
		activeView.show();
		
		view = bot.createView(activeView.getReference(),bot);
	}
	
	@After
	public void after() throws Exception {
	}
	
	@Test
	public void exist() throws Exception {
		view.getEditPart(BoardSelecterModel.NAME).focus();
		view.getEditPart(InboxIconModel.NAME).focus();
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
	
}
