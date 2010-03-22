package org.kompiro.jamcircle.storage.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class StorageSettingsTest {
	
	private StorageSettings settings;

	@Before
	public void init() throws Exception{
		settings = new StorageSettings();
	}
	
	@Test
	public void add() throws Exception {
		StorageSetting setting1 = new StorageSetting(-1,"c:\\test","FILE","sa","sa");
		settings.add(setting1);
		assertEquals(1, settings.size());
		StorageSetting setting1d = new StorageSetting(-1,"c:\\test","FILE","sa","aaaa");
		assertTrue(settings.hasSetting(setting1d));
		settings.add(setting1d);
		assertEquals(1, settings.size());
		StorageSetting setting3 = new StorageSetting(-1,"c:\\test2","FILE","sa","aaaa");
		settings.add(setting3);
		assertEquals(2, settings.size());
		System.out.println();
		for(StorageSetting setting: settings){
			System.out.println(setting.getUri());
		}
		settings.add(setting1d);
		System.out.println();
		for(StorageSetting setting: settings){
			System.out.println(setting.getUri());
		}
		StorageSetting setting4 = new StorageSetting(-1,"c:\\test4","FILE","ss","sss");
		settings.add(setting4);
		System.out.println();
		for(StorageSetting setting: settings){
			System.out.println(setting.getUri());
		}
		settings.add(setting1d);
		System.out.println();
		for(StorageSetting setting: settings){
			System.out.println(setting.getUri());
		}
	}
	
	@Test
	public void remove() throws Exception {
		StorageSetting setting1 = new StorageSetting(1,"c:\\test","FILE","sa","sa");
		settings.add(setting1);
		settings.remove(setting1);
		assertEquals(0, settings.size());
		
	}

}
