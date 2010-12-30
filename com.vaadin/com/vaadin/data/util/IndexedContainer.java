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

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * An implementation of the <code>{@link Container.Indexed}</code> interface
 * with all important features.</p>
 * 
 * Features:
 * <ul>
 * <li> {@link Container.Indexed}
 * <li> {@link Container.Ordered}
 * <li> {@link Container.Sortable}
 * <li> {@link Container.Filterable}
 * <li> {@link Cloneable}
 * <li>Sends all needed events on content changes.
 * </ul>
 * 
 * @see com.vaadin.data.Container
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 3.0
 */

@SuppressWarnings("serial")
public class IndexedContainer implements Container.Indexed,
        Container.ItemSetChangeNotifier, Container.PropertySetChangeNotifier,
        Property.ValueChangeNotifier, Container.Sortable, Cloneable,
        Container.Filterable {

    /* Internal structure */

    /**
     * Linked list of ordered Item IDs.
     */
    private ArrayList<Object> itemIds = new ArrayList<Object>();

    /** List of item ids that passes the filtering */
    private LinkedHashSet<Object> filteredItemIds = null;

    /**
     * Linked list of ordered Property IDs.
     */
    private ArrayList<Object> propertyIds = new ArrayList<Object>();

    /**
     * Property ID to type mapping.
     */
    private Hashtable types = new Hashtable();

    /**
     * Hash of Items, where each Item is implemented as a mapping from Property
     * ID to Property value.
     */
    private Hashtable items = new Hashtable();

    /**
     * Set of properties that are read-only.
     */
    private HashSet readOnlyProperties = new HashSet();

    /**
     * List of all Property value change event listeners listening all the
     * properties.
     */
    private LinkedList propertyValueChangeListeners = null;

    /**
     * Data structure containing all listeners interested in changes to single
     * Properties. The data structure is a hashtable mapping Property IDs to a
     * hashtable that maps Item IDs to a linked list of listeners listening
     * Property identified by given Property ID and Item ID.
     */
    private Hashtable singlePropertyValueChangeListeners = null;

    /**
     * List of all Property set change event listeners.
     */
    private LinkedList propertySetChangeListeners = null;

    /**
     * List of all container Item set change event listeners.
     */
    private LinkedList itemSetChangeListeners = null;

    /**
     * The item sorter which is used for sorting the container.
     */
    private ItemSorter itemSorter = new DefaultItemSorter();

    /**
     * Filters that are applied to the container to limit the items visible in
     * it
     */
    private HashSet<Filter> filters;

    private HashMap<Object, Object> defaultPropertyValues;

    private int nextGeneratedItemId = 1;

    /* Container constructors */

    public IndexedContainer() {
    }

    public IndexedContainer(Collection itemIds) {
        if (items != null) {
            for (final Iterator i = itemIds.iterator(); i.hasNext();) {
                this.addItem(i.next());
            }
        }
    }

    /* Container methods */

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getItem(java.lang.Object)
     */
    public Item getItem(Object itemId) {

        if (itemId != null
                && items.containsKey(itemId)
                && (filteredItemIds == null || filteredItemIds.contains(itemId))) {
            return new IndexedContainerItem(itemId);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getItemIds()
     */
    public Collection getItemIds() {
        if (filteredItemIds != null) {
            return Collections.unmodifiableCollection(filteredItemIds);
        }
        return Collections.unmodifiableCollection(itemIds);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getContainerPropertyIds()
     */
    public Collection getContainerPropertyIds() {
        return Collections.unmodifiableCollection(propertyIds);
    }

    /**
     * Gets the type of a Property stored in the list.
     * 
     * @param id
     *            the ID of the Property.
     * @return Type of the requested Property
     */
    public Class getType(Object propertyId) {
        return (Class) types.get(propertyId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getContainerProperty(java.lang.Object,
     * java.lang.Object)
     */
    public Property getContainerProperty(Object itemId, Object propertyId) {
        if (itemId == null) {
            return null;
        } else if (filteredItemIds == null) {
            if (!items.containsKey(itemId)) {
                return null;
            }
        } else if (!filteredItemIds.contains(itemId)) {
            return null;
        }

        return new IndexedContainerProperty(itemId, propertyId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#size()
     */
    public int size() {
        if (filteredItemIds == null) {
            return itemIds.size();
        }
        return filteredItemIds.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#containsId(java.lang.Object)
     */
    public boolean containsId(Object itemId) {
        if (itemId == null) {
            return false;
        }
        if (filteredItemIds != null) {
            return filteredItemIds.contains(itemId);
        }
        return items.containsKey(itemId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#addContainerProperty(java.lang.Object,
     * java.lang.Class, java.lang.Object)
     */
    public boolean addContainerProperty(Object propertyId, Class<?> type,
            Object defaultValue) {

        // Fails, if nulls are given
        if (propertyId == null || type == null) {
            return false;
        }

        // Fails if the Property is already present
        if (propertyIds.contains(propertyId)) {
            return false;
        }

        // Adds the Property to Property list and types
        propertyIds.add(propertyId);
        types.put(propertyId, type);

        // If default value is given, set it
        if (defaultValue != null) {
            // for existing rows
            for (final Iterator i = itemIds.iterator(); i.hasNext();) {
                getItem(i.next()).getItemProperty(propertyId).setValue(
                        defaultValue);
            }
            // store for next rows
            if (defaultPropertyValues == null) {
                defaultPropertyValues = new HashMap<Object, Object>();
            }
            defaultPropertyValues.put(propertyId, defaultValue);
        }

        // Sends a change event
        fireContainerPropertySetChange();

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#removeAllItems()
     */
    public boolean removeAllItems() {

        // Removes all Items
        itemIds.clear();
        items.clear();
        if (filteredItemIds != null) {
            filteredItemIds.clear();
        }

        // Sends a change event
        fireContentsChange(-1);

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#addItem()
     */
    public Object addItem() {

        // Creates a new id
        final Object id = generateId();

        // Adds the Item into container
        addItem(id);

        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#addItem(java.lang.Object)
     */
    public Item addItem(Object itemId) {

        // Make sure that the Item is valid and has not been created yet
        if (itemId == null || items.containsKey(itemId)) {
            return null;
        }

        // Adds the Item to container (at the end of the unfiltered list)
        itemIds.add(itemId);
        Hashtable t = new Hashtable();
        items.put(itemId, t);

        addDefaultValues(t);

        // this optimization is why some code is duplicated with
        // addItemAtInternalIndex()
        if (filteredItemIds != null) {
            if (passesFilters(itemId)) {
                filteredItemIds.add(itemId);
            }
        }

        // Sends the event
        fireContentsChange(itemIds.size() - 1);

        return new IndexedContainerItem(itemId);
    }

    /**
     * Helper method to add default values for items if available
     * 
     * @param t
     *            data table of added item
     */
    private void addDefaultValues(Hashtable t) {
        if (defaultPropertyValues != null) {
            for (Object key : defaultPropertyValues.keySet()) {
                t.put(key, defaultPropertyValues.get(key));
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#removeItem(java.lang.Object)
     */
    public boolean removeItem(Object itemId) {
        if (itemId == null) {
            return false;
        }

        if (items.remove(itemId) == null) {
            return false;
        }
        itemIds.remove(itemId);
        if (filteredItemIds != null) {
            filteredItemIds.remove(itemId);
        }

        fireContentsChange(-1);

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#removeContainerProperty(java.lang.Object )
     */
    public boolean removeContainerProperty(Object propertyId) {

        // Fails if the Property is not present
        if (!propertyIds.contains(propertyId)) {
            return false;
        }

        // Removes the Property to Property list and types
        propertyIds.remove(propertyId);
        types.remove(propertyId);
        if (defaultPropertyValues != null) {
            defaultPropertyValues.remove(propertyId);
        }

        // If remove the Property from all Items
        for (final Iterator i = itemIds.iterator(); i.hasNext();) {
            ((Hashtable) items.get(i.next())).remove(propertyId);
        }

        // Sends a change event
        fireContainerPropertySetChange();

        return true;
    }

    /* Container.Ordered methods */

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#firstItemId()
     */
    public Object firstItemId() {
        try {
            if (filteredItemIds != null) {
                return filteredItemIds.iterator().next();
            }
            return itemIds.get(0);
        } catch (final IndexOutOfBoundsException e) {
        } catch (final NoSuchElementException e) {
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#lastItemId()
     */
    public Object lastItemId() {
        try {
            if (filteredItemIds != null) {
                final Iterator i = filteredItemIds.iterator();
                Object last = null;
                while (i.hasNext()) {
                    last = i.next();
                }
                return last;
            }
            return itemIds.get(itemIds.size() - 1);
        } catch (final IndexOutOfBoundsException e) {
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#nextItemId(java.lang.Object)
     */
    public Object nextItemId(Object itemId) {
        if (filteredItemIds != null) {
            if (itemId == null || !filteredItemIds.contains(itemId)) {
                return null;
            }
            final Iterator i = filteredItemIds.iterator();
            while (i.hasNext() && !itemId.equals(i.next())) {
                ;
            }
            if (i.hasNext()) {
                return i.next();
            }
            return null;
        }
        try {
            int idx = itemIds.indexOf(itemId);
            if (idx == -1) {
                // If the given Item is not found in the Container,
                // null is returned.
                return null;
            }
            return itemIds.get(idx + 1);
        } catch (final IndexOutOfBoundsException e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#prevItemId(java.lang.Object)
     */
    public Object prevItemId(Object itemId) {
        if (filteredItemIds != null) {
            if (!filteredItemIds.contains(itemId)) {
                return null;
            }
            final Iterator i = filteredItemIds.iterator();
            if (itemId == null) {
                return null;
            }
            Object prev = null;
            Object current;
            while (i.hasNext() && !itemId.equals(current = i.next())) {
                prev = current;
            }
            return prev;
        }
        try {
            return itemIds.get(itemIds.indexOf(itemId) - 1);
        } catch (final IndexOutOfBoundsException e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#isFirstId(java.lang.Object)
     */
    public boolean isFirstId(Object itemId) {
        if (filteredItemIds != null) {
            try {
                final Object first = filteredItemIds.iterator().next();
                return (itemId != null && itemId.equals(first));
            } catch (final NoSuchElementException e) {
                return false;
            }
        }
        return (size() >= 1 && itemIds.get(0).equals(itemId));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#isLastId(java.lang.Object)
     */
    public boolean isLastId(Object itemId) {
        if (filteredItemIds != null) {
            try {
                Object last = null;
                for (final Iterator i = filteredItemIds.iterator(); i.hasNext();) {
                    last = i.next();
                }
                return (itemId != null && itemId.equals(last));
            } catch (final NoSuchElementException e) {
                return false;
            }
        }
        final int s = size();
        return (s >= 1 && itemIds.get(s - 1).equals(itemId));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#addItemAfter(java.lang.Object,
     * java.lang.Object)
     */
    public Item addItemAfter(Object previousItemId, Object newItemId) {
        // Adding an item after null item adds the item as first item of the
        // ordered container.
        if (previousItemId == null) {
            return addItemAt(0, newItemId);
        }

        // Get the index of the addition
        int index = -1;
        if (previousItemId != null) {
            index = 1 + indexOfId(previousItemId);
            if (index <= 0 || index > size()) {
                return null;
            }
        }
        return addItemAt(index, newItemId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#addItemAfter(java.lang.Object)
     */
    public Object addItemAfter(Object previousItemId) {

        // Creates a new id
        final Object id = generateId();

        if (addItemAfter(previousItemId, id) != null) {
            return id;
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Indexed#getIdByIndex(int)
     */
    public Object getIdByIndex(int index) {

        if (filteredItemIds != null) {
            if (index < 0) {
                throw new IndexOutOfBoundsException();
            }
            try {
                final Iterator i = filteredItemIds.iterator();
                while (index-- > 0) {
                    i.next();
                }
                return i.next();
            } catch (final NoSuchElementException e) {
                throw new IndexOutOfBoundsException();
            }
        }

        return itemIds.get(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Indexed#indexOfId(java.lang.Object)
     */
    public int indexOfId(Object itemId) {
        if (filteredItemIds != null) {
            int index = 0;
            if (itemId == null) {
                return -1;
            }
            final Iterator i = filteredItemIds.iterator();
            while (i.hasNext()) {
                Object id = i.next();
                if (itemId.equals(id)) {
                    return index;
                }
                index++;
            }
            return -1;
        }
        return itemIds.indexOf(itemId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Indexed#addItemAt(int, java.lang.Object)
     */
    public Item addItemAt(int index, Object newItemId) {

        // add item based on a filtered index
        int internalIndex = -1;
        if (filteredItemIds == null) {
            internalIndex = index;
        } else if (index == 0) {
            internalIndex = 0;
        } else if (index == size()) {
            // add just after the last item
            Object id = getIdByIndex(index - 1);
            internalIndex = itemIds.indexOf(id) + 1;
        } else if (index > 0 && index < size()) {
            // map the index of the visible item to its unfiltered index
            Object id = getIdByIndex(index);
            internalIndex = itemIds.indexOf(id);
        }
        if (internalIndex >= 0) {
            return addItemAtInternalIndex(internalIndex, newItemId);
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Indexed#addItemAt(int)
     */
    public Object addItemAt(int index) {

        // Creates a new id
        final Object id = generateId();

        // Adds the Item into container
        addItemAt(index, id);

        return id;
    }

    /**
     * Generates an unique identifier for use as an item id. Guarantees that the
     * generated id is not currently used as an id.
     * 
     * @return
     */
    private Serializable generateId() {
        Serializable id;
        do {
            id = Integer.valueOf(nextGeneratedItemId++);
        } while (items.containsKey(id));

        return id;
    }

    /* Event notifiers */

    /**
     * Adds new item at given index of the internal (unfiltered) list.
     * <p>
     * The item is also added in the visible part of the list if it passes the
     * filters.
     * </p>
     * 
     * @param index
     *            Internal index to add the new item.
     * @param newItemId
     *            Id of the new item to be added.
     * @return Returns new item or null if the operation fails.
     */
    private Item addItemAtInternalIndex(int index, Object newItemId) {
        // Make sure that the Item is valid and has not been created yet
        if (index < 0 || index > itemIds.size() || newItemId == null
                || items.containsKey(newItemId)) {
            return null;
        }

        // Adds the Item to container
        itemIds.add(index, newItemId);
        Hashtable t = new Hashtable();
        items.put(newItemId, t);
        addDefaultValues(t);

        if (filteredItemIds != null) {
            // when the item data is set later (IndexedContainerProperty),
            // filtering is updated
            updateContainerFiltering();
        } else {
            fireContentsChange(index);
        }

        return new IndexedContainerItem(newItemId);
    }

    /**
     * An <code>event</code> object specifying the list whose Property set has
     * changed.
     * 
     * @author IT Mill Ltd.
     * @version
     * 6.4.8
     * @since 3.0
     */
    private class PropertySetChangeEvent extends EventObject implements
            Container.PropertySetChangeEvent, Serializable {

        private PropertySetChangeEvent(IndexedContainer source) {
            super(source);
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.vaadin.data.Container.PropertySetChangeEvent#getContainer ()
         */
        public Container getContainer() {
            return (Container) getSource();
        }
    }

    /**
     * An <code>event</code> object specifying the list whose Item set has
     * changed.
     * 
     * @author IT Mill Ltd.
     * @version
     * 6.4.8
     * @since 3.0
     */
    public class ItemSetChangeEvent extends EventObject implements
            Container.ItemSetChangeEvent, Serializable {

        private final int addedItemIndex;

        private ItemSetChangeEvent(IndexedContainer source, int addedItemIndex) {
            super(source);
            this.addedItemIndex = addedItemIndex;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.vaadin.data.Container.ItemSetChangeEvent#getContainer()
         */
        public Container getContainer() {
            return (Container) getSource();
        }

        /**
         * Iff one item is added, gives its index.
         * 
         * @return -1 if either multiple items are changed or some other change
         *         than add is done.
         */
        public int getAddedItemIndex() {
            return addedItemIndex;
        }

    }

    /**
     * An <code>event</code> object specifying the Property in a list whose
     * value has changed.
     * 
     * @author IT Mill Ltd.
     * @version
     * 6.4.8
     * @since 3.0
     */
    private class PropertyValueChangeEvent extends EventObject implements
            Property.ValueChangeEvent, Serializable {

        private PropertyValueChangeEvent(Property source) {
            super(source);
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.vaadin.data.Property.ValueChangeEvent#getProperty()
         */
        public Property getProperty() {
            return (Property) getSource();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.PropertySetChangeNotifier#addListener
     * (com.vaadin.data.Container.PropertySetChangeListener)
     */
    public void addListener(Container.PropertySetChangeListener listener) {
        if (propertySetChangeListeners == null) {
            propertySetChangeListeners = new LinkedList();
        }
        propertySetChangeListeners.add(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.PropertySetChangeNotifier#removeListener
     * (com.vaadin.data.Container.PropertySetChangeListener)
     */
    public void removeListener(Container.PropertySetChangeListener listener) {
        if (propertySetChangeListeners != null) {
            propertySetChangeListeners.remove(listener);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.ItemSetChangeNotifier#addListener(com
     * .vaadin.data.Container.ItemSetChangeListener)
     */
    public void addListener(Container.ItemSetChangeListener listener) {
        if (itemSetChangeListeners == null) {
            itemSetChangeListeners = new LinkedList();
        }
        itemSetChangeListeners.add(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.ItemSetChangeNotifier#removeListener
     * (com.vaadin.data.Container.ItemSetChangeListener)
     */
    public void removeListener(Container.ItemSetChangeListener listener) {
        if (itemSetChangeListeners != null) {
            itemSetChangeListeners.remove(listener);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Property.ValueChangeNotifier#addListener(com.
     * vaadin.data.Property.ValueChangeListener)
     */
    public void addListener(Property.ValueChangeListener listener) {
        if (propertyValueChangeListeners == null) {
            propertyValueChangeListeners = new LinkedList();
        }
        propertyValueChangeListeners.add(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Property.ValueChangeNotifier#removeListener(com
     * .vaadin.data.Property.ValueChangeListener)
     */
    public void removeListener(Property.ValueChangeListener listener) {
        if (propertyValueChangeListeners != null) {
            propertyValueChangeListeners.remove(listener);
        }
    }

    /**
     * Sends a Property value change event to all interested listeners.
     * 
     * @param source
     *            the IndexedContainerProperty object.
     */
    private void firePropertyValueChange(IndexedContainerProperty source) {

        // Sends event to listeners listening all value changes
        if (propertyValueChangeListeners != null) {
            final Object[] l = propertyValueChangeListeners.toArray();
            final Property.ValueChangeEvent event = new IndexedContainer.PropertyValueChangeEvent(
                    source);
            for (int i = 0; i < l.length; i++) {
                ((Property.ValueChangeListener) l[i]).valueChange(event);
            }
        }

        // Sends event to single property value change listeners
        if (singlePropertyValueChangeListeners != null) {
            final Hashtable propertySetToListenerListMap = (Hashtable) singlePropertyValueChangeListeners
                    .get(source.propertyId);
            if (propertySetToListenerListMap != null) {
                final LinkedList listenerList = (LinkedList) propertySetToListenerListMap
                        .get(source.itemId);
                if (listenerList != null) {
                    final Property.ValueChangeEvent event = new IndexedContainer.PropertyValueChangeEvent(
                            source);
                    Object[] listeners = listenerList.toArray();
                    for (int i = 0; i < listeners.length; i++) {
                        ((Property.ValueChangeListener) listeners[i])
                                .valueChange(event);
                    }
                }
            }
        }

    }

    /**
     * Sends a Property set change event to all interested listeners.
     */
    private void fireContainerPropertySetChange() {
        if (propertySetChangeListeners != null) {
            final Object[] l = propertySetChangeListeners.toArray();
            final Container.PropertySetChangeEvent event = new IndexedContainer.PropertySetChangeEvent(
                    this);
            for (int i = 0; i < l.length; i++) {
                ((Container.PropertySetChangeListener) l[i])
                        .containerPropertySetChange(event);
            }
        }
    }

    /**
     * Sends Item set change event to all registered interested listeners.
     * 
     * @param addedItemIndex
     *            index of new item if change event was an item addition
     */
    protected void fireContentsChange(int addedItemIndex) {
        if (itemSetChangeListeners != null) {
            final Object[] l = itemSetChangeListeners.toArray();
            final Container.ItemSetChangeEvent event = new IndexedContainer.ItemSetChangeEvent(
                    this, addedItemIndex);
            for (int i = 0; i < l.length; i++) {
                ((Container.ItemSetChangeListener) l[i])
                        .containerItemSetChange(event);
            }
        }
    }

    /**
     * Adds new single Property change listener.
     * 
     * @param propertyId
     *            the ID of the Property to add.
     * @param itemId
     *            the ID of the Item .
     * @param listener
     *            the listener to be added.
     */
    private void addSinglePropertyChangeListener(Object propertyId,
            Object itemId, Property.ValueChangeListener listener) {
        if (listener != null) {
            if (singlePropertyValueChangeListeners == null) {
                singlePropertyValueChangeListeners = new Hashtable();
            }
            Hashtable propertySetToListenerListMap = (Hashtable) singlePropertyValueChangeListeners
                    .get(propertyId);
            if (propertySetToListenerListMap == null) {
                propertySetToListenerListMap = new Hashtable();
                singlePropertyValueChangeListeners.put(propertyId,
                        propertySetToListenerListMap);
            }
            LinkedList listenerList = (LinkedList) propertySetToListenerListMap
                    .get(itemId);
            if (listenerList == null) {
                listenerList = new LinkedList();
                propertySetToListenerListMap.put(itemId, listenerList);
            }
            listenerList.addLast(listener);
        }
    }

    /**
     * Removes a previously registered single Property change listener.
     * 
     * @param propertyId
     *            the ID of the Property to remove.
     * @param itemId
     *            the ID of the Item.
     * @param listener
     *            the listener to be removed.
     */
    private void removeSinglePropertyChangeListener(Object propertyId,
            Object itemId, Property.ValueChangeListener listener) {
        if (listener != null && singlePropertyValueChangeListeners != null) {
            final Hashtable propertySetToListenerListMap = (Hashtable) singlePropertyValueChangeListeners
                    .get(propertyId);
            if (propertySetToListenerListMap != null) {
                final LinkedList listenerList = (LinkedList) propertySetToListenerListMap
                        .get(itemId);
                if (listenerList != null) {
                    listenerList.remove(listener);
                    if (listenerList.isEmpty()) {
                        propertySetToListenerListMap.remove(itemId);
                    }
                }
                if (propertySetToListenerListMap.isEmpty()) {
                    singlePropertyValueChangeListeners.remove(propertyId);
                }
            }
            if (singlePropertyValueChangeListeners.isEmpty()) {
                singlePropertyValueChangeListeners = null;
            }
        }
    }

    /* Internal Item and Property implementations */

    /*
     * A class implementing the com.vaadin.data.Item interface to be contained
     * in the list.
     * 
     * @author IT Mill Ltd.
     * 
     * @version 6.4.8
     * 
     * @since 3.0
     */
    class IndexedContainerItem implements Item {

        /**
         * Item ID in the host container for this Item.
         */
        private final Object itemId;

        /**
         * Constructs a new ListItem instance and connects it to a host
         * container.
         * 
         * @param itemId
         *            the Item ID of the new Item.
         */
        private IndexedContainerItem(Object itemId) {

            // Gets the item contents from the host
            if (itemId == null) {
                throw new NullPointerException();
            }
            this.itemId = itemId;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.vaadin.data.Item#getItemProperty(java.lang.Object)
         */
        public Property getItemProperty(Object id) {
            return new IndexedContainerProperty(itemId, id);
        }

        public Collection getItemPropertyIds() {
            return Collections.unmodifiableCollection(propertyIds);
        }

        /**
         * Gets the <code>String</code> representation of the contents of the
         * Item. The format of the string is a space separated catenation of the
         * <code>String</code> representations of the Properties contained by
         * the Item.
         * 
         * @return <code>String</code> representation of the Item contents
         */
        @Override
        public String toString() {
            String retValue = "";

            for (final Iterator i = propertyIds.iterator(); i.hasNext();) {
                final Object propertyId = i.next();
                retValue += getItemProperty(propertyId).toString();
                if (i.hasNext()) {
                    retValue += " ";
                }
            }

            return retValue;
        }

        /**
         * Calculates a integer hash-code for the Item that's unique inside the
         * list. Two Items inside the same list have always different
         * hash-codes, though Items in different lists may have identical
         * hash-codes.
         * 
         * @return A locally unique hash-code as integer
         */
        @Override
        public int hashCode() {
            return itemId.hashCode();
        }

        /**
         * Tests if the given object is the same as the this object. Two Items
         * got from a list container with the same ID are equal.
         * 
         * @param obj
         *            an object to compare with this object
         * @return <code>true</code> if the given object is the same as this
         *         object, <code>false</code> if not
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null
                    || !obj.getClass().equals(IndexedContainerItem.class)) {
                return false;
            }
            final IndexedContainerItem li = (IndexedContainerItem) obj;
            return getHost() == li.getHost() && itemId.equals(li.itemId);
        }

        private IndexedContainer getHost() {
            return IndexedContainer.this;
        }

        /**
         * IndexedContainerItem does not support adding new properties. Add
         * properties at container level. See
         * {@link IndexedContainer#addContainerProperty(Object, Class, Object)}
         * 
         * @see com.vaadin.data.Item#addProperty(Object, Property)
         */
        public boolean addItemProperty(Object id, Property property)
                throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Indexed container item "
                    + "does not support adding new properties");
        }

        /**
         * Indexed container does not support removing properties. Remove
         * properties at container level. See
         * {@link IndexedContainer#removeContainerProperty(Object)}
         * 
         * @see com.vaadin.data.Item#removeProperty(Object)
         */
        public boolean removeItemProperty(Object id)
                throws UnsupportedOperationException {
            throw new UnsupportedOperationException(
                    "Indexed container item does not support property removal");
        }

    }

    /**
     * A class implementing the {@link Property} interface to be contained in
     * the {@link IndexedContainerItem} contained in the
     * {@link IndexedContainer}.
     * 
     * @author IT Mill Ltd.
     * 
     * @version
     * 6.4.8
     * @since 3.0
     */
    private class IndexedContainerProperty implements Property,
            Property.ValueChangeNotifier {

        /**
         * ID of the Item, where this property resides.
         */
        private final Object itemId;

        /**
         * Id of the Property.
         */
        private final Object propertyId;

        /**
         * Constructs a new {@link IndexedContainerProperty} object.
         * 
         * @param itemId
         *            the ID of the Item to connect the new Property to.
         * @param propertyId
         *            the Property ID of the new Property.
         * @param host
         *            the list that contains the Item to contain the new
         *            Property.
         */
        private IndexedContainerProperty(Object itemId, Object propertyId) {
            if (itemId == null || propertyId == null) {
                // Null ids are not accepted
                throw new NullPointerException(
                        "Container item or property ids can not be null");
            }
            this.propertyId = propertyId;
            this.itemId = itemId;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.vaadin.data.Property#getType()
         */
        public Class getType() {
            return (Class) types.get(propertyId);
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.vaadin.data.Property#getValue()
         */
        public Object getValue() {
            return ((Hashtable) items.get(itemId)).get(propertyId);
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.vaadin.data.Property#isReadOnly()
         */
        public boolean isReadOnly() {
            return readOnlyProperties.contains(this);
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.vaadin.data.Property#setReadOnly(boolean)
         */
        public void setReadOnly(boolean newStatus) {
            if (newStatus) {
                readOnlyProperties.add(this);
            } else {
                readOnlyProperties.remove(this);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.vaadin.data.Property#setValue(java.lang.Object)
         */
        public void setValue(Object newValue)
                throws Property.ReadOnlyException, Property.ConversionException {

            // Gets the Property set
            final Hashtable propertySet = (Hashtable) items.get(itemId);

            // Support null values on all types
            if (newValue == null) {
                propertySet.remove(propertyId);
            } else if (getType().isAssignableFrom(newValue.getClass())) {
                propertySet.put(propertyId, newValue);
            } else {
                try {

                    // Gets the string constructor
                    final Constructor constr = getType().getConstructor(
                            new Class[] { String.class });

                    // Creates new object from the string
                    propertySet.put(propertyId, constr
                            .newInstance(new Object[] { newValue.toString() }));

                } catch (final java.lang.Exception e) {
                    throw new Property.ConversionException(
                            "Conversion for value '" + newValue + "' of class "
                                    + newValue.getClass().getName() + " to "
                                    + getType().getName() + " failed");
                }
            }

            // update the container filtering if this property is being filtered
            updateContainerFiltering(propertyId);

            firePropertyValueChange(this);
        }

        /**
         * Returns the value of the Property in human readable textual format.
         * The return value should be assignable to the <code>setValue</code>
         * method if the Property is not in read-only mode.
         * 
         * @return <code>String</code> representation of the value stored in the
         *         Property
         */
        @Override
        public String toString() {
            final Object value = getValue();
            if (value == null) {
                return null;
            }
            return value.toString();
        }

        /**
         * Calculates a integer hash-code for the Property that's unique inside
         * the Item containing the Property. Two different Properties inside the
         * same Item contained in the same list always have different
         * hash-codes, though Properties in different Items may have identical
         * hash-codes.
         * 
         * @return A locally unique hash-code as integer
         */
        @Override
        public int hashCode() {
            return itemId.hashCode() ^ propertyId.hashCode();
        }

        /**
         * Tests if the given object is the same as the this object. Two
         * Properties got from an Item with the same ID are equal.
         * 
         * @param obj
         *            an object to compare with this object
         * @return <code>true</code> if the given object is the same as this
         *         object, <code>false</code> if not
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null
                    || !obj.getClass().equals(IndexedContainerProperty.class)) {
                return false;
            }
            final IndexedContainerProperty lp = (IndexedContainerProperty) obj;
            return lp.getHost() == getHost()
                    && lp.propertyId.equals(propertyId)
                    && lp.itemId.equals(itemId);
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.vaadin.data.Property.ValueChangeNotifier#addListener(
         * com.vaadin.data.Property.ValueChangeListener)
         */
        public void addListener(Property.ValueChangeListener listener) {
            addSinglePropertyChangeListener(propertyId, itemId, listener);
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.vaadin.data.Property.ValueChangeNotifier#removeListener
         * (com.vaadin.data.Property.ValueChangeListener)
         */
        public void removeListener(Property.ValueChangeListener listener) {
            removeSinglePropertyChangeListener(propertyId, itemId, listener);
        }

        private IndexedContainer getHost() {
            return IndexedContainer.this;
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Sortable#sort(java.lang.Object[],
     * boolean[])
     */
    public void sort(Object[] propertyId, boolean[] ascending) {
        // Set up the item sorter for the sort operation
        itemSorter.setSortProperties(this, propertyId, ascending);

        // Perform the actual sort
        doSort();

        // Post sort updates
        if (filteredItemIds != null) {
            updateContainerFiltering();
        } else {
            fireContentsChange(-1);
        }

    }

    /**
     * Perform the sorting of the data structures in the container. This is
     * invoked when the <code>itemSorter</code> has been prepared for the sort
     * operation. Typically this method calls
     * <code>Collections.sort(aCollection, getItemSorter())</code> on all arrays
     * (containing item ids) that need to be sorted.
     * 
     */
    protected void doSort() {
        Collections.sort(itemIds, getItemSorter());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Sortable#getSortableContainerPropertyIds
     * ()
     */
    public Collection<?> getSortableContainerPropertyIds() {

        final LinkedList list = new LinkedList();
        for (final Iterator i = propertyIds.iterator(); i.hasNext();) {
            final Object id = i.next();
            final Class type = getType(id);
            if (type != null && Comparable.class.isAssignableFrom(type)) {
                list.add(id);
            }
        }

        return list;
    }

    /**
     * Returns the ItemSorter used for comparing items in a sort. See
     * {@link #setItemSorter(ItemSorter)} for more information.
     * 
     * @return The ItemSorter used for comparing two items in a sort.
     */
    public ItemSorter getItemSorter() {
        return itemSorter;
    }

    /**
     * Sets the ItemSorter used for comparing items in a sort. The ItemSorter is
     * called for each collection that needs sorting. A default ItemSorter is
     * used if this is not explicitly set.
     * 
     * @param itemSorter
     *            The ItemSorter used for comparing two items in a sort.
     */
    public void setItemSorter(ItemSorter itemSorter) {
        this.itemSorter = itemSorter;
    }

    /**
     * Supports cloning of the IndexedContainer cleanly.
     * 
     * @throws CloneNotSupportedException
     *             if an object cannot be cloned. .
     */
    @Override
    public Object clone() throws CloneNotSupportedException {

        // Creates the clone
        final IndexedContainer nc = new IndexedContainer();

        // Clone the shallow properties
        nc.itemIds = itemIds != null ? (ArrayList) itemIds.clone() : null;
        nc.itemSetChangeListeners = itemSetChangeListeners != null ? (LinkedList) itemSetChangeListeners
                .clone() : null;
        nc.propertyIds = propertyIds != null ? (ArrayList) propertyIds.clone()
                : null;
        nc.propertySetChangeListeners = propertySetChangeListeners != null ? (LinkedList) propertySetChangeListeners
                .clone() : null;
        nc.propertyValueChangeListeners = propertyValueChangeListeners != null ? (LinkedList) propertyValueChangeListeners
                .clone() : null;
        nc.readOnlyProperties = readOnlyProperties != null ? (HashSet) readOnlyProperties
                .clone() : null;
        nc.singlePropertyValueChangeListeners = singlePropertyValueChangeListeners != null ? (Hashtable) singlePropertyValueChangeListeners
                .clone() : null;

        nc.types = types != null ? (Hashtable) types.clone() : null;

        nc.filters = filters == null ? null : (HashSet<Filter>) filters.clone();

        nc.filteredItemIds = filteredItemIds == null ? null
                : (LinkedHashSet) filteredItemIds.clone();

        // Clone property-values
        if (items == null) {
            nc.items = null;
        } else {
            nc.items = new Hashtable();
            for (final Iterator i = items.keySet().iterator(); i.hasNext();) {
                final Object id = i.next();
                final Hashtable it = (Hashtable) items.get(id);
                nc.items.put(id, it.clone());
            }
        }

        return nc;
    }

    public void addContainerFilter(Object propertyId, String filterString,
            boolean ignoreCase, boolean onlyMatchPrefix) {
        if (filters == null) {
            filters = new HashSet<Filter>();
        }
        filters.add(new Filter(propertyId, filterString, ignoreCase,
                onlyMatchPrefix));
        updateContainerFiltering();
    }

    public void removeAllContainerFilters() {
        if (filters == null) {
            return;
        }
        filters.clear();
        updateContainerFiltering();
    }

    public void removeContainerFilters(Object propertyId) {
        if (filters == null || propertyId == null) {
            return;
        }
        final Iterator<Filter> i = filters.iterator();
        while (i.hasNext()) {
            final Filter f = i.next();
            if (propertyId.equals(f.propertyId)) {
                i.remove();
            }
        }
        updateContainerFiltering();
    }

    private void updateContainerFiltering(Object propertyId) {
        if (filters == null || propertyId == null) {
            return;
        }
        // update container filtering if there is a filter for the given
        // property
        final Iterator<Filter> i = filters.iterator();
        while (i.hasNext()) {
            final Filter f = i.next();
            if (propertyId.equals(f.propertyId)) {
                updateContainerFiltering();
                return;
            }
        }
    }

    /**
     * Called when the filters have changed or when another event that effects
     * filtering has taken place. Updates internal data structures and fires an
     * item set change if necessary.
     */
    private void updateContainerFiltering() {

        // Clearing filters?
        boolean hasFilters = (filters != null && !filters.isEmpty());

        if (doFilterContainer(hasFilters)) {
            fireContentsChange(-1);
        }
    }

    /**
     * Filters the data in the container and updates internal data structures.
     * This method should reset any internal data structures and then repopulate
     * them so {@link #getItemIds()} and other methods only return the filtered
     * items.
     * 
     * @param hasFilters
     *            true if filters has been set for the container, false
     *            otherwise
     * @return true if the item set has changed as a result of the filtering
     */
    protected boolean doFilterContainer(boolean hasFilters) {
        if (!hasFilters) {
            filteredItemIds = null;
            if (filters != null) {
                filters = null;
                return true;
            }

            return false;
        }
        // Reset filtered list
        if (filteredItemIds == null) {
            filteredItemIds = new LinkedHashSet();
        } else {
            filteredItemIds.clear();
        }

        // Filter
        for (final Iterator i = itemIds.iterator(); i.hasNext();) {
            final Object id = i.next();
            if (passesFilters(id)) {
                filteredItemIds.add(id);
            }
        }

        return true;

    }

    /**
     * Checks if the given itemId passes the filters set for the container. The
     * caller should make sure the itemId exists in the container. For
     * non-existing itemIds the behavior is undefined.
     * 
     * @param itemId
     *            An itemId that exists in the container.
     * @return true if the itemId passes all filters or no filters are set,
     *         false otherwise.
     */
    protected boolean passesFilters(Object itemId) {
        IndexedContainerItem item = new IndexedContainerItem(itemId);
        if (filters == null) {
            return true;
        }
        if (item == null) {
            return false;
        }
        final Iterator<Filter> i = filters.iterator();
        while (i.hasNext()) {
            final Filter f = i.next();
            if (!f.passesFilter(item)) {
                return false;
            }
        }
        return true;
    }

}
