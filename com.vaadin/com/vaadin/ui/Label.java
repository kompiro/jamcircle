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

import java.lang.reflect.Method;

import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.VLabel;
import com.vaadin.ui.ClientWidget.LoadStyle;

/**
 * Label component for showing non-editable short texts.
 * 
 * The label content can be set to the modes specified by the final members
 * CONTENT_*
 * 
 * <p>
 * The contents of the label may contain simple formatting:
 * <ul>
 * <li><b>&lt;b></b> Bold
 * <li><b>&lt;i></b> Italic
 * <li><b>&lt;u></b> Underlined
 * <li><b>&lt;br/></b> Linebreak
 * <li><b>&lt;ul>&lt;li>item 1&lt;/li>&lt;li>item 2&lt;/li>&lt;/ul></b> List of
 * items
 * </ul>
 * The <b>b</b>,<b>i</b>,<b>u</b> and <b>li</b> tags can contain all the tags in
 * the list recursively.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 3.0
 */
@SuppressWarnings("serial")
@ClientWidget(value = VLabel.class, loadStyle = LoadStyle.EAGER)
public class Label extends AbstractComponent implements Property,
        Property.Viewer, Property.ValueChangeListener,
        Property.ValueChangeNotifier, Comparable {

    /**
     * Content mode, where the label contains only plain text. The getValue()
     * result is coded to XML when painting.
     */
    public static final int CONTENT_TEXT = 0;

    /**
     * Content mode, where the label contains preformatted text.
     */
    public static final int CONTENT_PREFORMATTED = 1;

    /**
     * Formatted content mode, where the contents is XML restricted to the UIDL
     * 1.0 formatting markups.
     * 
     * @deprecated Use CONTENT_XML instead.
     */
    @Deprecated
    public static final int CONTENT_UIDL = 2;

    /**
     * Content mode, where the label contains XHTML. Contents is then enclosed
     * in DIV elements having namespace of
     * "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd".
     */
    public static final int CONTENT_XHTML = 3;

    /**
     * Content mode, where the label contains well-formed or well-balanced XML.
     * Each of the root elements must have their default namespace specified.
     */
    public static final int CONTENT_XML = 4;

    /**
     * Content mode, where the label contains RAW output. Output is not required
     * to comply to with XML. In Web Adapter output is inserted inside the
     * resulting HTML document as-is. This is useful for some specific purposes
     * where possibly broken HTML content needs to be shown, but in most cases
     * XHTML mode should be preferred.
     */
    public static final int CONTENT_RAW = 5;

    /**
     * The default content mode is plain text.
     */
    public static final int CONTENT_DEFAULT = CONTENT_TEXT;

    /** Array of content mode names that are rendered in UIDL as mode attribute. */
    private static final String[] CONTENT_MODE_NAME = { "text", "pre", "uidl",
            "xhtml", "xml", "raw" };

    private static final String DATASOURCE_MUST_BE_SET = "Datasource must be set";

    private Property dataSource;

    private int contentMode = CONTENT_DEFAULT;

    /**
     * Creates an empty Label.
     */
    public Label() {
        this("");
    }

    /**
     * Creates a new instance of Label with text-contents.
     * 
     * @param content
     */
    public Label(String content) {
        this(content, CONTENT_DEFAULT);
    }

    /**
     * Creates a new instance of Label with text-contents read from given
     * datasource.
     * 
     * @param contentSource
     */
    public Label(Property contentSource) {
        this(contentSource, CONTENT_DEFAULT);
    }

    /**
     * Creates a new instance of Label with text-contents.
     * 
     * @param content
     * @param contentMode
     */
    public Label(String content, int contentMode) {
        this(new ObjectProperty(content, String.class), contentMode);
    }

    /**
     * Creates a new instance of Label with text-contents read from given
     * datasource.
     * 
     * @param contentSource
     * @param contentMode
     */
    public Label(Property contentSource, int contentMode) {
        setPropertyDataSource(contentSource);
        if (contentMode != CONTENT_DEFAULT) {
            setContentMode(contentMode);
        }
        setWidth(100, UNITS_PERCENTAGE);
    }

    /**
     * Set the component to read-only. Readonly is not used in label.
     * 
     * @param readOnly
     *            True to enable read-only mode, False to disable it.
     */
    @Override
    public void setReadOnly(boolean readOnly) {
        if (dataSource == null) {
            throw new IllegalStateException(DATASOURCE_MUST_BE_SET);
        }
        dataSource.setReadOnly(readOnly);
    }

    /**
     * Is the component read-only ? Readonly is not used in label - this returns
     * allways false.
     * 
     * @return <code>true</code> if the component is in read only mode.
     */
    @Override
    public boolean isReadOnly() {
        if (dataSource == null) {
            throw new IllegalStateException(DATASOURCE_MUST_BE_SET);
        }
        return dataSource.isReadOnly();
    }

    /**
     * Paints the content of this component.
     * 
     * @param target
     *            the Paint Event.
     * @throws PaintException
     *             if the Paint Operation fails.
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        if (contentMode != CONTENT_TEXT) {
            target.addAttribute("mode", CONTENT_MODE_NAME[contentMode]);
        }
        if (contentMode == CONTENT_TEXT) {
            target.addText(toString());
        } else if (contentMode == CONTENT_UIDL) {
            target.addUIDL(toString());
        } else if (contentMode == CONTENT_XHTML) {
            target.startTag("data");
            target.addXMLSection("div", toString(),
                    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
            target.endTag("data");
        } else if (contentMode == CONTENT_PREFORMATTED) {
            target.startTag("pre");
            target.addText(toString());
            target.endTag("pre");
        } else if (contentMode == CONTENT_XML) {
            target.addXMLSection("data", toString(), null);
        } else if (contentMode == CONTENT_RAW) {
            target.startTag("data");
            target.addAttribute("escape", false);
            target.addText(toString());
            target.endTag("data");
        }

    }

    /**
     * Gets the value of the label. Value of the label is the XML contents of
     * the label.
     * 
     * @return the Value of the label.
     */
    public Object getValue() {
        if (dataSource == null) {
            throw new IllegalStateException(DATASOURCE_MUST_BE_SET);
        }
        return dataSource.getValue();
    }

    /**
     * Set the value of the label. Value of the label is the XML contents of the
     * label.
     * 
     * @param newValue
     *            the New value of the label.
     */
    public void setValue(Object newValue) {
        if (dataSource == null) {
            throw new IllegalStateException(DATASOURCE_MUST_BE_SET);
        }
        dataSource.setValue(newValue);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (dataSource == null) {
            throw new IllegalStateException(DATASOURCE_MUST_BE_SET);
        }
        return dataSource.toString();
    }

    /**
     * Gets the type of the Property.
     * 
     * @see com.vaadin.data.Property#getType()
     */
    public Class getType() {
        if (dataSource == null) {
            throw new IllegalStateException(DATASOURCE_MUST_BE_SET);
        }
        return dataSource.getType();
    }

    /**
     * Gets the viewing data-source property.
     * 
     * @return the data source property.
     * @see com.vaadin.data.Property.Viewer#getPropertyDataSource()
     */
    public Property getPropertyDataSource() {
        return dataSource;
    }

    /**
     * Sets the property as data-source for viewing.
     * 
     * @param newDataSource
     *            the new data source Property
     * @see com.vaadin.data.Property.Viewer#setPropertyDataSource(com.vaadin.data.Property)
     */
    public void setPropertyDataSource(Property newDataSource) {
        // Stops listening the old data source changes
        if (dataSource != null
                && Property.ValueChangeNotifier.class
                        .isAssignableFrom(dataSource.getClass())) {
            ((Property.ValueChangeNotifier) dataSource).removeListener(this);
        }

        // Sets the new data source
        dataSource = newDataSource;

        // Listens the new data source if possible
        if (dataSource != null
                && Property.ValueChangeNotifier.class
                        .isAssignableFrom(dataSource.getClass())) {
            ((Property.ValueChangeNotifier) dataSource).addListener(this);
        }
        requestRepaint();
    }

    /**
     * Gets the content mode of the Label.
     * 
     * <p>
     * Possible content modes include:
     * <ul>
     * <li><b>CONTENT_TEXT</b> Content mode, where the label contains only plain
     * text. The getValue() result is coded to XML when painting.</li>
     * <li><b>CONTENT_PREFORMATTED</b> Content mode, where the label contains
     * preformatted text.</li>
     * <li><b>CONTENT_UIDL</b> Formatted content mode, where the contents is XML
     * restricted to the UIDL 1.0 formatting markups.</li>
     * <li><b>CONTENT_XHTML</b> Content mode, where the label contains XHTML.
     * Contents is then enclosed in DIV elements having namespace of
     * "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd".</li>
     * <li><b>CONTENT_XML</b> Content mode, where the label contains well-formed
     * or well-balanced XML. Each of the root elements must have their default
     * namespace specified.</li>
     * <li><b>CONTENT_RAW</b> Content mode, where the label contains RAW output.
     * Output is not required to comply to with XML. In Web Adapter output is
     * inserted inside the resulting HTML document as-is. This is useful for
     * some specific purposes where possibly broken HTML content needs to be
     * shown, but in most cases XHTML mode should be preferred.</li>
     * </ul>
     * </p>
     * 
     * @return the Content mode of the label.
     */
    public int getContentMode() {
        return contentMode;
    }

    /**
     * Sets the content mode of the Label.
     * 
     * <p>
     * Possible content modes include:
     * <ul>
     * <li><b>CONTENT_TEXT</b> Content mode, where the label contains only plain
     * text. The getValue() result is coded to XML when painting.</li>
     * <li><b>CONTENT_PREFORMATTED</b> Content mode, where the label contains
     * preformatted text.</li>
     * <li><b>CONTENT_UIDL</b> Formatted content mode, where the contents is XML
     * restricted to the UIDL 1.0 formatting markups.</li>
     * <li><b>CONTENT_XHTML</b> Content mode, where the label contains XHTML.
     * Contents is then enclosed in DIV elements having namespace of
     * "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd".</li>
     * <li><b>CONTENT_XML</b> Content mode, where the label contains well-formed
     * or well-balanced XML. Each of the root elements must have their default
     * namespace specified.</li>
     * <li><b>CONTENT_RAW</b> Content mode, where the label contains RAW output.
     * Output is not required to comply to with XML. In Web Adapter output is
     * inserted inside the resulting HTML document as-is. This is useful for
     * some specific purposes where possibly broken HTML content needs to be
     * shown, but in most cases XHTML mode should be preferred.</li>
     * </ul>
     * </p>
     * 
     * @param contentMode
     *            the New content mode of the label.
     */
    public void setContentMode(int contentMode) {
        if (contentMode != this.contentMode && contentMode >= CONTENT_TEXT
                && contentMode <= CONTENT_RAW) {
            this.contentMode = contentMode;
            requestRepaint();
        }
    }

    /* Value change events */

    private static final Method VALUE_CHANGE_METHOD;

    static {
        try {
            VALUE_CHANGE_METHOD = Property.ValueChangeListener.class
                    .getDeclaredMethod("valueChange",
                            new Class[] { Property.ValueChangeEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in Label");
        }
    }

    /**
     * Value change event
     * 
     * @author IT Mill Ltd.
     * @version
     * 6.4.8
     * @since 3.0
     */
    public class ValueChangeEvent extends Component.Event implements
            Property.ValueChangeEvent {

        /**
         * New instance of text change event
         * 
         * @param source
         *            the Source of the event.
         */
        public ValueChangeEvent(Label source) {
            super(source);
        }

        /**
         * Gets the Property that has been modified.
         * 
         * @see com.vaadin.data.Property.ValueChangeEvent#getProperty()
         */
        public Property getProperty() {
            return (Property) getSource();
        }
    }

    /**
     * Adds the value change listener.
     * 
     * @param listener
     *            the Listener to be added.
     * @see com.vaadin.data.Property.ValueChangeNotifier#addListener(com.vaadin.data.Property.ValueChangeListener)
     */
    public void addListener(Property.ValueChangeListener listener) {
        addListener(Label.ValueChangeEvent.class, listener, VALUE_CHANGE_METHOD);
    }

    /**
     * Removes the value change listener.
     * 
     * @param listener
     *            the Listener to be removed.
     * @see com.vaadin.data.Property.ValueChangeNotifier#removeListener(com.vaadin.data.Property.ValueChangeListener)
     */
    public void removeListener(Property.ValueChangeListener listener) {
        removeListener(Label.ValueChangeEvent.class, listener,
                VALUE_CHANGE_METHOD);
    }

    /**
     * Emits the options change event.
     */
    protected void fireValueChange() {
        // Set the error message
        fireEvent(new Label.ValueChangeEvent(this));
        requestRepaint();
    }

    /**
     * Listens the value change events from data source.
     * 
     * @see com.vaadin.data.Property.ValueChangeListener#valueChange(Property.ValueChangeEvent)
     */
    public void valueChange(Property.ValueChangeEvent event) {
        fireValueChange();
    }

    /**
     * Compares the Label to other objects.
     * 
     * <p>
     * Labels can be compared to other labels for sorting label contents. This
     * is especially handy for sorting table columns.
     * </p>
     * 
     * <p>
     * In RAW, PREFORMATTED and TEXT modes, the label contents are compared as
     * is. In XML, UIDL and XHTML modes, only CDATA is compared and tags
     * ignored. If the other object is not a Label, its toString() return value
     * is used in comparison.
     * </p>
     * 
     * @param other
     *            the Other object to compare to.
     * @return a negative integer, zero, or a positive integer as this object is
     *         less than, equal to, or greater than the specified object.
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object other) {

        String thisValue;
        String otherValue;

        if (contentMode == CONTENT_XML || contentMode == CONTENT_UIDL
                || contentMode == CONTENT_XHTML) {
            thisValue = stripTags(toString());
        } else {
            thisValue = toString();
        }

        if (other instanceof Label
                && (((Label) other).getContentMode() == CONTENT_XML
                        || ((Label) other).getContentMode() == CONTENT_UIDL || ((Label) other)
                        .getContentMode() == CONTENT_XHTML)) {
            otherValue = stripTags(other.toString());
        } else {
            otherValue = other.toString();
        }

        return thisValue.compareTo(otherValue);
    }

    /**
     * Strips the tags from the XML.
     * 
     * @param xml
     *            the String containing a XML snippet.
     * @return the original XML without tags.
     */
    private String stripTags(String xml) {

        final StringBuffer res = new StringBuffer();

        int processed = 0;
        final int xmlLen = xml.length();
        while (processed < xmlLen) {
            int next = xml.indexOf('<', processed);
            if (next < 0) {
                next = xmlLen;
            }
            res.append(xml.substring(processed, next));
            if (processed < xmlLen) {
                next = xml.indexOf('>', processed);
                if (next < 0) {
                    next = xmlLen;
                }
                processed = next + 1;
            }
        }

        return res.toString();
    }

}
