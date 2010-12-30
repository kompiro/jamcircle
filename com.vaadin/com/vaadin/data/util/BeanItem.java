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

package com.vaadin.data.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;

import com.vaadin.data.Property;

/**
 * A wrapper class for adding the Item interface to any Java Bean.
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 3.0
 */
@SuppressWarnings("serial")
public class BeanItem<BT> extends PropertysetItem {

    /**
     * The bean which this Item is based on.
     */
    private final BT bean;

    /**
     * <p>
     * Creates a new instance of <code>BeanItem</code> and adds all properties
     * of a Java Bean to it. The properties are identified by their respective
     * bean names.
     * </p>
     * 
     * <p>
     * Note : This version only supports introspectable bean properties and
     * their getter and setter methods. Stand-alone <code>is</code> and
     * <code>are</code> methods are not supported.
     * </p>
     * 
     * @param bean
     *            the Java Bean to copy properties from.
     * 
     */
    public BeanItem(BT bean) {
        this(bean, getPropertyDescriptors(bean.getClass()));
    }

    /**
     * <p>
     * Creates a new instance of <code>BeanItem</code> using a pre-computed set
     * of properties. The properties are identified by their respective bean
     * names.
     * </p>
     * 
     * @param bean
     *            the Java Bean to copy properties from.
     * @param propertyDescriptors
     *            pre-computed property descriptors
     */
    BeanItem(BT bean,
            LinkedHashMap<String, PropertyDescriptor> propertyDescriptors) {

        this.bean = bean;

        for (PropertyDescriptor pd : propertyDescriptors.values()) {
            final Method getMethod = pd.getReadMethod();
            final Method setMethod = pd.getWriteMethod();
            final Class<?> type = pd.getPropertyType();
            final String name = pd.getName();
            final Property p = new MethodProperty(type, bean, getMethod,
                    setMethod);
            addItemProperty(name, p);

        }
    }

    /**
     * <p>
     * Creates a new instance of <code>BeanItem</code> and adds all listed
     * properties of a Java Bean to it - in specified order. The properties are
     * identified by their respective bean names.
     * </p>
     * 
     * <p>
     * Note : This version only supports introspectable bean properties and
     * their getter and setter methods. Stand-alone <code>is</code> and
     * <code>are</code> methods are not supported.
     * </p>
     * 
     * @param bean
     *            the Java Bean to copy properties from.
     * @param propertyIds
     *            id of the property.
     */
    public BeanItem(BT bean, Collection<?> propertyIds) {

        this.bean = bean;

        // Create bean information
        LinkedHashMap<String, PropertyDescriptor> pds = getPropertyDescriptors(bean
                .getClass());

        // Add all the bean properties as MethodProperties to this Item
        for (Object id : propertyIds) {
            PropertyDescriptor pd = pds.get(id);
            if (pd != null) {
                final String name = pd.getName();
                final Method getMethod = pd.getReadMethod();
                final Method setMethod = pd.getWriteMethod();
                final Class<?> type = pd.getPropertyType();
                final Property p = new MethodProperty(type, bean, getMethod,
                        setMethod);
                addItemProperty(name, p);
            }
        }

    }

    /**
     * <p>
     * Creates a new instance of <code>BeanItem</code> and adds all listed
     * properties of a Java Bean to it - in specified order. The properties are
     * identified by their respective bean names.
     * </p>
     * 
     * <p>
     * Note : This version only supports introspectable bean properties and
     * their getter and setter methods. Stand-alone <code>is</code> and
     * <code>are</code> methods are not supported.
     * </p>
     * 
     * @param bean
     *            the Java Bean to copy properties from.
     * @param propertyIds
     *            ids of the properties.
     */
    public BeanItem(BT bean, String[] propertyIds) {
        this(bean, Arrays.asList(propertyIds));
    }

    /**
     * <p>
     * Perform introspection on a Java Bean class to find its properties.
     * </p>
     * 
     * <p>
     * Note : This version only supports introspectable bean properties and
     * their getter and setter methods. Stand-alone <code>is</code> and
     * <code>are</code> methods are not supported.
     * </p>
     * 
     * @param beanClass
     *            the Java Bean class to get properties for.
     * @return an ordered map from property names to property descriptors
     */
    static LinkedHashMap<String, PropertyDescriptor> getPropertyDescriptors(
            final Class<?> beanClass) {
        final LinkedHashMap<String, PropertyDescriptor> pdMap = new LinkedHashMap<String, PropertyDescriptor>();

        // Try to introspect, if it fails, we just have an empty Item
        try {
            final BeanInfo info = Introspector.getBeanInfo(beanClass);
            final PropertyDescriptor[] pds = info.getPropertyDescriptors();

            // Add all the bean properties as MethodProperties to this Item
            for (int i = 0; i < pds.length; i++) {
                final Method getMethod = pds[i].getReadMethod();
                if ((getMethod != null)
                        && getMethod.getDeclaringClass() != Object.class) {
                    pdMap.put(pds[i].getName(), pds[i]);
                }
            }
        } catch (final java.beans.IntrospectionException ignored) {
        }

        return pdMap;
    }

    /**
     * Gets the underlying JavaBean object.
     * 
     * @return the bean object.
     */
    public BT getBean() {
        return bean;
    }

}
