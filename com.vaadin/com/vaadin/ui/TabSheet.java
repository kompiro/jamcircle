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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.vaadin.terminal.ErrorMessage;
import com.vaadin.terminal.KeyMapper;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.gwt.client.ui.VTabsheet;
import com.vaadin.terminal.gwt.server.CommunicationManager;
import com.vaadin.ui.TabSheet.CloseHandler;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.Runo;

/**
 * TabSheet component.
 * 
 * Tabs are typically identified by the component contained on the tab (see
 * {@link ComponentContainer}), and tab metadata (including caption, icon,
 * visibility, enabledness, closability etc.) is kept in separate {@link Tab}
 * instances.
 * 
 * Tabs added with {@link #addComponent(Component)} get the caption and the icon
 * of the component at the time when the component is created, and these are not
 * automatically updated after tab creation.
 * 
 * A tab sheet can have multiple tab selection listeners and one tab close
 * handler ({@link CloseHandler}), which by default removes the tab from the
 * TabSheet.
 * 
 * The {@link TabSheet} can be styled with the .v-tabsheet, .v-tabsheet-tabs and
 * .v-tabsheet-content styles. Themes may also have pre-defined variations of
 * the tab sheet presentation, such as {@link Reindeer#TABSHEET_BORDERLESS},
 * {@link Runo#TABSHEET_SMALL} and several other styles in {@link Reindeer}.
 * 
 * The current implementation does not load the tabs to the UI before the first
 * time they are shown, but this may change in future releases.
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 3.0
 */
@SuppressWarnings("serial")
@ClientWidget(VTabsheet.class)
public class TabSheet extends AbstractComponentContainer {

    /**
     * List of component tabs (tab contents). In addition to being on this list,
     * there is a {@link Tab} object in tabs for each tab with meta-data about
     * the tab.
     */
    private final LinkedList<Component> components = new LinkedList<Component>();

    /**
     * Map containing information related to the tabs (caption, icon etc).
     */
    private final HashMap<Component, Tab> tabs = new HashMap<Component, Tab>();

    /**
     * Selected tab content component.
     */
    private Component selected = null;

    /**
     * Mapper between server-side component instances (tab contents) and keys
     * given to the client that identify tabs.
     */
    private final KeyMapper keyMapper = new KeyMapper();

    /**
     * When true, the tab selection area is not displayed to the user.
     */
    private boolean tabsHidden;

    /**
     * Tabs that have been shown to the user (have been painted as selected).
     */
    private HashSet<Component> paintedTabs = new HashSet<Component>();

    /**
     * Handler to be called when a tab is closed.
     */
    private CloseHandler closeHandler;

    /**
     * Constructs a new Tabsheet. Tabsheet is immediate by default, and the
     * default close handler removes the tab being closed.
     */
    public TabSheet() {
        super();
        // expand horizontally by default
        setWidth(100, UNITS_PERCENTAGE);
        setImmediate(true);
        setCloseHandler(new CloseHandler() {
            public void onTabClose(TabSheet tabsheet, Component c) {
                tabsheet.removeComponent(c);
            }
        });
    }

    /**
     * Gets the component container iterator for going through all the
     * components (tab contents).
     * 
     * @return the unmodifiable Iterator of the tab content components
     */
    public Iterator<Component> getComponentIterator() {
        return Collections.unmodifiableList(components).iterator();
    }

    /**
     * Removes a component and its corresponding tab.
     * 
     * If the tab was selected, the first eligible (visible and enabled)
     * remaining tab is selected.
     * 
     * @param c
     *            the component to be removed.
     */
    @Override
    public void removeComponent(Component c) {
        if (c != null && components.contains(c)) {
            super.removeComponent(c);
            keyMapper.remove(c);
            components.remove(c);
            tabs.remove(c);
            if (c.equals(selected)) {
                if (components.isEmpty()) {
                    selected = null;
                } else {
                    // select the first enabled and visible tab, if any
                    updateSelection();
                    fireSelectedTabChange();
                }
            }
            requestRepaint();
        }
    }

