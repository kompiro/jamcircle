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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.Application;
import com.vaadin.event.EventRouter;
import com.vaadin.event.MethodEventSource;
import com.vaadin.terminal.ErrorMessage;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.Terminal;
import com.vaadin.terminal.gwt.server.ComponentSizeValidator;
import com.vaadin.tools.ReflectTools;

/**
 * An abstract class that defines default implementation for the
 * {@link Component} interface. Basic UI components that are not derived from an
 * external component can inherit this class to easily qualify as Vaadin
 * components. Most components in Vaadin do just that.
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 3.0
 */
@SuppressWarnings("serial")
public abstract class AbstractComponent implements Component, MethodEventSource {

    /* Private members */

    /**
     * Style names.
     */
    private ArrayList<String> styles;

    /**
     * Caption text.
     */
    private String caption;

    /**
     * Application specific data object. The component does not use or modify
     * this.
     */
    private Object applicationData;

    /**
     * Icon to be shown together with caption.
     */
    private Resource icon;

    /**
     * Is the component enabled (its normal usage is allowed).
     */
    private boolean enabled = true;

    /**
     * Is the component visible (it is rendered).
     */
    private boolean visible = true;

    /**
     * Is the component read-only ?
     */
    private boolean readOnly = false;

    /**
     * Description of the usage (XML).
     */
    private String description = null;

    /**
     * The container this component resides in.
     */
    private Component parent = null;

    /**
     * The EventRouter used for the event model.
     */
    private EventRouter eventRouter = null;

    /**
     * A set of event identifiers with registered listeners.
     */
    private Set<String> eventIdentifiers = null;

    /**
     * The internal error message of the component.
     */
    private ErrorMessage componentError = null;

    /**
     * Immediate mode: if true, all variable changes are required to be sent
     * from the terminal immediately.
     */
    private boolean immediate = false;

    /**
     * Locale of this component.
     */
    private Locale locale;

    /**
     * The component should receive focus (if {@link Focusable}) when attached.
     */
    private boolean delayedFocus;

    /**
     * List of repaint request listeners or null if not listened at all.
     */
    private LinkedList<RepaintRequestListener> repaintRequestListeners = null;

    /**
     * Are all the repaint listeners notified about recent changes ?
     */
    private boolean repaintRequestListenersNotified = false;

    private String testingId;

    /* Sizeable fields */

    private float width = SIZE_UNDEFINED;
    private float height = SIZE_UNDEFINED;
    private int widthUnit = UNITS_PIXELS;
    private int heightUnit = UNITS_PIXELS;
    private static final Pattern sizePattern = Pattern
            .compile("^(-?\\d+(\\.\\d+)?)(%|px|em|ex|in|cm|mm|pt|pc)?$");

    private ComponentErrorHandler errorHandler = null;

    /* Constructor */

    /**
     * Constructs a new Component.
     */
    public AbstractComponent() {
        // ComponentSizeValidator.setCreationLocation(this);
    }

    /* Get/Set component properties */

    /**
     * Gets the UIDL tag corresponding to the component.
     * 
     * <p>
     * Note! In version 6.2 the method for mapping server side components to
     * their client side counterparts was enhanced. This method was made final
     * to intentionally "break" code where it is needed. If your code does not
     * compile due overriding this method, it is very likely that you need to:
     * <ul>
     * <li>remove the implementation of getTag
     * <li>add {@link ClientWidget} annotation to your component
     * </ul>
     * 
     * @return the component's UIDL tag as <code>String</code>
     * @deprecated tags are no more required for components. Instead of tags we
     *             are now using {@link ClientWidget} annotations to map server
     *             side components to client side counterparts. Generating
     *             identifier for component type is delegated to terminal.
     * @see ClientWidget
     */
    @Deprecated
    public final String getTag() {
        return "";
    }

    public void setDebugId(String id) {
        testingId = id;
    }

    public String getDebugId() {
        return testingId;
    }

    /**
     * Gets style for component. Multiple styles are joined with spaces.
     * 
     * @return the component's styleValue of property style.
     * @deprecated Use getStyleName() instead; renamed for consistency and to
     *             indicate that "style" should not be used to switch client
     *             side implementation, only to style the component.
     */
    @Deprecated
    public String getStyle() {
        return getStyleName();
    }

