package org.kompiro.jamcircle.kanban.ui.internal.command;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.net.URL;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.mock.Lane;
import org.kompiro.jamcircle.scripting.ScriptTypes;


public class LaneUpdateCommandTest extends AbstractCommandTest{

	private static final String INIT_SCRIPT = null;
	private static final String INIT_STATUS = null;
	private static final String EXPECTED_SCRIPT = "script";
	private static final String EXPECTED_STATUS = "status";
	private Lane lane;
	private LaneUpdateCommand command;
	private File customIcon;

	@Test
	public void initialize() throws Exception {
		Lane lane = null;
		String status = null;
		String script = null;
		ScriptTypes type = null;
		File customIcon = null;
		LaneUpdateCommand command = new LaneUpdateCommand(lane, status, script, type,customIcon );
		command.initialize();
		assertThat(command.canExecute(),is(false));
		lane = mock(Lane.class);
		command = new LaneUpdateCommand(lane, status, script, type, customIcon);
		command.initialize();
		assertThat(command.canExecute(),is(true));
	}
	
	@Override
	public void execute() throws Exception {
		command.execute();
		assertThat(lane.getStatus(),is(EXPECTED_STATUS));
		assertThat(lane.getScript(),is(EXPECTED_SCRIPT));
		assertThat(lane.getScriptType(),is(ScriptTypes.JRuby));
		assertThat(lane.getCustomIcon().getName(),is(customIcon.getName()));
	}
	
	@Override
	public void undo() throws Exception {
		command.execute();
		assertThat(command.canUndo(),is(true));
		command.undo();
		assertThat(lane.getStatus(),is(INIT_STATUS));
		assertThat(lane.getScript(),is(INIT_SCRIPT));
		assertThat(lane.getScriptType(),is(nullValue()));		
		assertThat(lane.getCustomIcon(),is(nullValue()));
	}
	
	@Override
	public void redo() throws Exception {
		command.execute();
		command.undo();
		command.redo();
		
		assertThat(lane.getStatus(),is(EXPECTED_STATUS));
		assertThat(lane.getScript(),is(EXPECTED_SCRIPT));
		assertThat(lane.getScriptType(),is(ScriptTypes.JRuby));
		assertThat(lane.getCustomIcon().getName(),is(customIcon.getName()));
	}
	
	@Override
	protected void createCommand() {
		lane = new Lane();
		String status = EXPECTED_STATUS;
		String script = EXPECTED_SCRIPT;
		ScriptTypes type = ScriptTypes.JRuby;
		URL resource = LaneToggleIconizedCommandTest.class.getResource("kanban_128.gif");
		customIcon = new File(resource.getFile());
		command = new LaneUpdateCommand(lane, status, script, type,customIcon);
		command.initialize();
		
	}

}
