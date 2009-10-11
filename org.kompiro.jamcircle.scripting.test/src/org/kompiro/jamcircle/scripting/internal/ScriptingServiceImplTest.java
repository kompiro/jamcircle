package org.kompiro.jamcircle.scripting.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.*;
import org.kompiro.jamcircle.scripting.ScriptTypes;

public class ScriptingServiceImplTest {
	
	private ScriptingServiceImpl service;

	@Before
	public void initialize() throws Exception{
		service = new ScriptingServiceImpl();
	}
	
	@After
	public void afterTest() throws Exception{
		service.terminate();
	}

	@Test
	public void initScriptingServiceIfNullWhenJavaScript() throws Exception {
		service.init(null);
		service.eval(ScriptTypes.JavaScript, "test", "java.lang.System.out.println(initScriptingServiceIfNullWhenJavaScript)", null);
	}

	@Test
	public void initScriptingServiceIfNullWhenJRuby() throws Exception {
		service.init(null);
		service.eval(ScriptTypes.JRuby, "test", "p 'initScriptingServiceIfNullWhenJRuby'", null);
	}
	
	@Test
	public void evalJRubyEmptyValue() throws Exception {
		service.init(null);
		service.eval(ScriptTypes.JRuby, "test", "p 'evalJRubyEmptyValue is ok.'", null);
	}

	@Test
	public void evalJRubyReturnValue() throws Exception {
		Map<String, Object> globalBeans = new HashMap<String, Object>();
		globalBeans.put("ONE", 1);
		globalBeans.put("TWO", 2);
		service.init(globalBeans);
		Object actual = service.eval(ScriptTypes.JRuby, "test", "result =  ONE + TWO; p 'evalJrubyReturnValue is testing...'; result", null);
		assertTrue(actual.getClass().getCanonicalName(),actual instanceof Long);
		assertThat((Long)actual, is(3L));
	}
	
	@Test
	public void evalJRubySomeValues() throws Exception {
		Map<String, Object> instanceBeans = new HashMap<String, Object>();
		service.init(null);
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

}
