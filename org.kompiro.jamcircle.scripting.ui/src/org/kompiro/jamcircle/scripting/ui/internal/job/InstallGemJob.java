package org.kompiro.jamcircle.scripting.ui.internal.job;

import java.io.*;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.console.*;
import org.kompiro.jamcircle.scripting.ui.*;
import org.kompiro.jamcircle.scripting.util.JRubyUtil;

public class InstallGemJob extends Job {
	private static final String INSTALL_GEM_JOB_NAME = "Run gem install";
	private static final String CONSOLE_NAME_OF_GEM = "Gem Console";
	private static final String COMMAND_NAME_OF_JGEM = "jgem";
	private final String target;
	private final JRubyUtil jRubyUtil = new JRubyUtil();
	private IOConsole console;

	public InstallGemJob(String target) {
		super(INSTALL_GEM_JOB_NAME);
		this.target = target;
		console = new IOConsole(CONSOLE_NAME_OF_GEM, getRubyAddImageDescriptor());
		getConsoleManager().addConsoles(new IConsole[] { console });

	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		ProcessBuilder builder = createProcessBuilder();

		IOConsoleOutputStream outputStream = null;
		IOConsoleOutputStream errorStream = null;
		try {
			Process process = builder.start();
			outputStream = console.newOutputStream();
			outputStream.setColor(ScriptingColorEnum.OUTPUT_STREAM_COLOR.getColor());
			errorStream = console.newOutputStream();
			errorStream.setColor(ScriptingColorEnum.ERROR_STREAM_COLOR.getColor());
			InputStreamThread it = new InputStreamThread(process.getInputStream(), new PrintStream(outputStream));
			InputStreamThread et = new InputStreamThread(process.getErrorStream(), new PrintStream(errorStream));
			it.start();
			et.start();
			process.waitFor();

		} catch (IOException e) {
			ScriptingUIActivator.logError(e);
		} catch (InterruptedException e) {
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					ScriptingUIActivator.logError(e);
				}
			}
			if (errorStream != null) {
				try {
					errorStream.close();
				} catch (IOException e) {
					ScriptingUIActivator.logError(e);
				}
			}
		}
		return Status.OK_STATUS;
	}

	private ProcessBuilder createProcessBuilder() {
		String jrubyHome = jRubyUtil.getJRubyHomeFromBundle();
		String scriptBin = jrubyHome + File.separator + "bin" + File.separator;
		String scriptPath = scriptBin + COMMAND_NAME_OF_JGEM;

		ProcessBuilder builder = new ProcessBuilder(scriptPath, "install", target);
		String envPath = System.getenv().get("PATH");
		builder.environment().put("PATH", envPath + File.pathSeparator + scriptBin);
		builder.environment().put("JRUBY_HOME", jrubyHome);
		String gemHome = jRubyUtil.getGemHome();
		builder.environment().put("GEM_HOME", gemHome);
		return builder;
	}

	private ImageDescriptor getRubyAddImageDescriptor() {
		return getImageRegistry().getDescriptor(ScriptingImageEnum.RUBY_ADD.getPath());
	}

	private ImageRegistry getImageRegistry() {
		return getActivator().getImageRegistry();
	}

	private ScriptingUIActivator getActivator() {
		return ScriptingUIActivator.getDefault();
	}

	private IConsoleManager getConsoleManager() {
		return ConsolePlugin.getDefault().getConsoleManager();
	}

	class InputStreamThread extends Thread {

		private BufferedReader br;

		private PrintStream stream;

		public InputStreamThread(InputStream is, PrintStream stream) {
			this.br = new BufferedReader(new InputStreamReader(is));
			this.stream = stream;
		}

		@Override
		public void run() {
			for (;;) {
				try {
					String line = br.readLine();
					if (line == null)
						break;
					stream.println(line);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}
