package org.kompiro.jamcircle.kanban.model;

import java.io.Serializable;


/**
 * This implementation is DTO of Card.
 * TODO This class needs to move XMPP Implementation
 * @author kompiro
 *
 */
public class CardDTO implements Serializable{

	private static final long serialVersionUID = 8904805055342491909L;
	private String uuid;
	private String content;
	private String subject;
	private int x;
	private int y;
	private String created;
	
	public CardDTO(Card card) {
		this.uuid = card.getUUID();
		this.content = card.getContent();
		this.subject = card.getSubject();
		this.x = card.getX();
		this.y = card.getY();
		this.created = card.getCreated();
	}
	
	public String getUUID() {
		return uuid;
	}

	public String getContent() {
		return content;
	}

	public String getSubject() {
		return subject;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getCreated() {
		return created;
	}

}
