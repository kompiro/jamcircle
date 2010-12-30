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
 * Interface to be implemented by components wishing to display some object that
 * may be dynamically resized during runtime.
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 3.0
 */
public interface Sizeable extends Serializable {

    /**
     * Unit code representing pixels.
     */
    public static final int UNITS_PIXELS = 0;

    /**
     * Unit code representing points (1/72nd of an inch).
     */
    public static final int UNITS_POINTS = 1;

    /**
     * Unit code representing picas (12 points).
     */
    public static final int UNITS_PICAS = 2;

    /**
     * Unit code representing the font-size of the relevant font.
     */
    public static final int UNITS_EM = 3;

    /**
     * Unit code representing the x-height of the relevant font.
     */
    public static final int UNITS_EX = 4;

    /**
     * Unit code representing millimeters.
     */
    public static final int UNITS_MM = 5;

    /**
     * Unit code representing centimeters.
     */
    public static final int UNITS_CM = 6;

    /**
     * Unit code representing inches.
     */
    public static final int UNITS_INCH = 7;

    /**
     * Unit code representing in percentage of the containing element defined by
     * terminal.
     */
    public static final int UNITS_PERCENTAGE = 8;

    public static final float SIZE_UNDEFINED = -1;

    /**
     * Textual representations of units symbols. Supported units and their
     * symbols are:
     * <ul>
     * <li>{@link #UNITS_PIXELS}: "px"</li>
     * <li>{@link #UNITS_POINTS}: "pt"</li>
     * <li>{@link #UNITS_PICAS}: "pc"</li>
     * <li>{@link #UNITS_EM}: "em"</li>
     * <li>{@link #UNITS_EX}: "ex"</li>
     * <li>{@link #UNITS_MM}: "mm"</li>
     * <li>{@link #UNITS_CM}. "cm"</li>
     * <li>{@link #UNITS_INCH}: "in"</li>
     * <li>{@link #UNITS_PERCENTAGE}: "%"</li>
     * </ul>
     * These can be used like <code>Sizeable.UNIT_SYMBOLS[UNITS_PIXELS]</code>.
     */
    public static final String[] UNIT_SYMBOLS = { "px", "pt", "pc", "em", "ex",
            "mm", "cm", "in", "%" };

    /**
     * Gets the width of the object. Negative number implies unspecified size
     * (terminal is free to set the size).
     * 
     * @return width of the object in units specified by widthUnits property.
     */
    public float getWidth();

    /**
     * Sets the width of the object. Negative number implies unspecified size
     * (terminal is free to set the size).
     * 
     * @param width
     *            the width of the object in units specified by widthUnits
     *            property.
     * @deprecated Consider using {@link #setWidth(String)} instead. This method
     *             works, but is error-prone since the unit must be set
     *             separately (and components might have different default
     *             unit).
     */
    @Deprecated
    public void setWidth(float width);

    /**
     * Gets the height of the object. Negative number implies unspecified size
     * (terminal is free to set the size).
     * 
     * @return height of the object in units specified by heightUnits property.
     */
    public float getHeight();

    /**
     * Sets the height of the object. Negative number implies unspecified size
     * (terminal is free to set the size).
     * 
     * @param height
     *            the height of the object in units specified by heightUnits
     *            property.
     * @deprecated Consider using {@link #setHeight(String)} or
     *             {@link #setHeight(float, int)} instead. This method works,
     *             but is error-prone since the unit must be set separately (and
     *             components might have different default unit).
     */
    @Deprecated
    public void setHeight(float height);

    /**
     * Gets the width property units.
     * 
     * @return units used in width property.
     */
    public int getWidthUnits();

    /**
     * Sets the width property units.
     * 
     * @param units
     *            the units used in width property.
     * @deprecated Consider setting width and unit simultaneously using
     *             {@link #setWidth(String)} or {@link #setWidth(float, int)},
     *             which is less error-prone.
     */
    @Deprecated
    public void setWidthUnits(int units);

    /**
     * Gets the height property units.
     * 
     * @return units used in height property.
     */
    public int getHeightUnits();

    /**
     * Sets the height property units.
     * 
     * @param units
     *            the units used in height property.
     * @deprecated Consider setting height and unit simultaneously using
     *             {@link #setHeight(String)} or {@link #setHeight(float, int)},
     *             which is less error-prone.
     */
    @Deprecated
    public void setHeightUnits(int units);

    /**
     * Sets the height of the component using String presentation.
     * 
     * String presentation is similar to what is used in Cascading Style Sheets.
     * Size can be length or percentage of available size.
     * 
     * The empty string ("") or null will unset the height and set the units to
     * pixels.
     * 
     * See <a
     * href="http://www.w3.org/TR/REC-CSS2/syndata.html#value-def-length">CSS
     * specification</a> for more details.
     * 
     * @param height
     *            in CSS style string representation
     */
    public void setHeight(String height);

    /**
     * Sets the width of the object. Negative number implies unspecified size
     * (terminal is free to set the size).
     * 
     * @param width
     *            the width of the object.
     * @param unit
     *            the unit used for the width. Possible values include
     *            {@link #UNITS_PIXELS}, {@link #UNITS_POINTS},
     *            {@link #UNITS_PICAS}, {@link #UNITS_EM}, {@link #UNITS_EX},
     *            {@link #UNITS_MM}, {@link #UNITS_CM}, {@link #UNITS_INCH},
     *            {@link #UNITS_PERCENTAGE}.
     */
    public void setWidth(float width, int unit);

    /**
     * Sets the height of the object. Negative number implies unspecified size
     * (terminal is free to set the size).
     * 
     * @param height
     *            the height of the object.
     * @param unit
     *            the unit used for the width. Possible values include
     *            {@link #UNITS_PIXELS}, {@link #UNITS_POINTS},
     *            {@link #UNITS_PICAS}, {@link #UNITS_EM}, {@link #UNITS_EX},
     *            {@link #UNITS_MM}, {@link #UNITS_CM}, {@link #UNITS_INCH},
     *            {@link #UNITS_PERCENTAGE}.
     */
    public void setHeight(float height, int unit);

    /**
     * Sets the width of the component using String presentation.
     * 
     * String presentation is similar to what is used in Cascading Style Sheets.
     * Size can be length or percentage of available size.
     * 
     * The empty string ("") or null will unset the width and set the units to
     * pixels.
     * 
     * See <a
     * href="http://www.w3.org/TR/REC-CSS2/syndata.html#value-def-length">CSS
     * specification</a> for more details.
     * 
     * @param width
     *            in CSS style string representation, null or empty string to
     *            reset
     */
    public void setWidth(String width);

    /**
     * Sets the size to 100% x 100%.
     */
    public void setSizeFull();

    /**
     * Clears any size settings.
     */
    public void setSizeUndefined();

}
