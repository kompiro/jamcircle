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
 * <p>
 * This interface is implemented by all visual objects that can be scrolled. The
 * unit of scrolling is pixel.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 3.0
 */
public interface Scrollable extends Serializable {

    /**
     * Gets scroll left offset.
     * 
     * <p>
     * Scrolling offset is the number of pixels this scrollable has been
     * scrolled right.
     * </p>
     * 
     * @return Horizontal scrolling position in pixels.
     */
    public int getScrollLeft();

    /**
     * Sets scroll left offset.
     * 
     * <p>
     * Scrolling offset is the number of pixels this scrollable has been
     * scrolled right.
     * </p>
     * 
     * @param pixelsScrolled
     *            the xOffset.
     */
    public void setScrollLeft(int pixelsScrolled);

    /**
     * Gets scroll top offset.
     * 
     * <p>
     * Scrolling offset is the number of pixels this scrollable has been
     * scrolled down.
     * </p>
     * 
     * @return Vertical scrolling position in pixels.
     */
    public int getScrollTop();

    /**
     * Sets scroll top offset.
     * 
     * <p>
     * Scrolling offset is the number of pixels this scrollable has been
     * scrolled down.
     * </p>
     * 
     * @param pixelsScrolled
     *            the yOffset.
     */
    public void setScrollTop(int pixelsScrolled);

    /**
     * Is the scrolling enabled.
     * 
     * <p>
     * Enabling scrolling allows the user to scroll the scrollable view
     * interactively
     * </p>
     * 
     * @return <code>true</code> if the scrolling is allowed, otherwise
     *         <code>false</code>.
     */
    public boolean isScrollable();

    /**
     * Enables or disables scrolling..
     * 
     * <p>
     * Enabling scrolling allows the user to scroll the scrollable view
     * interactively
     * </p>
     * 
     * @param isScrollingEnabled
     *            true if the scrolling is allowed.
     */
    public void setScrollable(boolean isScrollingEnabled);

}
