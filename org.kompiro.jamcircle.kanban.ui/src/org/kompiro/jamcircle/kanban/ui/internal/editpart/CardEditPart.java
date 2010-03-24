package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.*;
import org.eclipse.gef.commands.*;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.CellEditorActionHandler;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.kanban.ui.command.MoveCommand;
import org.kompiro.jamcircle.kanban.ui.command.provider.ConfirmProvider;
import org.kompiro.jamcircle.kanban.ui.command.provider.MessageDialogConfirmProvider;
import org.kompiro.jamcircle.kanban.ui.dialog.BrowserPopupDialog;
import org.kompiro.jamcircle.kanban.ui.dialog.CardEditDialog;
import org.kompiro.jamcircle.kanban.ui.editpart.AbstractEditPart;
import org.kompiro.jamcircle.kanban.ui.figure.*;
import org.kompiro.jamcircle.kanban.ui.internal.command.*;
import org.kompiro.jamcircle.kanban.ui.internal.figure.CardFigureLayer;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;

public class CardEditPart extends AbstractEditPart {
	
	private final class FlagActionIcon extends
			ClickableActionIcon {
		private FlagActionIcon(Image image) {
			super(image);
		}

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
		}
	}

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
		
	private StatusIcon userIcon;
	private Clickable pageIcon;
	private Clickable fileIcon;
	private Clickable colorIcon;
	private Clickable editIcon;
	private Clickable storeIcon;
	
	private StatusIcon dueIcon;
	private StatusIcon overDueIcon;

	private SubjectDirectEditManager directManager;

	private Clickable deleteIcon;
	private StatusIcon completedIcon;
	private IFigure dueDummy;
	private Figure flagSection;
	private Clickable flagEditIcon;
	private StatusIcon flagWhiteIcon, flagBlueIcon, flagOrangeIcon, flagGreenIcon, flagRedIcon;
	private Figure flagCurrentIcon;
	private boolean movable = true;
	

	public CardEditPart(BoardModel board) {
		super(board);
	}
	
	@Override
	protected IFigure createFigure() {
		Card model = getCardModel();
		CardFigureLayer figure = new CardFigureLayer(getImageRegistry());
		figure.setSubject(model.getSubject());
		figure.setMock(model.isMock());
		figure.setId(model.getID());
		
		figure.setColorType(model.getColorType());
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
		
		createFlagSection();
		createEditIcon();
		createColorIcon();
		createDeleteAction();
		createFileIcon();
		createPageIcon();
		createStoreIcon();

		userIcon = new StatusIcon(KanbanImageConstants.USER_IMAGE.getIamge());
		completedIcon = new StatusIcon(KanbanImageConstants.COMPLETED_IMAGE.getIamge());
		dueIcon = new StatusIcon(KanbanImageConstants.CLOCK_IMAGE.getIamge());
		overDueIcon = new StatusIcon(KanbanImageConstants.CLOCK_RED_IMAGE.getIamge());
	}
	
	private void createStoreIcon() {
		storeIcon = new ClickableActionIcon(KanbanImageConstants.DB_ADD_IMAGE.getIamge()){
			public void actionPerformed(ActionEvent event) {
				Card card = getCardModel();
				CardContainer container = (CardContainer)CardEditPart.this.getParent().getModel();
				CompoundCommand command = new CompoundCommand();
				command.add(new CardStoreCommand((org.kompiro.jamcircle.kanban.model.mock.Card) card, container));
				command.add(new RemoveCardCommand(card, container));
				getCommandStack().execute(command);
			}
		};
	}


	private void createPageIcon() {
		pageIcon = new ClickableActionIcon(KanbanImageConstants.PAGE_IMAGE.getIamge()){
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
	}

	private void createFileIcon() {
		fileIcon = new ClickableActionIcon(KanbanImageConstants.FILE_LINK_IMAGE.getIamge()){
			public void actionPerformed(ActionEvent event) {
				String filePath = getCardModel().getFilePath();
				Program.launch(filePath);
			}
		};
	}

	private void createColorIcon() {
		colorIcon = new ClickableActionIcon(KanbanImageConstants.COLOR_IMAGE.getIamge()){
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
	}

	private void createDeleteAction() {
		deleteIcon = new ClickableActionIcon(KanbanImageConstants.DELETE_IMAGE.getIamge()){
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
	}

	private void createEditIcon() {
		editIcon = new ClickableActionIcon(KanbanImageConstants.EDIT_IMAGE.getIamge()){
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
					ConfirmProvider provider = new MessageDialogConfirmProvider(getShell());
					CardUpdateCommand command = new CardUpdateCommand(provider,card,subject,content,dueDate,files);
					getCommandStack().execute(command);
				}
			}
		};
	}

	private void createFlagSection() {
		flagWhiteIcon = new StatusIcon(KanbanImageConstants.FLAG_WHITE_IMAGE.getIamge());
		flagEditIcon = new FlagActionIcon(KanbanImageConstants.FLAG_WHITE_IMAGE.getIamge());
		flagBlueIcon = new StatusIcon(KanbanImageConstants.FLAG_BLUE_IMAGE.getIamge());
		flagOrangeIcon = new StatusIcon(KanbanImageConstants.FLAG_ORANGE_IMAGE.getIamge());
		flagRedIcon = new StatusIcon(KanbanImageConstants.FLAG_RED_IMAGE.getIamge());
		flagGreenIcon = new StatusIcon(KanbanImageConstants.FLAG_GREEN_IMAGE.getIamge());

		flagSection = new Figure();
		flagSection.setLayoutManager(new StackLayout());
	}
	
	@Override
	public void activate() {
		super.activate();
		if( ! PlatformUI.isWorkbenchRunning()) return;
		createIcons();
		hideActionIcons();
		IFigure actionSection = getCardFigure().getActionSection();
		if(getCardModel().isMock()){
			actionSection.add(storeIcon);
		}
		actionSection.add(flagEditIcon);
		actionSection.add(editIcon);
		actionSection.add(colorIcon);
		actionSection.add(deleteIcon);
		IFigure statusSection = getCardFigure().getStatusSection();
		statusSection.add(flagSection);
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
		}
		if(flagCurrentIcon != null) flagSection.add(flagCurrentIcon,0);
	}

	private void setDue(Card card) {
		if(card.getDueDate() == null){
			dueDummy.setVisible(false);
			overDueIcon.setToolTip(null);
			dueIcon.setVisible(false);
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
		CardFigureLayer cardFigure = getCardFigure();
		GraphicalEditPart part = (GraphicalEditPart) getParent();
		if(part != null){
			Object constraint = cardFigure.getBounds();
			part.setLayoutConstraint(this, cardFigure, constraint);
		}else{
			cardFigure.removeAll();
		}
	}
	
	public void doPropertyChange(final PropertyChangeEvent evt) {
		KanbanUIStatusHandler.debug("CardEditPart#propertyChange " +
				"evt.getPropertyName:'" + evt.getPropertyName() + 
				"' evt.getNewValue:'" + evt.getNewValue() + 
				"' evt.getSource:'" + evt.getSource() + "'");
		if(isPropLocation(evt)){
			Object newValue = evt.getNewValue();
			if(! (newValue instanceof Integer)) return ;
			Point location = (Point) new Point(getCardModel().getX(),getCardModel().getY());
			IFigure figure = getFigure();
			if(movable){
				figure.revalidate();
				effectToParentConstraint();
			}
			figure.setLocation(location);
		}
		else if(isPropPrepareLocation(evt)){
			movable = false;
		}
		else if(isPropCommitLocation(evt)){
			getCardFigure().revalidate();
			effectToParentConstraint();
			movable = true;
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
	
	private boolean isPropCommitLocation(PropertyChangeEvent evt) {
		return Card.PROP_COMMIT_LOCATION.equals(evt.getPropertyName());
	}

	private boolean isPropPrepareLocation(PropertyChangeEvent evt) {
		return Card.PROP_PREPARE_LOCATION.equals(evt.getPropertyName());
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
	
	protected boolean isPropLocation(PropertyChangeEvent evt) {
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

	private CardFigureLayer getCardFigure(){
		return (CardFigureLayer) getFigure();
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
		storeIcon.setVisible(true);
		flagEditIcon.setVisible(true);
		colorIcon.setVisible(true);
		editIcon.setVisible(true);
		deleteIcon.setVisible(true);
	}

	private void hideActionIcons() {
		storeIcon.setVisible(false);
		flagEditIcon.setVisible(false);
		editIcon.setVisible(false);
		colorIcon.setVisible(false);
		deleteIcon.setVisible(false);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class key) {
		if(MoveCommand.class.equals(key)){
			return new MoveCardCommand();
		}
		return super.getAdapter(key);
	}
	
	void setFileIcon(Clickable fileIcon) {
		this.fileIcon = fileIcon;
	}
	
	void setPageIcon(Clickable pageIcon) {
		this.pageIcon = pageIcon;
	}
	
	void setCompletedIcon(StatusIcon completedIcon) {
		this.completedIcon = completedIcon;
	}
	
	void setDueIcon(StatusIcon dueIcon) {
		this.dueIcon = dueIcon;
	}
	
	void setDueDummy(IFigure dueDummy) {
		this.dueDummy = dueDummy;
	}
	
	void setOverDueIcon(StatusIcon overDueIcon) {
		this.overDueIcon = overDueIcon;
	}
}
