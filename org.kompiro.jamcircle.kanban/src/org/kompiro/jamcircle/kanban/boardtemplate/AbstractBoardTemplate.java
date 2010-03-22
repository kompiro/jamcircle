package org.kompiro.jamcircle.kanban.boardtemplate;

import java.io.*;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.kompiro.jamcircle.kanban.KanbanActivator;
import org.kompiro.jamcircle.kanban.KanbanStatusHandler;


public abstract class AbstractBoardTemplate implements KanbanBoardTemplate {

	public static final String DEFAULT_ICONS_PATH = "icons/kanban.gif";
	private String icon;
	private String name;
	private String description;
	private String contributor;

	protected String readFromResourceString(URL resource) {
		StreamReadWrapper<String> runner = new StreamReadWrapper<String>(){
			@Override
			public String doRun(InputStream stream) {
				StringBuilder builder = new StringBuilder();
				Reader r = new InputStreamReader(stream);
				BufferedReader br = new BufferedReader(r);
				String line = null;
				try {
					while((line = br.readLine()) != null){
						builder.append(line + System.getProperty("line.separator"));
					}
				} catch (IOException e) {
					fail(e);
				}
				try{
					br.close();
				}catch(IOException e){
					fail(e);
				}
				return builder.toString();
			}
		};
		return runner.run(resource);
	}
	
	public URL getIconFromResource(){
		return Platform.getBundle(getContributor()).getResource(getIcon());
	}
	
	void fail(Exception e){
		KanbanStatusHandler.fail(e, getClass().getSimpleName() + "#readFromResource",false);			
	}
	
	public String getIcon() {
		if(icon == null) return DEFAULT_ICONS_PATH;
		return icon;
	}
	
	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName(){
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setContributor(String contributor) {
		this.contributor = contributor;
	}
	
	public String getContributor() {
		if(contributor == null) return KanbanActivator.ID;
		return contributor;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

}
