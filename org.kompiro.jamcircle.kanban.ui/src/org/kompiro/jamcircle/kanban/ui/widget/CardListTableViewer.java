package org.kompiro.jamcircle.kanban.ui.widget;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.*;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.ui.*;
import org.kompiro.jamcircle.kanban.ui.dialog.CardEditDialog;

public class CardListTableViewer implements PropertyChangeListener{
	
	public List<CardListListener> listeners = new ArrayList<CardListListener>();

	public final class CardWrapper implements TableListWrapper{
		private Card card;
		private boolean even;
		private CardContainer container;
		
		public CardWrapper(CardContainer container,Card card,boolean even){
			this.container = container;
			this.card = card;
			this.even = even;
		}

		public Card getCard() {
			return card;
		}

		public boolean isEven() {
			return even;
		}
		
		public CardContainer getContainer(){
			return container;
		}
		
		@Override
		public String toString() {
			if(card == null){
				return "null";
			}
			return card.toString();
		}
	}

	private final class CardListContentProvider implements IStructuredContentProvider {

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			if (!(inputElement instanceof CardContainer)) throw new IllegalArgumentException();
			CardContainer container = (CardContainer) inputElement;
			List<CardWrapper> list = new ArrayList<CardWrapper>();
			boolean even = false;
			for(Card card : container.getCards()){
				list.add(new CardWrapper(container,card,even));
				even = !even;
			}
			return list.toArray();
		}

	}


	private TableViewer viewer;
	private TableViewerColumn statusColumn;
	private TableViewerColumn subjectColumn;
	private TableViewerColumn idColumn;
	private TableViewerColumn fromUserColumn;
	private CardListEditProvider editProvider;

	public static int OPERATIONS = DND.DROP_MOVE;

	public CardListTableViewer(Composite comp) {
		Composite composite = new Composite(comp, SWT.NONE);
		composite.setLayout(new GridLayout());
		viewer = new TableViewer(composite,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);

		viewer.setContentProvider(new CardListContentProvider());
		final Table table = viewer.getTable();
		GridDataFactory.fillDefaults().grab(true, true).hint(400, 400).applyTo(table);
		createIdColumn();
		createSubjectColumn();
		createStatusColumn();
		createFromUserColumn();

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addListener(SWT.MeasureItem, new Listener(){
			public void handleEvent(Event event) {
				if(event.type == SWT.MeasureItem){
					event.height = event.height > 16 ? event.height : 16;
				}
			}
		});
//		table.addListener(SWT.EraseItem, new Listener() {
//			public void handleEvent(Event event) {
//				event.detail &= ~SWT.HOT;	
//				if((event.detail & SWT.SELECTED) != 0) {
//					GC gc = event.gc;
//					Rectangle area = table.getClientArea();
//					/*
//					 * If you wish to paint the selection beyond the end of
//					 * last column, you must change the clipping region.
//					 */
//					int columnCount = table.getColumnCount();
//					if (event.index == columnCount - 1 || columnCount == 0) {
//						int width = area.x + area.width - event.x;
//						if (width > 0) {
//							Region region = new Region();
//							gc.getClipping(region);
//							region.add(event.x, event.y, width, event.height); 
//							gc.setClipping(region);
//							region.dispose();
//						}
//					}
//					gc.setAdvanced(true);
//					if (gc.getAdvanced()) gc.setAlpha(127);								
//					Rectangle rect = event.getBounds();
//					Color foreground = gc.getForeground();
//					Color background = gc.getBackground();
//					gc.setForeground(table.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
//					gc.setBackground(table.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
//					gc.fillGradientRectangle(0, rect.y, 600, rect.height, false);
//					// restore colors for subsequent drawing
//					gc.setForeground(foreground);
//					gc.setBackground(background);
//					event.detail &= ~SWT.SELECTED;					
//				}						
//			}
//		});
		viewer.addOpenListener(new IOpenListener(){

			public void open(OpenEvent event) {
				if(editProvider != null){
					ISelection sel = event.getSelection();
					if (sel instanceof StructuredSelection) {
						StructuredSelection selection = (StructuredSelection) sel;
						if(selection.size() == 1){
							Object obj = selection.getFirstElement();
							if (obj instanceof CardWrapper) {
								CardWrapper wrapper = (CardWrapper) obj;
								Card card = wrapper.getCard();
								CardEditDialog dialog = new CardEditDialog(viewer.getControl().getShell(),card);
								int returnCode = dialog.open();
								if(Dialog.OK == returnCode){
									String subject = dialog.getSubjectText();
									String content = dialog.getContentText();
									Date dueDate = dialog.getDueDate();
									List<File> files = dialog.getFiles();
									editProvider.edit(card,subject,content,dueDate,files);
								}
							}
						}
					}
				}
			}
		});
		
		Transfer[] types = new Transfer[] {CardObjectTransfer.getTransfer()};
		configurateDragSource(table,types);
	 	configureDropTarget(table, types);
	}


	private void configureDropTarget(Table table, Transfer[] types) {
		DropTarget target = new DropTarget(table,OPERATIONS);
	 	target.setTransfer(types);
	 	target.addDropListener(new DropTargetAdapter(){
			public void drop(DropTargetEvent event) {
				Object data = event.data;
				if (data instanceof StructuredSelection) {
					StructuredSelection selection = (StructuredSelection) data;
					for(Object obj : selection.toList()){
						System.out.println(obj);
					}
				}
			}
	 	});
	}

	private void configurateDragSource(Table table,Transfer[] types) {
		DragSource source = new DragSource(table, OPERATIONS);
		source.addDragListener(new DragSourceListener(){
			public void dragStart(DragSourceEvent event) {
				event.doit = true;
				Widget widget = event.widget;
				KanbanUIStatusHandler.debugUI("CardListTableViewer#dragStart() '%s' widget:'%s'", event,widget);
			}
			public void dragFinished(DragSourceEvent event) {
				ISelection selection = viewer.getSelection();
				if (selection instanceof StructuredSelection) {
					StructuredSelection ss = (StructuredSelection) selection;
					for(Object target :ss.toArray()){
						viewer.remove(target);
					}
				}
			}
			public void dragSetData(DragSourceEvent event) {
				ISelection selection = viewer.getSelection();
				if (selection instanceof StructuredSelection) {
					StructuredSelection ss = (StructuredSelection) selection;
					event.data = ss.toList();
				}
				KanbanUIStatusHandler.debugUI("CardListTableViewer#dragSetData() data:'%s'",event.data);
			}
		});
	 	source.setTransfer(types);
	}

	private void createIdColumn() {
		idColumn = new TableViewerColumn(viewer, SWT.LEFT);
		idColumn.getColumn().setText("id");
		idColumn.getColumn().setWidth(40);
		idColumn.setLabelProvider(new TableListColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return Integer.toString(getCard(element).getID());
			}
		});
		ColumnViewerSorter cSorter = new ColumnViewerSorter(viewer,idColumn) {

			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				Card card1 = getCard(e1);
				Card card2 = getCard(e2);
				return card1.getID() - card2.getID();
			}
			
		};
		cSorter.setSorter(cSorter, ColumnViewerSorter.ASC);
	}

	private void createStatusColumn() {
		statusColumn = new TableViewerColumn(viewer, SWT.LEFT);
		statusColumn.getColumn().setText("status");
		statusColumn.getColumn().setWidth(60);
		statusColumn.setLabelProvider(new TableListColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return getCard(element).getStatus();
			}
		});
		new ColumnViewerSorter(viewer,statusColumn) {

			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				Card card1 = getCard(e1);
				Card card2 = getCard(e2);
				
				String status1 = card1.getStatus() == null ? "" : card1.getStatus();
				return status1.compareToIgnoreCase(card2.getStatus());
			}
			
		};
	}

	private void createSubjectColumn() {
		subjectColumn = new TableViewerColumn(viewer, SWT.LEFT);
		subjectColumn.getColumn().setText("subject");
		subjectColumn.getColumn().setWidth(200);
		subjectColumn.setLabelProvider(new TableListColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return getCard(element).getSubject();
			}
		});
		new ColumnViewerSorter(viewer,subjectColumn) {
	
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				Card card1 = getCard(e1);
				Card card2 = getCard(e2);
				
				String subject1 = card1.getSubject() == null ? "" : card1.getSubject();
				return subject1.compareToIgnoreCase(card2.getSubject());
			}
			
		};
	}
	
	private void createFromUserColumn() {
		fromUserColumn = new TableViewerColumn(viewer,SWT.LEFT);
		fromUserColumn.getColumn().setText("from");
		fromUserColumn.getColumn().setWidth(120);
		fromUserColumn.setLabelProvider(new TableListColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				Card card = getCard(element);
				User from = card.getFrom();
				if(from == null) return null;
				return from.getUserName() != null ? from.getUserName() : from.getUserId();
			}
			@Override
			public Image getImage(Object element) {
				Card card = getCard(element);
				User from = card.getFrom();
				if(from == null) return null;
				ImageRegistry imageRegistry = getImageRegistry();
				return imageRegistry != null ? imageRegistry.get(KanbanImageConstants.USER_IMAGE.toString()): null;
			}
		});
		new ColumnViewerSorter(viewer,fromUserColumn) {

			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				Card card1 = getCard(e1);
				Card card2 = getCard(e2);
				
				String userName1 = getUserName(card1);
				String userName2 = getUserName(card2);
			return userName1.compareToIgnoreCase(userName2);
			}

			private String getUserName(Card card) {
				String userName;
				User from = card.getFrom();
				if(from == null){
					userName = "";
				}else{
					userName = from.getUserName() != null ? from.getUserName() : from.getUserId();
				}
				return userName;
			}
			
		};

	}

	
	public void setInput(CardContainer cards){
		viewer.setInput(cards);
	}
	
	public void refresh(){
		viewer.refresh(false);
	}
	
	public void dispose(){
		viewer.getTable().dispose();
	}

	public void addCardListListener(CardListListener listener){
		if(listener != null) listeners.add(listener);
	}
	
	public void removeCardListListener(CardListListener listener){
		if(listener != null) listeners.remove(listener);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		KanbanUIStatusHandler.debugUI("source:'%s' new:'%s' old:'%s'",evt.getSource(),evt.getNewValue(),evt.getOldValue());
		if (evt.getSource() instanceof CardContainer && evt.getNewValue() instanceof Card){
			final CardContainer container = (CardContainer) evt.getSource();
			final Card card = (Card) evt.getNewValue();
			final boolean even = container.getCards().length % 2 == 0;
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					viewer.add(new CardWrapper(container, card, even));
				}
			});
		}
	}

	public void setEditProvider(CardListEditProvider editProvider) {
		this.editProvider = editProvider;
	}

	private ImageRegistry getImageRegistry() {
		if(!Platform.isRunning()) return null;
		return KanbanUIActivator.getDefault().getImageRegistry();
	}
	private Card getCard(Object element) {
		if(!(element instanceof CardWrapper)) throw new IllegalArgumentException();
		return ((CardWrapper) element).getCard();
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		CardListTableViewer viewer = new CardListTableViewer(shell);
		CardContainer cards = new CardContainer.Mock();
		class MockDefault extends org.kompiro.jamcircle.kanban.model.mock.Card{
			private String status;
			private int id;
			public MockDefault(int id ,String subject,String status){
				super(subject);
				this.id = id;
				this.status = status;
			}
			@Override
			public String getStatus() {
				return status;
			}
			@Override
			public int getID() {
				return id;
			}
		};
		for(int i = 0; i < 10; i++){
			cards.addCard(new MockDefault(i,"Card List" + i,"Todo"));
		}
		viewer.setInput(cards);
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
	
}
