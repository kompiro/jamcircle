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
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.vaadin.data.Buffered;
import com.vaadin.data.Property;
import com.vaadin.data.Validatable;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.event.Action;
import com.vaadin.event.ActionManager;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.terminal.CompositeErrorMessage;
import com.vaadin.terminal.ErrorMessage;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

/**
 * <p>
 * Abstract field component for implementing buffered property editors. The
 * field may hold an internal value, or it may be connected to any data source
 * that implements the {@link com.vaadin.data.Property}interface.
 * <code>AbstractField</code> implements that interface itself, too, so
 * accessing the Property value represented by it is straightforward.
 * </p>
 * 
 * <p>
 * AbstractField also provides the {@link com.vaadin.data.Buffered} interface
 * for buffering the data source value. By default the Field is in write
 * through-mode and {@link #setWriteThrough(boolean)}should be called to enable
 * buffering.
 * </p>
 * 
 * <p>
 * The class also supports {@link com.vaadin.data.Validator validators} to make
 * sure the value contained in the field is valid.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 3.0
 */
@SuppressWarnings("serial")
public abstract class AbstractField extends AbstractComponent implements Field,
        Property.ReadOnlyStatusChangeListener,
        Property.ReadOnlyStatusChangeNotifier, Action.ShortcutNotifier {

    /* Private members */

    /**
     * Value of the abstract field.
     */
    private Object value;

    /**
     * Connected data-source.
     */
    private Property dataSource = null;

    /**
     * The list of validators.
     */
    private LinkedList<Validator> validators = null;

    /**
     * Auto commit mode.
     */
    private boolean writeTroughMode = true;

    /**
     * Reads the value from data-source, when it is not modified.
     */
    private boolean readTroughMode = true;

    /**
     * Is the field modified but not committed.
     */
    private boolean modified = false;

    /**
     * Should value change event propagation from property data source to
     * listeners of the field be suppressed. This is used internally while the
     * field makes changes to the property value.
     */
    private boolean suppressValueChangePropagation = false;

    /**
     * Current source exception.
     */
    private Buffered.SourceException currentBufferedSourceException = null;

    /**
     * Are the invalid values allowed in fields ?
     */
    private boolean invalidAllowed = true;

    /**
     * Are the invalid values committed ?
     */
    private boolean invalidCommitted = false;

    /**
     * The tab order number of this field.
     */
    private int tabIndex = 0;

    /**
     * Required field.
     */
    private boolean required = false;

    /**
     * The error message for the exception that is thrown when the field is
     * required but empty.
     */
    private String requiredError = "";

    /**
     * Is automatic validation enabled.
     */
    private boolean validationVisible = true;

    /**
     * Keeps track of the Actions added to this component; the actual
     * handling/notifying is delegated, usually to the containing window.
     */
    private ActionManager actionManager;

    /* Component basics */

    /*
     * Paints the field. Don't add a JavaDoc comment here, we use the default
     * documentation from the implemented interface.
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {

        // The tab ordering number
        if (getTabIndex() != 0) {
            target.addAttribute("tabindex", getTabIndex());
        }

        // If the field is modified, but not committed, set modified attribute
        if (isModified()) {
            target.addAttribute("modified", true);
        }

        // Adds the required attribute
        if (!isReadOnly() && isRequired()) {
            target.addAttribute("required", true);
        }

        // Hide the error indicator if needed
        if (isRequired() && isEmpty() && getComponentError() == null
                && getErrorMessage() != null) {
            target.addAttribute("hideErrors", true);
        }
    }

    /*
     * Gets the field type Don't add a JavaDoc comment here, we use the default
     * documentation from the implemented interface.
     */
    public abstract Class getType();

    /**
     * The abstract field is read only also if the data source is in read only
     * mode.
     */
    @Override
    public boolean isReadOnly() {
        return super.isReadOnly()
                || (dataSource != null && dataSource.isReadOnly());
    }

    /**
     * Changes the readonly state and throw read-only status change events.
     * 
     * @see com.vaadin.ui.Component#setReadOnly(boolean)
     */
    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        fireReadOnlyStatusChange();
    }

    /**
     * Tests if the invalid data is committed to datasource.
     * 
     * @see com.vaadin.data.BufferedValidatable#isInvalidCommitted()
     */
    public boolean isInvalidCommitted() {
        return invalidCommitted;
    }

    /**
     * Sets if the invalid data should be committed to datasource.
     * 
     * @see com.vaadin.data.BufferedValidatable#setInvalidCommitted(boolean)
     */
    public void setInvalidCommitted(boolean isCommitted) {
        invalidCommitted = isCommitted;
    }

    /*
     * Saves the current value to the data source Don't add a JavaDoc comment
     * here, we use the default documentation from the implemented interface.
     */
    public void commit() throws Buffered.SourceException, InvalidValueException {
        if (dataSource != null && !dataSource.isReadOnly()) {
            if ((isInvalidCommitted() || isValid())) {
                final Object newValue = getValue();
                try {

                    // Commits the value to datasource.
                    suppressValueChangePropagation = true;
                    dataSource.setValue(newValue);

                } catch (final Throwable e) {

                    // Sets the buffering state.
                    currentBufferedSourceException = new Buffered.SourceException(
                            this, e);
                    requestRepaint();

                    // Throws the source exception.
                    throw currentBufferedSourceException;
                } finally {
                    suppressValueChangePropagation = false;
                }
            } else {
                /* An invalid value and we don't allow them, throw the exception */
                validate();
            }
        }

        boolean repaintNeeded = false;

        // The abstract field is not modified anymore
        if (modified) {
            modified = false;
            repaintNeeded = true;
        }

        // If successful, remove set the buffering state to be ok
        if (currentBufferedSourceException != null) {
            currentBufferedSourceException = null;
            repaintNeeded = true;
        }

        if (repaintNeeded) {
            requestRepaint();
        }
    }

    /*
     * Updates the value from the data source. Don't add a JavaDoc comment here,
     * we use the default documentation from the implemented interface.
     */
    public void discard() throws Buffered.SourceException {
        if (dataSource != null) {

            // Gets the correct value from datasource
            Object newValue;
            try {

                // Discards buffer by overwriting from datasource
                newValue = String.class == getType() ? dataSource.toString()
                        : dataSource.getValue();

                // If successful, remove set the buffering state to be ok
                if (currentBufferedSourceException != null) {
                    currentBufferedSourceException = null;
                    requestRepaint();
                }
            } catch (final Throwable e) {

                // Sets the buffering state
                currentBufferedSourceException = new Buffered.SourceException(
                        this, e);
                requestRepaint();

                // Throws the source exception
                throw currentBufferedSourceException;
            }

            final boolean wasModified = isModified();
            modified = false;

            // If the new value differs from the previous one
            if ((newValue == null && value != null)
                    || (newValue != null && !newValue.equals(value))) {
                setInternalValue(newValue);
                fireValueChange(false);
            }

            // If the value did not change, but the modification status did
            else if (wasModified) {
                requestRepaint();
            }
        }
    }

    /*
     * Has the field been modified since the last commit()? Don't add a JavaDoc
     * comment here, we use the default documentation from the implemented
     * interface.
     */
    public boolean isModified() {
        return modified;
    }

    /*
     * Tests if the field is in write-through mode. Don't add a JavaDoc comment
     * here, we use the default documentation from the implemented interface.
     */
    public boolean isWriteThrough() {
        return writeTroughMode;
    }

    /*
     * Sets the field's write-through mode to the specified status Don't add a
     * JavaDoc comment here, we use the default documentation from the
     * implemented interface.
     */
    public void setWriteThrough(boolean writeThrough)
            throws Buffered.SourceException, InvalidValueException {
        if (writeTroughMode == writeThrough) {
            return;
        }
        writeTroughMode = writeThrough;
        if (writeTroughMode) {
            commit();
        }
    }

    /*
     * Tests if the field is in read-through mode. Don't add a JavaDoc comment
     * here, we use the default documentation from the implemented interface.
     */
    public boolean isReadThrough() {
        return readTroughMode;
    }

    /*
     * Sets the field's read-through mode to the specified status Don't add a
     * JavaDoc comment here, we use the default documentation from the
     * implemented interface.
     */
    public void setReadThrough(boolean readTrough)
            throws Buffered.SourceException {
        if (readTroughMode == readTrough) {
            return;
        }
        readTroughMode = readTrough;
        if (!isModified() && readTroughMode && dataSource != null) {
            setInternalValue(String.class == getType() ? dataSource.toString()
                    : dataSource.getValue());
            fireValueChange(false);
        }
    }

    /* Property interface implementation */

    /**
     * Returns the value of the Property in human readable textual format.
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final Object value = getValue();
        if (value == null) {
            return null;
        }
        return getValue().toString();
    }

    /**
     * Gets the current value of the field.
     * 
     * <p>
     * This is the visible, modified and possible invalid value the user have
     * entered to the field. In the read-through mode, the abstract buffer is
     * also updated and validation is performed.
     * </p>
     * 
     * <p>
     * Note that the object returned is compatible with getType(). For example,
     * if the type is String, this returns Strings even when the underlying
     * datasource is of some other type. In order to access the datasources
     * native type, use getPropertyDatasource().getValue() instead.
     * </p>
     * 
     * <p>
     * Note that when you extend AbstractField, you must reimplement this method
     * if datasource.getValue() is not assignable to class returned by getType()
     * AND getType() is not String. In case of Strings, getValue() calls
     * datasource.toString() instead of datasource.getValue().
     * </p>
     * 
     * @return the current value of the field.
     */
    public Object getValue() {

        // Give the value from abstract buffers if the field if possible
        if (dataSource == null || !isReadThrough() || isModified()) {
            return value;
        }

        Object newValue = String.class == getType() ? dataSource.toString()
                : dataSource.getValue();
        if ((newValue == null && value != null)
                || (newValue != null && !newValue.equals(value))) {
            setInternalValue(newValue);
            fireValueChange(false);
        }

        return newValue;
    }

    /**
     * Sets the value of the field.
     * 
     * @param newValue
     *            the New value of the field.
     * @throws Property.ReadOnlyException
     * @throws Property.ConversionException
     */
    public void setValue(Object newValue) throws Property.ReadOnlyException,
            Property.ConversionException {
        setValue(newValue, false);
    }

    /**
     * Sets the value of the field.
     * 
     * @param newValue
     *            the New value of the field.
     * @param repaintIsNotNeeded
     *            True iff caller is sure that repaint is not needed.
     * @throws Property.ReadOnlyException
     * @throws Property.ConversionException
     */
    protected void setValue(Object newValue, boolean repaintIsNotNeeded)
            throws Property.ReadOnlyException, Property.ConversionException {

        if ((newValue == null && value != null)
                || (newValue != null && !newValue.equals(value))) {

            // Read only fields can not be changed
            if (isReadOnly()) {
                throw new Property.ReadOnlyException();
            }

            // Repaint is needed even when the client thinks that it knows the
            // new state if validity of the component may change
            if (repaintIsNotNeeded && (isRequired() || getValidators() != null)) {
                repaintIsNotNeeded = false;
            }

            // If invalid values are not allowed, the value must be checked
            if (!isInvalidAllowed()) {
                final Collection<Validator> v = getValidators();
                if (v != null) {
                    for (final Iterator<Validator> i = v.iterator(); i
                            .hasNext();) {
                        (i.next()).validate(newValue);
                    }
                }
            }

            // Changes the value
            setInternalValue(newValue);
            modified = dataSource != null;

            // In write trough mode , try to commit
            if (isWriteThrough() && dataSource != null
                    && (isInvalidCommitted() || isValid())) {
                try {

                    // Commits the value to datasource
                    suppressValueChangePropagation = true;
                    dataSource.setValue(newValue);

                    // The buffer is now unmodified
                    modified = false;

                } catch (final Throwable e) {

                    // Sets the buffering state
                    currentBufferedSourceException = new Buffered.SourceException(
                            this, e);
                    requestRepaint();

                    // Throws the source exception
                    throw currentBufferedSourceException;
                } finally {
                    suppressValueChangePropagation = false;
                }
            }

            // If successful, remove set the buffering state to be ok
            if (currentBufferedSourceException != null) {
                currentBufferedSourceException = null;
                requestRepaint();
            }

            // Fires the value change
            fireValueChange(repaintIsNotNeeded);
        }
    }

    /* External data source */

    /**
     * Gets the current data source of the field, if any.
     * 
     * @return the current data source as a Property, or <code>null</code> if
     *         none defined.
     */
    public Property getPropertyDataSource() {
        return dataSource;
    }

    /**
     * <p>
     * Sets the specified Property as the data source for the field. All
     * uncommitted changes to the field are discarded and the value is refreshed
     * from the new data source.
     * </p>
     * 
     * <p>
     * If the datasource has any validators, the same validators are added to
     * the field. Because the default behavior of the field is to allow invalid
     * values, but not to allow committing them, this only adds visual error
     * messages to fields and do not allow committing them as long as the value
     * is invalid. After the value is valid, the error message is not shown and
     * the commit can be done normally.
     * </p>
     * 
     * @param newDataSource
     *            the new data source Property.
     */
    public void setPropertyDataSource(Property newDataSource) {

        // Saves the old value
        final Object oldValue = value;

        // Discards all changes to old datasource
        try {
            discard();
        } catch (final Buffered.SourceException ignored) {
        }

        // Stops listening the old data source changes
        if (dataSource != null
                && Property.ValueChangeNotifier.class
                        .isAssignableFrom(dataSource.getClass())) {
            ((Property.ValueChangeNotifier) dataSource).removeListener(this);
        }
        if (dataSource != null
                && Property.ReadOnlyStatusChangeNotifier.class
                        .isAssignableFrom(dataSource.getClass())) {
            ((Property.ReadOnlyStatusChangeNotifier) dataSource)
                    .removeListener(this);
        }

        // Sets the new data source
        dataSource = newDataSource;

        // Gets the value from source
        try {
            if (dataSource != null) {
                setInternalValue(String.class == getType() ? dataSource
                        .toString() : dataSource.getValue());
            }
            modified = false;
        } catch (final Throwable e) {
            currentBufferedSourceException = new Buffered.SourceException(this,
                    e);
            modified = true;
        }

        // Listens the new data source if possible
        if (dataSource instanceof Property.ValueChangeNotifier) {
            ((Property.ValueChangeNotifier) dataSource).addListener(this);
        }
        if (dataSource instanceof Property.ReadOnlyStatusChangeNotifier) {
            ((Property.ReadOnlyStatusChangeNotifier) dataSource)
                    .addListener(this);
        }

        // Copy the validators from the data source
        if (dataSource instanceof Validatable) {
            final Collection<Validator> validators = ((Validatable) dataSource)
                    .getValidators();
            if (validators != null) {
                for (final Iterator<Validator> i = validators.iterator(); i
                        .hasNext();) {
                    addValidator(i.next());
                }
            }
        }

        // Fires value change if the value has changed
        if ((value != oldValue)
                && ((value != null && !value.equals(oldValue)) || value == null)) {
            fireValueChange(false);
        }
    }

    /* Validation */

    /**
     * Adds a new validator for the field's value. All validators added to a
     * field are checked each time the its value changes.
     * 
     * @param validator
     *            the new validator to be added.
     */
    public void addValidator(Validator validator) {
        if (validators == null) {
            validators = new LinkedList<Validator>();
        }
        validators.add(validator);
        requestRepaint();
    }

    /**
     * Gets the validators of the field.
     * 
     * @return the Unmodifiable collection that holds all validators for the
     *         field.
     */
    public Collection<Validator> getValidators() {
        if (validators == null || validators.isEmpty()) {
            return null;
        }
        return Collections.unmodifiableCollection(validators);
    }

    /**
     * Removes the validator from the field.
     * 
     * @param validator
     *            the validator to remove.
     */
    public void removeValidator(Validator validator) {
        if (validators != null) {
            validators.remove(validator);
        }
        requestRepaint();
    }

    /**
     * Tests the current value against registered validators if the field is not
     * empty. If the field is empty it is considered valid if it is not required
     * and invalid otherwise. Validators are never checked for empty fields.
     * 
     * @return <code>true</code> if all registered validators claim that the
     *         current value is valid or if the field is empty and not required,
     *         <code>false</code> otherwise.
     */
    public boolean isValid() {

        if (isEmpty()) {
            if (isRequired()) {
                return false;
            } else {
                return true;
            }
        }

        if (validators == null) {
            return true;
        }

        final Object value = getValue();
        for (final Iterator<Validator> i = validators.iterator(); i.hasNext();) {
            if (!(i.next()).isValid(value)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks the validity of the Validatable by validating the field with all
     * attached validators except when the field is empty. An empty field is
     * invalid if it is required and valid otherwise.
     * 
     * The "required" validation is a built-in validation feature. If the field
     * is required, but empty, validation will throw an EmptyValueException with
     * the error message set with setRequiredError().
     * 
     * @see com.vaadin.data.Validatable#validate()
     */
    public void validate() throws Validator.InvalidValueException {

        if (isEmpty()) {
            if (isRequired()) {
                throw new Validator.EmptyValueException(requiredError);
            } else {
                return;
            }
        }

        // If there is no validator, there can not be any errors
        if (validators == null) {
            return;
        }

        // Initialize temps
        Validator.InvalidValueException firstError = null;
        LinkedList<InvalidValueException> errors = null;
        final Object value = getValue();

        // Gets all the validation errors
        for (final Iterator<Validator> i = validators.iterator(); i.hasNext();) {
            try {
                (i.next()).validate(value);
            } catch (final Validator.InvalidValueException e) {
                if (firstError == null) {
                    firstError = e;
                } else {
                    if (errors == null) {
                        errors = new LinkedList<InvalidValueException>();
                        errors.add(firstError);
                    }
                    errors.add(e);
                }
            }
        }

        // If there were no error
        if (firstError == null) {
            return;
        }

        // If only one error occurred, throw it forwards
        if (errors == null) {
            throw firstError;
        }

        // Creates composite validator
        final Validator.InvalidValueException[] exceptions = new Validator.InvalidValueException[errors
                .size()];
        int index = 0;
        for (final Iterator<InvalidValueException> i = errors.iterator(); i
                .hasNext();) {
            exceptions[index++] = i.next();
        }

        throw new Validator.InvalidValueException(null, exceptions);
    }

    /**
     * Fields allow invalid values by default. In most cases this is wanted,
     * because the field otherwise visually forget the user input immediately.
     * 
     * @return true iff the invalid values are allowed.
     * @see com.vaadin.data.Validatable#isInvalidAllowed()
     */
    public boolean isInvalidAllowed() {
        return invalidAllowed;
    }

    /**
     * Fields allow invalid values by default. In most cases this is wanted,
     * because the field otherwise visually forget the user input immediately.
     * <p>
     * In common setting where the user wants to assure the correctness of the
     * datasource, but allow temporarily invalid contents in the field, the user
     * should add the validators to datasource, that should not allow invalid
     * values. The validators are automatically copied to the field when the
     * datasource is set.
     * </p>
     * 
     * @see com.vaadin.data.Validatable#setInvalidAllowed(boolean)
     */
    public void setInvalidAllowed(boolean invalidAllowed)
            throws UnsupportedOperationException {
        this.invalidAllowed = invalidAllowed;
    }

    /**
     * Error messages shown by the fields are composites of the error message
     * thrown by the superclasses (that is the component error message),
     * validation errors and buffered source errors.
     * 
     * @see com.vaadin.ui.AbstractComponent#getErrorMessage()
     */
    @Override
    public ErrorMessage getErrorMessage() {

        /*
         * Check validation errors only if automatic validation is enabled.
         * Empty, required fields will generate a validation error containing
         * the requiredError string. For these fields the exclamation mark will
         * be hidden but the error must still be sent to the client.
         */
        ErrorMessage validationError = null;
        if (isValidationVisible()) {
            try {
                validate();
            } catch (Validator.InvalidValueException e) {
                if (!e.isInvisible()) {
                    validationError = e;
                }
            }
        }

        // Check if there are any systems errors
        final ErrorMessage superError = super.getErrorMessage();

        // Return if there are no errors at all
        if (superError == null && validationError == null
                && currentBufferedSourceException == null) {
            return null;
        }

        // Throw combination of the error types
        return new CompositeErrorMessage(new ErrorMessage[] { superError,
                validationError, currentBufferedSourceException });

    }

    /* Value change events */

    private static final Method VALUE_CHANGE_METHOD;

    static {
        try {
            VALUE_CHANGE_METHOD = Property.ValueChangeListener.class
                    .getDeclaredMethod("valueChange",
                            new Class[] { Property.ValueChangeEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in AbstractField");
        }
    }

    /*
     * Adds a value change listener for the field. Don't add a JavaDoc comment
     * here, we use the default documentation from the implemented interface.
     */
    public void addListener(Property.ValueChangeListener listener) {
        addListener(AbstractField.ValueChangeEvent.class, listener,
                VALUE_CHANGE_METHOD);
    }

    /*
     * Removes a value change listener from the field. Don't add a JavaDoc
     * comment here, we use the default documentation from the implemented
     * interface.
     */
    public void removeListener(Property.ValueChangeListener listener) {
        removeListener(AbstractField.ValueChangeEvent.class, listener,
                VALUE_CHANGE_METHOD);
    }

    /**
     * Emits the value change event. The value contained in the field is
     * validated before the event is created.
     */
    protected void fireValueChange(boolean repaintIsNotNeeded) {
        fireEvent(new AbstractField.ValueChangeEvent(this));
        if (!repaintIsNotNeeded) {
            requestRepaint();
        }
    }

    /* Read-only status change events */

    private static final Method READ_ONLY_STATUS_CHANGE_METHOD;

    static {
        try {
            READ_ONLY_STATUS_CHANGE_METHOD = Property.ReadOnlyStatusChangeListener.class
                    .getDeclaredMethod(
                            "readOnlyStatusChange",
                            new Class[] { Property.ReadOnlyStatusChangeEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in AbstractField");
        }
    }

    /**
     * React to read only status changes of the property by requesting a
     * repaint.
     * 
     * @see Property.ReadOnlyStatusChangeListener
     */
    public void readOnlyStatusChange(Property.ReadOnlyStatusChangeEvent event) {
        requestRepaint();
    }

    /**
     * An <code>Event</code> object specifying the Property whose read-only
     * status has changed.
     * 
     * @author IT Mill Ltd.
     * @version
     * 6.4.8
     * @since 3.0
     */
    public class ReadOnlyStatusChangeEvent extends Component.Event implements
            Property.ReadOnlyStatusChangeEvent, Serializable {

        /**
         * New instance of text change event.
         * 
         * @param source
         *            the Source of the event.
         */
        public ReadOnlyStatusChangeEvent(AbstractField source) {
            super(source);
        }

        /**
         * Property where the event occurred.
         * 
         * @return the Source of the event.
         */
        public Property getProperty() {
            return (Property) getSource();
        }
    }

    /*
     * Adds a read-only status change listener for the field. Don't add a
     * JavaDoc comment here, we use the default documentation from the
     * implemented interface.
     */
    public void addListener(Property.ReadOnlyStatusChangeListener listener) {
        addListener(Property.ReadOnlyStatusChangeEvent.class, listener,
                READ_ONLY_STATUS_CHANGE_METHOD);
    }

    /*
     * Removes a read-only status change listener from the field. Don't add a
     * JavaDoc comment here, we use the default documentation from the
     * implemented interface.
     */
    public void removeListener(Property.ReadOnlyStatusChangeListener listener) {
        removeListener(Property.ReadOnlyStatusChangeEvent.class, listener,
                READ_ONLY_STATUS_CHANGE_METHOD);
    }

    /**
     * Emits the read-only status change event. The value contained in the field
     * is validated before the event is created.
     */
    protected void fireReadOnlyStatusChange() {
        fireEvent(new AbstractField.ReadOnlyStatusChangeEvent(this));
    }

    /**
     * This method listens to data source value changes and passes the changes
     * forwards.
     * 
     * Changes are not forwarded to the listeners of the field during internal
     * operations of the field to avoid duplicate notifications.
     * 
     * @param event
     *            the value change event telling the data source contents have
     *            changed.
     */
    public void valueChange(Property.ValueChangeEvent event) {
        if (!suppressValueChangePropagation
                && (isReadThrough() || !isModified())) {
            fireValueChange(false);
        }
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void focus() {
        super.focus();
    }

    /**
     * Creates abstract field by the type of the property.
     * 
     * <p>
     * This returns most suitable field type for editing property of given type.
     * </p>
     * 
     * @param propertyType
     *            the Type of the property, that needs to be edited.
     */
    public static AbstractField constructField(Class propertyType) {

        // Null typed properties can not be edited
        if (propertyType == null) {
            return null;
        }

        // Date field
        if (Date.class.isAssignableFrom(propertyType)) {
            return new DateField();
        }

        // Boolean field
        if (Boolean.class.isAssignableFrom(propertyType)) {
            final Button button = new Button("");
            button.setSwitchMode(true);
            button.setImmediate(false);
            return button;
        }

        // Text field is used by default
        return new TextField();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component.Focusable#getTabIndex()
     */
    public int getTabIndex() {
        return tabIndex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component.Focusable#setTabIndex(int)
     */
    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
        requestRepaint();
    }

    /**
     * Sets the internal field value. This is purely used by AbstractField to
     * change the internal Field value. It does not trigger valuechange events.
     * It can be overriden by the inheriting classes to update all dependent
     * variables.
     * 
     * @param newValue
     *            the new value to be set.
     */
    protected void setInternalValue(Object newValue) {
        value = newValue;
        if (validators != null && !validators.isEmpty()) {
            requestRepaint();
        }
    }

    /**
     * Notifies the component that it is connected to an application.
     * 
     * @see com.vaadin.ui.Component#attach()
     */
    @Override
    public void attach() {
        super.attach();
        if (actionManager != null) {
            actionManager.setViewer(getWindow());
        }
    }

    @Override
    public void detach() {
        super.detach();
        if (actionManager != null) {
            actionManager.setViewer((Window) null);
        }
    }

    /**
     * Is this field required. Required fields must filled by the user.
     * 
     * If the field is required, it is visually indicated in the user interface.
     * Furthermore, setting field to be required implicitly adds "non-empty"
     * validator and thus isValid() == false or any isEmpty() fields. In those
     * cases validation errors are not painted as it is obvious that the user
     * must fill in the required fields.
     * 
     * On the other hand, for the non-required fields isValid() == true if the
     * field isEmpty() regardless of any attached validators.
     * 
     * 
     * @return <code>true</code> if the field is required .otherwise
     *         <code>false</code>.
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Sets the field required. Required fields must filled by the user.
     * 
     * If the field is required, it is visually indicated in the user interface.
     * Furthermore, setting field to be required implicitly adds "non-empty"
     * validator and thus isValid() == false or any isEmpty() fields. In those
     * cases validation errors are not painted as it is obvious that the user
     * must fill in the required fields.
     * 
     * On the other hand, for the non-required fields isValid() == true if the
     * field isEmpty() regardless of any attached validators.
     * 
     * @param required
     *            Is the field required.
     */
    public void setRequired(boolean required) {
        this.required = required;
        requestRepaint();
    }

    /**
     * Set the error that is show if this field is required, but empty. When
     * setting requiredMessage to be "" or null, no error pop-up or exclamation
     * mark is shown for a empty required field. This faults to "". Even in
     * those cases isValid() returns false for empty required fields.
     * 
     * @param requiredMessage
     *            Message to be shown when this field is required, but empty.
     */
    public void setRequiredError(String requiredMessage) {
        requiredError = requiredMessage;
        requestRepaint();
    }

    public String getRequiredError() {
        return requiredError;
    }

    /**
     * Is the field empty?
     * 
     * In general, "empty" state is same as null. As an exception, TextField
     * also treats empty string as "empty".
     */
    protected boolean isEmpty() {
        return (getValue() == null);
    }

    /**
     * Is automatic, visible validation enabled?
     * 
     * If automatic validation is enabled, any validators connected to this
     * component are evaluated while painting the component and potential error
     * messages are sent to client. If the automatic validation is turned off,
     * isValid() and validate() methods still work, but one must show the
     * validation in their own code.
     * 
     * @return True, if automatic validation is enabled.
     */
    public boolean isValidationVisible() {
        return validationVisible;
    }

    /**
     * Enable or disable automatic, visible validation.
     * 
     * If automatic validation is enabled, any validators connected to this
     * component are evaluated while painting the component and potential error
     * messages are sent to client. If the automatic validation is turned off,
     * isValid() and validate() methods still work, but one must show the
     * validation in their own code.
     * 
     * @param validateAutomatically
     *            True, if automatic validation is enabled.
     */
    public void setValidationVisible(boolean validateAutomatically) {
        if (validationVisible != validateAutomatically) {
            requestRepaint();
            validationVisible = validateAutomatically;
        }
    }

    /**
     * Sets the current buffered source exception.
     * 
     * @param currentBufferedSourceException
     */
    public void setCurrentBufferedSourceException(
            Buffered.SourceException currentBufferedSourceException) {
        this.currentBufferedSourceException = currentBufferedSourceException;
        requestRepaint();
    }

    /*
     * Actions
     */

    /**
     * Gets the {@link ActionManager} used to manage the
     * {@link ShortcutListener}s added to this {@link Field}.
     * 
     * @return the ActionManager in use
     */
    protected ActionManager getActionManager() {
        if (actionManager == null) {
            actionManager = new ActionManager();
            if (getWindow() != null) {
                actionManager.setViewer(getWindow());
            }
        }
        return actionManager;
    }

    public void addShortcutListener(ShortcutListener shortcut) {
        getActionManager().addAction(shortcut);
    }

    public void removeShortcutListener(ShortcutListener shortcut) {
        if (actionManager != null) {
            actionManager.removeAction(shortcut);
        }
    }

    /**
     * A ready-made {@link ShortcutListener} that focuses the given
     * {@link Focusable} (usually a {@link Field}) when the keyboard shortcut is
     * invoked.
     * 
     */
    public static class FocusShortcut extends ShortcutListener {
        protected Focusable focusable;

        /**
         * Creates a keyboard shortcut for focusing the given {@link Focusable}
         * using the shorthand notation defined in {@link ShortcutAction}.
         * 
         * @param focusable
         *            to focused when the shortcut is invoked
         * @param shorthandCaption
         *            caption with keycode and modifiers indicated
         */
        public FocusShortcut(Focusable focusable, String shorthandCaption) {
            super(shorthandCaption);
            this.focusable = focusable;
        }

        /**
         * Creates a keyboard shortcut for focusing the given {@link Focusable}.
         * 
         * @param focusable
         *            to focused when the shortcut is invoked
         * @param keyCode
         *            keycode that invokes the shortcut
         * @param modifiers
         *            modifiers required to invoke the shortcut
         */
        public FocusShortcut(Focusable focusable, int keyCode, int... modifiers) {
            super(null, keyCode, modifiers);
            this.focusable = focusable;
        }

        /**
         * Creates a keyboard shortcut for focusing the given {@link Focusable}.
         * 
         * @param focusable
         *            to focused when the shortcut is invoked
         * @param keyCode
         *            keycode that invokes the shortcut
         */
        public FocusShortcut(Focusable focusable, int keyCode) {
            this(focusable, keyCode, null);
        }

        @Override
        public void handleAction(Object sender, Object target) {
            focusable.focus();
        }
    }
}
