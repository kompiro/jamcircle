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
package com.vaadin.terminal.gwt.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;

/**
 * Helper class to store and transfer mouse event details.
 */
public class MouseEventDetails {
    public static final int BUTTON_LEFT = Event.BUTTON_LEFT;
    public static final int BUTTON_MIDDLE = Event.BUTTON_MIDDLE;
    public static final int BUTTON_RIGHT = Event.BUTTON_RIGHT;

    private static final char DELIM = ',';

    private int button;
    private int clientX;
    private int clientY;
    private boolean altKey;
    private boolean ctrlKey;
    private boolean metaKey;
    private boolean shiftKey;
    private int type;
    private int relativeX = -1;
    private int relativeY = -1;

    public int getButton() {
        return button;
    }

    public int getClientX() {
        return clientX;
    }

    public int getClientY() {
        return clientY;
    }

    public boolean isAltKey() {
        return altKey;
    }

    public boolean isCtrlKey() {
        return ctrlKey;
    }

    public boolean isMetaKey() {
        return metaKey;
    }

    public boolean isShiftKey() {
        return shiftKey;
    }

    public int getRelativeX() {
        return relativeX;
    }

    public int getRelativeY() {
        return relativeY;
    }

    public MouseEventDetails(NativeEvent evt) {
        this(evt, null);
    }

    public MouseEventDetails(NativeEvent evt, Element relativeToElement) {
        button = evt.getButton();
        clientX = evt.getClientX();
        clientY = evt.getClientY();
        altKey = evt.getAltKey();
        ctrlKey = evt.getCtrlKey();
        metaKey = evt.getMetaKey();
        shiftKey = evt.getShiftKey();
        type = Event.getTypeInt(evt.getType());
        if (relativeToElement != null) {
            relativeX = getRelativeX(evt, relativeToElement);
            relativeY = getRelativeY(evt, relativeToElement);
        }
    }

    private MouseEventDetails() {
    }

    @Override
    public String toString() {
        return serialize();
    }

    public String serialize() {
        return "" + button + DELIM + clientX + DELIM + clientY + DELIM + altKey
                + DELIM + ctrlKey + DELIM + metaKey + DELIM + shiftKey + DELIM
                + type + DELIM + relativeX + DELIM + relativeY;
    }

    public static MouseEventDetails deSerialize(String serializedString) {
        MouseEventDetails instance = new MouseEventDetails();
        String[] fields = serializedString.split(",");

        instance.button = Integer.parseInt(fields[0]);
        instance.clientX = Integer.parseInt(fields[1]);
        instance.clientY = Integer.parseInt(fields[2]);
        instance.altKey = Boolean.valueOf(fields[3]).booleanValue();
        instance.ctrlKey = Boolean.valueOf(fields[4]).booleanValue();
        instance.metaKey = Boolean.valueOf(fields[5]).booleanValue();
        instance.shiftKey = Boolean.valueOf(fields[6]).booleanValue();
        instance.type = Integer.parseInt(fields[7]);
        instance.relativeX = Integer.parseInt(fields[8]);
        instance.relativeY = Integer.parseInt(fields[9]);
        return instance;
    }

    public String getButtonName() {
        if (button == BUTTON_LEFT) {
            return "left";
        } else if (button == BUTTON_RIGHT) {
            return "right";
        } else if (button == BUTTON_MIDDLE) {
            return "middle";
        }

        return "";
    }

    public Class<MouseEventDetails> getType() {
        return MouseEventDetails.class;
    }

    public boolean isDoubleClick() {
        return type == Event.ONDBLCLICK;
    }

    private static int getRelativeX(NativeEvent evt, Element target) {
        return evt.getClientX() - target.getAbsoluteLeft()
                + target.getScrollLeft()
                + target.getOwnerDocument().getScrollLeft();
    }

    private static int getRelativeY(NativeEvent evt, Element target) {
        return evt.getClientY() - target.getAbsoluteTop()
                + target.getScrollTop()
                + target.getOwnerDocument().getScrollTop();
    }

}
