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

/**
 * This <code>StringLengthValidator</code> is used to validate the length of
 * strings.
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 3.0
 */
@SuppressWarnings("serial")
public class StringLengthValidator extends AbstractValidator {

    private int minLength = -1;

    private int maxLength = -1;

    private boolean allowNull = true;

    /**
     * Creates a new StringLengthValidator with a given error message.
     * 
     * @param errorMessage
     *            the message to display in case the value does not validate.
     */
    public StringLengthValidator(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Creates a new StringLengthValidator with a given error message,
     * permissable lengths and null-string allowance.
     * 
     * @param errorMessage
     *            the message to display in case the value does not validate.
     * @param minLength
     *            the minimum permissible length of the string.
     * @param maxLength
     *            the maximum permissible length of the string.
     * @param allowNull
     *            Are null strings permissible? This can be handled better by
     *            setting a field as required or not.
     */
    public StringLengthValidator(String errorMessage, int minLength,
            int maxLength, boolean allowNull) {
        this(errorMessage);
        setMinLength(minLength);
        setMaxLength(maxLength);
        setNullAllowed(allowNull);
    }

    /**
     * Checks if the given value is valid.
     * 
     * @param value
     *            the value to validate.
     * @return <code>true</code> for valid value, otherwise <code>false</code>.
     */
    public boolean isValid(Object value) {
        if (value == null) {
            return allowNull;
        }
        final String s = value.toString();
        if (s == null) {
            return allowNull;
        }
        final int len = s.length();
        if ((minLength >= 0 && len < minLength)
                || (maxLength >= 0 && len > maxLength)) {
            return false;
        }
        return true;
    }

    /**
     * Returns <code>true</code> if null strings are allowed.
     * 
     * @return <code>true</code> if allows null string, otherwise
     *         <code>false</code>.
     */
    @Deprecated
    public final boolean isNullAllowed() {
        return allowNull;
    }

    /**
     * Gets the maximum permissible length of the string.
     * 
     * @return the maximum length of the string.
     */
    public final int getMaxLength() {
        return maxLength;
    }

    /**
     * Gets the minimum permissible length of the string.
     * 
     * @return the minimum length of the string.
     */
    public final int getMinLength() {
        return minLength;
    }

    /**
     * Sets whether null-strings are to be allowed. This can be better handled
     * by setting a field as required or not.
     */
    @Deprecated
    public void setNullAllowed(boolean allowNull) {
        this.allowNull = allowNull;
    }

    /**
     * Sets the maximum permissible length of the string.
     * 
     * @param maxLength
     *            the length to set.
     */
    public void setMaxLength(int maxLength) {
        if (maxLength < -1) {
            maxLength = -1;
        }
        this.maxLength = maxLength;
    }

    /**
     * Sets the minimum permissible length.
     * 
     * @param minLength
     *            the length to set.
     */
    public void setMinLength(int minLength) {
        if (minLength < -1) {
            minLength = -1;
        }
        this.minLength = minLength;
    }

}
