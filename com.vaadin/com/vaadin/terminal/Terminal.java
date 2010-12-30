/* 
 * Copyright 2010 IT Mill Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.terminal;

import java.io.Serializable;

/**
 * An interface that provides information about the user's terminal.
 * Implementors typically provide additional information using methods not in
 * this interface. </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 3.0
 */
public interface Terminal extends Serializable {

    /**
     * Gets the name of the default theme for this terminal.
     * 
     * @return the name of the theme that is used by default by this terminal.
     */
    public String getDefaultTheme();

    /**
     * Gets the width of the terminal screen in pixels. This is the width of the
     * screen and not the width available for the application.
     * <p>
     * Note that the screen width is typically not available in the
     * {@link com.vaadin.Application#init()} method as this is called before the
     * browser has a chance to report the screen size to the server.
     * </p>
     * 
     * @return the width of the terminal screen.
     */
    public int getScreenWidth();

    /**
     * Gets the height of the terminal screen in pixels. This is the height of
     * the screen and not the height available for the application.
     * 
     * <p>
     * Note that the screen height is typically not available in the
     * {@link com.vaadin.Application#init()} method as this is called before the
     * browser has a chance to report the screen size to the server.
     * </p>
     * 
     * @return the height of the terminal screen.
     */
    public int getScreenHeight();

    /**
     * An error event implementation for Terminal.
     */
    public interface ErrorEvent extends Serializable {

        /**
         * Gets the contained throwable, the cause of the error.
         */
        public Throwable getThrowable();

    }

    /**
     * Interface for listening to Terminal errors.
     */
    public interface ErrorListener extends Serializable {

        /**
         * Invoked when a terminal error occurs.
         * 
         * @param event
         *            the fired event.
         */
        public void terminalError(Terminal.ErrorEvent event);
    }
}
