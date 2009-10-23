package org.kompiro.jamcircle.kanban.ui.internal.command;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.draw2d.geometry.Rectangle;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.mock.Lane;


public class ChangeLaneConstraintCommandTest extends AbstractCommandTest{

	private static final int EXCEPTED_X = 30;
	private static final int EXCEPTED_Y = 100;
	private static final int INITIALIZE_X = 500;
	private static final int INITIALIZE_Y = 0;
	private Lane lane;
	private ChangeLaneConstraintCommand command;
	private Rectangle rect;

	@Test
	public void initialize() throws Exception {
		ChangeLaneConstraintCommand command = new ChangeLaneConstraintCommand();
		assertThat(command.canUndo(),is(false));
		command.setModel(new Lane());
		command.setRectangle(new Rectangle());
		assertThat(command.canExecute(),is(true));
	}
	
	@Override
	public void execute() throws Exception {
		command.execute();
		assertThat(lane.getX(),is(EXCEPTED_X));
		assertThat(lane.getY(),is(EXCEPTED_Y));
	}

	@Override
	public void undo() throws Exception {
		command.execute();
		assertThat(command.canUndo(),is(true));
		command.undo();
		assertThat(lane.getX(),is(INITIALIZE_X));
		assertThat(lane.getY(),is(INITIALIZE_Y));
	}
	
	@Override
	public void redo() throws Exception {
		command.execute();
		command.undo();
		command.redo();
		assertThat(lane.getX(),is(EXCEPTED_X));
		assertThat(lane.getY(),is(EXCEPTED_Y));
	}
	
	@Override
	protected void createCommand() {
		command = new ChangeLaneConstraintCommand();
		lane = new Lane();
		lane.setX(INITIALIZE_X);
		lane.setY(INITIALIZE_Y);
		command.setModel(lane);
		rect = new Rectangle();
		rect.x = EXCEPTED_X;
		rect.y = EXCEPTED_Y;
		command.setRectangle(rect);
		command.initialize();
	}
	
}
