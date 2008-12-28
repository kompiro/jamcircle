package org.kompiro.jamcircle.kanban.ui.widget;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.kompiro.jamcircle.kanban.model.Card;


public interface CardListEditProvider {

	public void edit(Card card, String subject,String content, Date dueDate, List<File> files);

}
