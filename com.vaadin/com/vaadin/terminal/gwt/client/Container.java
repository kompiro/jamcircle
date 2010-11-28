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

import java.util.Set;

import com.google.gwt.user.client.ui.Widget;

public interface Container extends Paintable {

    /**
     * Replace child of this layout with another component.
     * 
     * Each layout must be able to switch children. To to this, one must just
     * give references to a current and new child.
     * 
     * @param oldComponent
     *            Child to be replaced
     * @param newComponent
     *            Child that replaces the oldComponent
     */
    void replaceChildComponent(Widget oldComponent, Widget newComponent);

    /**
     * Is a given component child of this layout.
     * 
     * @param component
     *            Component to test.
     * @return true iff component is a child of this layout.
     */
    boolean hasChildComponent(Widget component);

    /**
     * Update child components caption, description and error message.
     * 
     * <p>
     * Each component is responsible for maintaining its caption, description
     * and error message. In most cases components doesn't want to do that and
     * those elements reside outside of the component. Because of this layouts
     * must provide service for it's childen to show those elements for them.
     * </p>
     * 
     * @param component
     *            Child component for which service is requested.
     * @param uidl
     *            UIDL of the child component.
     */
    void updateCaption(Paintable component, UIDL uidl);

    /**
     * Called when a child components size has been updated in the rendering
     * phase.
     * 
     * @param children
     *            Set of child widgets whose size have changed
     * @return true if the size of the Container remains the same, false if the
     *         event need to be propagated to the Containers parent
     */
    boolean requestLayout(Set<Paintable> children);

    /**
     * Returns the size currently allocated for the child component.
     * 
     * @param child
     * @return
     */
    RenderSpace getAllocatedSpace(Widget child);

}
