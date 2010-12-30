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

package com.vaadin.data;

import java.io.Serializable;
import java.util.Collection;

/**
 * <p>
 * A specialized set of identified Items. Basically the Container is a set of
 * Items, but it imposes certain constraints on its contents. These constraints
 * state the following:
 * </p>
 * 
 * <ul>
 * <li>All Items in the Container must include the same number of Properties
 * <li>All Items in the Container must include the same Property ID sets (see
 * {@link Item#getItemPropertyIds()}).
 * <li>all Properties in the Items corresponding to the same Property ID must
 * have the same data type.
 * <li>All Items within a container are uniquely identified by their non-null
 * ids
 * </ul>
 * 
 * <p>
 * The Container can be visualized as a representation of a relational database
 * table. Each Item in the Container represents a row in the table, and all
 * cells in a column (identified by a Property ID) have the same data type. Note
 * that as with the cells in a database table, no Property in a Container may be
 * empty, though they may contain <code>null</code> values.
 * </p>
 * 
 * <p>
 * Note that though uniquely identified, the Items in a Container are not
 * neccessarily {@link Container.Ordered ordered}or {@link Container.Indexed
 * indexed}.
 * </p>
 * 
 * <p>
 * <img src=doc-files/Container_full.gif>
 * </p>
 * 
 * <p>
 * The Container interface is split to several subinterfaces so that a class can
 * implement only the ones it needs.
 * </p>
 * 
 * @author IT Mill Ltd
 * @version
 * 6.4.8
 * @since 3.0
 */
public interface Container extends Serializable {

    /**
     * Gets the Item with the given Item ID from the Container. If the Container
     * does not contain the requested Item, <code>null</code> is returned.
     * 
     * @param itemId
     *            ID of the Item to retrieve
     * @return the Item with the given ID or <code>null</code> if the Item is
     *         not found in the Container
     */
    public Item getItem(Object itemId);

    /**
     * Gets the ID's of all Properties stored in the Container. The ID's are
     * returned as a unmodifiable collection.
     * 
     * @return unmodifiable collection of Property IDs
     */
    public Collection<?> getContainerPropertyIds();

    /**
     * Gets the ID's of all Items stored in the Container. The ID's are returned
     * as a unmodifiable collection.
     * 
     * @return unmodifiable collection of Item IDs
     */
    public Collection<?> getItemIds();

    /**
     * Gets the Property identified by the given itemId and propertyId from the
     * Container. If the Container does not contain the Property,
     * <code>null</code> is returned.
     * 
     * @param itemId
     *            ID of the Item which contains the Property
     * @param propertyId
     *            ID of the Property to retrieve
     * @return Property with the given ID or <code>null</code>
     */
    public Property getContainerProperty(Object itemId, Object propertyId);

    /**
     * Gets the data type of all Properties identified by the given Property ID.
     * 
     * @param propertyId
     *            ID identifying the Properties
     * @return data type of the Properties
     */
    public Class<?> getType(Object propertyId);

    /**
     * Gets the number of Items in the Container.
     * 
     * @return number of Items in the Container
     */
    public int size();

    /**
     * Tests if the Container contains the specified Item
     * 
     * @param itemId
     *            ID the of Item to be tested
     * @return boolean indicating if the Container holds the specified Item
     */
    public boolean containsId(Object itemId);

    /**
     * Creates a new Item with the given ID into the Container. The new
     * <p>
     * Item is returned, and it is ready to have its Properties modified.
     * Returns <code>null</code> if the operation fails or the Container already
     * contains a Item with the given ID.
     * </p>
     * 
     * <p>
     * This functionality is optional.
     * </p>
     * 
     * @param itemId
     *            ID of the Item to be created
     * @return Created new Item, or <code>null</code> in case of a failure
     */
    public Item addItem(Object itemId) throws UnsupportedOperationException;

    /**
     * Creates a new Item into the Container, and assign it an automatic ID.
     * 
     * <p>
     * The new ID is returned, or <code>null</code> if the operation fails.
     * After a successful call you can use the {@link #getItem(Object ItemId)
     * <code>getItem</code>}method to fetch the Item.
     * </p>
     * 
     * <p>
     * This functionality is optional.
     * </p>
     * 
     * @return ID of the newly created Item, or <code>null</code> in case of a
     *         failure
     */
    public Object addItem() throws UnsupportedOperationException;

