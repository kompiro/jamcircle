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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.UserError;
import com.vaadin.terminal.gwt.client.ui.VDateField;
import com.vaadin.terminal.gwt.client.ui.VPopupCalendar;

/**
 * <p>
 * A date editor component that can be bound to any bindable Property. that is
 * compatible with <code>java.util.Date</code>.
 * </p>
 * <p>
 * Since <code>DateField</code> extends <code>AbstractField</code> it implements
 * the {@link com.vaadin.data.Buffered}interface. A <code>DateField</code> is in
 * write-through mode by default, so
 * {@link com.vaadin.ui.AbstractField#setWriteThrough(boolean)}must be called to
 * enable buffering.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 3.0
 */
@SuppressWarnings("serial")
@ClientWidget(VPopupCalendar.class)
public class DateField extends AbstractField implements
        FieldEvents.BlurNotifier, FieldEvents.FocusNotifier {

    /* Private members */

    /**
     * Resolution identifier: milliseconds.
     */
    public static final int RESOLUTION_MSEC = 0;

    /**
     * Resolution identifier: seconds.
     */
    public static final int RESOLUTION_SEC = 1;

    /**
     * Resolution identifier: minutes.
     */
    public static final int RESOLUTION_MIN = 2;

    /**
     * Resolution identifier: hours.
     */
    public static final int RESOLUTION_HOUR = 3;

    /**
     * Resolution identifier: days.
     */
    public static final int RESOLUTION_DAY = 4;

    /**
     * Resolution identifier: months.
     */
    public static final int RESOLUTION_MONTH = 5;

    /**
     * Resolution identifier: years.
     */
    public static final int RESOLUTION_YEAR = 6;

    /**
     * Popup date selector (calendar).
     */
    protected static final String TYPE_POPUP = "popup";

    /**
     * Inline date selector (calendar).
     */
    protected static final String TYPE_INLINE = "inline";

    /**
     * Specified widget type.
     */
    protected String type = TYPE_POPUP;

    /**
     * Specified smallest modifiable unit.
     */
    private int resolution = RESOLUTION_MSEC;

    /**
     * Specified largest modifiable unit.
     */
    private static final int largestModifiable = RESOLUTION_YEAR;

    /**
     * The internal calendar to be used in java.utl.Date conversions.
     */
    private transient Calendar calendar;

    /**
     * Overridden format string
     */
    private String dateFormat;

    private boolean lenient = false;

    private String dateString = null;

    /**
     * Was the last entered string parsable?
     */
    private boolean parsingSucceeded = true;

    /**
     * Determines if week numbers are shown in the date selector.
     */
    private boolean showISOWeekNumbers = false;

    /* Constructors */

    /**
     * Constructs an empty <code>DateField</code> with no caption.
     */
    public DateField() {
        setInvalidAllowed(false);
    }

    /**
     * Constructs an empty <code>DateField</code> with caption.
     * 
     * @param caption
     *            the caption of the datefield.
     */
    public DateField(String caption) {
        setCaption(caption);
        setInvalidAllowed(false);
    }

    /**
     * Constructs a new <code>DateField</code> that's bound to the specified
     * <code>Property</code> and has the given caption <code>String</code>.
     * 
     * @param caption
     *            the caption <code>String</code> for the editor.
     * @param dataSource
     *            the Property to be edited with this editor.
     */
    public DateField(String caption, Property dataSource) {
        this(dataSource);
        setCaption(caption);
    }

    /**
     * Constructs a new <code>DateField</code> that's bound to the specified
     * <code>Property</code> and has no caption.
     * 
     * @param dataSource
     *            the Property to be edited with this editor.
     */
    public DateField(Property dataSource) throws IllegalArgumentException {
        setInvalidAllowed(false);
        if (!Date.class.isAssignableFrom(dataSource.getType())) {
            throw new IllegalArgumentException("Can't use "
                    + dataSource.getType().getName()
                    + " typed property as datasource");
        }

        setPropertyDataSource(dataSource);
    }

    /**
     * Constructs a new <code>DateField</code> with the given caption and
     * initial text contents. The editor constructed this way will not be bound
     * to a Property unless
     * {@link com.vaadin.data.Property.Viewer#setPropertyDataSource(Property)}
     * is called to bind it.
     * 
     * @param caption
     *            the caption <code>String</code> for the editor.
     * @param value
     *            the Date value.
     */
    public DateField(String caption, Date value) {
        setInvalidAllowed(false);
        setValue(value);
        setCaption(caption);
    }

    /* Component basic features */

    /*
     * Paints this component. Don't add a JavaDoc comment here, we use the
     * default documentation from implemented interface.
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        // Adds the locale as attribute
        final Locale l = getLocale();
        if (l != null) {
            target.addAttribute("locale", l.toString());
        }

        if (getDateFormat() != null) {
            target.addAttribute("format", dateFormat);
        }

        if (!isLenient()) {
            target.addAttribute("strict", true);
        }

        target.addAttribute("type", type);
        target.addAttribute(VDateField.WEEK_NUMBERS, isShowISOWeekNumbers());
        target.addAttribute("parsable", parsingSucceeded);

        // Gets the calendar
        final Calendar calendar = getCalendar();
        final Date currentDate = (Date) getValue();

        for (int r = resolution; r <= largestModifiable; r++) {
            switch (r) {
            case RESOLUTION_MSEC:
                target.addVariable(
                        this,
                        "msec",
                        currentDate != null ? calendar
                                .get(Calendar.MILLISECOND) : -1);
                break;
            case RESOLUTION_SEC:
                target.addVariable(this, "sec",
                        currentDate != null ? calendar.get(Calendar.SECOND)
                                : -1);
                break;
            case RESOLUTION_MIN:
                target.addVariable(this, "min",
                        currentDate != null ? calendar.get(Calendar.MINUTE)
                                : -1);
                break;
            case RESOLUTION_HOUR:
                target.addVariable(
                        this,
                        "hour",
                        currentDate != null ? calendar
                                .get(Calendar.HOUR_OF_DAY) : -1);
                break;
            case RESOLUTION_DAY:
                target.addVariable(
                        this,
                        "day",
                        currentDate != null ? calendar
                                .get(Calendar.DAY_OF_MONTH) : -1);
                break;
            case RESOLUTION_MONTH:
                target.addVariable(this, "month",
                        currentDate != null ? calendar.get(Calendar.MONTH) + 1
                                : -1);
                break;
            case RESOLUTION_YEAR:
                target.addVariable(this, "year",
                        currentDate != null ? calendar.get(Calendar.YEAR) : -1);
                break;
            }
        }
    }

    /*
     * Invoked when a variable of the component changes. Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public void changeVariables(Object source, Map variables) {
        super.changeVariables(source, variables);
        setComponentError(null);

        if (!isReadOnly()
                && (variables.containsKey("year")
                        || variables.containsKey("month")
                        || variables.containsKey("day")
                        || variables.containsKey("hour")
                        || variables.containsKey("min")
                        || variables.containsKey("sec")
                        || variables.containsKey("msec") || variables
                        .containsKey("dateString"))) {

            // Old and new dates
            final Date oldDate = (Date) getValue();
            Date newDate = null;

            // this enables analyzing invalid input on the server
            Object o = variables.get("dateString");
            dateString = null;
            if (o != null) {
                dateString = o.toString();
            }

            // Gets the new date in parts
            // Null values are converted to negative values.
            int year = variables.containsKey("year") ? (variables.get("year") == null ? -1
                    : ((Integer) variables.get("year")).intValue())
                    : -1;
            int month = variables.containsKey("month") ? (variables
                    .get("month") == null ? -1 : ((Integer) variables
                    .get("month")).intValue() - 1) : -1;
            int day = variables.containsKey("day") ? (variables.get("day") == null ? -1
                    : ((Integer) variables.get("day")).intValue())
                    : -1;
            int hour = variables.containsKey("hour") ? (variables.get("hour") == null ? -1
                    : ((Integer) variables.get("hour")).intValue())
                    : -1;
            int min = variables.containsKey("min") ? (variables.get("min") == null ? -1
                    : ((Integer) variables.get("min")).intValue())
                    : -1;
            int sec = variables.containsKey("sec") ? (variables.get("sec") == null ? -1
                    : ((Integer) variables.get("sec")).intValue())
                    : -1;
            int msec = variables.containsKey("msec") ? (variables.get("msec") == null ? -1
                    : ((Integer) variables.get("msec")).intValue())
                    : -1;

            // If all of the components is < 0 use the previous value
            if (year < 0 && month < 0 && day < 0 && hour < 0 && min < 0
                    && sec < 0 && msec < 0) {
                newDate = null;
            } else {

                // Clone the calendar for date operation
                final Calendar cal = getCalendar();

                // Make sure that meaningful values exists
                // Use the previous value if some of the variables
                // have not been changed.
                year = year < 0 ? cal.get(Calendar.YEAR) : year;
                month = month < 0 ? cal.get(Calendar.MONTH) : month;
                day = day < 0 ? cal.get(Calendar.DAY_OF_MONTH) : day;
                hour = hour < 0 ? cal.get(Calendar.HOUR_OF_DAY) : hour;
                min = min < 0 ? cal.get(Calendar.MINUTE) : min;
                sec = sec < 0 ? cal.get(Calendar.SECOND) : sec;
                msec = msec < 0 ? cal.get(Calendar.MILLISECOND) : msec;

                // Sets the calendar fields
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, day);
                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.MINUTE, min);
                cal.set(Calendar.SECOND, sec);
                cal.set(Calendar.MILLISECOND, msec);

                // Assigns the date
                newDate = cal.getTime();
            }

            if (newDate == null && dateString != null && !"".equals(dateString)) {
                try {
                    Date parsedDate = handleUnparsableDateString(dateString);
                    parsingSucceeded = true;
                    setValue(parsedDate, true);

                    /*
                     * Ensure the value is sent to the client if the value is
                     * set to the same as the previous (#4304). Does not repaint
                     * if handleUnparsableDateString throws an exception. In
                     * this case the invalid text remains in the DateField.
                     */
                    requestRepaint();
                } catch (ConversionException e) {
                    /*
                     * Sets the component error to the Conversion Exceptions
                     * message. This can be overriden in
                     * handleUnparsableDateString.
                     */
                    setComponentError(new UserError(e.getLocalizedMessage()));

                    /*
                     * The value of the DateField should be null if an invalid
                     * value has been given. Not using setValue() since we do
                     * not want to cause the client side value to change.
                     */
                    parsingSucceeded = false;
                    setInternalValue(null);
                }
            } else if (newDate != oldDate
                    && (newDate == null || !newDate.equals(oldDate))) {
                parsingSucceeded = true;
                setValue(newDate, true); // Don't require a repaint, client
                // updates itself
            }
        }

        if (variables.containsKey(FocusEvent.EVENT_ID)) {
            fireEvent(new FocusEvent(this));
        }

        if (variables.containsKey(BlurEvent.EVENT_ID)) {
            fireEvent(new BlurEvent(this));
        }
    }

    /**
     * This method is called to handle a non-empty date string from the client
     * if the client could not parse it as a Date.
     * 
     * By default, a Property.ConversionException is thrown, and the current
     * value is not modified.
     * 
     * This can be overridden to handle conversions, to return null (equivalent
     * to empty input), to throw an exception or to fire an event.
     * 
     * @param dateString
     * @return parsed Date
     * @throws Property.ConversionException
     *             to keep the old value and indicate an error
     */
    protected Date handleUnparsableDateString(String dateString)
            throws Property.ConversionException {
        throw new Property.ConversionException("Date format not recognized");
    }

    /* Property features */

    /*
     * Gets the edited property's type. Don't add a JavaDoc comment here, we use
     * the default documentation from implemented interface.
     */
    @Override
    public Class getType() {
        return Date.class;
    }

    /*
     * Returns the value of the property in human readable textual format. Don't
     * add a JavaDoc comment here, we use the default documentation from
     * implemented interface.
     */
    @Override
    public String toString() {
        final Date value = (Date) getValue();
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    /*
     * Sets the value of the property. Don't add a JavaDoc comment here, we use
     * the default documentation from implemented interface.
     */
    @Override
    public void setValue(Object newValue) throws Property.ReadOnlyException,
            Property.ConversionException {
        setValue(newValue, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.AbstractField#setValue(java.lang.Object, boolean)
     */
    @Override
    public void setValue(Object newValue, boolean repaintIsNotNeeded)
            throws Property.ReadOnlyException, Property.ConversionException {

        dateString = null;

        // Allows setting dates directly
        if (newValue == null || newValue instanceof Date) {
            try {
                dateString = newValue == null ? null : newValue.toString();
                if (dateString == null && super.getValue() == null) {
                    /*
                     * AbstractField cannot handle invalid changes in client
                     * side so we have to repaint the component manually. No
                     * value change event is also fired.
                     */
                    requestRepaint();

                } else {
                    super.setValue(newValue, repaintIsNotNeeded);
                }

                setComponentError(null);

                parsingSucceeded = true;
            } catch (InvalidValueException ive) {
                // Thrown if validator fails
                parsingSucceeded = false;
                throw ive;
            }
        } else {

            // Try to parse as string
            try {
                final SimpleDateFormat parser = new SimpleDateFormat();
                final Date val = parser.parse(newValue.toString());
                super.setValue(val, repaintIsNotNeeded);
                parsingSucceeded = true;
                dateString = newValue.toString();
            } catch (final ParseException e) {
                parsingSucceeded = false;
                throw new Property.ConversionException(
                        "Date format not recognized");
            }
        }
    }

    /**
     * Sets the DateField datasource. Datasource type must assignable to Date.
     * 
     * @see com.vaadin.data.Property.Viewer#setPropertyDataSource(Property)
     */
    @Override
    public void setPropertyDataSource(Property newDataSource) {
        if (newDataSource == null
                || Date.class.isAssignableFrom(newDataSource.getType())) {
            super.setPropertyDataSource(newDataSource);
        } else {
            throw new IllegalArgumentException(
                    "DateField only supports Date properties");
        }
    }

    @Override
    protected void setInternalValue(Object newValue) {
        // Also set the internal dateString
        if (newValue != null) {
            dateString = newValue.toString();
        }
        super.setInternalValue(newValue);
    }

    /**
     * Gets the resolution.
     * 
     * @return int
     */
    public int getResolution() {
        return resolution;
    }

    /**
     * Sets the resolution of the DateField.
     * 
     * @param resolution
     *            the resolution to set.
     */
    public void setResolution(int resolution) {
        this.resolution = resolution;
        requestRepaint();
    }

    /**
     * Returns new instance calendar used in Date conversions.
     * 
     * Returns new clone of the calendar object initialized using the the
     * current date (if available)
     * 
     * If this is no calendar is assigned the <code>Calendar.getInstance</code>
     * is used.
     * 
     * @return the Calendar.
     * @see #setCalendar(Calendar)
     */
    private Calendar getCalendar() {

        // Makes sure we have an calendar instance
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }

        // Clone the instance
        final Calendar newCal = (Calendar) calendar.clone();

        // Assigns the current time tom calendar.
        final Date currentDate = (Date) getValue();
        if (currentDate != null) {
            newCal.setTime(currentDate);
        }

        return newCal;
    }

    /**
     * Sets formatting used by some component implementations. See
     * {@link SimpleDateFormat} for format details.
     * 
     * By default it is encouraged to used default formatting defined by Locale,
     * but due some JVM bugs it is sometimes necessary to use this method to
     * override formatting. See Vaadin issue #2200.
     * 
     * @param dateFormat
     *            the dateFormat to set
     * 
     * @see com.vaadin.ui.AbstractComponent#setLocale(Locale))
     */
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    /**
     * Reterns a format string used to format date value on client side or null
     * if default formatting from {@link Component#getLocale()} is used.
     * 
     * @return the dateFormat
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * Specifies whether or not date/time interpretation in component is to be
     * lenient.
     * 
     * @see Calendar#setLenient(boolean)
     * @see #isLenient()
     * 
     * @param lenient
     *            true if the lenient mode is to be turned on; false if it is to
     *            be turned off.
     */
    public void setLenient(boolean lenient) {
        this.lenient = lenient;
        requestRepaint();
    }

    /**
     * Returns whether date/time interpretation is to be lenient.
     * 
     * @see #setLenient(boolean)
     * 
     * @return true if the interpretation mode of this calendar is lenient;
     *         false otherwise.
     */
    public boolean isLenient() {
        return lenient;
    }

    public void addListener(FocusListener listener) {
        addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
                FocusListener.focusMethod);
    }

    public void removeListener(FocusListener listener) {
        removeListener(FocusEvent.EVENT_ID, FocusEvent.class, listener);
    }

    public void addListener(BlurListener listener) {
        addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
                BlurListener.blurMethod);
    }

    public void removeListener(BlurListener listener) {
        removeListener(BlurEvent.EVENT_ID, BlurEvent.class, listener);
    }

    /**
     * Checks whether ISO 8601 week numbers are shown in the date selector.
     * 
     * @return true if week numbers are shown, false otherwise.
     */
    public boolean isShowISOWeekNumbers() {
        return showISOWeekNumbers;
    }

    /**
     * Sets the visibility of ISO 8601 week numbers in the date selector. ISO
     * 8601 defines that a week always starts with a Monday so the week numbers
     * are only shown if this is the case.
     * 
     * @param showWeekNumbers
     *            true if week numbers should be shown, false otherwise.
     */
    public void setShowISOWeekNumbers(boolean showWeekNumbers) {
        showISOWeekNumbers = showWeekNumbers;
        requestRepaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.AbstractField#isEmpty()
     */
    @Override
    protected boolean isEmpty() {
        /*
         * Logically isEmpty() should return false also in the case that the
         * entered value is invalid.
         */
        boolean empty = (dateString == null || dateString.equals(""));
        return empty;
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        /*
         * We also need to update the dateString if the value of the property
         * data source changes. This has to be done before super fires the value
         * change event in case someone checks isEmpty in a value change
         * listener.
         */
        Object value = getValue();
        if (value != null) {
            dateString = value.toString();
        }

        super.valueChange(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.AbstractField#isValid()
     */
    @Override
    public boolean isValid() {
        /*
         * For the DateField to be valid it has to be parsable also
         */
        boolean parsable = isEmpty() || (!isEmpty() && getValue() != null);
        return parsable && super.isValid();
    }
}
