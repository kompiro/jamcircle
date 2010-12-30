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

package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.StyleConstants;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.VConsole;
import com.vaadin.terminal.gwt.client.ValueMap;

public class VCssLayout extends SimplePanel implements Paintable, Container {
    public static final String TAGNAME = "csslayout";
    public static final String CLASSNAME = "v-" + TAGNAME;
    public static final String CLICK_EVENT_IDENTIFIER = "click";

    private FlowPane panel = new FlowPane();

    private Element margin = DOM.createDiv();

    private LayoutClickEventHandler clickEventHandler = new LayoutClickEventHandler(
            this, CLICK_EVENT_IDENTIFIER) {

        @Override
        protected Paintable getChildComponent(Element element) {
            return panel.getComponent(element);
        }

        @Override
        protected <H extends EventHandler> HandlerRegistration registerHandler(
                H handler, Type<H> type) {
            return addDomHandler(handler, type);
        }
    };

    private boolean hasHeight;
    private boolean hasWidth;
    private boolean rendering;

    public VCssLayout() {
        super();
        getElement().appendChild(margin);
        setStyleName(CLASSNAME);
        margin.setClassName(CLASSNAME + "-margin");
        setWidget(panel);
    }

    @Override
    protected Element getContainerElement() {
        return margin;
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        // panel.setWidth(width);
        hasWidth = width != null && !width.equals("");
        if (!rendering) {
            panel.updateRelativeSizes();
        }
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        // panel.setHeight(height);
        hasHeight = height != null && !height.equals("");
        if (!rendering) {
            panel.updateRelativeSizes();
        }
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        rendering = true;

        if (client.updateComponent(this, uidl, true)) {
            rendering = false;
            return;
        }
        clickEventHandler.handleEventHandlerRegistration(client);

        final VMarginInfo margins = new VMarginInfo(
                uidl.getIntAttribute("margins"));
        setStyleName(margin, CLASSNAME + "-" + StyleConstants.MARGIN_TOP,
                margins.hasTop());
        setStyleName(margin, CLASSNAME + "-" + StyleConstants.MARGIN_RIGHT,
                margins.hasRight());
        setStyleName(margin, CLASSNAME + "-" + StyleConstants.MARGIN_BOTTOM,
                margins.hasBottom());
        setStyleName(margin, CLASSNAME + "-" + StyleConstants.MARGIN_LEFT,
                margins.hasLeft());

        setStyleName(margin, CLASSNAME + "-" + "spacing",
                uidl.hasAttribute("spacing"));
        panel.updateFromUIDL(uidl, client);
        rendering = false;
    }

    public boolean hasChildComponent(Widget component) {
        return panel.hasChildComponent(component);
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        panel.replaceChildComponent(oldComponent, newComponent);
    }

    public void updateCaption(Paintable component, UIDL uidl) {
        panel.updateCaption(component, uidl);
    }

    public class FlowPane extends FlowPanel {

        private final HashMap<Widget, VCaption> widgetToCaption = new HashMap<Widget, VCaption>();
        private ApplicationConnection client;

        public FlowPane() {
            super();
            setStyleName(CLASSNAME + "-container");
        }

        public void updateRelativeSizes() {
            for (Widget w : getChildren()) {
                if (w instanceof Paintable) {
                    client.handleComponentRelativeSize(w);
                }
            }
        }

        public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

            // for later requests
            this.client = client;

            final ArrayList<Widget> oldWidgets = new ArrayList<Widget>();
            for (final Iterator<Widget> iterator = iterator(); iterator
                    .hasNext();) {
                oldWidgets.add(iterator.next());
            }
            clear();

            ValueMap mapAttribute = null;
            if (uidl.hasAttribute("css")) {
                mapAttribute = uidl.getMapAttribute("css");
            }

