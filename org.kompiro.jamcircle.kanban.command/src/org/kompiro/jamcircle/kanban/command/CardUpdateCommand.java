package org.kompiro.jamcircle.kanban.command;

import java.io.File;
import java.util.*;

import org.kompiro.jamcircle.kanban.command.provider.ConfirmProvider;
import org.kompiro.jamcircle.kanban.model.Card;

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
	private ConfirmProvider provider;

	public CardUpdateCommand(
			ConfirmProvider provider,
			Card card,
			String subject,
			String content,
			Date dueDate,
			List<File> files) {
		provider.setTitle(Messages.CardUpdateCommand_title);
		provider.setMessage(Messages.CardUpdateCommand_message);
		this.provider = provider;
		this.card = card;
		this.oldSubject = card.getSubject();
		this.subject = subject;
		this.oldContent = card.getContent();
		this.content = content;
		this.oldDueDate = card.getDueDate();
		List<File> oldFiles = card.getFiles();
		this.deleteTargetFileSet.addAll(oldFiles);
		this.dueDate = dueDate;
		if (files != null) {
			for (File file : files) {
				if (file.exists()) {
					deleteTargetFileSet.remove(file);
					if (card.hasFile(file)) {
						continue;
					}
					addTargetFileSet.add(file);
				}
			}
		}
	}

	@Override
	public boolean canExecute() {
		if (deleteTargetFileSet.isEmpty()) {
			return true;
		}
		return confirm();
	}

	protected boolean confirm() {
		return provider.confirm();
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
		for (File deleteTarget : deleteTargetFileSet) {
			card.deleteFile(deleteTarget);
		}
		for (File addTarget : addTargetFileSet) {
			card.addFile(addTarget);
		}
		card.save(false);
		setUndoable(true);
	}

	@Override
	public void undo() {
		card.setSubject(oldSubject);
		card.setContent(oldContent);
		card.setDueDate(oldDueDate);
		for (File addTarget : addTargetFileSet) {
			card.deleteFile(addTarget);
		}
		card.save(false);
		setUndoable(false);
	}

	@Override
	protected void initialize() {
	}

}
