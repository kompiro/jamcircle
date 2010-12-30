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

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Util;

/**
 * In Vaadin UI this Overlay should always be used for all elements that
 * temporary float over other components like context menus etc. This is to deal
 * stacking order correctly with VWindow objects.
 */
public class VOverlay extends PopupPanel {

    /*
     * The z-index value from where all overlays live. This can be overridden in
     * any extending class.
     */
    protected static int Z_INDEX = 20000;

    /*
     * Shadow element style. If an extending class wishes to use a different
     * style of shadow, it can use setShadowStyle(String) to give the shadow
     * element a new style name.
     */
    public static final String CLASSNAME_SHADOW = "v-shadow";

    /*
     * The shadow element for this overlay.
     */
    private Element shadow;

    /**
     * The HTML snippet that is used to render the actual shadow. In consists of
     * nine different DIV-elements with the following class names:
     * 
     * <pre class='code'>
     *   .v-shadow[-stylename]
     *   ----------------------------------------------
     *   | .top-left     |   .top    |     .top-right |
     *   |---------------|-----------|----------------|
     *   |               |           |                |
     *   | .left         |  .center  |         .right |
     *   |               |           |                |
     *   |---------------|-----------|----------------|
     *   | .bottom-left  |  .bottom  |  .bottom-right |
     *   ----------------------------------------------
     * </pre>
     * 
     * See default theme 'shadow.css' for implementation example.
     */
    private static final String SHADOW_HTML = "<div class=\"top-left\"></div><div class=\"top\"></div><div class=\"top-right\"></div><div class=\"left\"></div><div class=\"center\"></div><div class=\"right\"></div><div class=\"bottom-left\"></div><div class=\"bottom\"></div><div class=\"bottom-right\"></div>";

    public VOverlay() {
        super();
        adjustZIndex();
    }

    public VOverlay(boolean autoHide) {
        super(autoHide);
        adjustZIndex();
    }

    public VOverlay(boolean autoHide, boolean modal) {
        super(autoHide, modal);
        adjustZIndex();
    }

    public VOverlay(boolean autoHide, boolean modal, boolean showShadow) {
        super(autoHide, modal);
        if (showShadow) {
            shadow = DOM.createDiv();
            shadow.setClassName(CLASSNAME_SHADOW);
            shadow.setInnerHTML(SHADOW_HTML);
            DOM.setStyleAttribute(shadow, "position", "absolute");

            addCloseHandler(new CloseHandler<PopupPanel>() {
                public void onClose(CloseEvent<PopupPanel> event) {
                    if (shadow.getParentElement() != null) {
                        shadow.getParentElement().removeChild(shadow);
                    }
                }
            });
        }
        adjustZIndex();
    }

    private void adjustZIndex() {
        setZIndex(Z_INDEX);
    }

    /**
     * Set the z-index (visual stack position) for this overlay.
     * 
     * @param zIndex
     *            The new z-index
     */
    protected void setZIndex(int zIndex) {
        DOM.setStyleAttribute(getElement(), "zIndex", "" + zIndex);
        if (shadow != null) {
            DOM.setStyleAttribute(shadow, "zIndex", "" + zIndex);
        }
    }

    @Override
    public void setPopupPosition(int left, int top) {
        super.setPopupPosition(left, top);
        if (shadow != null) {
            updateShadowSizeAndPosition(isAnimationEnabled() ? 0 : 1);
        }
    }

    @Override
    public void show() {
        super.show();
        if (shadow != null) {
            if (isAnimationEnabled()) {
                ShadowAnimation sa = new ShadowAnimation();
                sa.run(200);
            } else {
                updateShadowSizeAndPosition(1.0);
            }
        }
        Util.runIE7ZeroSizedBodyFix();
    }

    @Override
    public void hide(boolean autoClosed) {
        super.hide(autoClosed);
        Util.runIE7ZeroSizedBodyFix();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (shadow != null) {
            shadow.getStyle().setProperty("visibility",
                    visible ? "visible" : "hidden");
        }
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        if (shadow != null) {
            updateShadowSizeAndPosition(1.0);
        }
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        if (shadow != null) {
            updateShadowSizeAndPosition(1.0);
        }
    }

