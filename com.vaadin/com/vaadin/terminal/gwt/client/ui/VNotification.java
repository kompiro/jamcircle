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
import java.util.Date;
import java.util.EventObject;
import java.util.Iterator;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.BrowserInfo;

public class VNotification extends VOverlay {

    public static final int CENTERED = 1;
    public static final int CENTERED_TOP = 2;
    public static final int CENTERED_BOTTOM = 3;
    public static final int TOP_LEFT = 4;
    public static final int TOP_RIGHT = 5;
    public static final int BOTTOM_LEFT = 6;
    public static final int BOTTOM_RIGHT = 7;

    public static final int DELAY_FOREVER = -1;
    public static final int DELAY_NONE = 0;

    private static final String STYLENAME = "v-Notification";
    private static final int mouseMoveThreshold = 7;
    private static final int Z_INDEX_BASE = 20000;
    public static final String STYLE_SYSTEM = "system";
    private static final int FADE_ANIMATION_INTERVAL = 50; // == 20 fps

    private int startOpacity = 90;
    private int fadeMsec = 400;
    private int delayMsec = 1000;

    private Timer fader;
    private Timer delay;

    private int x = -1;
    private int y = -1;

    private String temporaryStyle;

    private ArrayList<EventListener> listeners;

    public VNotification() {
        setStyleName(STYLENAME);
        sinkEvents(Event.ONCLICK);
        DOM.setStyleAttribute(getElement(), "zIndex", "" + Z_INDEX_BASE);
    }

    public VNotification(int delayMsec) {
        this();
        this.delayMsec = delayMsec;
    }

    public VNotification(int delayMsec, int fadeMsec, int startOpacity) {
        this(delayMsec);
        this.fadeMsec = fadeMsec;
        this.startOpacity = startOpacity;
    }

    public void startDelay() {
        DOM.removeEventPreview(this);
        if (delayMsec > 0) {
            if (delay == null) {
                delay = new Timer() {
                    @Override
                    public void run() {
                        fade();
                    }
                };
                delay.schedule(delayMsec);
            }
        } else if (delayMsec == 0) {
            fade();
        }
    }

    @Override
    public void show() {
        show(CENTERED);
    }

    public void show(String style) {
        show(CENTERED, style);
    }

    public void show(int position) {
        show(position, null);
    }

    public void show(Widget widget, int position, String style) {
        setWidget(widget);
        show(position, style);
    }

    public void show(String html, int position, String style) {
        setWidget(new HTML(html));
        show(position, style);
    }

    public void show(int position, String style) {
        setOpacity(getElement(), startOpacity);
        if (style != null) {
            temporaryStyle = style;
            addStyleName(style);
            addStyleDependentName(style);
        }
        super.show();
        setPosition(position);
    }

    @Override
    public void hide() {
        DOM.removeEventPreview(this);
        cancelDelay();
        cancelFade();
        if (temporaryStyle != null) {
            removeStyleName(temporaryStyle);
            removeStyleDependentName(temporaryStyle);
            temporaryStyle = null;
        }
        super.hide();
        fireEvent(new HideEvent(this));
    }

    public void fade() {
        DOM.removeEventPreview(this);
        cancelDelay();
        if (fader == null) {
            fader = new Timer() {
                private final long start = new Date().getTime();

                @Override
                public void run() {
                    /*
                     * To make animation smooth, don't count that event happens
                     * on time. Reduce opacity according to the actual time
                     * spent instead of fixed decrement.
                     */
                    long now = new Date().getTime();
                    long timeEplaced = now - start;
                    float remainingFraction = 1 - timeEplaced
                            / (float) fadeMsec;
                    int opacity = (int) (startOpacity * remainingFraction);
                    if (opacity <= 0) {
                        cancel();
                        hide();
                        if (BrowserInfo.get().isOpera()) {
                            // tray notification on opera needs to explicitly
                            // define
                            // size, reset it
                            DOM.setStyleAttribute(getElement(), "width", "");
                            DOM.setStyleAttribute(getElement(), "height", "");
                        }
                    } else {
                        setOpacity(getElement(), opacity);
                    }
                }
            };
            fader.scheduleRepeating(FADE_ANIMATION_INTERVAL);
        }
    }