    /**
     * Removes a {@link Tab} and the component associated with it, as previously
     * added with {@link #addTab(Component)},
     * {@link #addTab(Component, String, Resource)} or
     * {@link #addComponent(Component)}.
     * <p>
     * If the tab was selected, the first eligible (visible and enabled)
     * remaining tab is selected.
     * </p>
     * 
     * @see #addTab(Component)
     * @see #addTab(Component, String, Resource)
     * @see #addComponent(Component)
     * @see #removeComponent(Component)
     * @param tab
     *            the Tab to remove
     */
    public void removeTab(Tab tab) {
        for (Component c : tabs.keySet()) {
            if (tabs.get(c).equals(tab)) {
                removeComponent(c);
                break;
            }
        }
    }

    /**
     * Adds a new tab into TabSheet. Component caption and icon are copied to
     * the tab metadata at creation time.
     * 
     * @see #addTab(Component)
     * 
     * @param c
     *            the component to be added.
     */
    @Override
    public void addComponent(Component c) {
        addTab(c);
    }

    /**
     * Adds a new tab into TabSheet.
     * 
     * The first tab added to a tab sheet is automatically selected and a tab
     * selection event is fired.
     * 
     * If the component is already present in the tab sheet, changes its caption
     * and icon and returns the corresponding (old) tab, preserving other tab
     * metadata.
     * 
     * @param c
     *            the component to be added onto tab - should not be null.
     * @param caption
     *            the caption to be set for the component and used rendered in
     *            tab bar
     * @param icon
     *            the icon to be set for the component and used rendered in tab
     *            bar
     * @return the created {@link Tab}
     */
    public Tab addTab(Component c, String caption, Resource icon) {
        if (c == null) {
            return null;
        } else if (tabs.containsKey(c)) {
            Tab tab = tabs.get(c);
            tab.setCaption(caption);
            tab.setIcon(icon);
            return tab;
        } else {
            components.addLast(c);
            Tab tab = new TabSheetTabImpl(caption, icon);

            tabs.put(c, tab);
            if (selected == null) {
                selected = c;
                fireSelectedTabChange();
            }
            super.addComponent(c);
            requestRepaint();
            return tab;
        }
    }

    /**
     * Adds a new tab into TabSheet. Component caption and icon are copied to
     * the tab metadata at creation time.
     * 
     * If the tab sheet already contains the component, its tab is returned.
     * 
     * @param c
     *            the component to be added onto tab - should not be null.
     * @return the created {@link Tab}
     */
    public Tab addTab(Component c) {
        if (c == null) {
            return null;
        } else if (tabs.containsKey(c)) {
            return tabs.get(c);
        } else {
            return addTab(c, c.getCaption(), c.getIcon());
        }
    }

    /**
     * Moves all components from another container to this container. The
     * components are removed from the other container.
     * 
     * If the source container is a {@link TabSheet}, component captions and
     * icons are copied from it.
     * 
     * @param source
     *            the container components are removed from.
     */
    @Override
    public void moveComponentsFrom(ComponentContainer source) {
        for (final Iterator<Component> i = source.getComponentIterator(); i
                .hasNext();) {
            final Component c = i.next();
            String caption = null;
            Resource icon = null;
            if (TabSheet.class.isAssignableFrom(source.getClass())) {
                caption = ((TabSheet) source).getTabCaption(c);
                icon = ((TabSheet) source).getTabIcon(c);
            }
            source.removeComponent(c);
            addTab(c, caption, icon);

        }
    }

