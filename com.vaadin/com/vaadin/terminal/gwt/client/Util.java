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

package com.vaadin.terminal.gwt.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.RenderInformation.FloatSize;

public class Util {

    /**
     * Helper method for debugging purposes.
     * 
     * Stops execution on firefox browsers on a breakpoint.
     * 
     */
    public static native void browserDebugger()
    /*-{
        if($wnd.console)
            debugger;
    }-*/;

    /**
     * 
     * Returns the topmost element of from given coordinates.
     * 
     * TODO fix crossplat issues clientX vs pageX. See quircksmode. Not critical
     * for vaadin as we scroll div istead of page.
     * 
     * @param x
     * @param y
     * @return the element at given coordinates
     */
    public static native Element getElementFromPoint(int clientX, int clientY)
    /*-{
        var el = $wnd.document.elementFromPoint(clientX, clientY);
        if(el != null && el.nodeType == 3) {
            el = el.parentNode;
        }
        return el;
    }-*/;

    private static final int LAZY_SIZE_CHANGE_TIMEOUT = 400;
    private static Set<Paintable> latelyChangedWidgets = new HashSet<Paintable>();

    private static Timer lazySizeChangeTimer = new Timer() {
        private boolean lazySizeChangeTimerScheduled = false;

        @Override
        public void run() {
            componentSizeUpdated(latelyChangedWidgets);
            latelyChangedWidgets.clear();
            lazySizeChangeTimerScheduled = false;
        }

        @Override
        public void schedule(int delayMillis) {
            if (lazySizeChangeTimerScheduled) {
                cancel();
            } else {
                lazySizeChangeTimerScheduled = true;
            }
            super.schedule(delayMillis);
        }
    };

    /**
     * This helper method can be called if components size have been changed
     * outside rendering phase. It notifies components parent about the size
     * change so it can react.
     * 
     * When using this method, developer should consider if size changes could
     * be notified lazily. If lazy flag is true, method will save widget and
     * wait for a moment until it notifies parents in chunks. This may vastly
     * optimize layout in various situation. Example: if component have a lot of
     * images their onload events may fire "layout phase" many times in a short
     * period.
     * 
     * @param widget
     * @param lazy
     *            run componentSizeUpdated lazyly
     */
    public static void notifyParentOfSizeChange(Paintable widget, boolean lazy) {
        if (lazy) {
            latelyChangedWidgets.add(widget);
            lazySizeChangeTimer.schedule(LAZY_SIZE_CHANGE_TIMEOUT);
        } else {
            Set<Paintable> widgets = new HashSet<Paintable>();
            widgets.add(widget);
            Util.componentSizeUpdated(widgets);
        }
    }

    /**
     * Called when the size of one or more widgets have changed during
     * rendering. Finds parent container and notifies them of the size change.
     * 
     * @param widgets
     */
    public static void componentSizeUpdated(Set<Paintable> widgets) {
        if (widgets.isEmpty()) {
            return;
        }

        Map<Container, Set<Paintable>> childWidgets = new HashMap<Container, Set<Paintable>>();

        for (Paintable widget : widgets) {
            // ApplicationConnection.getConsole().log(
            // "Widget " + Util.getSimpleName(widget) + " size updated");
            Widget parent = ((Widget) widget).getParent();
            while (parent != null && !(parent instanceof Container)) {
                parent = parent.getParent();
            }
            if (parent != null) {
                Set<Paintable> set = childWidgets.get(parent);
                if (set == null) {
                    set = new HashSet<Paintable>();
                    childWidgets.put((Container) parent, set);
                }
                set.add(widget);
            }
        }

        Set<Paintable> parentChanges = new HashSet<Paintable>();
        for (Container parent : childWidgets.keySet()) {
            if (!parent.requestLayout(childWidgets.get(parent))) {
                parentChanges.add(parent);
            }
        }

        componentSizeUpdated(parentChanges);
    }

    public static float parseRelativeSize(String size) {
        if (size == null || !size.endsWith("%")) {
            return -1;
        }

        try {
            return Float.parseFloat(size.substring(0, size.length() - 1));
        } catch (Exception e) {
            VConsole.log("Unable to parse relative size");
            return -1;
        }
    }

