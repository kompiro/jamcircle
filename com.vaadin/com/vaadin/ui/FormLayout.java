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

import com.vaadin.terminal.gwt.client.ui.VFormLayout;

/**
 * FormLayout is used by {@link Form} to layout fields. It may also be used
 * separately without {@link Form}.
 * 
 * FormLayout is a close relative to vertical {@link OrderedLayout}, but in
 * FormLayout caption is rendered on left side of component. Required and
 * validation indicators are between captions and fields.
 * 
 * FormLayout does not currently support some advanced methods from
 * OrderedLayout like setExpandRatio and setComponentAlignment.
 * 
 * FormLayout by default has component spacing on. Also margin top and margin
 * bottom are by default on.
 * 
 */
@SuppressWarnings({ "deprecation", "serial" })
@ClientWidget(VFormLayout.class)
public class FormLayout extends OrderedLayout {

    public FormLayout() {
        super();
        setSpacing(true);
        setMargin(true, false, true, false);
    }

}
