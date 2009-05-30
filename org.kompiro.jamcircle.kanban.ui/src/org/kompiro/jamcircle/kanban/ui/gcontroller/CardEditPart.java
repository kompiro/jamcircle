package org.kompiro.jamcircle.kanban.ui.gcontroller;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;


import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.CellEditorActionHandler;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.ColorTypes;
import org.kompiro.jamcircle.kanban.model.HasColorTypeEntity;
import org.kompiro.jamcircle.kanban.model.ShowdownConverter;
import org.kompiro.jamcircle.kanban.model.User;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.kanban.ui.command.AddCardToContanerCommand;
import org.kompiro.jamcircle.kanban.ui.command.CardSubjectDirectEditCommand;
import org.kompiro.jamcircle.kanban.ui.command.CardUpdateCommand;
import org.kompiro.jamcircle.kanban.ui.dialog.BrowserPopupDialog;
import org.kompiro.jamcircle.kanban.ui.dialog.CardEditDialog;
import org.kompiro.jamcircle.kanban.ui.figure.CardFigure;
import org.kompiro.jamcircle.kanban.ui.figure.CellEditorLocator;
import org.kompiro.jamcircle.kanban.ui.figure.ColorPopUpHelper;
import org.kompiro.jamcircle.kanban.ui.figure.FlagPopUpHelper;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;

public class CardEditPart extends AbstractEditPart {
	
	private class SubjectDirectEditManager extends DirectEditManager{

		public SubjectDirectEditManager(CellEditorLocator locator) {
			super(CardEditPart.this, TextCellEditor.class, locator);
		}

		protected CellEditor createCellEditorOn(Composite composite) {
			return new TextCellEditor(composite,SWT.MULTI|SWT.WRAP);
		}
		
		@Override
		protected void initCellEditor() {
			IViewPart kanbanView = WorkbenchUtil.findKanbanView();
			CellEditorActionHandler handlers = (CellEditorActionHandler)kanbanView.getAdapter(CellEditorActionHandler.class);
			handlers.addCellEditor(getCellEditor());
		    getCellEditor().setValue(getCardModel().getSubject());
		    Text text = (Text) getCellEditor().getControl();
		    text.selectAll();
		}
		
	}
		
	private Clickable userIcon;
	private Clickable pageIcon;
	private Clickable fileIcon;
	private ActionListener fileIconListener = new ActionListener(){

		public void actionPerformed(ActionEvent event) {
			String filePath = getCardModel().getFilePath();
			Program.launch(filePath);
		}
		
	};

