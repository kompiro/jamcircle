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

import java.util.Date;

import com.vaadin.data.Property;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

/**
 * <p>
 * A date entry component, which displays the actual date selector as a popup.
 * 
 * </p>
 * 
 * @see DateField
 * @see InlineDateField
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 5.0
 */
@SuppressWarnings("serial")
public class PopupDateField extends DateField {

    private String inputPrompt = null;

    public PopupDateField() {
        super();
        type = TYPE_POPUP;
    }

    public PopupDateField(Property dataSource) throws IllegalArgumentException {
        super(dataSource);
        type = TYPE_POPUP;
    }

    public PopupDateField(String caption, Date value) {
        super(caption, value);
        type = TYPE_POPUP;
    }

    public PopupDateField(String caption, Property dataSource) {
        super(caption, dataSource);
        type = TYPE_POPUP;
    }

    public PopupDateField(String caption) {
        super(caption);
        type = TYPE_POPUP;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        if (inputPrompt != null) {
            target.addAttribute("prompt", inputPrompt);
        }
    }

    /**
     * Gets the current input prompt.
     * 
     * @see #setInputPrompt(String)
     * @return the current input prompt, or null if not enabled
     */
    public String getInputPrompt() {
        return inputPrompt;
    }

    /**
     * Sets the input prompt - a textual prompt that is displayed when the field
     * would otherwise be empty, to prompt the user for input.
     * 
     * @param inputPrompt
     */
    public void setInputPrompt(String inputPrompt) {
        this.inputPrompt = inputPrompt;
        requestRepaint();
    }

}
