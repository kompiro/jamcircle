package org.kompiro.jamcircle.kanban.ui.figure;

import java.util.*;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.kompiro.jamcircle.kanban.model.ColorTypes;
import org.kompiro.jamcircle.kanban.model.HasColorTypeEntity;
import org.kompiro.jamcircle.kanban.ui.internal.command.ChangeColorCommand;
import org.kompiro.jamcircle.kanban.ui.internal.figure.CardFigure;

public class ColorPopUpHelper extends PopUpHelper{
	private IFigure currentTipSource;

	private Timer timer;

	private HasColorTypeEntity entity;

	private CommandStack stack;

	private List<Image> images;
	
	public ColorPopUpHelper(Control c,CommandStack stack, HasColorTypeEntity entity){
		super(c, SWT.TOOL | SWT.ON_TOP);
		getShell().setBackground(ColorConstants.tooltipBackground);
		getShell().setForeground(ColorConstants.tooltipForeground);
		this.entity = entity;
		images = new ArrayList<Image>();
		for(int i = 0; i < CardFigure.maxColorCount; i++){
			Color color = JFaceResources.getColorRegistry().get(CardFigure.COLOR_KEY_CARD_BORDER + i);
			images.add(new Image(c.getDisplay(),new ImageData(24,16,8,new PaletteData(new RGB[]{color.getRGB()}))));
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
		IFigure tip = new ColorSelectTooltip();
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
	
	private class ColorSelectTooltip extends Figure {

		private class ColorTypeChangeActionListener implements ActionListener {
			private ColorTypes type;

			private ColorTypeChangeActionListener(ColorTypes type) {
				this.type = type;
			}

			public void actionPerformed(ActionEvent event) {
				Command command = new ChangeColorCommand(entity,type);
				stack.execute(command);
				ColorPopUpHelper.this.hide();
			}
		}

		public ColorSelectTooltip(){
			GridLayout manager = new GridLayout();
			manager.numColumns = 2;
			manager.marginWidth = 2;
			manager.marginHeight = 2;
			this.setLayoutManager(manager);
			for(int i = 0; i < images.size(); i++){
				Clickable colorLabel = new Clickable(new Label(images.get(i)));
				colorLabel.addActionListener(new ColorTypeChangeActionListener(ColorTypes.values()[i]));
				add(colorLabel,new GridData());
			}
		}
	}
}