    /**
     * Removes the Item identified by <code>ItemId</code> from the Container.
     * This functionality is optional.
     * 
     * @param itemId
     *            ID of the Item to remove
     * @return <code>true</code> if the operation succeeded, <code>false</code>
     *         if not
     */
    public boolean removeItem(Object itemId)
            throws UnsupportedOperationException;

    /**
     * Adds a new Property to all Items in the Container. The Property ID, data
     * type and default value of the new Property are given as parameters.
     * 
     * This functionality is optional.
     * 
     * @param propertyId
     *            ID of the Property
     * @param type
     *            Data type of the new Property
     * @param defaultValue
     *            The value all created Properties are initialized to
     * @return <code>true</code> if the operation succeeded, <code>false</code>
     *         if not
     */
    public boolean addContainerProperty(Object propertyId, Class<?> type,
            Object defaultValue) throws UnsupportedOperationException;

    /**
     * Removes a Property specified by the given Property ID from the Container.
     * Note that the Property will be removed from all Items in the Container.
     * 
     * This functionality is optional.
     * 
     * @param propertyId
     *            ID of the Property to remove
     * @return <code>true</code> if the operation succeeded, <code>false</code>
     *         if not
     */
    public boolean removeContainerProperty(Object propertyId)
            throws UnsupportedOperationException;

    /**
     * Removes all Items from the Container.
     * 
     * <p>
     * Note that Property ID and type information is preserved. This
     * functionality is optional.
     * </p>
     * 
     * @return <code>true</code> if the operation succeeded, <code>false</code>
     *         if not
     */
    public boolean removeAllItems() throws UnsupportedOperationException;

    /**
     * Interface for Container classes whose Items can be traversed in order.
     */
    public interface Ordered extends Container {

        /**
         * Gets the ID of the Item following the Item that corresponds to
         * <code>itemId</code>. If the given Item is the last or not found in
         * the Container, <code>null</code> is returned.
         * 
         * @param itemId
         *            ID of an Item in the Container
         * @return ID of the next Item or <code>null</code>
         */
        public Object nextItemId(Object itemId);

        /**
         * Gets the ID of the Item preceding the Item that corresponds to
         * <code>itemId</code>. If the given Item is the first or not found in
         * the Container, <code>null</code> is returned.
         * 
         * @param itemId
         *            ID of an Item in the Container
         * @return ID of the previous Item or <code>null</code>
         */
        public Object prevItemId(Object itemId);

        /**
         * Gets the ID of the first Item in the Container.
         * 
         * @return ID of the first Item in the Container
         */
        public Object firstItemId();

        /**
         * Gets the ID of the last Item in the Container..
         * 
         * @return ID of the last Item in the Container
         */
        public Object lastItemId();

        /**
         * Tests if the Item corresponding to the given Item ID is the first
         * Item in the Container.
         * 
         * @param itemId
         *            ID of an Item in the Container
         * @return <code>true</code> if the Item is first in the Container,
         *         <code>false</code> if not
         */
        public boolean isFirstId(Object itemId);

        /**
         * Tests if the Item corresponding to the given Item ID is the last Item
         * in the Container.
         * 
         * @return <code>true</code> if the Item is last in the Container,
         *         <code>false</code> if not
         */
        public boolean isLastId(Object itemId);

        /**
         * Adds new item after the given item.
         * <p>
         * Adding an item after null item adds the item as first item of the
         * ordered container.
         * </p>
         * 
         * @param previousItemId
         *            Id of the previous item in ordered container.
         * @return Returns item id the the created new item or null if the
         *         operation fails.
         */
        public Object addItemAfter(Object previousItemId)
                throws UnsupportedOperationException;

        /**
         * Adds new item after the given item.
         * <p>
         * Adding an item after null item adds the item as first item of the
         * ordered container.
         * </p>
         * 
         * @param previousItemId
         *            Id of the previous item in ordered container.
         * @param newItemId
         *            Id of the new item to be added.
         * @return Returns new item or null if the operation fails.
         */
        public Item addItemAfter(Object previousItemId, Object newItemId)
                throws UnsupportedOperationException;

    }

    /** Interface for Container classes whose Items can be sorted. */
    public interface Sortable extends Ordered {

