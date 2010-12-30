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
package com.vaadin.ui;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.ui.VAbsoluteLayout;

/**
 * AbsoluteLayout is a layout implementation that mimics html absolute
 * positioning.
 * 
 */
@SuppressWarnings("serial")
@ClientWidget(VAbsoluteLayout.class)
public class AbsoluteLayout extends AbstractLayout {

    private static final String CLICK_EVENT = VAbsoluteLayout.CLICK_EVENT_IDENTIFIER;

    // The components in the layout
    private Collection<Component> components = new LinkedHashSet<Component>();

    // Maps each component to a position
    private Map<Component, ComponentPosition> componentToCoordinates = new HashMap<Component, ComponentPosition>();

    /**
     * Creates an AbsoluteLayout with full size.
     */
    public AbsoluteLayout() {
        setSizeFull();
    }

    /**
     * Gets an iterator for going through all components enclosed in the
     * absolute layout.
     */
    public Iterator<Component> getComponentIterator() {
        return components.iterator();
    }

    /**
     * Replaces one component with another one. The new component inherits the
     * old components position.
     */
    public void replaceComponent(Component oldComponent, Component newComponent) {
        ComponentPosition position = getPosition(oldComponent);
        removeComponent(oldComponent);
        addComponent(newComponent);
        componentToCoordinates.put(newComponent, position);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.AbstractComponentContainer#addComponent(com.vaadin.ui.Component
     * )
     */
    @Override
    public void addComponent(Component c) {
        components.add(c);
        try {
            super.addComponent(c);
            requestRepaint();
        } catch (IllegalArgumentException e) {
            components.remove(c);
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.AbstractComponentContainer#removeComponent(com.vaadin.ui
     * .Component)
     */
    @Override
    public void removeComponent(Component c) {
        components.remove(c);
        componentToCoordinates.remove(c);
        super.removeComponent(c);
        requestRepaint();
    }

    /**
     * Adds a component to the layout. The component can be positioned by
     * providing a string formatted in CSS-format.
     * <p>
     * For example the string "top:10px;left:10px" will position the component
     * 10 pixels from the left and 10 pixels from the top. The identifiers:
     * "top","left","right" and "bottom" can be used to specify the position.
     * </p>
     * 
     * @param c
     *            The component to add to the layout
     * @param cssPosition
     *            The css position string
     */
    public void addComponent(Component c, String cssPosition) {
        addComponent(c);
        getPosition(c).setCSSString(cssPosition);
    }

    /**
     * Gets the position of a component in the layout. Returns null if component
     * is not attached to the layout.
     * 
     * @param component
     *            The component which position is needed
     * @return An instance of ComponentPosition containing the position of the
     *         component, or null if the component is not enclosed in the
     *         layout.
     */
    public ComponentPosition getPosition(Component component) {
        if (component.getParent() != this) {
            return null;
        } else if (componentToCoordinates.containsKey(component)) {
            return componentToCoordinates.get(component);
        } else {
            ComponentPosition coords = new ComponentPosition();
            componentToCoordinates.put(component, coords);
            return coords;
        }
    }

    /**
     * The CompontPosition class represents a components position within the
     * absolute layout. It contains the attributes for left, right, top and
     * bottom and the units used to specify them.
     */
    public class ComponentPosition implements Serializable {

        private int zIndex = -1;
        private Float topValue = null;
        private Float rightValue = null;
        private Float bottomValue = null;
        private Float leftValue = null;

        private int topUnits;
        private int rightUnits;
        private int bottomUnits;
        private int leftUnits;

        /**
         * Sets the position attributes using CSS syntax. Attributes not
         * included in the string are reset to their unset states.
         * 
         * <code><pre>
         * setCSSString("top:10px;left:20%;z-index:16;");
         * </pre></code>
         * 
         * @param css
         */
        public void setCSSString(String css) {
            topValue = rightValue = bottomValue = leftValue = null;
            topUnits = rightUnits = bottomUnits = leftUnits = 0;
            zIndex = -1;
            if (css == null) {
                return;
            }

            String[] cssProperties = css.split(";");
            for (int i = 0; i < cssProperties.length; i++) {
                String[] keyValuePair = cssProperties[i].split(":");
                String key = keyValuePair[0].trim();
                if (key.equals("")) {
                    continue;
                }
                if (key.equals("z-index")) {
                    zIndex = Integer.parseInt(keyValuePair[1].trim());
                } else {
                    String value;
                    if (keyValuePair.length > 1) {
                        value = keyValuePair[1].trim();
                    } else {
                        value = "";
                    }
                    String unit = value.replaceAll("[0-9\\.\\-]+", "");
                    if (!unit.equals("")) {
                        value = value.substring(0, value.indexOf(unit)).trim();
                    }
                    float v = Float.parseFloat(value);
                    int unitInt = parseCssUnit(unit);
                    if (key.equals("top")) {
                        topValue = v;
                        topUnits = unitInt;
                    } else if (key.equals("right")) {
                        rightValue = v;
                        rightUnits = unitInt;
                    } else if (key.equals("bottom")) {
                        bottomValue = v;
                        bottomUnits = unitInt;
                    } else if (key.equals("left")) {
                        leftValue = v;
                        leftUnits = unitInt;
                    }
                }
            }
            requestRepaint();
        }

        /**
         * Parses a string and checks if a unit is found. If a unit is not found
         * from the string the unit pixels is used.
         * 
         * @param string
         *            The string to parse the unit from
         * @return The found unit
         */
        private int parseCssUnit(String string) {
            for (int i = 0; i < UNIT_SYMBOLS.length; i++) {
                if (UNIT_SYMBOLS[i].equals(string)) {
                    return i;
                }
            }
            return 0; // defaults to px (eg. top:0;)
        }

        /**
         * Converts the internal values into a valid CSS string.
         * 
         * @return A valid CSS string
         */
        public String getCSSString() {
            String s = "";
            if (topValue != null) {
                s += "top:" + topValue + UNIT_SYMBOLS[topUnits] + ";";
            }
            if (rightValue != null) {
                s += "right:" + rightValue + UNIT_SYMBOLS[rightUnits] + ";";
            }
            if (bottomValue != null) {
                s += "bottom:" + bottomValue + UNIT_SYMBOLS[bottomUnits] + ";";
            }
            if (leftValue != null) {
                s += "left:" + leftValue + UNIT_SYMBOLS[leftUnits] + ";";
            }
            if (zIndex >= 0) {
                s += "z-index:" + zIndex + ";";
            }
            return s;
        }

        /**
         * Sets the 'top' attribute; distance from the top of the component to
         * the top edge of the layout.
         * 
         * @param topValue
         *            The value of the 'top' attribute
         * @param topUnits
         *            The unit of the 'top' attribute. See UNIT_SYMBOLS for a
         *            description of the available units.
         */
        public void setTop(Float topValue, int topUnits) {
            this.topValue = topValue;
            this.topUnits = topUnits;
            requestRepaint();
        }

        /**
         * Sets the 'right' attribute; distance from the right of the component
         * to the right edge of the layout.
         * 
         * @param rightValue
         *            The value of the 'right' attribute
         * @param rightUnits
         *            The unit of the 'right' attribute. See UNIT_SYMBOLS for a
         *            description of the available units.
         */
        public void setRight(Float rightValue, int rightUnits) {
            this.rightValue = rightValue;
            this.rightUnits = rightUnits;
            requestRepaint();
        }

        /**
         * Sets the 'bottom' attribute; distance from the bottom of the
         * component to the bottom edge of the layout.
         * 
         * @param bottomValue
         *            The value of the 'bottom' attribute
         * @param units
         *            The unit of the 'bottom' attribute. See UNIT_SYMBOLS for a
         *            description of the available units.
         */
        public void setBottom(Float bottomValue, int bottomUnits) {
            this.bottomValue = bottomValue;
            this.bottomUnits = bottomUnits;
            requestRepaint();
        }

        /**
         * Sets the 'left' attribute; distance from the left of the component to
         * the left edge of the layout.
         * 
         * @param leftValue
         *            The value of the 'left' attribute
         * @param units
         *            The unit of the 'left' attribute. See UNIT_SYMBOLS for a
         *            description of the available units.
         */
        public void setLeft(Float leftValue, int leftUnits) {
            this.leftValue = leftValue;
            this.leftUnits = leftUnits;
            requestRepaint();
        }

        /**
         * Sets the 'z-index' attribute; the visual stacking order
         * 
         * @param zIndex
         *            The z-index for the component.
         */
        public void setZIndex(int zIndex) {
            this.zIndex = zIndex;
            requestRepaint();
        }

        /**
         * Sets the value of the 'top' attribute; distance from the top of the
         * component to the top edge of the layout.
         * 
         * @param topValue
         *            The value of the 'left' attribute
         */
        public void setTopValue(Float topValue) {
            this.topValue = topValue;
            requestRepaint();
        }

        /**
         * Gets the 'top' attributes value in current units.
         * 
         * @see #getTopUnits()
         * @return The value of the 'top' attribute, null if not set
         */
        public Float getTopValue() {
            return topValue;
        }

        /**
         * Gets the 'right' attributes value in current units.
         * 
         * @return The value of the 'right' attribute, null if not set
         * @see #getRightUnits()
         */
        public Float getRightValue() {
            return rightValue;
        }

        /**
         * Sets the 'right' attribute value (distance from the right of the
         * component to the right edge of the layout). Currently active units
         * are maintained.
         * 
         * @param rightValue
         *            The value of the 'right' attribute
         * @see #setRightUnits(int)
         */
        public void setRightValue(Float rightValue) {
            this.rightValue = rightValue;
            requestRepaint();
        }

        /**
         * Gets the 'bottom' attributes value using current units.
         * 
         * @return The value of the 'bottom' attribute, null if not set
         * @see #getBottomUnits()
         */
        public Float getBottomValue() {
            return bottomValue;
        }

        /**
         * Sets the 'bottom' attribute value (distance from the bottom of the
         * component to the bottom edge of the layout). Currently active units
         * are maintained.
         * 
         * @param bottomValue
         *            The value of the 'bottom' attribute
         * @see #setBottomUnits(int)
         */
        public void setBottomValue(Float bottomValue) {
            this.bottomValue = bottomValue;
            requestRepaint();
        }

        /**
         * Gets the 'left' attributes value using current units.
         * 
         * @return The value of the 'left' attribute, null if not set
         * @see #getLeftUnits()
         */
        public Float getLeftValue() {
            return leftValue;
        }

        /**
         * Sets the 'left' attribute value (distance from the left of the
         * component to the left edge of the layout). Currently active units are
         * maintained.
         * 
         * @param leftValue
         *            The value of the 'left' CSS-attribute
         * @see #setLeftUnits(int)
         */
        public void setLeftValue(Float leftValue) {
            this.leftValue = leftValue;
            requestRepaint();
        }

        /**
         * Gets the unit for the 'top' attribute
         * 
         * @return See {@link Sizeable} UNIT_SYMBOLS for a description of the
         *         available units.
         */
        public int getTopUnits() {
            return topUnits;
        }

        /**
         * Sets the unit for the 'top' attribute
         * 
         * @param topUnits
         *            See {@link Sizeable} UNIT_SYMBOLS for a description of the
         *            available units.
         */
        public void setTopUnits(int topUnits) {
            this.topUnits = topUnits;
            requestRepaint();
        }

        /**
         * Gets the unit for the 'right' attribute
         * 
         * @return See {@link Sizeable} UNIT_SYMBOLS for a description of the
         *         available units.
         */
        public int getRightUnits() {
            return rightUnits;
        }

        /**
         * Sets the unit for the 'right' attribute
         * 
         * @param rightUnits
         *            See {@link Sizeable} UNIT_SYMBOLS for a description of the
         *            available units.
         */
        public void setRightUnits(int rightUnits) {
            this.rightUnits = rightUnits;
            requestRepaint();
        }

        /**
         * Gets the unit for the 'bottom' attribute
         * 
         * @return See {@link Sizeable} UNIT_SYMBOLS for a description of the
         *         available units.
         */
        public int getBottomUnits() {
            return bottomUnits;
        }

        /**
         * Sets the unit for the 'bottom' attribute
         * 
         * @param bottomUnits
         *            See {@link Sizeable} UNIT_SYMBOLS for a description of the
         *            available units.
         */
        public void setBottomUnits(int bottomUnits) {
            this.bottomUnits = bottomUnits;
            requestRepaint();
        }

        /**
         * Gets the unit for the 'left' attribute
         * 
         * @return See {@link Sizeable} UNIT_SYMBOLS for a description of the
         *         available units.
         */
        public int getLeftUnits() {
            return leftUnits;
        }

        /**
         * Sets the unit for the 'left' attribute
         * 
         * @param leftUnits
         *            See {@link Sizeable} UNIT_SYMBOLS for a description of the
         *            available units.
         */
        public void setLeftUnits(int leftUnits) {
            this.leftUnits = leftUnits;
            requestRepaint();
        }

        /**
         * Gets the 'z-index' attribute.
         * 
         * @return the zIndex The z-index attribute
         */
        public int getZIndex() {
            return zIndex;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return getCSSString();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.AbstractLayout#paintContent(com.vaadin.terminal.PaintTarget
     * )
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        for (Component component : components) {
            target.startTag("cc");
            target.addAttribute("css", getPosition(component).getCSSString());
            component.paint(target);
            target.endTag("cc");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.AbstractComponent#changeVariables(java.lang.Object,
     * java.util.Map)
     */
    @Override
    public void changeVariables(Object source, Map variables) {
        super.changeVariables(source, variables);
        if (variables.containsKey(CLICK_EVENT)) {
            fireClick((Map<String, Object>) variables.get(CLICK_EVENT));
        }

    }

    /**
     * Fires a click event when the layout is clicked
     * 
     * @param parameters
     *            The parameters recieved from the client side implementation
     */
    private void fireClick(Map<String, Object> parameters) {
        MouseEventDetails mouseDetails = MouseEventDetails
                .deSerialize((String) parameters.get("mouseDetails"));
        Component childComponent = (Component) parameters.get("component");

        fireEvent(new LayoutClickEvent(this, mouseDetails, childComponent));
    }

    /**
     * Add a click listener to the layout. The listener is called whenever the
     * user clicks inside the layout. Also when the click targets a component
     * inside the Panel, provided the targeted component does not prevent the
     * click event from propagating.
     * 
     * The child component that was clicked is included in the
     * {@link LayoutClickEvent}.
     * 
     * Use {@link #removeListener(LayoutClickListener)} to remove the listener.
     * 
     * @param listener
     *            The listener to add
     */
    public void addListener(LayoutClickListener listener) {
        addListener(CLICK_EVENT, LayoutClickEvent.class, listener,
                LayoutClickListener.clickMethod);
    }

    /**
     * Remove a click listener from the layout. The listener should earlier have
     * been added using {@link #addListener(LayoutClickListener)}.
     * 
     * @param listener
     *            The listener to remove
     */
    public void removeListener(LayoutClickListener listener) {
        removeListener(CLICK_EVENT, LayoutClickEvent.class, listener);
    }

}
