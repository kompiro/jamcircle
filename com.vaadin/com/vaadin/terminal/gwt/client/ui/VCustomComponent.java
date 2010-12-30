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

import java.util.Set;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;

public class VCustomComponent extends SimplePanel implements Container {

    private static final String CLASSNAME = "v-customcomponent";
    private String height;
    private ApplicationConnection client;
    private boolean rendering;
    private String width;
    private RenderSpace renderSpace = new RenderSpace();

    public VCustomComponent() {
        super();
        setStyleName(CLASSNAME);
    }

    public void updateFromUIDL(UIDL uidl, final ApplicationConnection client) {
        rendering = true;
        if (client.updateComponent(this, uidl, true)) {
            rendering = false;
            return;
        }
        this.client = client;

        final UIDL child = uidl.getChildUIDL(0);
        if (child != null) {
            final Paintable p = client.getPaintable(child);
            if (p != getWidget()) {
                if (getWidget() != null) {
                    client.unregisterPaintable((Paintable) getWidget());
                    clear();
                }
                setWidget((Widget) p);
            }
            p.updateFromUIDL(child, client);
        }

        boolean updateDynamicSize = updateDynamicSize();
        if (updateDynamicSize) {
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    // FIXME deferred relative size update needed to fix some
                    // scrollbar issues in sampler. This must be the wrong way
                    // to do it. Might be that some other component is broken.
                    client.handleComponentRelativeSize(VCustomComponent.this);

                }
            });
        }

        renderSpace.setWidth(getElement().getOffsetWidth());
        renderSpace.setHeight(getElement().getOffsetHeight());

        /*
         * Needed to update client size if the size of this component has
         * changed and the child uses relative size(s).
         */
        client.runDescendentsLayout(this);

        rendering = false;
    }

    private boolean updateDynamicSize() {
        boolean updated = false;
        if (isDynamicWidth()) {
            int childWidth = Util.getRequiredWidth(getWidget());
            getElement().getStyle().setPropertyPx("width", childWidth);
            updated = true;
        }
        if (isDynamicHeight()) {
            int childHeight = Util.getRequiredHeight(getWidget());
            getElement().getStyle().setPropertyPx("height", childHeight);
            updated = true;
        }

        return updated;
    }

    protected boolean isDynamicWidth() {
        return width == null || width.equals("");
    }

    protected boolean isDynamicHeight() {
        return height == null || height.equals("");
    }

    public boolean hasChildComponent(Widget component) {
        if (getWidget() == component) {
            return true;
        } else {
            return false;
        }
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        if (hasChildComponent(oldComponent)) {
            clear();
            setWidget(newComponent);
        } else {
            throw new IllegalStateException();
        }
    }

    public void updateCaption(Paintable component, UIDL uidl) {
        // NOP, custom component dont render composition roots caption
    }

    public boolean requestLayout(Set<Paintable> child) {
        return !updateDynamicSize();
    }

    public RenderSpace getAllocatedSpace(Widget child) {
        return renderSpace;
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        renderSpace.setHeight(getElement().getOffsetHeight());

        if (!height.equals(this.height)) {
            this.height = height;
            if (!rendering) {
                client.handleComponentRelativeSize(getWidget());
            }
        }
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        renderSpace.setWidth(getElement().getOffsetWidth());

        if (!width.equals(this.width)) {
            this.width = width;
            if (!rendering) {
                client.handleComponentRelativeSize(getWidget());
            }
        }
    }

}
