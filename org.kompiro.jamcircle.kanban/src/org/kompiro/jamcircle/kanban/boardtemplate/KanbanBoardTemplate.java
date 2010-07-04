package org.kompiro.jamcircle.kanban.boardtemplate;

import org.kompiro.jamcircle.kanban.model.Board;

/**
 * This interface describes board template.
 */
public interface KanbanBoardTemplate {

	/**
	 * This method is called after board created.
	 * So you can program some behavior to the board.
	 * @param board target board
	 */
	public void initialize(Board board);
	
	/**
	 * Return template name
	 * @return template name
	 */
	public String getName();
	public void setName(String name);
	
	/**
	 * Return template description
	 * @return template description
	 */
	public String getDescription();
	public void setDescription(String description);
	
	/**
	 * return icon's relative path to show on the wizard.
	 * @return icon's path
	 */
	public String getIcon();
	public void setIcon(String icon);
	
	/**
	 * Return contributed bundle's name
	 * @param contributor
	 */
	public String getContributor();
	public void setContributor(String contributor);
}
