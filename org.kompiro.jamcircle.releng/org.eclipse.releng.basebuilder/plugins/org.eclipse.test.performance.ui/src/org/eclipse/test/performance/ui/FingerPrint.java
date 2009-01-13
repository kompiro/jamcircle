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
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.eclipse.test.internal.performance.results.AbstractResults;
import org.eclipse.test.internal.performance.results.ConfigResults;
import org.eclipse.test.internal.performance.results.PerformanceResults;
import org.eclipse.test.internal.performance.results.ScenarioResults;

/**
 * Class used to create scenario fingerprint.
 */
public class FingerPrint {

private static final int GRAPH_WIDTH = 1000;

	String component;
	PrintStream stream;
	File outputDir;

public FingerPrint(String name, PrintStream ps, File outputDir) {
	if (!name.equals("global")) this.component = name;
	this.stream = ps;
	this.outputDir = outputDir;
}

/**
 * Create and save fingerprints as image and print their reference in the current stream.
 * 
 * @param performanceResults The performance results used to print the fingerprints
 */
public void print(PerformanceResults performanceResults) {
	String baselineBuildName = performanceResults.getBaselineName();
	String buildName = performanceResults.getName();
	
	// Compute fingerprint output file name prefix
	int referenceUnderscoreIndex = baselineBuildName.indexOf('_');
	String baselinePrefix = baselineBuildName;
	if (referenceUnderscoreIndex != -1) {
		baselinePrefix = baselineBuildName.substring(0, referenceUnderscoreIndex);
	}
	int currentUnderscoreIndex = buildName.indexOf('_');
	if  (currentUnderscoreIndex != -1){
		buildName = buildName.substring(0, currentUnderscoreIndex);
	}
	StringBuffer buffer = new StringBuffer("FP_");
	if (this.component != null) {
		buffer.append(this.component);
		buffer.append('_');
	}
	buffer.append(baselinePrefix);
	buffer.append('_');
	buffer.append(buildName);
	buffer.append('.');
	String filePrefix = buffer.toString();

	// Create each fingerprint and save it
	String[] configNames = performanceResults.getConfigNames(false/* not sorted*/);
	String[] configBoxes = performanceResults.getConfigBoxes(false/* not sorted*/);
	int length = configNames.length;
	for (int c=0; c<length; c++) {
		String configName  = configNames[c];
		List scenarios = performanceResults.getComponentSummaryScenarios(this.component, configName);
		if (scenarios == null) continue;

		// Create BarGraph
//		BarGraph barGraph = new BarGraph(null);
		BarGraph barGraph = null;
		for (int i=0, size=scenarios.size(); i<size; i++) {
			ScenarioResults scenarioResults = (ScenarioResults) scenarios.get(i);
			ConfigResults configResults = scenarioResults.getConfigResults(configName);
			if (configResults == null || !configResults.isValid()) continue;
			double[] results = configResults.getCurrentBuildDeviation();
			double percent = -results[0] * 100.0;
			if (results != null && Math.abs(percent) < 200) {
				String defaultDimensionName = AbstractResults.SUPPORTED_DIMS[0].getName();
				String name = scenarioResults.getLabel() + " (" + defaultDimensionName + ")";
				if (!configResults.getCurrentBuildName().equals(buildName)) {
					continue; // the test didn't run on last build, skip it
				}
				if (!configResults.isBaselined()) {
					name = "*" + name + " (" + configResults.getBaselineBuildName() + ")";
				}
				if (barGraph == null) {
					barGraph = new BarGraph(null);
				}
				barGraph.addItem(name,
				    results,
				    configName + "/" + scenarioResults.getFileName() + ".html#" + defaultDimensionName,
				    configResults.getCurrentBuildResults().getComment(),
				    (Utils.confidenceLevel(results) & Utils.ERR) == 0);
			}
		}
		 if (barGraph == null) continue;

		// Save image file
		String fileName = filePrefix + configName ;
		File outputFile = new File(this.outputDir, fileName+".gif");
		save(barGraph, outputFile);

		// Print image file reference in stream
		String boxName = configBoxes[c];
		if (outputFile.exists()) {
			String areas = barGraph.getAreas();
			if (areas == null) areas = "";
			this.stream.print("<h4>");
			this.stream.print(boxName);
			this.stream.print("</h4>");
			this.stream.print("<img src=\"");
			this.stream.print(fileName);
			this.stream.print(".gif\" usemap=\"#");
			this.stream.print(fileName);
			this.stream.print("\"><map name=\"");
			this.stream.print(fileName);
			this.stream.print("\">");
			this.stream.print(areas);
			this.stream.print("</map>\n");
		} else {
			this.stream.print("<br><br>There is no fingerprint for ");
			this.stream.print(boxName);
			this.stream.print("<br><br>\n");
		}
	}
}

/*
 * Save the computed bar graph.
 */
private void save(BarGraph barGraph, File outputFile) {

	// Create and paint image
	Display display = Display.getDefault();
	int height = barGraph.getHeight();
	Image image = new Image(display, GRAPH_WIDTH, height);
	GC gc = new GC(image);
	barGraph.paint(display, GRAPH_WIDTH, height, gc);
	gc.dispose();

	// Save image
	ImageData data = Utils.downSample(image);
	ImageLoader imageLoader = new ImageLoader();
	imageLoader.data = new ImageData[] { data };

	OutputStream out = null;
	try {
		out = new BufferedOutputStream(new FileOutputStream(outputFile));
		imageLoader.save(out, SWT.IMAGE_GIF);
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
