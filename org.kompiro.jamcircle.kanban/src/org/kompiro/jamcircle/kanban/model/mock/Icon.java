
package org.kompiro.jamcircle.kanban.model.mock;

/**
 * This implementation is mock of Icon and isn't able to store any persistence.
 * @author kompiro
 */
public class Icon extends MockGraphicalEntity implements org.kompiro.jamcircle.kanban.model.Icon{
	
	private String classType;

	public String getClassType() {
		return this.classType;
	}

	public void setClassType(String classType) {
		this.classType = classType;
	}
	
}