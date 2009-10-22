package org.kompiro.jamcircle.xmpp.kanban.ui.model;


import org.eclipse.draw2d.geometry.Point;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;
import org.kompiro.jamcircle.kanban.model.User;
import org.kompiro.jamcircle.kanban.ui.model.AbstractModel;
import org.kompiro.jamcircle.kanban.ui.model.IconModel;
import org.kompiro.jamcircle.xmpp.kanban.ui.internal.util.XMPPUtil;

public class UserModel extends AbstractModel implements Comparable<UserModel>,IconModel{
	
	private static final long serialVersionUID = -5229805599410605915L;
	public static final String PROP_PRESENCE = "PROP_PRESENCE";
	
	private RosterEntry entry;
	private Presence presence;
	private User user;

	public UserModel(RosterEntry entry, Presence presence,User user){
		this(user);
		this.entry = entry;
		this.presence = presence;
		// it is need for initialization.
		this.presence.isAvailable();
	}
	
	public UserModel(User user) {
		this.user = user;
		this.location = new Point(user.getX(),user.getY());
	}

	public String getName() {
		if(entry == null) return "";
		return entry.getName();
	}
	
	public String getPresenceFrom(){
		if(presence == null) return "";
		return presence.getFrom();
	}
	
	public String getResource(){
		String from = getPresenceFrom();
		if(from == null) return null;
		return XMPPUtil.getResource(from);
	}
	
	public String getUserId(){
		return user.getUserId();
	}
	
	public User getUser(){
		return user;
	}
		
	public void setPresence(Presence newValue){
		Presence oldValue = this.presence;
		this.presence = newValue;
		firePropertyChange(PROP_PRESENCE, oldValue, newValue);
	}
	
	public boolean isAvailable(){
		if(presence == null) return false;
		return presence.isAvailable();
	}

	public Point getLocation() {
		return this.location;
	}

	public void setLocation(Point location) {
		Point oldLocation = getLocation();
		this.location = location;
		user.setX(location.x);
		user.setY(location.y);
		user.commitLocation();
		user.save(false);
		firePropertyChange(PROP_LOCATION, oldLocation, location);
	}

	
	public int compareTo(UserModel o) {
		return getPresenceFrom().compareTo(o.getPresenceFrom());
	}

}
