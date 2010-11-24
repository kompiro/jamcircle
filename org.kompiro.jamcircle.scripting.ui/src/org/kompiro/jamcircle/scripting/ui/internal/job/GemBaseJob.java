package org.kompiro.jamcircle.scripting.ui.internal.job;

import java.io.*;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.console.*;
import org.kompiro.jamcircle.scripting.ui.*;
import org.kompiro.jamcircle.scripting.util.JRubyUtil;

public abstract class GemBaseJob extends Job {
	private static final String GEM_JOB_NAME = "Run gem ";
	private static final String CONSOLE_NAME_OF_GEM = "Gem Console";
	private static final String COMMAND_NAME_OF_JGEM = "jgem";
	private String target;
	private final JRubyUtil jRubyUtil = new JRubyUtil();

	public GemBaseJob() {
		super("");
		setName(GEM_JOB_NAME + getCommand());
	}

	protected IStatus run(IProgressMonitor monitor) {
		ProcessBuilder builder = createProcessBuilder();
		IOConsole console = new IOConsole(CONSOLE_NAME_OF_GEM, getRubyAddImageDescriptor());
		getConsoleManager().addConsoles(new IConsole[] { console });

		IOConsoleOutputStream outputStream = null;
		outputStream = console.newOutputStream();
		outputStream.setColor(ScriptingColorEnum.OUTPUT_STREAM_COLOR.getColor());
		IOConsoleOutputStream errorStream = null;
		errorStream = console.newOutputStream();
		errorStream.setColor(ScriptingColorEnum.ERROR_STREAM_COLOR.getColor());

		try {
			Process process = builder.start();
			InputStreamThread it = new InputStreamThread(process.getInputStream(), new PrintStream(outputStream));
			InputStreamThread et = new InputStreamThread(process.getErrorStream(), new PrintStream(errorStream));
			it.start();
			et.start();
			process.waitFor();

		} catch (IOException e) {
			ScriptingUIActivator.logError(e);
			e.printStackTrace(new PrintStream(errorStream));
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
		if (Platform.getOS().equals("win32")) {
			scriptPath = scriptPath + ".bat";
		}

		ProcessBuilder builder;
		if (target == null) {
			builder = new ProcessBuilder(scriptPath, getCommand());

		} else {
			builder = new ProcessBuilder(scriptPath, getCommand(), target);
		}
		String envPath = System.getenv().get("PATH");
		builder.environment().put("PATH", envPath + File.pathSeparator + scriptBin);
		builder.environment().put("JRUBY_HOME", jrubyHome);
		String gemHome = jRubyUtil.getGemHome();
		builder.environment().put("GEM_HOME", gemHome);
		return builder;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	private ImageDescriptor getRubyAddImageDescriptor() {
		ImageRegistry imageRegistry = getImageRegistry();
		if (imageRegistry == null)
			return null;
		return imageRegistry.getDescriptor(ScriptingImageEnum.RUBY_ADD.getPath());
	}

	private ImageRegistry getImageRegistry() {
		ScriptingUIActivator activator = getActivator();
		if (activator == null)
			return null;
		return activator.getImageRegistry();
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

	protected abstract String getCommand();
}
