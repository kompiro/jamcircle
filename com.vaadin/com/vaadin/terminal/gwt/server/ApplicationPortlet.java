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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortalContext;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.util.PropsUtil;
import com.vaadin.Application;

/**
 * Portlet main class for Portlet 1.0 (JSR-168) portlets which consist of a
 * portlet and a servlet. For Portlet 2.0 (JSR-286, no servlet required), use
 * {@link ApplicationPortlet2} instead.
 */
@SuppressWarnings("serial")
public class ApplicationPortlet implements Portlet, Serializable {
    // portlet configuration parameters
    private static final String PORTLET_PARAMETER_APPLICATION = "application";
    private static final String PORTLET_PARAMETER_STYLE = "style";
    private static final String PORTLET_PARAMETER_WIDGETSET = "widgetset";

    // The application to show
    protected String app = null;
    // some applications might require forced height (and, more seldom, width)
    protected String style = null; // e.g "height:500px;"
    // force the portlet to use this widgetset - portlet level setting
    protected String portletWidgetset = null;

    public void destroy() {

    }

    public void init(PortletConfig config) throws PortletException {
        app = config.getInitParameter(PORTLET_PARAMETER_APPLICATION);
        if (app == null) {
            throw new PortletException(
                    "No porlet application url defined in portlet.xml. Define the '"
                            + PORTLET_PARAMETER_APPLICATION
                            + "' init parameter to be the servlet deployment path.");
        }
        style = config.getInitParameter(PORTLET_PARAMETER_STYLE);
        // enable forcing the selection of the widgetset in portlet
        // configuration for a single portlet (backwards compatibility)
        portletWidgetset = config.getInitParameter(PORTLET_PARAMETER_WIDGETSET);
    }

    public void processAction(ActionRequest request, ActionResponse response)
            throws PortletException, IOException {
        PortletApplicationContext.dispatchRequest(this, request, response);
    }