    public void setPosition(int position) {
        final Element el = getElement();
        DOM.setStyleAttribute(el, "top", "");
        DOM.setStyleAttribute(el, "left", "");
        DOM.setStyleAttribute(el, "bottom", "");
        DOM.setStyleAttribute(el, "right", "");
        switch (position) {
        case TOP_LEFT:
            DOM.setStyleAttribute(el, "top", "0px");
            DOM.setStyleAttribute(el, "left", "0px");
            break;
        case TOP_RIGHT:
            DOM.setStyleAttribute(el, "top", "0px");
            DOM.setStyleAttribute(el, "right", "0px");
            break;
        case BOTTOM_RIGHT:
            DOM.setStyleAttribute(el, "position", "absolute");
            if (BrowserInfo.get().isOpera()) {
                // tray notification on opera needs explicitly defined size
                DOM.setStyleAttribute(el, "width", getOffsetWidth() + "px");
                DOM.setStyleAttribute(el, "height", getOffsetHeight() + "px");
            }
            DOM.setStyleAttribute(el, "bottom", "0px");
            DOM.setStyleAttribute(el, "right", "0px");
            break;
        case BOTTOM_LEFT:
            DOM.setStyleAttribute(el, "bottom", "0px");
            DOM.setStyleAttribute(el, "left", "0px");
            break;
        case CENTERED_TOP:
            center();
            DOM.setStyleAttribute(el, "top", "0px");
            break;
        case CENTERED_BOTTOM:
            center();
            DOM.setStyleAttribute(el, "top", "");
            DOM.setStyleAttribute(el, "bottom", "0px");
            break;
        default:
        case CENTERED:
            center();
            break;
        }
    }

    private void cancelFade() {
        if (fader != null) {
            fader.cancel();
            fader = null;
        }
    }

    private void cancelDelay() {
        if (delay != null) {
            delay.cancel();
            delay = null;
        }
    }

    private void setOpacity(Element el, int opacity) {
        DOM.setStyleAttribute(el, "opacity", "" + (opacity / 100.0));
        if (BrowserInfo.get().isIE()) {
            DOM.setStyleAttribute(el, "filter", "Alpha(opacity=" + opacity
                    + ")");
        }
    }

    @Override
    public void onBrowserEvent(Event event) {
        DOM.removeEventPreview(this);
        if (fader == null) {
            fade();
        }
    }

    @Override
    public boolean onEventPreview(Event event) {
        int type = DOM.eventGetType(event);
        // "modal"
        if (delayMsec == -1 || temporaryStyle == STYLE_SYSTEM) {
            if (type == Event.ONCLICK) {
                if (DOM.isOrHasChild(getElement(), DOM.eventGetTarget(event))) {
                    fade();
                    return false;
                }
            }
            if (temporaryStyle == STYLE_SYSTEM) {
                return true;
            } else {
                return false;
            }
        }
        // default
        switch (type) {
        case Event.ONMOUSEMOVE:

            if (x < 0) {
                x = DOM.eventGetClientX(event);
                y = DOM.eventGetClientY(event);
            } else if (Math.abs(DOM.eventGetClientX(event) - x) > mouseMoveThreshold
                    || Math.abs(DOM.eventGetClientY(event) - y) > mouseMoveThreshold) {
                startDelay();
            }
            break;
        case Event.ONMOUSEDOWN:
        case Event.ONMOUSEWHEEL:
        case Event.ONSCROLL:
            startDelay();
            break;
        case Event.ONKEYDOWN:
            if (event.getRepeat()) {
                return true;
            }
            startDelay();
            break;
        default:
            break;
        }
        return true;
    }

    public void addEventListener(EventListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<EventListener>();
        }
        listeners.add(listener);
    }

    public void removeEventListener(EventListener listener) {
        if (listeners == null) {
            return;
        }
        listeners.remove(listener);
    }

    private void fireEvent(HideEvent event) {
        if (listeners != null) {
            for (Iterator<EventListener> it = listeners.iterator(); it
                    .hasNext();) {
                EventListener l = it.next();
                l.notificationHidden(event);
            }
        }
    }

    public class HideEvent extends EventObject {

        public HideEvent(Object source) {
            super(source);
        }
    }

    public interface EventListener extends java.util.EventListener {
        public void notificationHidden(HideEvent event);
    }
}
