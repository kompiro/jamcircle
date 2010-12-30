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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.data.Validator;

/**
 * The <code>CompositeValidator</code> allows you to chain (compose) many
 * validators to validate one field. The contained validators may be required to
 * all validate the value to validate or it may be enough that one contained
 * validator validates the value. This behaviour is controlled by the modes
 * <code>AND</code> and <code>OR</code>.
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 3.0
 */
@SuppressWarnings("serial")
public class CompositeValidator extends AbstractValidator {

    /**
     * The validators are combined with <code>AND</code> clause: validity of the
     * composite implies validity of the all validators it is composed of must
     * be valid.
     */
    public static final int MODE_AND = 0;

    /**
     * The validators are combined with <code>OR</code> clause: validity of the
     * composite implies that some of validators it is composed of must be
     * valid.
     */
    public static final int MODE_OR = 1;

    /**
     * The validators are combined with and clause: validity of the composite
     * implies validity of the all validators it is composed of
     */
    public static final int MODE_DEFAULT = MODE_AND;

    /**
     * Operation mode.
     */
    private int mode = MODE_DEFAULT;

    /**
     * List of contained validators.
     */
    private final List<Validator> validators = new LinkedList<Validator>();

    /**
     * Construct a composite validator in <code>AND</code> mode without error
     * message.
     */
    public CompositeValidator() {
        super("");
    }

    /**
     * Constructs a composite validator in given mode.
     */
    public CompositeValidator(int mode, String errorMessage) {
        super(errorMessage);
        setMode(mode);
    }

    /**
     * Validates the given value.
     * <p>
     * The value is valid, if:
     * <ul>
     * <li><code>MODE_AND</code>: All of the sub-validators are valid
     * <li><code>MODE_OR</code>: Any of the sub-validators are valid
     * </ul>
     * 
     * If the value is invalid, validation error is thrown. If the error message
     * is set (non-null), it is used. If the error message has not been set, the
     * first error occurred is thrown.
     * </p>
     * 
     * @param value
     *            the value to check.
     * @throws Validator.InvalidValueException
     *             if the value is not valid.
     */
    @Override
    public void validate(Object value) throws Validator.InvalidValueException {
        switch (mode) {
        case MODE_AND:
            for (Validator validator : validators) {
                validator.validate(value);
            }
            return;

        case MODE_OR:
            Validator.InvalidValueException first = null;
            for (Validator v : validators) {
                try {
                    v.validate(value);
                    return;
                } catch (final Validator.InvalidValueException e) {
                    if (first == null) {
                        first = e;
                    }
                }
            }
            if (first == null) {
                return;
            }
            final String em = getErrorMessage();
            if (em != null) {
                throw new Validator.InvalidValueException(em);
            } else {
                throw first;
            }
        }
        throw new IllegalStateException(
                "The validator is in unsupported operation mode");
    }

    /**
     * Checks the validity of the the given value. The value is valid, if:
     * <ul>
     * <li><code>MODE_AND</code>: All of the sub-validators are valid
     * <li><code>MODE_OR</code>: Any of the sub-validators are valid
     * </ul>
     * 
     * @param value
     *            the value to check.
     */
    public boolean isValid(Object value) {
        switch (mode) {
        case MODE_AND:
            for (Validator v : validators) {
                if (!v.isValid(value)) {
                    return false;
                }
            }
            return true;

        case MODE_OR:
            for (Validator v : validators) {
                if (v.isValid(value)) {
                    return true;
                }
            }
            return false;
        }
        throw new IllegalStateException(
                "The valitor is in unsupported operation mode");
    }

    /**
     * Gets the mode of the validator.
     * 
     * @return Operation mode of the validator: <code>MODE_AND</code> or
     *         <code>MODE_OR</code>.
     */
    public final int getMode() {
        return mode;
    }

    /**
     * Sets the mode of the validator. The valid modes are:
     * <ul>
     * <li><code>MODE_AND</code> (default)
     * <li><code>MODE_OR</code>
     * </ul>
     * 
     * @param mode
     *            the mode to set.
     */
    public void setMode(int mode) {
        if (mode != MODE_AND && mode != MODE_OR) {
            throw new IllegalArgumentException("Mode " + mode + " unsupported");
        }
        this.mode = mode;
    }

    /**
     * Gets the error message for the composite validator. If the error message
     * is null, original error messages of the sub-validators are used instead.
     */
    @Override
    public String getErrorMessage() {
        if (super.getErrorMessage() != null) {
            return super.getErrorMessage();
        }

        // TODO Return composite error message

        return null;
    }

    /**
     * Adds validator to the interface.
     * 
     * @param validator
     *            the Validator object which performs validation checks on this
     *            set of data field values.
     */
    public void addValidator(Validator validator) {
        if (validator == null) {
            return;
        }
        validators.add(validator);
    }

    /**
     * Removes a validator from the composite.
     * 
     * @param validator
     *            the Validator object which performs validation checks on this
     *            set of data field values.
     */
    public void removeValidator(Validator validator) {
        validators.remove(validator);
    }

    /**
     * Gets sub-validators by class.
     * 
     * <p>
     * If the component contains directly or recursively (it contains another
     * composite containing the validator) validators compatible with given type
     * they are returned. This only applies to <code>AND</code> mode composite
     * validators.
     * </p>
     * 
     * <p>
     * If the validator is in <code>OR</code> mode or does not contain any
     * validators of given type null is returned.
     * </p>
     * 
     * @return Collection<Validator> of validators compatible with given type
     *         that must apply or null if none fould.
     */
    public Collection<Validator> getSubValidators(Class validatorType) {
        if (mode != MODE_AND) {
            return null;
        }

        final HashSet<Validator> found = new HashSet<Validator>();
        for (Validator v : validators) {
            if (validatorType.isAssignableFrom(v.getClass())) {
                found.add(v);
            }
            if (v instanceof CompositeValidator
                    && ((CompositeValidator) v).getMode() == MODE_AND) {
                final Collection<Validator> c = ((CompositeValidator) v)
                        .getSubValidators(validatorType);
                if (c != null) {
                    found.addAll(c);
                }
            }
        }

        return found.isEmpty() ? null : found;
    }

}