    public void render(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {

        // display the Vaadin application
        writeAjaxWindow(request, response);
    }

    protected void writeAjaxWindow(RenderRequest request,
            RenderResponse response) throws IOException {

        response.setContentType("text/html");
        if (app != null) {
            PortletSession sess = request.getPortletSession();
            PortletApplicationContext ctx = PortletApplicationContext
                    .getApplicationContext(sess);

            PortletRequestDispatcher dispatcher = sess.getPortletContext()
                    .getRequestDispatcher("/" + app);

            try {
                // portal-wide settings
                PortalContext portalCtx = request.getPortalContext();

                boolean isLifeRay = portalCtx.getPortalInfo().toLowerCase()
                        .contains("liferay");

                request.setAttribute(ApplicationServlet.REQUEST_FRAGMENT,
                        "true");

                // fixed base theme to use - all portal pages with Vaadin
                // applications will load this exactly once
                String portalTheme = getPortalProperty(
                        Constants.PORTAL_PARAMETER_VAADIN_THEME, portalCtx);

                String portalWidgetset = getPortalProperty(
                        Constants.PORTAL_PARAMETER_VAADIN_WIDGETSET, portalCtx);

                // location of the widgetset(s) and default theme (to which
                // /VAADIN/widgetsets/...
                // is appended)
                String portalResourcePath = getPortalProperty(
                        Constants.PORTAL_PARAMETER_VAADIN_RESOURCE_PATH,
                        portalCtx);

                if (portalResourcePath != null) {
                    // if portalResourcePath is defined, set it as a request
                    // parameter which will override the default location in
                    // servlet
                    request.setAttribute(
                            ApplicationServlet.REQUEST_VAADIN_STATIC_FILE_PATH,
                            portalResourcePath);
                }

                // - if the user has specified a widgetset for this portlet, use
                // it from the portlet (not fully supported)
                // - otherwise, if specified, use the portal-wide widgetset
                // and widgetset path settings (recommended)
                // - finally, default to use the default widgetset if nothing
                // else is found
                if (portletWidgetset != null) {
                    request.setAttribute(ApplicationServlet.REQUEST_WIDGETSET,
                            portletWidgetset);
                }
                if (portalWidgetset != null) {
                    request.setAttribute(
                            ApplicationServlet.REQUEST_SHARED_WIDGETSET,
                            portalWidgetset);
                }

                if (style != null) {
                    request.setAttribute(ApplicationServlet.REQUEST_APPSTYLE,
                            style);
                }

                // portalTheme is only used if the shared portal resource
                // directory is defined
                if (portalTheme != null && portalResourcePath != null) {
                    request.setAttribute(
                            ApplicationServlet.REQUEST_DEFAULT_THEME,
                            portalTheme);

                    String defaultThemeUri = null;
                    defaultThemeUri = portalResourcePath + "/"
                            + AbstractApplicationServlet.THEME_DIRECTORY_PATH
                            + portalTheme;
                    /*
                     * Make sure portal default Vaadin theme is included in DOM.
                     * Vaadin portlet themes do not "inherit" base theme, so we
                     * need to force loading of the common base theme.
                     */
                    OutputStream out = response.getPortletOutputStream();

                    // Using portal-wide theme
                    String loadDefaultTheme = ("<script type=\"text/javascript\">\n"
                            + "if(!vaadin) { var vaadin = {} } \n"
                            + "if(!vaadin.themesLoaded) { vaadin.themesLoaded = {} } \n"
                            + "if(!vaadin.themesLoaded['"
                            + portalTheme
                            + "']) {\n"
                            + "var stylesheet = document.createElement('link');\n"
                            + "stylesheet.setAttribute('rel', 'stylesheet');\n"
                            + "stylesheet.setAttribute('type', 'text/css');\n"
                            + "stylesheet.setAttribute('href', '"
                            + defaultThemeUri
                            + "/styles.css');\n"
                            + "document.getElementsByTagName('head')[0].appendChild(stylesheet);\n"
                            + "vaadin.themesLoaded['"
                            + portalTheme
                            + "'] = true;\n}\n" + "</script>\n");
                    out.write(loadDefaultTheme.getBytes());
                }

                dispatcher.include(request, response);

                if (isLifeRay) {
                    /*
                     * Temporary support to heartbeat Liferay session when using
                     * Vaadin based portlet. We hit an extra xhr to liferay
                     * servlet to extend the session lifetime after each Vaadin
                     * request. This hack can be removed when supporting portlet
                     * 2.0 and resourceRequests.
                     * 
                     * TODO make this configurable, this is not necessary with
                     * some custom session configurations.
                     */
                    OutputStream out = response.getPortletOutputStream();

                    String lifeRaySessionHearbeatHack = ("<script type=\"text/javascript\">"
                            + "if(!vaadin.postRequestHooks) {"
                            + "    vaadin.postRequestHooks = {};"
                            + "}"
                            + "vaadin.postRequestHooks.liferaySessionHeartBeat = function() {"
                            + "    if (Liferay && Liferay.Session && Liferay.Session.setCookie) {"
                            + "        Liferay.Session.setCookie();"
                            + "    }"
                            + "};" + "</script>");
                    out.write(lifeRaySessionHearbeatHack.getBytes());
                }

            } catch (PortletException e) {
                PrintWriter out = response.getWriter();
                out.print("<h1>Servlet include failed!</h1>");
                out.print("<div>" + e + "</div>");
                ctx.setPortletApplication(this, null);
                return;
            }

            Application app = (Application) request
                    .getAttribute(Application.class.getName());
            ctx.setPortletApplication(this, app);
            ctx.firePortletRenderRequest(this, request, response);

        }
    }

    private String getPortalProperty(String name, PortalContext context) {
        boolean isLifeRay = context.getPortalInfo().toLowerCase()
                .contains("liferay");

        // TODO test on non-LifeRay platforms

        String value;
        if (isLifeRay) {
            value = getLifeRayPortalProperty(name);
        } else {
            value = context.getProperty(name);
        }

        return value;
    }

    private String getLifeRayPortalProperty(String name) {
        String value;
        try {
            value = PropsUtil.get(name);
        } catch (Exception e) {
            value = null;
        }
        return value;
    }
}
