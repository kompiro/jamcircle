package org.kompiro.jamcircle.scripting.ui;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public enum ScriptingColorEnum {
	OUTPUT_STREAM_COLOR("RubyScriptingConsole.OutputStreamColor", SWT.COLOR_BLUE),
	ERROR_STREAM_COLOR("RubyScriptingConsole.ErrorStreamColor", SWT.COLOR_RED);
	private String symbolicName;
	private int keyColor;

	private ScriptingColorEnum(String key, int keyColor) {
		this.symbolicName = key;
		this.keyColor = keyColor;
	}

	public void initialize() {
		ColorRegistry registry = JFaceResources.getColorRegistry();
		Color color = getDisplay().getSystemColor(keyColor);
		registry.put(symbolicName, color.getRGB());
	}

	public Color getColor() {
		ColorRegistry registry = JFaceResources.getColorRegistry();
		return registry.get(symbolicName);
	}

	private Display getDisplay() {
		return Display.getDefault();
	}
}
