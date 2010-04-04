package org.kompiro.jamcircle.xmpp.service;

import java.io.IOException;
import java.util.*;

import org.eclipse.equinox.security.storage.*;
import org.kompiro.jamcircle.xmpp.Messages;
import org.kompiro.jamcircle.xmpp.XMPPStatusHandler;

public class XMPPSettings implements Iterable<XMPPSettings.Setting>{
	
	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final String KEY_OF_HOST_NAME = "hostName"; //$NON-NLS-1$
	private static final String KEY_OF_RESOURCE = "resource"; //$NON-NLS-1$
	private static final String KEY_OF_PORT = "port"; //$NON-NLS-1$
	private static final String KEY_OF_PASSWORD = "password"; //$NON-NLS-1$
	private static final String KEY_OF_USERNAME = "username"; //$NON-NLS-1$
	private static final String KEY_OF_SERVICE_NAME = "serviceName"; //$NON-NLS-1$
	private static final String SECURE_PREFERENCES_KEY_NAME = "org.kompiro.jamcircle.xmpp"; //$NON-NLS-1$
	
	private List<Setting> settings = new ArrayList<Setting>();
	
	public class Setting{
		private static final String SLASH = "/"; //$NON-NLS-1$
		private static final String AT_MARK = "@"; //$NON-NLS-1$
		private String host;
		private String resource;
		private String serviceName;
		private String username;
		private String password;
		private int port = 5222;

		public Setting(String host,String resource,  String serviceName,String username,String password){
			this.host = host;
			this.resource = resource;
			this.serviceName = serviceName;
			this.username = username;
			this.password = password;
		}

		public Setting(String host,String resource,String serviceName,String username,String password,int port){
			this(host,resource,serviceName,username,password);
			this.port = port;
		}

		public String getHost() {
			return host;
		}

		public String getResource() {
			return resource;
		}
		
		public String getServiceName() {
			return serviceName;
		}

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}

		public int getPort() {
			return port;
		}
		
		public String getSettingName(){
			return getUsername() + AT_MARK + getHost() + SLASH + getResource();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((host == null) ? 0 : host.hashCode());
			result = prime * result
					+ ((serviceName == null) ? 0 : serviceName.hashCode());
			result = prime * result
					+ ((username == null) ? 0 : username.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Setting other = (Setting) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (host == null) {
				if (other.host != null)
					return false;
			} else if (!host.equals(other.host))
				return false;
			if (serviceName == null) {
				if (other.serviceName != null)
					return false;
			} else if (!serviceName.equals(other.serviceName))
				return false;
			if (username == null) {
				if (other.username != null)
					return false;
			} else if (!username.equals(other.username))
				return false;
			return true;
		}

		private XMPPSettings getOuterType() {
			return XMPPSettings.this;
		}
		
	}

	public void add(Setting setting) {
		if(hasSetting(setting)){
			int i = getIndex(setting);
			settings.remove(i);
		}
		settings.add(0,setting);
	}

	private int getIndex(Setting o) {
		int i = 0;
		for(Setting setting : settings){
			if(setting.getSettingName().equals(o.getSettingName())){
				return i;
			}
			i++;
		}
		return -1;
	}

	private boolean hasSetting(Setting o) {
		for(Setting setting : settings){
			if(setting.getSettingName().equals(o.getSettingName())){
				return true;
			}
		}
		return false;
	}
	
	public Setting get(String settingName){
		for(Setting setting : settings){
			if(setting.getSettingName().equals(settingName)){
				return setting;
			}
		}
		return null;
	}

	public Setting get(int index) {
		return settings.get(index);
	}

	public Iterator<Setting> iterator() {
		return settings.iterator();
	}

	public void clear() {
		settings.clear();
	}

	public void add(String host,String resource, String serviceName, String username,
			String password, int port) {
		add(new Setting(host,resource,serviceName,username,password,port));
	}
	
	public boolean remove(Setting target){
		return settings.remove(target);
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
			for(XMPPSettings.Setting setting:settings){
				String settingName = setting.getSettingName();
				settingName = EncodingUtils.encodeSlashes(settingName);
				ISecurePreferences settingNode = node.node(settingName);
				settingNode.put(KEY_OF_HOST_NAME, setting.getHost(), false);
				settingNode.put(KEY_OF_SERVICE_NAME, setting.getServiceName(), false);
				settingNode.put(KEY_OF_USERNAME, setting.getUsername(), false);
				settingNode.put(KEY_OF_PASSWORD, setting.getPassword(), false);
				settingNode.putInt(KEY_OF_PORT, setting.getPort(), false);
			}
			node.flush();
		} catch (StorageException e) {
			XMPPStatusHandler.fail(e, Messages.XMPPSettings_store_error_message,true);
		} catch (IOException e) {
			XMPPStatusHandler.fail(e, Messages.XMPPSettings_store_error_message,true);
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
			for(String settingName : node.childrenNames()){
				ISecurePreferences settingNode = node.node(settingName);
				try {
					String hostName = settingNode.get(KEY_OF_HOST_NAME,EMPTY);
					String resource = settingNode.get(KEY_OF_RESOURCE,XMPPConnectionService.DEFAULT_RESOURCE_NAME);
					String serviceName = settingNode.get(KEY_OF_SERVICE_NAME,EMPTY);
					String username = settingNode.get(KEY_OF_USERNAME,EMPTY);
					String password = settingNode.get(KEY_OF_PASSWORD,EMPTY);
					int port = settingNode.getInt(KEY_OF_PORT,5222);
					add(hostName, resource, serviceName, username, password, port);				
				} catch (StorageException e) {
					XMPPStatusHandler.fail(e, Messages.XMPPSettings_load_error_message,true);
				}
			}			
		}
	}

	
}
