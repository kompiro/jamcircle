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
package com.vaadin.data.validator;

import com.vaadin.data.Validator;

/**
 * Abstract {@link com.vaadin.data.Validator Validator} implementation that
 * provides a basic Validator implementation except the {@link #isValid(Object)}
 * method. Sub-classes need to implement the {@link #isValid(Object)} method.
 * <p>
 * To include the value that failed validation in the exception message you can
 * use "{0}" in the error message. This will be replaced with the failed value
 * (converted to string using {@link #toString()}) or "null" if the value is
 * null.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 5.4
 */
@SuppressWarnings("serial")
public abstract class AbstractValidator implements Validator {

    /**
     * Error message that is included in an {@link InvalidValueException} if
     * such is thrown.
     */
    private String errorMessage;

    /**
     * Constructs a validator with the given error message.
     * 
     * @param errorMessage
     *            the message to be included in an {@link InvalidValueException}
     *            (with "{0}" replaced by the value that failed validation).
     */
    public AbstractValidator(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void validate(Object value) throws InvalidValueException {
        if (!isValid(value)) {
            String message;
            if (value == null) {
                message = errorMessage.replace("{0}", "null");
            } else {
                message = errorMessage.replace("{0}", value.toString());
            }
            throw new InvalidValueException(message);
        }
    }

    /**
     * Returns the message to be included in the exception in case the value
     * does not validate.
     * 
     * @return the error message provided in the constructor or using
     *         {@link #setErrorMessage(String)}.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the message to be included in the exception in case the value does
     * not validate. The exception message is typically shown to the end user.
     * 
     * @param errorMessage
     *            the error message. "{0}" is automatically replaced by the
     *            value that did not validate.
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
