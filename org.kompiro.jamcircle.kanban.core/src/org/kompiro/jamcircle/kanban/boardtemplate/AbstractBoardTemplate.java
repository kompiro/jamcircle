package org.kompiro.jamcircle.kanban.boardtemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.kompiro.jamcircle.kanban.KanbanStatusHandler;


public abstract class AbstractBoardTemplate implements KanbanBoardTemplate {

	protected String readFromResource(URL resource) {
		StringBuilder builder = new StringBuilder();
		try {
			InputStream stream= resource.openStream();
			Reader r = new InputStreamReader(stream);
			BufferedReader br = new BufferedReader(r);
			String line = null;
			while((line = br.readLine()) != null){
				builder.append(line + System.getProperty("line.separator"));
			}
			try{
				br.close();
			}catch(IOException e){
				fail(e);
			}
			try{
				stream.close();
			}catch(IOException e){
				fail(e);
			}
		} catch (IOException e) {
			fail(e);
			return null;
		}
		return builder.toString();
	}
	
	private void fail(Exception e){
		KanbanStatusHandler.fail(e, getClass().getSimpleName() + "#readFromResource",true);			
	}

}
