/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Remy Chi Jian Suen <remy.suen@gmail.com> - Bug 214424 IOConsole(String, String, ImageDescriptor, String, boolean) constructor is missing api javadoc
 *******************************************************************************/

package org.kompiro.jamcircle.scripting.ui.internal.eclipse.ui.console;

import java.io.*;
import java.util.*;

import jline.History;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.*;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.contentassist.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.console.*;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.statushandlers.StatusManager;
import org.jruby.*;
import org.jruby.ext.Readline;
import org.jruby.internal.runtime.ValueAccessor;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.ui.KanbanView;
import org.kompiro.jamcircle.kanban.ui.editpart.IBoardCommandExecuter;
import org.kompiro.jamcircle.scripting.ui.Messages;
import org.kompiro.jamcircle.scripting.ui.ScriptingUIActivator;

/**
 * A console that displays text from I/O streams. An I/O console can have multiple
 * output streams connected to it and provides one input stream connected to the
 * keyboard.
 * <p>
 * Clients may instantiate and subclass this class.
 * </p>
 * Copied from org.eclipse.ui.console.IOConsole 3.4.0 by kompiro
 */
public class RubyScriptingConsole extends TextConsole {

	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final String KEY_OF_BUNDLE_VERSION = "Bundle-Version"; //$NON-NLS-1$
	private static final String INIT_SCRIPT_RB = "init.rb"; //$NON-NLS-1$
	private static final String PATH_OF_JRUBY_HOME = "META-INF/jruby.home"; //$NON-NLS-1$
	private static final String BUNDLE_OF_ORG_JRUBY = "org.jruby"; //$NON-NLS-1$
	private static final String KEY_OF_RUNTIME = "$$"; //$NON-NLS-1$
	private static final String KEY_OF_BOARD_COMMAND_EXECUTER_ACCESSOR = "$board_command_executer_accessor"; //$NON-NLS-1$
	private static final String KEY_OF_BOARD_ACCESSOR = "$board_accessor"; //$NON-NLS-1$
	private static final String ARGS = "-Ku"; //$NON-NLS-1$
	private static final String OUTPUT_STREAM_COLOR = "RubyScriptingConsole.OutputStreamColor"; //$NON-NLS-1$
	private static final String ERROR_STREAM_COLOR = "RubyScriptingConsole.ErrorStreamColor"; //$NON-NLS-1$

	/**
	 * The document partitioner
	 */
    private IOConsolePartitioner partitioner;
    
    /**
     * The stream from which user input may be read
     */
    private IOConsoleInputStream inputStream;
        
    /**
     * A collection of open streams connected to this console.
     */
    private List<Object> openStreams;

    /**
     * The encoding used to for displaying console output.
     */
    private String fEncoding = "utf-8"; //$NON-NLS-1$

	private IOConsolePage consolePage;

	private Ruby runtime;

	private IOConsoleOutputStream output;

	private IOConsoleOutputStream error;

	private IOConsoleInputStream input;
	
	private ControlFromKey keyListener;
	
	private ContentAssistant assist;
    
    /**
     * Constructs a console with the given name, type, image, and lifecycle, with the
     * workbench's default encoding.
     * 
     * @param name name to display for this console
     * @param consoleType console type identifier or <code>null</code>
     * @param imageDescriptor image to display for this console or <code>null</code>
     * @param autoLifecycle whether lifecycle methods should be called automatically
     *  when this console is added/removed from the console manager
     */
    public RubyScriptingConsole(String name, String consoleType, ImageDescriptor imageDescriptor, boolean autoLifecycle) {
        this(name, consoleType, imageDescriptor, null, autoLifecycle);
    }

