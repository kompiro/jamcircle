package org.kompiro.jamcircle.kanban.internal.util;

import java.io.*;
import java.net.URL;

import org.kompiro.jamcircle.kanban.KanbanStatusHandler;

public class StreamUtil {
	
	private StreamUtil(){}
	
	public static String readFromResource(URL resource) {
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