    /**
     * Paints the content of this component.
     * 
     * @param target
     *            the paint target
     * @throws PaintException
     *             if the paint operation failed.
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {

        if (areTabsHidden()) {
            target.addAttribute("hidetabs", true);
        }

        target.startTag("tabs");

        Collection<Component> orphaned = new HashSet<Component>(paintedTabs);

        for (final Iterator<Component> i = getComponentIterator(); i.hasNext();) {
            final Component component = i.next();

            orphaned.remove(component);

            Tab tab = tabs.get(component);

            target.startTag("tab");
            if (!tab.isEnabled() && tab.isVisible()) {
                target.addAttribute("disabled", true);
            }

            if (!tab.isVisible()) {
                target.addAttribute("hidden", true);
            }

            if (tab.isClosable()) {
                target.addAttribute("closable", true);
            }

            final Resource icon = tab.getIcon();
            if (icon != null) {
                target.addAttribute("icon", icon);
            }
            final String caption = tab.getCaption();
            if (caption != null && caption.length() > 0) {
                target.addAttribute("caption", caption);
            }

            final String description = tab.getDescription();
            if (description != null) {
                target.addAttribute("description", description);
            }

            final ErrorMessage componentError = tab.getComponentError();
            if (componentError != null) {
                componentError.paint(target);
            }

            target.addAttribute("key", keyMapper.key(component));
            if (component.equals(selected)) {
                target.addAttribute("selected", true);
                component.paint(target);
                paintedTabs.add(component);
            } else if (paintedTabs.contains(component)) {
                component.paint(target);
            } else {
                component.requestRepaintRequests();
            }
            target.endTag("tab");
        }

        target.endTag("tabs");

        if (selected != null) {
            target.addVariable(this, "selected", keyMapper.key(selected));
        }

        // clean possibly orphaned entries in paintedTabs
        for (Component component2 : orphaned) {
            paintedTabs.remove(component2);
        }
    }

    /**
     * Are the tab selection parts ("tabs") hidden.
     * 
     * @return true if the tabs are hidden in the UI
     */
    public boolean areTabsHidden() {
        return tabsHidden;
    }

    /**
     * Hides or shows the tab selection parts ("tabs").
     * 
     * @param tabsHidden
     *            true if the tabs should be hidden
     */
    public void hideTabs(boolean tabsHidden) {
        this.tabsHidden = tabsHidden;
        requestRepaint();
    }

    /**
     * Gets tab caption. The tab is identified by the tab content component.
     * 
     * @param c
     *            the component in the tab
     * @deprecated Use {@link #getTab(Component)} and {@link Tab#getCaption()}
     *             instead.
     */
    @Deprecated
    public String getTabCaption(Component c) {
        Tab info = tabs.get(c);
        if (info == null) {
            return "";
        } else {
            return info.getCaption();
        }
    }

    /**
     * Sets tab caption. The tab is identified by the tab content component.
     * 
     * @param c
     *            the component in the tab
     * @param caption
     *            the caption to set.
     * @deprecated Use {@link #getTab(Component)} and
     *             {@link Tab#setCaption(String)} instead.
     */
    @Deprecated
    public void setTabCaption(Component c, String caption) {
        Tab info = tabs.get(c);
        if (info != null) {
            info.setCaption(caption);
            requestRepaint();
        }
    }

    /**
     * Gets the icon for a tab. The tab is identified by the tab content
     * component.
     * 
     * @param c
     *            the component in the tab
     * @deprecated Use {@link #getTab(Component)} and {@link Tab#getIcon()}
     *             instead.
     */
    @Deprecated
    public Resource getTabIcon(Component c) {
        Tab info = tabs.get(c);
        if (info == null) {
            return null;
        } else {
            return info.getIcon();
        }
    }

    /**
     * Sets icon for the given component. The tab is identified by the tab
     * content component.
     * 
     * @param c
     *            the component in the tab
     * @param icon
     *            the icon to set
     * @deprecated Use {@link #getTab(Component)} and
     *             {@link Tab#setIcon(Resource)} instead.
     */
    @Deprecated
    public void setTabIcon(Component c, Resource icon) {
        Tab info = tabs.get(c);
        if (info != null) {
            info.setIcon(icon);
            requestRepaint();
        }
    }

    /**
     * Returns the {@link Tab} (metadata) for a component. The {@link Tab}
     * object can be used for setting caption,icon, etc for the tab.
     * 
     * @param c
     *            the component
     * @return
     */
    public Tab getTab(Component c) {
        return tabs.get(c);
    }

    /**
     * Sets the selected tab. The tab is identified by the tab content
     * component.
     * 
     * @param c
     */
    public void setSelectedTab(Component c) {
        if (c != null && components.contains(c) && !c.equals(selected)) {
            selected = c;
            updateSelection();
            fireSelectedTabChange();
            requestRepaint();
        }
    }

