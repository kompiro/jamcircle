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

package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.UIDL;

public class VOptionGroup extends VOptionGroupBase implements FocusHandler,
        BlurHandler {

    public static final String CLASSNAME = "v-select-optiongroup";

    private final Panel panel;

    private final Map optionsToKeys;

    private boolean sendFocusEvents = false;
    private boolean sendBlurEvents = false;
    private List<HandlerRegistration> focusHandlers = null;
    private List<HandlerRegistration> blurHandlers = null;

    /**
     * used to check whether a blur really was a blur of the complete
     * optiongroup: if a control inside this optiongroup gains focus right after
     * blur of another control inside this optiongroup (meaning: if onFocus
     * fires after onBlur has fired), the blur and focus won't be sent to the
     * server side as only a focus change inside this optiongroup occured
     */
    private boolean blurOccured = false;

    public VOptionGroup() {
        super(CLASSNAME);
        panel = (Panel) optionsContainer;
        optionsToKeys = new HashMap();
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);

        sendFocusEvents = client.hasEventListeners(this, EventId.FOCUS);
        sendBlurEvents = client.hasEventListeners(this, EventId.BLUR);

        if (focusHandlers != null) {
            for (HandlerRegistration reg : focusHandlers) {
                reg.removeHandler();
            }
            focusHandlers.clear();
            focusHandlers = null;

            for (HandlerRegistration reg : blurHandlers) {
                reg.removeHandler();
            }
            blurHandlers.clear();
            blurHandlers = null;
        }

        if (sendFocusEvents || sendBlurEvents) {
            focusHandlers = new ArrayList<HandlerRegistration>();
            blurHandlers = new ArrayList<HandlerRegistration>();

            // add focus and blur handlers to checkboxes / radio buttons
            for (Widget wid : panel) {
                if (wid instanceof CheckBox) {
                    focusHandlers.add(((CheckBox) wid).addFocusHandler(this));
                    blurHandlers.add(((CheckBox) wid).addBlurHandler(this));
                }
            }
        }
    }

    /*
     * Return true if no elements were changed, false otherwise.
     */
    @Override
    protected void buildOptions(UIDL uidl) {
        panel.clear();
        for (final Iterator it = uidl.getChildIterator(); it.hasNext();) {
            final UIDL opUidl = (UIDL) it.next();
            CheckBox op;
            if (isMultiselect()) {
                op = new VCheckBox();
                op.setText(opUidl.getStringAttribute("caption"));
            } else {
                op = new RadioButton(id, opUidl.getStringAttribute("caption"));
                op.setStyleName("v-radiobutton");
            }
            op.addStyleName(CLASSNAME_OPTION);
            op.setValue(opUidl.getBooleanAttribute("selected"));
            boolean enabled = !opUidl.getBooleanAttribute("disabled")
                    && !isReadonly() && !isDisabled();
            op.setEnabled(enabled);
            setStyleName(op.getElement(), "v-disabled", !enabled);
            op.addClickHandler(this);
            optionsToKeys.put(op, opUidl.getStringAttribute("key"));
            panel.add(op);
        }
    }

    @Override
    protected String[] getSelectedItems() {
        return selectedKeys.toArray(new String[selectedKeys.size()]);
    }

    @Override
    public void onClick(ClickEvent event) {
        super.onClick(event);
        if (event.getSource() instanceof CheckBox) {
            final boolean selected = ((CheckBox) event.getSource()).getValue();
            final String key = (String) optionsToKeys.get(event.getSource());
            if (!isMultiselect()) {
                selectedKeys.clear();
            }
            if (selected) {
                selectedKeys.add(key);
            } else {
                selectedKeys.remove(key);
            }
            client.updateVariable(id, "selected", getSelectedItems(),
                    isImmediate());
        }
    }

    @Override
    protected void setTabIndex(int tabIndex) {
        for (Iterator iterator = panel.iterator(); iterator.hasNext();) {
            FocusWidget widget = (FocusWidget) iterator.next();
            widget.setTabIndex(tabIndex);
        }
    }

    public void focus() {
        Iterator<Widget> iterator = panel.iterator();
        if (iterator.hasNext()) {
            ((Focusable) iterator.next()).setFocus(true);
        }
    }

    public void onFocus(FocusEvent arg0) {
        if (!blurOccured) {
            // no blur occured before this focus event
            // panel was blurred => fire the event to the server side if
            // requested by server side
            if (sendFocusEvents) {
                client.updateVariable(id, EventId.FOCUS, "", true);
            }
        } else {
            // blur occured before this focus event
            // another control inside the panel (checkbox / radio box) was
            // blurred => do not fire the focus and set blurOccured to false, so
            // blur will not be fired, too
            blurOccured = false;
        }
    }

    public void onBlur(BlurEvent arg0) {
        blurOccured = true;
        if (sendBlurEvents) {
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    // check whether blurOccured still is true and then send the
                    // event out to the server
                    if (blurOccured) {
                        client.updateVariable(id, EventId.BLUR, "", true);
                        blurOccured = false;
                    }
                }
            });
        }
    }
}
