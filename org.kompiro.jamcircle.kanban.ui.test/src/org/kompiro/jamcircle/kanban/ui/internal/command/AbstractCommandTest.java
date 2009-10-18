package org.kompiro.jamcircle.kanban.ui.internal.command;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractCommandTest {
	
	@Before
	public void before() throws Exception{
		createCommand();
	}
	
	protected abstract void createCommand();

	@Test
	public void initialize() throws Exception{
		throw new NotImplementedException();
	}

	@Test
	public void execute() throws Exception{
		throw new NotImplementedException();
	} 

	@Test
	public void undo() throws Exception{
		throw new NotImplementedException();
	} 

	@Test
	public void redo() throws Exception{
		throw new NotImplementedException();
	} 

}
