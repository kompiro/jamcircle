package org.kompiro.jamcircle.kanban.command;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.ColorTypes;
import org.kompiro.jamcircle.kanban.model.HasColorTypeEntity;


public class ChangeColorCommandTest extends AbstractCommandTest{

	private HasColorTypeEntity entity;
	private ColorTypes type;
	private ChangeColorCommand command;

	@Test
	public void initialize() throws Exception {
		HasColorTypeEntity entity = null;
		ColorTypes type = null;
		ChangeColorCommand command = new ChangeColorCommand(entity, type);
		command.initialize();
		assertThat(command.canExecute(),is(false));
		entity = mock(HasColorTypeEntity.class);
		type = ColorTypes.YELLOW;
		command = new ChangeColorCommand(entity, type);
		command.initialize();
		assertThat(command.canExecute(),is(true));
	}
	
	@Override
	public void execute() throws Exception {
		command.execute();
		verify(entity).setColorType(type);
	}
	
	@Override
	public void undo() throws Exception {
		assertThat(command.canUndo(),is(false));
		command.execute();
		assertThat(command.canUndo(),is(true));
		command.undo();
		verify(entity).setColorType(null);
	}
	
	@Override
	public void redo() throws Exception {
		command.execute();
		command.undo();
		command.redo();
		verify(entity,times(2)).setColorType(type);
	}
	
	@Override
	protected void createCommand() {
		entity = mock(HasColorTypeEntity.class);
		type = ColorTypes.YELLOW;
		command = new ChangeColorCommand(entity, type);		
	}
	

}
