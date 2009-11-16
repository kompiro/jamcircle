package org.kompiro.jamcircle.storage.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


import org.eclipse.equinox.security.storage.EncodingUtils;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.kompiro.jamcircle.storage.StorageStatusHandler;

public class StorageSettings implements Iterable<StorageSetting>{
	
	private static final String KEY_OF_INDEX = "index";
	private static final String KEY_OF_MODE = "mode";
	private static final String KEY_OF_PASSWORD = "password";
	private static final String KEY_OF_USERNAME = "username";
	private static final String SECURE_PREFERENCES_KEY_NAME = "org.kompiro.jamcircle.storage";
	
	private List<StorageSetting> settings = new ArrayList<StorageSetting>();
	
	public void add(StorageSetting setting) {
		if(hasSetting(setting)){
			int i = getIndex(setting);
			settings.remove(i);
		}
		settings.add(0,setting);
	}

	private int getIndex(StorageSetting target) {
		int i = 0;
		for(StorageSetting setting : settings){
			if(setting.getUri().equals(target.getUri())){
				return i;
			}
			i++;
		}
		return -1;
	}

	boolean hasSetting(StorageSetting o) {
		for(StorageSetting setting : settings){
			if(setting.getUri().equals(o.getUri())){
				return true;
			}
		}
		return false;
	}
	
	public StorageSetting get(String url){
		for(StorageSetting setting : settings){
			if(setting.getUri().equals(url)){
				return setting;
			}
		}
		return null;
	}

	public StorageSetting get(int index) {
		return settings.get(index);
	}

	public Iterator<StorageSetting> iterator() {
		return settings.iterator();
	}

	public void clear() {
		settings.clear();
	}

	public void add(int index,String uri,String mode,String username, String password) {
		add(new StorageSetting(index,uri,mode,username,password));
	}
	
	public boolean remove(StorageSetting target){
		boolean remove = settings.remove(target);
		if(!remove){
			remove = settings.remove(get(target.getUri()));
		}
		return remove;
	}

	public int size() {
		return settings.size();
	}
	
	public void storeSttings(){
		ISecurePreferences node = getConnectionPreferenceNode();
		for(String name : node.childrenNames()){
			node.node(name).removeNode();
		}
		try {
			node.flush();
		} catch (IOException e) {
			StorageStatusHandler.fail(e, "can't store settings.",true);
		}
		try {
			int i = 0;
			for(StorageSetting setting:settings){
				String uri = setting.getUri();
				uri = EncodingUtils.encodeSlashes(uri);
				ISecurePreferences settingNode = node.node(uri);
				settingNode.putInt(KEY_OF_INDEX, i++, false);
				settingNode.put(KEY_OF_MODE, setting.getMode(), false);
				settingNode.put(KEY_OF_USERNAME, setting.getUsername(), false);
				settingNode.put(KEY_OF_PASSWORD, setting.getPassword(), false);
			}
			node.flush();
		} catch (StorageException e) {
			StorageStatusHandler.fail(e, "can't store settings.",true);
		} catch (IOException e) {
			StorageStatusHandler.fail(e, "can't store settings.",true);
		}
	}

	private ISecurePreferences getConnectionPreferenceNode() {
		ISecurePreferences root = SecurePreferencesFactory.getDefault();
		return root.node(SECURE_PREFERENCES_KEY_NAME);
	}

	public void loadSettings(){
		clear();
		ISecurePreferences node = getConnectionPreferenceNode();
		if(node != null){
			for(String uri : node.childrenNames()){
				ISecurePreferences settingNode = node.node(uri);
				try {
					uri = EncodingUtils.decodeSlashes(uri);
					int index = settingNode.getInt(KEY_OF_INDEX, -1);
					String mode = settingNode.get(KEY_OF_MODE,"");
					String username = settingNode.get(KEY_OF_USERNAME,"");
					String password = settingNode.get(KEY_OF_PASSWORD,"");
					add(index,uri, mode, username, password);
				} catch (StorageException e) {
					StorageStatusHandler.fail(e, "can't load settings.",true);
				}
			}			
		}
		Collections.sort(settings,new Comparator<StorageSetting>(){

			public int compare(StorageSetting o1, StorageSetting o2) {
				return o1.getIndex() - o2.getIndex();
			}
			
		});
	}

	
}
