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

import java.util.Date;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.DateTimeService;
import com.vaadin.terminal.gwt.client.LocaleNotLoadedException;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VConsole;
import com.vaadin.terminal.gwt.client.VTooltip;

public class VDateField extends FlowPanel implements Paintable, Field {

    public static final String CLASSNAME = "v-datefield";

    private String id;

    private ApplicationConnection client;

    protected boolean immediate;

    public static final int RESOLUTION_YEAR = 1;
    public static final int RESOLUTION_MONTH = 2;
    public static final int RESOLUTION_DAY = 4;
    public static final int RESOLUTION_HOUR = 8;
    public static final int RESOLUTION_MIN = 16;
    public static final int RESOLUTION_SEC = 32;
    public static final int RESOLUTION_MSEC = 64;

    public static final String WEEK_NUMBERS = "wn";

    static String resolutionToString(int res) {
        if (res > RESOLUTION_DAY) {
            return "full";
        }
        if (res == RESOLUTION_DAY) {
            return "day";
        }
        if (res == RESOLUTION_MONTH) {
            return "month";
        }
        return "year";
    }

    protected int currentResolution = RESOLUTION_YEAR;

    protected String currentLocale;

    protected boolean readonly;

    protected boolean enabled;

    /**
     * The date that is selected in the date field. Null if an invalid date is
     * specified.
     */
    private Date date = null;

    protected DateTimeService dts;

    private boolean showISOWeekNumbers = false;

    public VDateField() {
        setStyleName(CLASSNAME);
        dts = new DateTimeService();
        sinkEvents(VTooltip.TOOLTIP_EVENTS);
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (client != null) {
            client.handleTooltipEvent(event, this);
        }
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Ensure correct implementation and let layout manage caption
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        // Save details
        this.client = client;
        id = uidl.getId();
        immediate = uidl.getBooleanAttribute("immediate");

        readonly = uidl.getBooleanAttribute("readonly");
        enabled = !uidl.getBooleanAttribute("disabled");

        if (uidl.hasAttribute("locale")) {
            final String locale = uidl.getStringAttribute("locale");
            try {
                dts.setLocale(locale);
                currentLocale = locale;
            } catch (final LocaleNotLoadedException e) {
                currentLocale = dts.getLocale();
                VConsole.error("Tried to use an unloaded locale \"" + locale
                        + "\". Using default locale (" + currentLocale + ").");
                VConsole.error(e);
            }
        }

        // We show week numbers only if the week starts with Monday, as ISO 8601
        // specifies
        showISOWeekNumbers = uidl.getBooleanAttribute(WEEK_NUMBERS)
                && dts.getFirstDayOfWeek() == 1;

        int newResolution;
        if (uidl.hasVariable("msec")) {
            newResolution = RESOLUTION_MSEC;
        } else if (uidl.hasVariable("sec")) {
            newResolution = RESOLUTION_SEC;
        } else if (uidl.hasVariable("min")) {
            newResolution = RESOLUTION_MIN;
        } else if (uidl.hasVariable("hour")) {
            newResolution = RESOLUTION_HOUR;
        } else if (uidl.hasVariable("day")) {
            newResolution = RESOLUTION_DAY;
        } else if (uidl.hasVariable("month")) {
            newResolution = RESOLUTION_MONTH;
        } else {
            newResolution = RESOLUTION_YEAR;
        }

        currentResolution = newResolution;

        // Add stylename that indicates current resolution
        addStyleName(CLASSNAME + "-" + resolutionToString(currentResolution));

        final int year = uidl.getIntVariable("year");
        final int month = (currentResolution >= RESOLUTION_MONTH) ? uidl
                .getIntVariable("month") : -1;
        final int day = (currentResolution >= RESOLUTION_DAY) ? uidl
                .getIntVariable("day") : -1;
        final int hour = (currentResolution >= RESOLUTION_HOUR) ? uidl
                .getIntVariable("hour") : 0;
        final int min = (currentResolution >= RESOLUTION_MIN) ? uidl
                .getIntVariable("min") : 0;
        final int sec = (currentResolution >= RESOLUTION_SEC) ? uidl
                .getIntVariable("sec") : 0;
        final int msec = (currentResolution >= RESOLUTION_MSEC) ? uidl
                .getIntVariable("msec") : 0;

        // Construct new date for this datefield (only if not null)
        if (year > -1) {
            setCurrentDate(new Date((long) getTime(year, month, day, hour, min,
                    sec, msec)));
        } else {
            setCurrentDate(null);
        }
    }

    /*
     * We need this redundant native function because Java's Date object doesn't
     * have a setMilliseconds method.
     */
    private static native double getTime(int y, int m, int d, int h, int mi,
            int s, int ms)
    /*-{
       try {
       	var date = new Date(2000,1,1,1); // don't use current date here
       	if(y && y >= 0) date.setFullYear(y);
       	if(m && m >= 1) date.setMonth(m-1);
       	if(d && d >= 0) date.setDate(d);
       	if(h >= 0) date.setHours(h);
       	if(mi >= 0) date.setMinutes(mi);
       	if(s >= 0) date.setSeconds(s);
       	if(ms >= 0) date.setMilliseconds(ms);
       	return date.getTime();
       } catch (e) {
       	// TODO print some error message on the console
       	//console.log(e);
       	return (new Date()).getTime();
       }
    }-*/;

    public int getMilliseconds() {
        return DateTimeService.getMilliseconds(date);
    }

    public void setMilliseconds(int ms) {
        DateTimeService.setMilliseconds(date, ms);
    }

    public int getCurrentResolution() {
        return currentResolution;
    }

    public void setCurrentResolution(int currentResolution) {
        this.currentResolution = currentResolution;
    }

    public String getCurrentLocale() {
        return currentLocale;
    }

    public void setCurrentLocale(String currentLocale) {
        this.currentLocale = currentLocale;
    }

    public Date getCurrentDate() {
        return date;
    }

    public void setCurrentDate(Date date) {
        this.date = date;
    }

    public boolean isImmediate() {
        return immediate;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public DateTimeService getDateTimeService() {
        return dts;
    }

    public String getId() {
        return id;
    }

    public ApplicationConnection getClient() {
        return client;
    }

    /**
     * Returns whether ISO 8601 week numbers should be shown in the date
     * selector or not. ISO 8601 defines that a week always starts with a Monday
     * so the week numbers are only shown if this is the case.
     * 
     * @return true if week number should be shown, false otherwise
     */
    public boolean isShowISOWeekNumbers() {
        return showISOWeekNumbers;
    }

    /**
     * Returns a copy of the current date. Modifying the returned date will not
     * modify the value of this VDateField. Use {@link #setDate(Date)} to change
     * the current date.
     * 
     * @return A copy of the current date
     */
    protected Date getDate() {
        Date current = getCurrentDate();
        if (current == null) {
            return null;
        } else {
            return (Date) getCurrentDate().clone();
        }
    }

    /**
     * Sets the current date for this VDateField.
     * 
     * @param date
     *            The new date to use
     */
    protected void setDate(Date date) {
        this.date = date;
    }
}
