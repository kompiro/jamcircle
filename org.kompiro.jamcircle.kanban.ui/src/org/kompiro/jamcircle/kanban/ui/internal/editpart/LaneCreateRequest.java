package org.kompiro.jamcircle.kanban.ui.internal.editpart;


import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.requests.CreateRequest;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.Messages;

public class LaneCreateRequest extends CreateRequest {
	
	public static final String EXPAND_DATA_KEY_SIZE = "size"; //$NON-NLS-1$
	
	private KanbanService service;

	private Board board;
	
	public LaneCreateRequest(KanbanService service,Board board) {
		this.service = service;
		this.board = board;
	}

	@Override
	public Object getNewObject() {
		if(service == null){
			return new org.kompiro.jamcircle.kanban.model.mock.Lane(){

				public int getHeight() {
					return 200;
				}

				public String getStatus() {
					return "Mock"; //$NON-NLS-1$
				}

				public int getWidth() {
					return 200;
				}
				
				@Override
				public int getX() {
					return LaneCreateRequest.this.getLocation().x;
				}

				@Override
				public int getY() {
					return LaneCreateRequest.this.getLocation().y;
				}
			};
		}
		Lane lane = null;
		Object o = getExtendedData().get(EXPAND_DATA_KEY_SIZE);
		Dimension size = new Dimension();
		if (o instanceof Dimension) {
			size = (Dimension) o;
		}
		size.width = size.width <= 200 ? 200 : size.width; 
		size.height = size.height <= 200 ? 200 : size.height; 

		lane = service.createLane(board,Messages.LaneCreateRequest_new_status_title,getLocation().x,getLocation().y,size.width,size.height);
		return lane;
	}

}