        /**
         * Sort method.
         * 
         * Sorts the container items.
         * 
         * @param propertyId
         *            Array of container property IDs, which values are used to
         *            sort the items in container as primary, secondary, ...
         *            sorting criterion. All of the item IDs must be in the
         *            collection returned by
         *            <code>getSortableContainerPropertyIds</code>
         * @param ascending
         *            Array of sorting order flags corresponding to each
         *            property ID used in sorting. If this array is shorter than
         *            propertyId array, ascending order is assumed for items
         *            where the order is not specified. Use <code>true</code> to
         *            sort in ascending order, <code>false</code> to use
         *            descending order.
         */
        void sort(Object[] propertyId, boolean[] ascending);

        /**
         * Gets the container property IDs, which can be used to sort the item.
         * 
         * @return The sortable field ids.
         */
        Collection<?> getSortableContainerPropertyIds();

    }

    /** Interface for Container classes whose Items can be indexed. */
    public interface Indexed extends Ordered {

        /**
         * Gets the index of the Item corresponding to the itemId. The following
         * is <code>true</code> for the returned index: 0 <= index < size().
         * 
         * @param itemId
         *            ID of an Item in the Container
         * @return index of the Item, or -1 if the Container does not include
         *         the Item
         */
        public int indexOfId(Object itemId);

        /**
         * Gets the ID of an Item by an index number.
         * 
         * @param index
         *            Index of the requested id in the Container
         * @return ID of the Item in the given index
         */
        public Object getIdByIndex(int index);

        /**
         * Adds new item at given index.
         * <p>
         * The indexes of the item currently in the given position and all the
         * following items are incremented.
         * </p>
         * 
         * @param index
         *            Index to add the new item.
         * @return Returns item id the the created new item or null if the
         *         operation fails.
         */
        public Object addItemAt(int index) throws UnsupportedOperationException;

        /**
         * Adds new item at given index.
         * <p>
         * The indexes of the item currently in the given position and all the
         * following items are incremented.
         * </p>
         * 
         * @param index
         *            Index to add the new item.
         * @param newItemId
         *            Id of the new item to be added.
         * @return Returns new item or null if the operation fails.
         */
        public Item addItemAt(int index, Object newItemId)
                throws UnsupportedOperationException;

    }

    /**
     * <p>
     * Interface for <code>Container</code> classes whose Items can be arranged
     * hierarchically. This means that the Items in the container belong in a
     * tree-like structure, with the following quirks:
     * </p>
     * 
     * <ul>
     * <li>The Item structure may have more than one root elements
     * <li>The Items in the hierarchy can be declared explicitly to be able or
     * unable to have children.
     * </ul>
     */
    public interface Hierarchical extends Container {

        /**
         * Gets the IDs of all Items that are children of the specified Item.
         * The returned collection is unmodifiable.
         * 
         * @param itemId
         *            ID of the Item whose children the caller is interested in
         * @return An unmodifiable {@link java.util.Collection collection}
         *         containing the IDs of all other Items that are children in
         *         the container hierarchy
         */
        public Collection<?> getChildren(Object itemId);

        /**
         * Gets the ID of the parent Item of the specified Item.
         * 
         * @param itemId
         *            ID of the Item whose parent the caller wishes to find out.
         * @return the ID of the parent Item. Will be <code>null</code> if the
         *         specified Item is a root element.
         */
        public Object getParent(Object itemId);

        /**
         * Gets the IDs of all Items in the container that don't have a parent.
         * Such items are called <code>root</code> Items. The returned
         * collection is unmodifiable.
         * 
         * @return An unmodifiable {@link java.util.Collection collection}
         *         containing IDs of all root elements of the container
         */
        public Collection<?> rootItemIds();

        /**
         * <p>
         * Sets the parent of an Item. The new parent item must exist and be
         * able to have children. (
         * <code>{@link #areChildrenAllowed(Object)} == true</code> ). It is
         * also possible to detach a node from the hierarchy (and thus make it
         * root) by setting the parent <code>null</code>.
         * </p>
         * 
         * <p>
         * This operation is optional.
         * </p>
         * 
         * @param itemId
         *            ID of the item to be set as the child of the Item
         *            identified with <code>newParentId</code>
         * @param newParentId
         *            ID of the Item that's to be the new parent of the Item
         *            identified with <code>itemId</code>
         * @return <code>true</code> if the operation succeeded,
         *         <code>false</code> if not
         */
        public boolean setParent(Object itemId, Object newParentId)
                throws UnsupportedOperationException;

