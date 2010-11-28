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
package com.vaadin.event;

import java.lang.reflect.Method;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.tools.ReflectTools;
import com.vaadin.ui.Component;

public interface LayoutEvents {

    public interface LayoutClickListener extends ComponentEventListener {

        public static final Method clickMethod = ReflectTools.findMethod(
                LayoutClickListener.class, "layoutClick",
                LayoutClickEvent.class);

        /**
         * Layout has been clicked
         * 
         * @param event
         *            Component click event.
         */
        public void layoutClick(LayoutClickEvent event);
    }

    /**
     * An event fired when the layout has been clicked. The event contains
     * information about the target layout (component) and the child component
     * that was clicked. If no child component was found it is set to null.
     * 
     */
    public static class LayoutClickEvent extends ClickEvent {

        private Component childComponent;

        public LayoutClickEvent(Component source,
                MouseEventDetails mouseEventDetails, Component childComponent) {
            super(source, mouseEventDetails);
            this.childComponent = childComponent;
        }

        public Component getChildComponent() {
            return childComponent;
        }

    }
}
