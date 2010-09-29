package org.kompiro.jamcircle.scripting;

import java.io.OutputStream;

public interface IScriptingEngineStreamLoader {

	OutputStream getOutputStream();

	OutputStream getErrorStream();

}