        /**
         * Tests if the Item with given ID can have children.
         * 
         * @param itemId
         *            ID of the Item in the container whose child capability is
         *            to be tested
         * @return <code>true</code> if the specified Item exists in the
         *         Container and it can have children, <code>false</code> if
         *         it's not found from the container or it can't have children.
         */
        public boolean areChildrenAllowed(Object itemId);

        /**
         * <p>
         * Sets the given Item's capability to have children. If the Item
         * identified with <code>itemId</code> already has children and
         * <code>{@link #areChildrenAllowed(Object)}</code> is false this method
         * fails and <code>false</code> is returned.
         * </p>
         * <p>
         * The children must be first explicitly removed with
         * {@link #setParent(Object itemId, Object newParentId)}or
         * {@link com.vaadin.data.Container#removeItem(Object itemId)}.
         * </p>
         * 
         * <p>
         * This operation is optional. If it is not implemented, the method
         * always returns <code>false</code>.
         * </p>
         * 
         * @param itemId
         *            ID of the Item in the container whose child capability is
         *            to be set
         * @param areChildrenAllowed
         *            boolean value specifying if the Item can have children or
         *            not
         * @return <code>true</code> if the operation succeeded,
         *         <code>false</code> if not
         */
        public boolean setChildrenAllowed(Object itemId,
                boolean areChildrenAllowed)
                throws UnsupportedOperationException;

        /**
         * Tests if the Item specified with <code>itemId</code> is a root Item.
         * The hierarchical container can have more than one root and must have
         * at least one unless it is empty. The {@link #getParent(Object itemId)}
         * method always returns <code>null</code> for root Items.
         * 
         * @param itemId
         *            ID of the Item whose root status is to be tested
         * @return <code>true</code> if the specified Item is a root,
         *         <code>false</code> if not
         */
        public boolean isRoot(Object itemId);

        /**
         * <p>
         * Tests if the Item specified with <code>itemId</code> has child Items
         * or if it is a leaf. The {@link #getChildren(Object itemId)} method
         * always returns <code>null</code> for leaf Items.
         * </p>
         * 
         * <p>
         * Note that being a leaf does not imply whether or not an Item is
         * allowed to have children.
         * </p>
         * .
         * 
         * @param itemId
         *            ID of the Item to be tested
         * @return <code>true</code> if the specified Item has children,
         *         <code>false</code> if not (is a leaf)
         */
        public boolean hasChildren(Object itemId);

        /**
         * <p>
         * Removes the Item identified by <code>ItemId</code> from the
         * Container.
         * </p>
         * 
         * <p>
         * Note that this does not remove any children the item might have.
         * </p>
         * 
         * @param itemId
         *            ID of the Item to remove
         * @return <code>true</code> if the operation succeeded,
         *         <code>false</code> if not
         */
        public boolean removeItem(Object itemId)
                throws UnsupportedOperationException;
    }

    /**
     * Interface that is implemented by containers which allow reducing their
     * visible contents based on a set of filters.
     * <p>
     * When a set of filters are set, only items that match the filters are
     * included in the visible contents of the container. Still new items that
     * do not match filters can be added to the container. Multiple filters can
     * be added and the container remembers the state of the filters. When
     * multiple filters are added, all filters must match for an item to be
     * visible in the container.
     * </p>
     * <p>
     * When an {@link Ordered} or {@link Indexed} container is filtered, all
     * operations of these interfaces should only use the filtered contents and
     * the filtered indices to the container.
     * </p>
     * <p>
     * How filtering is performed when a {@link Hierarchical} container
     * implements {@link Filterable} is implementation specific and should be
     * documented in the implementing class.
     * </p>
     * <p>
     * Adding items (if supported) to a filtered {@link Ordered} or
     * {@link Indexed} container should insert them immediately after the
     * indicated visible item. The unfiltered position of items added at index
     * 0, at index {@link com.vaadin.data.Container#size()} or at an undefined
     * position is up to the implementation.
     * </p>
     * 
     * @since 5.0
     */
    public interface Filterable extends Container, Serializable {

        /**
         * Add a filter for given property.
         * 
         * Only items where given property for which toString() contains or
         * starts with given filterString are visible in the container.
         * 
         * @param propertyId
         *            Property for which the filter is applied to.
         * @param filterString
         *            String that must match contents of the property
         * @param ignoreCase
         *            Determine if the casing can be ignored when comparing
         *            strings.
         * @param onlyMatchPrefix
         *            Only match prefixes; no other matches are included.
         */
        public void addContainerFilter(Object propertyId, String filterString,
                boolean ignoreCase, boolean onlyMatchPrefix);