    /**
     * Checks if the current selection is valid, and updates the selection if
     * the previously selected component is not visible and enabled. The first
     * visible and enabled tab is selected if the current selection is empty or
     * invalid.
     * 
     * This method does not fire tab change events, but the caller should do so
     * if appropriate.
     * 
     * @return true if selection was changed, false otherwise
     */
    private boolean updateSelection() {
        Component originalSelection = selected;
        for (final Iterator<Component> i = getComponentIterator(); i.hasNext();) {
            final Component component = i.next();

            Tab tab = tabs.get(component);

            /*
             * If we have no selection, if the current selection is invisible or
             * if the current selection is disabled (but the whole component is
             * not) we select this tab instead
             */
            Tab selectedTabInfo = null;
            if (selected != null) {
                selectedTabInfo = tabs.get(selected);
            }
            if (selected == null || selectedTabInfo == null
                    || !selectedTabInfo.isVisible()
                    || !selectedTabInfo.isEnabled()) {

                // The current selection is not valid so we need to change
                // it
                if (tab.isEnabled() && tab.isVisible()) {
                    selected = component;
                    break;
                } else {
                    /*
                     * The current selection is not valid but this tab cannot be
                     * selected either.
                     */
                    selected = null;
                }
            }
        }
        return originalSelection != selected;
    }

    /**
     * Gets the selected tab content component.
     * 
     * @return the selected tab contents
     */
    public Component getSelectedTab() {
        return selected;
    }

    // inherits javadoc
    @Override
    public void changeVariables(Object source, Map variables) {
        if (variables.containsKey("selected")) {
            setSelectedTab((Component) keyMapper.get((String) variables
                    .get("selected")));
        }
        if (variables.containsKey("close")) {
            final Component tab = (Component) keyMapper.get((String) variables
                    .get("close"));
            if (tab != null) {
                closeHandler.onTabClose(this, tab);
            }
        }
    }

    /**
     * Replaces a component (tab content) with another. This can be used to
     * change tab contents or to rearrange tabs. The tab position and some
     * metadata are preserved when moving components within the same
     * {@link TabSheet}.
     * 
     * If the oldComponent is not present in the tab sheet, the new one is added
     * at the end.
     * 
     * If the oldComponent is already in the tab sheet but the newComponent
     * isn't, the old tab is replaced with a new one, and the caption and icon
     * of the old one are copied to the new tab.
     * 
     * If both old and new components are present, their positions are swapped.
     * 
     * {@inheritDoc}
     */
    public void replaceComponent(Component oldComponent, Component newComponent) {

        if (selected == oldComponent) {
            // keep selection w/o selectedTabChange event
            selected = newComponent;
        }

        Tab newTab = tabs.get(newComponent);
        Tab oldTab = tabs.get(oldComponent);

        // Gets the captions
        String oldCaption = null;
        Resource oldIcon = null;
        String newCaption = null;
        Resource newIcon = null;

        if (oldTab != null) {
            oldCaption = oldTab.getCaption();
            oldIcon = oldTab.getIcon();
        }

        if (newTab != null) {
            newCaption = newTab.getCaption();
            newIcon = newTab.getIcon();
        } else {
            newCaption = newComponent.getCaption();
            newIcon = newComponent.getIcon();
        }

        // Gets the locations
        int oldLocation = -1;
        int newLocation = -1;
        int location = 0;
        for (final Iterator<Component> i = components.iterator(); i.hasNext();) {
            final Component component = i.next();

            if (component == oldComponent) {
                oldLocation = location;
            }
            if (component == newComponent) {
                newLocation = location;
            }

            location++;
        }

        if (oldLocation == -1) {
            addComponent(newComponent);
        } else if (newLocation == -1) {
            removeComponent(oldComponent);
            keyMapper.remove(oldComponent);
            newTab = addTab(newComponent);
            components.remove(newComponent);
            components.add(oldLocation, newComponent);
            newTab.setCaption(oldCaption);
            newTab.setIcon(oldIcon);
        } else {
            if (oldLocation > newLocation) {
                components.remove(oldComponent);
                components.add(newLocation, oldComponent);
                components.remove(newComponent);
                components.add(oldLocation, newComponent);
            } else {
                components.remove(newComponent);
                components.add(oldLocation, newComponent);
                components.remove(oldComponent);
                components.add(newLocation, oldComponent);
            }

            if (newTab != null) {
                // This should always be true
                newTab.setCaption(oldCaption);
                newTab.setIcon(oldIcon);
            }
            if (oldTab != null) {
                // This should always be true
                oldTab.setCaption(newCaption);
                oldTab.setIcon(newIcon);
            }

            requestRepaint();
        }

    }

