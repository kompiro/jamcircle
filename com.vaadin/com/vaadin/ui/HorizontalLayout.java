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

import com.vaadin.terminal.gwt.client.ui.VHorizontalLayout;
import com.vaadin.ui.ClientWidget.LoadStyle;

/**
 * Horizontal layout
 * 
 * <code>HorizontalLayout</code> is a component container, which shows the
 * subcomponents in the order of their addition (horizontally).
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 5.3
 */
@SuppressWarnings("serial")
@ClientWidget(value = VHorizontalLayout.class, loadStyle = LoadStyle.EAGER)
public class HorizontalLayout extends AbstractOrderedLayout {

    public HorizontalLayout() {

    }

}
