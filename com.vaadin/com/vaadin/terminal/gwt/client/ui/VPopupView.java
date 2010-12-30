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

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderInformation.Size;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.VCaptionWrapper;
import com.vaadin.terminal.gwt.client.VTooltip;
import com.vaadin.terminal.gwt.client.ui.richtextarea.VRichTextArea;

public class VPopupView extends HTML implements Container, Iterable<Widget> {

    public static final String CLASSNAME = "v-popupview";

    /** For server-client communication */
    private String uidlId;
    private ApplicationConnection client;

    /** This variable helps to communicate popup visibility to the server */
    private boolean hostPopupVisible;

    private final CustomPopup popup;
    private final Label loading = new Label();

    /**
     * loading constructor
     */
    public VPopupView() {
        super();
        popup = new CustomPopup();

        setStyleName(CLASSNAME);
        popup.setStyleName(CLASSNAME + "-popup");
        loading.setStyleName(CLASSNAME + "-loading");

        setHTML("");
        popup.setWidget(loading);

        // When we click to open the popup...
        addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                updateState(true);
            }
        });

        // ..and when we close it
        popup.addCloseHandler(new CloseHandler<PopupPanel>() {
            public void onClose(CloseEvent<PopupPanel> event) {
                updateState(false);
            }
        });

        popup.setAnimationEnabled(true);
        sinkEvents(VTooltip.TOOLTIP_EVENTS);
    }

    /**
     * 
     * 
     * @see com.vaadin.terminal.gwt.client.Paintable#updateFromUIDL(com.vaadin.terminal.gwt.client.UIDL,
     *      com.vaadin.terminal.gwt.client.ApplicationConnection)
     */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // This call should be made first. Ensure correct implementation,
        // and don't let the containing layout manage caption.
        if (client.updateComponent(this, uidl, false)) {
            return;
        }
        // These are for future server connections
        this.client = client;
        uidlId = uidl.getId();

        hostPopupVisible = uidl.getBooleanVariable("popupVisibility");

        setHTML(uidl.getStringAttribute("html"));

        if (uidl.hasAttribute("hideOnMouseOut")) {
            popup.setHideOnMouseOut(uidl.getBooleanAttribute("hideOnMouseOut"));
        }

        // Render the popup if visible and show it.
        if (hostPopupVisible) {
            UIDL popupUIDL = uidl.getChildUIDL(0);

            // showPopupOnTop(popup, hostReference);
            preparePopup(popup);
            popup.updateFromUIDL(popupUIDL, client);
            if (uidl.hasAttribute("style")) {
                final String[] styles = uidl.getStringAttribute("style").split(
                        " ");
                final StringBuffer styleBuf = new StringBuffer();
                final String primaryName = popup.getStylePrimaryName();
                styleBuf.append(primaryName);
                for (int i = 0; i < styles.length; i++) {
                    styleBuf.append(" ");
                    styleBuf.append(primaryName);
                    styleBuf.append("-");
                    styleBuf.append(styles[i]);
                }
                popup.setStyleName(styleBuf.toString());
            } else {
                popup.setStyleName(popup.getStylePrimaryName());
            }
            showPopup(popup);

            // The popup shouldn't be visible, try to hide it.
        } else {
            popup.hide();
        }
    }// updateFromUIDL

    /**
     * Update popup visibility to server
     * 
     * @param visibility
     */
    private void updateState(boolean visible) {
        // If we know the server connection
        // then update the current situation
        if (uidlId != null && client != null && isAttached()) {
            client.updateVariable(uidlId, "popupVisibility", visible, true);
        }
    }

    private void preparePopup(final CustomPopup popup) {
        popup.setVisible(false);
        popup.show();
    }

    /**
     * Determines the correct position for a popup and displays the popup at
     * that position.
     * 
     * By default, the popup is shown centered relative to its host component,
     * ensuring it is visible on the screen if possible.
     * 
     * Can be overridden to customize the popup position.
     * 
     * @param popup
     */
    protected void showPopup(final CustomPopup popup) {
        int windowTop = RootPanel.get().getAbsoluteTop();
        int windowLeft = RootPanel.get().getAbsoluteLeft();
        int windowRight = windowLeft + RootPanel.get().getOffsetWidth();
        int windowBottom = windowTop + RootPanel.get().getOffsetHeight();

        int offsetWidth = popup.getOffsetWidth();
        int offsetHeight = popup.getOffsetHeight();

        int hostHorizontalCenter = VPopupView.this.getAbsoluteLeft()
                + VPopupView.this.getOffsetWidth() / 2;
        int hostVerticalCenter = VPopupView.this.getAbsoluteTop()
                + VPopupView.this.getOffsetHeight() / 2;

        int left = hostHorizontalCenter - offsetWidth / 2;
        int top = hostVerticalCenter - offsetHeight / 2;

        // Don't show the popup outside the screen.
        if ((left + offsetWidth) > windowRight) {
            left -= (left + offsetWidth) - windowRight;
        }

        if ((top + offsetHeight) > windowBottom) {
            top -= (top + offsetHeight) - windowBottom;
        }

        if (left < 0) {
            left = 0;
        }

        if (top < 0) {
            top = 0;
        }

        popup.setPopupPosition(left, top);

        popup.setVisible(true);
    }

    /**
     * Make sure that we remove the popup when the main widget is removed.
     * 
     * @see com.google.gwt.user.client.ui.Widget#onUnload()
     */
    @Override
    protected void onDetach() {
        popup.hide();
        super.onDetach();
    }

    private static native void nativeBlur(Element e)
    /*-{
        if(e && e.blur) {
            e.blur();
        }
    }-*/;

    /**
     * This class is only protected to enable overriding showPopup, and is
     * currently not intended to be extended or otherwise used directly. Its API
     * (other than it being a VOverlay) is to be considered private and
     * potentially subject to change.
     */
    protected class CustomPopup extends VOverlay {

        private Paintable popupComponentPaintable = null;
        private Widget popupComponentWidget = null;
        private VCaptionWrapper captionWrapper = null;

        private boolean hasHadMouseOver = false;
        private boolean hideOnMouseOut = true;
        private final Set<Element> activeChildren = new HashSet<Element>();
        private boolean hiding = false;

        public CustomPopup() {
            super(true, false, true); // autoHide, not modal, dropshadow
        }

        // For some reason ONMOUSEOUT events are not always received, so we have
        // to use ONMOUSEMOVE that doesn't target the popup
        @Override
        public boolean onEventPreview(Event event) {
            Element target = DOM.eventGetTarget(event);
            boolean eventTargetsPopup = DOM.isOrHasChild(getElement(), target);
            int type = DOM.eventGetType(event);

            // Catch children that use keyboard, so we can unfocus them when
            // hiding
            if (eventTargetsPopup && type == Event.ONKEYPRESS) {
                activeChildren.add(target);
            }

            if (eventTargetsPopup && type == Event.ONMOUSEMOVE) {
                hasHadMouseOver = true;
            }

            if (!eventTargetsPopup && type == Event.ONMOUSEMOVE) {
                if (hasHadMouseOver && hideOnMouseOut) {
                    hide();
                    return true;
                }
            }

            // Was the TAB key released outside of our popup?
            if (!eventTargetsPopup && type == Event.ONKEYUP
                    && event.getKeyCode() == KeyCodes.KEY_TAB) {
                // Should we hide on focus out (mouse out)?
                if (hideOnMouseOut) {
                    hide();
                    return true;
                }
            }

            return super.onEventPreview(event);
        }

        @Override
        public void hide(boolean autoClosed) {
            hiding = true;
            syncChildren();
            unregisterPaintables();
            if (popupComponentWidget != null && popupComponentWidget != loading) {
                remove(popupComponentWidget);
            }
            hasHadMouseOver = false;
            super.hide(autoClosed);
        }

        @Override
        public void show() {
            hiding = false;
            super.show();
        }

        /**
         * Try to sync all known active child widgets to server
         */
        public void syncChildren() {
            // Notify children with focus
            if ((popupComponentWidget instanceof Focusable)) {
                ((Focusable) popupComponentWidget).setFocus(false);
            } else {

                checkForRTE(popupComponentWidget);
            }

            // Notify children that have used the keyboard
            for (Element e : activeChildren) {
                try {
                    nativeBlur(e);
                } catch (Exception ignored) {
                }
            }
            activeChildren.clear();
        }

        private void checkForRTE(Widget popupComponentWidget2) {
            if (popupComponentWidget2 instanceof VRichTextArea) {
                ((VRichTextArea) popupComponentWidget2)
                        .synchronizeContentToServer();
            } else if (popupComponentWidget2 instanceof HasWidgets) {
                HasWidgets hw = (HasWidgets) popupComponentWidget2;
                Iterator<Widget> iterator = hw.iterator();
                while (iterator.hasNext()) {
                    checkForRTE(iterator.next());
                }
            }
        }

        @Override
        public boolean remove(Widget w) {

            popupComponentPaintable = null;
            popupComponentWidget = null;
            captionWrapper = null;

            return super.remove(w);
        }

        public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

            Paintable newPopupComponent = client.getPaintable(uidl
                    .getChildUIDL(0));

            if (newPopupComponent != popupComponentPaintable) {

                setWidget((Widget) newPopupComponent);

                popupComponentWidget = (Widget) newPopupComponent;

                popupComponentPaintable = newPopupComponent;
            }

            popupComponentPaintable
                    .updateFromUIDL(uidl.getChildUIDL(0), client);
        }

        public void unregisterPaintables() {
            if (popupComponentPaintable != null) {
                client.unregisterPaintable(popupComponentPaintable);
            }
        }

        public void setHideOnMouseOut(boolean hideOnMouseOut) {
            this.hideOnMouseOut = hideOnMouseOut;
        }

        /*
         * 
         * We need a hack make popup act as a child of VPopupView in Vaadin's
         * component tree, but work in default GWT manner when closing or
         * opening.
         * 
         * (non-Javadoc)
         * 
         * @see com.google.gwt.user.client.ui.Widget#getParent()
         */
        @Override
        public Widget getParent() {
            if (!isAttached() || hiding) {
                return super.getParent();
            } else {
                return VPopupView.this;
            }
        }

        @Override
        protected void onDetach() {
            super.onDetach();
            hiding = false;
        }

        @Override
        public Element getContainerElement() {
            return super.getContainerElement();
        }

    }// class CustomPopup

    // Container methods

    public RenderSpace getAllocatedSpace(Widget child) {
        Size popupExtra = calculatePopupExtra();

        return new RenderSpace(RootPanel.get().getOffsetWidth()
                - popupExtra.getWidth(), RootPanel.get().getOffsetHeight()
                - popupExtra.getHeight());
    }

    /**
     * Calculate extra space taken by the popup decorations
     * 
     * @return
     */
    protected Size calculatePopupExtra() {
        Element pe = popup.getElement();
        Element ipe = popup.getContainerElement();

        // border + padding
        int width = Util.getRequiredWidth(pe) - Util.getRequiredWidth(ipe);
        int height = Util.getRequiredHeight(pe) - Util.getRequiredHeight(ipe);

        return new Size(width, height);
    }

    public boolean hasChildComponent(Widget component) {
        if (popup.popupComponentWidget != null) {
            return popup.popupComponentWidget == component;
        } else {
            return false;
        }
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        popup.setWidget(newComponent);
        popup.popupComponentWidget = newComponent;
    }

    public boolean requestLayout(Set<Paintable> child) {
        popup.updateShadowSizeAndPosition();
        return true;
    }

    public void updateCaption(Paintable component, UIDL uidl) {
        if (VCaption.isNeeded(uidl)) {
            if (popup.captionWrapper != null) {
                popup.captionWrapper.updateCaption(uidl);
            } else {
                popup.captionWrapper = new VCaptionWrapper(component, client);
                popup.setWidget(popup.captionWrapper);
                popup.captionWrapper.updateCaption(uidl);
            }
        } else {
            if (popup.captionWrapper != null) {
                popup.setWidget(popup.popupComponentWidget);
            }
        }

        popup.popupComponentWidget = (Widget) component;
        popup.popupComponentPaintable = component;
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (client != null) {
            client.handleTooltipEvent(event, this);
        }
    }

    public Iterator<Widget> iterator() {
        return new Iterator<Widget>() {

            int pos = 0;

            public boolean hasNext() {
                // There is a child widget only if next() has not been called.
                return (pos == 0);
            }

            public Widget next() {
                // Next can be called only once to return the popup.
                if (pos != 0) {
                    throw new NoSuchElementException();
                }
                pos++;
                return popup;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }

}// class VPopupView
