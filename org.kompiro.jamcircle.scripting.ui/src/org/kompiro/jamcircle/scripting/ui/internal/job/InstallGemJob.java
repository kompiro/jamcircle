package org.kompiro.jamcircle.scripting.ui.internal.job;

import java.io.*;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.kompiro.jamcircle.scripting.util.JRubyUtil;

public class InstallGemJob extends Job {
	private final String target;
	private final JRubyUtil jRubyUtil = new JRubyUtil();

	public InstallGemJob(String target) {
		super("Run gem install");
		this.target = target;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		String jrubyHome = jRubyUtil.getJRubyHomeFromBundle();
		String scriptBin = jrubyHome + File.separator + "bin" + File.separator;
		String scriptPath = scriptBin + "jgem";
		final ProcessBuilder builder = new ProcessBuilder(scriptPath, "install", target);
		String envPath = System.getenv().get("PATH");
		builder.environment().put("PATH", envPath + File.pathSeparator + scriptBin);
		builder.environment().put("JRUBY_HOME", jrubyHome);
		String gemHome = jRubyUtil.getGemHome();
		builder.environment().put("GEM_HOME", gemHome);
		try {
			Process process = builder.start();
			InputStreamThread it = new InputStreamThread(process.getInputStream(), System.out);
			InputStreamThread et = new InputStreamThread(process.getErrorStream(), System.err);
			it.start();
			et.start();
			process.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return Status.OK_STATUS;
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
