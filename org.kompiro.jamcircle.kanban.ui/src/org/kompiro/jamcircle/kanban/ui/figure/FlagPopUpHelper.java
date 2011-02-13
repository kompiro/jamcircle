package org.kompiro.jamcircle.kanban.ui.figure;

import static java.lang.String.format;

import java.util.*;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.kompiro.jamcircle.kanban.command.ChangeFlagCommand;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.FlagTypes;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.kompiro.jamcircle.kanban.ui.Messages;

public class FlagPopUpHelper extends PopUpHelper{
	private static final String KEY_OF_FLAG_IMAGE = "FLAG_%s_IMAGE"; //$NON-NLS-1$

	private IFigure currentTipSource;

	private Timer timer;

	private Card entity;

	private CommandStack stack;

	private List<Image> images;
	
	public FlagPopUpHelper(Control c,CommandStack stack, Card entity){
		super(c, SWT.TOOL | SWT.ON_TOP);
		getShell().setBackground(ColorConstants.tooltipBackground);
		getShell().setForeground(ColorConstants.tooltipForeground);
		this.entity = entity;
		images = new ArrayList<Image>();
		FlagTypes[] flagTypeValues = FlagTypes.values();
		for(int i = 0; i < flagTypeValues.length; i++){
			Image image = KanbanUIActivator.getDefault().getImageRegistry().get(format(KEY_OF_FLAG_IMAGE,flagTypeValues[i]));
			images.add(new Image(c.getDisplay(),image.getImageData().scaledTo(16, 16)));
		}
		this.stack = stack;
	}

	@Override
	protected void hookShellListeners() {
		getShell().addMouseTrackListener(new MouseTrackAdapter() {
			public void mouseExit(MouseEvent e) {
				hide();
			}
			public void mouseEnter(MouseEvent e) {
				timer.cancel();
			}
		});
	}
	
	public void displayToolTipNear(IFigure hoverSource, int eventX, int eventY) {
		IFigure tip = new FlagSelectTooltip();
		if (tip != null && hoverSource != currentTipSource) {
			getLightweightSystem().setContents(tip);
			Point displayPoint = computeWindowLocation(tip, eventX, eventY);
			Dimension shellSize = getLightweightSystem().getRootFigure()
				.getPreferredSize().getExpanded(getShellTrimSize());
			setShellBounds(displayPoint.x, displayPoint.y, shellSize.width, shellSize.height);
			show();
			currentTipSource = hoverSource;
			timer = new Timer(true);
			timer.schedule(new TimerTask() {
				public void run() {
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							hide();
							timer.cancel();
						}
					});
				}
			}, 5000);
		}
	}
	
	private Point computeWindowLocation(IFigure tip, int eventX, int eventY) {
		org.eclipse.swt.graphics.Rectangle clientArea = control.getDisplay().getClientArea();
		
		Dimension tipSize = getLightweightSystem()
			.getRootFigure()
			.getPreferredSize()
			.getExpanded(getShellTrimSize());
		Point preferredLocation = new Point(eventX + 10, eventY - tipSize.height / 2);

		// Adjust location if tip is going to fall outside display
		if (preferredLocation.y + tipSize.height > clientArea.height)  
			preferredLocation.y = eventY - tipSize.height;
		
		if (preferredLocation.x + tipSize.width > clientArea.width)
			preferredLocation.x -= (preferredLocation.x + tipSize.width) - clientArea.width;
		
		return preferredLocation; 
	}
	
	@Override
	public void dispose() {
		super.dispose();
		for(Image image:images){
			image.dispose();
		}
	}
	
	private class FlagSelectTooltip extends Figure {

		private class FlagTypeChangeActionListener implements ActionListener {
			private FlagTypes type;

			private FlagTypeChangeActionListener(FlagTypes type) {
				this.type = type;
			}

			public void actionPerformed(ActionEvent event) {
				Command command = new ChangeFlagCommand(entity,type);
				stack.execute(command);
				FlagPopUpHelper.this.hide();
			}
		}

		public FlagSelectTooltip(){
			GridLayout manager = new GridLayout();
			manager.numColumns = 1;
			manager.marginWidth = 2;
			manager.marginHeight = 2;
			this.setLayoutManager(manager);
			for(int i = 0; i < images.size(); i++){
				Clickable flagLabel = new Clickable(new Label(images.get(i)));
				flagLabel.addActionListener(new FlagTypeChangeActionListener(FlagTypes.values()[i]));
				add(flagLabel,new GridData());
			}
			Clickable hideFlagLabel = new Clickable(new Label(Messages.FlagPopUpHelper_hide_label));
			hideFlagLabel.addActionListener(new FlagTypeChangeActionListener(null));
			add(hideFlagLabel,new GridData());
		}
	}
}
