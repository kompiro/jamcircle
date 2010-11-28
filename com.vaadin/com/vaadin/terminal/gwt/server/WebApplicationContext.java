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

import java.io.File;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingListener;

import com.vaadin.Application;

/**
 * Web application context for Vaadin applications.
 * 
 * This is automatically added as a {@link HttpSessionBindingListener} when
 * added to a {@link HttpSession}.
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 3.1
 */
@SuppressWarnings("serial")
public class WebApplicationContext extends AbstractWebApplicationContext {

    protected transient HttpSession session;

    /**
     * Creates a new Web Application Context.
     * 
     */
    WebApplicationContext() {

    }

    /**
     * Gets the application context base directory.
     * 
     * @see com.vaadin.service.ApplicationContext#getBaseDirectory()
     */
    public File getBaseDirectory() {
        final String realPath = ApplicationServlet.getResourcePath(
                session.getServletContext(), "/");
        if (realPath == null) {
            return null;
        }
        return new File(realPath);
    }

    /**
     * Gets the http-session application is running in.
     * 
     * @return HttpSession this application context resides in.
     */
    public HttpSession getHttpSession() {
        return session;
    }

    /**
     * Gets the application context for an HttpSession.
     * 
     * @param session
     *            the HTTP session.
     * @return the application context for HttpSession.
     */
    static public WebApplicationContext getApplicationContext(
            HttpSession session) {
        WebApplicationContext cx = (WebApplicationContext) session
                .getAttribute(WebApplicationContext.class.getName());
        if (cx == null) {
            cx = new WebApplicationContext();
            session.setAttribute(WebApplicationContext.class.getName(), cx);
        }
        if (cx.session == null) {
            cx.session = session;
        }
        return cx;
    }

    protected void addApplication(Application application) {
        applications.add(application);
    }

    /**
     * Gets communication manager for an application.
     * 
     * If this application has not been running before, a new manager is
     * created.
     * 
     * @param application
     * @return CommunicationManager
     */
    protected CommunicationManager getApplicationManager(
            Application application, AbstractApplicationServlet servlet) {
        CommunicationManager mgr = (CommunicationManager) applicationToAjaxAppMgrMap
                .get(application);

        if (mgr == null) {
            // Creates new manager
            mgr = new CommunicationManager(application, servlet);
            applicationToAjaxAppMgrMap.put(application, mgr);
        }
        return mgr;
    }

}