    /* Click event */

    private static final Method SELECTED_TAB_CHANGE_METHOD;
    static {
        try {
            SELECTED_TAB_CHANGE_METHOD = SelectedTabChangeListener.class
                    .getDeclaredMethod("selectedTabChange",
                            new Class[] { SelectedTabChangeEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in TabSheet");
        }
    }

    /**
     * Selected tab change event. This event is sent when the selected (shown)
     * tab in the tab sheet is changed.
     * 
     * @author IT Mill Ltd.
     * @version
     * 6.4.8
     * @since 3.0
     */
    public class SelectedTabChangeEvent extends Component.Event {

        /**
         * New instance of selected tab change event
         * 
         * @param source
         *            the Source of the event.
         */
        public SelectedTabChangeEvent(Component source) {
            super(source);
        }

        /**
         * TabSheet where the event occurred.
         * 
         * @return the Source of the event.
         */
        public TabSheet getTabSheet() {
            return (TabSheet) getSource();
        }
    }

    /**
     * Selected tab change event listener. The listener is called whenever
     * another tab is selected, including when adding the first tab to a
     * tabsheet.
     * 
     * @author IT Mill Ltd.
     * 
     * @version
     * 6.4.8
     * @since 3.0
     */
    public interface SelectedTabChangeListener extends Serializable {

        /**
         * Selected (shown) tab in tab sheet has has been changed.
         * 
         * @param event
         *            the selected tab change event.
         */
        public void selectedTabChange(SelectedTabChangeEvent event);
    }

    /**
     * Adds a tab selection listener
     * 
     * @param listener
     *            the Listener to be added.
     */
    public void addListener(SelectedTabChangeListener listener) {
        addListener(SelectedTabChangeEvent.class, listener,
                SELECTED_TAB_CHANGE_METHOD);
    }

    /**
     * Removes a tab selection listener
     * 
     * @param listener
     *            the Listener to be removed.
     */
    public void removeListener(SelectedTabChangeListener listener) {
        removeListener(SelectedTabChangeEvent.class, listener,
                SELECTED_TAB_CHANGE_METHOD);
    }

    /**
     * Sends an event that the currently selected tab has changed.
     */
    protected void fireSelectedTabChange() {
        fireEvent(new SelectedTabChangeEvent(this));
    }

    @Override
    public void removeListener(RepaintRequestListener listener) {
        super.removeListener(listener);
        if (listener instanceof CommunicationManager) {
            // clean the paintedTabs here instead of detach to avoid subtree
            // caching issues when detached-attached without render
            paintedTabs.clear();
        }
    }

    /**
     * Tab meta-data for a component in a {@link TabSheet}.
     * 
     * The meta-data includes the tab caption, icon, visibility and enabledness,
     * closability, description (tooltip) and an optional component error shown
     * in the tab.
     * 
     * Tabs are identified by the component contained on them in most cases, and
     * the meta-data can be obtained with {@link TabSheet#getTab(Component)}.
     */
    public interface Tab extends Serializable {
        /**
         * Returns the visible status for the tab. An invisible tab is not shown
         * in the tab bar and cannot be selected.
         * 
         * @return true for visible, false for hidden
         */
        public boolean isVisible();

        /**
         * Sets the visible status for the tab. An invisible tab is not shown in
         * the tab bar and cannot be selected, selection is changed
         * automatically when there is an attempt to select an invisible tab.
         * 
         * @param visible
         *            true for visible, false for hidden
         */
        public void setVisible(boolean visible);

        /**
         * Returns the closability status for the tab.
         * 
         * @return true if the tab is allowed to be closed by the end user,
         *         false for not allowing closing
         */
        public boolean isClosable();

        /**
         * Sets the closability status for the tab. A closable tab can be closed
         * by the user through the user interface. This also controls if a close
         * button is shown to the user or not.
         * <p>
         * Note! Currently only supported by TabSheet, not Accordion.
         * </p>
         * 
         * @param visible
         *            true if the end user is allowed to close the tab, false
         *            for not allowing to close. Should default to false.
         */
        public void setClosable(boolean closable);

        /**
         * Returns the enabled status for the tab. A disabled tab is shown as
         * such in the tab bar and cannot be selected.
         * 
         * @return true for enabled, false for disabled
         */
        public boolean isEnabled();

        /**
         * Sets the enabled status for the tab. A disabled tab is shown as such
         * in the tab bar and cannot be selected.
         * 
         * @param enabled
         *            true for enabled, false for disabled
         */
        public void setEnabled(boolean enabled);

        /**
         * Sets the caption for the tab.
         * 
         * @param caption
         *            the caption to set
         */
        public void setCaption(String caption);

        /**
         * Gets the caption for the tab.
         */
        public String getCaption();

        /**
         * Gets the icon for the tab.
         */
        public Resource getIcon();

        /**
         * Sets the icon for the tab.
         * 
         * @param icon
         *            the icon to set
         */
        public void setIcon(Resource icon);

        /**
         * Gets the description for the tab. The description can be used to
         * briefly describe the state of the tab to the user, and is typically
         * shown as a tooltip when hovering over the tab.
         * 
         * @return the description for the tab
         */
        public String getDescription();

        /**
         * Sets the description for the tab. The description can be used to
         * briefly describe the state of the tab to the user, and is typically
         * shown as a tooltip when hovering over the tab.
         * 
         * @param description
         *            the new description string for the tab.
         */
        public void setDescription(String description);

        /**
         * Sets an error indicator to be shown in the tab. This can be used e.g.
         * to communicate to the user that there is a problem in the contents of
         * the tab.
         * 
         * @see AbstractComponent#setComponentError(ErrorMessage)
         * 
         * @param componentError
         *            error message or null for none
         */
        public void setComponentError(ErrorMessage componentError);

        /**
         * Gets the curent error message shown for the tab.
         * 
         * @see AbstractComponent#setComponentError(ErrorMessage)
         * 
         * @param error
         *            message or null if none
         */
        public ErrorMessage getComponentError();

    }

    /**
     * TabSheet's implementation of {@link Tab} - tab metadata.
     */
    public class TabSheetTabImpl implements Tab {

        private String caption = "";
        private Resource icon = null;
        private boolean enabled = true;
        private boolean visible = true;
        private boolean closable = false;
        private String description = null;
        private ErrorMessage componentError = null;

        public TabSheetTabImpl(String caption, Resource icon) {
            if (caption == null) {
                caption = "";
            }
            this.caption = caption;
            this.icon = icon;
        }

        /**
         * Returns the tab caption. Can never be null.
         */
        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
            requestRepaint();
        }

        public Resource getIcon() {
            return icon;
        }

        public void setIcon(Resource icon) {
            this.icon = icon;
            requestRepaint();
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
            if (updateSelection()) {
                fireSelectedTabChange();
            }
            requestRepaint();
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
            if (updateSelection()) {
                fireSelectedTabChange();
            }
            requestRepaint();
        }

        public boolean isClosable() {
            return closable;
        }

        public void setClosable(boolean closable) {
            this.closable = closable;
            requestRepaint();
        }

        public void close() {

        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
            requestRepaint();
        }

        public ErrorMessage getComponentError() {
            return componentError;
        }

        public void setComponentError(ErrorMessage componentError) {
            this.componentError = componentError;
            requestRepaint();
        }
    }

    /**
     * CloseHandler is used to process tab closing events. Default behavior is
     * to remove the tab from the TabSheet.
     * 
     * @author Jouni Koivuviita / IT Mill Ltd.
     * @since 6.2.0
     * 
     */
    public interface CloseHandler extends Serializable {

        /**
         * Called when a user has pressed the close icon of a tab in the client
         * side widget.
         * 
         * @param tabsheet
         *            the TabSheet to which the tab belongs to
         * @param tabContent
         *            the component that corresponds to the tab whose close
         *            button was clicked
         */
        void onTabClose(final TabSheet tabsheet, final Component tabContent);
    }

    /**
     * Provide a custom {@link CloseHandler} for this TabSheet if you wish to
     * perform some additional tasks when a user clicks on a tabs close button,
     * e.g. show a confirmation dialogue before removing the tab.
     * 
     * To remove the tab, if you provide your own close handler, you must call
     * {@link #removeComponent(Component)} yourself.
     * 
     * The default CloseHandler for TabSheet will only remove the tab.
     * 
     * @param handler
     */
    public void setCloseHandler(CloseHandler handler) {
        closeHandler = handler;
    }
}