            for (final Iterator<Object> i = uidl.getChildIterator(); i
                    .hasNext();) {
                final UIDL r = (UIDL) i.next();
                final Paintable child = client.getPaintable(r);
                if (oldWidgets.contains(child)) {
                    oldWidgets.remove(child);
                    VCaption vCaption = widgetToCaption.get(child);
                    if (vCaption != null) {
                        add(vCaption);
                        oldWidgets.remove(vCaption);
                    }
                }

                add((Widget) child);
                if (mapAttribute != null && mapAttribute.containsKey(r.getId())) {
                    String css = null;
                    try {
                        Style style = ((Widget) child).getElement().getStyle();
                        css = mapAttribute.getString(r.getId());
                        String[] cssRules = css.split(";");
                        for (int j = 0; j < cssRules.length; j++) {
                            String[] rule = cssRules[j].split(":");
                            if (rule.length == 0) {
                                continue;
                            } else {
                                style.setProperty(
                                        makeCamelCase(rule[0].trim()),
                                        rule[1].trim());
                            }
                        }
                    } catch (Exception e) {
                        VConsole.log("CssLayout encounterd invalid css string: "
                                + css);
                    }
                }

                if (!r.getBooleanAttribute("cached")) {
                    child.updateFromUIDL(r, client);
                }
            }

            // loop oldWidgetWrappers that where not re-attached and unregister
            // them
            for (Widget w : oldWidgets) {
                if (w instanceof Paintable) {
                    final Paintable p = (Paintable) w;
                    client.unregisterPaintable(p);
                }
                widgetToCaption.remove(w);
            }
        }

        public boolean hasChildComponent(Widget component) {
            return component.getParent() == this;
        }

        public void replaceChildComponent(Widget oldComponent,
                Widget newComponent) {
            VCaption caption = widgetToCaption.get(oldComponent);
            if (caption != null) {
                remove(caption);
                widgetToCaption.remove(oldComponent);
            }
            int index = getWidgetIndex(oldComponent);
            if (index >= 0) {
                remove(oldComponent);
                insert(newComponent, index);
            }
        }

        public void updateCaption(Paintable component, UIDL uidl) {
            VCaption caption = widgetToCaption.get(component);
            if (VCaption.isNeeded(uidl)) {
                Widget widget = (Widget) component;
                if (caption == null) {
                    caption = new VCaption(component, client);
                    widgetToCaption.put(widget, caption);
                    insert(caption, getWidgetIndex(widget));
                } else if (!caption.isAttached()) {
                    insert(caption, getWidgetIndex(widget));
                }
                caption.updateCaption(uidl);
            } else if (caption != null) {
                remove(caption);
                widgetToCaption.remove(component);
            }
        }

        private Paintable getComponent(Element element) {
            return Util.getChildPaintableForElement(client, VCssLayout.this,
                    element);
        }

    }

    private RenderSpace space;

    public RenderSpace getAllocatedSpace(Widget child) {
        if (space == null) {
            space = new RenderSpace(-1, -1) {
                @Override
                public int getWidth() {
                    if (BrowserInfo.get().isIE()) {
                        int width = getOffsetWidth();
                        int margins = margin.getOffsetWidth()
                                - panel.getOffsetWidth();
                        return width - margins;
                    } else {
                        return panel.getOffsetWidth();
                    }
                }

                @Override
                public int getHeight() {
                    int height = getOffsetHeight();
                    int margins = margin.getOffsetHeight()
                            - panel.getOffsetHeight();
                    return height - margins;
                }
            };
        }
        return space;
    }

    public boolean requestLayout(Set<Paintable> children) {
        if (hasSize()) {
            return true;
        } else {
            // Size may have changed
            // TODO optimize this: cache size if not fixed, handle both width
            // and height separately
            return false;
        }
    }

    private boolean hasSize() {
        return hasWidth && hasHeight;
    }

    private static final String makeCamelCase(String cssProperty) {
        // TODO this might be cleaner to implement with regexp
        while (cssProperty.contains("-")) {
            int indexOf = cssProperty.indexOf("-");
            cssProperty = cssProperty.substring(0, indexOf)
                    + String.valueOf(cssProperty.charAt(indexOf + 1))
                            .toUpperCase() + cssProperty.substring(indexOf + 2);
        }
        if ("float".equals(cssProperty)) {
            if (BrowserInfo.get().isIE()) {
                return "styleFloat";
            } else {
                return "cssFloat";
            }
        }
        return cssProperty;
    }
}
