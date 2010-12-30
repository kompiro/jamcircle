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
 * Interface for validatable objects. Defines methods to verify if the object's
 * value is valid or not, and to add, remove and list registered validators of
 * the object.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 3.0
 * @see com.vaadin.data.Validator
 */
public interface Validatable extends Serializable {

    /**
     * <p>
     * Adds a new validator for this object. The validator's
     * {@link Validator#validate(Object)} method is activated every time the
     * object's value needs to be verified, that is, when the {@link #isValid()}
     * method is called. This usually happens when the object's value changes.
     * </p>
     * 
     * @param validator
     *            the new validator
     */
    void addValidator(Validator validator);

    /**
     * <p>
     * Removes a previously registered validator from the object. The specified
     * validator is removed from the object and its <code>validate</code> method
     * is no longer called in {@link #isValid()}.
     * </p>
     * 
     * @param validator
     *            the validator to remove
     */
    void removeValidator(Validator validator);

    /**
     * <p>
     * Lists all validators currently registered for the object. If no
     * validators are registered, returns <code>null</code>.
     * </p>
     * 
     * @return collection of validators or <code>null</code>
     */
    public Collection<Validator> getValidators();

    /**
     * <p>
     * Tests the current value of the object against all registered validators.
     * The registered validators are iterated and for each the
     * {@link Validator#validate(Object)} method is called. If any validator
     * throws the {@link Validator.InvalidValueException} this method returns
     * <code>false</code>.
     * </p>
     * 
     * @return <code>true</code> if the registered validators concur that the
     *         value is valid, <code>false</code> otherwise
     */
    public boolean isValid();

    /**
     * <p>
     * Checks the validity of the validatable. If the validatable is valid this
     * method should do nothing, and if it's not valid, it should throw
     * <code>Validator.InvalidValueException</code>
     * </p>
     * 
     * @throws Validator.InvalidValueException
     *             if the value is not valid
     */
    public void validate() throws Validator.InvalidValueException;

    /**
     * <p>
     * Checks the validabtable object accept invalid values.The default value is
     * <code>true</code>.
     * </p>
     * 
     */
    public boolean isInvalidAllowed();

    /**
     * <p>
     * Should the validabtable object accept invalid values. Supporting this
     * configuration possibility is optional. By default invalid values are
     * allowed.
     * </p>
     * 
     * @param invalidValueAllowed
     * 
     * @throws UnsupportedOperationException
     *             if the setInvalidAllowed is not supported.
     */
    public void setInvalidAllowed(boolean invalidValueAllowed)
            throws UnsupportedOperationException;

}
