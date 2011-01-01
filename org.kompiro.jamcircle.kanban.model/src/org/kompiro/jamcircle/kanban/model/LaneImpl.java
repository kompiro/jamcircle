package org.kompiro.jamcircle.kanban.model;

import static java.lang.String.format;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.*;

import org.kompiro.jamcircle.scripting.ScriptTypes;
import org.kompiro.jamcircle.storage.service.FileStorageService;
import org.kompiro.jamcircle.storage.service.StorageService;

/**
 * This implementation describes Lane implmentation wrapper.
 * 
 * @author kompiro
 */
public class LaneImpl extends GraphicalImpl {

	private static final String LANE_PATH = "lanes" + File.separator; //$NON-NLS-1$

	private static final String ICON_PATH = "icon" + File.separator;//$NON-NLS-1$

	private static String TO_STRING_FORMAT = "LANE['#%d':'%s' trashed:'%s' size:'%d,%d' point:'%d,%d']"; //$NON-NLS-1$

	private final Lane lane;

	private List<Card> mockCards = new ArrayList<Card>();

	private FileStorageService fileService;

	private StorageService storageService;

	public LaneImpl(Lane lane) throws IllegalArgumentException {
		super(lane);
		this.lane = lane;
	}

	public boolean addCard(Card card) {
		if (card == null)
			throw new IllegalArgumentException("card is null");
		if (lane.getBoard() == null) {
			String message = format(Messages.LaneImpl_error_parent_board, lane);
			throw new IllegalArgumentException(message);
		}
		card.setLane(lane);
		Board cardsBoard = card.getBoard();
		Board lanesBoard = lane.getBoard();
		if (cardsBoard == null || lanesBoard.getID() != cardsBoard.getID()) {
			card.setBoard(lanesBoard);
		}
		card.setTrashed(false);
		card.setDeletedVisuals(false);
		card.save(false);
		if (card.isMock()) {
			mockCards.add(card);
		} else {
			getStorageService().flushEntity(lane);
		}
		PropertyChangeEvent event = new PropertyChangeEvent(lane, Lane.PROP_CARD, null, card);
		fireEvent(event);
		return true;
	}

	public boolean containCard(Card card) {
		return lane.equals(card.getLane());
	}

	@Override
	protected void fireEvent(PropertyChangeEvent event) {
		boolean added = event.getNewValue() != null;
		KanbanStatusHandler.debug(Messages.LaneImpl_fire_debug_message,
				lane.getStatus(), event.getPropertyName(), added);
		super.fireEvent(event);
	}

	public boolean removeCard(Card card) {
		if (card == null)
			throw new IllegalArgumentException("card is null");
		if (lane.getBoard() == null) {
			String message = format(Messages.LaneImpl_error_parent_board, lane);
			throw new IllegalArgumentException(message);
		}
		card.setLane(null);
		card.setBoard(null);
		card.setDeletedVisuals(true);
		card.save(false);
		if (card.isMock()) {
			mockCards.remove(card);
		} else {
			getStorageService().flushEntity(lane);
		}

		PropertyChangeEvent event = new PropertyChangeEvent(lane, Lane.PROP_CARD, card, null);
		fireEvent(event);
		return true;
	}

	public Card[] getCards() {
		Collection<Card> allCards = new ArrayList<Card>();
		allCards.addAll(Arrays.asList(lane.getCards()));
		allCards.addAll(mockCards);
		return allCards.toArray(new Card[] {});
	}

	public ScriptTypes getScriptType() {
		ScriptTypes scriptType = lane.getScriptType();
		if (scriptType == null)
			return ScriptTypes.JavaScript;
		return scriptType;
	}

	public void commitConstraint(Object bounds) {
		fireEvent(new PropertyChangeEvent(this, Lane.PROP_CONSTRAINT, null, bounds));
	}

	public String getContainerName() {
		return format("Lane[%s]", lane.getStatus()); //$NON-NLS-1$
	}

	public Board getBoard() {
		return lane.getBoard();
	}

	public void setCustomIcon(File file) {
		File oldIcon = getCustomIcon();
		if (oldIcon != null && oldIcon.exists()) {
			oldIcon.delete();
		}
		// when null, for remove custom icon
		if (file == null) {
			PropertyChangeEvent event = new PropertyChangeEvent(lane, Lane.PROP_CUSTOM_ICON, oldIcon, file);
			fireEvent(event);
			return;
		}
		getFileService().addFile(getIconPath(), file);
		PropertyChangeEvent event = new PropertyChangeEvent(lane, Lane.PROP_CUSTOM_ICON, oldIcon, file);
		fireEvent(event);
	}

	public File getCustomIcon() {
		List<File> files = getIconFiles();
		if (files == null || files.size() == 0)
			return null;
		return files.get(0);
	}

	private List<File> getIconFiles() {
		return getFileService().getFiles(getIconPath());
	}

	private FileStorageService getFileService() {
		if (fileService == null) {
			fileService = getStorageService().getFileService();
		}
		return fileService;
	}

	public boolean hasCustomIcon() {
		return getCustomIcon() != null;
	}

	void setMockCards(List<Card> mockCards) {
		this.mockCards = mockCards;
	}

	public void setFileService(FileStorageService fileService) {
		this.fileService = fileService;
	}

	String getIconPath() {
		return getPath() + ICON_PATH;
	}

	private String getPath() {
		return LANE_PATH + lane.getID();
	}

	private StorageService getStorageService() {
		if (storageService == null) {
			storageService = KanbanModelContext.getDefault().getStorageService();
		}
		return storageService;
	}

	public void setStorageService(StorageService storageService) {
		this.storageService = storageService;
	}

	@Override
	public String toString() {
		return format(TO_STRING_FORMAT, lane.getID(), lane.getStatus(), lane.isTrashed(),
				lane.getWidth(), lane.getHeight(), lane.getX(), lane.getY());
	}

}