    /**
     * Sets the shadow style for this overlay. Will override any previous style
     * for the shadow. The default style name is defined by CLASSNAME_SHADOW.
     * The given style will be prefixed with CLASSNAME_SHADOW.
     * 
     * @param style
     *            The new style name for the shadow element. Will be prefixed by
     *            CLASSNAME_SHADOW, e.g. style=='foobar' -> actual style
     *            name=='v-shadow-foobar'.
     */
    protected void setShadowStyle(String style) {
        if (shadow != null) {
            shadow.setClassName(CLASSNAME_SHADOW + "-" + style);
        }
    }

    /*
     * Extending classes should always call this method after they change the
     * size of overlay without using normal 'setWidth(String)' and
     * 'setHeight(String)' methods (if not calling super.setWidth/Height).
     */
    protected void updateShadowSizeAndPosition() {
        updateShadowSizeAndPosition(1.0);
    }

    /**
     * Recalculates proper position and dimensions for the shadow element. Can
     * be used to animate the shadow, using the 'progress' parameter (used to
     * animate the shadow in sync with GWT PopupPanel's default animation
     * 'PopupPanel.AnimationType.CENTER').
     * 
     * @param progress
     *            A value between 0.0 and 1.0, indicating the progress of the
     *            animation (0=start, 1=end).
     */
    private void updateShadowSizeAndPosition(final double progress) {
        // Don't do anything if overlay element is not attached
        if (!isAttached()) {
            return;
        }
        // Calculate proper z-index
        String zIndex = null;
        try {
            // Odd behaviour with Windows Hosted Mode forces us to use
            // this redundant try/catch block (See dev.vaadin.com #2011)
            zIndex = DOM.getStyleAttribute(getElement(), "zIndex");
        } catch (Exception ignore) {
            // Ignored, will cause no harm
            zIndex = "1000";
        }
        if (zIndex == null) {
            zIndex = "" + Z_INDEX;
        }
        // Calculate position and size
        if (BrowserInfo.get().isIE()) {
            // Shake IE
            getOffsetHeight();
            getOffsetWidth();
        }

        int x = getAbsoluteLeft();
        int y = getAbsoluteTop();

        /* This is needed for IE7 at least */
        // Account for the difference between absolute position and the
        // body's positioning context.
        x -= Document.get().getBodyOffsetLeft();
        y -= Document.get().getBodyOffsetTop();

        int width = getOffsetWidth();
        int height = getOffsetHeight();

        if (width < 0) {
            width = 0;
        }
        if (height < 0) {
            height = 0;
        }

        // Animate the shadow size
        x += (int) (width * (1.0 - progress) / 2.0);
        y += (int) (height * (1.0 - progress) / 2.0);
        width = (int) (width * progress);
        height = (int) (height * progress);

        // Opera needs some shaking to get parts of the shadow showing
        // properly
        // (ticket #2704)
        if (BrowserInfo.get().isOpera()) {
            // Clear the height of all middle elements
            DOM.getChild(shadow, 3).getStyle().setProperty("height", "auto");
            DOM.getChild(shadow, 4).getStyle().setProperty("height", "auto");
            DOM.getChild(shadow, 5).getStyle().setProperty("height", "auto");
        }

        // Update correct values
        DOM.setStyleAttribute(shadow, "zIndex", zIndex);
        DOM.setStyleAttribute(shadow, "width", width + "px");
        DOM.setStyleAttribute(shadow, "height", height + "px");
        DOM.setStyleAttribute(shadow, "top", y + "px");
        DOM.setStyleAttribute(shadow, "left", x + "px");
        DOM.setStyleAttribute(shadow, "display", progress < 0.9 ? "none" : "");

        // Opera fix, part 2 (ticket #2704)
        if (BrowserInfo.get().isOpera()) {
            // We'll fix the height of all the middle elements
            DOM.getChild(shadow, 3)
                    .getStyle()
                    .setPropertyPx("height",
                            DOM.getChild(shadow, 3).getOffsetHeight());
            DOM.getChild(shadow, 4)
                    .getStyle()
                    .setPropertyPx("height",
                            DOM.getChild(shadow, 4).getOffsetHeight());
            DOM.getChild(shadow, 5)
                    .getStyle()
                    .setPropertyPx("height",
                            DOM.getChild(shadow, 5).getOffsetHeight());
        }

        // Attach to dom if not there already
        if (shadow.getParentElement() == null) {
            RootPanel.get().getElement().insertBefore(shadow, getElement());
        }

    }

    protected class ShadowAnimation extends Animation {
        @Override
        protected void onUpdate(double progress) {
            if (shadow != null) {
                updateShadowSizeAndPosition(progress);
            }
        }
    }
}
