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

import java.util.Collection;

import com.vaadin.data.Container;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.VTwinColSelect;

/**
 * Multiselect component with two lists: left side for available items and right
 * side for selected items.
 */
@SuppressWarnings("serial")
@ClientWidget(VTwinColSelect.class)
public class TwinColSelect extends AbstractSelect {

    private int columns = 0;
    private int rows = 0;

    /**
     * 
     */
    public TwinColSelect() {
        super();
        setMultiSelect(true);
    }

    /**
     * @param caption
     */
    public TwinColSelect(String caption) {
        super(caption);
        setMultiSelect(true);
    }

    /**
     * @param caption
     * @param dataSource
     */
    public TwinColSelect(String caption, Container dataSource) {
        super(caption, dataSource);
        setMultiSelect(true);
    }

    /**
     * Sets the number of columns in the editor. If the number of columns is set
     * 0, the actual number of displayed columns is determined implicitly by the
     * adapter.
     * 
     * @param columns
     *            the number of columns to set.
     */
    public void setColumns(int columns) {
        if (columns < 0) {
            columns = 0;
        }
        if (this.columns != columns) {
            this.columns = columns;
            requestRepaint();
        }
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    /**
     * Sets the number of rows in the editor. If the number of rows is set 0,
     * the actual number of displayed rows is determined implicitly by the
     * adapter.
     * 
     * @param rows
     *            the number of rows to set.
     */
    public void setRows(int rows) {
        if (rows < 0) {
            rows = 0;
        }
        if (this.rows != rows) {
            this.rows = rows;
            requestRepaint();
        }
    }

    /**
     * @param caption
     * @param options
     */
    public TwinColSelect(String caption, Collection options) {
        super(caption, options);
        setMultiSelect(true);
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        target.addAttribute("type", "twincol");
        // Adds the number of columns
        if (columns != 0) {
            target.addAttribute("cols", columns);
        }
        // Adds the number of rows
        if (rows != 0) {
            target.addAttribute("rows", rows);
        }
        super.paintContent(target);
    }

}