    /**
     * Constructs a console with the given name, type, image, encoding and lifecycle.
     * 
     * @param name name to display for this console
     * @param consoleType console type identifier or <code>null</code>
     * @param imageDescriptor image to display for this console or <code>null</code>
     * @param encoding the encoding that should be used to render the text, or <code>null</code>
     * 	if the system default encoding should be used
     * @param autoLifecycle whether lifecycle methods should be called automatically
     *  when this console is added/removed from the console manager
     */
    public RubyScriptingConsole(String name, String consoleType, ImageDescriptor imageDescriptor, String encoding, boolean autoLifecycle) {
        super(name, consoleType, imageDescriptor, autoLifecycle);
        if (encoding != null) {
            fEncoding = encoding;
        }
        openStreams = new ArrayList<Object>();
        inputStream = new IOConsoleInputStream(this);
        synchronized (openStreams) {
        	openStreams.add(inputStream);	
		}
        
        partitioner = new IOConsolePartitioner(inputStream, this);
        partitioner.connect(getDocument());
    }
    
    /**
     * Constructs a console with the given name, type, and image with the workbench's
     * default encoding. Lifecycle methods will be called when this console is
     * added/removed from the console manager.
     * 
     * @param name name to display for this console
     * @param consoleType console type identifier or <code>null</code>
     * @param imageDescriptor image to display for this console or <code>null</code>
     */
    public RubyScriptingConsole(String name, String consoleType, ImageDescriptor imageDescriptor) {
        this(name, consoleType, imageDescriptor, true);
    }    
    
