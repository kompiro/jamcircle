package org.kompiro.jamcircle.kanban.boardtemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.kompiro.jamcircle.kanban.KanbanStatusHandler;
import org.kompiro.jamcircle.kanban.Messages;

public abstract class StreamReadWrapper<T> {
	public T run(URL resource){
		try{
			InputStream stream= resource.openStream();
			T result = null;
			result = doRun(stream);
			try{
				stream.close();
			}catch(IOException e){
				fail(e);
			}
			return result;
		} catch (IOException e) {
			fail(e);
			return null;
		}
	}
	public abstract T doRun(InputStream stream);

	private void fail(Exception e){
		KanbanStatusHandler.fail(e, Messages.StreamReadWrapper_error_message,false);			
	}

}