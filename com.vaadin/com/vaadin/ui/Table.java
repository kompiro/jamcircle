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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.ContainerOrderedWrapper;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ItemClickEvent.ItemClickSource;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DragSource;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.acceptcriteria.ClientCriterion;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
import com.vaadin.terminal.KeyMapper;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.ui.VScrollTable;
import com.vaadin.terminal.gwt.client.ui.dd.VLazyInitItemIdentifiers;

/**
 * <p>
 * <code>Table</code> is used for representing data or components in a pageable
 * and selectable table.
 * </p>
 * 
 * <p>
 * Scalability of the Table is largely dictated by the container. A table does
 * not have a limit for the number of items and is just as fast with hundreds of
 * thousands of items as with just a few. The current GWT implementation with
 * scrolling however limits the number of rows to around 500000, depending on
 * the browser and the pixel height of rows.
 * </p>
 * 
 * <p>
 * Components in a Table will not have their caption nor icon rendered.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 3.0
 */
@SuppressWarnings("serial")
@ClientWidget(VScrollTable.class)
public class Table extends AbstractSelect implements Action.Container,
        Container.Ordered, Container.Sortable, ItemClickSource, DragSource,
        DropTarget {

    /**
     * Modes that Table support as drag sourse.
     */
    public enum TableDragMode {
        /**
         * Table does not start drag and drop events. HTM5 style events started
         * by browser may still happen.
         */
        NONE,
        /**
         * Table starts drag with a one row only.
         */
        ROW,
        /**
         * Table drags selected rows, if drag starts on a selected rows. Else it
         * starts like in ROW mode. Note, that in Transferable there will still
         * be only the row on which the drag started, other dragged rows need to
         * be checked from the source Table.
         */
        MULTIROW
    }

    protected static final int CELL_KEY = 0;

    protected static final int CELL_HEADER = 1;

    protected static final int CELL_ICON = 2;

    protected static final int CELL_ITEMID = 3;

    protected static final int CELL_FIRSTCOL = 4;

    /**
     * Left column alignment. <b>This is the default behaviour. </b>
     */
    public static final String ALIGN_LEFT = "b";

    /**
     * Center column alignment.
     */
    public static final String ALIGN_CENTER = "c";

    /**
     * Right column alignment.
     */
    public static final String ALIGN_RIGHT = "e";

    /**
     * Column header mode: Column headers are hidden.
     */
    public static final int COLUMN_HEADER_MODE_HIDDEN = -1;

    /**
     * Column header mode: Property ID:s are used as column headers.
     */
    public static final int COLUMN_HEADER_MODE_ID = 0;

    /**
     * Column header mode: Column headers are explicitly specified with
     * {@link #setColumnHeaders(String[])}.
     */
    public static final int COLUMN_HEADER_MODE_EXPLICIT = 1;

    /**
     * Column header mode: Column headers are explicitly specified with
     * {@link #setColumnHeaders(String[])}. If a header is not specified for a
     * given property, its property id is used instead.
     * <p>
     * <b>This is the default behavior. </b>
     */
    public static final int COLUMN_HEADER_MODE_EXPLICIT_DEFAULTS_ID = 2;

    /**
     * Row caption mode: The row headers are hidden. <b>This is the default
     * mode. </b>
     */
    public static final int ROW_HEADER_MODE_HIDDEN = -1;

    /**
     * Row caption mode: Items Id-objects toString is used as row caption.
     */
    public static final int ROW_HEADER_MODE_ID = AbstractSelect.ITEM_CAPTION_MODE_ID;

    /**
     * Row caption mode: Item-objects toString is used as row caption.
     */
    public static final int ROW_HEADER_MODE_ITEM = AbstractSelect.ITEM_CAPTION_MODE_ITEM;

    /**
     * Row caption mode: Index of the item is used as item caption. The index
     * mode can only be used with the containers implementing Container.Indexed
     * interface.
     */
    public static final int ROW_HEADER_MODE_INDEX = AbstractSelect.ITEM_CAPTION_MODE_INDEX;

    /**
     * Row caption mode: Item captions are explicitly specified.
     */
    public static final int ROW_HEADER_MODE_EXPLICIT = AbstractSelect.ITEM_CAPTION_MODE_EXPLICIT;

    /**
     * Row caption mode: Item captions are read from property specified with
     * {@link #setItemCaptionPropertyId(Object)}.
     */
    public static final int ROW_HEADER_MODE_PROPERTY = AbstractSelect.ITEM_CAPTION_MODE_PROPERTY;

    /**
     * Row caption mode: Only icons are shown, the captions are hidden.
     */
    public static final int ROW_HEADER_MODE_ICON_ONLY = AbstractSelect.ITEM_CAPTION_MODE_ICON_ONLY;

    /**
     * Row caption mode: Item captions are explicitly specified, but if the
     * caption is missing, the item id objects <code>toString()</code> is used
     * instead.
     */
    public static final int ROW_HEADER_MODE_EXPLICIT_DEFAULTS_ID = AbstractSelect.ITEM_CAPTION_MODE_EXPLICIT_DEFAULTS_ID;

    /**
     * The default rate that table caches rows for smooth scrolling.
     */
    private static final double CACHE_RATE_DEFAULT = 2;

    /* Private table extensions to Select */

    /**
     * True if column collapsing is allowed.
     */
    private boolean columnCollapsingAllowed = false;

    /**
     * True if reordering of columns is allowed on the client side.
     */
    private boolean columnReorderingAllowed = false;

    /**
     * Keymapper for column ids.
     */
    private final KeyMapper columnIdMap = new KeyMapper();

    /**
     * Holds visible column propertyIds - in order.
     */
    private LinkedList<Object> visibleColumns = new LinkedList<Object>();

    /**
     * Holds propertyIds of currently collapsed columns.
     */
    private final HashSet<Object> collapsedColumns = new HashSet<Object>();

    /**
     * Holds headers for visible columns (by propertyId).
     */
    private final HashMap<Object, String> columnHeaders = new HashMap<Object, String>();

    /**
     * Holds footers for visible columns (by propertyId).
     */
    private final HashMap<Object, String> columnFooters = new HashMap<Object, String>();

    /**
     * Holds icons for visible columns (by propertyId).
     */
    private final HashMap<Object, Resource> columnIcons = new HashMap<Object, Resource>();

    /**
     * Holds alignments for visible columns (by propertyId).
     */
    private HashMap<Object, String> columnAlignments = new HashMap<Object, String>();

    /**
     * Holds column widths in pixels (Integer) or expand ratios (Float) for
     * visible columns (by propertyId).
     */
    private final HashMap<Object, Object> columnWidths = new HashMap<Object, Object>();

    /**
     * Holds column generators
     */
    private final HashMap<Object, ColumnGenerator> columnGenerators = new LinkedHashMap<Object, ColumnGenerator>();

    /**
     * Holds value of property pageLength. 0 disables paging.
     */
    private int pageLength = 15;

    /**
     * Id the first item on the current page.
     */
    private Object currentPageFirstItemId = null;

    /**
     * Index of the first item on the current page.
     */
    private int currentPageFirstItemIndex = 0;

    /**
     * Holds value of property selectable.
     */
    private boolean selectable = false;

    /**
     * Holds value of property columnHeaderMode.
     */
    private int columnHeaderMode = COLUMN_HEADER_MODE_EXPLICIT_DEFAULTS_ID;

    /**
     * Should the Table footer be visible?
     */
    private boolean columnFootersVisible = false;

    /**
     * True iff the row captions are hidden.
     */
    private boolean rowCaptionsAreHidden = true;

    /**
     * Page contents buffer used in buffered mode.
     */
    private Object[][] pageBuffer = null;

    /**
     * Set of properties listened - the list is kept to release the listeners
     * later.
     */
    private HashSet<Property> listenedProperties = null;

    /**
     * Set of visible components - the is used for needsRepaint calculation.
     */
    private HashSet<Component> visibleComponents = null;

    /**
     * List of action handlers.
     */
    private LinkedList<Handler> actionHandlers = null;

    /**
     * Action mapper.
     */
    private KeyMapper actionMapper = null;

    /**
     * Table cell editor factory.
     */
    private TableFieldFactory fieldFactory = DefaultFieldFactory.get();

    /**
     * Is table editable.
     */
    private boolean editable = false;

    /**
     * Current sorting direction.
     */
    private boolean sortAscending = true;

    /**
     * Currently table is sorted on this propertyId.
     */
    private Object sortContainerPropertyId = null;

    /**
     * Is table sorting disabled alltogether; even if some of the properties
     * would be sortable.
     */
    private boolean sortDisabled = false;

    /**
     * Number of rows explicitly requested by the client to be painted on next
     * paint. This is -1 if no request by the client is made. Painting the
     * component will automatically reset this to -1.
     */
    private int reqRowsToPaint = -1;

    /**
     * Index of the first rows explicitly requested by the client to be painted.
     * This is -1 if no request by the client is made. Painting the component
     * will automatically reset this to -1.
     */
    private int reqFirstRowToPaint = -1;

    private int firstToBeRenderedInClient = -1;

    private int lastToBeRenderedInClient = -1;

    private boolean isContentRefreshesEnabled = true;

    private int pageBufferFirstIndex;

    private boolean containerChangeToBeRendered = false;

    /**
     * Table cell specific style generator
     */
    private CellStyleGenerator cellStyleGenerator = null;

    /*
     * EXPERIMENTAL feature: will tell the client to re-calculate column widths
     * if set to true. Currently no setter: extend to enable.
     */
    protected boolean alwaysRecalculateColumnWidths = false;

    private double cacheRate = CACHE_RATE_DEFAULT;

    private TableDragMode dragMode = TableDragMode.NONE;

    private DropHandler dropHandler;

    private MultiSelectMode multiSelectMode = MultiSelectMode.DEFAULT;

    /* Table constructors */

    /**
     * Creates a new empty table.
     */
    public Table() {
        setRowHeaderMode(ROW_HEADER_MODE_HIDDEN);
    }

    /**
     * Creates a new empty table with caption.
     * 
     * @param caption
     */
    public Table(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Creates a new table with caption and connect it to a Container.
     * 
     * @param caption
     * @param dataSource
     */
    public Table(String caption, Container dataSource) {
        this();
        setCaption(caption);
        setContainerDataSource(dataSource);
    }

    /* Table functionality */

    /**
     * Gets the array of visible column id:s, including generated columns.
     * 
     * <p>
     * The columns are show in the order of their appearance in this array.
     * </p>
     * 
     * @return an array of currently visible propertyIds and generated column
     *         ids.
     */
    public Object[] getVisibleColumns() {
        if (visibleColumns == null) {
            return null;
        }
        return visibleColumns.toArray();
    }

    /**
     * Sets the array of visible column property id:s.
     * 
     * <p>
     * The columns are show in the order of their appearance in this array.
     * </p>
     * 
     * @param visibleColumns
     *            the Array of shown property id:s.
     */
    public void setVisibleColumns(Object[] visibleColumns) {

        // Visible columns must exist
        if (visibleColumns == null) {
            throw new NullPointerException(
                    "Can not set visible columns to null value");
        }

        // TODO add error check that no duplicate identifiers exist

        // Checks that the new visible columns contains no nulls and properties
        // exist
        final Collection properties = getContainerPropertyIds();
        for (int i = 0; i < visibleColumns.length; i++) {
            if (visibleColumns[i] == null) {
                throw new NullPointerException("Ids must be non-nulls");
            } else if (!properties.contains(visibleColumns[i])
                    && !columnGenerators.containsKey(visibleColumns[i])) {
                throw new IllegalArgumentException(
                        "Ids must exist in the Container or as a generated column , missing id: "
                                + visibleColumns[i]);
            }
        }

        // If this is called before the constructor is finished, it might be
        // uninitialized
        final LinkedList<Object> newVC = new LinkedList<Object>();
        for (int i = 0; i < visibleColumns.length; i++) {
            newVC.add(visibleColumns[i]);
        }

        // Removes alignments, icons and headers from hidden columns
        if (this.visibleColumns != null) {
            boolean disabledHere = disableContentRefreshing();
            try {
                for (final Iterator<Object> i = this.visibleColumns.iterator(); i
                        .hasNext();) {
                    final Object col = i.next();
                    if (!newVC.contains(col)) {
                        setColumnHeader(col, null);
                        setColumnAlignment(col, null);
                        setColumnIcon(col, null);
                    }
                }
            } finally {
                if (disabledHere) {
                    enableContentRefreshing(false);
                }
            }
        }

        this.visibleColumns = newVC;

        // Assures visual refresh
        resetPageBuffer();
        refreshRenderedCells();
    }

    /**
     * Gets the headers of the columns.
     * 
     * <p>
     * The headers match the property id:s given my the set visible column
     * headers. The table must be set in either
     * {@link #COLUMN_HEADER_MODE_EXPLICIT} or
     * {@link #COLUMN_HEADER_MODE_EXPLICIT_DEFAULTS_ID} mode to show the
     * headers. In the defaults mode any nulls in the headers array are replaced
     * with id.toString().
     * </p>
     * 
     * @return the Array of column headers.
     */
    public String[] getColumnHeaders() {
        if (columnHeaders == null) {
            return null;
        }
        final String[] headers = new String[visibleColumns.size()];
        int i = 0;
        for (final Iterator<Object> it = visibleColumns.iterator(); it
                .hasNext(); i++) {
            headers[i] = getColumnHeader(it.next());
        }
        return headers;
    }

    /**
     * Sets the headers of the columns.
     * 
     * <p>
     * The headers match the property id:s given my the set visible column
     * headers. The table must be set in either
     * {@link #COLUMN_HEADER_MODE_EXPLICIT} or
     * {@link #COLUMN_HEADER_MODE_EXPLICIT_DEFAULTS_ID} mode to show the
     * headers. In the defaults mode any nulls in the headers array are replaced
     * with id.toString() outputs when rendering.
     * </p>
     * 
     * @param columnHeaders
     *            the Array of column headers that match the
     *            {@link #getVisibleColumns()} method.
     */
    public void setColumnHeaders(String[] columnHeaders) {

        if (columnHeaders.length != visibleColumns.size()) {
            throw new IllegalArgumentException(
                    "The length of the headers array must match the number of visible columns");
        }

        this.columnHeaders.clear();
        int i = 0;
        for (final Iterator<Object> it = visibleColumns.iterator(); it
                .hasNext() && i < columnHeaders.length; i++) {
            this.columnHeaders.put(it.next(), columnHeaders[i]);
        }

        // Assures the visual refresh
        // FIXME: Is this really needed? Header captions should not affect
        // content so requestRepaint() should be sufficient.
        resetPageBuffer();
        refreshRenderedCells();
    }

    /**
     * Gets the icons of the columns.
     * 
     * <p>
     * The icons in headers match the property id:s given my the set visible
     * column headers. The table must be set in either
     * {@link #COLUMN_HEADER_MODE_EXPLICIT} or
     * {@link #COLUMN_HEADER_MODE_EXPLICIT_DEFAULTS_ID} mode to show the headers
     * with icons.
     * </p>
     * 
     * @return the Array of icons that match the {@link #getVisibleColumns()}.
     */
    public Resource[] getColumnIcons() {
        if (columnIcons == null) {
            return null;
        }
        final Resource[] icons = new Resource[visibleColumns.size()];
        int i = 0;
        for (final Iterator<Object> it = visibleColumns.iterator(); it
                .hasNext(); i++) {
            icons[i] = columnIcons.get(it.next());
        }

        return icons;
    }

    /**
     * Sets the icons of the columns.
     * 
     * <p>
     * The icons in headers match the property id:s given my the set visible
     * column headers. The table must be set in either
     * {@link #COLUMN_HEADER_MODE_EXPLICIT} or
     * {@link #COLUMN_HEADER_MODE_EXPLICIT_DEFAULTS_ID} mode to show the headers
     * with icons.
     * </p>
     * 
     * @param columnIcons
     *            the Array of icons that match the {@link #getVisibleColumns()}
     *            .
     */
    public void setColumnIcons(Resource[] columnIcons) {

        if (columnIcons.length != visibleColumns.size()) {
            throw new IllegalArgumentException(
                    "The length of the icons array must match the number of visible columns");
        }

        this.columnIcons.clear();
        int i = 0;
        for (final Iterator<Object> it = visibleColumns.iterator(); it
                .hasNext() && i < columnIcons.length; i++) {
            this.columnIcons.put(it.next(), columnIcons[i]);
        }

        // Assure visual refresh
        resetPageBuffer();
        refreshRenderedCells();
    }

    /**
     * Gets the array of column alignments.
     * 
     * <p>
     * The items in the array must match the properties identified by
     * {@link #getVisibleColumns()}. The possible values for the alignments
     * include:
     * <ul>
     * <li>{@link #ALIGN_LEFT}: Left alignment</li>
     * <li>{@link #ALIGN_CENTER}: Centered</li>
     * <li>{@link #ALIGN_RIGHT}: Right alignment</li>
     * </ul>
     * The alignments default to {@link #ALIGN_LEFT}: any null values are
     * rendered as align lefts.
     * </p>
     * 
     * @return the Column alignments array.
     */
    public String[] getColumnAlignments() {
        if (columnAlignments == null) {
            return null;
        }
        final String[] alignments = new String[visibleColumns.size()];
        int i = 0;
        for (final Iterator<Object> it = visibleColumns.iterator(); it
                .hasNext(); i++) {
            alignments[i++] = getColumnAlignment(it.next());
        }

        return alignments;
    }

    /**
     * Sets the column alignments.
     * 
     * <p>
     * The items in the array must match the properties identified by
     * {@link #getVisibleColumns()}. The possible values for the alignments
     * include:
     * <ul>
     * <li>{@link #ALIGN_LEFT}: Left alignment</li>
     * <li>{@link #ALIGN_CENTER}: Centered</li>
     * <li>{@link #ALIGN_RIGHT}: Right alignment</li>
     * </ul>
     * The alignments default to {@link #ALIGN_LEFT}
     * </p>
     * 
     * @param columnAlignments
     *            the Column alignments array.
     */
    public void setColumnAlignments(String[] columnAlignments) {

        if (columnAlignments.length != visibleColumns.size()) {
            throw new IllegalArgumentException(
                    "The length of the alignments array must match the number of visible columns");
        }

        // Checks all alignments
        for (int i = 0; i < columnAlignments.length; i++) {
            final String a = columnAlignments[i];
            if (a != null && !a.equals(ALIGN_LEFT) && !a.equals(ALIGN_CENTER)
                    && !a.equals(ALIGN_RIGHT)) {
                throw new IllegalArgumentException("Column " + i
                        + " aligment '" + a + "' is invalid");
            }
        }

        // Resets the alignments
        final HashMap<Object, String> newCA = new HashMap<Object, String>();
        int i = 0;
        for (final Iterator<Object> it = visibleColumns.iterator(); it
                .hasNext() && i < columnAlignments.length; i++) {
            newCA.put(it.next(), columnAlignments[i]);
        }
        this.columnAlignments = newCA;

        // Assures the visual refresh
        resetPageBuffer();
        refreshRenderedCells();
    }

    /**
     * Sets columns width (in pixels). Theme may not necessary respect very
     * small or very big values. Setting width to -1 (default) means that theme
     * will make decision of width.
     * 
     * <p>
     * Column can either have a fixed width or expand ratio. The latter one set
     * is used. See @link {@link #setColumnExpandRatio(Object, float)}.
     * 
     * @param columnId
     *            colunmns property id
     * @param width
     *            width to be reserved for colunmns content
     * @since 4.0.3
     */
    public void setColumnWidth(Object columnId, int width) {
        if (width < 0) {
            columnWidths.remove(columnId);
        } else {
            columnWidths.put(columnId, Integer.valueOf(width));
        }
    }

    /**
     * Sets the column expand ratio for given column.
     * <p>
     * Expand ratios can be defined to customize the way how excess space is
     * divided among columns. Table can have excess space if it has its width
     * defined and there is horizontally more space than columns consume
     * naturally. Excess space is the space that is not used by columns with
     * explicit width (see {@link #setColumnWidth(Object, int)}) or with natural
     * width (no width nor expand ratio).
     * 
     * <p>
     * By default (without expand ratios) the excess space is divided
     * proportionally to columns natural widths.
     * 
     * <p>
     * Only expand ratios of visible columns are used in final calculations.
     * 
     * <p>
     * Column can either have a fixed width or expand ratio. The latter one set
     * is used.
     * 
     * <p>
     * A column with expand ratio is considered to be minimum width by default
     * (if no excess space exists). The minimum width is defined by terminal
     * implementation.
     * 
     * <p>
     * If terminal implementation supports re-sizeable columns the column
     * becomes fixed width column if users resizes the column.
     * 
     * @param columnId
     *            colunmns property id
     * @param expandRatio
     *            the expandRatio used to divide excess space for this column
     */
    public void setColumnExpandRatio(Object columnId, float expandRatio) {
        if (expandRatio < 0) {
            columnWidths.remove(columnId);
        } else {
            columnWidths.put(columnId, new Float(expandRatio));
        }
    }

    public float getColumnExpandRatio(Object propertyId) {
        final Object width = columnWidths.get(propertyId);
        if (width == null || !(width instanceof Float)) {
            return -1;
        }
        final Float value = (Float) width;
        return value.floatValue();

    }

    /**
     * Gets the pixel width of column
     * 
     * @param propertyId
     * @return width of colun or -1 when value not set
     */
    public int getColumnWidth(Object propertyId) {
        final Object width = columnWidths.get(propertyId);
        if (width == null || !(width instanceof Integer)) {
            return -1;
        }
        final Integer value = (Integer) width;
        return value.intValue();
    }

    /**
     * Gets the page length.
     * 
     * <p>
     * Setting page length 0 disables paging.
     * </p>
     * 
     * @return the Length of one page.
     */
    public int getPageLength() {
        return pageLength;
    }

    /**
     * Sets the page length.
     * 
     * <p>
     * Setting page length 0 disables paging. The page length defaults to 15.
     * </p>
     * 
     * <p>
     * If Table has width set ({@link #setWidth(float, int)} ) the client side
     * may update the page length automatically the correct value.
     * </p>
     * 
     * @param pageLength
     *            the length of one page.
     */
    public void setPageLength(int pageLength) {
        if (pageLength >= 0 && this.pageLength != pageLength) {
            this.pageLength = pageLength;
            // Assures the visual refresh
            resetPageBuffer();
            refreshRenderedCells();
        }
    }

    /**
     * This method adjusts a possible caching mechanism of table implementation.
     * 
     * <p>
     * Table component may fetch and render some rows outside visible area. With
     * complex tables (for example containing layouts and components), the
     * client side may become unresponsive. Setting the value lower, UI will
     * become more responsive. With higher values scrolling in client will hit
     * server less frequently.
     * 
     * <p>
     * The amount of cached rows will be cacheRate multiplied with pageLength (
     * {@link #setPageLength(int)} both below and above visible area..
     * 
     * @param cacheRate
     *            a value over 0 (fastest rendering time). Higher value will
     *            cache more rows on server (smoother scrolling). Default value
     *            is 2.
     */
    public void setCacheRate(double cacheRate) {
        if (cacheRate < 0) {
            throw new IllegalArgumentException(
                    "cacheRate cannot be less than zero");
        }
        if (this.cacheRate != cacheRate) {
            this.cacheRate = cacheRate;
            requestRepaint();
        }
    }

    /**
     * @see #setCacheRate(double)
     * 
     * @return the current cache rate value
     */
    public double getCacheRate() {
        return cacheRate;
    }

    /**
     * Getter for property currentPageFirstItem.
     * 
     * @return the Value of property currentPageFirstItem.
     */
    public Object getCurrentPageFirstItemId() {

        // Priorise index over id if indexes are supported
        if (items instanceof Container.Indexed) {
            final int index = getCurrentPageFirstItemIndex();
            Object id = null;
            if (index >= 0 && index < size()) {
                id = getIdByIndex(index);
            }
            if (id != null && !id.equals(currentPageFirstItemId)) {
                currentPageFirstItemId = id;
            }
        }

        // If there is no item id at all, use the first one
        if (currentPageFirstItemId == null) {
            currentPageFirstItemId = firstItemId();
        }

        return currentPageFirstItemId;
    }

    protected Object getIdByIndex(int index) {
        return ((Container.Indexed) items).getIdByIndex(index);
    }

    /**
     * Setter for property currentPageFirstItemId.
     * 
     * @param currentPageFirstItemId
     *            the New value of property currentPageFirstItemId.
     */
    public void setCurrentPageFirstItemId(Object currentPageFirstItemId) {

        // Gets the corresponding index
        int index = -1;
        if (items instanceof Container.Indexed) {
            index = indexOfId(currentPageFirstItemId);
        } else {
            // If the table item container does not have index, we have to
            // calculates the index by hand
            Object id = firstItemId();
            while (id != null && !id.equals(currentPageFirstItemId)) {
                index++;
                id = nextItemId(id);
            }
            if (id == null) {
                index = -1;
            }
        }

        // If the search for item index was successful
        if (index >= 0) {
            /*
             * The table is not capable of displaying an item in the container
             * as the first if there are not enough items following the selected
             * item so the whole table (pagelength) is filled.
             */
            int maxIndex = size() - pageLength;
            if (maxIndex < 0) {
                maxIndex = 0;
            }

            if (index > maxIndex) {
                setCurrentPageFirstItemIndex(maxIndex);
                return;
            }

            this.currentPageFirstItemId = currentPageFirstItemId;
            currentPageFirstItemIndex = index;
        }

        // Assures the visual refresh
        resetPageBuffer();
        refreshRenderedCells();

    }

    protected int indexOfId(Object itemId) {
        return ((Container.Indexed) items).indexOfId(itemId);
    }

    /**
     * Gets the icon Resource for the specified column.
     * 
     * @param propertyId
     *            the propertyId indentifying the column.
     * @return the icon for the specified column; null if the column has no icon
     *         set, or if the column is not visible.
     */
    public Resource getColumnIcon(Object propertyId) {
        return columnIcons.get(propertyId);
    }

    /**
     * Sets the icon Resource for the specified column.
     * <p>
     * Throws IllegalArgumentException if the specified column is not visible.
     * </p>
     * 
     * @param propertyId
     *            the propertyId identifying the column.
     * @param icon
     *            the icon Resource to set.
     */
    public void setColumnIcon(Object propertyId, Resource icon) {

        if (icon == null) {
            columnIcons.remove(propertyId);
        } else {
            columnIcons.put(propertyId, icon);
        }

        // Assures the visual refresh
        resetPageBuffer();
        refreshRenderedCells();
    }

    /**
     * Gets the header for the specified column.
     * 
     * @param propertyId
     *            the propertyId indentifying the column.
     * @return the header for the specifed column if it has one.
     */
    public String getColumnHeader(Object propertyId) {
        if (getColumnHeaderMode() == COLUMN_HEADER_MODE_HIDDEN) {
            return null;
        }

        String header = columnHeaders.get(propertyId);
        if ((header == null && getColumnHeaderMode() == COLUMN_HEADER_MODE_EXPLICIT_DEFAULTS_ID)
                || getColumnHeaderMode() == COLUMN_HEADER_MODE_ID) {
            header = propertyId.toString();
        }

        return header;
    }

    /**
     * Sets the column header for the specified column;
     * 
     * @param propertyId
     *            the propertyId indentifying the column.
     * @param header
     *            the header to set.
     */
    public void setColumnHeader(Object propertyId, String header) {

        if (header == null) {
            columnHeaders.remove(propertyId);
        } else {
            columnHeaders.put(propertyId, header);
        }

        // Assures the visual refresh
        // FIXME: Is this really needed? Header captions should not affect
        // content so requestRepaint() should be sufficient.
        refreshRenderedCells();
    }

    /**
     * Gets the specified column's alignment.
     * 
     * @param propertyId
     *            the propertyID identifying the column.
     * @return the specified column's alignment if it as one; null otherwise.
     */
    public String getColumnAlignment(Object propertyId) {
        final String a = columnAlignments.get(propertyId);
        return a == null ? ALIGN_LEFT : a;
    }

    /**
     * Sets the specified column's alignment.
     * 
     * <p>
     * Throws IllegalArgumentException if the alignment is not one of the
     * following: {@link #ALIGN_LEFT}, {@link #ALIGN_CENTER} or
     * {@link #ALIGN_RIGHT}
     * </p>
     * 
     * @param propertyId
     *            the propertyID identifying the column.
     * @param alignment
     *            the desired alignment.
     */
    public void setColumnAlignment(Object propertyId, String alignment) {

        // Checks for valid alignments
        if (alignment != null && !alignment.equals(ALIGN_LEFT)
                && !alignment.equals(ALIGN_CENTER)
                && !alignment.equals(ALIGN_RIGHT)) {
            throw new IllegalArgumentException("Column alignment '" + alignment
                    + "' is not supported.");
        }

        if (alignment == null || alignment.equals(ALIGN_LEFT)) {
            columnAlignments.remove(propertyId);
            return;
        }

        columnAlignments.put(propertyId, alignment);

        // Assures the visual refresh
        refreshRenderedCells();
    }

    /**
     * Checks if the specified column is collapsed.
     * 
     * @param propertyId
     *            the propertyID identifying the column.
     * @return true if the column is collapsed; false otherwise;
     */
    public boolean isColumnCollapsed(Object propertyId) {
        return collapsedColumns != null
                && collapsedColumns.contains(propertyId);
    }

    /**
     * Sets whether the specified column is collapsed or not.
     * 
     * 
     * @param propertyId
     *            the propertyID identifying the column.
     * @param collapsed
     *            the desired collapsedness.
     * @throws IllegalAccessException
     */
    public void setColumnCollapsed(Object propertyId, boolean collapsed)
            throws IllegalAccessException {
        if (!isColumnCollapsingAllowed()) {
            throw new IllegalAccessException("Column collapsing not allowed!");
        }

        if (collapsed) {
            collapsedColumns.add(propertyId);
        } else {
            collapsedColumns.remove(propertyId);
        }

        // Assures the visual refresh
        resetPageBuffer();
        refreshRenderedCells();
    }

    /**
     * Checks if column collapsing is allowed.
     * 
     * @return true if columns can be collapsed; false otherwise.
     */
    public boolean isColumnCollapsingAllowed() {
        return columnCollapsingAllowed;
    }

    /**
     * Sets whether column collapsing is allowed or not.
     * 
     * @param collapsingAllowed
     *            specifies whether column collapsing is allowed.
     */
    public void setColumnCollapsingAllowed(boolean collapsingAllowed) {
        columnCollapsingAllowed = collapsingAllowed;

        if (!collapsingAllowed) {
            collapsedColumns.clear();
        }

        // Assures the visual refresh
        refreshRenderedCells();
    }

    /**
     * Checks if column reordering is allowed.
     * 
     * @return true if columns can be reordered; false otherwise.
     */
    public boolean isColumnReorderingAllowed() {
        return columnReorderingAllowed;
    }

    /**
     * Sets whether column reordering is allowed or not.
     * 
     * @param reorderingAllowed
     *            specifies whether column reordering is allowed.
     */
    public void setColumnReorderingAllowed(boolean reorderingAllowed) {
        columnReorderingAllowed = reorderingAllowed;

        // Assures the visual refresh
        refreshRenderedCells();
    }

    /*
     * Arranges visible columns according to given columnOrder. Silently ignores
     * colimnId:s that are not visible columns, and keeps the internal order of
     * visible columns left out of the ordering (trailing). Silently does
     * nothing if columnReordering is not allowed.
     */
    private void setColumnOrder(Object[] columnOrder) {
        if (columnOrder == null || !isColumnReorderingAllowed()) {
            return;
        }
        final LinkedList<Object> newOrder = new LinkedList<Object>();
        for (int i = 0; i < columnOrder.length; i++) {
            if (columnOrder[i] != null
                    && visibleColumns.contains(columnOrder[i])) {
                visibleColumns.remove(columnOrder[i]);
                newOrder.add(columnOrder[i]);
            }
        }
        for (final Iterator<Object> it = visibleColumns.iterator(); it
                .hasNext();) {
            final Object columnId = it.next();
            if (!newOrder.contains(columnId)) {
                newOrder.add(columnId);
            }
        }
        visibleColumns = newOrder;

        // Assure visual refresh
        resetPageBuffer();
        refreshRenderedCells();
    }

    /**
     * Getter for property currentPageFirstItem.
     * 
     * @return the Value of property currentPageFirstItem.
     */
    public int getCurrentPageFirstItemIndex() {
        return currentPageFirstItemIndex;
    }

    private void setCurrentPageFirstItemIndex(int newIndex,
            boolean needsPageBufferReset) {

        if (newIndex < 0) {
            newIndex = 0;
        }

        /*
         * minimize Container.size() calls which may be expensive. For example
         * it may cause sql query.
         */
        final int size = size();

        /*
         * The table is not capable of displaying an item in the container as
         * the first if there are not enough items following the selected item
         * so the whole table (pagelength) is filled.
         */
        int maxIndex = size - pageLength;
        if (maxIndex < 0) {
            maxIndex = 0;
        }

        // Ensures that the new value is valid
        if (newIndex > maxIndex) {
            newIndex = maxIndex;
        }

        // Refresh first item id
        if (items instanceof Container.Indexed) {
            try {
                currentPageFirstItemId = getIdByIndex(newIndex);
            } catch (final IndexOutOfBoundsException e) {
                currentPageFirstItemId = null;
            }
            currentPageFirstItemIndex = newIndex;
        } else {

            // For containers not supporting indexes, we must iterate the
            // container forwards / backwards
            // next available item forward or backward

            currentPageFirstItemId = firstItemId();

            // Go forwards in the middle of the list (respect borders)
            while (currentPageFirstItemIndex < newIndex
                    && !isLastId(currentPageFirstItemId)) {
                currentPageFirstItemIndex++;
                currentPageFirstItemId = nextItemId(currentPageFirstItemId);
            }

            // If we did hit the border
            if (isLastId(currentPageFirstItemId)) {
                currentPageFirstItemIndex = size - 1;
            }

            // Go backwards in the middle of the list (respect borders)
            while (currentPageFirstItemIndex > newIndex
                    && !isFirstId(currentPageFirstItemId)) {
                currentPageFirstItemIndex--;
                currentPageFirstItemId = prevItemId(currentPageFirstItemId);
            }

            // If we did hit the border
            if (isFirstId(currentPageFirstItemId)) {
                currentPageFirstItemIndex = 0;
            }

            // Go forwards once more
            while (currentPageFirstItemIndex < newIndex
                    && !isLastId(currentPageFirstItemId)) {
                currentPageFirstItemIndex++;
                currentPageFirstItemId = nextItemId(currentPageFirstItemId);
            }

            // If for some reason we do hit border again, override
            // the user index request
            if (isLastId(currentPageFirstItemId)) {
                newIndex = currentPageFirstItemIndex = size - 1;
            }
        }
        if (needsPageBufferReset) {
            // Assures the visual refresh
            resetPageBuffer();
            refreshRenderedCells();
        }
    }

    /**
     * Setter for property currentPageFirstItem.
     * 
     * @param newIndex
     *            the New value of property currentPageFirstItem.
     */
    public void setCurrentPageFirstItemIndex(int newIndex) {
        setCurrentPageFirstItemIndex(newIndex, true);
    }

    /**
     * Getter for property pageBuffering.
     * 
     * @deprecated functionality is not needed in ajax rendering model
     * 
     * @return the Value of property pageBuffering.
     */
    @Deprecated
    public boolean isPageBufferingEnabled() {
        return true;
    }

    /**
     * Setter for property pageBuffering.
     * 
     * @deprecated functionality is not needed in ajax rendering model
     * 
     * @param pageBuffering
     *            the New value of property pageBuffering.
     */
    @Deprecated
    public void setPageBufferingEnabled(boolean pageBuffering) {

    }

    /**
     * Getter for property selectable.
     * 
     * <p>
     * The table is not selectable by default.
     * </p>
     * 
     * @return the Value of property selectable.
     */
    public boolean isSelectable() {
        return selectable;
    }

    /**
     * Setter for property selectable.
     * 
     * <p>
     * The table is not selectable by default.
     * </p>
     * 
     * @param selectable
     *            the New value of property selectable.
     */
    public void setSelectable(boolean selectable) {
        if (this.selectable != selectable) {
            this.selectable = selectable;
            requestRepaint();
        }
    }

    /**
     * Getter for property columnHeaderMode.
     * 
     * @return the Value of property columnHeaderMode.
     */
    public int getColumnHeaderMode() {
        return columnHeaderMode;
    }

    /**
     * Setter for property columnHeaderMode.
     * 
     * @param columnHeaderMode
     *            the New value of property columnHeaderMode.
     */
    public void setColumnHeaderMode(int columnHeaderMode) {
        if (columnHeaderMode >= COLUMN_HEADER_MODE_HIDDEN
                && columnHeaderMode <= COLUMN_HEADER_MODE_EXPLICIT_DEFAULTS_ID) {
            this.columnHeaderMode = columnHeaderMode;
        }

        // Assures the visual refresh
        refreshRenderedCells();
    }

    /**
     * Refreshes rendered rows
     */
    protected void refreshRenderedCells() {
        if (getParent() == null) {
            return;
        }

        if (isContentRefreshesEnabled) {

            HashSet<Property> oldListenedProperties = listenedProperties;
            HashSet<Component> oldVisibleComponents = visibleComponents;

            // initialize the listener collections
            listenedProperties = new HashSet<Property>();
            visibleComponents = new HashSet<Component>();

            // Collects the basic facts about the table page
            final Object[] colids = getVisibleColumns();
            final int cols = colids.length;
            final int pagelen = getPageLength();
            int firstIndex = getCurrentPageFirstItemIndex();
            int rows, totalRows;
            rows = totalRows = size();
            if (rows > 0 && firstIndex >= 0) {
                rows -= firstIndex;
            }
            if (pagelen > 0 && pagelen < rows) {
                rows = pagelen;
            }

            // If "to be painted next" variables are set, use them
            if (lastToBeRenderedInClient - firstToBeRenderedInClient > 0) {
                rows = lastToBeRenderedInClient - firstToBeRenderedInClient + 1;
            }
            Object id;
            if (firstToBeRenderedInClient >= 0) {
                if (firstToBeRenderedInClient < totalRows) {
                    firstIndex = firstToBeRenderedInClient;
                } else {
                    firstIndex = totalRows - 1;
                }
            } else {
                // initial load
                firstToBeRenderedInClient = firstIndex;
            }
            if (totalRows > 0) {
                if (rows + firstIndex > totalRows) {
                    rows = totalRows - firstIndex;
                }
            } else {
                rows = 0;
            }

            Object[][] cells = new Object[cols + CELL_FIRSTCOL][rows];
            if (rows == 0) {
                pageBuffer = cells;
                unregisterPropertiesAndComponents(oldListenedProperties,
                        oldVisibleComponents);

                /*
                 * We need to repaint so possible header or footer changes are
                 * sent to the server
                 */
                requestRepaint();

                return;
            }

            // Gets the first item id
            if (items instanceof Container.Indexed) {
                id = getIdByIndex(firstIndex);
            } else {
                id = firstItemId();
                for (int i = 0; i < firstIndex; i++) {
                    id = nextItemId(id);
                }
            }

            final int headmode = getRowHeaderMode();
            final boolean[] iscomponent = new boolean[cols];
            for (int i = 0; i < cols; i++) {
                iscomponent[i] = columnGenerators.containsKey(colids[i])
                        || Component.class.isAssignableFrom(getType(colids[i]));
            }
            int firstIndexNotInCache;
            if (pageBuffer != null && pageBuffer[CELL_ITEMID].length > 0) {
                firstIndexNotInCache = pageBufferFirstIndex
                        + pageBuffer[CELL_ITEMID].length;
            } else {
                firstIndexNotInCache = -1;
            }

            // Creates the page contents
            int filledRows = 0;
            for (int i = 0; i < rows && id != null; i++) {
                cells[CELL_ITEMID][i] = id;
                cells[CELL_KEY][i] = itemIdMapper.key(id);
                if (headmode != ROW_HEADER_MODE_HIDDEN) {
                    switch (headmode) {
                    case ROW_HEADER_MODE_INDEX:
                        cells[CELL_HEADER][i] = String.valueOf(i + firstIndex
                                + 1);
                        break;
                    default:
                        cells[CELL_HEADER][i] = getItemCaption(id);
                    }
                    cells[CELL_ICON][i] = getItemIcon(id);
                }

                if (cols > 0) {
                    for (int j = 0; j < cols; j++) {
                        if (isColumnCollapsed(colids[j])) {
                            continue;
                        }
                        Property p = null;
                        Object value = "";
                        boolean isGenerated = columnGenerators
                                .containsKey(colids[j]);

                        if (!isGenerated) {
                            p = getContainerProperty(id, colids[j]);
                        }

                        // check in current pageBuffer already has row
                        int index = firstIndex + i;
                        if (p != null || isGenerated) {
                            if (index < firstIndexNotInCache
                                    && index >= pageBufferFirstIndex) {
                                // we have data already in our cache,
                                // recycle it instead of fetching it via
                                // getValue/getPropertyValue
                                int indexInOldBuffer = index
                                        - pageBufferFirstIndex;
                                value = pageBuffer[CELL_FIRSTCOL + j][indexInOldBuffer];
                                if (!isGenerated && iscomponent[j]
                                        || !(value instanceof Component)) {
                                    listenProperty(p, oldListenedProperties);
                                }
                            } else {
                                if (isGenerated) {
                                    ColumnGenerator cg = columnGenerators
                                            .get(colids[j]);
                                    value = cg
                                            .generateCell(this, id, colids[j]);

                                } else if (iscomponent[j]) {
                                    value = p.getValue();
                                    listenProperty(p, oldListenedProperties);
                                } else if (p != null) {
                                    value = getPropertyValue(id, colids[j], p);
                                    /*
                                     * If returned value is Component (via
                                     * fieldfactory or overridden
                                     * getPropertyValue) we excpect it to listen
                                     * property value changes. Otherwise if
                                     * property emits value change events, table
                                     * will start to listen them and refresh
                                     * content when needed.
                                     */
                                    if (!(value instanceof Component)) {
                                        listenProperty(p, oldListenedProperties);
                                    }
                                } else {
                                    value = getPropertyValue(id, colids[j],
                                            null);
                                }
                            }
                        }

                        if (value instanceof Component) {
                            if (oldVisibleComponents == null
                                    || !oldVisibleComponents.contains(value)) {
                                ((Component) value).setParent(this);
                            }
                            visibleComponents.add((Component) value);
                        }
                        cells[CELL_FIRSTCOL + j][i] = value;
                    }
                }

                // Gets the next item id
                if (items instanceof Container.Indexed) {
                    int index = firstIndex + i + 1;
                    if (index < totalRows) {
                        id = getIdByIndex(index);
                    } else {
                        id = null;
                    }
                } else {
                    id = nextItemId(id);
                }

                filledRows++;
            }

            // Assures that all the rows of the cell-buffer are valid
            if (filledRows != cells[0].length) {
                final Object[][] temp = new Object[cells.length][filledRows];
                for (int i = 0; i < cells.length; i++) {
                    for (int j = 0; j < filledRows; j++) {
                        temp[i][j] = cells[i][j];
                    }
                }
                cells = temp;
            }

            pageBufferFirstIndex = firstIndex;

            // Saves the results to internal buffer
            pageBuffer = cells;

            unregisterPropertiesAndComponents(oldListenedProperties,
                    oldVisibleComponents);

            requestRepaint();
        }

    }

    private void listenProperty(Property p,
            HashSet<Property> oldListenedProperties) {
        if (p instanceof Property.ValueChangeNotifier) {
            if (oldListenedProperties == null
                    || !oldListenedProperties.contains(p)) {
                ((Property.ValueChangeNotifier) p).addListener(this);
            }
            /*
             * register listened properties, so we can do proper cleanup to free
             * memory. Essential if table has loads of data and it is used for a
             * long time.
             */
            listenedProperties.add(p);

        }
    }

    /**
     * Helper method to remove listeners and maintain correct component
     * hierarchy. Detaches properties and components if those are no more
     * rendered in client.
     * 
     * @param oldListenedProperties
     *            set of properties that where listened in last render
     * @param oldVisibleComponents
     *            set of components that where attached in last render
     */
    private void unregisterPropertiesAndComponents(
            HashSet<Property> oldListenedProperties,
            HashSet<Component> oldVisibleComponents) {
        if (oldVisibleComponents != null) {
            for (final Iterator<Component> i = oldVisibleComponents.iterator(); i
                    .hasNext();) {
                Component c = i.next();
                if (!visibleComponents.contains(c)) {
                    c.setParent(null);
                }
            }
        }

        if (oldListenedProperties != null) {
            for (final Iterator<Property> i = oldListenedProperties.iterator(); i
                    .hasNext();) {
                Property.ValueChangeNotifier o = (ValueChangeNotifier) i.next();
                if (!listenedProperties.contains(o)) {
                    o.removeListener(this);
                }
            }
        }
    }

    /**
     * Refreshes the current page contents.
     * 
     * @deprecated should not need to be used
     */
    @Deprecated
    public void refreshCurrentPage() {

    }

    /**
     * Sets the row header mode.
     * <p>
     * The mode can be one of the following ones:
     * <ul>
     * <li>{@link #ROW_HEADER_MODE_HIDDEN}: The row captions are hidden.</li>
     * <li>{@link #ROW_HEADER_MODE_ID}: Items Id-objects <code>toString()</code>
     * is used as row caption.
     * <li>{@link #ROW_HEADER_MODE_ITEM}: Item-objects <code>toString()</code>
     * is used as row caption.
     * <li>{@link #ROW_HEADER_MODE_PROPERTY}: Property set with
     * {@link #setItemCaptionPropertyId(Object)} is used as row header.
     * <li>{@link #ROW_HEADER_MODE_EXPLICIT_DEFAULTS_ID}: Items Id-objects
     * <code>toString()</code> is used as row header. If caption is explicitly
     * specified, it overrides the id-caption.
     * <li>{@link #ROW_HEADER_MODE_EXPLICIT}: The row headers must be explicitly
     * specified.</li>
     * <li>{@link #ROW_HEADER_MODE_INDEX}: The index of the item is used as row
     * caption. The index mode can only be used with the containers implementing
     * <code>Container.Indexed</code> interface.</li>
     * </ul>
     * The default value is {@link #ROW_HEADER_MODE_HIDDEN}
     * </p>
     * 
     * @param mode
     *            the One of the modes listed above.
     */
    public void setRowHeaderMode(int mode) {
        if (ROW_HEADER_MODE_HIDDEN == mode) {
            rowCaptionsAreHidden = true;
        } else {
            rowCaptionsAreHidden = false;
            setItemCaptionMode(mode);
        }

        // Assure visual refresh
        refreshRenderedCells();
    }

    /**
     * Gets the row header mode.
     * 
     * @return the Row header mode.
     * @see #setRowHeaderMode(int)
     */
    public int getRowHeaderMode() {
        return rowCaptionsAreHidden ? ROW_HEADER_MODE_HIDDEN
                : getItemCaptionMode();
    }

    /**
     * Adds the new row to table and fill the visible cells (except generated
     * columns) with given values.
     * 
     * @param cells
     *            the Object array that is used for filling the visible cells
     *            new row. The types must be settable to visible column property
     *            types.
     * @param itemId
     *            the Id the new row. If null, a new id is automatically
     *            assigned. If given, the table cant already have a item with
     *            given id.
     * @return Returns item id for the new row. Returns null if operation fails.
     */
    public Object addItem(Object[] cells, Object itemId)
            throws UnsupportedOperationException {

        // remove generated columns from the list of columns being assigned
        final LinkedList<Object> availableCols = new LinkedList<Object>();
        for (Iterator<Object> it = visibleColumns.iterator(); it.hasNext();) {
            Object id = it.next();
            if (!columnGenerators.containsKey(id)) {
                availableCols.add(id);
            }
        }
        // Checks that a correct number of cells are given
        if (cells.length != availableCols.size()) {
            return null;
        }

        // Creates new item
        Item item;
        if (itemId == null) {
            itemId = items.addItem();
            if (itemId == null) {
                return null;
            }
            item = items.getItem(itemId);
        } else {
            item = items.addItem(itemId);
        }
        if (item == null) {
            return null;
        }

        // Fills the item properties
        for (int i = 0; i < availableCols.size(); i++) {
            item.getItemProperty(availableCols.get(i)).setValue(cells[i]);
        }

        if (!(items instanceof Container.ItemSetChangeNotifier)) {
            resetPageBuffer();
            refreshRenderedCells();
        }

        return itemId;
    }

    /* Overriding select behavior */

    @Override
    public void setValue(Object newValue) throws ReadOnlyException,
            ConversionException {
        // external selection change, need to truncate pageBuffer
        resetPageBuffer();
        refreshRenderedCells();
        super.setValue(newValue);
    }

    @Override
    public void setContainerDataSource(Container newDataSource) {

        disableContentRefreshing();

        if (newDataSource == null) {
            newDataSource = new IndexedContainer();
        }

        // Assures that the data source is ordered by making unordered
        // containers ordered by wrapping them
        if (newDataSource instanceof Container.Ordered) {
            super.setContainerDataSource(newDataSource);
        } else {
            super.setContainerDataSource(new ContainerOrderedWrapper(
                    newDataSource));
        }

        // Resets page position
        currentPageFirstItemId = null;
        currentPageFirstItemIndex = 0;

        // Resets column properties
        if (collapsedColumns != null) {
            collapsedColumns.clear();
        }

        // columnGenerators 'override' properties, don't add the same id twice
        Collection<Object> col = new LinkedList<Object>();
        for (Iterator it = getContainerPropertyIds().iterator(); it.hasNext();) {
            Object id = it.next();
            if (columnGenerators == null || !columnGenerators.containsKey(id)) {
                col.add(id);
            }
        }
        // generators added last
        if (columnGenerators != null && columnGenerators.size() > 0) {
            col.addAll(columnGenerators.keySet());
        }

        setVisibleColumns(col.toArray());

        // Assure visual refresh
        resetPageBuffer();

        enableContentRefreshing(true);
    }

    /**
     * Gets items ids from a range of key values
     * 
     * @param startRowKey
     *            The start key
     * @param endRowKey
     *            The end key
     * @return
     */
    private Set<Object> getItemIdsInRange(Object itemId, final int length) {
        HashSet<Object> ids = new HashSet<Object>();
        for (int i = 0; i < length; i++) {
            assert itemId != null; // should not be null unless client-server
                                   // are out of sync
            ids.add(itemId);
            itemId = nextItemId(itemId);
        }
        return ids;
    }

    /**
     * Handles selection if selection is a multiselection
     * 
     * @param variables
     *            The variables
     */
    private void handleSelectedItems(Map<String, Object> variables) {
        final String[] ka = (String[]) variables.get("selected");
        final String[] ranges = (String[]) variables.get("selectedRanges");

        Set<Object> renderedItemIds = getCurrentlyRenderedItemIds();

        HashSet<Object> newValue = new HashSet<Object>(
                (Collection<Object>) getValue());

        if (variables.containsKey("clearSelections")) {
            // the client side has instructed to swipe all previous selections
            newValue.clear();
        } else {
            /*
             * first clear all selections that are currently rendered rows (the
             * ones that the client side counterpart is aware of)
             */
            newValue.removeAll(renderedItemIds);
        }

        /*
         * Then add (possibly some of them back) rows that are currently
         * selected on the client side (the ones that the client side is aware
         * of).
         */
        for (int i = 0; i < ka.length; i++) {
            // key to id
            final Object id = itemIdMapper.get(ka[i]);
            if (!isNullSelectionAllowed()
                    && (id == null || id == getNullSelectionItemId())) {
                // skip empty selection if nullselection is not allowed
                requestRepaint();
            } else if (id != null && containsId(id)) {
                newValue.add(id);
            }
        }

        if (!isNullSelectionAllowed() && newValue.size() < 1) {
            // empty selection not allowed, keep old value
            requestRepaint();
            return;
        }

        /* Add range items aka shift clicked multiselection areas */
        if (ranges != null) {
            for (String range : ranges) {
                String[] split = range.split("-");
                Object startItemId = itemIdMapper.get(split[0]);
                int length = Integer.valueOf(split[1]);
                newValue.addAll(getItemIdsInRange(startItemId, length));
            }
        }

        setValue(newValue, true);

    }

    private Set<Object> getCurrentlyRenderedItemIds() {
        HashSet<Object> ids = new HashSet<Object>();
        if (pageBuffer != null) {
            for (int i = 0; i < pageBuffer[CELL_ITEMID].length; i++) {
                ids.add(pageBuffer[CELL_ITEMID][i]);
            }
        }
        return ids;
    }

    /* Component basics */

    /**
     * Invoked when the value of a variable has changed.
     * 
     * @see com.vaadin.ui.Select#changeVariables(java.lang.Object,
     *      java.util.Map)
     */
    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {

        boolean clientNeedsContentRefresh = false;

        handleClickEvent(variables);

        handleColumnResizeEvent(variables);

        disableContentRefreshing();

        if (!isSelectable() && variables.containsKey("selected")) {
            // Not-selectable is a special case, AbstractSelect does not support
            // TODO could be optimized.
            variables = new HashMap<String, Object>(variables);
            variables.remove("selected");
        }

        /*
         * The AbstractSelect cannot handle the multiselection properly, instead
         * we handle it ourself
         */
        else if (isSelectable() && isMultiSelect()
                && variables.containsKey("selected")
                && multiSelectMode == MultiSelectMode.DEFAULT) {
            handleSelectedItems(variables);
            variables = new HashMap<String, Object>(variables);
            variables.remove("selected");
        }

        super.changeVariables(source, variables);

        // Client might update the pagelength if Table height is fixed
        if (variables.containsKey("pagelength")) {
            // Sets pageLength directly to avoid repaint that setter causes
            pageLength = (Integer) variables.get("pagelength");
        }

        // Page start index
        if (variables.containsKey("firstvisible")) {
            final Integer value = (Integer) variables.get("firstvisible");
            if (value != null) {
                setCurrentPageFirstItemIndex(value.intValue(), false);
            }
        }

        // Sets requested firstrow and rows for the next paint
        if (variables.containsKey("reqfirstrow")
                || variables.containsKey("reqrows")) {

            try {
                firstToBeRenderedInClient = ((Integer) variables
                        .get("firstToBeRendered")).intValue();
                lastToBeRenderedInClient = ((Integer) variables
                        .get("lastToBeRendered")).intValue();
            } catch (Exception e) {
                // FIXME: Handle exception
                e.printStackTrace();
            }

            // respect suggested rows only if table is not otherwise updated
            // (row caches emptied by other event)
            if (!containerChangeToBeRendered) {
                Integer value = (Integer) variables.get("reqfirstrow");
                if (value != null) {
                    reqFirstRowToPaint = value.intValue();
                }
                value = (Integer) variables.get("reqrows");
                if (value != null) {
                    reqRowsToPaint = value.intValue();
                    // sanity check
                    if (reqFirstRowToPaint + reqRowsToPaint > size()) {
                        reqRowsToPaint = size() - reqFirstRowToPaint;
                    }
                }
            }
            clientNeedsContentRefresh = true;
        }

        if (!sortDisabled) {
            // Sorting
            boolean doSort = false;
            if (variables.containsKey("sortcolumn")) {
                final String colId = (String) variables.get("sortcolumn");
                if (colId != null && !"".equals(colId) && !"null".equals(colId)) {
                    final Object id = columnIdMap.get(colId);
                    setSortContainerPropertyId(id, false);
                    doSort = true;
                }
            }
            if (variables.containsKey("sortascending")) {
                final boolean state = ((Boolean) variables.get("sortascending"))
                        .booleanValue();
                if (state != sortAscending) {
                    setSortAscending(state, false);
                    doSort = true;
                }
            }
            if (doSort) {
                this.sort();
                resetPageBuffer();
            }
        }

        // Dynamic column hide/show and order
        // Update visible columns
        if (isColumnCollapsingAllowed()) {
            if (variables.containsKey("collapsedcolumns")) {
                try {
                    final Object[] ids = (Object[]) variables
                            .get("collapsedcolumns");
                    for (final Iterator<Object> it = visibleColumns.iterator(); it
                            .hasNext();) {
                        setColumnCollapsed(it.next(), false);
                    }
                    for (int i = 0; i < ids.length; i++) {
                        setColumnCollapsed(columnIdMap.get(ids[i].toString()),
                                true);
                    }
                } catch (final Exception e) {
                    // FIXME: Handle exception
                    e.printStackTrace();
                }
                clientNeedsContentRefresh = true;
            }
        }
        if (isColumnReorderingAllowed()) {
            if (variables.containsKey("columnorder")) {
                try {
                    final Object[] ids = (Object[]) variables
                            .get("columnorder");
                    // need a real Object[], ids can be a String[]
                    final Object[] idsTemp = new Object[ids.length];
                    for (int i = 0; i < ids.length; i++) {
                        idsTemp[i] = columnIdMap.get(ids[i].toString());
                    }
                    setColumnOrder(idsTemp);
                } catch (final Exception e) {
                    // FIXME: Handle exception
                    e.printStackTrace();

                }
                clientNeedsContentRefresh = true;
            }
        }

        enableContentRefreshing(clientNeedsContentRefresh);

        // Actions
        if (variables.containsKey("action")) {
            final StringTokenizer st = new StringTokenizer(
                    (String) variables.get("action"), ",");
            if (st.countTokens() == 2) {
                final Object itemId = itemIdMapper.get(st.nextToken());
                final Action action = (Action) actionMapper.get(st.nextToken());
                if (action != null && containsId(itemId)
                        && actionHandlers != null) {
                    for (final Iterator<Handler> i = actionHandlers.iterator(); i
                            .hasNext();) {
                        (i.next()).handleAction(action, this, itemId);
                    }
                }
            }
        }

    }

    /**
     * Handles click event
     * 
     * @param variables
     */
    private void handleClickEvent(Map<String, Object> variables) {

        // Item click event
        if (variables.containsKey("clickEvent")) {
            String key = (String) variables.get("clickedKey");
            Object itemId = itemIdMapper.get(key);
            Object propertyId = null;
            String colkey = (String) variables.get("clickedColKey");
            // click is not necessary on a property
            if (colkey != null) {
                propertyId = columnIdMap.get(colkey);
            }
            MouseEventDetails evt = MouseEventDetails
                    .deSerialize((String) variables.get("clickEvent"));
            Item item = getItem(itemId);
            if (item != null) {
                fireEvent(new ItemClickEvent(this, item, itemId, propertyId,
                        evt));
            }
        }

        // Header click event
        else if (variables.containsKey("headerClickEvent")) {

            MouseEventDetails details = MouseEventDetails
                    .deSerialize((String) variables.get("headerClickEvent"));

            Object cid = variables.get("headerClickCID");
            Object propertyId = null;
            if (cid != null) {
                propertyId = columnIdMap.get(cid.toString());
            }
            fireEvent(new HeaderClickEvent(this, propertyId, details));
        }

        // Footer click event
        else if (variables.containsKey("footerClickEvent")) {
            MouseEventDetails details = MouseEventDetails
                    .deSerialize((String) variables.get("footerClickEvent"));

            Object cid = variables.get("footerClickCID");
            Object propertyId = null;
            if (cid != null) {
                propertyId = columnIdMap.get(cid.toString());
            }
            fireEvent(new FooterClickEvent(this, propertyId, details));
        }
    }

    /**
     * Handles the column resize event sent by the client.
     * 
     * @param variables
     */
    private void handleColumnResizeEvent(Map<String, Object> variables) {
        if (variables.containsKey("columnResizeEventColumn")) {
            Object cid = variables.get("columnResizeEventColumn");
            Object propertyId = null;
            if (cid != null) {
                propertyId = columnIdMap.get(cid.toString());
            }

            Object prev = variables.get("columnResizeEventPrev");
            int previousWidth = -1;
            if (prev != null) {
                previousWidth = Integer.valueOf(prev.toString());
            }

            Object curr = variables.get("columnResizeEventCurr");
            int currentWidth = -1;
            if (curr != null) {
                currentWidth = Integer.valueOf(curr.toString());
            }

            /*
             * Update the sizes on the server side. If a column previously had a
             * expand ratio and the user resized the column then the expand
             * ratio will be turned into a static pixel size.
             */
            setColumnWidth(propertyId, currentWidth);

            fireEvent(new ColumnResizeEvent(this, propertyId, previousWidth,
                    currentWidth));
        }
    }

    /**
     * Go to mode where content updates are not done. This is due we want to
     * bypass expensive content for some reason (like when we know we may have
     * other content changes on their way).
     * 
     * @return true if content refresh flag was enabled prior this call
     */
    protected boolean disableContentRefreshing() {
        boolean wasDisabled = isContentRefreshesEnabled;
        isContentRefreshesEnabled = false;
        return wasDisabled;
    }

    /**
     * Go to mode where content content refreshing has effect.
     * 
     * @param refreshContent
     *            true if content refresh needs to be done
     */
    protected void enableContentRefreshing(boolean refreshContent) {
        isContentRefreshesEnabled = true;
        if (refreshContent) {
            refreshRenderedCells();
            // Ensure that client gets a response
            requestRepaint();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.AbstractSelect#paintContent(com.vaadin.
     * terminal.PaintTarget)
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {

        // The tab ordering number
        if (getTabIndex() > 0) {
            target.addAttribute("tabindex", getTabIndex());
        }

        if (dragMode != TableDragMode.NONE) {
            target.addAttribute("dragmode", dragMode.ordinal());
        }

        if (multiSelectMode != MultiSelectMode.DEFAULT) {
            target.addAttribute("multiselectmode", multiSelectMode.ordinal());
        }

        // Initialize temps
        final Object[] colids = getVisibleColumns();
        final int cols = colids.length;
        final int first = getCurrentPageFirstItemIndex();
        int total = size();
        final int pagelen = getPageLength();
        final int colHeadMode = getColumnHeaderMode();
        final boolean colheads = colHeadMode != COLUMN_HEADER_MODE_HIDDEN;
        final Object[][] cells = getVisibleCells();
        final boolean iseditable = isEditable();
        int rows;
        if (reqRowsToPaint >= 0) {
            rows = reqRowsToPaint;
        } else {
            rows = cells[0].length;
            if (alwaysRecalculateColumnWidths) {
                // TODO experimental feature for now: tell the client to
                // recalculate column widths.
                // We'll only do this for paints that do not originate from
                // table scroll/cache requests (i.e when reqRowsToPaint<0)
                target.addAttribute("recalcWidths", true);
            }
        }

        if (!isNullSelectionAllowed() && getNullSelectionItemId() != null
                && containsId(getNullSelectionItemId())) {
            total--;
            rows--;
        }

        // selection support
        LinkedList<String> selectedKeys = new LinkedList<String>();
        if (isMultiSelect()) {
            HashSet sel = new HashSet((Set) getValue());
            Collection vids = getVisibleItemIds();
            for (Iterator it = vids.iterator(); it.hasNext();) {
                Object id = it.next();
                if (sel.contains(id)) {
                    selectedKeys.add(itemIdMapper.key(id));
                }
            }
        } else {
            Object value = getValue();
            if (value == null) {
                value = getNullSelectionItemId();
            }
            if (value != null) {
                selectedKeys.add(itemIdMapper.key(value));
            }
        }

        // Table attributes
        if (isSelectable()) {
            target.addAttribute("selectmode", (isMultiSelect() ? "multi"
                    : "single"));
        } else {
            target.addAttribute("selectmode", "none");
        }

        if (cacheRate != CACHE_RATE_DEFAULT) {
            target.addAttribute("cr", cacheRate);
        }

        target.addAttribute("cols", cols);
        target.addAttribute("rows", rows);

        if (!isNullSelectionAllowed()) {
            target.addAttribute("nsa", false);
        }

        target.addAttribute("firstrow",
                (reqFirstRowToPaint >= 0 ? reqFirstRowToPaint
                        : firstToBeRenderedInClient));
        target.addAttribute("totalrows", total);
        if (pagelen != 0) {
            target.addAttribute("pagelength", pagelen);
        }
        if (colheads) {
            target.addAttribute("colheaders", true);
        }
        if (getRowHeaderMode() != ROW_HEADER_MODE_HIDDEN) {
            target.addAttribute("rowheaders", true);
        }

        target.addAttribute("colfooters", columnFootersVisible);

        // Visible column order
        final Collection sortables = getSortableContainerPropertyIds();
        final ArrayList<String> visibleColOrder = new ArrayList<String>();
        for (final Iterator<Object> it = visibleColumns.iterator(); it
                .hasNext();) {
            final Object columnId = it.next();
            if (!isColumnCollapsed(columnId)) {
                visibleColOrder.add(columnIdMap.key(columnId));
            }
        }
        target.addAttribute("vcolorder", visibleColOrder.toArray());

        // Rows
        final Set<Action> actionSet = new LinkedHashSet<Action>();
        final boolean selectable = isSelectable();
        final boolean[] iscomponent = new boolean[visibleColumns.size()];
        int iscomponentIndex = 0;
        for (final Iterator<Object> it = visibleColumns.iterator(); it
                .hasNext() && iscomponentIndex < iscomponent.length;) {
            final Object columnId = it.next();
            if (columnGenerators.containsKey(columnId)) {
                iscomponent[iscomponentIndex++] = true;
            } else {
                final Class colType = getType(columnId);
                iscomponent[iscomponentIndex++] = colType != null
                        && Component.class.isAssignableFrom(colType);
            }
        }
        target.startTag("rows");
        // cells array contains all that are supposed to be visible on client,
        // but we'll start from the one requested by client
        int start = 0;
        if (reqFirstRowToPaint != -1 && firstToBeRenderedInClient != -1) {
            start = reqFirstRowToPaint - firstToBeRenderedInClient;
        }
        int end = cells[0].length;
        if (reqRowsToPaint != -1) {
            end = start + reqRowsToPaint;
        }
        // sanity check
        if (lastToBeRenderedInClient != -1 && lastToBeRenderedInClient < end) {
            end = lastToBeRenderedInClient + 1;
        }
        if (start > cells[CELL_ITEMID].length || start < 0) {
            start = 0;
        }

        for (int indexInRowbuffer = start; indexInRowbuffer < end; indexInRowbuffer++) {
            final Object itemId = cells[CELL_ITEMID][indexInRowbuffer];

            if (!isNullSelectionAllowed() && getNullSelectionItemId() != null
                    && itemId == getNullSelectionItemId()) {
                // Remove null selection item if null selection is not allowed
                continue;
            }

            paintRow(target, cells, iseditable, actionSet, iscomponent,
                    indexInRowbuffer, itemId);
        }
        target.endTag("rows");

        // The select variable is only enabled if selectable
        if (selectable) {
            target.addVariable(this, "selected",
                    selectedKeys.toArray(new String[selectedKeys.size()]));
        }

        // The cursors are only shown on pageable table
        if (first != 0 || getPageLength() > 0) {
            target.addVariable(this, "firstvisible", first);
        }

        // Sorting
        if (getContainerDataSource() instanceof Container.Sortable) {
            target.addVariable(this, "sortcolumn",
                    columnIdMap.key(sortContainerPropertyId));
            target.addVariable(this, "sortascending", sortAscending);
        }

        // Resets and paints "to be painted next" variables. Also reset
        // pageBuffer
        reqFirstRowToPaint = -1;
        reqRowsToPaint = -1;
        containerChangeToBeRendered = false;
        target.addVariable(this, "reqrows", reqRowsToPaint);
        target.addVariable(this, "reqfirstrow", reqFirstRowToPaint);

        // Actions
        if (!actionSet.isEmpty()) {
            target.addVariable(this, "action", "");
            target.startTag("actions");
            for (final Iterator<Action> it = actionSet.iterator(); it.hasNext();) {
                final Action a = it.next();
                target.startTag("action");
                if (a.getCaption() != null) {
                    target.addAttribute("caption", a.getCaption());
                }
                if (a.getIcon() != null) {
                    target.addAttribute("icon", a.getIcon());
                }
                target.addAttribute("key", actionMapper.key(a));
                target.endTag("action");
            }
            target.endTag("actions");
        }
        if (columnReorderingAllowed) {
            final String[] colorder = new String[visibleColumns.size()];
            int i = 0;
            for (final Iterator<Object> it = visibleColumns.iterator(); it
                    .hasNext() && i < colorder.length;) {
                colorder[i++] = columnIdMap.key(it.next());
            }
            target.addVariable(this, "columnorder", colorder);
        }
        // Available columns
        if (columnCollapsingAllowed) {
            final HashSet<Object> ccs = new HashSet<Object>();
            for (final Iterator<Object> i = visibleColumns.iterator(); i
                    .hasNext();) {
                final Object o = i.next();
                if (isColumnCollapsed(o)) {
                    ccs.add(o);
                }
            }
            final String[] collapsedkeys = new String[ccs.size()];
            int nextColumn = 0;
            for (final Iterator<Object> it = visibleColumns.iterator(); it
                    .hasNext() && nextColumn < collapsedkeys.length;) {
                final Object columnId = it.next();
                if (isColumnCollapsed(columnId)) {
                    collapsedkeys[nextColumn++] = columnIdMap.key(columnId);
                }
            }
            target.addVariable(this, "collapsedcolumns", collapsedkeys);
        }
        target.startTag("visiblecolumns");
        int i = 0;
        for (final Iterator<Object> it = visibleColumns.iterator(); it
                .hasNext(); i++) {
            final Object columnId = it.next();
            if (columnId != null) {
                target.startTag("column");
                target.addAttribute("cid", columnIdMap.key(columnId));
                final String head = getColumnHeader(columnId);
                target.addAttribute("caption", (head != null ? head : ""));
                final String foot = getColumnFooter(columnId);
                target.addAttribute("fcaption", (foot != null ? foot : ""));
                if (isColumnCollapsed(columnId)) {
                    target.addAttribute("collapsed", true);
                }
                if (colheads) {
                    if (getColumnIcon(columnId) != null) {
                        target.addAttribute("icon", getColumnIcon(columnId));
                    }
                    if (sortables.contains(columnId)) {
                        target.addAttribute("sortable", true);
                    }
                }
                if (!ALIGN_LEFT.equals(getColumnAlignment(columnId))) {
                    target.addAttribute("align", getColumnAlignment(columnId));
                }
                if (columnWidths.containsKey(columnId)) {
                    if (getColumnWidth(columnId) > -1) {
                        target.addAttribute("width",
                                String.valueOf(getColumnWidth(columnId)));
                    } else {
                        target.addAttribute("er",
                                getColumnExpandRatio(columnId));
                    }
                }
                target.endTag("column");
            }
        }
        target.endTag("visiblecolumns");

        if (dropHandler != null) {
            dropHandler.getAcceptCriterion().paint(target);
        }
    }

    private void paintRow(PaintTarget target, final Object[][] cells,
            final boolean iseditable, final Set<Action> actionSet,
            final boolean[] iscomponent, int indexInRowbuffer,
            final Object itemId) throws PaintException {
        target.startTag("tr");

        paintRowAttributes(target, cells, actionSet, indexInRowbuffer, itemId);

        // cells
        int currentColumn = 0;
        for (final Iterator<Object> it = visibleColumns.iterator(); it
                .hasNext(); currentColumn++) {
            final Object columnId = it.next();
            if (columnId == null || isColumnCollapsed(columnId)) {
                continue;
            }
            /*
             * For each cell, if a cellStyleGenerator is specified, get the
             * specific style for the cell. If there is any, add it to the
             * target.
             */
            if (cellStyleGenerator != null) {
                String cellStyle = cellStyleGenerator
                        .getStyle(itemId, columnId);
                if (cellStyle != null && !cellStyle.equals("")) {
                    target.addAttribute("style-" + columnIdMap.key(columnId),
                            cellStyle);
                }
            }
            if ((iscomponent[currentColumn] || iseditable)
                    && Component.class.isInstance(cells[CELL_FIRSTCOL
                            + currentColumn][indexInRowbuffer])) {
                final Component c = (Component) cells[CELL_FIRSTCOL
                        + currentColumn][indexInRowbuffer];
                if (c == null) {
                    target.addText("");
                } else {
                    c.paint(target);
                }
            } else {
                target.addText((String) cells[CELL_FIRSTCOL + currentColumn][indexInRowbuffer]);
            }
        }

        target.endTag("tr");
    }

    private void paintRowAttributes(PaintTarget target, final Object[][] cells,
            final Set<Action> actionSet, int indexInRowbuffer,
            final Object itemId) throws PaintException {
        // tr attributes

        paintRowIcon(target, cells, indexInRowbuffer);
        paintRowHeader(target, cells, indexInRowbuffer);
        target.addAttribute("key",
                Integer.parseInt(cells[CELL_KEY][indexInRowbuffer].toString()));

        if (isSelected(itemId)) {
            target.addAttribute("selected", true);
        }

        // Actions
        if (actionHandlers != null) {
            final ArrayList<String> keys = new ArrayList<String>();
            for (final Iterator<Handler> ahi = actionHandlers.iterator(); ahi
                    .hasNext();) {
                final Action[] aa = (ahi.next()).getActions(itemId, this);
                if (aa != null) {
                    for (int ai = 0; ai < aa.length; ai++) {
                        final String key = actionMapper.key(aa[ai]);
                        actionSet.add(aa[ai]);
                        keys.add(key);
                    }
                }
            }
            target.addAttribute("al", keys.toArray());
        }

        /*
         * For each row, if a cellStyleGenerator is specified, get the specific
         * style for the cell, using null as propertyId. If there is any, add it
         * to the target.
         */
        if (cellStyleGenerator != null) {
            String rowStyle = cellStyleGenerator.getStyle(itemId, null);
            if (rowStyle != null && !rowStyle.equals("")) {
                target.addAttribute("rowstyle", rowStyle);
            }
        }
        paintRowAttributes(target, itemId);
    }

    protected void paintRowHeader(PaintTarget target, Object[][] cells,
            int indexInRowbuffer) throws PaintException {
        if (getRowHeaderMode() != ROW_HEADER_MODE_HIDDEN) {
            if (cells[CELL_HEADER][indexInRowbuffer] != null) {
                target.addAttribute("caption",
                        (String) cells[CELL_HEADER][indexInRowbuffer]);
            }
        }

    }

    protected void paintRowIcon(PaintTarget target, final Object[][] cells,
            int indexInRowbuffer) throws PaintException {
        if (getRowHeaderMode() != ROW_HEADER_MODE_HIDDEN
                && cells[CELL_ICON][indexInRowbuffer] != null) {
            target.addAttribute("icon",
                    (Resource) cells[CELL_ICON][indexInRowbuffer]);
        }
    }

    /**
     * A method where extended Table implementations may add their custom
     * attributes for rows.
     * 
     * @param target
     * @param itemId
     */
    protected void paintRowAttributes(PaintTarget target, Object itemId)
            throws PaintException {

    }

    /**
     * Gets the cached visible table contents.
     * 
     * @return the cached visible table contents.
     */
    private Object[][] getVisibleCells() {
        if (pageBuffer == null) {
            refreshRenderedCells();
        }
        return pageBuffer;
    }

    /**
     * Gets the value of property.
     * 
     * By default if the table is editable the fieldFactory is used to create
     * editors for table cells. Otherwise formatPropertyValue is used to format
     * the value representation.
     * 
     * @param rowId
     *            the Id of the row (same as item Id).
     * @param colId
     *            the Id of the column.
     * @param property
     *            the Property to be presented.
     * @return Object Either formatted value or Component for field.
     * @see #setTableFieldFactory(TableFieldFactory)
     */
    protected Object getPropertyValue(Object rowId, Object colId,
            Property property) {
        if (isEditable() && fieldFactory != null) {
            final Field f = fieldFactory.createField(getContainerDataSource(),
                    rowId, colId, this);
            if (f != null) {
                f.setPropertyDataSource(property);
                return f;
            }
        }

        return formatPropertyValue(rowId, colId, property);
    }

    /**
     * Formats table cell property values. By default the property.toString()
     * and return a empty string for null properties.
     * 
     * @param rowId
     *            the Id of the row (same as item Id).
     * @param colId
     *            the Id of the column.
     * @param property
     *            the Property to be formatted.
     * @return the String representation of property and its value.
     * @since 3.1
     */
    protected String formatPropertyValue(Object rowId, Object colId,
            Property property) {
        if (property == null) {
            return "";
        }
        return property.toString();
    }

    /* Action container */

    /**
     * Registers a new action handler for this container
     * 
     * @see com.vaadin.event.Action.Container#addActionHandler(Action.Handler)
     */
    public void addActionHandler(Action.Handler actionHandler) {

        if (actionHandler != null) {

            if (actionHandlers == null) {
                actionHandlers = new LinkedList<Handler>();
                actionMapper = new KeyMapper();
            }

            if (!actionHandlers.contains(actionHandler)) {
                actionHandlers.add(actionHandler);
                requestRepaint();
            }

        }
    }

    /**
     * Removes a previously registered action handler for the contents of this
     * container.
     * 
     * @see com.vaadin.event.Action.Container#removeActionHandler(Action.Handler)
     */
    public void removeActionHandler(Action.Handler actionHandler) {

        if (actionHandlers != null && actionHandlers.contains(actionHandler)) {

            actionHandlers.remove(actionHandler);

            if (actionHandlers.isEmpty()) {
                actionHandlers = null;
                actionMapper = null;
            }

            requestRepaint();
        }
    }

    /**
     * Removes all action handlers
     */
    public void removeAllActionHandlers() {
        actionHandlers = null;
        actionMapper = null;
        requestRepaint();
    }

    /* Property value change listening support */

    /**
     * Notifies this listener that the Property's value has changed.
     * 
     * Also listens changes in rendered items to refresh content area.
     * 
     * @see com.vaadin.data.Property.ValueChangeListener#valueChange(Property.ValueChangeEvent)
     */
    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        if (event.getProperty() == this) {
            super.valueChange(event);
        } else {
            resetPageBuffer();
            refreshRenderedCells();
            containerChangeToBeRendered = true;
        }
        requestRepaint();
    }

    protected void resetPageBuffer() {
        firstToBeRenderedInClient = -1;
        lastToBeRenderedInClient = -1;
        reqFirstRowToPaint = -1;
        reqRowsToPaint = -1;
        pageBuffer = null;
    }

    /**
     * Notifies the component that it is connected to an application.
     * 
     * @see com.vaadin.ui.Component#attach()
     */
    @Override
    public void attach() {
        super.attach();

        refreshRenderedCells();

        if (visibleComponents != null) {
            for (final Iterator<Component> i = visibleComponents.iterator(); i
                    .hasNext();) {
                i.next().attach();
            }
        }
    }

    /**
     * Notifies the component that it is detached from the application
     * 
     * @see com.vaadin.ui.Component#detach()
     */
    @Override
    public void detach() {
        super.detach();

        if (visibleComponents != null) {
            for (final Iterator<Component> i = visibleComponents.iterator(); i
                    .hasNext();) {
                i.next().detach();
            }
        }
    }

    /**
     * Removes all Items from the Container.
     * 
     * @see com.vaadin.data.Container#removeAllItems()
     */
    @Override
    public boolean removeAllItems() {
        currentPageFirstItemId = null;
        currentPageFirstItemIndex = 0;
        return super.removeAllItems();
    }

    /**
     * Removes the Item identified by <code>ItemId</code> from the Container.
     * 
     * @see com.vaadin.data.Container#removeItem(Object)
     */
    @Override
    public boolean removeItem(Object itemId) {
        final Object nextItemId = nextItemId(itemId);
        final boolean ret = super.removeItem(itemId);
        if (ret && (itemId != null) && (itemId.equals(currentPageFirstItemId))) {
            currentPageFirstItemId = nextItemId;
        }
        if (!(items instanceof Container.ItemSetChangeNotifier)) {
            resetPageBuffer();
            refreshRenderedCells();
        }
        return ret;
    }

    /**
     * Removes a Property specified by the given Property ID from the Container.
     * 
     * @see com.vaadin.data.Container#removeContainerProperty(Object)
     */
    @Override
    public boolean removeContainerProperty(Object propertyId)
            throws UnsupportedOperationException {

        // If a visible property is removed, remove the corresponding column
        visibleColumns.remove(propertyId);
        columnAlignments.remove(propertyId);
        columnIcons.remove(propertyId);
        columnHeaders.remove(propertyId);
        columnFooters.remove(propertyId);

        return super.removeContainerProperty(propertyId);
    }

    /**
     * Adds a new property to the table and show it as a visible column.
     * 
     * @param propertyId
     *            the Id of the proprty.
     * @param type
     *            the class of the property.
     * @param defaultValue
     *            the default value given for all existing items.
     * @see com.vaadin.data.Container#addContainerProperty(Object, Class,
     *      Object)
     */
    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type,
            Object defaultValue) throws UnsupportedOperationException {

        boolean visibleColAdded = false;
        if (!visibleColumns.contains(propertyId)) {
            visibleColumns.add(propertyId);
            visibleColAdded = true;
        }

        if (!super.addContainerProperty(propertyId, type, defaultValue)) {
            if (visibleColAdded) {
                visibleColumns.remove(propertyId);
            }
            return false;
        }
        if (!(items instanceof Container.PropertySetChangeNotifier)) {
            resetPageBuffer();
            refreshRenderedCells();
        }
        return true;
    }

    /**
     * Adds a new property to the table and show it as a visible column.
     * 
     * @param propertyId
     *            the Id of the proprty
     * @param type
     *            the class of the property
     * @param defaultValue
     *            the default value given for all existing items
     * @param columnHeader
     *            the Explicit header of the column. If explicit header is not
     *            needed, this should be set null.
     * @param columnIcon
     *            the Icon of the column. If icon is not needed, this should be
     *            set null.
     * @param columnAlignment
     *            the Alignment of the column. Null implies align left.
     * @throws UnsupportedOperationException
     *             if the operation is not supported.
     * @see com.vaadin.data.Container#addContainerProperty(Object, Class,
     *      Object)
     */
    public boolean addContainerProperty(Object propertyId, Class<?> type,
            Object defaultValue, String columnHeader, Resource columnIcon,
            String columnAlignment) throws UnsupportedOperationException {
        if (!this.addContainerProperty(propertyId, type, defaultValue)) {
            return false;
        }
        setColumnAlignment(propertyId, columnAlignment);
        setColumnHeader(propertyId, columnHeader);
        setColumnIcon(propertyId, columnIcon);
        return true;
    }

    /**
     * Adds a generated column to the Table.
     * <p>
     * A generated column is a column that exists only in the Table, not as a
     * property in the underlying Container. It shows up just as a regular
     * column.
     * </p>
     * <p>
     * A generated column will override a property with the same id, so that the
     * generated column is shown instead of the column representing the
     * property. Note that getContainerProperty() will still get the real
     * property.
     * </p>
     * <p>
     * Table will not listen to value change events from properties overridden
     * by generated columns. If the content of your generated column depends on
     * properties that are not directly visible in the table, attach value
     * change listener to update the content on all depended properties.
     * Otherwise your UI might not get updated as expected.
     * </p>
     * <p>
     * Also note that getVisibleColumns() will return the generated columns,
     * while getContainerPropertyIds() will not.
     * </p>
     * 
     * @param id
     *            the id of the column to be added
     * @param generatedColumn
     *            the {@link ColumnGenerator} to use for this column
     */
    public void addGeneratedColumn(Object id, ColumnGenerator generatedColumn) {
        if (generatedColumn == null) {
            throw new IllegalArgumentException(
                    "Can not add null as a GeneratedColumn");
        }
        if (columnGenerators.containsKey(id)) {
            throw new IllegalArgumentException(
                    "Can not add the same GeneratedColumn twice, id:" + id);
        } else {
            columnGenerators.put(id, generatedColumn);
            /*
             * add to visible column list unless already there (overriding
             * column from DS)
             */
            if (!visibleColumns.contains(id)) {
                visibleColumns.add(id);
            }
            resetPageBuffer();
            refreshRenderedCells();
        }
    }

    /**
     * Removes a generated column previously added with addGeneratedColumn.
     * 
     * @param columnId
     *            id of the generated column to remove
     * @return true if the column could be removed (existed in the Table)
     */
    public boolean removeGeneratedColumn(Object columnId) {
        if (columnGenerators.containsKey(columnId)) {
            columnGenerators.remove(columnId);
            // remove column from visibleColumns list unless it exists in
            // container (generator previously overrode this column)
            if (!items.getContainerPropertyIds().contains(columnId)) {
                visibleColumns.remove(columnId);
            }
            resetPageBuffer();
            refreshRenderedCells();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the list of items on the current page
     * 
     * @see com.vaadin.ui.Select#getVisibleItemIds()
     */
    @Override
    public Collection getVisibleItemIds() {

        final LinkedList<Object> visible = new LinkedList<Object>();

        final Object[][] cells = getVisibleCells();
        for (int i = 0; i < cells[CELL_ITEMID].length; i++) {
            visible.add(cells[CELL_ITEMID][i]);
        }

        return visible;
    }

    /**
     * Container datasource item set change. Table must flush its buffers on
     * change.
     * 
     * @see com.vaadin.data.Container.ItemSetChangeListener#containerItemSetChange(com.vaadin.data.Container.ItemSetChangeEvent)
     */
    @Override
    public void containerItemSetChange(Container.ItemSetChangeEvent event) {
        super.containerItemSetChange(event);
        if (event instanceof IndexedContainer.ItemSetChangeEvent) {
            IndexedContainer.ItemSetChangeEvent evt = (IndexedContainer.ItemSetChangeEvent) event;
            // if the event is not a global one and the added item is outside
            // the visible/buffered area, no need to do anything
            if (evt.getAddedItemIndex() != -1
                    && (firstToBeRenderedInClient >= 0)
                    && (lastToBeRenderedInClient >= 0)
                    && (firstToBeRenderedInClient > evt.getAddedItemIndex() || lastToBeRenderedInClient < evt
                            .getAddedItemIndex())) {
                return;
            }
        }
        // ensure that page still has first item in page, ignore buffer refresh
        // (forced in this method)
        setCurrentPageFirstItemIndex(getCurrentPageFirstItemIndex(), false);

        resetPageBuffer();
        refreshRenderedCells();
    }

    /**
     * Container datasource property set change. Table must flush its buffers on
     * change.
     * 
     * @see com.vaadin.data.Container.PropertySetChangeListener#containerPropertySetChange(com.vaadin.data.Container.PropertySetChangeEvent)
     */
    @Override
    public void containerPropertySetChange(
            Container.PropertySetChangeEvent event) {
        disableContentRefreshing();
        super.containerPropertySetChange(event);

        // sanitetize visibleColumns. note that we are not adding previously
        // non-existing properties as columns
        Collection<?> containerPropertyIds = getContainerDataSource()
                .getContainerPropertyIds();

        LinkedList<Object> newVisibleColumns = new LinkedList<Object>(
                visibleColumns);
        for (Iterator<Object> iterator = newVisibleColumns.iterator(); iterator
                .hasNext();) {
            Object id = iterator.next();
            if (!(containerPropertyIds.contains(id) || columnGenerators
                    .containsKey(id))) {
                iterator.remove();
            }
        }
        setVisibleColumns(newVisibleColumns.toArray());
        // same for collapsed columns
        for (Iterator<Object> iterator = collapsedColumns.iterator(); iterator
                .hasNext();) {
            Object id = iterator.next();
            if (!(containerPropertyIds.contains(id) || columnGenerators
                    .containsKey(id))) {
                iterator.remove();
            }
        }

        resetPageBuffer();
        enableContentRefreshing(true);
    }

    /**
     * Adding new items is not supported.
     * 
     * @throws UnsupportedOperationException
     *             if set to true.
     * @see com.vaadin.ui.Select#setNewItemsAllowed(boolean)
     */
    @Override
    public void setNewItemsAllowed(boolean allowNewOptions)
            throws UnsupportedOperationException {
        if (allowNewOptions) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Gets the ID of the Item following the Item that corresponds to itemId.
     * 
     * @see com.vaadin.data.Container.Ordered#nextItemId(java.lang.Object)
     */
    public Object nextItemId(Object itemId) {
        return ((Container.Ordered) items).nextItemId(itemId);
    }

    /**
     * Gets the ID of the Item preceding the Item that corresponds to the
     * itemId.
     * 
     * @see com.vaadin.data.Container.Ordered#prevItemId(java.lang.Object)
     */
    public Object prevItemId(Object itemId) {
        return ((Container.Ordered) items).prevItemId(itemId);
    }

    /**
     * Gets the ID of the first Item in the Container.
     * 
     * @see com.vaadin.data.Container.Ordered#firstItemId()
     */
    public Object firstItemId() {
        return ((Container.Ordered) items).firstItemId();
    }

    /**
     * Gets the ID of the last Item in the Container.
     * 
     * @see com.vaadin.data.Container.Ordered#lastItemId()
     */
    public Object lastItemId() {
        return ((Container.Ordered) items).lastItemId();
    }

    /**
     * Tests if the Item corresponding to the given Item ID is the first Item in
     * the Container.
     * 
     * @see com.vaadin.data.Container.Ordered#isFirstId(java.lang.Object)
     */
    public boolean isFirstId(Object itemId) {
        return ((Container.Ordered) items).isFirstId(itemId);
    }

    /**
     * Tests if the Item corresponding to the given Item ID is the last Item in
     * the Container.
     * 
     * @see com.vaadin.data.Container.Ordered#isLastId(java.lang.Object)
     */
    public boolean isLastId(Object itemId) {
        return ((Container.Ordered) items).isLastId(itemId);
    }

    /**
     * Adds new item after the given item.
     * 
     * @see com.vaadin.data.Container.Ordered#addItemAfter(java.lang.Object)
     */
    public Object addItemAfter(Object previousItemId)
            throws UnsupportedOperationException {
        Object itemId = ((Container.Ordered) items)
                .addItemAfter(previousItemId);
        if (!(items instanceof Container.ItemSetChangeNotifier)) {
            resetPageBuffer();
            refreshRenderedCells();
        }
        return itemId;
    }

    /**
     * Adds new item after the given item.
     * 
     * @see com.vaadin.data.Container.Ordered#addItemAfter(java.lang.Object,
     *      java.lang.Object)
     */
    public Item addItemAfter(Object previousItemId, Object newItemId)
            throws UnsupportedOperationException {
        Item item = ((Container.Ordered) items).addItemAfter(previousItemId,
                newItemId);
        if (!(items instanceof Container.ItemSetChangeNotifier)) {
            resetPageBuffer();
            refreshRenderedCells();
        }
        return item;
    }

    /**
     * Sets the TableFieldFactory that is used to create editor for table cells.
     * 
     * The TableFieldFactory is only used if the Table is editable. By default
     * the DefaultFieldFactory is used.
     * 
     * @param fieldFactory
     *            the field factory to set.
     * @see #isEditable
     * @see DefaultFieldFactory
     */
    public void setTableFieldFactory(TableFieldFactory fieldFactory) {
        this.fieldFactory = fieldFactory;
    }

    /**
     * Gets the TableFieldFactory that is used to create editor for table cells.
     * 
     * The FieldFactory is only used if the Table is editable.
     * 
     * @return TableFieldFactory used to create the Field instances.
     * @see #isEditable
     */
    public TableFieldFactory getTableFieldFactory() {
        return fieldFactory;
    }

    /**
     * Gets the FieldFactory that is used to create editor for table cells.
     * 
     * The FieldFactory is only used if the Table is editable.
     * 
     * @return FieldFactory used to create the Field instances.
     * @see #isEditable
     * @deprecated use {@link #getTableFieldFactory()} instead
     */
    @Deprecated
    public FieldFactory getFieldFactory() {
        if (fieldFactory instanceof FieldFactory) {
            return (FieldFactory) fieldFactory;

        }
        return null;
    }

    /**
     * Sets the FieldFactory that is used to create editor for table cells.
     * 
     * The FieldFactory is only used if the Table is editable. By default the
     * BaseFieldFactory is used.
     * 
     * @param fieldFactory
     *            the field factory to set.
     * @see #isEditable
     * @see BaseFieldFactory
     * @deprecated use {@link #setTableFieldFactory(TableFieldFactory)} instead
     */
    @Deprecated
    public void setFieldFactory(FieldFactory fieldFactory) {
        this.fieldFactory = fieldFactory;

        // Assure visual refresh
        resetPageBuffer();
        refreshRenderedCells();
    }

    /**
     * Is table editable.
     * 
     * If table is editable a editor of type Field is created for each table
     * cell. The assigned FieldFactory is used to create the instances.
     * 
     * To provide custom editors for table cells create a class implementins the
     * FieldFactory interface, and assign it to table, and set the editable
     * property to true.
     * 
     * @return true if table is editable, false oterwise.
     * @see Field
     * @see FieldFactory
     * 
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Sets the editable property.
     * 
     * If table is editable a editor of type Field is created for each table
     * cell. The assigned FieldFactory is used to create the instances.
     * 
     * To provide custom editors for table cells create a class implementins the
     * FieldFactory interface, and assign it to table, and set the editable
     * property to true.
     * 
     * @param editable
     *            true if table should be editable by user.
     * @see Field
     * @see FieldFactory
     * 
     */
    public void setEditable(boolean editable) {
        this.editable = editable;

        // Assure visual refresh
        resetPageBuffer();
        refreshRenderedCells();
    }

    /**
     * Sorts the table.
     * 
     * @throws UnsupportedOperationException
     *             if the container data source does not implement
     *             Container.Sortable
     * @see com.vaadin.data.Container.Sortable#sort(java.lang.Object[],
     *      boolean[])
     * 
     */
    public void sort(Object[] propertyId, boolean[] ascending)
            throws UnsupportedOperationException {
        final Container c = getContainerDataSource();
        if (c instanceof Container.Sortable) {
            final int pageIndex = getCurrentPageFirstItemIndex();
            ((Container.Sortable) c).sort(propertyId, ascending);
            setCurrentPageFirstItemIndex(pageIndex);
            resetPageBuffer();
            refreshRenderedCells();

        } else if (c != null) {
            throw new UnsupportedOperationException(
                    "Underlying Data does not allow sorting");
        }
    }

    /**
     * Sorts the table by currently selected sorting column.
     * 
     * @throws UnsupportedOperationException
     *             if the container data source does not implement
     *             Container.Sortable
     */
    public void sort() {
        if (getSortContainerPropertyId() == null) {
            return;
        }
        sort(new Object[] { sortContainerPropertyId },
                new boolean[] { sortAscending });
    }

    /**
     * Gets the container property IDs, which can be used to sort the item.
     * 
     * @see com.vaadin.data.Container.Sortable#getSortableContainerPropertyIds()
     */
    public Collection getSortableContainerPropertyIds() {
        final Container c = getContainerDataSource();
        if (c instanceof Container.Sortable && !isSortDisabled()) {
            return ((Container.Sortable) c).getSortableContainerPropertyIds();
        } else {
            return new LinkedList();
        }
    }

    /**
     * Gets the currently sorted column property ID.
     * 
     * @return the Container property id of the currently sorted column.
     */
    public Object getSortContainerPropertyId() {
        return sortContainerPropertyId;
    }

    /**
     * Sets the currently sorted column property id.
     * 
     * @param propertyId
     *            the Container property id of the currently sorted column.
     */
    public void setSortContainerPropertyId(Object propertyId) {
        setSortContainerPropertyId(propertyId, true);
    }

    /**
     * Internal method to set currently sorted column property id. With doSort
     * flag actual sorting may be bypassed.
     * 
     * @param propertyId
     * @param doSort
     */
    private void setSortContainerPropertyId(Object propertyId, boolean doSort) {
        if ((sortContainerPropertyId != null && !sortContainerPropertyId
                .equals(propertyId))
                || (sortContainerPropertyId == null && propertyId != null)) {
            sortContainerPropertyId = propertyId;

            if (doSort) {
                sort();
                // Assures the visual refresh
                refreshRenderedCells();
            }
        }
    }

    /**
     * Is the table currently sorted in ascending order.
     * 
     * @return <code>true</code> if ascending, <code>false</code> if descending.
     */
    public boolean isSortAscending() {
        return sortAscending;
    }

    /**
     * Sets the table in ascending order.
     * 
     * @param ascending
     *            <code>true</code> if ascending, <code>false</code> if
     *            descending.
     */
    public void setSortAscending(boolean ascending) {
        setSortAscending(ascending, true);
    }

    /**
     * Internal method to set sort ascending. With doSort flag actual sort can
     * be bypassed.
     * 
     * @param ascending
     * @param doSort
     */
    private void setSortAscending(boolean ascending, boolean doSort) {
        if (sortAscending != ascending) {
            sortAscending = ascending;
            if (doSort) {
                sort();
            }
        }
        // Assures the visual refresh
        refreshRenderedCells();
    }

    /**
     * Is sorting disabled altogether.
     * 
     * True iff no sortable columns are given even in the case where data source
     * would support this.
     * 
     * @return True iff sorting is disabled.
     */
    public boolean isSortDisabled() {
        return sortDisabled;
    }

    /**
     * Disables the sorting altogether.
     * 
     * To disable sorting altogether, set to true. In this case no sortable
     * columns are given even in the case where datasource would support this.
     * 
     * @param sortDisabled
     *            True iff sorting is disabled.
     */
    public void setSortDisabled(boolean sortDisabled) {
        if (this.sortDisabled != sortDisabled) {
            this.sortDisabled = sortDisabled;
            refreshRenderedCells();
        }
    }

    /**
     * Table does not support lazy options loading mode. Setting this true will
     * throw UnsupportedOperationException.
     * 
     * @see com.vaadin.ui.Select#setLazyLoading(boolean)
     */
    public void setLazyLoading(boolean useLazyLoading) {
        if (useLazyLoading) {
            throw new UnsupportedOperationException(
                    "Lazy options loading is not supported by Table.");
        }
    }

    /**
     * Used to create "generated columns"; columns that exist only in the Table,
     * not in the underlying Container. Implement this interface and pass it to
     * Table.addGeneratedColumn along with an id for the column to be generated.
     * 
     */
    public interface ColumnGenerator extends Serializable {

        /**
         * Called by Table when a cell in a generated column needs to be
         * generated.
         * 
         * @param source
         *            the source Table
         * @param itemId
         *            the itemId (aka rowId) for the of the cell to be generated
         * @param columnId
         *            the id for the generated column (as specified in
         *            addGeneratedColumn)
         * @return
         */
        public abstract Component generateCell(Table source, Object itemId,
                Object columnId);
    }

    /**
     * Set cell style generator for Table.
     * 
     * @param cellStyleGenerator
     *            New cell style generator or null to remove generator.
     */
    public void setCellStyleGenerator(CellStyleGenerator cellStyleGenerator) {
        this.cellStyleGenerator = cellStyleGenerator;
        requestRepaint();
    }

    /**
     * Get the current cell style generator.
     * 
     */
    public CellStyleGenerator getCellStyleGenerator() {
        return cellStyleGenerator;
    }

    /**
     * Allow to define specific style on cells (and rows) contents. Implements
     * this interface and pass it to Table.setCellStyleGenerator. Row styles are
     * generated when porpertyId is null. The CSS class name that will be added
     * to the cell content is <tt>v-table-cell-content-[style name]</tt>, and
     * the row style will be <tt>v-table-row-[style name]</tt>.
     */
    public interface CellStyleGenerator extends Serializable {

        /**
         * Called by Table when a cell (and row) is painted.
         * 
         * @param itemId
         *            The itemId of the painted cell
         * @param propertyId
         *            The propertyId of the cell, null when getting row style
         * @return The style name to add to this cell or row. (the CSS class
         *         name will be v-table-cell-content-[style name], or
         *         v-table-row-[style name] for rows)
         */
        public abstract String getStyle(Object itemId, Object propertyId);
    }

    public void addListener(ItemClickListener listener) {
        addListener(VScrollTable.ITEM_CLICK_EVENT_ID, ItemClickEvent.class,
                listener, ItemClickEvent.ITEM_CLICK_METHOD);
    }

    public void removeListener(ItemClickListener listener) {
        removeListener(VScrollTable.ITEM_CLICK_EVENT_ID, ItemClickEvent.class,
                listener);
    }

    // Identical to AbstractCompoenentContainer.setEnabled();
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (getParent() != null && !getParent().isEnabled()) {
            // some ancestor still disabled, don't update children
            return;
        } else {
            requestRepaintAll();
        }
    }

    // Virtually identical to AbstractCompoenentContainer.setEnabled();
    public void requestRepaintAll() {
        requestRepaint();
        if (visibleComponents != null) {
            for (Iterator<Component> childIterator = visibleComponents
                    .iterator(); childIterator.hasNext();) {
                Component c = childIterator.next();
                if (c instanceof Form) {
                    // Form has children in layout, but is not
                    // ComponentContainer
                    c.requestRepaint();
                    ((Form) c).getLayout().requestRepaintAll();
                } else if (c instanceof Table) {
                    ((Table) c).requestRepaintAll();
                } else if (c instanceof ComponentContainer) {
                    ((ComponentContainer) c).requestRepaintAll();
                } else {
                    c.requestRepaint();
                }
            }
        }
    }

    /**
     * Sets the drag start mode of the Table. Drag start mode controls how Table
     * behaves as a drag source.
     * 
     * @param newDragMode
     */
    public void setDragMode(TableDragMode newDragMode) {
        dragMode = newDragMode;
        requestRepaint();
    }

    /**
     * @return the current start mode of the Table. Drag start mode controls how
     *         Table behaves as a drag source.
     */
    public TableDragMode getDragMode() {
        return dragMode;
    }

    /**
     * Concrete implementation of {@link DataBoundTransferable} for data
     * transferred from a table.
     * 
     * @see {@link DataBoundTransferable}.
     * 
     * @since 6.3
     */
    public class TableTransferable extends DataBoundTransferable {

        protected TableTransferable(Map<String, Object> rawVariables) {
            super(Table.this, rawVariables);
            Object object = rawVariables.get("itemId");
            if (object != null) {
                setData("itemId", itemIdMapper.get((String) object));
            }
            object = rawVariables.get("propertyId");
            if (object != null) {
                setData("propertyId", columnIdMap.get((String) object));
            }
        }

        @Override
        public Object getItemId() {
            return getData("itemId");
        }

        @Override
        public Object getPropertyId() {
            return getData("propertyId");
        }

        @Override
        public Table getSourceComponent() {
            return (Table) super.getSourceComponent();
        }

    }

    public TableTransferable getTransferable(Map<String, Object> rawVariables) {
        TableTransferable transferable = new TableTransferable(rawVariables);
        return transferable;
    }

    public DropHandler getDropHandler() {
        return dropHandler;
    }

    public void setDropHandler(DropHandler dropHandler) {
        this.dropHandler = dropHandler;
    }

    public AbstractSelectTargetDetails translateDropTargetDetails(
            Map<String, Object> clientVariables) {
        return new AbstractSelectTargetDetails(clientVariables);
    }

    /**
     * Sets the behavior of how the multi-select mode should behave when the
     * table is both selectable and in multi-select mode.
     * 
     * @param mode
     *            The select mode of the table
     */
    public void setMultiSelectMode(MultiSelectMode mode) {
        multiSelectMode = mode;
        requestRepaint();
    }

    /**
     * Returns the select mode in which multi-select is used.
     * 
     * @return The multi select mode
     */
    public MultiSelectMode getMultiSelectMode() {
        return multiSelectMode;
    }

    /**
     * Lazy loading accept criterion for Table. Accepted target rows are loaded
     * from server once per drag and drop operation. Developer must override one
     * method that decides on which rows the currently dragged data can be
     * dropped.
     * 
     * <p>
     * Initially pretty much no data is sent to client. On first required
     * criterion check (per drag request) the client side data structure is
     * initialized from server and no subsequent requests requests are needed
     * during that drag and drop operation.
     */
    @ClientCriterion(VLazyInitItemIdentifiers.class)
    public static abstract class TableDropCriterion extends ServerSideCriterion {

        private Table table;

        private Set<Object> allowedItemIds;

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.vaadin.event.dd.acceptcriteria.ServerSideCriterion#getIdentifier
         * ()
         */
        @Override
        protected String getIdentifier() {
            return TableDropCriterion.class.getCanonicalName();
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.vaadin.event.dd.acceptcriteria.AcceptCriterion#accepts(com.vaadin
         * .event.dd.DragAndDropEvent)
         */
        public boolean accept(DragAndDropEvent dragEvent) {
            AbstractSelectTargetDetails dropTargetData = (AbstractSelectTargetDetails) dragEvent
                    .getTargetDetails();
            table = (Table) dragEvent.getTargetDetails().getTarget();
            ArrayList<Object> visibleItemIds = new ArrayList<Object>(
                    table.getPageLength());
            visibleItemIds.size();
            Object id = table.getCurrentPageFirstItemId();
            for (int i = 0; i < table.getPageLength() && id != null; i++) {
                visibleItemIds.add(id);
                id = table.nextItemId(id);
            }
            allowedItemIds = getAllowedItemIds(dragEvent, table, visibleItemIds);

            return allowedItemIds.contains(dropTargetData.getItemIdOver());
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.vaadin.event.dd.acceptcriteria.AcceptCriterion#paintResponse(
         * com.vaadin.terminal.PaintTarget)
         */
        @Override
        public void paintResponse(PaintTarget target) throws PaintException {
            /*
             * send allowed nodes to client so subsequent requests can be
             * avoided
             */
            Object[] array = allowedItemIds.toArray();
            for (int i = 0; i < array.length; i++) {
                String key = table.itemIdMapper.key(array[i]);
                array[i] = key;
            }
            target.addAttribute("allowedIds", array);
        }

        /**
         * @param dragEvent
         * @param table
         *            the table for which the allowed item identifiers are
         *            defined
         * @param visibleItemIds
         *            the list of currently rendered item identifiers, accepted
         *            item id's need to be detected only for these visible items
         * @return the set of identifiers for items on which the dragEvent will
         *         be accepted
         */
        protected abstract Set<Object> getAllowedItemIds(
                DragAndDropEvent dragEvent, Table table,
                Collection<Object> visibleItemIds);

    }

    /**
     * Click event fired when clicking on the Table headers. The event includes
     * a reference the the Table the event originated from, the property id of
     * the column which header was pressed and details about the mouse event
     * itself.
     */
    public static class HeaderClickEvent extends ClickEvent {
        public static final Method HEADER_CLICK_METHOD;

        static {
            try {
                // Set the header click method
                HEADER_CLICK_METHOD = HeaderClickListener.class
                        .getDeclaredMethod("headerClick",
                                new Class[] { HeaderClickEvent.class });
            } catch (final java.lang.NoSuchMethodException e) {
                // This should never happen
                throw new java.lang.RuntimeException();
            }
        }

        // The property id of the column which header was pressed
        private Object columnPropertyId;

        public HeaderClickEvent(Component source, Object propertyId,
                MouseEventDetails details) {
            super(source, details);
            columnPropertyId = propertyId;
        }

        /**
         * Gets the property id of the column which header was pressed
         * 
         * @return The column propety id
         */
        public Object getPropertyId() {
            return columnPropertyId;
        }
    }

    /**
     * Click event fired when clicking on the Table footers. The event includes
     * a reference the the Table the event originated from, the property id of
     * the column which header was pressed and details about the mouse event
     * itself.
     */
    public static class FooterClickEvent extends ClickEvent {
        public static final Method FOOTER_CLICK_METHOD;

        static {
            try {
                // Set the header click method
                FOOTER_CLICK_METHOD = FooterClickListener.class
                        .getDeclaredMethod("footerClick",
                                new Class[] { FooterClickEvent.class });
            } catch (final java.lang.NoSuchMethodException e) {
                // This should never happen
                throw new java.lang.RuntimeException();
            }
        }

        // The property id of the column which header was pressed
        private Object columnPropertyId;

        /**
         * Constructor
         * 
         * @param source
         *            The source of the component
         * @param propertyId
         *            The propertyId of the column
         * @param details
         *            The mouse details of the click
         */
        public FooterClickEvent(Component source, Object propertyId,
                MouseEventDetails details) {
            super(source, details);
            columnPropertyId = propertyId;
        }

        /**
         * Gets the property id of the column which header was pressed
         * 
         * @return The column propety id
         */
        public Object getPropertyId() {
            return columnPropertyId;
        }
    }

    /**
     * Interface for the listener for column header mouse click events. The
     * headerClick method is called when the user presses a header column cell.
     */
    public interface HeaderClickListener extends Serializable {

        /**
         * Called when a user clicks a header column cell
         * 
         * @param event
         *            The event which contains information about the column and
         *            the mouse click event
         */
        public void headerClick(HeaderClickEvent event);
    }

    /**
     * Interface for the listener for column footer mouse click events. The
     * footerClick method is called when the user presses a footer column cell.
     */
    public interface FooterClickListener extends Serializable {

        /**
         * Called when a user clicks a footer column cell
         * 
         * @param event
         *            The event which contains information about the column and
         *            the mouse click event
         */
        public void footerClick(FooterClickEvent event);
    }

    /**
     * Adds a header click listener which handles the click events when the user
     * clicks on a column header cell in the Table.
     * <p>
     * The listener will receive events which contain information about which
     * column was clicked and some details about the mouse event.
     * </p>
     * 
     * @param listener
     *            The handler which should handle the header click events.
     */
    public void addListener(HeaderClickListener listener) {
        addListener(VScrollTable.HEADER_CLICK_EVENT_ID, HeaderClickEvent.class,
                listener, HeaderClickEvent.HEADER_CLICK_METHOD);
    }

    /**
     * Removes a header click listener
     * 
     * @param listener
     *            The listener to remove.
     */
    public void removeListener(HeaderClickListener listener) {
        removeListener(VScrollTable.HEADER_CLICK_EVENT_ID,
                HeaderClickEvent.class, listener);
    }

    /**
     * Adds a footer click listener which handles the click events when the user
     * clicks on a column footer cell in the Table.
     * <p>
     * The listener will receive events which contain information about which
     * column was clicked and some details about the mouse event.
     * </p>
     * 
     * @param listener
     *            The handler which should handle the footer click events.
     */
    public void addListener(FooterClickListener listener) {
        addListener(VScrollTable.FOOTER_CLICK_EVENT_ID, FooterClickEvent.class,
                listener, FooterClickEvent.FOOTER_CLICK_METHOD);
    }

    /**
     * Removes a footer click listener
     * 
     * @param listener
     *            The listener to remove.
     */
    public void removeListener(FooterClickListener listener) {
        removeListener(VScrollTable.FOOTER_CLICK_EVENT_ID,
                FooterClickEvent.class, listener);
    }

    /**
     * Gets the footer caption beneath the rows
     * 
     * @param propertyId
     *            The propertyId of the column *
     * @return The caption of the footer or NULL if not set
     */
    public String getColumnFooter(Object propertyId) {
        return columnFooters.get(propertyId);
    }

    /**
     * Sets the column footer caption. The column footer caption is the text
     * displayed beneath the column if footers have been set visible.
     * 
     * @param propertyId
     *            The properyId of the column
     * 
     * @param footer
     *            The caption of the footer
     */
    public void setColumnFooter(Object propertyId, String footer) {
        if (footer == null) {
            columnFooters.remove(propertyId);
        } else {
            columnFooters.put(propertyId, footer);
        }

        requestRepaint();
    }

    /**
     * Sets the footer visible in the bottom of the table.
     * <p>
     * The footer can be used to add column related data like sums to the bottom
     * of the Table using setColumnFooter(Object propertyId, String footer).
     * </p>
     * 
     * @param visible
     *            Should the footer be visible
     */
    public void setFooterVisible(boolean visible) {
        columnFootersVisible = visible;

        // Assures the visual refresh
        refreshRenderedCells();
    }

    /**
     * Is the footer currently visible?
     * 
     * @return Returns true if visible else false
     */
    public boolean isFooterVisible() {
        return columnFootersVisible;
    }

    /**
     * This event is fired when a column is resized. The event contains the
     * columns property id which was fired, the previous width of the column and
     * the width of the column after the resize.
     */
    public static class ColumnResizeEvent extends Component.Event {
        public static final Method COLUMN_RESIZE_METHOD;

        static {
            try {
                COLUMN_RESIZE_METHOD = ColumnResizeListener.class
                        .getDeclaredMethod("columnResize",
                                new Class[] { ColumnResizeEvent.class });
            } catch (final java.lang.NoSuchMethodException e) {
                // This should never happen
                throw new java.lang.RuntimeException();
            }
        }

        private final int previousWidth;
        private final int currentWidth;
        private final Object columnPropertyId;

        /**
         * Constructor
         * 
         * @param source
         *            The source of the event
         * @param propertyId
         *            The columns property id
         * @param previous
         *            The width in pixels of the column before the resize event
         * @param current
         *            The width in pixels of the column after the resize event
         */
        public ColumnResizeEvent(Component source, Object propertyId,
                int previous, int current) {
            super(source);
            previousWidth = previous;
            currentWidth = current;
            columnPropertyId = propertyId;
        }

        /**
         * Get the column property id of the column that was resized.
         * 
         * @return The column property id
         */
        public Object getPropertyId() {
            return columnPropertyId;
        }

        /**
         * Get the width in pixels of the column before the resize event
         * 
         * @return Width in pixels
         */
        public int getPreviousWidth() {
            return previousWidth;
        }

        /**
         * Get the width in pixels of the column after the resize event
         * 
         * @return Width in pixels
         */
        public int getCurrentWidth() {
            return currentWidth;
        }
    }

    /**
     * Interface for listening to column resize events.
     */
    public interface ColumnResizeListener extends Serializable {

        /**
         * This method is triggered when the column has been resized
         * 
         * @param event
         *            The event which contains the column property id, the
         *            previous width of the column and the current width of the
         *            column
         */
        public void columnResize(ColumnResizeEvent event);
    }

    /**
     * Adds a column resize listener to the Table. A column resize listener is
     * called when a user resizes a columns width.
     * 
     * @param listener
     *            The listener to attach to the Table
     */
    public void addListener(ColumnResizeListener listener) {
        addListener(VScrollTable.COLUMN_RESIZE_EVENT_ID,
                ColumnResizeEvent.class, listener,
                ColumnResizeEvent.COLUMN_RESIZE_METHOD);
    }

    /**
     * Removes a column resize listener from the Table.
     * 
     * @param listener
     *            The listener to remove
     */
    public void removeListener(ColumnResizeListener listener) {
        removeListener(VScrollTable.COLUMN_RESIZE_EVENT_ID,
                ColumnResizeEvent.class, listener);
    }
}
