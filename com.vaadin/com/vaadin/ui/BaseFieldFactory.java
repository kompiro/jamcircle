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

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * Default implementation of the the following Field types are used by default:
 * <p>
 * <b>Boolean</b>: Button(switchMode:true).<br/>
 * <b>Date</b>: DateField(resolution: day).<br/>
 * <b>Item</b>: Form. <br/>
 * <b>default field type</b>: TextField.
 * <p>
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 3.1
 * @deprecated use {@link DefaultFieldFactory} or own implementations on
 *             {@link FormFieldFactory} or {@link TableFieldFactory} instead.
 */

@Deprecated
@SuppressWarnings("serial")
public class BaseFieldFactory implements FieldFactory {

    /**
     * Creates the field based on type of data.
     * 
     * 
     * @param type
     *            the type of data presented in field.
     * @param uiContext
     *            the context where the Field is presented.
     * 
     * @see com.vaadin.ui.FieldFactory#createField(Class, Component)
     */
    public Field createField(Class type, Component uiContext) {
        return DefaultFieldFactory.createFieldByPropertyType(type);
    }

    /**
     * Creates the field based on the datasource property.
     * 
     * @see com.vaadin.ui.FieldFactory#createField(Property, Component)
     */
    public Field createField(Property property, Component uiContext) {
        if (property != null) {
            return createField(property.getType(), uiContext);
        } else {
            return null;
        }
    }

    /**
     * Creates the field based on the item and property id.
     * 
     * @see com.vaadin.ui.FieldFactory#createField(Item, Object, Component)
     */
    public Field createField(Item item, Object propertyId, Component uiContext) {
        if (item != null && propertyId != null) {
            final Field f = createField(item.getItemProperty(propertyId),
                    uiContext);
            if (f instanceof AbstractComponent) {
                String name = DefaultFieldFactory
                        .createCaptionByPropertyId(propertyId);
                f.setCaption(name);
            }
            return f;
        } else {
            return null;
        }
    }

    /**
     * @see com.vaadin.ui.FieldFactory#createField(com.vaadin.data.Container,
     *      java.lang.Object, java.lang.Object, com.vaadin.ui.Component)
     */
    public Field createField(Container container, Object itemId,
            Object propertyId, Component uiContext) {
        return createField(container.getContainerProperty(itemId, propertyId),
                uiContext);
    }

}
