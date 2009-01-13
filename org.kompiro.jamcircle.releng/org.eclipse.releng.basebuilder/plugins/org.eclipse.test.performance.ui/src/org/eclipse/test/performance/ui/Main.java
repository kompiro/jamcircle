/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.performance.ui;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.test.internal.performance.PerformanceTestPlugin;
import org.eclipse.test.internal.performance.results.AbstractResults;
import org.eclipse.test.internal.performance.results.ConfigResults;
import org.eclipse.test.internal.performance.results.DB_Results;
import org.eclipse.test.internal.performance.results.PerformanceResults;
import org.eclipse.test.internal.performance.results.ScenarioResults;
import org.osgi.framework.Bundle;

/**
 * Main class to generate performance results of all scenarios matching a given pattern
 * in one HTML page per component.
 * 
 * @see #printUsage() method to see a detailed parameters usage
 */
public class Main implements IApplication {

/**
 * Prefix of baseline builds displayed in data graphs.
 * This field is set using <b>-baselinePrefix</b> argument.
 * <p>
 * Example:
 *		<pre>-baseline.prefix 3.2_200606291905</pre>
 *
 * @see #currentBuildPrefixes
 */
private String baselinePrefix = null;

/**
 * Root directory where all files are generated.
 * This field is set using <b>-output</b> argument.
 * <p>
 * Example:
 * 	<pre>-output /releng/results/I20070615-1200/performance</pre>
 */
private File outputDir;

/**
 * Root directory where all data are locally stored to speed-up generation.
 * This field is set using <b>-dataDir</b> argument.
 * <p>
 * Example:
 * 	<pre>-dataDir /tmp</pre>
 */
private File dataDir;

/**
 * Arrays of 2 strings which contains config information: name and description.
 * This field is set using <b>-config</b> and/or <b>-config.properties</b> arguments.
 * <p>
 * Example:
 * <pre>
 * 	-config eclipseperflnx3_R3.3,eclipseperfwin2_R3.3,eclipseperflnx2_R3.3,eclipseperfwin1_R3.3,eclipseperflnx1_R3.3
 * 	-config.properties
 * 		"eclipseperfwin1_R3.3,Win XP Sun 1.4.2_08 (2 GHz 512 MB);
 * 		eclipseperflnx1_R3.3,RHEL 3.0 Sun 1.4.2_08 (2 GHz 512 MB);
 * 		eclipseperfwin2_R3.3,Win XP Sun 1.4.2_08 (3 GHz 2 GB);
 * 		eclipseperflnx2_R3.3,RHEL 3.0 Sun 1.4.2_08 (3 GHz 2 GB);
 * 		eclipseperflnx3_R3.3,RHEL 4.0 Sun 1.4.2_08 (3 GHz 2.5 GB)"
 * </pre>
 * Note that:
 * <ul>
 * <li>if only <b>-config</b> is set, then configuration name is used for description </li>
 * <li>if only <b>-config.properties</b> is set, then all configurations defined with this argument are generated
 * <li>if both arguments are defined, then only configurations defined by <b>-config</b> argument are generated,
 * 		<b>-config.properties</b> argument is only used to set the configuration description.</li>
 * </ul>
 */
private String[][] configDescriptors;

/**
 * Scenario pattern used to generate performance results.
 * This field is set using <b>-scenarioPattern</b> argument.
 * <p>
 * Note that this pattern uses SQL conventions, not RegEx ones,
 * which means that '%' is used to match several consecutive characters
 * and '_' to match a single character.
 * <p>
 * Example:
 * 	<pre>-scenario.pattern org.eclipse.%.test</pre>
 */
private String scenarioPattern;

/**
 * A list of prefixes for builds displayed in data graphs.
 * This field is set using <b>-currentPrefix</b> argument.
 * <p>
 * Example:
 * 	<pre>-current.prefix N, I</pre>
 * 
 * @see #baselinePrefix
 */
private List currentBuildPrefixes;

/**
 * A list of prefixes of builds to highlight in displayed data graphs.
 * This field is set using <b>-highlight</b> and/or <b>-highlight.latest</b> arguments.
 * <p>
 * Example:
 * 	<pre>-higlight 3_2</pre>
 */
private List pointsOfInterest;

/**
 * Tells whether only fingerprints has to be generated.
 * This field is set to <code>true</code> if <b>-fingerprints</b> argument is specified.
 * <p>
 * Default is <code>false</code> which means that scenario data
 * will also be generated.
 * 
 * @see #genData
 * @see #genAll
 */
private boolean genFingerPrints = false;

/**
 * Tells whether only fingerprints has to be generated.
 * This field is set to <code>true</code> if <b>-data</b> argument is specified.
 * <p>
 * Default is <code>false</code> which means that fingerprints
 * will also be generated.
 * 
 * @see #genFingerPrints
 * @see #genAll
 */
private boolean genData = false;

/**
 * Tells whether only fingerprints has to be generated.
 * This field is set to <code>false</code>
 * if <b>-fingerprints</b> or <b>-data</b> argument is specified.
 * <p>
 * Default is <code>true</code> which means that scenario data
 * will also be generated.
 * 
 * @see #genData
 * @see #genFingerPrints
 */
private boolean genAll = true;

/**
 * Tells whether information should be displayed in the console while generating.
 * This field is set to <code>true</code> if <b>-print</b> argument is specified.
 * <p>
 * Default is <code>false</code> which means that nothing is print during the generation.
 */
private boolean print = false;

/*
 * Parse the command arguments and create corresponding performance
 * results object.
 */
private PerformanceResults parse(Object argsObject) {
	StringBuffer buffer = new StringBuffer("Parameters used to generate performance results (");
	buffer.append(new SimpleDateFormat().format(new Date(System.currentTimeMillis())));
	buffer.append("):\n");
	String[] args = (String[]) argsObject;
	int i = 0;
	if (args.length == 0) {
		printUsage();
	}

	String currentBuildId = null;
	String baseline = null;
	String jvm = null;
	this.configDescriptors = null;

	while (i < args.length) {
		String arg = args[i];
		if (!arg.startsWith("-")) {
			i++;
			continue;
		}
		if (args.length == i + 1 && i != args.length - 1) {
			System.out.println("Missing value for last parameter");
			printUsage();
		}
		if (arg.equals("-baseline")) {
			baseline = args[i + 1];
			if (baseline.startsWith("-")) {
				System.out.println("Missing value for -baseline parameter");
				printUsage();
			}
			buffer.append("	-baseline = "+baseline+'\n');
			i++;
			continue;
		}
		if (arg.equals("-baseline.prefix")) {
			this.baselinePrefix = args[i + 1];
			if (this.baselinePrefix.startsWith("-")) {
				System.out.println("Missing value for -baseline.prefix parameter");
				printUsage();
			}
			buffer.append("	-baselinePrefix = "+this.baselinePrefix+'\n');
			i++;
			continue;
		}
		if (arg.equals("-current.prefix")) {
			String idPrefixList = args[i + 1];
			if (idPrefixList.startsWith("-")) {
				System.out.println("Missing value for -current.prefix parameter");
				printUsage();
			}
			buffer.append("	-current.prefix = ");
			String[] ids = idPrefixList.split(",");
			this.currentBuildPrefixes = new ArrayList();
			for (int j = 0; j < ids.length; j++) {
				this.currentBuildPrefixes.add(ids[j]);
				buffer.append(ids[j]);
			}
			buffer.append('\n');
			i++;
			continue;
		}
		if (arg.equals("-highlight") || arg.equals("-highlight.latest")) {
			if (args[i + 1].startsWith("-")) {
				System.out.println("Missing value for -highlight parameter");
				printUsage();
			}
			buffer.append("	"+arg+" = ");
			String[] ids = args[i + 1].split(",");
			this.pointsOfInterest = new ArrayList();
			for (int j = 0; j < ids.length; j++) {
				this.pointsOfInterest.add(ids[j]);
				buffer.append(ids[j]);
			}
			buffer.append('\n');
			i++;
			continue;
		}
		if (arg.equals("-current")) {
			currentBuildId  = args[i + 1];
			if (currentBuildId.startsWith("-")) {
				System.out.println("Missing value for -current parameter");
				printUsage();
			}
			buffer.append("	-current = "+currentBuildId+'\n');
			i++;
			continue;
		}
		if (arg.equals("-jvm")) {
			jvm = args[i + 1];
			if (jvm.startsWith("-")) {
				System.out.println("Missing value for -jvm parameter");
				printUsage();
			}
			buffer.append("	-jvm = "+jvm+'\n');
			i++;
			continue;
		}
		if (arg.equals("-output")) {
			String dir = args[++i];
			if (dir.startsWith("-")) {
				System.out.println("Missing value for -output parameter");
				printUsage();
			}
			this.outputDir = new File(dir);
			if (!this.outputDir.exists() && !this.outputDir.mkdirs()) {
				System.err.println("Cannot create directory "+dir+" to write results in!");
				System.exit(2);
			}
			buffer.append("	-output = "+dir+'\n');
			continue;
		}
		if (arg.equals("-dataDir")) {
			String dir = args[++i];
			if (dir.startsWith("-")) {
				System.out.println("Missing value for -output parameter");
				printUsage();
			}
			this.dataDir = new File(dir);
			if (!this.dataDir.exists() && !this.dataDir.mkdirs()) {
				System.err.println("Cannot create directory "+dir+" to save data locally!");
				System.exit(2);
			}
			buffer.append("	-dataDir = "+dir+'\n');
			continue;
		}
		if (arg.equals("-config")) {
			String configs = args[i + 1];
			if (configs.startsWith("-")) {
				System.out.println("Missing value for -config parameter");
				printUsage();
			}
			String[] names = configs.split(",");
			int length = names.length;
			buffer.append("	-config = ");
			for (int j=0; j<length; j++) {
				if (j>0) buffer.append(',');
				buffer.append(names[j]);
			}
			if (this.configDescriptors == null) {
				this.configDescriptors = new String[length][2];
				for (int j=0; j<length; j++) {
					this.configDescriptors[j][0] = names[j];
					this.configDescriptors[j][1] = names[j];
				}
			} else {
				int confLength = this.configDescriptors[0].length;
				int newLength = confLength;
				mainLoop: for (int j=0; j<confLength; j++) {
					for (int k=0; k<length; k++) {
						if (this.configDescriptors[j][0].equals(names[k])) {
							continue mainLoop;
						}
					}
					this.configDescriptors[j][0] = null;
					this.configDescriptors[j][1] = null;
					newLength--;
				}
				if (newLength < confLength) {
					String[][] newDescriptors = new String[newLength][2];
					for (int j=0, c=0; j<newLength; j++) {
						if (this.configDescriptors[c] != null) {
							newDescriptors[j][0] = this.configDescriptors[c][0];
							newDescriptors[j][1] = this.configDescriptors[c][1];
						} else {
							c++;
						}
					}
					this.configDescriptors = newDescriptors;
				}
			}
			buffer.append('\n');
			i++;
			continue;
		}
		if (arg.equals("-config.properties")) {
			String configProperties = args[i + 1];
			if (configProperties.startsWith("-")) {
				System.out.println("Missing value for -config.properties parameter");
				printUsage();
			}
			if (this.configDescriptors == null) {
				System.out.println("Missing -config parameter");
				printUsage();
			}
			int length = this.configDescriptors.length;
			StringTokenizer tokenizer = new StringTokenizer(configProperties, ";");
			buffer.append("	-config.properties = ");
			while (tokenizer.hasMoreTokens()) {
				String labelDescriptor = tokenizer.nextToken();
				String[] elements = labelDescriptor.trim().split(",");
				for (int j=0; j<length; j++) {
					if (elements[0].equals(this.configDescriptors[j][0])) {
						this.configDescriptors[j][1] = elements[1];
						buffer.append("\n\t\t+ ");
						buffer.append(elements[0]);
						buffer.append(" -> ");
						buffer.append(elements[1]);
					}
				}
			}
			buffer.append('\n');
			i++;
			continue;
		}
		if (arg.equals("-scenario.filter") || arg.equals("-scenario.pattern")) {
			this.scenarioPattern= args[i + 1];
			if (this.scenarioPattern.startsWith("-")) {
				System.out.println("Missing value for -baseline parameter");
				printUsage();
			}
			buffer.append("	"+arg+" = "+this.scenarioPattern+'\n');
			i++;
			continue;
		}
		if (arg.equals("-fingerprints")) {
			this.genFingerPrints = true;
			this.genAll = false;
			buffer.append("	-fingerprints\n");
			i++;
			continue;
		}
		if (arg.equals("-data")) {
			this.genData = true;
			this.genAll = false;
			buffer.append("	-data\n");
			i++;
			continue;
		}
		if (arg.equals("-print")) {
			this.print = true;
			buffer.append("	-print\n");
			i++;
			continue;
		}
		i++;
	}
	if (this.print) System.out.println(buffer.toString());
	if (baseline == null || this.outputDir == null || this.configDescriptors == null || jvm == null || currentBuildId == null) {
		printUsage();
	}
	if (this.baselinePrefix == null) {
		// Assume that baseline name format is *always* x.y_yyyyMMddhhmm_yyyyMMddhhmm
		this.baselinePrefix = baseline.substring(0, baseline.lastIndexOf('_'));
	}

	if (this.currentBuildPrefixes == null) {
		this.currentBuildPrefixes = new ArrayList();
		this.currentBuildPrefixes.add("N");
		this.currentBuildPrefixes.add("I");
	}
	return new PerformanceResults(currentBuildId, baseline, this.print);
}

/*
 * Print component PHP file
 */
private void printComponent(PerformanceResults performanceResults, String component) throws FileNotFoundException {
	if (this.print) System.out.print(".");
	File outputFile = new File(this.outputDir, component + ".php");
	PrintStream stream = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
	stream.println(Utils.HTML_OPEN);
	stream.println("<link href=\"ToolTip.css\" rel=\"stylesheet\" type=\"text/css\"><script src=\"ToolTip.js\"></script>");
	stream.println(Utils.HTML_DEFAULT_CSS);
	stream.println("<body>");

	String baselineName = performanceResults.getBaselineName();
	String currentName = performanceResults.getName();
	boolean isGlobal = component.equals("global");
	StringBuffer title = new StringBuffer("<h3>Performance of ");
	if (!isGlobal) {
		title.append(component);
		title.append(": ");
	}
	title.append(currentName);
	title.append(" relative to ");
	int index = baselineName.indexOf('_');
	title.append(baselineName.substring(0, index));
	title.append(" (");
	index = baselineName.lastIndexOf('_');
	title.append(baselineName.substring(index+1, baselineName.length()));
	title.append(")</h3>");
	stream.println(title.toString());

	// print the html representation of fingerprint for each config
	if (genFingerPrints || genAll) {
		FingerPrint fingerprint = new FingerPrint(component, stream, this.outputDir);
		try {
			fingerprint.print(performanceResults);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// print scenario status table
	if (isGlobal) {
		if (!PerformanceTestPlugin.getDBLocation().startsWith("net://")) {
			stream.println("<table border=0 cellpadding=2 cellspacing=5 width=\"100%\">");
			stream.println("<tbody><tr> <td colspan=3 align=\"left\" bgcolor=\"#0080c0\" valign=\"top\"><b><font color=\"#ffffff\" face=\"Arial,Helvetica\">");
			stream.println("Detailed performance data grouped by scenario prefix</font></b></td></tr></tbody></table>");
			stream.println("<a href=\"org.eclipse.ant.php?\">org.eclipse.ant*</a><br>");
			stream.println("<a href=\"org.eclipse.compare.php?\">org.eclipse.compare*</a><br>");
			stream.println("<a href=\"org.eclipse.core.php?\">org.eclipse.core*</a><br>");
			stream.println("<a href=\"org.eclipse.jdt.core.php?\">org.eclipse.jdt.core*</a><br>");
			stream.println("<a href=\"org.eclipse.jdt.debug.php?\">org.eclipse.jdt.debug*</a><br>");
			stream.println("<a href=\"org.eclipse.jdt.text.php?\">org.eclipse.jdt.text*</a><br>");
			stream.println("<a href=\"org.eclipse.jdt.ui.php?\">org.eclipse.jdt.ui*</a><br>");
			stream.println("<a href=\"org.eclipse.jface.php?\">org.eclipse.jface*</a><br>");
			stream.println("<a href=\"org.eclipse.osgi.php?\">org.eclipse.osgi*</a><br>");
			stream.println("<a href=\"org.eclipse.pde.ui.php?\">org.eclipse.pde.ui*</a><br>");
			stream.println("<a href=\"org.eclipse.swt.php?\">org.eclipse.swt*</a><br>");
			stream.println("<a href=\"org.eclipse.team.php?\">org.eclipse.team*</a><br>");
			stream.println("<a href=\"org.eclipse.ua.php?\">org.eclipse.ua*</a><br>");
			stream.println("<a href=\"org.eclipse.ui.php?\">org.eclipse.ui*</a><br><p><br><br>");
		}
	} else if (component.length() > 0) {
		// print the component scenario status table beneath the fingerprint
		ScenarioStatusTable sst = new ScenarioStatusTable(component, stream);
		try {
			sst.print(performanceResults);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	stream.println(Utils.HTML_CLOSE);
	stream.close();
}

/*
 * Print summary of coefficient of variation for each scenario of the given pattern
 * both for baseline and current builds.
 */
private void printSummary(PerformanceResults performanceResults) {
	long start = System.currentTimeMillis();
	if (this.print) System.out.print("Print scenarios variations summary...");
	File outputFile = new File(this.outputDir, "cvsummary.html");
	PrintStream stream = null;
	try {
		stream = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
		printSummaryPresentation(stream);
		List scenarioNames = DB_Results.getScenariosNames();
		int size = scenarioNames.size();
		printSummaryColumnsTitle(stream, performanceResults);
		String[] configs = performanceResults.getConfigNames(true/*sorted*/);
		int configsLength = configs.length;
		for (int i=0; i<size; i++) {
			String scenarioName = (String) scenarioNames.get(i);
			if (scenarioName == null) continue;
			ScenarioResults scenarioResults = performanceResults.getScenarioResults(scenarioName);
			if (scenarioResults != null) {
				stream.println("<tr>");
				for (int j=0; j<2; j++) {
					for (int c=0; c<configsLength; c++) {
						printSummaryScenarioLine(j, configs[c], scenarioResults, stream);
					}
				}
				stream.print("<td>");
				stream.print(scenarioName);
				stream.println("</td></tr>");
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		stream.println("</table></body></html>");
		stream.flush();
		stream.close();
	}
	if (this.print) System.out.println("done in "+(System.currentTimeMillis()-start)+"ms");
}

/*
 * Print summary presentation (eg. file start and text presenting the purpose of this file contents)..
 */
private void printSummaryPresentation(PrintStream stream) {
	stream.println(Utils.HTML_OPEN);
	stream.print(Utils.HTML_DEFAULT_CSS);
	stream.println("<title>Summary of Elapsed Process Variation Coefficients</title></head>");
	stream.println("<body><h3>Summary of Elapsed Process Variation Coefficients</h3>\n");
	stream.println("<p> This table provides a bird's eye view of variability in elapsed process times\n");
	stream.print("for baseline and current build stream performance scenarios.");
	stream.print(" This summary is provided to facilitate the identification of scenarios that should be examined due to high variability.");
	stream.println("The variability for each scenario is expressed as a <a href=\"http://en.wikipedia.org/wiki/Coefficient_of_variation\">coefficient\n");
	stream.println("of variation</a> (CV). The CV is calculated by dividing the <b>standard deviation\n");
	stream.println("of the elapse process time over builds</b> by the <b>average elapsed process\n");
	stream.println("time over builds</b> and multiplying by 100.\n");
	stream.println("</p><p>High CV values may be indicative of any of the following:<br></p>\n");
	stream.println("<ol><li> an unstable performance test. </li>\n");
	stream.println("<ul><li>may be evidenced by an erratic elapsed process line graph.<br><br></li></ul>\n");
	stream.println("<li>performance regressions or improvements at some time in the course of builds.</li>\n");
	stream.println("<ul><li>may be evidenced by plateaus in elapsed process line graphs.<br><br></li></ul>\n");
	stream.println("<li>unstable testing hardware.\n");
	stream.print("<ul><li>consistent higher CV values for one test configuration as compared to others across");
	stream.println(" scenarios may be related to hardward problems.</li></ul></li></ol>\n");
	stream.println("<p> Scenarios are listed in alphabetical order in the far right column. A scenario's\n");
	stream.println("variation coefficients (CVs) are in columns to the left for baseline and current\n");
	stream.println("build streams for each test configuration. Scenarios with CVs > 10% are highlighted\n");
	stream.println("in yellow (10%<CV>&lt;CV<20%) and orange(CV>20%). </p>\n");
	stream.println("<p> Each CV value links to the scenario's detailed results to allow viewers to\n");
	stream.println("investigate the variability.</p>\n");
}

/*
 * Print columns titles of the summary table.
 */
private void printSummaryColumnsTitle(PrintStream stream, PerformanceResults performanceResults) {
	String[] configBoxes = performanceResults.getConfigBoxes(true/*sorted*/);
	int length = configBoxes.length;
	stream.print("<table border=\"1\"><tr><td colspan=\"");
	stream.print(length);
	stream.print("\"><b>Baseline CVs</b></td><td colspan=\"");
	stream.print(length);
	stream.println("\"><b>Current Build Stream CVs</b></td><td rowspan=\"2\"><b>Scenario Name</b></td></tr>");
	stream.print("<tr>");
	for (int n=0; n<2; n++) {
		for (int c=0; c<length; c++) {
			stream.print("<td>");
			stream.print(configBoxes[c]);
			stream.print("</td>");
		}
	}
	stream.println("</tr>\n");
}

/*
 * Print a scenario line in the summary table.
 */
private void printSummaryScenarioLine(int i, String config, ScenarioResults scenarioResults, PrintStream stream) {
	ConfigResults configResults = scenarioResults.getConfigResults(config);
	if (configResults == null || !configResults.isValid()) {
		stream.print("<td>n/a</td>");
		return;
	}
	String url = config + "/" + scenarioResults.getFileName()+".html";
	double[] stats = null;
	int dim_id = AbstractResults.SUPPORTED_DIMS[0].getId();
	if (i==0) { // baseline results
		List baselinePrefixes = new ArrayList();
		baselinePrefixes.add(this.baselinePrefix);
		stats = configResults.getStatistics(baselinePrefixes, dim_id);
	} else {
		stats = configResults.getStatistics(this.currentBuildPrefixes, dim_id);
	}
	double variation = stats[3];
	if (variation > 10 && variation < 20) {
		stream.print("<td bgcolor=\"yellow\">");
	} else if (variation >= 20) {
		stream.print("<td bgcolor=\"FF9900\">");
	} else {
		stream.print("<td>");
	}
	stream.print("<a href=\"");
	stream.print(url);
	stream.print("\"/>");
	stream.print(variation);
	stream.print("%</a></td>");
}

/*
 * Print usage in case one of the argument of the line was incorrect.
 * Note that calling this method ends the program run due to final System.exit()
 */
private void printUsage() {
	System.out.println(
		"Usage:\n\n" +
		"-baseline\n" +
		"	Build id against which to compare results.\n" +
		"	Same as value specified for the \"build\" key in the eclipse.perf.config system property.\n\n" +

		"[-baseline.prefix]\n" +
		"	Optional.  Build id prefix used in baseline test builds and reruns.  Used to plot baseline historical data.\n" +
		"	A common prefix used for the value of the \"build\" key in the eclipse.perf.config system property when rerunning baseline tests.\n\n" +

		"-current\n" +
		"	build id for which to generate results.  Compared to build id specified in -baseline parameter above.\n" +
		"	Same as value specified for the \"build\" key in the eclipse.perf.config system property. \n\n" +

		"[-current.prefix]\n" +
		"	Optional.  Comma separated list of build id prefixes used in current build stream.\n" +
		"	Used to plot current build stream historical data.  Defaults to \"N,I\".\n" +
		"	Prefixes for values specified for the \"build\" key in the eclipse.perf.config system property. \n\n" +

		"-jvm\n" +
		"	Value specified in \"jvm\" key in eclipse.perf.config system property for current build.\n\n" +

		"-config\n" +
		"	Comma separated list of config names for which to generate results.\n" +
		"	Same as values specified in \"config\" key in eclipse.perf.config system property.\n\n" +

		"-output\n" +
		"	Path to default output directory.\n\n" +

		"[-config.properties]\n" +
		"	Optional.  Used by scenario status table to provide the following:\n" +
		"		alternate descriptions of config values to use in columns.\n" +
		"	The value should be specified in the following format:\n" +
		"	name1,description1;name2,description2;etc..\n\n" +

		"[-highlight]\n" +
		"	Optional.  Comma-separated list of build Id prefixes used to find most recent matching for each entry.\n" +
		"	Result used to highlight points in line graphs.\n\n" +

		"[-scenario.pattern]\n" +
		"	Optional.  Scenario prefix pattern to query database.  If not specified,\n" +
		"	default of % used in query.\n\n" +

		"[-fingerprints]\n" +
		"	Optional.  Use to generate fingerprints only.\n\n" +

		"[-data]\n" +
		"	Optional.  Generates table of scenario reference and current data with line graphs.\n\n" +

		"[-print]\n" +
		"	Optional.  Display output in the console while generating.\n"
	);

	System.exit(1);
}

/**
 * Generate the performance results for a specified build regarding to a specific reference.
 * This action generates following HTML files:
 * <ul>
 * <li>A summary table to see the variations for all the concerned scenarios</li>
 * <li>A global php file including global scenario fingerprints and links for all concerned components results php files</li>
 * <li>A php file for each component including scenario fingerprints and status table with links to a scenario data file</li>
 * <li>A data HTML file for each config of each scenario included in status table</li>
 * </ul>
 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
 */
public Object start(IApplicationContext context) throws Exception {

	long begin = System.currentTimeMillis();

	// Parse arguments and read DB info
	PerformanceResults performanceResults = parse(context.getArguments().get("application.args"));
	performanceResults.read(this.configDescriptors, this.scenarioPattern, this.dataDir);

	// Print whole scenarios summary
	printSummary(performanceResults);

	// Copy images and scripts to output dir
	Bundle bundle = UiPlugin.getDefault().getBundle();
	URL images = bundle.getEntry("images");
	URL scripts = bundle.getEntry("scripts");
	if (images != null) {
		images = FileLocator.resolve(images);
		Utils.copyImages(new File(images.getPath()), this.outputDir);
	}
	if (scripts != null) {
		scripts = FileLocator.resolve(scripts);
		Utils.copyScripts(new File(scripts.getPath()), this.outputDir);
	}

	// Print HTML pages and all linked files
	if (this.print) {
		System.out.println("Print performance results HTML pages:");
		System.out.print("	- all components");
	}
	long start = System.currentTimeMillis();
	printComponent(performanceResults, "global");
	Iterator components = performanceResults.getComponents().iterator();
	while (components.hasNext()) {
		printComponent(performanceResults, (String) components.next());
	}
	if (this.print) System.out.println("done in "+(System.currentTimeMillis()-start)+"ms");

	// Print the scenarios data
	if (genData || genAll) {
		start = System.currentTimeMillis();
		if (this.print) System.out.print("	- all scenarios data...");
		ScenarioData data = new ScenarioData(this.baselinePrefix, this.pointsOfInterest, this.currentBuildPrefixes, this.outputDir);
		try {
			data.print(performanceResults);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (this.print) System.out.println("done in "+(System.currentTimeMillis()-start)+"ms");
	}
	if (this.print) {
		long time = System.currentTimeMillis();
		System.out.println("End of generation: "+new SimpleDateFormat("H:mm:ss").format(new Date(time)));
		long ms = System.currentTimeMillis() - begin;
		int sec = (int) (ms / 1000L);
		if ((ms - (sec*1000)) >= 500) sec++;
		if (sec < 60) {
			System.out.println("=> done in "+sec+" second"+(sec==1?"":"s"));
		} else if (sec < 3600) {
			int m = sec / 60;
			int s = sec % 60;
			System.out.println("=> done in "+m+" minute"+(m==1?"":"s")+" and "+s+" second"+(s==1?"":"s"));
		} else {
			int h = sec / 3600;
			int m = (sec-h*3600) / 60;
			int s = (sec-h*3600)  % 60;
			System.out.println("=> done in "+h+" hour"+(h==1?"":"s")+", "+m+" minute"+(m==1?"":"s")+" and "+s+" second"+(s==1?"":"s"));
		}
	}
	return null;
}

/* (non-Javadoc)
 * @see org.eclipse.equinox.app.IApplication#stop()
 */
public void stop() {
	// Do nothing
}
}