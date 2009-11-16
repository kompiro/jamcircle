package org.kompiro.jamcircle.kanban.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.kompiro.jamcircle.kanban.KanbanStatusHandler;


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
		String script = readFromResource(resource);
		if(script == null) return null;

		resource = this.getClass().getResource("template.txt");
		String template = readFromResource(resource);
		if(template == null) return null;

		String result = String.format(template,script,target);
		return result;
	}
	
	private String readFromResource(URL resource) {
		StringBuilder builder = new StringBuilder();
		try {
			InputStream stream= resource.openStream();
			Reader r = new InputStreamReader(stream);
			BufferedReader br = new BufferedReader(r);
			String line = null;
			while((line = br.readLine()) != null){
				builder.append(line + "\n");
			}
			try{
				br.close();
			}catch(IOException e){
				KanbanStatusHandler.fail(e, "ShowdownConverter#readFromResource",true);				
			}
			try{
				stream.close();
			}catch(IOException e){
				KanbanStatusHandler.fail(e, "ShowdownConverter#readFromResource",true);				
			}
		} catch (IOException e) {
			KanbanStatusHandler.fail(e, "ShowdownConverter#readFromResource",true);
			return null;
		}
		return builder.toString();
	}
	
}
