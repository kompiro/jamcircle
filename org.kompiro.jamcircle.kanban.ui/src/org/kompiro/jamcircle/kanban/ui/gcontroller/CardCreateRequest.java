package org.kompiro.jamcircle.kanban.ui.gcontroller;

import java.io.File;


import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.requests.CreateRequest;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.User;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.kompiro.jamcircle.xmpp.service.XMPPConnectionService;

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
		Card card = service.createCard(this.board,"new card",user,x,y);
		if(filePath != null){
			File file = new File(filePath);
			if(file.exists() && file.isFile()){
				card.addFile(file);
			}
		}
		return card;
	}
	
	private User getUser() {
		KanbanUIActivator activator = KanbanUIActivator.getDefault();
		if(activator == null) return null;
		XMPPConnectionService connectionService = activator.getConnectionService();
		if(connectionService == null)return null;
		return connectionService.getCurrentUser();
	}

}
