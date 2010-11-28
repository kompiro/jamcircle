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

package com.vaadin.terminal;

import java.io.Serializable;
import java.net.URL;

/**
 * A URIHandler is used for handling URI:s requested by the user and can
 * optionally provide a {@link DownloadStream}. If a {@link DownloadStream} is
 * returned by {@link #handleURI(URL, String)}, the stream is sent to the
 * client.
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 3.0
 */
public interface URIHandler extends Serializable {

    /**
     * Handles a given URI. If the URI handler to emit a downloadable stream it
     * should return a {@code DownloadStream} object.
     * 
     * @param context
     *            the base URL
     * @param relativeUri
     *            a URI relative to {@code context}
     * @return A downloadable stream or null if no stream is provided
     */
    public DownloadStream handleURI(URL context, String relativeUri);

    /**
     * An {@code ErrorEvent} implementation for URIHandler.
     */
    public interface ErrorEvent extends Terminal.ErrorEvent {

        /**
         * Gets the URIHandler that caused this error.
         * 
         * @return the URIHandler that caused the error
         */
        public URIHandler getURIHandler();

    }
}
