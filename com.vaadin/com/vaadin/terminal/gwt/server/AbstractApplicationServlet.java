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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.vaadin.Application;
import com.vaadin.Application.SystemMessages;
import com.vaadin.external.org.apache.commons.fileupload.servlet.ServletFileUpload;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.terminal.Terminal;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.URIHandler;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.ui.Window;

/**
 * Abstract implementation of the ApplicationServlet which handles all
 * communication between the client and the server.
 * 
 * It is possible to extend this class to provide own functionality but in most
 * cases this is unnecessary.
 * 
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.4.8
 * @since 6.0
 */

@SuppressWarnings("serial")
public abstract class AbstractApplicationServlet extends HttpServlet implements
        Constants {

    // TODO Move some (all?) of the constants to a separate interface (shared
    // with portlet)

    /**
     * The version number of this release. For example "6.2.0". Always in the
     * format "major.minor.revision[.build]". The build part is optional. All of
     * major, minor, revision must be integers.
     */
    public static final String VERSION;
    /**
     * Major version number. For example 6 in 6.2.0.
     */
    public static final int VERSION_MAJOR;

    /**
     * Minor version number. For example 2 in 6.2.0.
     */
    public static final int VERSION_MINOR;

    /**
     * Version revision number. For example 0 in 6.2.0.
     */
    public static final int VERSION_REVISION;

    /**
     * Build identifier. For example "nightly-20091123-c9963" in
     * 6.2.0.nightly-20091123-c9963.
     */
    public static final String VERSION_BUILD;

    /* Initialize version numbers from string replaced by build-script. */
    static {
        if ("6.4.8".equals("@" + "VERSION" + "@")) {
            VERSION = "9.9.9.INTERNAL-DEBUG-BUILD";
        } else {
            VERSION = "6.4.8";
        }
        final String[] digits = VERSION.split("\\.", 4);
        VERSION_MAJOR = Integer.parseInt(digits[0]);
        VERSION_MINOR = Integer.parseInt(digits[1]);
        VERSION_REVISION = Integer.parseInt(digits[2]);
        if (digits.length == 4) {
            VERSION_BUILD = digits[3];
        } else {
            VERSION_BUILD = "";
        }
    }

    /**
     * If the attribute is present in the request, a html fragment will be
     * written instead of a whole page.
     * 
     * It is set to "true" by the {@link ApplicationPortlet} (Portlet 1.0) and
     * read by {@link AbstractApplicationServlet}.
     */
    public static final String REQUEST_FRAGMENT = ApplicationServlet.class
            .getName() + ".fragment";
    /**
     * This request attribute forces widgetsets to be loaded from under the
     * specified base path; e.g shared widgetset for all portlets in a portal.
     * 
     * It is set by the {@link ApplicationPortlet} (Portlet 1.0) based on
     * {@link Constants.PORTAL_PARAMETER_VAADIN_RESOURCE_PATH} and read by
     * {@link AbstractApplicationServlet}.
     */
    public static final String REQUEST_VAADIN_STATIC_FILE_PATH = ApplicationServlet.class
            .getName() + ".widgetsetPath";
    /**
     * This request attribute forces widgetset used; e.g for portlets that can
     * not have different widgetsets.
     * 
     * It is set by the {@link ApplicationPortlet} (Portlet 1.0) based on
     * {@link ApplicationPortlet.PORTLET_PARAMETER_WIDGETSET} and read by
     * {@link AbstractApplicationServlet}.
     */
    public static final String REQUEST_WIDGETSET = ApplicationServlet.class
            .getName() + ".widgetset";
    /**
     * This request attribute indicates the shared widgetset (e.g. portal-wide
     * default widgetset).
     * 
     * It is set by the {@link ApplicationPortlet} (Portlet 1.0) based on
     * {@link Constants.PORTAL_PARAMETER_VAADIN_WIDGETSET} and read by
     * {@link AbstractApplicationServlet}.
     */
    public static final String REQUEST_SHARED_WIDGETSET = ApplicationServlet.class
            .getName() + ".sharedWidgetset";
    /**
     * If set, do not load the default theme but assume that loading it is
     * handled e.g. by ApplicationPortlet.
     * 
     * It is set by the {@link ApplicationPortlet} (Portlet 1.0) based on
     * {@link Constants.PORTAL_PARAMETER_VAADIN_THEME} and read by
     * {@link AbstractApplicationServlet}.
     */
    public static final String REQUEST_DEFAULT_THEME = ApplicationServlet.class
            .getName() + ".defaultThemeUri";
    /**
     * This request attribute is used to add styles to the main element. E.g
     * "height:500px" generates a style="height:500px" to the main element,
     * useful from some embedding situations (e.g portlet include.)
     * 
     * It is typically set by the {@link ApplicationPortlet} (Portlet 1.0) based
     * on {@link ApplicationPortlet.PORTLET_PARAMETER_STYLE} and read by
     * {@link AbstractApplicationServlet}.
     */
    public static final String REQUEST_APPSTYLE = ApplicationServlet.class
            .getName() + ".style";

    private Properties applicationProperties;

    private boolean productionMode = false;

    private final String resourcePath = null;

    private int resourceCacheTime = 3600;

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

        // Stores the application parameters into Properties object
        applicationProperties = new Properties();
        for (final Enumeration e = servletConfig.getInitParameterNames(); e
                .hasMoreElements();) {
            final String name = (String) e.nextElement();
            applicationProperties.setProperty(name,
                    servletConfig.getInitParameter(name));
        }

        // Overrides with server.xml parameters
        final ServletContext context = servletConfig.getServletContext();
        for (final Enumeration e = context.getInitParameterNames(); e
                .hasMoreElements();) {
            final String name = (String) e.nextElement();
            applicationProperties.setProperty(name,
                    context.getInitParameter(name));
        }
        checkProductionMode();
        checkCrossSiteProtection();
        checkResourceCacheTime();
    }

    private void checkCrossSiteProtection() {
        if (getApplicationOrSystemProperty(
                SERVLET_PARAMETER_DISABLE_XSRF_PROTECTION, "false").equals(
                "true")) {
            /*
             * Print an information/warning message about running with xsrf
             * protection disabled
             */
            System.err.println(WARNING_XSRF_PROTECTION_DISABLED);
        }
    }

    private void checkProductionMode() {
        // Check if the application is in production mode.
        // We are in production mode if Debug=false or productionMode=true
        if (getApplicationOrSystemProperty(SERVLET_PARAMETER_DEBUG, "true")
                .equals("false")) {
            // "Debug=true" is the old way and should no longer be used
            productionMode = true;
        } else if (getApplicationOrSystemProperty(
                SERVLET_PARAMETER_PRODUCTION_MODE, "false").equals("true")) {
            // "productionMode=true" is the real way to do it
            productionMode = true;
        }

        if (!productionMode) {
            /* Print an information/warning message about running in debug mode */
            System.err.println(NOT_PRODUCTION_MODE_INFO);
        }

    }

    private void checkResourceCacheTime() {
        // Check if the browser caching time has been set in web.xml
        try {
            String rct = getApplicationOrSystemProperty(
                    SERVLET_PARAMETER_RESOURCE_CACHE_TIME, "3600");
            resourceCacheTime = Integer.parseInt(rct);
        } catch (NumberFormatException nfe) {
            // Default is 1h
            resourceCacheTime = 3600;
            System.err.println(WARNING_RESOURCE_CACHING_TIME_NOT_NUMERIC);
        }
    }

    /**
     * Gets an application property value.
     * 
     * @param parameterName
     *            the Name or the parameter.
     * @return String value or null if not found
     */
    protected String getApplicationProperty(String parameterName) {

        String val = applicationProperties.getProperty(parameterName);
        if (val != null) {
            return val;
        }

        // Try lower case application properties for backward compatibility with
        // 3.0.2 and earlier
        val = applicationProperties.getProperty(parameterName.toLowerCase());

        return val;
    }

    /**
     * Gets an system property value.
     * 
     * @param parameterName
     *            the Name or the parameter.
     * @return String value or null if not found
     */
    protected String getSystemProperty(String parameterName) {
        String val = null;

        String pkgName;
        final Package pkg = getClass().getPackage();
        if (pkg != null) {
            pkgName = pkg.getName();
        } else {
            final String className = getClass().getName();
            pkgName = new String(className.toCharArray(), 0,
                    className.lastIndexOf('.'));
        }
        val = System.getProperty(pkgName + "." + parameterName);
        if (val != null) {
            return val;
        }

        // Try lowercased system properties
        val = System.getProperty(pkgName + "." + parameterName.toLowerCase());
        return val;
    }

    /**
     * Gets an application or system property value.
     * 
     * @param parameterName
     *            the Name or the parameter.
     * @param defaultValue
     *            the Default to be used.
     * @return String value or default if not found
     */
    private String getApplicationOrSystemProperty(String parameterName,
            String defaultValue) {

        String val = null;

        // Try application properties
        val = getApplicationProperty(parameterName);
        if (val != null) {
            return val;
        }

        // Try system properties
        val = getSystemProperty(parameterName);
        if (val != null) {
            return val;
        }

        return defaultValue;
    }

    /**
     * Returns true if the servlet is running in production mode. Production
     * mode disables all debug facilities.
     * 
     * @return true if in production mode, false if in debug mode
     */
    public boolean isProductionMode() {
        return productionMode;
    }

    /**
     * Returns the amount of milliseconds the browser should cache a file.
     * Default is 1 hour (3600 ms).
     * 
     * @return The amount of milliseconds files are cached in the browser
     */
    public int getResourceCacheTime() {
        return resourceCacheTime;
    }

    /**
     * Receives standard HTTP requests from the public service method and
     * dispatches them.
     * 
     * @param request
     *            the object that contains the request the client made of the
     *            servlet.
     * @param response
     *            the object that contains the response the servlet returns to
     *            the client.
     * @throws ServletException
     *             if an input or output error occurs while the servlet is
     *             handling the TRACE request.
     * @throws IOException
     *             if the request for the TRACE cannot be handled.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        RequestType requestType = getRequestType(request);
        if (!ensureCookiesEnabled(requestType, request, response)) {
            return;
        }

        if (requestType == RequestType.STATIC_FILE) {
            serveStaticResources(request, response);
            return;
        }

        Application application = null;
        boolean transactionStarted = false;
        boolean requestStarted = false;

        try {
            // If a duplicate "close application" URL is received for an
            // application that is not open, redirect to the application's main
            // page.
            // This is needed as e.g. Spring Security remembers the last
            // URL from the application, which is the logout URL, and repeats
            // it.
            // We can tell apart a real onunload request from a repeated one
            // based on the real one having content (at least the UIDL security
            // key).
            if (requestType == RequestType.UIDL
                    && request.getParameterMap().containsKey(
                            ApplicationConnection.PARAM_UNLOADBURST)
                    && request.getContentLength() < 1
                    && getExistingApplication(request, false) == null) {
                redirectToApplication(request, response);
                return;
            }

            // Find out which application this request is related to
            application = findApplicationInstance(request, requestType);
            if (application == null) {
                return;
            }

            /*
             * Get or create a WebApplicationContext and an ApplicationManager
             * for the session
             */
            WebApplicationContext webApplicationContext = WebApplicationContext
                    .getApplicationContext(request.getSession());
            CommunicationManager applicationManager = webApplicationContext
                    .getApplicationManager(application, this);

            /* Update browser information from the request */
            updateBrowserProperties(webApplicationContext.getBrowser(), request);

            /*
             * Call application requestStart before Application.init() is called
             * (bypasses the limitation in TransactionListener)
             */
            if (application instanceof HttpServletRequestListener) {
                ((HttpServletRequestListener) application).onRequestStart(
                        request, response);
                requestStarted = true;
            }

            // Start the newly created application
            startApplication(request, application, webApplicationContext);

            /*
             * Transaction starts. Call transaction listeners. Transaction end
             * is called in the finally block below.
             */
            webApplicationContext.startTransaction(application, request);
            transactionStarted = true;

            /* Handle the request */
            if (requestType == RequestType.FILE_UPLOAD) {
                applicationManager.handleFileUpload(request, response);
                return;
            } else if (requestType == RequestType.UIDL) {
                // Handles AJAX UIDL requests
                Window window = applicationManager.getApplicationWindow(
                        request, this, application, null);
                applicationManager.handleUidlRequest(request, response, this,
                        window);
                return;
            }

            // Removes application if it has stopped (mayby by thread or
            // transactionlistener)
            if (!application.isRunning()) {
                endApplication(request, response, application);
                return;
            }

            // Finds the window within the application
            Window window = getApplicationWindow(request, applicationManager,
                    application);
            if (window == null) {
                throw new ServletException(ERROR_NO_WINDOW_FOUND);
            }

            // Sets terminal type for the window, if not already set
            if (window.getTerminal() == null) {
                window.setTerminal(webApplicationContext.getBrowser());
            }

            // Handle parameters
            final Map parameters = request.getParameterMap();
            if (window != null && parameters != null) {
                window.handleParameters(parameters);
            }

            /*
             * Call the URI handlers and if this turns out to be a download
             * request, send the file to the client
             */
            if (handleURI(applicationManager, window, request, response)) {
                return;
            }

            // Send initial AJAX page that kickstarts a Vaadin application
            writeAjaxPage(request, response, window, application);

        } catch (final SessionExpiredException e) {
            // Session has expired, notify user
            handleServiceSessionExpired(request, response);
        } catch (final GeneralSecurityException e) {
            handleServiceSecurityException(request, response);
        } catch (final Throwable e) {
            handleServiceException(request, response, application, e);
        } finally {
            // Notifies transaction end
            try {
                if (transactionStarted) {
                    ((WebApplicationContext) application.getContext())
                            .endTransaction(application, request);

                }

            } finally {
                if (requestStarted) {
                    ((HttpServletRequestListener) application).onRequestEnd(
                            request, response);
                }

            }

        }
    }

    /**
     * Check that cookie support is enabled in the browser. Only checks UIDL
     * requests.
     * 
     * @param requestType
     *            Type of the request as returned by
     *            {@link #getRequestType(HttpServletRequest)}
     * @param request
     *            The request from the browser
     * @param response
     *            The response to which an error can be written
     * @return false if cookies are disabled, true otherwise
     * @throws IOException
     */
    private boolean ensureCookiesEnabled(RequestType requestType,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (requestType == RequestType.UIDL && !isRepaintAll(request)) {
            // In all other but the first UIDL request a cookie should be
            // returned by the browser.
            // This can be removed if cookieless mode (#3228) is supported
            if (request.getRequestedSessionId() == null) {
                // User has cookies disabled
                criticalNotification(request, response, getSystemMessages()
                        .getCookiesDisabledCaption(), getSystemMessages()
                        .getCookiesDisabledMessage(), null, getSystemMessages()
                        .getCookiesDisabledURL());
                return false;
            }
        }
        return true;
    }

    private void updateBrowserProperties(WebBrowser browser,
            HttpServletRequest request) {
        browser.updateBrowserProperties(request.getLocale(),
                request.getRemoteAddr(), request.isSecure(),
                request.getHeader("user-agent"), request.getParameter("sw"),
                request.getParameter("sh"));
    }

    protected ClassLoader getClassLoader() throws ServletException {
        // Gets custom class loader
        final String classLoaderName = getApplicationOrSystemProperty(
                "ClassLoader", null);
        ClassLoader classLoader;
        if (classLoaderName == null) {
            classLoader = getClass().getClassLoader();
        } else {
            try {
                final Class<?> classLoaderClass = getClass().getClassLoader()
                        .loadClass(classLoaderName);
                final Constructor<?> c = classLoaderClass
                        .getConstructor(new Class[] { ClassLoader.class });
                classLoader = (ClassLoader) c
                        .newInstance(new Object[] { getClass().getClassLoader() });
            } catch (final Exception e) {
                throw new ServletException(
                        "Could not find specified class loader: "
                                + classLoaderName, e);
            }
        }
        return classLoader;
    }

    /**
     * Send a notification to client's application. Used to notify client of
     * critical errors, session expiration and more. Server has no knowledge of
     * what application client refers to.
     * 
     * @param request
     *            the HTTP request instance.
     * @param response
     *            the HTTP response to write to.
     * @param caption
     *            the notification caption
     * @param message
     *            to notification body
     * @param details
     *            a detail message to show in addition to the message. Currently
     *            shown directly below the message but could be hidden behind a
     *            details drop down in the future. Mainly used to give
     *            additional information not necessarily useful to the end user.
     * @param url
     *            url to load when the message is dismissed. Null will reload
     *            the current page.
     * @throws IOException
     *             if the writing failed due to input/output error.
     */
    protected final void criticalNotification(HttpServletRequest request,
            HttpServletResponse response, String caption, String message,
            String details, String url) throws IOException {

        if (isUIDLRequest(request)) {

            if (caption != null) {
                caption = "\"" + JsonPaintTarget.escapeJSON(caption) + "\"";
            }
            if (details != null) {
                if (message == null) {
                    message = details;
                } else {
                    message += "<br/><br/>" + details;
                }
            }

            if (message != null) {
                message = "\"" + JsonPaintTarget.escapeJSON(message) + "\"";
            }
            if (url != null) {
                url = "\"" + JsonPaintTarget.escapeJSON(url) + "\"";
            }

            String output = "for(;;);[{\"changes\":[], \"meta\" : {"
                    + "\"appError\": {" + "\"caption\":" + caption + ","
                    + "\"message\" : " + message + "," + "\"url\" : " + url
                    + "}}, \"resources\": {}, \"locales\":[]}]";
            writeResponse(response, "application/json; charset=UTF-8", output);
        } else {
            // Create an HTML reponse with the error
            String output = "";

            if (url != null) {
                output += "<a href=\"" + url + "\">";
            }
            if (caption != null) {
                output += "<b>" + caption + "</b><br/>";
            }
            if (message != null) {
                output += message;
                output += "<br/><br/>";
            }

            if (details != null) {
                output += details;
                output += "<br/><br/>";
            }
            if (url != null) {
                output += "</a>";
            }
            writeResponse(response, "text/html; charset=UTF-8", output);

        }

    }

    /**
     * Writes the response in {@code output} using the contentType given in
     * {@code contentType} to the provided {@link HttpServletResponse}
     * 
     * @param response
     * @param contentType
     * @param output
     *            Output to write (UTF-8 encoded)
     * @throws IOException
     */
    private void writeResponse(HttpServletResponse response,
            String contentType, String output) throws IOException {
        response.setContentType(contentType);
        final ServletOutputStream out = response.getOutputStream();
        // Set the response type
        final PrintWriter outWriter = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(out, "UTF-8")));
        outWriter.print(output);
        outWriter.flush();
        outWriter.close();
        out.flush();

    }

    /**
     * Returns the application instance to be used for the request. If an
     * existing instance is not found a new one is created or null is returned
     * to indicate that the application is not available.
     * 
     * @param request
     * @param requestType
     * @return
     * @throws MalformedURLException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ServletException
     * @throws SessionExpiredException
     */
    private Application findApplicationInstance(HttpServletRequest request,
            RequestType requestType) throws MalformedURLException,
            ServletException, SessionExpiredException {

        boolean requestCanCreateApplication = requestCanCreateApplication(
                request, requestType);

        /* Find an existing application for this request. */
        Application application = getExistingApplication(request,
                requestCanCreateApplication);

        if (application != null) {
            /*
             * There is an existing application. We can use this as long as the
             * user not specifically requested to close or restart it.
             */

            final boolean restartApplication = (request
                    .getParameter(URL_PARAMETER_RESTART_APPLICATION) != null);
            final boolean closeApplication = (request
                    .getParameter(URL_PARAMETER_CLOSE_APPLICATION) != null);

            if (restartApplication) {
                closeApplication(application, request.getSession(false));
                return createApplication(request);
            } else if (closeApplication) {
                closeApplication(application, request.getSession(false));
                return null;
            } else {
                return application;
            }
        }

        // No existing application was found

        if (requestCanCreateApplication) {
            /*
             * If the request is such that it should create a new application if
             * one as not found, we do that.
             */
            return createApplication(request);
        } else {
            /*
             * The application was not found and a new one should not be
             * created. Assume the session has expired.
             */
            throw new SessionExpiredException();
        }

    }

    /**
     * Check if the request should create an application if an existing
     * application is not found.
     * 
     * @param request
     * @param requestType
     * @return true if an application should be created, false otherwise
     */
    boolean requestCanCreateApplication(HttpServletRequest request,
            RequestType requestType) {
        if (requestType == RequestType.UIDL && isRepaintAll(request)) {
            /*
             * UIDL request contains valid repaintAll=1 event, the user probably
             * wants to initiate a new application through a custom index.html
             * without using writeAjaxPage.
             */
            return true;

        } else if (requestType == RequestType.OTHER) {
            /*
             * I.e URIs that are not application resources or static (theme)
             * files.
             */
            return true;

        }

        return false;
    }

    /**
     * Gets resource path using different implementations. Required to
     * supporting different servlet container implementations (application
     * servers).
     * 
     * @param servletContext
     * @param path
     *            the resource path.
     * @return the resource path.
     */
    protected static String getResourcePath(ServletContext servletContext,
            String path) {
        String resultPath = null;
        resultPath = servletContext.getRealPath(path);
        if (resultPath != null) {
            return resultPath;
        } else {
            try {
                final URL url = servletContext.getResource(path);
                resultPath = url.getFile();
            } catch (final Exception e) {
                // FIXME: Handle exception
                e.printStackTrace();
            }
        }
        return resultPath;
    }

    /**
     * Handles the requested URI. An application can add handlers to do special
     * processing, when a certain URI is requested. The handlers are invoked
     * before any windows URIs are processed and if a DownloadStream is returned
     * it is sent to the client.
     * 
     * @param stream
     *            the download stream.
     * 
     * @param request
     *            the HTTP request instance.
     * @param response
     *            the HTTP response to write to.
     * @throws IOException
     * 
     * @see com.vaadin.terminal.URIHandler
     */
    @SuppressWarnings("unchecked")
    private void handleDownload(DownloadStream stream,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (stream.getParameter("Location") != null) {
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.addHeader("Location", stream.getParameter("Location"));
            return;
        }

        // Download from given stream
        final InputStream data = stream.getStream();
        if (data != null) {

            // Sets content type
            response.setContentType(stream.getContentType());

            // Sets cache headers
            final long cacheTime = stream.getCacheTime();
            if (cacheTime <= 0) {
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Pragma", "no-cache");
                response.setDateHeader("Expires", 0);
            } else {
                response.setHeader("Cache-Control", "max-age=" + cacheTime
                        / 1000);
                response.setDateHeader("Expires", System.currentTimeMillis()
                        + cacheTime);
                response.setHeader("Pragma", "cache"); // Required to apply
                // caching in some
                // Tomcats
            }

            // Copy download stream parameters directly
            // to HTTP headers.
            final Iterator i = stream.getParameterNames();
            if (i != null) {
                while (i.hasNext()) {
                    final String param = (String) i.next();
                    response.setHeader(param, stream.getParameter(param));
                }
            }

            // suggest local filename from DownloadStream if Content-Disposition
            // not explicitly set
            String contentDispositionValue = stream
                    .getParameter("Content-Disposition");
            if (contentDispositionValue == null) {
                contentDispositionValue = "filename=\"" + stream.getFileName()
                        + "\"";
                response.setHeader("Content-Disposition",
                        contentDispositionValue);
            }

            int bufferSize = stream.getBufferSize();
            if (bufferSize <= 0 || bufferSize > MAX_BUFFER_SIZE) {
                bufferSize = DEFAULT_BUFFER_SIZE;
            }
            final byte[] buffer = new byte[bufferSize];
            int bytesRead = 0;

            final OutputStream out = response.getOutputStream();

            while ((bytesRead = data.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
                out.flush();
            }
            out.close();
            data.close();
        }

    }

    /**
     * Creates a new application and registers it into WebApplicationContext
     * (aka session). This is not meant to be overridden. Override
     * getNewApplication to create the application instance in a custom way.
     * 
     * @param request
     * @return
     * @throws ServletException
     * @throws MalformedURLException
     */
    private Application createApplication(HttpServletRequest request)
            throws ServletException, MalformedURLException {
        Application newApplication = getNewApplication(request);

        final WebApplicationContext context = WebApplicationContext
                .getApplicationContext(request.getSession());
        context.addApplication(newApplication);

        return newApplication;
    }

    private void handleServiceException(HttpServletRequest request,
            HttpServletResponse response, Application application, Throwable e)
            throws IOException, ServletException {
        // if this was an UIDL request, response UIDL back to client
        if (getRequestType(request) == RequestType.UIDL) {
            Application.SystemMessages ci = getSystemMessages();
            criticalNotification(request, response,
                    ci.getInternalErrorCaption(), ci.getInternalErrorMessage(),
                    null, ci.getInternalErrorURL());
            if (application != null) {
                application.getErrorHandler()
                        .terminalError(new RequestError(e));
            } else {
                throw new ServletException(e);
            }
        } else {
            // Re-throw other exceptions
            throw new ServletException(e);
        }

    }

    /**
     * Returns the theme for given request/window
     * 
     * @param request
     * @param window
     * @return
     */
    private String getThemeForWindow(HttpServletRequest request, Window window) {
        // Finds theme name
        String themeName;

        if (request.getParameter(URL_PARAMETER_THEME) != null) {
            themeName = request.getParameter(URL_PARAMETER_THEME);
        } else {
            themeName = window.getTheme();
        }

        if (themeName == null) {
            // no explicit theme for window defined
            if (request.getAttribute(REQUEST_DEFAULT_THEME) != null) {
                // the default theme is defined in request (by portal)
                themeName = (String) request
                        .getAttribute(REQUEST_DEFAULT_THEME);
            } else {
                // using the default theme defined by Vaadin
                themeName = getDefaultTheme();
            }
        }
        return themeName;
    }

    /**
     * Returns the default theme. Must never return null.
     * 
     * @return
     */
    public static String getDefaultTheme() {
        return DEFAULT_THEME_NAME;
    }

    /**
     * Calls URI handlers for the request. If an URI handler returns a
     * DownloadStream the stream is passed to the client for downloading.
     * 
     * @param applicationManager
     * @param window
     * @param request
     * @param response
     * @return true if an DownloadStream was sent to the client, false otherwise
     * @throws IOException
     */
    protected boolean handleURI(CommunicationManager applicationManager,
            Window window, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        // Handles the URI
        DownloadStream download = applicationManager.handleURI(window, request,
                response, this);

        // A download request
        if (download != null) {
            // Client downloads an resource
            handleDownload(download, request, response);
            return true;
        }

        return false;
    }

    void handleServiceSessionExpired(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {

        if (isOnUnloadRequest(request)) {
            /*
             * Request was an unload request (e.g. window close event) and the
             * client expects no response if it fails.
             */
            return;
        }

        try {
            Application.SystemMessages ci = getSystemMessages();
            if (getRequestType(request) != RequestType.UIDL) {
                // 'plain' http req - e.g. browser reload;
                // just go ahead redirect the browser
                response.sendRedirect(ci.getSessionExpiredURL());
            } else {
                /*
                 * Invalidate session (weird to have session if we're saying
                 * that it's expired, and worse: portal integration will fail
                 * since the session is not created by the portal.
                 * 
                 * Session must be invalidated before criticalNotification as it
                 * commits the response.
                 */
                request.getSession().invalidate();

                // send uidl redirect
                criticalNotification(request, response,
                        ci.getSessionExpiredCaption(),
                        ci.getSessionExpiredMessage(), null,
                        ci.getSessionExpiredURL());

            }
        } catch (SystemMessageException ee) {
            throw new ServletException(ee);
        }

    }

    private void handleServiceSecurityException(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {
        if (isOnUnloadRequest(request)) {
            /*
             * Request was an unload request (e.g. window close event) and the
             * client expects no response if it fails.
             */
            return;
        }

        try {
            Application.SystemMessages ci = getSystemMessages();
            if (getRequestType(request) != RequestType.UIDL) {
                // 'plain' http req - e.g. browser reload;
                // just go ahead redirect the browser
                response.sendRedirect(ci.getCommunicationErrorURL());
            } else {
                // send uidl redirect
                criticalNotification(request, response,
                        ci.getCommunicationErrorCaption(),
                        ci.getCommunicationErrorMessage(),
                        INVALID_SECURITY_KEY_MSG, ci.getCommunicationErrorURL());
                /*
                 * Invalidate session. Portal integration will fail otherwise
                 * since the session is not created by the portal.
                 */
                request.getSession().invalidate();
            }
        } catch (SystemMessageException ee) {
            throw new ServletException(ee);
        }

        log("Invalid security key received from " + request.getRemoteHost());
    }

    /**
     * Creates a new application for the given request.
     * 
     * @param request
     *            the HTTP request.
     * @return A new Application instance.
     * @throws ServletException
     */
    protected abstract Application getNewApplication(HttpServletRequest request)
            throws ServletException;

    /**
     * Starts the application if it is not already running.
     * 
     * @param request
     * @param application
     * @param webApplicationContext
     * @throws ServletException
     * @throws MalformedURLException
     */
    private void startApplication(HttpServletRequest request,
            Application application, WebApplicationContext webApplicationContext)
            throws ServletException, MalformedURLException {

        if (!application.isRunning()) {
            // Create application
            final URL applicationUrl = getApplicationUrl(request);

            // Initial locale comes from the request
            Locale locale = request.getLocale();
            application.setLocale(locale);
            application.start(applicationUrl, applicationProperties,
                    webApplicationContext);
        }
    }

    /**
     * Check if this is a request for a static resource and, if it is, serve the
     * resource to the client.
     * 
     * @param request
     * @param response
     * @return true if a file was served and the request has been handled, false
     *         otherwise.
     * @throws IOException
     * @throws ServletException
     */
    private boolean serveStaticResources(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {

        // FIXME What does 10 refer to?
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 10) {
            return false;
        }

        if ((request.getContextPath() != null)
                && (request.getRequestURI().startsWith("/VAADIN/"))) {
            serveStaticResourcesInVAADIN(request.getRequestURI(), request,
                    response);
            return true;
        } else if (request.getRequestURI().startsWith(
                request.getContextPath() + "/VAADIN/")) {
            serveStaticResourcesInVAADIN(
                    request.getRequestURI().substring(
                            request.getContextPath().length()), request,
                    response);
            return true;
        }

        return false;
    }

    /**
     * Serve resources from VAADIN directory.
     * 
     * @param filename
     *            The filename to serve. Should always start with /VAADIN/.
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    private void serveStaticResourcesInVAADIN(String filename,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        final ServletContext sc = getServletContext();
        URL resourceUrl = sc.getResource(filename);
        if (resourceUrl == null) {
            // try if requested file is found from classloader

            // strip leading "/" otherwise stream from JAR wont work
            filename = filename.substring(1);
            resourceUrl = getClassLoader().getResource(filename);

            if (resourceUrl == null) {
                // cannot serve requested file
                System.err
                        .println("Requested resource ["
                                + filename
                                + "] not found from filesystem or through class loader."
                                + " Add widgetset and/or theme JAR to your classpath or add files to WebContent/VAADIN folder.");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        // Find the modification timestamp
        long lastModifiedTime = 0;
        try {
            lastModifiedTime = resourceUrl.openConnection().getLastModified();
            // Remove milliseconds to avoid comparison problems (milliseconds
            // are not returned by the browser in the "If-Modified-Since"
            // header).
            lastModifiedTime = lastModifiedTime - lastModifiedTime % 1000;

            if (browserHasNewestVersion(request, lastModifiedTime)) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }
        } catch (Exception e) {
            // Failed to find out last modified timestamp. Continue without it.
            e.printStackTrace();
        }

        // Set type mime type if we can determine it based on the filename
        final String mimetype = sc.getMimeType(filename);
        if (mimetype != null) {
            response.setContentType(mimetype);
        }

        // Provide modification timestamp to the browser if it is known.
        if (lastModifiedTime > 0) {
            response.setDateHeader("Last-Modified", lastModifiedTime);
            /*
             * The browser is allowed to cache for 1 hour without checking if
             * the file has changed. This forces browsers to fetch a new version
             * when the Vaadin version is updated. This will cause more requests
             * to the servlet than without this but for high volume sites the
             * static files should never be served through the servlet. The
             * cache timeout can be configured by setting the resourceCacheTime
             * parameter in web.xml
             */
            response.setHeader("Cache-Control",
                    "max-age: " + String.valueOf(resourceCacheTime));
        }

        // Write the resource to the client.
        final OutputStream os = response.getOutputStream();
        final byte buffer[] = new byte[DEFAULT_BUFFER_SIZE];
        int bytes;
        InputStream is = resourceUrl.openStream();
        while ((bytes = is.read(buffer)) >= 0) {
            os.write(buffer, 0, bytes);
        }
        is.close();
    }

    /**
     * Checks if the browser has an up to date cached version of requested
     * resource. Currently the check is performed using the "If-Modified-Since"
     * header. Could be expanded if needed.
     * 
     * @param request
     *            The HttpServletRequest from the browser.
     * @param resourceLastModifiedTimestamp
     *            The timestamp when the resource was last modified. 0 if the
     *            last modification time is unknown.
     * @return true if the If-Modified-Since header tells the cached version in
     *         the browser is up to date, false otherwise
     */
    private boolean browserHasNewestVersion(HttpServletRequest request,
            long resourceLastModifiedTimestamp) {
        if (resourceLastModifiedTimestamp < 1) {
            // We do not know when it was modified so the browser cannot have an
            // up-to-date version
            return false;
        }
        /*
         * The browser can request the resource conditionally using an
         * If-Modified-Since header. Check this against the last modification
         * time.
         */
        try {
            // If-Modified-Since represents the timestamp of the version cached
            // in the browser
            long headerIfModifiedSince = request
                    .getDateHeader("If-Modified-Since");

            if (headerIfModifiedSince >= resourceLastModifiedTimestamp) {
                // Browser has this an up-to-date version of the resource
                return true;
            }
        } catch (Exception e) {
            // Failed to parse header. Fail silently - the browser does not have
            // an up-to-date version in its cache.
        }
        return false;
    }

    protected enum RequestType {
        FILE_UPLOAD, UIDL, OTHER, STATIC_FILE, APPLICATION_RESOURCE;
    }

    protected RequestType getRequestType(HttpServletRequest request) {
        if (isFileUploadRequest(request)) {
            return RequestType.FILE_UPLOAD;
        } else if (isUIDLRequest(request)) {
            return RequestType.UIDL;
        } else if (isStaticResourceRequest(request)) {
            return RequestType.STATIC_FILE;
        } else if (isApplicationRequest(request)) {
            return RequestType.APPLICATION_RESOURCE;
        } else if (request.getHeader("FileId") != null) {
            return RequestType.FILE_UPLOAD;
        }
        return RequestType.OTHER;

    }

    private boolean isApplicationRequest(HttpServletRequest request) {
        String path = getRequestPathInfo(request);
        if (path != null && path.startsWith("/APP/")) {
            return true;
        }
        return false;
    }

    private boolean isStaticResourceRequest(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 10) {
            return false;
        }

        if ((request.getContextPath() != null)
                && (request.getRequestURI().startsWith("/VAADIN/"))) {
            return true;
        } else if (request.getRequestURI().startsWith(
                request.getContextPath() + "/VAADIN/")) {
            return true;
        }

        return false;
    }

    private boolean isUIDLRequest(HttpServletRequest request) {
        String pathInfo = getRequestPathInfo(request);

        if (pathInfo == null) {
            return false;
        }

        String compare = AJAX_UIDL_URI;

        if (pathInfo.startsWith(compare + "/") || pathInfo.endsWith(compare)) {
            return true;
        }

        return false;
    }

    private boolean isFileUploadRequest(HttpServletRequest request) {
        return ServletFileUpload.isMultipartContent(request);
    }

    private boolean isOnUnloadRequest(HttpServletRequest request) {
        return request.getParameter(ApplicationConnection.PARAM_UNLOADBURST) != null;
    }

    /**
     * Get system messages from the current application class
     * 
     * @return
     */
    protected SystemMessages getSystemMessages() {
        try {
            Class<? extends Application> appCls = getApplicationClass();
            Method m = appCls.getMethod("getSystemMessages", (Class[]) null);
            return (Application.SystemMessages) m.invoke(null, (Object[]) null);
        } catch (ClassNotFoundException e) {
            // This should never happen
            throw new SystemMessageException(e);
        } catch (SecurityException e) {
            throw new SystemMessageException(
                    "Application.getSystemMessage() should be static public", e);
        } catch (NoSuchMethodException e) {
            // This is completely ok and should be silently ignored
        } catch (IllegalArgumentException e) {
            // This should never happen
            throw new SystemMessageException(e);
        } catch (IllegalAccessException e) {
            throw new SystemMessageException(
                    "Application.getSystemMessage() should be static public", e);
        } catch (InvocationTargetException e) {
            // This should never happen
            throw new SystemMessageException(e);
        }
        return Application.getSystemMessages();
    }

    protected abstract Class<? extends Application> getApplicationClass()
            throws ClassNotFoundException;

    /**
     * Return the URL from where static files, e.g. the widgetset and the theme,
     * are served. In a standard configuration the VAADIN folder inside the
     * returned folder is what is used for widgetsets and themes.
     * 
     * The returned folder is usually the same as the context path and
     * independent of the application.
     * 
     * @param request
     * @return The location of static resources (should contain the VAADIN
     *         directory). Never ends with a slash (/).
     */
    protected String getStaticFilesLocation(HttpServletRequest request) {

        // request may have an attribute explicitly telling location (portal
        // case)
        String staticFileLocation = (String) request
                .getAttribute(REQUEST_VAADIN_STATIC_FILE_PATH);
        if (staticFileLocation != null) {
            // TODO remove trailing slash if any?
            return staticFileLocation;
        }

        return getWebApplicationsStaticFileLocation(request);
    }

    /**
     * The default method to fetch static files location. This method does not
     * check for request attribute {@value #REQUEST_VAADIN_STATIC_FILE_PATH}.
     * 
     * @param request
     * @return
     */
    private String getWebApplicationsStaticFileLocation(
            HttpServletRequest request) {
        String staticFileLocation;
        // if property is defined in configurations, use that
        staticFileLocation = getApplicationOrSystemProperty(
                PARAMETER_VAADIN_RESOURCES, null);
        if (staticFileLocation != null) {
            return staticFileLocation;
        }

        // the last (but most common) option is to generate default location
        // from request

        // if context is specified add it to widgetsetUrl
        String ctxPath = request.getContextPath();

        // FIXME: ctxPath.length() == 0 condition is probably unnecessary and
        // might even be wrong.

        if (ctxPath.length() == 0
                && request.getAttribute("javax.servlet.include.context_path") != null) {
            // include request (e.g portlet), get context path from
            // attribute
            ctxPath = (String) request
                    .getAttribute("javax.servlet.include.context_path");
        }

        // Remove heading and trailing slashes from the context path
        ctxPath = removeHeadingOrTrailing(ctxPath, "/");

        if (ctxPath.equals("")) {
            return "";
        } else {
            return "/" + ctxPath;
        }
    }

    /**
     * Remove any heading or trailing "what" from the "string".
     * 
     * @param string
     * @param what
     * @return
     */
    private static String removeHeadingOrTrailing(String string, String what) {
        while (string.startsWith(what)) {
            string = string.substring(1);
        }

        while (string.endsWith(what)) {
            string = string.substring(0, string.length() - 1);
        }

        return string;
    }

    /**
     * Write a redirect response to the main page of the application.
     * 
     * @param request
     * @param response
     * @throws IOException
     *             if sending the redirect fails due to an input/output error or
     *             a bad application URL
     */
    private void redirectToApplication(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        String applicationUrl = getApplicationUrl(request).toExternalForm();
        response.sendRedirect(response.encodeRedirectURL(applicationUrl));
    }

    /**
     * This method writes the html host page (aka kickstart page) that starts
     * the actual Vaadin application.
     * <p>
     * If one needs to override parts of the host page, it is suggested that one
     * overrides on of several submethods which are called by this method:
     * <ul>
     * <li> {@link #setAjaxPageHeaders(HttpServletResponse)}
     * <li> {@link #writeAjaxPageHtmlHeadStart(BufferedWriter)}
     * <li> {@link #writeAjaxPageHtmlHeader(BufferedWriter, String, String)}
     * <li> {@link #writeAjaxPageHtmlBodyStart(BufferedWriter)}
     * <li>
     * {@link #writeAjaxPageHtmlVaadinScripts(Window, String, Application, BufferedWriter, String, String, String, String, String, String)}
     * <li>
     * {@link #writeAjaxPageHtmlMainDiv(BufferedWriter, String, String, String)}
     * <li> {@link #writeAjaxPageHtmlBodyEnd(BufferedWriter)}
     * </ul>
     * 
     * @param request
     *            the HTTP request.
     * @param response
     *            the HTTP response to write to.
     * @param out
     * @param unhandledParameters
     * @param window
     * @param terminalType
     * @param theme
     * @throws IOException
     *             if the writing failed due to input/output error.
     * @throws MalformedURLException
     *             if the application is denied access the persistent data store
     *             represented by the given URL.
     */
    protected void writeAjaxPage(HttpServletRequest request,
            HttpServletResponse response, Window window, Application application)
            throws IOException, MalformedURLException, ServletException {

        // e.g portlets only want a html fragment
        boolean fragment = (request.getAttribute(REQUEST_FRAGMENT) != null);
        if (fragment) {
            // if this is a fragment request, the actual application is put to
            // request so ApplicationPortlet can save it for a later use
            request.setAttribute(Application.class.getName(), application);
        }

        final BufferedWriter page = new BufferedWriter(new OutputStreamWriter(
                response.getOutputStream(), "UTF-8"));

        String title = ((window.getCaption() == null) ? "Vaadin 6" : window
                .getCaption());

        /* Fetch relative url to application */
        // don't use server and port in uri. It may cause problems with some
        // virtual server configurations which lose the server name
        String appUrl = getApplicationUrl(request).getPath();
        if (appUrl.endsWith("/")) {
            appUrl = appUrl.substring(0, appUrl.length() - 1);
        }

        String themeName = getThemeForWindow(request, window);

        String themeUri = getThemeUri(themeName, request);

        if (!fragment) {
            setAjaxPageHeaders(response);
            writeAjaxPageHtmlHeadStart(page);
            writeAjaxPageHtmlHeader(page, title, themeUri);
            writeAjaxPageHtmlBodyStart(page);
        }

        String appId = appUrl;
        if ("".equals(appUrl)) {
            appId = "ROOT";
        }
        appId = appId.replaceAll("[^a-zA-Z0-9]", "");
        // Add hashCode to the end, so that it is still (sort of) predictable,
        // but indicates that it should not be used in CSS and such:
        int hashCode = appId.hashCode();
        if (hashCode < 0) {
            hashCode = -hashCode;
        }
        appId = appId + "-" + hashCode;

        writeAjaxPageHtmlVaadinScripts(window, themeName, application, page,
                appUrl, themeUri, appId, request);

        /*- Add classnames;
         *      .v-app
         *      .v-app-loading
         *      .v-app-<simpleName for app class>
         *      .v-theme-<themeName, remove non-alphanum>
         */

        String appClass = "v-app-" + getApplicationCSSClassName();

        String themeClass = "";
        if (themeName != null) {
            themeClass = "v-theme-" + themeName.replaceAll("[^a-zA-Z0-9]", "");
        } else {
            themeClass = "v-theme-"
                    + getDefaultTheme().replaceAll("[^a-zA-Z0-9]", "");
        }

        String classNames = "v-app v-app-loading " + themeClass + " "
                + appClass;

        String divStyle = null;
        if (request.getAttribute(REQUEST_APPSTYLE) != null) {
            divStyle = "style=\"" + request.getAttribute(REQUEST_APPSTYLE)
                    + "\"";
        }

        writeAjaxPageHtmlMainDiv(page, appId, classNames, divStyle);

        if (!fragment) {
            page.write("</body>\n</html>\n");
        }

        page.close();

    }

    /**
     * Returns the application class identifier for use in the application CSS
     * class name in the root DIV. The application CSS class name is of form
     * "v-app-"+getApplicationCSSClassName().
     * 
     * This method should normally not be overridden.
     * 
     * @return The CSS class name to use in combination with "v-app-".
     */
    protected String getApplicationCSSClassName() {
        try {
            return getApplicationClass().getSimpleName();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return "unknown";
        }
    }

    /**
     * Get the URI for the application theme.
     * 
     * A portal-wide default theme is fetched from the portal shared resource
     * directory (if any), other themes from the portlet.
     * 
     * @param themeName
     * @param request
     * @return
     */
    private String getThemeUri(String themeName, HttpServletRequest request) {
        final String staticFilePath;
        if (themeName.equals(request.getAttribute(REQUEST_DEFAULT_THEME))) {
            // our window theme is the portal wide default theme, make it load
            // from portals directory is defined
            staticFilePath = getStaticFilesLocation(request);
        } else {
            /*
             * theme is a custom theme, which is not necessarily located in
             * portals VAADIN directory. Let the default servlet conf decide
             * (omitting request parameter) the location. Note that theme can
             * still be placed to portal directory with servlet parameter.
             */
            staticFilePath = getWebApplicationsStaticFileLocation(request);
        }
        return staticFilePath + "/" + THEME_DIRECTORY_PATH + themeName;
    }

    /**
     * Method to write the div element into which that actual Vaadin application
     * is rendered.
     * <p>
     * Override this method if you want to add some custom html around around
     * the div element into which the actual Vaadin application will be
     * rendered.
     * 
     * @param page
     * @param appId
     * @param classNames
     * @param divStyle
     * @throws IOException
     */
    protected void writeAjaxPageHtmlMainDiv(final BufferedWriter page,
            String appId, String classNames, String divStyle)
            throws IOException {
        page.write("<div id=\"" + appId + "\" class=\"" + classNames + "\" "
                + (divStyle != null ? divStyle : "") + "></div>\n");
        page.write("<noscript>" + getNoScriptMessage() + "</noscript>");
    }

    /**
     * Method to write the script part of the page which loads needed Vaadin
     * scripts and themes.
     * <p>
     * Override this method if you want to add some custom html around scripts.
     * 
     * @param window
     * @param themeName
     * @param application
     * @param page
     * @param appUrl
     * @param themeUri
     * @param appId
     * @param request
     * @throws ServletException
     * @throws IOException
     */
    protected void writeAjaxPageHtmlVaadinScripts(Window window,
            String themeName, Application application,
            final BufferedWriter page, String appUrl, String themeUri,
            String appId, HttpServletRequest request) throws ServletException,
            IOException {

        // request widgetset takes precedence (e.g portlet include)
        String requestWidgetset = (String) request
                .getAttribute(REQUEST_WIDGETSET);
        String sharedWidgetset = (String) request
                .getAttribute(REQUEST_SHARED_WIDGETSET);
        if (requestWidgetset == null && sharedWidgetset == null) {
            // Use the value from configuration or DEFAULT_WIDGETSET.
            // If no shared widgetset is specified, the default widgetset is
            // assumed to be in the servlet/portlet itself.
            requestWidgetset = getApplicationOrSystemProperty(
                    PARAMETER_WIDGETSET, DEFAULT_WIDGETSET);
        }

        String widgetset;
        String widgetsetBasePath;
        if (requestWidgetset != null) {
            widgetset = requestWidgetset;
            widgetsetBasePath = getWebApplicationsStaticFileLocation(request);
        } else {
            widgetset = sharedWidgetset;
            widgetsetBasePath = getStaticFilesLocation(request);
        }

        final String widgetsetFilePath = widgetsetBasePath + "/"
                + WIDGETSET_DIRECTORY_PATH + widgetset + "/" + widgetset
                + ".nocache.js?" + new Date().getTime();

        // Get system messages
        Application.SystemMessages systemMessages = null;
        try {
            systemMessages = getSystemMessages();
        } catch (SystemMessageException e) {
            // failing to get the system messages is always a problem
            throw new ServletException("CommunicationError!", e);
        }

        page.write("<script type=\"text/javascript\">\n");
        page.write("//<![CDATA[\n");
        page.write("if(!vaadin || !vaadin.vaadinConfigurations) {\n "
                + "if(!vaadin) { var vaadin = {}} \n"
                + "vaadin.vaadinConfigurations = {};\n"
                + "if (!vaadin.themesLoaded) { vaadin.themesLoaded = {}; }\n");
        if (!isProductionMode()) {
            page.write("vaadin.debug = true;\n");
        }
        page.write("document.write('<iframe tabIndex=\"-1\" id=\"__gwt_historyFrame\" "
                + "style=\"position:absolute;width:0;height:0;border:0;overflow:"
                + "hidden;\" src=\"javascript:false\"></iframe>');\n");
        page.write("document.write(\"<script language='javascript' src='"
                + widgetsetFilePath + "'><\\/script>\");\n}\n");

        page.write("vaadin.vaadinConfigurations[\"" + appId + "\"] = {");
        page.write("appUri:'" + appUrl + "', ");

        String pathInfo = getRequestPathInfo(request);
        if (pathInfo == null) {
            pathInfo = "/";
        }

        page.write("pathInfo: '" + pathInfo + "', ");
        if (window != application.getMainWindow()) {
            page.write("windowName: '" + window.getName() + "', ");
        }
        page.write("themeUri:");
        page.write(themeUri != null ? "'" + themeUri + "'" : "null");
        page.write(", versionInfo : {vaadinVersion:\"");
        page.write(VERSION);
        page.write("\",applicationVersion:\"");
        page.write(application.getVersion());
        page.write("\"}");
        if (systemMessages != null) {
            // Write the CommunicationError -message to client
            String caption = systemMessages.getCommunicationErrorCaption();
            if (caption != null) {
                caption = "\"" + caption + "\"";
            }
            String message = systemMessages.getCommunicationErrorMessage();
            if (message != null) {
                message = "\"" + message + "\"";
            }
            String url = systemMessages.getCommunicationErrorURL();
            if (url != null) {
                url = "\"" + url + "\"";
            }

            page.write(",\"comErrMsg\": {" + "\"caption\":" + caption + ","
                    + "\"message\" : " + message + "," + "\"url\" : " + url
                    + "}");

            // Write the AuthenticationError -message to client
            caption = systemMessages.getAuthenticationErrorCaption();
            if (caption != null) {
                caption = "\"" + caption + "\"";
            }
            message = systemMessages.getAuthenticationErrorMessage();
            if (message != null) {
                message = "\"" + message + "\"";
            }
            url = systemMessages.getAuthenticationErrorURL();
            if (url != null) {
                url = "\"" + url + "\"";
            }

            page.write(",\"authErrMsg\": {" + "\"caption\":" + caption + ","
                    + "\"message\" : " + message + "," + "\"url\" : " + url
                    + "}");
        }
        page.write("};\n//]]>\n</script>\n");

        if (themeName != null) {
            // Custom theme's stylesheet, load only once, in different
            // script
            // tag to be dominate styles injected by widget
            // set
            page.write("<script type=\"text/javascript\">\n");
            page.write("//<![CDATA[\n");
            page.write("if(!vaadin.themesLoaded['" + themeName + "']) {\n");
            page.write("var stylesheet = document.createElement('link');\n");
            page.write("stylesheet.setAttribute('rel', 'stylesheet');\n");
            page.write("stylesheet.setAttribute('type', 'text/css');\n");
            page.write("stylesheet.setAttribute('href', '" + themeUri
                    + "/styles.css');\n");
            page.write("document.getElementsByTagName('head')[0].appendChild(stylesheet);\n");
            page.write("vaadin.themesLoaded['" + themeName + "'] = true;\n}\n");
            page.write("//]]>\n</script>\n");
        }

        // Warn if the widgetset has not been loaded after 15 seconds on
        // inactivity
        page.write("<script type=\"text/javascript\">\n");
        page.write("//<![CDATA[\n");
        page.write("setTimeout('if (typeof " + widgetset.replace('.', '_')
                + " == \"undefined\") {alert(\"Failed to load the widgetset: "
                + widgetsetFilePath + "\")};',15000);\n" + "//]]>\n</script>\n");
    }

    /**
     * 
     * Method to open the body tag of the html kickstart page.
     * <p>
     * This method is responsible for closing the head tag and opening the body
     * tag.
     * <p>
     * Override this method if you want to add some custom html to the page.
     * 
     * @param page
     * @throws IOException
     */
    protected void writeAjaxPageHtmlBodyStart(final BufferedWriter page)
            throws IOException {
        page.write("\n</head>\n<body scroll=\"auto\" class=\""
                + ApplicationConnection.GENERATED_BODY_CLASSNAME + "\">\n");
    }

    /**
     * Method to write the contents of head element in html kickstart page.
     * <p>
     * Override this method if you want to add some custom html to the header of
     * the page.
     * 
     * @param page
     * @param title
     * @param themeUri
     * @throws IOException
     */
    protected void writeAjaxPageHtmlHeader(final BufferedWriter page,
            String title, String themeUri) throws IOException {
        page.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n");

        // Force IE9 into IE8 mode. Remove when IE 9 mode works (#5546)
        page.write("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=8\"/>\n");

        page.write("<style type=\"text/css\">"
                + "html, body {height:100%;margin:0;}</style>");

        // Add favicon links
        page.write("<link rel=\"shortcut icon\" type=\"image/vnd.microsoft.icon\" href=\""
                + themeUri + "/favicon.ico\" />");
        page.write("<link rel=\"icon\" type=\"image/vnd.microsoft.icon\" href=\""
                + themeUri + "/favicon.ico\" />");

        page.write("<title>" + title + "</title>");
    }

    /**
     * Method to write the beginning of the html page.
     * <p>
     * This method is responsible for writing appropriate doc type declarations
     * and to open html and head tags.
     * <p>
     * Override this method if you want to add some custom html to the very
     * beginning of the page.
     * 
     * @param page
     * @throws IOException
     */
    protected void writeAjaxPageHtmlHeadStart(final BufferedWriter page)
            throws IOException {
        // write html header
        page.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD "
                + "XHTML 1.0 Transitional//EN\" "
                + "\"http://www.w3.org/TR/xhtml1/"
                + "DTD/xhtml1-transitional.dtd\">\n");

        page.write("<html xmlns=\"http://www.w3.org/1999/xhtml\""
                + ">\n<head>\n");
    }

    /**
     * Method to set http request headers for the Vaadin kickstart page.
     * <p>
     * Override this method if you need to customize http headers of the page.
     * 
     * @param response
     */
    protected void setAjaxPageHeaders(HttpServletResponse response) {
        // Window renders are not cacheable
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("text/html; charset=UTF-8");
    }

    /**
     * Returns a message printed for browsers without scripting support or if
     * browsers scripting support is disabled.
     */
    protected String getNoScriptMessage() {
        return "You have to enable javascript in your browser to use an application built with Vaadin.";
    }

    /**
     * Gets the current application URL from request.
     * 
     * @param request
     *            the HTTP request.
     * @throws MalformedURLException
     *             if the application is denied access to the persistent data
     *             store represented by the given URL.
     */
    URL getApplicationUrl(HttpServletRequest request)
            throws MalformedURLException {
        final URL reqURL = new URL(
                (request.isSecure() ? "https://" : "http://")
                        + request.getServerName()
                        + ((request.isSecure() && request.getServerPort() == 443)
                                || (!request.isSecure() && request
                                        .getServerPort() == 80) ? "" : ":"
                                + request.getServerPort())
                        + request.getRequestURI());
        String servletPath = "";
        if (request.getAttribute("javax.servlet.include.servlet_path") != null) {
            // this is an include request
            servletPath = request.getAttribute(
                    "javax.servlet.include.context_path").toString()
                    + request
                            .getAttribute("javax.servlet.include.servlet_path");

        } else {
            servletPath = request.getContextPath() + request.getServletPath();
        }

        if (servletPath.length() == 0
                || servletPath.charAt(servletPath.length() - 1) != '/') {
            servletPath = servletPath + "/";
        }
        URL u = new URL(reqURL, servletPath);
        return u;
    }

    /**
     * Gets the existing application for given request. Looks for application
     * instance for given request based on the requested URL.
     * 
     * @param request
     *            the HTTP request.
     * @param allowSessionCreation
     *            true if a session should be created if no session exists,
     *            false if no session should be created
     * @return Application instance, or null if the URL does not map to valid
     *         application.
     * @throws MalformedURLException
     *             if the application is denied access to the persistent data
     *             store represented by the given URL.
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws SessionExpiredException
     */
    private Application getExistingApplication(HttpServletRequest request,
            boolean allowSessionCreation) throws MalformedURLException,
            SessionExpiredException {

        // Ensures that the session is still valid
        final HttpSession session = request.getSession(allowSessionCreation);
        if (session == null) {
            throw new SessionExpiredException();
        }

        WebApplicationContext context = WebApplicationContext
                .getApplicationContext(session);

        // Gets application list for the session.
        final Collection<Application> applications = context.getApplications();

        // Search for the application (using the application URI) from the list
        for (final Iterator<Application> i = applications.iterator(); i
                .hasNext();) {
            final Application sessionApplication = i.next();
            final String sessionApplicationPath = sessionApplication.getURL()
                    .getPath();
            String requestApplicationPath = getApplicationUrl(request)
                    .getPath();

            if (requestApplicationPath.equals(sessionApplicationPath)) {
                // Found a running application
                if (sessionApplication.isRunning()) {
                    return sessionApplication;
                }
                // Application has stopped, so remove it before creating a new
                // application
                WebApplicationContext.getApplicationContext(session)
                        .removeApplication(sessionApplication);
                break;
            }
        }

        // Existing application not found
        return null;
    }

    /**
     * Ends the application.
     * 
     * @param request
     *            the HTTP request.
     * @param response
     *            the HTTP response to write to.
     * @param application
     *            the application to end.
     * @throws IOException
     *             if the writing failed due to input/output error.
     */
    private void endApplication(HttpServletRequest request,
            HttpServletResponse response, Application application)
            throws IOException {

        String logoutUrl = application.getLogoutURL();
        if (logoutUrl == null) {
            logoutUrl = application.getURL().toString();
        }

        final HttpSession session = request.getSession();
        if (session != null) {
            WebApplicationContext.getApplicationContext(session)
                    .removeApplication(application);
        }

        response.sendRedirect(response.encodeRedirectURL(logoutUrl));
    }

    /**
     * Gets the existing application or create a new one. Get a window within an
     * application based on the requested URI.
     * 
     * @param request
     *            the HTTP Request.
     * @param application
     *            the Application to query for window.
     * @return Window matching the given URI or null if not found.
     * @throws ServletException
     *             if an exception has occurred that interferes with the
     *             servlet's normal operation.
     */
    private Window getApplicationWindow(HttpServletRequest request,
            CommunicationManager applicationManager, Application application)
            throws ServletException {

        // Finds the window where the request is handled
        Window assumedWindow = null;
        String path = getRequestPathInfo(request);

        // Main window as the URI is empty
        if (!(path == null || path.length() == 0 || path.equals("/"))) {
            if (path.startsWith("/APP/")) {
                // Use main window for application resources
                return application.getMainWindow();
            }
            String windowName = null;
            if (path.charAt(0) == '/') {
                path = path.substring(1);
            }
            final int index = path.indexOf('/');
            if (index < 0) {
                windowName = path;
                path = "";
            } else {
                windowName = path.substring(0, index);
            }
            assumedWindow = application.getWindow(windowName);

        }

        return applicationManager.getApplicationWindow(request, this,
                application, assumedWindow);
    }

    /**
     * Returns the path info; note that this _can_ be different than
     * request.getPathInfo() (e.g application runner).
     * 
     * @param request
     * @return
     */
    String getRequestPathInfo(HttpServletRequest request) {
        return request.getPathInfo();
    }

    /**
     * Gets relative location of a theme resource.
     * 
     * @param theme
     *            the Theme name.
     * @param resource
     *            the Theme resource.
     * @return External URI specifying the resource
     */
    public String getResourceLocation(String theme, ThemeResource resource) {

        if (resourcePath == null) {
            return resource.getResourceId();
        }
        return resourcePath + theme + "/" + resource.getResourceId();
    }

    private boolean isRepaintAll(HttpServletRequest request) {
        return (request.getParameter(URL_PARAMETER_REPAINT_ALL) != null)
                && (request.getParameter(URL_PARAMETER_REPAINT_ALL).equals("1"));
    }

    private void closeApplication(Application application, HttpSession session) {
        if (application == null) {
            return;
        }

        application.close();
        if (session != null) {
            WebApplicationContext context = WebApplicationContext
                    .getApplicationContext(session);
            context.removeApplication(application);
        }
    }

    /**
     * Implementation of ParameterHandler.ErrorEvent interface.
     */
    public class ParameterHandlerErrorImpl implements
            ParameterHandler.ErrorEvent, Serializable {

        private ParameterHandler owner;

        private Throwable throwable;

        /**
         * Gets the contained throwable.
         * 
         * @see com.vaadin.terminal.Terminal.ErrorEvent#getThrowable()
         */
        public Throwable getThrowable() {
            return throwable;
        }

        /**
         * Gets the source ParameterHandler.
         * 
         * @see com.vaadin.terminal.ParameterHandler.ErrorEvent#getParameterHandler()
         */
        public ParameterHandler getParameterHandler() {
            return owner;
        }

    }

    /**
     * Implementation of URIHandler.ErrorEvent interface.
     */
    public class URIHandlerErrorImpl implements URIHandler.ErrorEvent,
            Serializable {

        private final URIHandler owner;

        private final Throwable throwable;

        /**
         * 
         * @param owner
         * @param throwable
         */
        private URIHandlerErrorImpl(URIHandler owner, Throwable throwable) {
            this.owner = owner;
            this.throwable = throwable;
        }

        /**
         * Gets the contained throwable.
         * 
         * @see com.vaadin.terminal.Terminal.ErrorEvent#getThrowable()
         */
        public Throwable getThrowable() {
            return throwable;
        }

        /**
         * Gets the source URIHandler.
         * 
         * @see com.vaadin.terminal.URIHandler.ErrorEvent#getURIHandler()
         */
        public URIHandler getURIHandler() {
            return owner;
        }
    }

    public class RequestError implements Terminal.ErrorEvent, Serializable {

        private final Throwable throwable;

        public RequestError(Throwable throwable) {
            this.throwable = throwable;
        }

        public Throwable getThrowable() {
            return throwable;
        }

    }
}
