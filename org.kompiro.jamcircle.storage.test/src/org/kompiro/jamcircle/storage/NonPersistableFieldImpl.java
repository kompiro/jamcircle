package org.kompiro.jamcircle.storage;

public class NonPersistableFieldImpl {

	private boolean deleted = false;
	
	public NonPersistableFieldImpl(NonPersistableField delegated){
		System.out.println("NonPersistableFieldImpl.NonPersistableFieldImpl()");
	}
	
	public void setDeleted(boolean deleted){
		this.deleted = deleted;
	}

	public boolean isDeleted(){
		return deleted;
	}
	
}
