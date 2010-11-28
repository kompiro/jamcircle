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
package com.vaadin.terminal.gwt.client.ui.dd;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.event.dd.DragSource;
import com.vaadin.terminal.gwt.client.Paintable;

/**
 * Client side counterpart for Transferable in com.vaadin.event.Transferable
 * 
 */
public class VTransferable {

    private Paintable component;

    private final Map<String, Object> variables = new HashMap<String, Object>();

    /**
     * Returns the component from which the transferable is created (eg. a tree
     * which node is dragged).
     * 
     * @return the component
     */
    public Paintable getDragSource() {
        return component;
    }

    /**
     * Sets the component currently being dragged or from which the transferable
     * is created (eg. a tree which node is dragged).
     * <p>
     * The server side counterpart of the component may implement
     * {@link DragSource} interface if it wants to translate or complement the
     * server side instance of this Transferable.
     * 
     * @param component
     *            the component to set
     */
    public void setDragSource(Paintable component) {
        this.component = component;
    }

    public Object getData(String dataFlawor) {
        return variables.get(dataFlawor);
    }

    public void setData(String dataFlawor, Object value) {
        variables.put(dataFlawor, value);
    }

    public Collection<String> getDataFlavors() {
        return variables.keySet();
    }

    /**
     * This helper method should only be called by {@link VDragAndDropManager}.
     * 
     * @return data in this Transferable that needs to be moved to server.
     */
    Map<String, Object> getVariableMap() {
        return variables;
    }

}
