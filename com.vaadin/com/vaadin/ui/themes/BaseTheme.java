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
package com.vaadin.ui.themes;

/**
 * <p>
 * The Base theme is the foundation for all Vaadin themes. Although it is not
 * necessary to use it as the starting point for all other themes, it is heavily
 * encouraged, since it abstracts and hides away many necessary style properties
 * that the Vaadin terminal expects and needs.
 * </p>
 * <p>
 * When creating your own theme, either extend this class and specify the styles
 * implemented in your theme here, or extend some other theme that has a class
 * file specified (e.g. Reindeer or Runo).
 * </p>
 * <p>
 * All theme class files should follow the convention of specifying the theme
 * name as a string constant <code>THEME_NAME</code>.
 * 
 * @since 6.3.0
 * 
 */
public class BaseTheme {

    public static final String THEME_NAME = "base";

    /**
     * Creates a button that looks like a regular hypertext link but still acts
     * like a normal button.
     */
    public static final String BUTTON_LINK = "link";

    /**
     * Removes extra decorations from the panel.
     * 
     * @deprecated Base theme does not implement this style, but it is defined
     *             here since it has been a part of the framework before
     *             multiple themes were available. Use the constant provided by
     *             the theme you're using instead, e.g.
     *             {@link Reindeer#PANEL_LIGHT} or {@link Runo#PANEL_LIGHT}.
     */
    @Deprecated
    public static final String PANEL_LIGHT = "light";

}
