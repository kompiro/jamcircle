package org.kompiro.jamcircle.kanban.model;

import static java.lang.String.format;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.*;

import org.kompiro.jamcircle.storage.model.GraphicalEntityImpl;
import org.kompiro.jamcircle.storage.service.StorageService;

/**
 * This implementation describes Card implmentation wrapper.
 * 
 * @author kompiro
 */
public class CardImpl extends GraphicalEntityImpl {

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	public static final String CARD_PATH = "cards" + File.separator; //$NON-NLS-1$

	private static String TO_STRING_FORMAT = "CARD['#%d':'%s' trashed:'%s' point:'%d,%d' BOARD:'%s']"; //$NON-NLS-1$

	private static String TO_STRING_UNDER_LANE_FORMAT = "CARD['#%d':'%s' trashed:'%s' point:'%d,%d' BOARD:'%s' LANE:'#%d' : '%s']"; //$NON-NLS-1$

	private Card card;

	private boolean deletedVisuals;

	private StorageService storageService;

	public CardImpl(Card card) {
		super(card);
		this.card = card;
	}

	public void init() {
		card.init();
		String uuid = card.getUUID();
		if (uuid == null || uuid.length() == 0) {
			uuid = UUID.randomUUID().toString();
			card.setUUID(uuid);
			card.save(false);
		}
	}

	public String getStatus() {
		Lane lane = card.getLane();
		if (lane == null)
			return EMPTY_STRING;
		return lane.getStatus();
	}

	public boolean isDeletedVisuals() {
		return this.deletedVisuals;
	}

	public void setDeletedVisuals(boolean deletedVisuals) {
		this.deletedVisuals = deletedVisuals;
	}

	public List<File> getFiles() {
		List<File> files = getStorageService().getFileService().getFiles(getPath());
		if (files != null)
			return files;
		return Collections.emptyList();
	}

	public String getFilePath() {
		if (!hasFiles())
			return null;
		return getFiles().get(0).getParent();
	}

	public void addFile(File srcFile) {
		if (srcFile == null)
			throw new IllegalArgumentException("srcFile is null.");
		getStorageService().getFileService().addFile(getPath(), srcFile);
		PropertyChangeEvent event = new PropertyChangeEvent(card, Card.PROP_FILES, null, srcFile);
		fireEvent(event);
	}

	public boolean hasFiles() {
		return !getFiles().isEmpty();
	}

	public void deleteFile(File file) {
		if (file == null)
			throw new IllegalArgumentException("file is null.");
		if (hasFile(file)) {
			file.delete();
			PropertyChangeEvent event = new PropertyChangeEvent(card, Card.PROP_FILES, file, null);
			fireEvent(event);
		}
	}

	public boolean hasFile(File file) {
		if (file != null && file.exists()) {
			for (File target : getFiles()) {
				if (target.getAbsolutePath().equals(file.getAbsolutePath())) {
					return true;
				}
			}
		}
		return false;
	}

	public void setCompleted(boolean completed) {
		this.card.setCompleted(completed);
		this.card.setCompletedDate(new Date());
	}

	public void setColorType(int colorType) {
		for (ColorTypes type : ColorTypes.values()) {
			if (colorType == type.ordinal()) {
				card.setColorType(type);
			}
		}
	}

	private String getPath() {
		return CARD_PATH + card.getID();
	}

	private StorageService getStorageService() {
		if (storageService != null) {
			return storageService;
		}
		return KanbanModelContext.getDefault().getStorageService();
	}

	public void setStorageService(StorageService storageService) {
		this.storageService = storageService;
	}

	@Override
	public String toString() {
		Lane lane = card.getLane();
		if (lane == null) {
			return format(TO_STRING_FORMAT, card.getID(), card.getSubject(), card.isTrashed(), card.getX(),
					card.getY(), card.getBoard());
		}
		return format(TO_STRING_UNDER_LANE_FORMAT, card.getID(), card.getSubject(), card.isTrashed(),
				card.getX(), card.getY(), card.getBoard(), lane.getID(), lane.getStatus());
	}

}