    /**
     * Returns closest parent Widget in hierarchy that implements Container
     * interface
     * 
     * @param component
     * @return closest parent Container
     */
    public static Container getLayout(Widget component) {
        Widget parent = component.getParent();
        while (parent != null && !(parent instanceof Container)) {
            parent = parent.getParent();
        }
        if (parent != null) {
            assert ((Container) parent).hasChildComponent(component);

            return (Container) parent;
        }
        return null;
    }

    /**
     * Detects if current browser is IE.
     * 
     * @deprecated use BrowserInfo class instead
     * 
     * @return true if IE
     */
    @Deprecated
    public static boolean isIE() {
        return BrowserInfo.get().isIE();
    }

    /**
     * Detects if current browser is IE6.
     * 
     * @deprecated use BrowserInfo class instead
     * 
     * @return true if IE6
     */
    @Deprecated
    public static boolean isIE6() {
        return BrowserInfo.get().isIE6();
    }

    /**
     * @deprecated use BrowserInfo class instead
     * @return
     */
    @Deprecated
    public static boolean isIE7() {
        return BrowserInfo.get().isIE7();
    }

    /**
     * @deprecated use BrowserInfo class instead
     * @return
     */
    @Deprecated
    public static boolean isFF2() {
        return BrowserInfo.get().isFF2();
    }

    private static final Element escapeHtmlHelper = DOM.createDiv();

    /**
     * Converts html entities to text.
     * 
     * @param html
     * @return escaped string presentation of given html
     */
    public static String escapeHTML(String html) {
        DOM.setInnerText(escapeHtmlHelper, html);
        return DOM.getInnerHTML(escapeHtmlHelper);
    }

    /**
     * Adds transparent PNG fix to image element; only use for IE6.
     * 
     * @param el
     *            IMG element
     * @param blankImageUrl
     *            URL to transparent one-pixel gif
     */
    public native static void addPngFix(Element el, String blankImageUrl)
    /*-{
        el.attachEvent("onload", function() {
            var src = el.src;
            if (src.indexOf(".png") < 1) return;
            var w = el.width || 16; 
            var h = el.height || 16;
            if(h==30 || w==28) {
                setTimeout(function(){
                    el.style.height = el.height + "px";
                    el.style.width = el.width + "px";
                    el.src = blankImageUrl;
                },10);
            } else {
                el.src = blankImageUrl;
                el.style.height = h + "px";
                el.style.width = w + "px";
            }
            el.style.padding = "0";
            el.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+src+"', sizingMethod='crop')";  
        },false);
    }-*/;

    /**
     * Clones given element as in JavaScript.
     * 
     * Deprecate this if there appears similar method into GWT someday.
     * 
     * @param element
     * @param deep
     *            clone child tree also
     * @return
     */
    public static native Element cloneNode(Element element, boolean deep)
    /*-{
        return element.cloneNode(deep);
    }-*/;

    public static int measureHorizontalPaddingAndBorder(Element element,
            int paddingGuess) {
        String originalWidth = DOM.getStyleAttribute(element, "width");
        String originalOverflow = "";
        if (BrowserInfo.get().isIE6()) {
            originalOverflow = DOM.getStyleAttribute(element, "overflow");
            DOM.setStyleAttribute(element, "overflow", "hidden");
        }
        int originalOffsetWidth = element.getOffsetWidth();
        int widthGuess = (originalOffsetWidth - paddingGuess);
        if (widthGuess < 1) {
            widthGuess = 1;
        }
        DOM.setStyleAttribute(element, "width", widthGuess + "px");
        int padding = element.getOffsetWidth() - widthGuess;

        DOM.setStyleAttribute(element, "width", originalWidth);
        if (BrowserInfo.get().isIE6()) {
            DOM.setStyleAttribute(element, "overflow", originalOverflow);
        }
        return padding;
    }

    public static int measureVerticalPaddingAndBorder(Element element,
            int paddingGuess) {
        String originalHeight = DOM.getStyleAttribute(element, "height");
        int originalOffsetHeight = element.getOffsetHeight();
        int widthGuess = (originalOffsetHeight - paddingGuess);
        if (widthGuess < 1) {
            widthGuess = 1;
        }
        DOM.setStyleAttribute(element, "height", widthGuess + "px");
        int padding = element.getOffsetHeight() - widthGuess;

        DOM.setStyleAttribute(element, "height", originalHeight);
        return padding;
    }

