package org.kompiro.jamcircle.kanban.model;

import static org.junit.Assert.assertEquals;

import java.io.*;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

public class ShowdownConverterTest {
	
	private ShowdownConverter converter;

	@Before
	public void init(){
		converter = ShowdownConverter.getInstance();
	}

	@Test
	public void empty_string_when_target_is_null() throws Exception {
		assertEquals("",converter.convert(null));
	}
	
	@Test
	public void empty_html_string_when_target_is_empty() throws Exception {
		String showdown = readFromResource(this.getClass().getResource("showdown.js"));
		String expected = String.format(
				"<html>" +
				"<script type=\"text/javascript\">%s</script>" +
				"<body><div id=\"rendaring\"></div><textarea id=\"target\" style=\"display:none\"></textarea><script type=\"text/javascript\">" +
				"var converter = new Showdown.converter();" +
				"var targetText = document.getElementById(\"target\").innerHTML;" +
				"var text = converter.makeHtml(targetText);" +
				"document.getElementById(\"rendaring\").innerHTML = text;" +
				"</script>" +
				"</body></html>",showdown);
		assertEquals(expected.replaceAll("\n",""),converter.convert("").replaceAll("\n", "").replaceAll("\t",""));
	}
	
	@Test
	public void readFromFile() throws Exception {
		URL resource = this.getClass().getResource("test.txt");
		String text = readFromResource(resource);
		assertEquals("test\n",text.toString());
	}

	private String readFromResource(URL resource) throws IOException {
		InputStream stream= resource.openStream();
		Reader r = new InputStreamReader(stream);
		BufferedReader br = new BufferedReader(r);
		StringBuilder builder = new StringBuilder();
		String line = null;
		while((line = br.readLine()) != null){
			builder.append(line + "\n");
		}
		try{
			br.close();
		}catch(IOException e){
			
		}
		try{
			stream.close();
		}catch(IOException e){
			
		}
		return builder.toString();
	}
	
}
