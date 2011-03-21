package org.kompiro.jamcircle.scripting.delegator;

import java.io.*;

public class DefaultSaveHistoryDelegator implements SaveHistoryDeligator {

	public DefaultSaveHistoryDelegator() {
	}

	public void delegate(File target, String histories) {
		if (target == null)
			throw new IllegalArgumentException("target is null.");
		if (histories == null)
			throw new IllegalArgumentException("histories is null.");
		FileWriter writer = null;
		try {
			writer = new FileWriter(target);
		} catch (IOException e) {
		}
		if (writer != null) {
			try {
				writer.append(histories);
			} catch (IOException e) {
			}
			try {
				writer.flush();
			} catch (IOException e) {
			}
			try {
				writer.close();
			} catch (IOException e) {
			}
		}
	}

}