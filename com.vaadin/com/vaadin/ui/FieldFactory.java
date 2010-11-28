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

import com.vaadin.data.Property;

/**
 * Factory for creating new Field-instances based on type, datasource and/or
 * context.
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 3.1
 * @deprecated FieldFactory was split into two lighter interfaces in 6.0 Use
 *             FormFieldFactory or TableFieldFactory or both instead.
 */
@Deprecated
public interface FieldFactory extends FormFieldFactory, TableFieldFactory {

    /**
     * Creates a field based on type of data.
     * 
     * @param type
     *            the type of data presented in field.
     * @param uiContext
     *            the component where the field is presented.
     * @return Field the field suitable for editing the specified data.
     * 
     */
    Field createField(Class type, Component uiContext);

    /**
     * Creates a field based on the property datasource.
     * 
     * @param property
     *            the property datasource.
     * @param uiContext
     *            the component where the field is presented.
     * @return Field the field suitable for editing the specified data.
     */
    Field createField(Property property, Component uiContext);

}