    /**
     * Constructs a console with the given name and image. Lifecycle methods
     * will be called when this console is added/removed from the console manager.
     * This console will have an unspecified (<code>null</code>) type.
     * 
     * @param name name to display for this console
     * @param imageDescriptor image to display for this console or <code>null</code>
     */
    public RubyScriptingConsole(String name, ImageDescriptor imageDescriptor) {
        this(name, null, imageDescriptor);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.console.IConsole#createPage(org.eclipse.ui.console.IConsoleView)
     */
    public IPageBookViewPage createPage(IConsoleView view) {
        consolePage = new IOConsolePage(this, view);
        assist = new ContentAssistant();
        assist.setContentAssistProcessor(new IContentAssistProcessor() {
			
			private String lastError;
			private ContextInformationValidator contextInfoValidator;
			{
				contextInfoValidator = new ContextInformationValidator(this);
			}

			public String getErrorMessage() {
				return lastError;
			}
			
			public IContextInformationValidator getContextInformationValidator() {
				return contextInfoValidator;
			}
			
			public char[] getContextInformationAutoActivationCharacters() {
				return null;
			}
			
			public char[] getCompletionProposalAutoActivationCharacters() {
				return null;
			}
			
			public IContextInformation[] computeContextInformation(ITextViewer viewer,
					int offset) {
				lastError = Messages.RubyScriptingConsole_no_context_error_message;
				return null;
			}
			
			public ICompletionProposal[] computeCompletionProposals(
					ITextViewer textViewer, int documentOffset) {
				IDocument document = textViewer.getDocument();
				int currOffset = documentOffset - 1;

				try {
					String currWord = EMPTY;
					char currChar;
					while (currOffset > 0
							&& !Character.isWhitespace(currChar = document
									.getChar(currOffset))) {
						currWord = currChar + currWord;
						currOffset--;
					}
			        List<?> suggestions = new LinkedList<String>();
					IDocument doc = getDocument();
					Readline.getCompletor(Readline.getHolder(runtime)).complete(doc.get(), doc.getLength(),  suggestions);

					ICompletionProposal[] proposals = null;
					if (suggestions.size() > 0) {
						proposals = buildProposals(suggestions, currWord,
								documentOffset - currWord.length());
						lastError = null;
					}
					return proposals;
				} catch (BadLocationException e) {
					e.printStackTrace();
					lastError = e.getMessage();
					return null;
				}
			}

			private ICompletionProposal[] buildProposals(List<?> suggestions,
					String replacedWord, int offset) {
				ICompletionProposal[] proposals = new ICompletionProposal[suggestions
						.size()];
				int index = 0;
				for (Object suggestion : suggestions) {
					String currSuggestion = suggestion.toString();
					proposals[index] = new CompletionProposal(currSuggestion, offset,
							replacedWord.length(), currSuggestion.length());
					index++;
				}
				return proposals;
			}
		}, "org.eclipse.ui.console.io_console_input_partition_type"); //$NON-NLS-1$
		return consolePage;
    }
    
    /**
     * Creates and returns a new output stream which may be used to write to this console.
     * A console may be connected to more than one output stream at once. Clients are
     * responsible for closing any output streams created on this console.
     * <p>
     * Clients should avoid writing large amounts of output to this stream in the UI
     * thread. The console needs to process the output in the UI thread and if the client
     * hogs the UI thread writing output to the console, the console will not be able
     * to process the output.
     * </p>
     * @return a new output stream connected to this console
     */
    public IOConsoleOutputStream newOutputStream() {
        IOConsoleOutputStream outputStream = new IOConsoleOutputStream(this);
        outputStream.setEncoding(fEncoding);
        synchronized(openStreams) {
            openStreams.add(outputStream);
        }
        return outputStream;
    }
    
    /**
     * Returns the input stream connected to the keyboard.
     * 
     * @return the input stream connected to the keyboard.
     */
    public IOConsoleInputStream getInputStream() {
        return inputStream;
    }

    /**
     * Returns this console's document partitioner.
     * 
     * @return this console's document partitioner
     */
    protected IConsoleDocumentPartitioner getPartitioner() {
        return partitioner;
    }

	/**
	 * Returns the maximum number of characters that the console will display at
	 * once. This is analogous to the size of the text buffer this console
	 * maintains.
	 * 
	 * @return the maximum number of characters that the console will display
	 */
	public int getHighWaterMark() {
	    return partitioner.getHighWaterMark();
	}
	
	/**
	 * Returns the number of characters that will remain in this console
	 * when its high water mark is exceeded.
	 *  
	 * @return the number of characters that will remain in this console
	 *  when its high water mark is exceeded
	 */
	public int getLowWaterMark() {
		return partitioner.getLowWaterMark();
	}
	
	/**
	 * Sets the text buffer size for this console. The high water mark indicates
	 * the maximum number of characters stored in the buffer. The low water mark
	 * indicates the number of characters remaining in the buffer when the high
	 * water mark is exceeded.
	 * 
	 * @param low the number of characters remaining in the buffer when the high
	 *  water mark is exceeded (if -1 the console does not limit output)
	 * @param high the maximum number of characters this console will cache in
	 *  its text buffer (if -1 the console does not limit output)
	 * @exception IllegalArgumentException if low >= high & low != -1
	 */
	public void setWaterMarks(int low, int high) {
        if (low >= 0) {
    	    if (low >= high) {
    	        throw new IllegalArgumentException("High water mark must be greater than low water mark"); //$NON-NLS-1$
    	    }
        }
		partitioner.setWaterMarks(low, high);
	}

    /**
     * Check if all streams connected to this console are closed. If so,
     * notify the partitioner that this console is finished. 
     */
    private void checkFinished() {
        if (openStreams.isEmpty()) {
            partitioner.streamsClosed();
        }
    }
    
    /**
     * Notification that an output stream connected to this console has been closed.
     * 
     * @param stream stream that closed
     */
    void streamClosed(IOConsoleOutputStream stream) {
    	synchronized (openStreams) {
            openStreams.remove(stream);
            checkFinished();
		}
    }
    
    /**
     * Notification that the input stream connected to this console has been closed.
     * 
     * @param stream stream that closed
     */
    void streamClosed(IOConsoleInputStream stream) {
    	synchronized (openStreams) {
            openStreams.remove(stream);
            checkFinished();
		}
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.console.TextConsole#clearConsole()
     */
    public void clearConsole() {
        if (partitioner != null) {
            partitioner.clearBuffer();
        }
    }
    
    /**
     * Disposes this console.
     */
    protected void dispose() {
        super.dispose();
        partitioner.disconnect();
        //make a copy of the open streams and close them all
        //a copy is needed as close the streams results in a callback that 
        //removes the streams from the openStreams collection (bug 152794)
        Object[] allStreams= openStreams.toArray();
        for (int i = 0; i < allStreams.length; i++) {
        	Object stream = allStreams[i];
        	if (stream instanceof IOConsoleInputStream) {
        		IOConsoleInputStream is = (IOConsoleInputStream) stream;
        		try {
        			is.close();
        		} catch (IOException e) {
        		}
        	} else if (stream instanceof IOConsoleOutputStream) {
        		IOConsoleOutputStream os = (IOConsoleOutputStream) stream;
        		try {
        			os.close();
        		} catch (IOException e) {
        		}					
        	}
        }
        inputStream = null;
        Display display = getDisplay();
        if(display.isDisposed()) return;
		display.syncExec(new Runnable() {
			public void run() {
				consolePage.dispose();
			}
		});
    }

    /**
     * Returns the encoding for this console, or <code>null</code> to indicate
     * default encoding.
     * 
     * @return the encoding set for this console, or <code>null</code> to indicate
     * 	default encoding
     * @since 3.3
     */
	public String getEncoding() {
		return fEncoding;
	}
	
	@Override
	protected void init() {	
        input = getInputStream();
        output = newOutputStream();
        error = newOutputStream();
        Display display = getDisplay();
        display.asyncExec(new Runnable() {
			public void run() {
		        init_in_swt_thread();
			}
		});

        createJob();
	}
	
	private void createJob() {
		final Job initJob = new Job(Messages.RubyScriptingConsole_initialize_runtime_message){
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				RubyInstanceConfig config = new RubyInstanceConfig();
				config.setArgv(new String[]{ARGS});
				config.setJRubyHome(getJRubyHomeFromBundle());
		        config.setObjectSpaceEnabled(true);
				config.setOutput(new PrintStream(output));
				config.setInput(input);
				config.setError(new PrintStream(error));
				runtime = JavaEmbedUtils.initialize(new ArrayList<Object>(),config);
				defineGlobalValues();
				return Status.OK_STATUS;
			}

			private void defineGlobalValues() {
				Map<String, Object> glovalValues = ScriptingUIActivator.getDefault().getScriptingService().getGlovalValues();
				if(glovalValues != null){
					for(Map.Entry<String, Object> value:glovalValues.entrySet()){
						IRubyObject rubyObject = JavaEmbedUtils.javaToRuby(runtime, value.getValue());
						runtime.defineGlobalConstant(value.getKey(), rubyObject);
					}
				}
				IRubyObject rubyBoard = JavaEmbedUtils.javaToRuby(runtime, new BoardAccessor());
		        runtime.getGlobalVariables().defineReadonly(KEY_OF_BOARD_ACCESSOR, new ValueAccessor(rubyBoard));		        
				IRubyObject rubyBoardPart = JavaEmbedUtils.javaToRuby(runtime, new BoardCommandExecuterAccessor());
		        runtime.getGlobalVariables().defineReadonly(KEY_OF_BOARD_COMMAND_EXECUTER_ACCESSOR, new ValueAccessor(rubyBoardPart));		        
		        runtime.getGlobalVariables().defineReadonly(KEY_OF_RUNTIME, new ValueAccessor(runtime.newFixnum(System.identityHashCode(runtime))));		        
			}

			private String getJRubyHomeFromBundle() {
				try {
					String path = new File(FileLocator.getBundleFile(Platform.getBundle(BUNDLE_OF_ORG_JRUBY)),PATH_OF_JRUBY_HOME).getAbsolutePath();
					return path;
				} catch (IOException e) {
				}
				return null;
			}
		};
		initJob.schedule();
		Job reader = new Job(Messages.RubyScriptingConsole_run_irb_message){
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					initJob.join();
				} catch (InterruptedException e) {
					return new Status(Status.WARNING, ScriptingUIActivator.PLUGIN_ID, Messages.RubyScriptingConsole_join_error_title, e);
				}

		        RubyRuntimeAdapter runtimeAdapter = JavaEmbedUtils.newRuntimeAdapter();
				String script = IOUtils.getStringFromResource(INIT_SCRIPT_RB);
				runtimeAdapter.eval(runtime, script);
				shutdown();
				return Status.OK_STATUS;
			}
			
        };
        reader.setSystem(true);
        reader.schedule();
	}

	private final class CaretMoveListener implements
			IDocumentListener {
		private final StyledText text;

		private CaretMoveListener(StyledText text) {
			this.text = text;
		}

		public void documentChanged(DocumentEvent event) {
			text.setCaretOffset(event.getDocument().getLength());
		}

		public void documentAboutToBeChanged(DocumentEvent event) {
		}
	}

	private final class ControlFromKey implements VerifyKeyListener,ICompletionListener{

		private boolean completing;

		public void verifyKey(VerifyEvent event) {
			switch(event.keyCode){
			case SWT.ARROW_UP:
				event.doit = false;
				if(completing) break;
				upAction();
				break;
			case SWT.ARROW_DOWN:
				event.doit = false;
				if(completing) break;
				downAction();
				break;
			case SWT.ARROW_LEFT:
				event.doit = isReadOnly(((StyledText)event.widget).getCaretOffset() - 1);
				break;
			case SWT.ARROW_RIGHT:
				event.doit = isReadOnly(((StyledText)event.widget).getCaretOffset() + 1);
				break;				
			case '\r':
				IDocument doc = getDocument();
				try {
					String text = doc.get(getLastOffset(), doc.getLength()-getLastOffset());
					if( ! completing){
						Readline.getHistory(Readline.getHolder(runtime)).addToHistory(text);
					}
				} catch (BadLocationException e) {
				}
				break;
			case '\t':
				event.doit = false;
				if(completing) break;
				completeAction(event);
			default:
			}
		}

		private boolean isReadOnly(int nextCaretOffset) {
			ITypedRegion partition = getPartitioner().getPartition(nextCaretOffset);
			if (partition instanceof IOConsolePartition) {
				IOConsolePartition ioPartition = (IOConsolePartition) partition;
				return !ioPartition.isReadOnly();
			}
			return true;
		}

		private void downAction() {
//	        if (!Readline.getHistory(Readline.getHolder(runtime)).next()) return;
	        
	        History history = Readline.getHistory(Readline.getHolder(runtime));
			if (history.next()){ // at end
//	            history.previous(); // undo check
	            String oldLine = history.current().trim();
		        write(oldLine);
	        }
		}

		private void upAction() {
//	        if (!Readline.getHistory(Readline.getHolder(runtime)).next()) // at end
//	            return;
//	        else
//	            Readline.getHistory(Readline.getHolder(runtime)).previous(); // undo check
	        History history = Readline.getHistory(Readline.getHolder(runtime));
			if (!history.previous()) return;
	        
	        String oldLine = history.current().trim();
	        write(oldLine);
		}

		private void write(String oldLine) {
			try {
				int last = getLastestIndex();
				ITypedRegion partition = getPartitioner().getPartition(last);
				int lastOffset;
				if(IOConsolePartition.OUTPUT_PARTITION_TYPE.equals(partition.getType())){
					lastOffset = last;
					getDocument().replace(lastOffset, 0, oldLine);
				}else{
					lastOffset = partition.getOffset();
					if(lastOffset == last){
						getDocument().replace(lastOffset, 0, oldLine);		
					}else{
						getDocument().replace(lastOffset, last - lastOffset, EMPTY); //$NON-NLS-1$
						getDocument().replace(lastOffset, 0, oldLine);
					}
				}
			} catch (BadLocationException e) {
				IStatus status = ScriptingUIActivator.createErrorStatus(e);
				StatusManager.getManager().handle(status );
			}
		}

		private int getLastOffset() {
			int last = getLastestIndex();
			ITypedRegion partition = getPartitioner().getPartition(last);
			return partition.getOffset();
		}

		private int getLastestIndex() {
			IDocument d = getDocument();
			int last = d.getLength();
			return last;
		}

		public void assistSessionEnded(ContentAssistEvent event) {
			completing = false;
		}

		public void assistSessionStarted(ContentAssistEvent event) {
			completing = true;
		}

		public void selectionChanged(ICompletionProposal proposal,
				boolean smartToggle) {
		}
	}

	public class BoardAccessor{
		public Object getBoard() {
			final Object[] ret = new Object[1];
			getDisplay().syncExec(new Runnable() {
				public void run() {
					KanbanView kanbanView = getKanbanView();
					ret[0] = kanbanView.getAdapter(Board.class);
				}
			});
			return ret[0];
		}
	}

	public class BoardCommandExecuterAccessor{
		public Object getExecuter() {
			final Object[] ret = new Object[1];
			getDisplay().syncExec(new Runnable() {
				public void run() {
					KanbanView kanbanView = getKanbanView();
					ret[0] = kanbanView.getAdapter(IBoardCommandExecuter.class);
				}
			});
			return ret[0];
		}
	}

	
	private void shutdown() {
		JavaEmbedUtils.terminate(runtime);
        runtime.getGlobalVariables().defineReadonly(KEY_OF_BOARD_ACCESSOR, null);		        
		runtime = null;
		input = null;
		output = null;
		error = null;

		Display display = getDisplay();
		if(display.isDisposed()) return;
		display.asyncExec(new Runnable() {
			public void run() {
				getConsoleManager().removeConsoles(new IConsole[]{RubyScriptingConsole.this});
		        TextConsoleViewer viewer = consolePage.getViewer();
		        Control control = viewer.getControl();
		        if (control instanceof StyledText) {
		        	StyledText text = (StyledText) control;
					text.removeVerifyKeyListener(keyListener);
					keyListener = null;
				}
				assist.uninstall();
			}
		});
	}

	private Display getDisplay() {
		return PlatformUI.getWorkbench().getDisplay();
	}

	private IConsoleManager getConsoleManager() {
		return ConsolePlugin.getDefault().getConsoleManager();
	}

	private void init_in_swt_thread() {
		Display display = getDisplay();
		ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
		Color outputColor = colorRegistry.get(OUTPUT_STREAM_COLOR);
		if(outputColor == null){
			outputColor = display.getSystemColor(SWT.COLOR_BLUE);
			colorRegistry.put(OUTPUT_STREAM_COLOR, outputColor.getRGB());
		}
		output.setColor(outputColor);
        Color errorColor = colorRegistry.get(ERROR_STREAM_COLOR);
        if(errorColor == null){
	        errorColor = display.getSystemColor(SWT.COLOR_RED);
	        colorRegistry.put(ERROR_STREAM_COLOR, errorColor.getRGB());
        }
        error.setColor(errorColor);
        assist.install(consolePage.getViewer());
        assist.enableAutoActivation(true);
        assist.enableAutoInsert(true);
        TextConsoleViewer viewer = consolePage.getViewer();
        Control control = viewer.getControl();
        if (control instanceof StyledText) {
        	final StyledText text = (StyledText) control;
			keyListener = new ControlFromKey();
			assist.addCompletionListener(keyListener);
			text.addVerifyKeyListener(keyListener);
	        getDocument().addDocumentListener(new CaretMoveListener(text));
		}
        try {
			String version = ScriptingUIActivator.getDefault().getBundle().getHeaders().get(KEY_OF_BUNDLE_VERSION).toString();
			String message = String.format(Messages.RubyScriptingConsole_initialize_message,version);
			output.write(message);
		} catch (IOException e) {
		}
	}

    protected void completeAction(KeyEvent event) {
        if (Readline.getCompletor(Readline.getHolder(runtime)) == null) return;
        assist.showPossibleCompletions();
    }

	private KanbanView getKanbanView() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		KanbanView view = null;
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				view = (KanbanView) page.findView(KanbanView.ID);
			}
		}
		KanbanView kanbanView = view;
		return kanbanView;
	}

}
