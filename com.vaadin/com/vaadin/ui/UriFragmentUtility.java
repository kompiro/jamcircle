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
import java.util.Map;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.VUriFragmentUtility;
import com.vaadin.ui.ClientWidget.LoadStyle;

/**
 * Experimental web browser dependent component for URI fragment (part after
 * hash mark "#") reading and writing.
 * 
 * Component can be used to workaround common ajax web applications pitfalls:
 * bookmarking a program state and back button.
 * 
 */
@SuppressWarnings("serial")
@ClientWidget(value = VUriFragmentUtility.class, loadStyle = LoadStyle.EAGER)
public class UriFragmentUtility extends AbstractComponent {

    /**
     * Listener that listens changes in URI fragment.
     */
    public interface FragmentChangedListener extends Serializable {

        public void fragmentChanged(FragmentChangedEvent source);

    }

    /**
     * Event fired when uri fragment changes.
     */
    public class FragmentChangedEvent extends Component.Event {

        /**
         * Creates a new instance of UriFragmentReader change event.
         * 
         * @param source
         *            the Source of the event.
         */
        public FragmentChangedEvent(Component source) {
            super(source);
        }

        /**
         * Gets the UriFragmentReader where the event occurred.
         * 
         * @return the Source of the event.
         */
        public UriFragmentUtility getUriFragmentUtility() {
            return (UriFragmentUtility) getSource();
        }
    }

    private static final Method FRAGMENT_CHANGED_METHOD;

    static {
        try {
            FRAGMENT_CHANGED_METHOD = FragmentChangedListener.class
                    .getDeclaredMethod("fragmentChanged",
                            new Class[] { FragmentChangedEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in FragmentChangedListener");
        }
    }

    public void addListener(FragmentChangedListener listener) {
        addListener(FragmentChangedEvent.class, listener,
                FRAGMENT_CHANGED_METHOD);
    }

    public void removeListener(FragmentChangedListener listener) {
        removeListener(FragmentChangedEvent.class, listener,
                FRAGMENT_CHANGED_METHOD);
    }

    private String fragment;

    public UriFragmentUtility() {
        // immediate by default
        setImmediate(true);
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        target.addVariable(this, "fragment", fragment);
    }

    @Override
    public void changeVariables(Object source, Map variables) {
        super.changeVariables(source, variables);
        fragment = (String) variables.get("fragment");
        fireEvent(new FragmentChangedEvent(this));
    }

    /**
     * Gets currently set URI fragment.
     * <p>
     * To listen changes in fragment, hook a {@link FragmentChangedListener}.
     * <p>
     * Note that initial URI fragment that user used to enter the application
     * will be read after application init. It fires FragmentChangedEvent only
     * if it is not the same as on server side.
     * 
     * @return the current fragment in browser uri or null if not known
     */
    public String getFragment() {
        return fragment;
    }

    /**
     * Sets URI fragment. Optionally fires a {@link FragmentChangedEvent}
     * 
     * @param newFragment
     *            id of the new fragment
     * @param fireEvent
     *            true to fire event
     * @see FragmentChangedEvent
     * @see FragmentChangedListener
     */
    public void setFragment(String newFragment, boolean fireEvent) {
        if ((newFragment == null && fragment != null)
                || (newFragment != null && !newFragment.equals(fragment))) {
            fragment = newFragment;
            if (fireEvent) {
                fireEvent(new FragmentChangedEvent(this));
            }
            requestRepaint();
        }
    }

    /**
     * Sets URI fragment. This method fires a {@link FragmentChangedEvent}
     * 
     * @param newFragment
     *            id of the new fragment
     * @see FragmentChangedEvent
     * @see FragmentChangedListener
     */
    public void setFragment(String newFragment) {
        setFragment(newFragment, true);
    }

}