    /**
     * Sets and replaces all previous style names of the component. This method
     * will trigger a {@link com.vaadin.terminal.Paintable.RepaintRequestEvent
     * RepaintRequestEvent}.
     * 
     * @param style
     *            the new style of the component.
     * @deprecated Use setStyleName() instead; renamed for consistency and to
     *             indicate that "style" should not be used to switch client
     *             side implementation, only to style the component.
     */
    @Deprecated
    public void setStyle(String style) {
        setStyleName(style);
    }

    /*
     * Gets the component's style. Don't add a JavaDoc comment here, we use the
     * default documentation from implemented interface.
     */
    public String getStyleName() {
        String s = "";
        if (styles != null) {
            for (final Iterator<String> it = styles.iterator(); it.hasNext();) {
                s += it.next();
                if (it.hasNext()) {
                    s += " ";
                }
            }
        }
        return s;
    }

    /*
     * Sets the component's style. Don't add a JavaDoc comment here, we use the
     * default documentation from implemented interface.
     */
    public void setStyleName(String style) {
        if (style == null || "".equals(style)) {
            styles = null;
            requestRepaint();
            return;
        }
        if (styles == null) {
            styles = new ArrayList<String>();
        }
        styles.clear();
        styles.add(style);
        requestRepaint();
    }

    public void addStyleName(String style) {
        if (style == null || "".equals(style)) {
            return;
        }
        if (styles == null) {
            styles = new ArrayList<String>();
        }
        if (!styles.contains(style)) {
            styles.add(style);
            requestRepaint();
        }
    }

    public void removeStyleName(String style) {
        if (styles != null) {
            styles.remove(style);
            requestRepaint();
        }
    }

    /*
     * Get's the component's caption. Don't add a JavaDoc comment here, we use
     * the default documentation from implemented interface.
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets the component's caption <code>String</code>. Caption is the visible
     * name of the component. This method will trigger a
     * {@link com.vaadin.terminal.Paintable.RepaintRequestEvent
     * RepaintRequestEvent}.
     * 
     * @param caption
     *            the new caption <code>String</code> for the component.
     */
    public void setCaption(String caption) {
        this.caption = caption;
        requestRepaint();
    }

    /*
     * Don't add a JavaDoc comment here, we use the default documentation from
     * implemented interface.
     */
    public Locale getLocale() {
        if (locale != null) {
            return locale;
        }
        if (parent != null) {
            return parent.getLocale();
        }
        final Application app = getApplication();
        if (app != null) {
            return app.getLocale();
        }
        return null;
    }

    /**
     * Sets the locale of this component.
     * 
     * <pre class='code'>
     * // Component for which the locale is meaningful
     * InlineDateField date = new InlineDateField(&quot;Datum&quot;);
     * 
     * // German language specified with ISO 639-1 language
     * // code and ISO 3166-1 alpha-2 country code.
     * date.setLocale(new Locale(&quot;de&quot;, &quot;DE&quot;));
     * 
     * date.setResolution(DateField.RESOLUTION_DAY);
     * layout.addComponent(date);
     * </pre>
     * 
     * 
     * @param locale
     *            the locale to become this component's locale.
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
        requestRepaint();
    }

    /*
     * Gets the component's icon resource. Don't add a JavaDoc comment here, we
     * use the default documentation from implemented interface.
     */
    public Resource getIcon() {
        return icon;
    }

    /**
     * Sets the component's icon. This method will trigger a
     * {@link com.vaadin.terminal.Paintable.RepaintRequestEvent
     * RepaintRequestEvent}.
     * 
     * @param icon
     *            the icon to be shown with the component's caption.
     */
    public void setIcon(Resource icon) {
        this.icon = icon;
        requestRepaint();
    }

    /*
     * Tests if the component is enabled or not. Don't add a JavaDoc comment
     * here, we use the default documentation from implemented interface.
     */
    public boolean isEnabled() {
        return enabled && (parent == null || parent.isEnabled()) && isVisible();
    }

    /*
     * Enables or disables the component. Don't add a JavaDoc comment here, we
     * use the default documentation from implemented interface.
     */
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            boolean wasEnabled = this.enabled;
            boolean wasEnabledInContext = isEnabled();

            this.enabled = enabled;

            boolean isEnabled = enabled;
            boolean isEnabledInContext = isEnabled();

