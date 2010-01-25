package org.kompiro.jamcircle.kanban.model;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.kompiro.jamcircle.kanban.KanbanActivator;
import org.kompiro.jamcircle.storage.service.StorageService;


public class CardImpl extends GraphicalImpl {
	
	public static final String CARD_PATH = "cards" + File.separator;

	private Card card;
	
	private boolean deletedVisuals;

	public CardImpl(Card card){
		super(card);
		this.card = card;
	}
	
	public void init(){
		card.init();
		String uuid = card.getUUID();
		if(uuid == null || uuid.length() == 0){
			uuid = UUID.randomUUID().toString();
			card.setUUID(uuid);
			card.save(false);
		}
	}
	
	public String getStatus(){
		Lane lane = card.getLane();
		if(lane == null) return "";
		return lane.getStatus();
	}
		
	public boolean isDeletedVisuals(){
		return this.deletedVisuals;
	}
	
	public void setDeletedVisuals(boolean deletedVisuals){
		this.deletedVisuals = deletedVisuals;
	}
	
	public List<File> getFiles(){
		List<File> files = getStorageService().getFiles(getPath());
		if(files != null) return files;
		return Collections.emptyList();
	}
	
	public String getFilePath(){
		if( ! hasFiles()) return null;
		return getFiles().get(0).getParent();
	}

	public void addFile(File srcFile){
		getStorageService().addFile(getPath(), srcFile);
		PropertyChangeEvent event = new PropertyChangeEvent(card,Card.PROP_FILES,null,srcFile);
		fireEvent(event);
	}
	
	public boolean hasFiles(){
		return ! getFiles().isEmpty();
	}
	
	public void deleteFile(File file){
		if(hasFile(file)){
			file.delete();
			PropertyChangeEvent event = new PropertyChangeEvent(card,Card.PROP_FILES,file,null);
			fireEvent(event);
		}
	}
	
	public boolean hasFile(File file) {
		if(file.exists()){
			for(File target : getFiles()){
				if(target.getAbsolutePath().equals(file.getAbsolutePath())){
					return true;
				}
			}
		}
		return false;
	}
	
	public void setCompleted(boolean completed){
		this.card.setCompleted(completed);
		this.card.setCompletedDate(new Date());
	}
	
	public void setColorType(int colorType){
		for(ColorTypes type :ColorTypes.values()){
			if(colorType == type.ordinal()){
				card.setColorType(type);
			}
		}
	}
	

	private String getPath() {
		return CARD_PATH + card.getID();
	}
	
	private StorageService getStorageService() {
		return KanbanActivator.getKanbanService().getStorageService();
	}
	
	@Override
	public String toString() {
		return String.format("['#%d':'%s' trashed:'%s']", card.getID(),card.getSubject(),card.isTrashed());
	}

}
