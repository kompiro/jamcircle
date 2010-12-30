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

package com.vaadin.terminal.gwt.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.Application;

/**
 * This servlet connects a Vaadin Application to Web.
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 5.0
 */

@SuppressWarnings("serial")
public class ApplicationServlet extends AbstractApplicationServlet {

    // Private fields
    private Class<? extends Application> applicationClass;

    /**
     * Called by the servlet container to indicate to a servlet that the servlet
     * is being placed into service.
     * 
     * @param servletConfig
     *            the object containing the servlet's configuration and
     *            initialization parameters
     * @throws javax.servlet.ServletException
     *             if an exception has occurred that interferes with the
     *             servlet's normal operation.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void init(javax.servlet.ServletConfig servletConfig)
            throws javax.servlet.ServletException {
        super.init(servletConfig);

        // Loads the application class using the same class loader
        // as the servlet itself

        // Gets the application class name
        final String applicationClassName = servletConfig
                .getInitParameter("application");
        if (applicationClassName == null) {
            throw new ServletException(
                    "Application not specified in servlet parameters");
        }

        try {
            applicationClass = (Class<? extends Application>) getClassLoader()
                    .loadClass(applicationClassName);
        } catch (final ClassNotFoundException e) {
            throw new ServletException("Failed to load application class: "
                    + applicationClassName);
        }
    }

    @Override
    protected Application getNewApplication(HttpServletRequest request)
            throws ServletException {

        // Creates a new application instance
        try {
            final Application application = getApplicationClass().newInstance();

            return application;
        } catch (final IllegalAccessException e) {
            throw new ServletException("getNewApplication failed", e);
        } catch (final InstantiationException e) {
            throw new ServletException("getNewApplication failed", e);
        } catch (ClassNotFoundException e) {
            throw new ServletException("getNewApplication failed", e);
        }
    }

    @Override
    protected Class<? extends Application> getApplicationClass()
            throws ClassNotFoundException {
        return applicationClass;
    }
}