    public static int measureHorizontalBorder(Element element) {
        int borders;
        int cw = element.getClientWidth();
        if (BrowserInfo.get().isIE()) {
            String width = element.getStyle().getProperty("width");
            String height = element.getStyle().getProperty("height");

            int offsetWidth = element.getOffsetWidth();
            int offsetHeight = element.getOffsetHeight();
            if (BrowserInfo.get().isIE6() || BrowserInfo.get().isIE8()) {
                if (offsetHeight < 1) {
                    offsetHeight = 1;
                }
                if (offsetWidth < 1) {
                    offsetWidth = 10;
                }
                element.getStyle().setPropertyPx("height", offsetHeight);
            }
            element.getStyle().setPropertyPx("width", offsetWidth);

            borders = element.getOffsetWidth() - element.getClientWidth();

            element.getStyle().setProperty("width", width);
            if (BrowserInfo.get().isIE6() || BrowserInfo.get().isIE8()) {
                element.getStyle().setProperty("height", height);
            }
        } else {
            borders = element.getOffsetWidth()
                    - element.getPropertyInt("clientWidth");
        }
        assert borders >= 0;

        return borders;
    }

    public static int measureVerticalBorder(Element element) {
        int borders;
        if (BrowserInfo.get().isIE()) {
            String width = element.getStyle().getProperty("width");
            String height = element.getStyle().getProperty("height");

            int offsetWidth = element.getOffsetWidth();
            int offsetHeight = element.getOffsetHeight();
            // if (BrowserInfo.get().isIE6()) {
            if (offsetHeight < 1) {
                offsetHeight = 1;
            }
            if (offsetWidth < 1) {
                offsetWidth = 10;
            }
            element.getStyle().setPropertyPx("width", offsetWidth);
            // }

            element.getStyle().setPropertyPx("height", offsetHeight);

            borders = element.getOffsetHeight()
                    - element.getPropertyInt("clientHeight");

            element.getStyle().setProperty("height", height);
            // if (BrowserInfo.get().isIE6()) {
            element.getStyle().setProperty("width", width);
            // }
        } else {
            borders = element.getOffsetHeight()
                    - element.getPropertyInt("clientHeight");
        }
        assert borders >= 0;

        return borders;
    }

    public static int measureMarginLeft(Element element) {
        return element.getAbsoluteLeft()
                - element.getParentElement().getAbsoluteLeft();
    }

    public static int setHeightExcludingPaddingAndBorder(Widget widget,
            String height, int paddingBorderGuess) {
        if (height.equals("")) {
            setHeight(widget, "");
            return paddingBorderGuess;
        } else if (height.endsWith("px")) {
            int pixelHeight = Integer.parseInt(height.substring(0,
                    height.length() - 2));
            return setHeightExcludingPaddingAndBorder(widget.getElement(),
                    pixelHeight, paddingBorderGuess, false);
        } else {
            // Set the height in unknown units
            setHeight(widget, height);
            // Use the offsetWidth
            return setHeightExcludingPaddingAndBorder(widget.getElement(),
                    widget.getOffsetHeight(), paddingBorderGuess, true);
        }
    }

    private static void setWidth(Widget widget, String width) {
        DOM.setStyleAttribute(widget.getElement(), "width", width);
    }

    private static void setHeight(Widget widget, String height) {
        DOM.setStyleAttribute(widget.getElement(), "height", height);
    }

    public static int setWidthExcludingPaddingAndBorder(Widget widget,
            String width, int paddingBorderGuess) {
        if (width.equals("")) {
            setWidth(widget, "");
            return paddingBorderGuess;
        } else if (width.endsWith("px")) {
            int pixelWidth = Integer.parseInt(width.substring(0,
                    width.length() - 2));
            return setWidthExcludingPaddingAndBorder(widget.getElement(),
                    pixelWidth, paddingBorderGuess, false);
        } else {
            setWidth(widget, width);
            return setWidthExcludingPaddingAndBorder(widget.getElement(),
                    widget.getOffsetWidth(), paddingBorderGuess, true);
        }
    }