        /** Remove all filters from all properties. */
        public void removeAllContainerFilters();

        /** Remove all filters from given property. */
        public void removeContainerFilters(Object propertyId);
    }

    /**
     * Interface implemented by viewer classes capable of using a Container as a
     * data source.
     */
    public interface Viewer extends Serializable {

        /**
         * Sets the Container that serves as the data source of the viewer.
         * 
         * @param newDataSource
         *            The new data source Item
         */
        public void setContainerDataSource(Container newDataSource);

        /**
         * Gets the Container serving as the data source of the viewer.
         * 
         * @return data source Container
         */
        public Container getContainerDataSource();

    }

    /**
     * <p>
     * Interface implemented by the editor classes supporting editing the
     * Container. Implementing this interface means that the Container serving
     * as the data source of the editor can be modified through it.
     * </p>
     * <p>
     * Note that not implementing the <code>Container.Editor</code> interface
     * does not restrict the class from editing the Container contents
     * internally.
     * </p>
     */
    public interface Editor extends Container.Viewer, Serializable {

    }

    /* Contents change event */

    /**
     * An <code>Event</code> object specifying the Container whose Item set has
     * changed.
     */
    public interface ItemSetChangeEvent extends Serializable {

        /**
         * Gets the Property where the event occurred.
         * 
         * @return source of the event
         */
        public Container getContainer();
    }

    /** Container Item set change listener interface. */
    public interface ItemSetChangeListener extends Serializable {

        /**
         * Lets the listener know a Containers Item set has changed.
         * 
         * @param event
         *            change event text
         */
        public void containerItemSetChange(Container.ItemSetChangeEvent event);
    }

    /**
     * The interface for adding and removing <code>ItemSetChangeEvent</code>
     * listeners. By implementing this interface a class explicitly announces
     * that it will generate a <code>ItemSetChangeEvent</code> when its contents
     * are modified.
     * <p>
     * Note: The general Java convention is not to explicitly declare that a
     * class generates events, but to directly define the
     * <code>addListener</code> and <code>removeListener</code> methods. That
     * way the caller of these methods has no real way of finding out if the
     * class really will send the events, or if it just defines the methods to
     * be able to implement an interface.
     * </p>
     */
    public interface ItemSetChangeNotifier extends Serializable {

        /**
         * Adds an Item set change listener for the object.
         * 
         * @param listener
         *            listener to be added
         */
        public void addListener(Container.ItemSetChangeListener listener);

        /**
         * Removes the Item set change listener from the object.
         * 
         * @param listener
         *            listener to be removed
         */
        public void removeListener(Container.ItemSetChangeListener listener);
    }

    /* Property set change event */

    /**
     * An <code>Event</code> object specifying the Container whose Property set
     * has changed.
     */
    public interface PropertySetChangeEvent extends Serializable {

        /**
         * Retrieves the Container whose contents have been modified.
         * 
         * @return Source Container of the event.
         */
        public Container getContainer();
    }

    /**
     * The listener interface for receiving <code>PropertySetChangeEvent</code>
     * objects.
     */
    public interface PropertySetChangeListener extends Serializable {

        /**
         * Notifies this listener that the Containers contents has changed.
         * 
         * @param event
         *            Change event.
         */
        public void containerPropertySetChange(
                Container.PropertySetChangeEvent event);
    }

    /**
     * <p>
     * The interface for adding and removing <code>PropertySetChangeEvent</code>
     * listeners. By implementing this interface a class explicitly announces
     * that it will generate a <code>PropertySetChangeEvent</code> when its
     * contents are modified.
     * </p>
     * <p>
     * Note that the general Java convention is not to explicitly declare that a
     * class generates events, but to directly define the
     * <code>addListener</code> and <code>removeListener</code> methods. That
     * way the caller of these methods has no real way of finding out if the
     * class really will send the events, or if it just defines the methods to
     * be able to implement an interface.
     * </p>
     */
    public interface PropertySetChangeNotifier extends Serializable {

        /**
         * Registers a new Property set change listener for this Container.
         * 
         * @param listener
         *            The new Listener to be registered
         */
        public void addListener(Container.PropertySetChangeListener listener);

        /**
         * Removes a previously registered Property set change listener.
         * 
         * @param listener
         *            Listener to be removed
         */
        public void removeListener(Container.PropertySetChangeListener listener);
    }
}
