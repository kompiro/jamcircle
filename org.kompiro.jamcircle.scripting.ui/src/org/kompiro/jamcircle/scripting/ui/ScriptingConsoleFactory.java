package org.kompiro.jamcircle.scripting.ui;

import java.awt.EventQueue;
import java.io.PrintStream;
import java.util.ArrayList;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.*;
import org.eclipse.ui.console.*;
import org.jruby.*;
import org.jruby.demo.TextAreaReadline.Channel;
import org.jruby.ext.Readline;
import org.jruby.internal.runtime.ValueAccessor;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.Arity;
import org.jruby.runtime.Block;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.callback.Callback;
import org.kompiro.jamcircle.kanban.ui.KanbanView;
import org.kompiro.jamcircle.scripting.ui.internal.eclipse.ui.console.IOConsole;
import org.kompiro.jamcircle.scripting.ui.internal.eclipse.ui.console.IOConsoleOutputStream;

public class ScriptingConsoleFactory implements IConsoleFactory {

	private static final String OUTPUT_STREAM_COLOR = "ScriptingConsoleFactory.OutputStreamColor";
	private IOConsole console = null;
	
	public ScriptingConsoleFactory(){
        Color outputColor = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLUE);
        ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
		colorRegistry.put(OUTPUT_STREAM_COLOR, outputColor.getRGB());
	}
	
	public void openConsole() {
		if(console == null){
	        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	        KanbanView view = null;
	        if (window != null) {
	            IWorkbenchPage page = window.getActivePage();
	            if (page != null) {
                    view = (KanbanView) page.findView(KanbanView.ID);
	            }
	        }
	        final KanbanView kanbanView = view;
			Job openJob = new Job("opening scripting console..."){

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					ImageDescriptor imageDescriptor = getImageRegistry().getDescriptor(ScriptingImageEnum.SCRIPT_GEAR.toString());
					console = new IOConsole("JAMCircle Scripting Console", imageDescriptor);
			        final RubyInstanceConfig config = new RubyInstanceConfig() {{
			            setInput(console.getInputStream());
			            IOConsoleOutputStream stream = console.newOutputStream();
						stream.setColor(JFaceResources.getColorRegistry().get(OUTPUT_STREAM_COLOR));
						PrintStream newOutput = new PrintStream(stream);
						setOutput(newOutput);
			            setError(newOutput);
			            
//			            setObjectSpaceEnabled(true); // useful for code completion inside the IRB
			        }};
			        config.setArgv(new String[]{"-Ku"});
			        final Ruby runtime = JavaEmbedUtils.initialize(new ArrayList<Object>(),config);
			        final RubyRuntimeAdapter newRuntimeAdapter = JavaEmbedUtils.newRuntimeAdapter();
			        runtime.getLoadService().require("readline");
			        RubyModule readlineM = runtime.fastGetModule("Readline");

			        readlineM.defineModuleFunction("readline", new Callback() {
			            public IRubyObject execute(IRubyObject recv, IRubyObject[] args, Block block) {
//			                String line = readLine(args[0].toString());
//			                if (line != null) {
//			                    return RubyString.newUnicodeString(runtime, line);
//			                } else {
			                    return runtime.getNil();
//			                }
			            }
			            public Arity getArity() { return Arity.twoArguments(); }
			        });

			        runtime.getGlobalVariables().defineReadonly("$$", new ValueAccessor(runtime.newFixnum(System.identityHashCode(runtime))));
			        IRubyObject rubyBoard = JavaEmbedUtils.javaToRuby(runtime, kanbanView.getBoard());
			        runtime.getGlobalVariables().defineReadonly("$board", new ValueAccessor(rubyBoard));
			        runtime.getLoadService().init(new ArrayList<Object>());
			        Job reader = createReaderJob(runtime, newRuntimeAdapter);
			        reader.schedule();
					ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{console});
					return Status.OK_STATUS;
				}

				private Job createReaderJob(final Ruby runtime,
						final RubyRuntimeAdapter newRuntimeAdapter) {
					Job reader = new Job("console reader"){
						@Override
						protected IStatus run(IProgressMonitor monitor) {
			                console.activate();
			                String script = "include_class 'org.kompiro.jamcircle.kanban.model.mock.Card'; \n" +
							"include_class 'org.kompiro.jamcircle.kanban.model.mock.Lane'; \n" +
							"require 'irb';" +
							" require 'irb/completion';" +
							" IRB.start";
			                
			                newRuntimeAdapter.eval(runtime, script);
							return Status.OK_STATUS;
						}
						
			        };
			        reader.setSystem(true);
					return reader;
				}
				
			};
			openJob.schedule();
		}
	}

	private ImageRegistry getImageRegistry() {
		return getActivator().getImageRegistry();
	}

	private ScriptingUIActivator getActivator() {
		return ScriptingUIActivator.getDefault();
	}

//    public String readLine(final String prompt) {
//        if (EventQueue.isDispatchThread()) {
//            throw runtime.newThreadError("Cannot call readline from event dispatch thread");
//        }
//
//        EventQueue.invokeLater(new Runnable() {
//           public void run() {
//               append(prompt.trim(), promptStyle);
//               append(" ", inputStyle); // hack to get right style for input
//               area.setCaretPosition(area.getDocument().getLength());
//               startPos = area.getDocument().getLength();
//               Readline.getHistory(Readline.getHolder(runtime)).moveToEnd();
//            }
//        });
//        
//        final String line = (String)inputJoin.call(Channel.GET_LINE, null);
//        if (line.length() > 0) {
//            return line.trim();
//        } else {
//            return null;
//        }
//    }

}
