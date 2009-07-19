package org.kompiro.jamcircle.scripting.ui;

import java.io.PrintStream;
import java.util.ArrayList;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.*;
import org.eclipse.ui.console.*;
import org.jruby.*;
import org.jruby.internal.runtime.ValueAccessor;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;
import org.kompiro.jamcircle.kanban.ui.KanbanView;
import org.kompiro.jamcircle.scripting.ui.internal.eclipse.ui.console.IOConsole;
import org.kompiro.jamcircle.scripting.ui.internal.eclipse.ui.console.IOConsoleOutputStream;

public class ScriptingConsoleFactory implements IConsoleFactory {

	private IOConsole console = null;
	
	public void openConsole() {
		if(console == null){
			ImageDescriptor imageDescriptor = getImageRegistry().getDescriptor(ScriptingImageEnum.SCRIPT_GEAR.toString());
			console = new IOConsole("JAMCircle Scripting Console", imageDescriptor);
	        final RubyInstanceConfig config = new RubyInstanceConfig() {{
	            setInput(console.getInputStream());
	            IOConsoleOutputStream stream = console.newOutputStream();
				PrintStream newOutput = new PrintStream(stream);
				setOutput(newOutput);
	            setError(newOutput);
	            
//	            setObjectSpaceEnabled(true); // useful for code completion inside the IRB
	        }};
	        final Ruby runtime = JavaEmbedUtils.initialize(new ArrayList<Object>(),config);
	        final RubyRuntimeAdapter newRuntimeAdapter = JavaEmbedUtils.newRuntimeAdapter();

	        runtime.getGlobalVariables().defineReadonly("$$", new ValueAccessor(runtime.newFixnum(System.identityHashCode(runtime))));
	        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	        KanbanView kanbanView = null;
	        if (window != null) {
	            IWorkbenchPage page = window.getActivePage();
	            if (page != null) {
                    kanbanView = (KanbanView) page.findView(KanbanView.ID);
	            }
	        }
	        IRubyObject rubyBoard = JavaEmbedUtils.javaToRuby(runtime, kanbanView.getBoard());
	        runtime.getGlobalVariables().defineReadonly("$board", new ValueAccessor(rubyBoard));
	        runtime.getLoadService().init(new ArrayList<Object>());
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
	        reader.schedule();
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{console});
		}
	}

	private ImageRegistry getImageRegistry() {
		return getActivator().getImageRegistry();
	}

	private ScriptingUIActivator getActivator() {
		return ScriptingUIActivator.getDefault();
	}

}
