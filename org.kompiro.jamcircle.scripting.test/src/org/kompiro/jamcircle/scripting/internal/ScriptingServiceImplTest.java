package org.kompiro.jamcircle.scripting.internal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import org.junit.Test;
import org.kompiro.jamcircle.scripting.ScriptTypes;

public class ScriptingServiceImplTest {

	@Test
	public void scripting() throws Exception {
		ScriptEngineManager manager = new ScriptEngineManager();
		for(ScriptEngineFactory e :manager.getEngineFactories()){
			System.out.println(e.getEngineName());
			System.out.println(e.getExtensions());
		}
		
	}
	
	@Test
	public void initScriptingServiceIfNull() throws Exception {
		ScriptingServiceImpl service = new ScriptingServiceImpl();
		service.init(null);
	}
	
	@Test
	public void initScriptingService() throws Exception {
		ScriptingServiceImpl service = new ScriptingServiceImpl();
		Map<String, Object> beans = new HashMap<String, Object>();
		service.init(beans);
		service.exec(ScriptTypes.JavaScript, "test", "print('test\\n')", beans);
		service.exec(ScriptTypes.JRuby, "test", "p 'test'", beans);
		service.exec(ScriptTypes.JRuby, "test", "card = Card.new", beans);
	}
}
