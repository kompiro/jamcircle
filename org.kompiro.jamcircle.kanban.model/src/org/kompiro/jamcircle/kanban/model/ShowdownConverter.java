package org.kompiro.jamcircle.kanban.model;

import java.net.URL;

import org.kompiro.jamcircle.kanban.internal.util.StreamUtil;


/**
 * This class provides to translate template using showdown(Markdown engine implemented by JavaScript).
 * @author kompiro
 */
public class ShowdownConverter {

 	private static final String FILE_NAME_OF_TEMPLATE = "template.txt"; //$NON-NLS-1$
	private static final String FILE_NAME_OF_SHOWDOWN_JS = "showdown.js"; //$NON-NLS-1$
	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final ShowdownConverter converter = new ShowdownConverter();
	
	private ShowdownConverter(){
	}
	
	public static ShowdownConverter getInstance(){
		return converter;
	}

	public String convert(String target){
		if(target == null) return EMPTY;
		URL resource = this.getClass().getResource(FILE_NAME_OF_SHOWDOWN_JS);
		String script = StreamUtil.readFromResource(resource);
		if(script == null) return null;

		resource = this.getClass().getResource(FILE_NAME_OF_TEMPLATE);
		String template = StreamUtil.readFromResource(resource);
		if(template == null) return null;

		String result = String.format(template,script,target);
		return result;
	}
	
	
}
