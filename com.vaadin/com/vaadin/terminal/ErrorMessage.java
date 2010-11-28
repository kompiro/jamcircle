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
 * Interface for rendering error messages to terminal. All the visible errors
 * shown to user must implement this interface.
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 3.0
 */
public interface ErrorMessage extends Paintable, Serializable {

    /**
     * Error code for system errors and bugs.
     */
    public static final int SYSTEMERROR = 5000;

    /**
     * Error code for critical error messages.
     */
    public static final int CRITICAL = 4000;

    /**
     * Error code for regular error messages.
     */
    public static final int ERROR = 3000;

    /**
     * Error code for warning messages.
     */
    public static final int WARNING = 2000;

    /**
     * Error code for informational messages.
     */
    public static final int INFORMATION = 1000;

    /**
     * Gets the errors level.
     * 
     * @return the level of error as an integer.
     */
    public int getErrorLevel();

    /**
     * Error messages are inmodifiable and thus listeners are not needed. This
     * method should be implemented as empty.
     * 
     * @param listener
     *            the listener to be added.
     * @see com.vaadin.terminal.Paintable#addListener(Paintable.RepaintRequestListener)
     */
    public void addListener(RepaintRequestListener listener);

    /**
     * Error messages are inmodifiable and thus listeners are not needed. This
     * method should be implemented as empty.
     * 
     * @param listener
     *            the listener to be removed.
     * @see com.vaadin.terminal.Paintable#removeListener(Paintable.RepaintRequestListener)
     */
    public void removeListener(RepaintRequestListener listener);

    /**
     * Error messages are inmodifiable and thus listeners are not needed. This
     * method should be implemented as empty.
     * 
     * @see com.vaadin.terminal.Paintable#requestRepaint()
     */
    public void requestRepaint();

}
