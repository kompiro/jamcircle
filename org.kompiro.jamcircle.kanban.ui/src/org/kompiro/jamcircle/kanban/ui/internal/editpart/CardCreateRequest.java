package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import java.io.File;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.requests.CreateRequest;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.Messages;

public class CardCreateRequest extends CreateRequest {
	
	private static final int INIT = 10;
	private KanbanService service;
	private String filePath;
	private Board board;
	
	public CardCreateRequest(KanbanService service,Board board) {
		this.service = service;
		this.board = board;
	}
	
	public CardCreateRequest(KanbanService service,Board board,String filePath){
		this(service,board);
		this.filePath = filePath;
	}

	@Override
	public Object getNewObject() {
		Point location = getLocation();
		int x = location.x - INIT;
		int y = location.y - INIT;
		if(service == null){
			Card mock = new org.kompiro.jamcircle.kanban.model.mock.Card();
			mock.setX(x);
			mock.setY(y);
			return mock;
		}
		User user = getUser();
		Card card = service.createCard(this.board,Messages.CardCreateRequest_new_card_subject,user,x,y);
		if(filePath != null){
			File file = new File(filePath);
			if(file.exists() && file.isFile()){
				card.addFile(file);
			}
		}
		return card;
	}
	
	private User getUser() {
		return service.getCurrentUser();
	}

}