    public static int setWidthExcludingPaddingAndBorder(Element element,
            int requestedWidth, int horizontalPaddingBorderGuess,
            boolean requestedWidthIncludesPaddingBorder) {

        int widthGuess = requestedWidth - horizontalPaddingBorderGuess;
        if (widthGuess < 0) {
            widthGuess = 0;
        }

        DOM.setStyleAttribute(element, "width", widthGuess + "px");
        int captionOffsetWidth = DOM.getElementPropertyInt(element,
                "offsetWidth");

        int actualPadding = captionOffsetWidth - widthGuess;

        if (requestedWidthIncludesPaddingBorder) {
            actualPadding += actualPadding;
        }

        if (actualPadding != horizontalPaddingBorderGuess) {
            int w = requestedWidth - actualPadding;
            if (w < 0) {
                // Cannot set negative width even if we would want to
                w = 0;
            }
            DOM.setStyleAttribute(element, "width", w + "px");

        }

        return actualPadding;

    }

    public static int setHeightExcludingPaddingAndBorder(Element element,
            int requestedHeight, int verticalPaddingBorderGuess,
            boolean requestedHeightIncludesPaddingBorder) {

        int heightGuess = requestedHeight - verticalPaddingBorderGuess;
        if (heightGuess < 0) {
            heightGuess = 0;
        }

        DOM.setStyleAttribute(element, "height", heightGuess + "px");
        int captionOffsetHeight = DOM.getElementPropertyInt(element,
                "offsetHeight");

        int actualPadding = captionOffsetHeight - heightGuess;

        if (requestedHeightIncludesPaddingBorder) {
            actualPadding += actualPadding;
        }

        if (actualPadding != verticalPaddingBorderGuess) {
            int h = requestedHeight - actualPadding;
            if (h < 0) {
                // Cannot set negative height even if we would want to
                h = 0;
            }
            DOM.setStyleAttribute(element, "height", h + "px");

        }

        return actualPadding;

    }

