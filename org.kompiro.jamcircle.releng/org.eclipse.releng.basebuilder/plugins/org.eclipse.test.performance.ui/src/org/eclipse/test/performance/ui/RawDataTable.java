/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.performance.ui;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.test.internal.performance.data.Dim;
import org.eclipse.test.internal.performance.results.AbstractResults;
import org.eclipse.test.internal.performance.results.BuildResults;
import org.eclipse.test.internal.performance.results.ConfigResults;

/**
 * Class used to fill details file of scenario builds data.
 * @see ScenarioData
 */
public class RawDataTable {

	private ConfigResults configResults;
	private List buildPrefixes;
	private PrintStream stream;
	private Dim[] dimensions = AbstractResults.SUPPORTED_DIMS;
	private boolean debug = false;

private RawDataTable(ConfigResults results, PrintStream ps) {
	this.configResults = results;
	this.stream = ps;
}

public RawDataTable(ConfigResults results, List prefixes, PrintStream ps) {
	this(results, ps);
	this.buildPrefixes = prefixes;
}
public RawDataTable(ConfigResults results, String baselinePrefix, PrintStream ps) {
	this(results, ps);
	this.buildPrefixes = new ArrayList();
	this.buildPrefixes.add(baselinePrefix);
}

/**
 * Print all build data to the current stream.
 */
public void print(){
	stream.print("<table border=\"1\">");
	printSummary();
	printDetails();
	stream.println("</table>");
}

/*
 * Print table columns headers.
 */
private void printColumnHeaders() {
	StringBuffer buffer = new StringBuffer();
	int length = this.dimensions.length;
	for (int i=0; i<length; i++) {
		buffer.append("<td><b>");
		buffer.append(this.dimensions[i].getName());
		buffer.append("</b></td>");
	}
	stream.print(buffer.toString());
}

/*
 * Print all build results in the table.
 */
private void printDetails() {
	stream.print("<tr><td><b>Build ID</b></td>");
	printColumnHeaders();
	stream.println("</tr>");

	List builds = this.configResults.getBuildsMatchingPrefixes(this.buildPrefixes);
	Collections.reverse(builds);
	int size = builds.size();
	for (int i=0; i<size; i++) {
		BuildResults buildResults = (BuildResults) builds.get(i);
		stream.print("<tr><td>");
		stream.print(buildResults.getName());
		stream.print("</td>");
		int dimLength = this.dimensions.length;
		for (int d=0; d<dimLength; d++) {
			int dim_id = this.dimensions[d].getId();
			double value = buildResults.getValue(dim_id);
			printDimTitle(this.dimensions[d].getName());
			String displayValue = this.dimensions[d].getDisplayValue(value);
			stream.print(displayValue);
			if (debug) System.out.print("\t"+displayValue);
			stream.print("</td>");
		}
		if (debug) System.out.println();
		stream.println("</tr>");
	}
	if (debug) System.out.println("\n");
}

/*
 * Print summary on top of the table.
 */
private void printSummary() {
	stream.print("<tr><td><b>Stats</b></td>");
	printColumnHeaders();
	stream.println("</tr>");

	int length = this.dimensions.length;
	double[][] dimStats = new double[2][];
	for (int i=0; i<this.dimensions.length; i++) {
		dimStats[i] = this.configResults.getStatistics(this.buildPrefixes, this.dimensions[i].getId());
	}

	stream.print("<tr><td>#BUILDS SAMPLED</td>");
	for (int i=0; i<length; i++) {
		String dimName = this.dimensions[i].getName();
		printDimTitle(dimName);
		stream.print((int)dimStats[i][0]);
		stream.print("</td>");
	}
	stream.println("</tr>");
	stream.print("<tr><td>MEAN</td>");
	printRowDoubles(dimStats, 1);
	stream.println("</tr>");
	stream.print("<tr><td>STD DEV</td>");
	printRowDoubles(dimStats, 2);
	stream.println("</tr>");
	stream.print("<tr><td>COEF. VAR</td>");
	printRowDoubles(dimStats, 3);
	stream.println("</tr>");

	// Blank line
	stream.print("<tr>");
	for (int i=0; i<length+1;	i++){
		stream.print("<td>&nbsp;</td>");
	}
	stream.println("</tr>");
}

/*
 * Print values in table row.
 */
private void printRowDoubles(double[][] stats, int idx) {
	int length = this.dimensions.length;
	for (int i=0; i<length; i++) {
		double value = stats[i][idx];
		String dimName = this.dimensions[i].getName();
		if (idx == 3) {
			if (value>10 && value<20) {
				stream.print("<td bgcolor=\"yellow\" title=\"");
			} else if (value>=20) {
				stream.print("<td bgcolor=\"FF9900\" title=\"");
			} else {
				stream.print("<td title=\"");
			}
			stream.print(dimName);
			stream.print("\">");
			stream.print(value);
			stream.print("%</td>");
		} else {
			printDimTitle(dimName);
			stream.print(this.dimensions[i].getDisplayValue(value));
			stream.print("</td>");
		}
	}
}

/*
 * Print dim title inside value reference.
 * TODO (frederic) See if this title is really necessary
 */
private void printDimTitle(String dimName) {
    stream.print("<td title=\"");
    stream.print(dimName);
    stream.print("\">");
}
}
