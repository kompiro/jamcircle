/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.performance.ui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.AssertionFailedError;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.eclipse.test.internal.performance.data.Dim;
import org.eclipse.test.internal.performance.data.DimensionMessages;
import org.eclipse.test.internal.performance.results.AbstractResults;
import org.eclipse.test.internal.performance.results.BuildResults;
import org.eclipse.test.internal.performance.results.ComponentResults;
import org.eclipse.test.internal.performance.results.ConfigResults;
import org.eclipse.test.internal.performance.results.PerformanceResults;
import org.eclipse.test.internal.performance.results.ScenarioResults;

/**
 * Class used to print scenario all builds data.
 */
public class ScenarioData {
	private String baselinePrefix = null;
	private List pointsOfInterest;
	private List buildIDStreamPatterns;
	private File rootDir;
	private static final int GRAPH_WIDTH = 600;
	private static final int GRAPH_HEIGHT = 200;

/**
 * Summary of results for a scenario for a given build compared to a
 * reference.
 *
 * @param baselinePrefix The prefix of the baseline build names
 * @param pointsOfInterest A list of buildId's to highlight on line graphs
 * @param buildIDPatterns
 * @param outputDir The directory root where the files are generated
 *
*/
public ScenarioData(String baselinePrefix, List pointsOfInterest, List buildIDPatterns, File outputDir) {
	this.baselinePrefix = baselinePrefix;
	this.pointsOfInterest = pointsOfInterest;
	this.buildIDStreamPatterns = buildIDPatterns;
	this.rootDir = outputDir;
}

/**
 * Print the scenario all builds data from the given performance results.
 * 
 * @param performanceResults The needed information to generate scenario data
 */
public void print(PerformanceResults performanceResults) {
	String[] configNames = performanceResults.getConfigNames(false/*not sorted*/);
	String[] configBoxes = performanceResults.getConfigBoxes(false/*not sorted*/);
	int length = configNames.length;
	for (int i=0; i<length; i++) {
		File outputDir = new File(this.rootDir, configNames[i]);
		outputDir.mkdir();
		Iterator components = performanceResults.getResults();
		while (components.hasNext()) {
			ComponentResults componentResults = (ComponentResults) components.next();
			printSummary(configNames[i], configBoxes[i], componentResults, outputDir);
			printDetails(configNames[i], configBoxes[i], componentResults, outputDir);
		}
	}
}

/*
 * Print the summary file of the builds data.
 */
private void printSummary(String configName, String configBox, ComponentResults componentResults, File outputDir) {
	Iterator scenarios = componentResults.getResults();
	while (scenarios.hasNext()) {
		List highlightedPoints = new ArrayList();
		ScenarioResults scenarioResults = (ScenarioResults) scenarios.next();
		ConfigResults configResults = scenarioResults.getConfigResults(configName);
		if (configResults == null || !configResults.isValid()) continue;

		// get latest points of interest matching
		if (this.pointsOfInterest != null) {
			Iterator buildPrefixes = this.pointsOfInterest.iterator();
			while (buildPrefixes.hasNext()) {
				String buildPrefix = (String) buildPrefixes.next();
				List builds = configResults.getBuilds(buildPrefix);
				if (buildPrefix.indexOf('*') <0 && buildPrefix.indexOf('?') < 0) {
					if (builds.size() > 0) {
						highlightedPoints.add(builds.get(builds.size()-1));
					}
				} else {
					highlightedPoints.addAll(builds);
				}
			}
		}

		String scenarioFileName = scenarioResults.getFileName();
		File outFile = new File(outputDir, scenarioFileName + ".html");
		PrintStream stream = null;
		try {
			stream = new PrintStream(new BufferedOutputStream(new FileOutputStream(outFile)));
		} catch (FileNotFoundException e) {
			System.err.println("can't create output file" + outFile); //$NON-NLS-1$
		}
		if (stream == null) {
			stream = System.out;
		}
		stream.println(Utils.HTML_OPEN);
		stream.println(Utils.HTML_DEFAULT_CSS);

		stream.println("<title>" + scenarioResults.getName() + "(" + configBox + ")" + "</title></head>"); //$NON-NLS-1$
		stream.println("<h4>Scenario: " + scenarioResults.getName() + " (" + configBox + ")</h4><br>"); //$NON-NLS-1$ //$NON-NLS-2$

		String failureMessage = Utils.failureMessage(configResults.getCurrentBuildDeviation(), true);
 		if (failureMessage != null){
   			stream.println("<table><tr><td><b>"+failureMessage+"</td></tr></table>\n");
 		}

 		BuildResults currentBuildResults = configResults.getCurrentBuildResults();
 		String comment = currentBuildResults.getComment();
		if (comment != null) {
			stream.println("<p><b>Note:</b><br>\n");
			stream.println(comment + "</p>");
		}

		// Print link to raw data.
		String rawDataFile = scenarioFileName+"_raw.html";
		stream.println("<br><br><b><a href=\""+rawDataFile+"\">Raw data and Stats</a></b><br><br>\n");
		stream.println("<b>Click measurement name to view line graph of measured values over builds.</b><br><br>\n");

		try {
			// Print build result table
			stream.println("<table border=\"1\">"); //$NON-NLS-1$
			stream.print("<tr><td><b>Build Id</b></td>"); //$NON-NLS-1$
			Dim[] dimensions = AbstractResults.SUPPORTED_DIMS;
			int dimLength = dimensions.length;
			for (int d=0; d<dimLength; d++) {
				String dimName = dimensions[d].getName();
				stream.print("<td><a href=\"#" + configName + "_" + scenarioFileName + "_" + dimName + "\"><b>" + dimName + "</b></a></td>");
			}
			stream.println("</tr>\n");

			// Write build lines
			printTableLine(stream, currentBuildResults);
			printTableLine(stream, configResults.getBaselineBuildResults());

			// Write difference line
			printDifferenceLine(stream, configResults);

			// End of table
			stream.println("</table>");
			stream.println("*Delta values in red and green indicate degradation > 10% and improvement > 10%,respectively.<br><br>");
			stream.println("<br><hr>\n\n");

			// print text legend.
			stream.println("Black and yellow points plot values measured in integration and last seven nightly builds.<br>\n" + "Magenta points plot the repeated baseline measurement over time.<br>\n"
					+ "Boxed points represent previous releases, milestone builds, current reference and current build.<br><br>\n"
					+ "Hover over any point for build id and value.\n");

			// print image maps of historical
			for (int d=0; d<dimLength; d++) {
				String dimName = dimensions[d].getName();
				int dim_id = dimensions[d].getId();
				TimeLineGraph lineGraph = getLineGraph(scenarioResults, configResults, dimensions[d], highlightedPoints, this.buildIDStreamPatterns);

				File graphsDir = new File(outputDir, "graphs");
				graphsDir.mkdir();
				File imgFile = new File(graphsDir, scenarioFileName + "_" + dimName + ".gif");
				saveGraph(lineGraph, imgFile);
				stream.println("<br><a name=\"" + configName + "_" + scenarioFileName + "_" + dimName + "\"></a>");
				stream.println("<br><b>" + dimName + "</b><br>");
				stream.println(DimensionMessages.getDescription(dim_id) + "<br><br>\n");
				stream.print("<img src=\"graphs/");
				stream.print(imgFile.getName());
				stream.print("\" usemap=\"#" + lineGraph.fTitle + "\">");
				stream.print("<map name=\"" + lineGraph.fTitle + "\">");
				stream.print(lineGraph.getAreas());
				stream.println("</map>");
			}
			stream.println("<br><br></body>");
			stream.println(Utils.HTML_CLOSE);
			if (stream != System.out)
				stream.close();

		} catch (AssertionFailedError e) {
			e.printStackTrace();
			continue;
		}
	}
}

/*
 * Print the data for a build results.
 */
private void printTableLine(PrintStream stream, BuildResults buildResults) {
	stream.print("<tr><td>");
	stream.print(buildResults.getName());
	if (buildResults.isBaseline()) stream.print(" (reference)");
	stream.print("</td>");
	Dim[] dimensions = AbstractResults.SUPPORTED_DIMS;
	int dimLength = dimensions.length;
	for (int d=0; d<dimLength; d++) {
		int dim_id = dimensions[d].getId();
		double stddev = buildResults.getDeviation(dim_id);
		String displayValue = dimensions[d].getDisplayValue(buildResults.getValue(dim_id));
		stream.print("<td>");
		stream.print(displayValue);
		if (stddev < 0) {
			stream.println(" [n/a]");
		} else if (stddev > 0) {
			stream.print(" [");
			stream.print(dimensions[d].getDisplayValue(stddev));
			stream.print("]");
		}
		stream.print( "</td>");
	}
	stream.println("</tr>");
}

/*
 * Print the line showing the difference between current and baseline builds.
 */
private void printDifferenceLine(PrintStream stream, ConfigResults configResults) {
	stream.print("<tr><td>*Delta</td>");
	Dim[] dimensions = AbstractResults.SUPPORTED_DIMS;
	int dimLength = dimensions.length;
	for (int d=0; d<dimLength; d++) {
		Dim currentDim = dimensions[d];
		int dim_id = currentDim.getId();
		BuildResults currentBuild = configResults.getCurrentBuildResults();
		BuildResults baselineBuild = configResults.getBaselineBuildResults();

		double baselineValue = baselineBuild.getValue(dim_id);
		double diffValue = currentBuild.getValue(dim_id) - baselineValue;
		double diffPercentage =  baselineValue == 0 ? 0 : Math.round(diffValue / baselineValue * 1000) / 10.0;
		String diffDisplayValue = currentDim.getDisplayValue(diffValue);
		// green
		String fontColor = "";
		if ((diffPercentage < -10 && !currentDim.largerIsBetter()) || (diffPercentage > 10 && currentDim.largerIsBetter()))
			fontColor = "#006600";
		if ((diffPercentage < -10 && currentDim.largerIsBetter()) || (diffPercentage > 10 && !currentDim.largerIsBetter()))
			fontColor = "#FF0000";

		diffPercentage = Math.abs(diffPercentage);
		String percentage = (diffPercentage == 0) ? "" : "<br>" + diffPercentage + " %";

		if (diffPercentage > 10 || diffPercentage < -10) {
			stream.print("<td><FONT COLOR=\"" + fontColor + "\"><b>" + diffDisplayValue + percentage + "</b></FONT></td>");
		} else {
			stream.print("<td>" + diffDisplayValue + percentage + "</td>");
		}
	}
	stream.print("</tr></font>");
}

/*
 * Returns a LineGraph object representing measurements for a scenario over builds.
 */
private TimeLineGraph getLineGraph(ScenarioResults scenarioResults, ConfigResults configResults, Dim dim, List highlightedPoints, List currentBuildIdPrefixes) {
	Display display = Display.getDefault();

	Color black = display.getSystemColor(SWT.COLOR_BLACK);
	Color yellow = display.getSystemColor(SWT.COLOR_DARK_YELLOW);
	Color magenta = display.getSystemColor(SWT.COLOR_MAGENTA);

	String scenarioName = scenarioResults.getName();
	TimeLineGraph graph = new TimeLineGraph(scenarioName + ": " + dim.getName(), dim);
	String baseline = configResults.getBaselineBuildName();
	String current = configResults.getCurrentBuildName();

	Iterator builds = configResults.getResults();
	List lastSevenNightlyBuilds = configResults.lastNightlyBuildNames(7);
	buildLoop: while (builds.hasNext()) {
		BuildResults buildResults = (BuildResults) builds.next();
		String buildID = buildResults.getName();
		int underscoreIndex = buildID.indexOf('_');
		String label = (underscoreIndex != -1 && (buildID.equals(baseline) || buildID.equals(current))) ? buildID.substring(0, underscoreIndex) : buildID;

		double value = buildResults.getValue(dim.getId());

		if (buildID.equals(current)) {
			Color color = black;
			if (buildID.startsWith("N"))
				color = yellow;

			graph.addItem("main", label, dim.getDisplayValue(value), value, color, true, Utils.getDateFromBuildID(buildID), true);
			continue;
		}
		if (highlightedPoints.contains(buildID)) {
			graph.addItem("main", label, dim.getDisplayValue(value), value, black, false, Utils.getDateFromBuildID(buildID, false), true);
			continue;
		}
		if (buildID.charAt(0) == 'N') {
			if (lastSevenNightlyBuilds.contains(buildID)) {
				graph.addItem("main", buildID, dim.getDisplayValue(value), value, yellow, false, Utils.getDateFromBuildID(buildID), false);
			}
			continue;
		}
		for (int i=0;i<currentBuildIdPrefixes.size();i++){
			if (buildID.startsWith(currentBuildIdPrefixes.get(i).toString())) {
				graph.addItem("main", buildID, dim.getDisplayValue(value), value, black, false, Utils.getDateFromBuildID(buildID), false);
				continue buildLoop;
			}
		}
		if (buildID.equals(baseline)) {
			boolean drawBaseline = (baselinePrefix != null) ? false : true;
			graph.addItem("reference", label, dim.getDisplayValue(value), value, magenta, true, Utils.getDateFromBuildID(buildID, true), true, drawBaseline);
			continue;
		}
		if (baselinePrefix != null) {
			if (buildID.startsWith(baselinePrefix) && !buildID.equals(baseline) && Utils.getDateFromBuildID(buildID, true) <= Utils.getDateFromBuildID(baseline, true)) {
				graph.addItem("reference", label, dim.getDisplayValue(value), value, magenta, false, Utils.getDateFromBuildID(buildID, true), false);
				continue;
			}
		}
	}
	return graph;
}

/*
 * Print details file of the scenario builds data.
 */
private void printDetails(String configName, String configBox, ComponentResults componentResults, File outputDir) {
	Iterator scenarios = componentResults.getResults();
	while (scenarios.hasNext()) {
		ScenarioResults scenarioResults = (ScenarioResults) scenarios.next();
		ConfigResults configResults = scenarioResults.getConfigResults(configName);
		if (configResults == null || !configResults.isValid()) continue;
		String scenarioName= scenarioResults.getName();
		String scenarioFileName = scenarioResults.getFileName();
		File outFile = new File(outputDir, scenarioFileName + "_raw.html");
		PrintStream stream = null;
		try {
			stream = new PrintStream(new BufferedOutputStream(new FileOutputStream(outFile)));
		} catch (FileNotFoundException e) {
			System.err.println("can't create output file" + outFile); //$NON-NLS-1$
		}
		if (stream == null) stream = System.out;
		RawDataTable currentResultsTable = new RawDataTable(configResults, this.buildIDStreamPatterns, stream);
		RawDataTable baselineResultsTable = new RawDataTable(configResults, this.baselinePrefix, stream);
		stream.println(Utils.HTML_OPEN);
		stream.println(Utils.HTML_DEFAULT_CSS);
		stream.println("<title>" + scenarioName + "(" + configBox + ")" + " - Details</title></head>"); //$NON-NLS-1$
		stream.println("<h4>Scenario: " + scenarioName + " (" + configBox + ")</h4>"); //$NON-NLS-1$
		stream.println("<a href=\""+scenarioFileName+".html\">VIEW GRAPH</a><br><br>"); //$NON-NLS-1$
		stream.println("<table><td><b>Current Stream Test Runs</b></td><td><b>Baseline Test Runs</b></td></tr>\n");
		stream.println("<tr valign=\"top\">");
		stream.print("<td>");
		currentResultsTable.print();
		stream.println("</td>");
		stream.print("<td>");
		baselineResultsTable.print();
		stream.println("</td>");
		stream.println("</tr>");
		stream.println("</table>");
		stream.close();
	}
}

/*
 * Prints a LineGraph object as a gif file.
 */
private void saveGraph(LineGraph p, File outputFile) {
	Image image = new Image(Display.getDefault(), GRAPH_WIDTH, GRAPH_HEIGHT);
	p.paint(image);

	/* Downscale to 8 bit depth palette to save to gif */
	ImageData data = Utils.downSample(image);
	ImageLoader il = new ImageLoader();
	il.data = new ImageData[] { data };
	OutputStream out = null;
	try {
		out = new BufferedOutputStream(new FileOutputStream(outputFile));
		il.save(out, SWT.IMAGE_GIF);

	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} finally {
		image.dispose();
		if (out != null) {
			try {
				out.close();
			} catch (IOException e1) {
				// silently ignored
			}
		}
	}
}
}
