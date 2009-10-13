package org.kompiro.jamcircle.kanban.ui.internal.command;

import java.io.File;
import java.util.*;

import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.ui.command.AbstractCommand;
import org.kompiro.jamcircle.kanban.ui.command.provider.ConfirmProvider;
import org.kompiro.jamcircle.kanban.ui.command.provider.MessageDialogConfirmProvider;

/**
 * @TestContext CardUpdateCommandTest
 */
public class CardUpdateCommand extends AbstractCommand {

	private Card card;
	private String subject;
	private String content;
	private HashSet<File> deleteTargetFileSet = new HashSet<File>();
	private Set<File> addTargetFileSet = new LinkedHashSet<File>();
	private String oldSubject;
	private String oldContent;
	private Date dueDate;
	private Date oldDueDate;
	private ConfirmProvider confirmExecution;
	
	public CardUpdateCommand(
			ConfirmProvider confirmExecution,
			Card card,
			String subject,
			String content,
			Date dueDate,
			List<File> files) {
		if(confirmExecution instanceof MessageDialogConfirmProvider){
			MessageDialogConfirmProvider provider = (MessageDialogConfirmProvider) confirmExecution;
			String title = "confirmation";
			String message = "Do you want to delete some files? If you'll select OK.After you can't undo this command.";
			provider.setTitle(title);
			provider.setMessage(message);
		}
		this.confirmExecution = confirmExecution;
		this.card = card;
		this.oldSubject = card.getSubject();
		this.subject = subject;
		this.oldContent = card.getContent();
		this.content = content;
		this.oldDueDate = card.getDueDate();
		List<File> oldFiles = card.getFiles();
		this.deleteTargetFileSet.addAll(oldFiles);
		this.dueDate = dueDate;
		if(files != null){
			for(File file : files){
				if(file.exists()){
					deleteTargetFileSet.remove(file);
					if(card.hasFile(file)){
						continue;
					}
					addTargetFileSet.add(file);
				}
			}
		}
	}
	
	@Override
	public boolean canExecute() {
		if(deleteTargetFileSet.isEmpty()){
			return true;
		}
		return confirm();
	}

	protected boolean confirm() {
		return confirmExecution.confirm();
	}
	
	@Override
	public boolean canUndo() {
		return super.canUndo() && deleteTargetFileSet.isEmpty();
	}

	@Override
	public void doExecute() {
		card.setSubject(subject);
		card.setContent(content);
		card.setDueDate(dueDate);
		for(File deleteTarget : deleteTargetFileSet){
			card.deleteFile(deleteTarget);
		}
		for(File addTarget : addTargetFileSet){
			card.addFile(addTarget);
		}
		card.save(false);
		setCanUndo(true);
	}
	
	@Override
	public void undo() {
		card.setSubject(oldSubject);
		card.setContent(oldContent);
		card.setDueDate(oldDueDate);
		for(File addTarget : addTargetFileSet){
			card.deleteFile(addTarget);
		}
		card.save(false);
		setCanUndo(false);
	}

}