            // If the actual enabled state (as rendered, in context) has not
            // changed we do not need to repaint except if the parent is
            // invisible.
            // If the parent is invisible we must request a repaint so the
            // component is repainted with the new enabled state when the parent
            // is set visible again. This workaround is needed as isEnabled
            // checks isVisible.
            boolean needRepaint = (wasEnabledInContext != isEnabledInContext)
                    || (wasEnabled != isEnabled && (getParent() == null || !getParent()
                            .isVisible()));

            if (needRepaint) {
                requestRepaint();
            }
        }
    }

    /*
     * Tests if the component is in the immediate mode. Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    public boolean isImmediate() {
        return immediate;
    }

    /**
     * Sets the component's immediate mode to the specified status. This method
     * will trigger a {@link com.vaadin.terminal.Paintable.RepaintRequestEvent
     * RepaintRequestEvent}.
     * 
     * @param immediate
     *            the boolean value specifying if the component should be in the
     *            immediate mode after the call.
     * @see Component#isImmediate()
     */
    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
        requestRepaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component#isVisible()
     */
    public boolean isVisible() {
        return visible && (getParent() == null || getParent().isVisible());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component#setVisible(boolean)
     */
    public void setVisible(boolean visible) {

        if (this.visible != visible) {
            this.visible = visible;
            // Instead of requesting repaint normally we
            // fire the event directly to assure that the
            // event goes through event in the component might
            // now be invisible
            fireRequestRepaintEvent(null);
        }
    }

    /**
     * <p>
     * Gets the component's description. The description can be used to briefly
     * describe the state of the component to the user. The description string
     * may contain certain XML tags:
     * </p>
     * 
     * <p>
     * <table border=1>
     * <tr>
     * <td width=120><b>Tag</b></td>
     * <td width=120><b>Description</b></td>
     * <td width=120><b>Example</b></td>
     * </tr>
     * <tr>
     * <td>&lt;b></td>
     * <td>bold</td>
     * <td><b>bold text</b></td>
     * </tr>
     * <tr>
     * <td>&lt;i></td>
     * <td>italic</td>
     * <td><i>italic text</i></td>
     * </tr>
     * <tr>
     * <td>&lt;u></td>
     * <td>underlined</td>
     * <td><u>underlined text</u></td>
     * </tr>
     * <tr>
     * <td>&lt;br></td>
     * <td>linebreak</td>
     * <td>N/A</td>
     * </tr>
     * <tr>
     * <td>&lt;ul><br>
     * &lt;li>item1<br>
     * &lt;li>item1<br>
     * &lt;/ul></td>
     * <td>item list</td>
     * <td>
     * <ul>
     * <li>item1
     * <li>item2
     * </ul>
     * </td>
     * </tr>
     * </table>
     * </p>
     * 
     * <p>
     * These tags may be nested.
     * </p>
     * 
     * @return component's description <code>String</code>
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the component's description. See {@link #getDescription()} for more
     * information on what the description is. This method will trigger a
     * {@link com.vaadin.terminal.Paintable.RepaintRequestEvent
     * RepaintRequestEvent}.
     * 
     * @param description
     *            the new description string for the component.
     */
    public void setDescription(String description) {
        this.description = description;
        requestRepaint();
    }

    /*
     * Gets the component's parent component. Don't add a JavaDoc comment here,
     * we use the default documentation from implemented interface.
     */
    public Component getParent() {
        return parent;
    }

    /*
     * Sets the parent component. Don't add a JavaDoc comment here, we use the
     * default documentation from implemented interface.
     */
    public void setParent(Component parent) {

        // If the parent is not changed, don't do anything
        if (parent == this.parent) {
            return;
        }

        if (parent != null && this.parent != null) {
            throw new IllegalStateException(getClass().getName()
                    + " already has a parent.");
        }

        // Send detach event if the component have been connected to a window
        if (getApplication() != null) {
            detach();
        }

        // Connect to new parent
        this.parent = parent;

        // Send attach event if connected to a window
        if (getApplication() != null) {
            attach();
        }
    }

    /**
     * Gets the error message for this component.
     * 
     * @return ErrorMessage containing the description of the error state of the
     *         component or null, if the component contains no errors. Extending
     *         classes should override this method if they support other error
     *         message types such as validation errors or buffering errors. The
     *         returned error message contains information about all the errors.
     */
    public ErrorMessage getErrorMessage() {
        return componentError;
    }

    /**
     * Gets the component's error message.
     * 
     * @link Terminal.ErrorMessage#ErrorMessage(String, int)
     * 
     * @return the component's error message.
     */
    public ErrorMessage getComponentError() {
        return componentError;
    }

    /**
     * Sets the component's error message. The message may contain certain XML
     * tags, for more information see
     * 
     * @link Component.ErrorMessage#ErrorMessage(String, int)
     * 
     * @param componentError
     *            the new <code>ErrorMessage</code> of the component.
     */
    public void setComponentError(ErrorMessage componentError) {
        this.componentError = componentError;
        fireComponentErrorEvent();
        requestRepaint();
    }

    /*
     * Tests if the component is in read-only mode. Don't add a JavaDoc comment
     * here, we use the default documentation from implemented interface.
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /*
     * Sets the component's read-only mode. Don't add a JavaDoc comment here, we
     * use the default documentation from implemented interface.
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        requestRepaint();
    }

    /*
     * Gets the parent window of the component. Don't add a JavaDoc comment
     * here, we use the default documentation from implemented interface.
     */
    public Window getWindow() {
        if (parent == null) {
            return null;
        } else {
            return parent.getWindow();
        }
    }

    /*
     * Notify the component that it's attached to a window. Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    public void attach() {
        requestRepaint();
        if (!visible) {
            /*
             * Bypass the repaint optimization in childRequestedRepaint method
             * when attaching. When reattaching (possibly moving) -> must
             * repaint
             */
            fireRequestRepaintEvent(null);
        }
        if (delayedFocus) {
            focus();
        }
    }

    /*
     * Detach the component from application. Don't add a JavaDoc comment here,
     * we use the default documentation from implemented interface.
     */
    public void detach() {
    }

    /**
     * Sets the focus for this component if the component is {@link Focusable}.
     */
    protected void focus() {
        if (this instanceof Focusable) {
            final Application app = getApplication();
            if (app != null) {
                getWindow().setFocusedComponent((Focusable) this);
                delayedFocus = false;
            } else {
                delayedFocus = true;
            }
        }
    }

    /*
     * Gets the parent application of the component. Don't add a JavaDoc comment
     * here, we use the default documentation from implemented interface.
     */
    public Application getApplication() {
        if (parent == null) {
            return null;
        } else {
            return parent.getApplication();
        }
    }

    /* Component painting */

    /* Documented in super interface */
    public void requestRepaintRequests() {
        repaintRequestListenersNotified = false;
    }

    /*
     * Paints the component into a UIDL stream. Don't add a JavaDoc comment
     * here, we use the default documentation from implemented interface.
     */
    public final void paint(PaintTarget target) throws PaintException {
        final String tag = target.getTag(this);
        if (!target.startTag(this, tag) || repaintRequestListenersNotified) {

            // Paint the contents of the component

            // Only paint content of visible components.
            if (isVisible()) {
                if (getHeight() >= 0
                        && (getHeightUnits() != UNITS_PERCENTAGE || ComponentSizeValidator
                                .parentCanDefineHeight(this))) {
                    target.addAttribute("height", "" + getCSSHeight());
                }

                if (getWidth() >= 0
                        && (getWidthUnits() != UNITS_PERCENTAGE || ComponentSizeValidator
                                .parentCanDefineWidth(this))) {
                    target.addAttribute("width", "" + getCSSWidth());
                }
                if (styles != null && styles.size() > 0) {
                    target.addAttribute("style", getStyle());
                }
                if (isReadOnly()) {
                    target.addAttribute("readonly", true);
                }

                if (isImmediate()) {
                    target.addAttribute("immediate", true);
                }
                if (!isEnabled()) {
                    target.addAttribute("disabled", true);
                }
                if (getCaption() != null) {
                    target.addAttribute("caption", getCaption());
                }
                if (getIcon() != null) {
                    target.addAttribute("icon", getIcon());
                }

                if (getDescription() != null && getDescription().length() > 0) {
                    target.addAttribute("description", getDescription());
                }

                if (eventIdentifiers != null) {
                    target.addAttribute("eventListeners",
                            eventIdentifiers.toArray());
                }

                paintContent(target);

                final ErrorMessage error = getErrorMessage();
                if (error != null) {
                    error.paint(target);
                }
            } else {
                target.addAttribute("invisible", true);
            }
        } else {

            // Contents have not changed, only cached presentation can be used
            target.addAttribute("cached", true);
        }
        target.endTag(tag);

        repaintRequestListenersNotified = false;
    }

    /**
     * Build CSS compatible string representation of height.
     * 
     * @return CSS height
     */
    private String getCSSHeight() {
        if (getHeightUnits() == UNITS_PIXELS) {
            return ((int) getHeight()) + UNIT_SYMBOLS[getHeightUnits()];
        } else {
            return getHeight() + UNIT_SYMBOLS[getHeightUnits()];
        }
    }

    /**
     * Build CSS compatible string representation of width.
     * 
     * @return CSS width
     */
    private String getCSSWidth() {
        if (getWidthUnits() == UNITS_PIXELS) {
            return ((int) getWidth()) + UNIT_SYMBOLS[getWidthUnits()];
        } else {
            return getWidth() + UNIT_SYMBOLS[getWidthUnits()];
        }
    }

    /**
     * Paints any needed component-specific things to the given UIDL stream. The
     * more general {@link #paint(PaintTarget)} method handles all general
     * attributes common to all components, and it calls this method to paint
     * any component-specific attributes to the UIDL stream.
     * 
     * @param target
     *            the target UIDL stream where the component should paint itself
     *            to
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void paintContent(PaintTarget target) throws PaintException {

    }

    /* Documentation copied from interface */
    public void requestRepaint() {

        // The effect of the repaint request is identical to case where a
        // child requests repaint
        childRequestedRepaint(null);
    }

    /* Documentation copied from interface */
    public void childRequestedRepaint(
            Collection<RepaintRequestListener> alreadyNotified) {
        // Invisible components (by flag in this particular component) do not
        // need repaints
        if (!visible) {
            return;
        }

        fireRequestRepaintEvent(alreadyNotified);
    }

    /**
     * Fires the repaint request event.
     * 
     * @param alreadyNotified
     */
    private void fireRequestRepaintEvent(
            Collection<RepaintRequestListener> alreadyNotified) {
        // Notify listeners only once
        if (!repaintRequestListenersNotified) {
            // Notify the listeners
            if (repaintRequestListeners != null
                    && !repaintRequestListeners.isEmpty()) {
                final Object[] listeners = repaintRequestListeners.toArray();
                final RepaintRequestEvent event = new RepaintRequestEvent(this);
                for (int i = 0; i < listeners.length; i++) {
                    if (alreadyNotified == null) {
                        alreadyNotified = new LinkedList<RepaintRequestListener>();
                    }
                    if (!alreadyNotified.contains(listeners[i])) {
                        ((RepaintRequestListener) listeners[i])
                                .repaintRequested(event);
                        alreadyNotified
                                .add((RepaintRequestListener) listeners[i]);
                        repaintRequestListenersNotified = true;
                    }
                }
            }

            // Notify the parent
            final Component parent = getParent();
            if (parent != null) {
                parent.childRequestedRepaint(alreadyNotified);
            }
        }
    }

    /* Documentation copied from interface */
    public void addListener(RepaintRequestListener listener) {
        if (repaintRequestListeners == null) {
            repaintRequestListeners = new LinkedList<RepaintRequestListener>();
        }
        if (!repaintRequestListeners.contains(listener)) {
            repaintRequestListeners.add(listener);
        }
    }

    /* Documentation copied from interface */
    public void removeListener(RepaintRequestListener listener) {
        if (repaintRequestListeners != null) {
            repaintRequestListeners.remove(listener);
            if (repaintRequestListeners.isEmpty()) {
                repaintRequestListeners = null;
            }
        }
    }

    /* Component variable changes */

    /*
     * Invoked when the value of a variable has changed. Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    public void changeVariables(Object source, Map<String, Object> variables) {

    }

    /* General event framework */

    private static final Method COMPONENT_EVENT_METHOD = ReflectTools
            .findMethod(Component.Listener.class, "componentEvent",
                    Component.Event.class);

    /**
     * <p>
     * Registers a new listener with the specified activation method to listen
     * events generated by this component. If the activation method does not
     * have any arguments the event object will not be passed to it when it's
     * called.
     * </p>
     * 
     * <p>
     * This method additionally informs the event-api to route events with the
     * given eventIdentifier to the components handleEvent function call.
     * </p>
     * 
     * <p>
     * For more information on the inheritable event mechanism see the
     * {@link com.vaadin.event com.vaadin.event package documentation}.
     * </p>
     * 
     * @param eventIdentifier
     *            the identifier of the event to listen for
     * @param eventType
     *            the type of the listened event. Events of this type or its
     *            subclasses activate the listener.
     * @param target
     *            the object instance who owns the activation method.
     * @param method
     *            the activation method.
     * 
     * @since 6.2
     */
    protected void addListener(String eventIdentifier, Class<?> eventType,
            Object target, Method method) {
        if (eventRouter == null) {
            eventRouter = new EventRouter();
        }
        if (eventIdentifiers == null) {
            eventIdentifiers = new HashSet<String>();
        }
        boolean needRepaint = !eventRouter.hasListeners(eventType);
        eventRouter.addListener(eventType, target, method);

        if (needRepaint) {
            eventIdentifiers.add(eventIdentifier);
            requestRepaint();
        }
    }

    /**
     * Removes all registered listeners matching the given parameters. Since
     * this method receives the event type and the listener object as
     * parameters, it will unregister all <code>object</code>'s methods that are
     * registered to listen to events of type <code>eventType</code> generated
     * by this component.
     * 
     * <p>
     * This method additionally informs the event-api to stop routing events
     * with the given eventIdentifier to the components handleEvent function
     * call.
     * </p>
     * 
     * <p>
     * For more information on the inheritable event mechanism see the
     * {@link com.vaadin.event com.vaadin.event package documentation}.
     * </p>
     * 
     * @param eventIdentifier
     *            the identifier of the event to stop listening for
     * @param eventType
     *            the exact event type the <code>object</code> listens to.
     * @param target
     *            the target object that has registered to listen to events of
     *            type <code>eventType</code> with one or more methods.
     * 
     * @since 6.2
     */
    protected void removeListener(String eventIdentifier, Class<?> eventType,
            Object target) {
        if (eventRouter != null) {
            eventRouter.removeListener(eventType, target);
            if (!eventRouter.hasListeners(eventType)) {
                eventIdentifiers.remove(eventIdentifier);
                requestRepaint();
            }
        }
    }

    /**
     * <p>
     * Registers a new listener with the specified activation method to listen
     * events generated by this component. If the activation method does not
     * have any arguments the event object will not be passed to it when it's
     * called.
     * </p>
     * 
     * <p>
     * For more information on the inheritable event mechanism see the
     * {@link com.vaadin.event com.vaadin.event package documentation}.
     * </p>
     * 
     * @param eventType
     *            the type of the listened event. Events of this type or its
     *            subclasses activate the listener.
     * @param target
     *            the object instance who owns the activation method.
     * @param method
     *            the activation method.
     */
    public void addListener(Class eventType, Object target, Method method) {
        if (eventRouter == null) {
            eventRouter = new EventRouter();
        }
        eventRouter.addListener(eventType, target, method);
    }

    /**
     * <p>
     * Convenience method for registering a new listener with the specified
     * activation method to listen events generated by this component. If the
     * activation method does not have any arguments the event object will not
     * be passed to it when it's called.
     * </p>
     * 
     * <p>
     * This version of <code>addListener</code> gets the name of the activation
     * method as a parameter. The actual method is reflected from
     * <code>object</code>, and unless exactly one match is found,
     * <code>java.lang.IllegalArgumentException</code> is thrown.
     * </p>
     * 
     * <p>
     * For more information on the inheritable event mechanism see the
     * {@link com.vaadin.event com.vaadin.event package documentation}.
     * </p>
     * 
     * <p>
     * Note: Using this method is discouraged because it cannot be checked
     * during compilation. Use {@link #addListener(Class, Object, Method)} or
     * {@link #addListener(com.vaadin.ui.Component.Listener)} instead.
     * </p>
     * 
     * @param eventType
     *            the type of the listened event. Events of this type or its
     *            subclasses activate the listener.
     * @param target
     *            the object instance who owns the activation method.
     * @param methodName
     *            the name of the activation method.
     */
    public void addListener(Class eventType, Object target, String methodName) {
        if (eventRouter == null) {
            eventRouter = new EventRouter();
        }
        eventRouter.addListener(eventType, target, methodName);
    }

    /**
     * Removes all registered listeners matching the given parameters. Since
     * this method receives the event type and the listener object as
     * parameters, it will unregister all <code>object</code>'s methods that are
     * registered to listen to events of type <code>eventType</code> generated
     * by this component.
     * 
     * <p>
     * For more information on the inheritable event mechanism see the
     * {@link com.vaadin.event com.vaadin.event package documentation}.
     * </p>
     * 
     * @param eventType
     *            the exact event type the <code>object</code> listens to.
     * @param target
     *            the target object that has registered to listen to events of
     *            type <code>eventType</code> with one or more methods.
     */
    public void removeListener(Class eventType, Object target) {
        if (eventRouter != null) {
            eventRouter.removeListener(eventType, target);
        }
    }

    /**
     * Removes one registered listener method. The given method owned by the
     * given object will no longer be called when the specified events are
     * generated by this component.
     * 
     * <p>
     * For more information on the inheritable event mechanism see the
     * {@link com.vaadin.event com.vaadin.event package documentation}.
     * </p>
     * 
     * @param eventType
     *            the exact event type the <code>object</code> listens to.
     * @param target
     *            target object that has registered to listen to events of type
     *            <code>eventType</code> with one or more methods.
     * @param method
     *            the method owned by <code>target</code> that's registered to
     *            listen to events of type <code>eventType</code>.
     */
    public void removeListener(Class eventType, Object target, Method method) {
        if (eventRouter != null) {
            eventRouter.removeListener(eventType, target, method);
        }
    }

    /**
     * <p>
     * Removes one registered listener method. The given method owned by the
     * given object will no longer be called when the specified events are
     * generated by this component.
     * </p>
     * 
     * <p>
     * This version of <code>removeListener</code> gets the name of the
     * activation method as a parameter. The actual method is reflected from
     * <code>target</code>, and unless exactly one match is found,
     * <code>java.lang.IllegalArgumentException</code> is thrown.
     * </p>
     * 
     * <p>
     * For more information on the inheritable event mechanism see the
     * {@link com.vaadin.event com.vaadin.event package documentation}.
     * </p>
     * 
     * @param eventType
     *            the exact event type the <code>object</code> listens to.
     * @param target
     *            the target object that has registered to listen to events of
     *            type <code>eventType</code> with one or more methods.
     * @param methodName
     *            the name of the method owned by <code>target</code> that's
     *            registered to listen to events of type <code>eventType</code>.
     */
    public void removeListener(Class eventType, Object target, String methodName) {
        if (eventRouter != null) {
            eventRouter.removeListener(eventType, target, methodName);
        }
    }

    /**
     * Sends the event to all listeners.
     * 
     * @param event
     *            the Event to be sent to all listeners.
     */
    protected void fireEvent(Component.Event event) {
        if (eventRouter != null) {
            eventRouter.fireEvent(event);
        }

    }

    /* Component event framework */

    /*
     * Registers a new listener to listen events generated by this component.
     * Don't add a JavaDoc comment here, we use the default documentation from
     * implemented interface.
     */
    public void addListener(Component.Listener listener) {
        addListener(Component.Event.class, listener, COMPONENT_EVENT_METHOD);
    }

    /*
     * Removes a previously registered listener from this component. Don't add a
     * JavaDoc comment here, we use the default documentation from implemented
     * interface.
     */
    public void removeListener(Component.Listener listener) {
        removeListener(Component.Event.class, listener, COMPONENT_EVENT_METHOD);
    }

    /**
     * Emits the component event. It is transmitted to all registered listeners
     * interested in such events.
     */
    protected void fireComponentEvent() {
        fireEvent(new Component.Event(this));
    }

    /**
     * Emits the component error event. It is transmitted to all registered
     * listeners interested in such events.
     */
    protected void fireComponentErrorEvent() {
        fireEvent(new Component.ErrorEvent(getComponentError(), this));
    }

    /**
     * Sets the data object, that can be used for any application specific data.
     * The component does not use or modify this data.
     * 
     * @param data
     *            the Application specific data.
     * @since 3.1
     */
    public void setData(Object data) {
        applicationData = data;
    }

    /**
     * Gets the application specific data. See {@link #setData(Object)}.
     * 
     * @return the Application specific data set with setData function.
     * @since 3.1
     */
    public Object getData() {
        return applicationData;
    }

    /* Sizeable and other size related methods */

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.Sizeable#getHeight()
     */
    public float getHeight() {
        return height;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.Sizeable#getHeightUnits()
     */
    public int getHeightUnits() {
        return heightUnit;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.Sizeable#getWidth()
     */
    public float getWidth() {
        return width;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.Sizeable#getWidthUnits()
     */
    public int getWidthUnits() {
        return widthUnit;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.Sizeable#setHeight(float)
     */
    @Deprecated
    public void setHeight(float height) {
        setHeight(height, getHeightUnits());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.Sizeable#setHeightUnits(int)
     */
    @Deprecated
    public void setHeightUnits(int unit) {
        setHeight(getHeight(), unit);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.Sizeable#setHeight(float, int)
     */
    public void setHeight(float height, int unit) {
        this.height = height;
        heightUnit = unit;
        requestRepaint();
        // ComponentSizeValidator.setHeightLocation(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.Sizeable#setSizeFull()
     */
    public void setSizeFull() {
        setWidth(100, UNITS_PERCENTAGE);
        setHeight(100, UNITS_PERCENTAGE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.Sizeable#setSizeUndefined()
     */
    public void setSizeUndefined() {
        setWidth(-1, UNITS_PIXELS);
        setHeight(-1, UNITS_PIXELS);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.Sizeable#setWidth(float)
     */
    @Deprecated
    public void setWidth(float width) {
        setWidth(width, getWidthUnits());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.Sizeable#setWidthUnits(int)
     */
    @Deprecated
    public void setWidthUnits(int unit) {
        setWidth(getWidth(), unit);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.Sizeable#setWidth(float, int)
     */
    public void setWidth(float width, int unit) {
        this.width = width;
        widthUnit = unit;
        requestRepaint();
        // ComponentSizeValidator.setWidthLocation(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.Sizeable#setWidth(java.lang.String)
     */
    public void setWidth(String width) {
        float[] p = parseStringSize(width);
        setWidth(p[0], (int) p[1]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.Sizeable#setHeight(java.lang.String)
     */
    public void setHeight(String height) {
        float[] p = parseStringSize(height);
        setHeight(p[0], (int) p[1]);
    }

    /*
     * Returns array with size in index 0 unit in index 1. Null or empty string
     * will produce {-1,UNITS_PIXELS}
     */
    private static float[] parseStringSize(String s) {
        float[] values = { -1, UNITS_PIXELS };
        if (s == null) {
            return values;
        }
        s = s.trim();
        if ("".equals(s)) {
            return values;
        }

        Matcher matcher = sizePattern.matcher(s);
        if (matcher.find()) {
            values[0] = Float.parseFloat(matcher.group(1));
            if (values[0] < 0) {
                values[0] = -1;
            } else {
                String unit = matcher.group(3);
                if (unit == null) {
                    values[1] = UNITS_PIXELS;
                } else if (unit.equals("px")) {
                    values[1] = UNITS_PIXELS;
                } else if (unit.equals("%")) {
                    values[1] = UNITS_PERCENTAGE;
                } else if (unit.equals("em")) {
                    values[1] = UNITS_EM;
                } else if (unit.equals("ex")) {
                    values[1] = UNITS_EX;
                } else if (unit.equals("in")) {
                    values[1] = UNITS_INCH;
                } else if (unit.equals("cm")) {
                    values[1] = UNITS_CM;
                } else if (unit.equals("mm")) {
                    values[1] = UNITS_MM;
                } else if (unit.equals("pt")) {
                    values[1] = UNITS_POINTS;
                } else if (unit.equals("pc")) {
                    values[1] = UNITS_PICAS;
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid size argument: \"" + s
                    + "\" (should match " + sizePattern.pattern() + ")");
        }
        return values;
    }

    public interface ComponentErrorEvent extends Terminal.ErrorEvent {
    }

    public interface ComponentErrorHandler extends Serializable {
        /**
         * Handle the component error
         * 
         * @param event
         * @return True if the error has been handled False, otherwise
         */
        public boolean handleComponentError(ComponentErrorEvent event);
    }

    /**
     * Gets the error handler for the component.
     * 
     * The error handler is dispatched whenever there is an error processing the
     * data coming from the client.
     * 
     * @return
     */
    public ComponentErrorHandler getErrorHandler() {
        return errorHandler;
    }

    /**
     * Sets the error handler for the component.
     * 
     * The error handler is dispatched whenever there is an error processing the
     * data coming from the client.
     * 
     * If the error handler is not set, the application error handler is used to
     * handle the exception.
     * 
     * @param errorHandler
     *            AbstractField specific error handler
     */
    public void setErrorHandler(ComponentErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
     * Handle the component error event.
     * 
     * @param error
     *            Error event to handle
     * @return True if the error has been handled False, otherwise. If the error
     *         haven't been handled by this component, it will be handled in the
     *         application error handler.
     */
    public boolean handleError(ComponentErrorEvent error) {
        if (errorHandler != null) {
            return errorHandler.handleComponentError(error);
        }
        return false;

    }

}
