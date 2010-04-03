package org.kompiro.jamcircle.kanban.internal.util;

import java.io.*;
import java.net.URL;

import org.kompiro.jamcircle.kanban.KanbanStatusHandler;

public class StreamUtil {
	
	private static final String LINE_BREAK = System.getProperty("line.separator");
	private static final String SHOWDOWN_CONVERTER_READ_FROM_RESOURCE = "ShowdownConverter#readFromResource"; //$NON-NLS-1$

	private StreamUtil(){}
	
	public static String readFromResource(URL resource) {
		StringBuilder builder = new StringBuilder();
		try {
			InputStream stream= resource.openStream();
			Reader r = new InputStreamReader(stream);
			BufferedReader br = new BufferedReader(r);
			String line = null;
			while((line = br.readLine()) != null){
				builder.append(line + LINE_BREAK);
			}
			try{
				br.close();
			}catch(IOException e){
				KanbanStatusHandler.fail(e, SHOWDOWN_CONVERTER_READ_FROM_RESOURCE,true);				
			}
			try{
				stream.close();
			}catch(IOException e){
				KanbanStatusHandler.fail(e, SHOWDOWN_CONVERTER_READ_FROM_RESOURCE,true);				
			}
		} catch (IOException e) {
			KanbanStatusHandler.fail(e, SHOWDOWN_CONVERTER_READ_FROM_RESOURCE,true);
			return null;
		}
		return builder.toString();
	}

}