    public static String getSimpleName(Object widget) {
        if (widget == null) {
            return "(null)";
        }

        String name = widget.getClass().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    public static void setFloat(Element element, String value) {
        if (BrowserInfo.get().isIE()) {
            DOM.setStyleAttribute(element, "styleFloat", value);
        } else {
            DOM.setStyleAttribute(element, "cssFloat", value);
        }
    }

    private static int detectedScrollbarSize = -1;

    public static int getNativeScrollbarSize() {
        if (detectedScrollbarSize < 0) {
            Element scroller = DOM.createDiv();
            scroller.getStyle().setProperty("width", "50px");
            scroller.getStyle().setProperty("height", "50px");
            scroller.getStyle().setProperty("overflow", "scroll");
            scroller.getStyle().setProperty("position", "absolute");
            scroller.getStyle().setProperty("marginLeft", "-5000px");
            RootPanel.getBodyElement().appendChild(scroller);
            detectedScrollbarSize = scroller.getOffsetWidth()
                    - scroller.getPropertyInt("clientWidth");

            // Asserting the detected value causes a problem
            // at least in Hosted Mode Browser/Linux/GWT-1.5.3, so
            // use a default if detection fails.
            if (detectedScrollbarSize == 0) {
                detectedScrollbarSize = 20;
            }

            RootPanel.getBodyElement().removeChild(scroller);

        }
        return detectedScrollbarSize;
    }

    /**
     * Run workaround for webkits overflow auto issue.
     * 
     * See: our bug #2138 and https://bugs.webkit.org/show_bug.cgi?id=21462
     * 
     * @param elem
     *            with overflow auto
     */
    public static void runWebkitOverflowAutoFix(final Element elem) {
        // Add max version if fix lands sometime to Webkit
        if (BrowserInfo.get().getWebkitVersion() > 0) {
            final String originalOverflow = elem.getStyle().getProperty(
                    "overflow");
            if ("hidden".equals(originalOverflow)) {
                return;
            }

            // check the scrolltop value before hiding the element
            final int scrolltop = elem.getScrollTop();
            elem.getStyle().setProperty("overflow", "hidden");

            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    // Dough, Safari scroll auto means actually just a moped
                    elem.getStyle().setProperty("overflow", originalOverflow);

                    if (scrolltop > 0 || elem.getScrollTop() > 0) {
                        int scrollvalue = scrolltop;
                        if (scrolltop == 0) {
                            // mysterious are the ways of webkits scrollbar
                            // handling. In some cases webkit reports bad (0)
                            // scrolltop before hiding the element temporary,
                            // sometimes after.
                            scrollvalue = elem.getScrollTop();
                        }
                        // fix another bug where scrollbar remains in wrong
                        // position
                        elem.setScrollTop(scrollvalue - 1);
                        elem.setScrollTop(scrollvalue);
                    }
                }
            });
        }

    }

    /**
     * Parses the UIDL parameter and fetches the relative size of the component.
     * If a dimension is not specified as relative it will return -1. If the
     * UIDL does not contain width or height specifications this will return
     * null.
     * 
     * @param uidl
     * @return
     */
    public static FloatSize parseRelativeSize(UIDL uidl) {
        boolean hasAttribute = false;
        String w = "";
        String h = "";
        if (uidl.hasAttribute("width")) {
            hasAttribute = true;
            w = uidl.getStringAttribute("width");
        }
        if (uidl.hasAttribute("height")) {
            hasAttribute = true;
            h = uidl.getStringAttribute("height");
        }

        if (!hasAttribute) {
            return null;
        }

        float relativeWidth = Util.parseRelativeSize(w);
        float relativeHeight = Util.parseRelativeSize(h);

        FloatSize relativeSize = new FloatSize(relativeWidth, relativeHeight);
        return relativeSize;

    }

    public static boolean isCached(UIDL uidl) {
        return uidl.getBooleanAttribute("cached");
    }

    public static void alert(String string) {
        if (true) {
            Window.alert(string);
        }
    }

    public static boolean equals(Object a, Object b) {
        if (a == null) {
            return b == null;
        }

        return a.equals(b);
    }

    public static void updateRelativeChildrenAndSendSizeUpdateEvent(
            ApplicationConnection client, HasWidgets container) {
        updateRelativeChildrenAndSendSizeUpdateEvent(client, container,
                (Paintable) container);
    }

    public static void updateRelativeChildrenAndSendSizeUpdateEvent(
            ApplicationConnection client, HasWidgets container, Paintable widget) {
        /*
         * Relative sized children must be updated first so the component has
         * the correct outer dimensions when signaling a size change to the
         * parent.
         */
        Iterator<Widget> childIterator = container.iterator();
        while (childIterator.hasNext()) {
            Widget w = childIterator.next();
            client.handleComponentRelativeSize(w);
        }

        HashSet<Paintable> widgets = new HashSet<Paintable>();
        widgets.add(widget);
        Util.componentSizeUpdated(widgets);
    }

    public static native int getRequiredWidth(
            com.google.gwt.dom.client.Element element)
    /*-{
        if (element.getBoundingClientRect) {
          var rect = element.getBoundingClientRect();
          return Math.ceil(rect.right - rect.left);
        } else {
          return element.offsetWidth;
        }
    }-*/;

    public static native int getRequiredHeight(
            com.google.gwt.dom.client.Element element)
    /*-{
        var height;
        if (element.getBoundingClientRect != null) {
          var rect = element.getBoundingClientRect();
          height = Math.ceil(rect.bottom - rect.top);
        } else {
          height = element.offsetHeight;
        }
        return height;
    }-*/;

    public static int getRequiredWidth(Widget widget) {
        return getRequiredWidth(widget.getElement());
    }

    public static int getRequiredHeight(Widget widget) {
        return getRequiredHeight(widget.getElement());
    }

    /**
     * Detects what is currently the overflow style attribute in given element.
     * 
     * @param pe
     *            the element to detect
     * @return true if auto or scroll
     */
    public static boolean mayHaveScrollBars(com.google.gwt.dom.client.Element pe) {
        String overflow = getComputedStyle(pe, "overflow");
        if (overflow != null) {
            if (overflow.equals("auto") || overflow.equals("scroll")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * A simple helper method to detect "computed style" (aka style sheets +
     * element styles). Values returned differ a lot depending on browsers.
     * Always be very careful when using this.
     * 
     * @param el
     *            the element from which the style property is detected
     * @param p
     *            the property to detect
     * @return String value of style property
     */
    private static native String getComputedStyle(
            com.google.gwt.dom.client.Element el, String p)
    /*-{
        try {
        
        if (el.currentStyle) {
            // IE
            return el.currentStyle[p];
        } else if (window.getComputedStyle) {
            // Sa, FF, Opera
            var view = el.ownerDocument.defaultView;
            return view.getComputedStyle(el,null).getPropertyValue(p);
        } else {
            // fall back for non IE, Sa, FF, Opera
            return "";
        }
        } catch (e) {
            return "";
        }

     }-*/;

    /**
     * IE7 sometimes "forgets" to render content. This function runs a hack to
     * workaround the bug if needed. This happens easily in framset. See #3295.
     */
    public static void runIE7ZeroSizedBodyFix() {
        if (BrowserInfo.get().isIE7()) {
            int offsetWidth = RootPanel.getBodyElement().getOffsetWidth();
            if (offsetWidth == 0) {
                shakeBodyElement();
            }
        }
    }

    /**
     * Does some very small adjustments to body element. We need this just to
     * overcome some IE bugs.
     */
    public static void shakeBodyElement() {
        final DivElement shaker = Document.get().createDivElement();
        RootPanel.getBodyElement().insertBefore(shaker,
                RootPanel.getBodyElement().getFirstChildElement());
        shaker.getStyle().setPropertyPx("height", 0);
        shaker.setInnerHTML("&nbsp;");
        RootPanel.getBodyElement().removeChild(shaker);

    }

    /**
     * Locates the child component of <literal>parent</literal> which contains
     * the element <literal>element</literal>. The child component is also
     * returned if "element" is part of its caption. If
     * <literal>element</literal> is not part of any child component, null is
     * returned.
     * 
     * @param client
     *            A reference to ApplicationConnection
     * @param parent
     *            The widget that contains <literal>element</literal>.
     * @param element
     *            An element that is a sub element of the parent
     * @return The Paintable which the element is a part of. Null if the element
     *         does not belong to a child.
     */
    public static Paintable getChildPaintableForElement(
            ApplicationConnection client, Container parent, Element element) {
        Element rootElement = ((Widget) parent).getElement();
        while (element != null && element != rootElement) {
            Paintable paintable = client.getPaintable(element);
            if (paintable == null) {
                String ownerPid = VCaption.getCaptionOwnerPid(element);
                if (ownerPid != null) {
                    paintable = client.getPaintable(ownerPid);
                }
            }

            if (paintable != null) {
                try {
                    if (parent.hasChildComponent((Widget) paintable)) {
                        return paintable;
                    }
                } catch (ClassCastException e) {
                    // We assume everything is a widget however there is no need
                    // to crash everything if there is a paintable that is not.
                }
            }

            element = (Element) element.getParentElement();
        }

        return null;
    }

    /**
     * Will (attempt) to focus the given DOM Element.
     * 
     * @param el
     *            the element to focus
     */
    public static native void focus(Element el)
    /*-{
        try {
            el.focus();
        } catch (e) {

        }
    }-*/;

    /**
     * Helper method to find first instance of given Widget type found by
     * traversing DOM upwards from given element.
     * 
     * @param element
     *            the element where to start seeking of Widget
     * @param class1
     *            the Widget type to seek for
     */
    public static <T> T findWidget(Element element,
            Class<? extends Widget> class1) {
        if (element != null) {
            /* First seek for the first EventListener (~Widget) from dom */
            EventListener eventListener = null;
            while (eventListener == null && element != null) {
                eventListener = Event.getEventListener(element);
                if (eventListener == null) {
                    element = (Element) element.getParentElement();
                }
            }
            if (eventListener != null) {
                /*
                 * Then find the first widget of type class1 from widget
                 * hierarchy
                 */
                Widget w = (Widget) eventListener;
                while (w != null) {
                    if (class1 == null || w.getClass() == class1) {
                        return (T) w;
                    }
                    w = w.getParent();
                }
            }
        }
        return null;
    }

}
