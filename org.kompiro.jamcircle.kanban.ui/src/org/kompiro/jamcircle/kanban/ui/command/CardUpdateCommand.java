package org.kompiro.jamcircle.kanban.ui.command;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.List;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.kompiro.jamcircle.kanban.model.Card;

public class CardUpdateCommand extends AbstractCommand {

	private Card card;
	private String subject;
	private String content;
	private HashSet<File> deleteTargetFileSet;
	private HashSet<File> addTargetFileSet;
	private String oldSubject;
	private String oldContent;
	private Date dueDate;
	private Date oldDueDate;
	
	public CardUpdateCommand(
			Card card,
			String subject,
			String content,
			Date dueDate,
			List<File> files) {
		this.card = card;
		this.oldSubject = card.getSubject();
		this.subject = subject;
		this.oldContent = card.getContent();
		this.content = content;
		this.oldDueDate = card.getDueDate();
		deleteTargetFileSet = new HashSet<File>();
		deleteTargetFileSet.addAll(card.getFiles());
		this.dueDate = dueDate;
		if(files != null){
			addTargetFileSet = new HashSet<File>();
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
		boolean result = MessageDialog.openConfirm(getShell(), "Confirmation", "Do you want to delete some files? If you'll select OK.After you can't undo this command.");
		return result;
	}
	
	@Override
	public boolean canUndo() {
		return deleteTargetFileSet.isEmpty();
	}
	
	private Shell getShell() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if(workbench == null) return new Shell();
		IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
		if(activeWorkbenchWindow == null) return new Shell();
		return activeWorkbenchWindow.getShell();
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
		card.save();
	}
	
	@Override
	public void undo() {
		card.setSubject(oldSubject);
		card.setContent(oldContent);
		card.setDueDate(oldDueDate);
		for(File addTarget : addTargetFileSet){
			card.deleteFile(addTarget);
		}
		card.save();
	}

}
