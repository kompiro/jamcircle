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
import java.util.Map;

import com.vaadin.ui.Window;

/**
 * {@code ParameterHandler} is implemented by classes capable of handling
 * external parameters.
 * 
 * <p>
 * What parameters are provided depend on what the {@link Terminal} provides and
 * if the application is deployed as a servlet or portlet. URL GET parameters
 * are typically provided to the {@link #handleParameters(Map)} method.
 * </p>
 * <p>
 * A {@code ParameterHandler} must be registered to a {@code Window} using
 * {@link Window#addParameterHandler(ParameterHandler)} to be called when
 * parameters are available.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 3.0
 */
public interface ParameterHandler extends Serializable {

    /**
     * Handles the given parameters. All parameters names are of type
     * {@link String} and the values are {@link String} arrays.
     * 
     * @param parameters
     *            an unmodifiable map which contains the parameter names and
     *            values
     * 
     */
    public void handleParameters(Map<String, String[]> parameters);

    /**
     * An ErrorEvent implementation for ParameterHandler.
     */
    public interface ErrorEvent extends Terminal.ErrorEvent {

        /**
         * Gets the ParameterHandler that caused the error.
         * 
         * @return the ParameterHandler that caused the error
         */
        public ParameterHandler getParameterHandler();

    }

}
