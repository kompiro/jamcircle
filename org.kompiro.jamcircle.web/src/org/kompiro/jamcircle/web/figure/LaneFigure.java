package org.kompiro.jamcircle.web.figure;

import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.web.figure.dd.CardAccept;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.incubator.dragdroplayouts.DDAbsoluteLayout;
import com.vaadin.incubator.dragdroplayouts.DDAbsoluteLayout.AbsoluteLayoutTargetDetails;
import com.vaadin.incubator.dragdroplayouts.client.ui.LayoutDragMode;
import com.vaadin.incubator.dragdroplayouts.events.LayoutBoundTransferable;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Panel;

public class LaneFigure {

	private final class LaneDropHandler implements DropHandler {

		private static final long serialVersionUID = 6503984357708576661L;

		@Override
		public void drop(DragAndDropEvent event) {
			Panel component = getComponent(event);
			AbsoluteLayoutTargetDetails targetDetails = (AbsoluteLayoutTargetDetails) event.getTargetDetails();
			int x = targetDetails.getRelativeLeft();
			int y = targetDetails.getRelativeTop();
			DDAbsoluteLayout layout = (DDAbsoluteLayout) targetDetails.getTarget();
			LayoutBoundTransferable transferable = (LayoutBoundTransferable) event.getTransferable();
			DDAbsoluteLayout sourceLayout = (DDAbsoluteLayout) transferable.getSourceComponent();
			remove(component, sourceLayout);
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

		private void remove(Panel component, DDAbsoluteLayout layout) {
			CardContainer parent = (CardContainer) ((Panel) (layout.getParent())).getData();
			Card card = getCard(component);
			layout.removeComponent(component);
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
			return CardAccept.get();
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