	private Clickable colorIcon;
	private ActionListener colorIconListener = new ActionListener(){
		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();
			if (source instanceof Clickable) {
				Clickable colorIcon = (Clickable) source;
				Control control = getViewer().getControl();
				CommandStack stack = getCommandStack();
				ColorPopUpHelper helper = new ColorPopUpHelper(control,stack,getCardModel());
				org.eclipse.swt.graphics.Point target = control.getDisplay().getCursorLocation();
				
				helper.displayToolTipNear(colorIcon,target.x,target.y);
			}
		}
		
	};
	
	private Clickable editIcon;
	private ActionListener editIconListener = new ActionListener(){
		public void actionPerformed(ActionEvent event) {
			Shell shell = getShell();
			Card card = getCardModel();
			CardEditDialog dialog = new CardEditDialog(shell,card);
			int returnCode = dialog.open();
			if(Dialog.OK == returnCode){
				String subject = dialog.getSubjectText();
				String content = dialog.getBodyText();
				Date dueDate = dialog.getDueDate();
				List<File> files = dialog.getFiles();
				CardUpdateCommand command = new CardUpdateCommand(card,subject,content,dueDate,files);
				getCommandStack().execute(command);
			}
		}
	};
	
	private Clickable dueIcon;
	private Clickable overDueIcon;

	private SubjectDirectEditManager directManager;
	private ActionListener pageIconListener = new ActionListener(){

		public void actionPerformed(ActionEvent event) {
			Shell shell = getShell();
			Card card = getCardModel();
			String title = String.format("#%d %s", card.getID(),card.getSubject());
			String content = card.getContent();
			BrowserPopupDialog dialog = new BrowserPopupDialog(shell,title,"show card contents",content);
			dialog.create();
			dialog.open();
		}
		
	};
	private Clickable deleteIcon;
	private ActionListener deleteIconListener = new ActionListener(){
		public void actionPerformed(ActionEvent event) {
			GroupRequest deleteReq =
				new GroupRequest(RequestConstants.REQ_DELETE);
			deleteReq.setEditParts(CardEditPart.this);

			CompoundCommand compoundCmd = new CompoundCommand();
			Command cmd = CardEditPart.this.getCommand(deleteReq);
			if (cmd != null) compoundCmd.add(cmd);
			getCommandStack().execute(compoundCmd);
		};
	};
	private Clickable completedIcon;
	private IFigure dueDummy;
	private Figure flagSection;
	private Clickable flagEditIcon, flagWhiteIcon, flagBlueIcon, flagOrangeIcon, flagGreenIcon, flagRedIcon;
	private ActionListener flagIconListener = new ActionListener(){
		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();
			if (source instanceof Clickable) {
				Clickable colorIcon = (Clickable) source;
				Control control = getViewer().getControl();
				CommandStack stack = getCommandStack();
				FlagPopUpHelper helper = new FlagPopUpHelper(control,stack,getCardModel());
				org.eclipse.swt.graphics.Point target = control.getDisplay().getCursorLocation();
				
				helper.displayToolTipNear(colorIcon,target.x,target.y);
			}
		};
	};
	private Clickable flagCurrentIcon;
	

	public CardEditPart(BoardModel board) {
		super(board);
	}
	
	@Override
	protected IFigure createFigure() {
		Card model = getCardModel();
		CardFigure figure = new CardFigure();
		figure.setSubject(model.getSubject());
		figure.setMock(model.isMock());
		figure.setId(model.getID());
		
		figure.setColorType(model.getColorType());
		figure.setOpaque(true);
		figure.setLocation(new Point(model.getX(),model.getY()));
		boolean animated = getBoardModel().isAnimated();
		if(!animated){
			figure.setAdded(true);
		}
		dueDummy = new Figure();
		dueDummy.setLayoutManager(new StackLayout());
		dueDummy.setSize(16,16);
		return figure;
	}

	private void createIcons() {
		ImageRegistry imageRegistry = getImageRegistry();
		
		Image flagWhiteIconImage = imageRegistry.get(KanbanImageConstants.FLAG_WHITE_IMAGE.toString());
		flagWhiteIcon = new Clickable(new Label(flagWhiteIconImage));
		flagWhiteIcon.setSize(16, 16);
		flagEditIcon = new Clickable(new Label(flagWhiteIconImage));
		flagEditIcon.setSize(16, 16);

		Image flagBlueIconImage = imageRegistry.get(KanbanImageConstants.FLAG_BLUE_IMAGE.toString());
		flagBlueIcon = new Clickable(new Label(flagBlueIconImage));
		flagBlueIcon.setSize(16, 16);

		Image flagOrangeIconImage = imageRegistry.get(KanbanImageConstants.FLAG_ORANGE_IMAGE.toString());
		flagOrangeIcon = new Clickable(new Label(flagOrangeIconImage));
		flagOrangeIcon.setSize(16, 16);

		Image flagRedIconImage = imageRegistry.get(KanbanImageConstants.FLAG_RED_IMAGE.toString());
		flagRedIcon = new Clickable(new Label(flagRedIconImage));
		flagRedIcon.setSize(16, 16);

		Image flagGreenIconImage = imageRegistry.get(KanbanImageConstants.FLAG_GREEN_IMAGE.toString());
		flagGreenIcon = new Clickable(new Label(flagGreenIconImage));
		flagGreenIcon.setSize(16, 16);
		
		flagSection = new Figure();
		flagSection.setLayoutManager(new StackLayout());
		flagSection.add(flagEditIcon);
		
		Image editIconImage = imageRegistry.get(KanbanImageConstants.EDIT_IMAGE.toString());
		editIcon = new Clickable(new Label(editIconImage));
		editIcon.setSize(16, 16);
		
		Image colorIconImage = imageRegistry.get(KanbanImageConstants.COLOR_IMAGE.toString());
		colorIcon = new Clickable(new Label(colorIconImage));
		colorIcon.setSize(16, 16);
	
		Image deleteIconImage = imageRegistry.get(KanbanImageConstants.DELETE_IMAGE.toString());
		deleteIcon = new Clickable(new Label(deleteIconImage));
		deleteIcon.setSize(16, 16);
		
		Image fileIconImage = imageRegistry.get(KanbanImageConstants.FILE_LINK_IMAGE.toString());
		fileIcon = new Clickable(new Label(fileIconImage));
		fileIcon.setSize(16, 16);
		
		Image userIconImage = imageRegistry.get(KanbanImageConstants.USER_IMAGE.toString());
		userIcon = new Clickable(new Label(userIconImage));
		userIcon.setSize(16, 16);
		
		Image pageIconImage = imageRegistry.get(KanbanImageConstants.PAGE_IMAGE.toString());
		pageIcon = new Clickable(new Label(pageIconImage));
		pageIcon.setSize(16, 16);

		Image completedIconImage = imageRegistry.get(KanbanImageConstants.COMPLETED_IMAGE.toString());
		completedIcon = new Clickable(new Label(completedIconImage));
		completedIcon.setSize(16, 16);
		
		Image dueIconImage = imageRegistry.get(KanbanImageConstants.CLOCK_IMAGE.toString());
		dueIcon = new Clickable(new Label(dueIconImage));
		dueIcon.setSize(16,16);

		Image overDueIconImage = imageRegistry.get(KanbanImageConstants.CLOCK_RED_IMAGE.toString());
		overDueIcon = new Clickable(new Label(overDueIconImage));
		overDueIcon.setSize(16,16);

	}
	
	@Override
	public void activate() {
		super.activate();
		if( ! PlatformUI.isWorkbenchRunning()) return;
		createIcons();

		fileIcon.addActionListener(fileIconListener);

		pageIcon.addActionListener(pageIconListener);
		
		deleteIcon.addActionListener(deleteIconListener);
		
		editIcon.addActionListener(editIconListener);
		
		colorIcon.addActionListener(colorIconListener);
		flagEditIcon.addActionListener(flagIconListener);
		flagWhiteIcon.addActionListener(flagIconListener);
		flagRedIcon.addActionListener(flagIconListener);
		flagBlueIcon.addActionListener(flagIconListener);
		flagGreenIcon.addActionListener(flagIconListener);
		flagOrangeIcon.addActionListener(flagIconListener);

		hideActionIcons();
		IFigure actionSection = getCardFigure().getActionSection();
		actionSection.add(flagSection);
		actionSection.add(editIcon);
		actionSection.add(colorIcon);
		actionSection.add(deleteIcon);
		IFigure statusSection = getCardFigure().getStatusSection();
		statusSection.add(dueDummy);
		statusSection.add(fileIcon);
		statusSection.add(userIcon);
		statusSection.add(pageIcon);
		statusSection.add(completedIcon);
		dueDummy.add(dueIcon);
		dueDummy.add(overDueIcon);

		Card card = getCardModel();
		setFrom(card.getFrom());
		setFiles(card.getFiles());
		setContent(card.getContent());
		setCompleted(card);
		setDue(card);
		setFlag(card);
	}
	

	private void setFlag(Card card) {
		if(flagCurrentIcon != null){
			flagSection.remove(flagCurrentIcon);
		}
		if(card.getFlagType() == null){
			flagCurrentIcon = null;
		}else{
			flagEditIcon.setVisible(false);
			switch (getCardModel().getFlagType()){
			case RED:
				flagCurrentIcon = flagRedIcon;
				break;
			case ORANGE:
				flagCurrentIcon = flagOrangeIcon;
				break;
			case GREEN:
				flagCurrentIcon = flagGreenIcon;
				break;
			case BLUE:
				flagCurrentIcon = flagBlueIcon;
				break;
			case WHITE:
				flagCurrentIcon = flagWhiteIcon;
				break;
			}
			flagSection.add(flagCurrentIcon,0);
		}
	}

	private void setDue(Card card) {
		if(card.getDueDate() == null){
			dueDummy.setVisible(false);
			overDueIcon.setToolTip(null);
			dueIcon.setToolTip(null);
		}else{
			dueDummy.setVisible(true);
			String dueText = DateFormat.getDateInstance().format(card.getDueDate());
			Label tip = new Label();
			tip.setText(dueText);
			overDueIcon.setToolTip(tip);
			dueIcon.setToolTip(tip);
			if(System.currentTimeMillis() > card.getDueDate().getTime()){
				overDueIcon.setVisible(true);
				dueIcon.setVisible(false);
			}else{
				dueIcon.setVisible(true);
				overDueIcon.setVisible(false);
			}
		}
	}

	private void setCompleted(Card card) {
		boolean completed = card.isCompleted();
		completedIcon.setVisible(completed);
		if(completed){
			Label dateLabel = new Label();
			Date completedDate = card.getCompletedDate();
			if(completedDate != null){
				String date = DateFormat.getDateInstance().format(completedDate);
				dateLabel.setText(date);
				completedIcon.setToolTip(dateLabel);
			}
		}else{
			completedIcon.setToolTip(null);
		}
	}

	@Override
	public void deactivate() {
		if(PlatformUI.isWorkbenchRunning()){
			removeActionListener(flagEditIcon,flagIconListener);
			removeActionListener(flagWhiteIcon,flagIconListener);
			removeActionListener(flagRedIcon,flagIconListener);
			removeActionListener(flagBlueIcon,flagIconListener);
			removeActionListener(flagGreenIcon,flagIconListener);
			removeActionListener(flagOrangeIcon,flagIconListener);
			removeActionListener(fileIcon,fileIconListener);
			removeActionListener(pageIcon,pageIconListener);
			removeActionListener(deleteIcon,deleteIconListener);
			removeActionListener(editIcon,editIconListener);
			removeActionListener(colorIcon,colorIconListener);
		}
		super.deactivate();
	}

	private void removeActionListener(Clickable clickable,ActionListener listener) {
		if(clickable != null) clickable.removeActionListener(listener);
	}

	/**
	 * Lane and Board needs command when request type is REQ_ADD.
	 */
	@Override
	public Command getCommand(Request request) {
		Command command = super.getCommand(request);
		if(command == null && RequestConstants.REQ_ADD.equals(request.getType())){
			EditPart parent = getParent();
			while(command == null && parent != null){
				command = parent.getCommand(request);
				if(command == null) parent = getParent();
			}
		}
		return command;
	}

	
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy(){
			@Override
			protected Command createDeleteCommand(GroupRequest deleteRequest) {
				CompoundCommand command = new CompoundCommand();
				GroupRequest requestToParent = new GroupRequest();
				requestToParent.setEditParts(CardEditPart.this);
				requestToParent.setType(REQ_ORPHAN_CHILDREN);
				command.add(getParent().getCommand(requestToParent));
				command.add(new AddCardToContanerCommand(getBoardModel().getTrashModel(),getCardModel()));
				return command;
			}
		});
	    installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,new DirectEditPolicy(){

			@Override
			protected Command getDirectEditCommand(DirectEditRequest request) {
				String value = (String)request.getCellEditor().getValue();
				return new CardSubjectDirectEditCommand(getCardModel(),value);
			}

			@Override
			protected void showCurrentEditValue(DirectEditRequest request) {
			}

		});
	}
	

	@Override
	public void performRequest(Request req) {
		if(RequestConstants.REQ_OPEN.equals(req.getType())){
			if(directManager == null){
				directManager = new SubjectDirectEditManager(new CellEditorLocator(getCardFigure().getMiddleSection()));
			}
			directManager.show();
		}
	}

	private void effectToParentConstraint() {
		CardFigure cardFigure = getCardFigure();
		GraphicalEditPart part = (GraphicalEditPart) getParent();
		Object constraint = cardFigure.getBounds();
		part.setLayoutConstraint(this, cardFigure, constraint);
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		KanbanUIStatusHandler.debug("CardEditPart#propertyChange " +
				"evt.getPropertyName:'" + evt.getPropertyName() + 
				"' evt.getNewValue:'" + evt.getNewValue() + 
				"' evt.getSource:'" + evt.getSource() + "'");
		if(isPropLocation(evt)){
			Object newValue = evt.getNewValue();
			if(! (newValue instanceof Integer)) return ;
			Point location = (Point) new Point(getCardModel().getX(),getCardModel().getY());
			IFigure figure = getFigure();
			figure.setLocation(location);
			effectToParentConstraint();
		}
		else if(isPropSubject(evt)){
			getCardFigure().setSubject(getCardModel().getSubject());
		}
		else if(isPropBody(evt)){
			setContent(getCardModel().getContent());
		}
		else if(isPropFiles(evt)){
			setFiles(getCardModel().getFiles());
		}
		else if(isPropColorChanged(evt)){
			getCardFigure().setColorType((ColorTypes) evt.getNewValue());
		}
		else if(isPropCompleted(evt)){
			setCompleted(getCardModel());
		}
		else if(isPropDue(evt)){
			setDue(getCardModel());
		}else if(isPropFlag(evt)){
			setFlag(getCardModel());
		}
		else{
			KanbanUIStatusHandler.info("no property handler:property name:" + evt.getPropertyName());
		}
	}
	
	private boolean isPropFlag(PropertyChangeEvent evt) {
		return Card.PROP_FLAG_TYPE.equals(evt.getPropertyName());
	}

	private boolean isPropDue(PropertyChangeEvent evt) {
		return Card.PROP_DUE_DATE.equals(evt.getPropertyName());
	}

	private boolean isPropCompleted(PropertyChangeEvent evt) {
		return Card.PROP_COMPLETED.equals(evt.getPropertyName());
	}

	private boolean isPropFiles(PropertyChangeEvent evt) {
		return Card.PROP_FILES.equals(evt.getPropertyName());
	}
	
	private boolean isPropLocation(PropertyChangeEvent evt) {
		return Card.PROP_LOCATION_X.equals(evt.getPropertyName())||
		Card.PROP_LOCATION_Y.equals(evt.getPropertyName());
	}

	private boolean isPropSubject(PropertyChangeEvent evt) {
		return Card.PROP_SUBJECT.equals(evt.getPropertyName());
	}

	private boolean isPropBody(PropertyChangeEvent evt) {
		return Card.PROP_CONTENT.equals(evt.getPropertyName());
	}
	
	private boolean isPropColorChanged(PropertyChangeEvent evt){
		return HasColorTypeEntity.PROP_COLOR_TYPE.equals(evt.getPropertyName());
	}

	public Card getCardModel(){
		return (Card) getModel();
	}

	private CardFigure getCardFigure(){
		return (CardFigure) getFigure();
	}
	
	private void setFiles(List<File> files){
		if(files == null || files.size() == 0 ){
			getFileIcon().setVisible(false);
			getFileIcon().setToolTip(null);
		}else{
			Label f = new Label();
			StringBuilder builder = new StringBuilder();
			boolean isNotFirst = false;
			for(File file : files){
				if(isNotFirst) builder.append("\n");
				builder.append(file.getAbsolutePath());
				isNotFirst = true;
			}
			f.setText(builder.toString());
			getFileIcon().setVisible(true);
			getFileIcon().setToolTip(f);
		}
	}
	
	private void setFrom(User from) {
		if(from != null){
			Label f = new Label(from.getUserId());
			getUserIcon().setToolTip(f);
			getUserIcon().setVisible(true);
		}else{
			getUserIcon().setToolTip(null);
			getUserIcon().setVisible(false);
		}
	}

	private void setContent(String content) {
		if(content == null || "".equals(content)){
			getPageIcon().setVisible(false);
		}else{
			getPageIcon().setVisible(true);
		}
	}


	private IFigure getPageIcon() {
		return pageIcon;
	}

	private IFigure getUserIcon() {
		return userIcon;
	}

	private IFigure getFileIcon() {
		return fileIcon;
	}

	@Override
	public void showTargetFeedback(Request request) {
		showActionIcons();
		super.showTargetFeedback(request);
	}


	@Override
	public void eraseTargetFeedback(Request request) {
		hideActionIcons();
		super.eraseTargetFeedback(request);
	}

	private void showActionIcons() {
		if(getCardModel().getFlagType() == null){
			flagEditIcon.setVisible(true);
		}
		colorIcon.setVisible(true);
		editIcon.setVisible(true);
		deleteIcon.setVisible(true);
	}

	private void hideActionIcons() {
		flagEditIcon.setVisible(false);
		editIcon.setVisible(false);
		colorIcon.setVisible(false);
		deleteIcon.setVisible(false);
	}
	
}
