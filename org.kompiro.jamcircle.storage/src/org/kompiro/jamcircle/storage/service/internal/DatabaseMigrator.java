package org.kompiro.jamcircle.storage.service.internal;

import java.io.*;

import org.h2.tools.RunScript;

public class DatabaseMigrator {

	private static final String TEMP_SCRIPT = "backup.sql";
	private PrintStream sysOut = System.out;
	private boolean quiet;

	public DatabaseMigrator() {
	}

	/**
	 * Migrate a database.
	 * 
	 * @param oldH2Jar
	 *            the old JAR file
	 * @param file
	 *            the database file (must end with .data.db) or directory
	 * @param recursive
	 *            if the file parameter is in fact a directory (in which
	 *            case the directory is scanned recursively)
	 * @param user
	 *            the user name of the database
	 * @param password
	 *            the password
	 * @param runQuiet
	 *            to run in quiet mode
	 * @throws Exception
	 *             if conversion fails
	 */
	public void execute(File oldH2Jar, File file, boolean recursive, String user, String password, boolean runQuiet)
			throws Exception {
		String pathToJavaExe = getJavaExecutablePath();
		this.quiet = runQuiet;
		if (file.isDirectory() && recursive) {
			for (File f : file.listFiles()) {
				execute(oldH2Jar, f, recursive, user, password, runQuiet);
			}
			return;
		}
		if (!file.getName().endsWith(".data.db")) {
			return;
		}
		println("Migrating " + file.getName());
		String url = "jdbc:h2:" + file.getAbsolutePath();
		url = url.substring(0, url.length() - ".data.db".length());
		
		if(password == null || password.equals("")){
			exec(wrapQuote(pathToJavaExe),
					"-Xmx128m",
					"-cp", wrapQuote(oldH2Jar.getAbsolutePath()),
					"org.h2.tools.Script",
					"-script", TEMP_SCRIPT,
					"-url", wrapQuote(url),
					"-user", user 
			);
		}else{
			exec(wrapQuote(pathToJavaExe),
					"-Xmx128m",
					"-cp", wrapQuote(oldH2Jar.getAbsolutePath()),
					"org.h2.tools.Script",
					"-script", TEMP_SCRIPT,
					"-url", wrapQuote(url),
					"-user", user,
					"-password", password 
			);
		}
		file.renameTo(new File(file.getAbsoluteFile() + ".backup"));
		RunScript.execute(url, user, password, TEMP_SCRIPT, "UTF-8", true);
		new File(TEMP_SCRIPT).delete();
	}

	private String getJavaExecutablePath() {
		String pathToJava;
		if (System.getProperty("os.name").startsWith("Windows")) {
			pathToJava = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java.exe";
		} else {
			pathToJava = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		}
		if (!new File(pathToJava).exists()) {
			// Fallback to old behaviour
			pathToJava = "java";
		}
		return pathToJava;
	}

	private String wrapQuote(String wrapped) {
		return "\"" + wrapped + "\"";
	}

	private void println(String s) {
		if (!quiet) {
			sysOut.println(s);
		}
	}

	private void print(String s) {
		if (!quiet) {
			sysOut.print(s);
		}
	}

	private int exec(String... command) {
		try {
			for (String c : command) {
				print(c + " ");
			}
			println("");
			Process p = Runtime.getRuntime().exec(command);
			copyInThread(p.getInputStream(), quiet ? null : sysOut);
			copyInThread(p.getErrorStream(), quiet ? null : sysOut);
			p.waitFor();
			return p.exitValue();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void copyInThread(final InputStream in, final OutputStream out) {
		new Thread() {
			public void run() {
				try {
					while (true) {
						int x = in.read();
						if (x < 0) {
							return;
						}
						if (out != null) {
							out.write(x);
						}
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}.start();
	}

}
