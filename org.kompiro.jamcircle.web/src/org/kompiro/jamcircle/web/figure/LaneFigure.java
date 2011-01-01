package org.kompiro.jamcircle.web.figure;

import org.kompiro.jamcircle.kanban.model.*;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.*;
import com.vaadin.incubator.dragdroplayouts.DDAbsoluteLayout;
import com.vaadin.incubator.dragdroplayouts.client.ui.LayoutDragMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Panel;

public class LaneFigure {

	public final class CardAccept extends ClientSideCriterion {

		private static final long serialVersionUID = -2515981634029336423L;

		@Override
		public boolean accept(DragAndDropEvent event) {
			Panel component = getComponent(event);
			if (component.getData() instanceof Card) {
				return true;
			}
			return false;
		}

		private Panel getComponent(DragAndDropEvent event) {
			return (Panel) event.getTransferable().getData("component");
		}

	}

	private final class LaneDropHandler implements DropHandler {

		private static final long serialVersionUID = 6503984357708576661L;

		@Override
		public void drop(DragAndDropEvent event) {
			Panel component = getComponent(event);
			int x = Integer.valueOf(event.getTargetDetails().getData("relativeLeft").toString());
			int y = Integer.valueOf(event.getTargetDetails().getData("relativeTop").toString());
			DDAbsoluteLayout layout = (DDAbsoluteLayout) event.getTargetDetails().getTarget();
			Panel container = (Panel) component.getParent().getParent();
			remove(component, container);
			add(component, x, y, layout);
		}

		private void add(Panel component, int x, int y, DDAbsoluteLayout layout) {
			Card card = getCard(component);
			card.setX(x);
			card.setY(y);
			String location = getLocation(x, y);
			Panel panel = (Panel) layout.getParent();
			CardContainer parent = (CardContainer) panel.getData();
			parent.addCard(card);
			layout.addComponent(component, location);
		}

		private void remove(Panel component, Panel container) {
			CardContainer parent = (CardContainer) container.getData();
			Card card = getCard(component);
			container.removeComponent(component);
			parent.removeCard(card);
		}

		private Card getCard(Panel component) {
			return (Card) component.getData();
		}

		private Panel getComponent(DragAndDropEvent event) {
			return (Panel) event.getTransferable().getData("component");
		}

		@Override
		public AcceptCriterion getAcceptCriterion() {
			return AcceptAll.get();
		}
	}

	private Panel panel;
	private DDAbsoluteLayout layout;
	private Lane lane;

	public LaneFigure(Lane lane) {
		this.lane = lane;
		this.panel = new Panel();
		panel.setData(lane);
		panel.setCaption(lane.getContainerName());
		panel.setWidth(toPx(lane.getWidth()));
		panel.setHeight(toPx(lane.getHeight()));
		layout = new DDAbsoluteLayout();
		layout.setSizeFull();
		layout.setDragMode(LayoutDragMode.CLONE);
		layout.setDropHandler(new LaneDropHandler());
		panel.setContent(layout);
	}

	private String getLocation(int x, int y) {
		String location = String.format("left: %dpx;top: %dpx;", x, y);
		return location;
	}

	private String toPx(int value) {
		return value + "px";
	}

	public AbsoluteLayout getLayout() {
		return layout;
	}

	public String getLocation() {
		return getLocation(lane.getX(), lane.getY());
	}

	public Panel getPanel() {
		return panel;
	}

}
