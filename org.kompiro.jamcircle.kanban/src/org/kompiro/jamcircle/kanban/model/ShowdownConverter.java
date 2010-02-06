package org.kompiro.jamcircle.kanban.model;

import java.net.URL;

import org.kompiro.jamcircle.kanban.internal.util.StreamUtil;


public class ShowdownConverter {

 	private static final ShowdownConverter converter = new ShowdownConverter();
	
	private ShowdownConverter(){
	}
	
	public static ShowdownConverter getInstance(){
		return converter;
	}

	public String convert(String target){
		if(target == null) return "";
		URL resource = this.getClass().getResource("showdown.js");
		String script = StreamUtil.readFromResource(resource);
		if(script == null) return null;

		resource = this.getClass().getResource("template.txt");
		String template = StreamUtil.readFromResource(resource);
		if(template == null) return null;

		String result = String.format(template,script,target);
		return result;
	}
	
	
}
