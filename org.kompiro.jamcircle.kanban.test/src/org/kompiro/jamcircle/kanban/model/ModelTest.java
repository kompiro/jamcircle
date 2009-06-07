package org.kompiro.jamcircle.kanban.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;

import net.java.ao.DBParam;

import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.KanbanActivator;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardImpl;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.service.internal.AbstractKanbanTest;
import org.kompiro.jamcircle.storage.service.StorageService;

public class ModelTest extends AbstractKanbanTest{
	
	private TestUtils util;

	@Before
	public void before() throws Exception {
		util = new TestUtils();
	}

	@Test
	public void modelRelation() throws Exception {
		Card card = entityManager.create(Card.class,new DBParam(Card.PROP_SUBJECT,"test"));
		Card card2 = entityManager.create(Card.class,new DBParam(Card.PROP_SUBJECT,"test2"));
		Lane lane = entityManager.create(Lane.class,new DBParam(Lane.PROP_STATUS,"todo"),new DBParam(Lane.PROP_CREATEDATE,new Date()));

		
		PropertyChangeListener listener = new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println(evt);
			}
			
		};
		lane.addPropertyChangeListener(listener);
		Lane lane2 = entityManager.create(Lane.class,new DBParam(Lane.PROP_STATUS,"todo"),new DBParam(Lane.PROP_CREATEDATE,new Date()));
		PropertyChangeListener listener2 = new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println(evt);
			}
			
		};
		lane2.addPropertyChangeListener(listener2);
		card.setLane(lane);
		card.save();
		card2.setLane(lane);
		card2.save();
		assertEquals(2,lane.getCards().length);
		assertEquals(card,lane.getCards()[0]);
		assertEquals("todo",lane.getStatus());
		assertEquals("todo",card.getStatus());
		assertTrue(card2.getFiles().isEmpty());
		
		int cardcount = 5;
		for(int i = 2; i < cardcount; i++){
			card = entityManager.create(Card.class,new DBParam(Card.PROP_SUBJECT,"test" + i));
			card.setLane(lane2);
			card.save();			
			card.setLane(null);
			card.setDeletedVisuals(true);
			card.setX(i * 5);
			card.setY(i * 50);
			card.save();
			card.setDeletedVisuals(false);
			lane.addCard(card);
		}
		assertEquals(cardcount,lane.getCards().length);
	}
	
	@Test
	public void trashed() throws Exception {
		DBParam[] params = new DBParam[]{
				new DBParam(Card.PROP_SUBJECT,"test"),
				new DBParam(Card.PROP_CREATEDATE,new Date()),
				new DBParam(Card.PROP_TRASHED,true),
		};
		entityManager.create(Card.class,params);
		assertNotNull(entityManager.find(Card.class,Card.PROP_TRASHED + " = true"));
	}
	
	@Test
	public void longValue() throws Exception {
		
		DBParam[] params = new DBParam[]{
				new DBParam(Card.PROP_SUBJECT,"very very long value"),
				new DBParam(Card.PROP_CONTENT,util.readFile()),
				new DBParam(Card.PROP_CREATEDATE,new Date()),
				new DBParam(Card.PROP_TRASHED,false),
		};
		entityManager.create(Card.class,params);
		params = new DBParam[]{
				new DBParam(Lane.PROP_STATUS,"very very long value"),
				new DBParam(Lane.PROP_SCRIPT,util.readFile()),
				new DBParam(Lane.PROP_CREATEDATE,new Date()),
		};
		entityManager.create(Lane.class,params);
		
	}
	
	@Test
	public void files() throws Exception {
		DBParam[] params = new DBParam[]{
				new DBParam(Card.PROP_SUBJECT,"test"),
				new DBParam(Card.PROP_CREATEDATE,new Date()),
				new DBParam(Card.PROP_TRASHED,true),
		};
		Card card = entityManager.create(Card.class,params);
		File target = util.target();
		card.addFile(target);
		System.out.println(target.getAbsolutePath());
		StorageService service = KanbanActivator.getDefault().getStorageService();
		String path = CardImpl.CARD_PATH + card.getID() + File.separator + "long.txt";
		assertTrue("'" + path +"' is not exist.",service.fileExists(path));
		assertTrue(card.hasFiles());
		FileInputStream stream = new FileInputStream(card.getFiles().get(0));
		assertEquals(util.readFile(),getString(stream));
	}
	
	private String getString(InputStream stream) throws IOException{
		Reader r = new InputStreamReader(stream);
		BufferedReader br = new BufferedReader(r);
		StringBuilder builder = new StringBuilder();
		String line = null;
		while((line = br.readLine()) != null){
			builder.append(line + "\n");
		}
		return builder.toString();

	}
	
}
