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
/*
 * Copyright 2008 Google Inc.
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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.impl.HistoryImpl;

/**
 * A slightly modified version of GWT's HistoryImplIE6 to bypass bug #2931. Also
 * combined with HistoryImplFrame.
 * 
 * This class should be removed if GWT issue 3890 gets resolved. (Also remember
 * to removed deferred binding rule from .gwt.xml file).
 */
public class HistoryImplIEVaadin extends HistoryImpl {

    private static native Element findHistoryFrame()
    /*-{
        return $doc.getElementById('__gwt_historyFrame');
    }-*/;

    private static native Element getTokenElement(Element historyFrame)
    /*-{
       // Initialize the history iframe.  If '__gwt_historyToken' already exists, then
       // we're probably backing into the app, so _don't_ set the iframe's location.
       if (historyFrame.contentWindow) {
           var doc = historyFrame.contentWindow.document;
           return doc.getElementById('__gwt_historyToken');
       }
    }-*/;

    protected Element historyFrame;

    @Override
    protected final void nativeUpdate(String historyToken) {
        /*
         * Must update the location hash since it isn't already correct.
         */
        updateHash(historyToken);
        navigateFrame(historyToken);
    }

    @Override
    protected final void nativeUpdateOnEvent(String historyToken) {
        updateHash(historyToken);
    }

    /**
     * Sanitizes an untrusted string to be used in an HTML context. NOTE: This
     * method of escaping strings should only be used on Internet Explorer.
     * 
     * @param maybeHtml
     *            untrusted string that may contain html
     * @return sanitized string
     */
    @SuppressWarnings("unused")
    private static String escapeHtml(String maybeHtml) {
        final Element div = DOM.createDiv();
        DOM.setInnerText(div, maybeHtml);
        return DOM.getInnerHTML(div);
    }

    /**
     * For IE6, reading from $wnd.location.hash drops part of the fragment if
     * the fragment contains a '?'. To avoid this bug, we use location.href
     * instead.
     */
    @SuppressWarnings("unused")
    private static native String getLocationHash()
    /*-{
       var href = $wnd.location.href;
       var hashLoc = href.lastIndexOf("#");
       return (hashLoc > 0) ? href.substring(hashLoc) : "";
    }-*/;

    @Override
    public boolean init() {
        historyFrame = findHistoryFrame();
        if (historyFrame == null) {
            return false;
        }

        initHistoryToken();

        // Initialize the history iframe. If a token element already exists,
        // then
        // we're probably backing into the app, so _don't_ create a new item.
        Element tokenElement = getTokenElement(historyFrame);
        if (tokenElement != null) {
            setToken(getTokenElementContent(tokenElement));
        } else {
            navigateFrame(getToken());
        }

        injectGlobalHandler();

        initUrlCheckTimer();
        return true;
    }

    protected native String getTokenElementContent(Element tokenElement)
    /*-{
        return tokenElement.innerText;
    }-*/;

    protected native void initHistoryToken()
    /*-{
       // Assume an empty token.
       var token = '';
       // Get the initial token from the url's hash component.
       var hash = @com.vaadin.terminal.gwt.client.HistoryImplIEVaadin::getLocationHash()();
       if (hash.length > 0) {
         try {
           token = this.@com.google.gwt.user.client.impl.HistoryImpl::decodeFragment(Ljava/lang/String;)(hash.substring(1));
         } catch (e) {
           // Clear the bad hash (this can't have been a valid token).
           $wnd.location.hash = '';
         }
       }
       @com.google.gwt.user.client.impl.HistoryImpl::setToken(Ljava/lang/String;)(token);
     }-*/;

    protected native void injectGlobalHandler()
    /*-{
       var historyImplRef = this;

       $wnd.__gwt_onHistoryLoad = function(token) {
         historyImplRef.@com.google.gwt.user.client.impl.HistoryImpl::newItemOnEvent(Ljava/lang/String;)(token);
       };
     }-*/;

    protected native void navigateFrame(String token)
    /*-{
       var escaped = @com.vaadin.terminal.gwt.client.HistoryImplIEVaadin::escapeHtml(Ljava/lang/String;)(token);
       var doc = this.@com.vaadin.terminal.gwt.client.HistoryImplIEVaadin::historyFrame.contentWindow.document;
       doc.open();
       doc.write('<html><body onload="if(parent.__gwt_onHistoryLoad)parent.__gwt_onHistoryLoad(__gwt_historyToken.innerText)"><div id="__gwt_historyToken">' + escaped + '</div></body></html>');
       doc.close();
     }-*/;

    protected native void updateHash(String token)
    /*-{
       $wnd.location.hash = this.@com.google.gwt.user.client.impl.HistoryImpl::encodeFragment(Ljava/lang/String;)(token);
     }-*/;

    private native void initUrlCheckTimer()
    /*-{
       // This is the URL check timer.  It detects when an unexpected change
       // occurs in the document's URL (e.g. when the user enters one manually
       // or selects a 'favorite', but only the #hash part changes).  When this
       // occurs, we _must_ reload the page.  This is because IE has a really
       // nasty bug that totally mangles its history stack and causes the location
       // bar in the UI to stop working under these circumstances.
       var historyImplRef = this;
       var urlChecker = function() {
         $wnd.setTimeout(urlChecker, 250);
         var hash = @com.vaadin.terminal.gwt.client.HistoryImplIEVaadin::getLocationHash()();
         if (hash.length > 0) {
           var token = '';
           try {
             token = historyImplRef.@com.google.gwt.user.client.impl.HistoryImpl::decodeFragment(Ljava/lang/String;)(hash.substring(1));
           } catch (e) {
             // If there's a bad hash, always reload. This could only happen if
             // if someone entered or linked to a bad url.
             $wnd.location.reload();
           }

           var historyToken = @com.google.gwt.user.client.impl.HistoryImpl::getToken()();
           if (token != historyToken) {
             $wnd.location.reload();
           }
         }
       };
       urlChecker();
     }-*/;

}
