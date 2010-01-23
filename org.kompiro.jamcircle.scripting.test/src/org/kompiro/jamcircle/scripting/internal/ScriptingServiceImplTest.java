package org.kompiro.jamcircle.scripting.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.jruby.Ruby;
import org.junit.*;
import org.kompiro.jamcircle.scripting.ScriptTypes;

public class ScriptingServiceImplTest {
	
	private ScriptingServiceImpl service;

	@Before
	public void initialize() throws Exception{
		Map<String, Object> globalBeans = new HashMap<String, Object>();
		globalBeans.put("ONE", 1);
		globalBeans.put("TWO", 2);

		service = new ScriptingServiceImpl();
		service.init();
		service.setGlobalValues(globalBeans);
	}
	
	@After
	public void afterTest() throws Exception{
		service.terminate();
	}

	@Test
	public void initScriptingServiceIfNullWhenJavaScript() throws Exception {
		service.eval(ScriptTypes.JavaScript, "test", "java.lang.System.out.println('initScriptingServiceIfNullWhenJavaScript')", null);
	}

	@Test
	public void initScriptingServiceIfNullWhenJRuby() throws Exception {
		service.eval(ScriptTypes.JRuby, "test", "p 'initScriptingServiceIfNullWhenJRuby'", null);
	}
	
	@Test
	public void evalJRubyEmptyValue() throws Exception {
		service.eval(ScriptTypes.JRuby, "test", "p 'evalJRubyEmptyValue is ok.'", null);
	}

	@Test
	public void evalJRubyReturnValue() throws Exception {
		Object actual = service.eval(ScriptTypes.JRuby, "test", "result =  ONE + TWO; p 'evalJrubyReturnValue is testing...'; result", null);
		assertTrue(actual.getClass().getCanonicalName(),actual instanceof Long);
		assertThat((Long)actual, is(3L));
	}
	
	@Test
	public void evalJRubySomeValues() throws Exception {
		Map<String, Object> instanceBeans = new HashMap<String, Object>();
		service.eval(ScriptTypes.JRuby, "test", 
				"p 'empty value'", new HashMap<String, Object>());
		Person value = new Person();
		value.setName("テスト太郎");
		instanceBeans.put("person", value);
		String name = Person.class.getName();
		Object result = service.eval(ScriptTypes.JRuby, "test", 
				"include_class \"" + name + 
				"\";\n" +
				"$bsf.lookupBean('person').name", instanceBeans);
		assertThat((String)result,is("テスト太郎"));
	}
	
	@Test
	public void adapter() throws Exception {
		Object adapter = service.getAdapter(Ruby.class);
		assertNotNull(adapter);
		assertTrue(adapter instanceof Ruby);
	}

}
