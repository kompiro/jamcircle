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

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.vaadin.Application;
import com.vaadin.terminal.ApplicationResource;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.terminal.URIHandler;
import com.vaadin.terminal.gwt.client.ApplicationConnection;

/**
 * LoginForm is a Vaadin component to handle common problem among Ajax
 * applications: browsers password managers don't fill dynamically created forms
 * like all those UI elements created by Vaadin.
 * <p>
 * For developer it is easy to use: add component to a desired place in you UI
 * and add LoginListener to validate form input. Behind the curtain LoginForm
 * creates an iframe with static html that browsers detect.
 * <p>
 * Login form is by default 100% width and height, so consider using it inside a
 * sized {@link Panel} or {@link Window}.
 * <p>
 * Login page html can be overridden by replacing protected getLoginHTML method.
 * As the login page is actually an iframe, styles must be handled manually. By
 * default component tries to guess the right place for theme css.
 * <p>
 * Note, this is a new Ajax terminal specific component and is likely to change.
 * 
 * @since 5.3
 */
public class LoginForm extends CustomComponent {

    private Embedded iframe = new Embedded();

    private ApplicationResource loginPage = new ApplicationResource() {

        public Application getApplication() {
            return LoginForm.this.getApplication();
        }

        public int getBufferSize() {
            return getLoginHTML().length;
        }

        public long getCacheTime() {
            return -1;
        }

        public String getFilename() {
            return "login";
        }

        public DownloadStream getStream() {
            return new DownloadStream(new ByteArrayInputStream(getLoginHTML()),
                    getMIMEType(), getFilename());
        }

        public String getMIMEType() {
            return "text/html; charset=utf-8";
        }
    };

    private ParameterHandler paramHandler = new ParameterHandler() {

        public void handleParameters(Map<String, String[]> parameters) {
            if (parameters.containsKey("username")) {
                getWindow().addURIHandler(uriHandler);

                HashMap<String, String> params = new HashMap<String, String>();
                // expecting single params
                for (Iterator<String> it = parameters.keySet().iterator(); it
                        .hasNext();) {
                    String key = it.next();
                    String value = (parameters.get(key))[0];
                    params.put(key, value);
                }
                LoginEvent event = new LoginEvent(params);
                fireEvent(event);
            }
        }
    };

    private URIHandler uriHandler = new URIHandler() {
        private final String responce = "<html><body>Login form handeled."
                + "<script type='text/javascript'>top.vaadin.forceSync();"
                + "</script></body></html>";

        public DownloadStream handleURI(URL context, String relativeUri) {
            if (relativeUri != null && relativeUri.contains("loginHandler")) {
                if (window != null) {
                    window.removeURIHandler(this);
                }
                DownloadStream downloadStream = new DownloadStream(
                        new ByteArrayInputStream(responce.getBytes()),
                        "text/html", "loginSuccesfull");
                downloadStream.setCacheTime(-1);
                return downloadStream;
            } else {
                return null;
            }
        }
    };

    private Window window;

    public LoginForm() {
        iframe.setType(Embedded.TYPE_BROWSER);
        iframe.setSizeFull();
        setSizeFull();
        setCompositionRoot(iframe);
        addStyleName("v-loginform");
    }

    /**
     * Returns byte array containing login page html. If you need to override
     * the login html, use the default html as basis. Login page sets its target
     * with javascript.
     * 
     * @return byte array containing login page html
     */
    protected byte[] getLoginHTML() {

        String appUri = getApplication().getURL().toString()
                + getWindow().getName() + "/";

        return ("<!DOCTYPE html PUBLIC \"-//W3C//DTD "
                + "XHTML 1.0 Transitional//EN\" "
                + "\"http://www.w3.org/TR/xhtml1/"
                + "DTD/xhtml1-transitional.dtd\">\n" + "<html>"
                + "<head><script type='text/javascript'>"
                + "var setTarget = function() {" + "var uri = '"
                + appUri
                + "loginHandler"
                + "'; var f = document.getElementById('loginf');"
                + "document.forms[0].action = uri;document.forms[0].username.focus();};"
                + ""
                + "var styles = window.parent.document.styleSheets;"
                + "for(var j = 0; j < styles.length; j++) {\n"
                + "if(styles[j].href) {"
                + "var stylesheet = document.createElement('link');\n"
                + "stylesheet.setAttribute('rel', 'stylesheet');\n"
                + "stylesheet.setAttribute('type', 'text/css');\n"
                + "stylesheet.setAttribute('href', styles[j].href);\n"
                + "document.getElementsByTagName('head')[0].appendChild(stylesheet);\n"
                + "}"
                + "}\n"
                + "function submitOnEnter(e) { var keycode = e.keyCode || e.which;"
                + " if (keycode == 13) {document.forms[0].submit();}  } \n"
                + "</script>"
                + "</head><body onload='setTarget();' style='margin:0;padding:0; background:transparent;' class=\""
                + ApplicationConnection.GENERATED_BODY_CLASSNAME
                + "\">"
                + "<div class='v-app v-app-loginpage' style=\"background:transparent;\">"
                + "<iframe name='logintarget' style='width:0;height:0;"
                + "border:0;margin:0;padding:0;'></iframe>"
                + "<form id='loginf' target='logintarget' onkeypress=\"submitOnEnter(event)\" method=\"post\">"
                + "<div>Username</div><div >"
                + "<input class='v-textfield' style='display:block;' type='text' name='username'></div>"
                + "<div>Password</div>"
                + "<div><input class='v-textfield' style='display:block;' type='password' name='password'></div>"
                + "<div><div onclick=\"document.forms[0].submit();\" tabindex=\"0\" class=\"v-button\" role=\"button\" ><span class=\"v-button-wrap\"><span class=\"v-button-caption\">Login</span></span></div></div></form></div>" + "</body></html>")
                .getBytes();
    }

    @Override
    public void attach() {
        super.attach();
        getApplication().addResource(loginPage);
        getWindow().addParameterHandler(paramHandler);
        iframe.setSource(loginPage);
    }

    @Override
    public void detach() {
        getApplication().removeResource(loginPage);
        getWindow().removeParameterHandler(paramHandler);
        // store window temporary to properly remove uri handler once
        // response is handled. (May happen if login handler removes login
        // form
        window = getWindow();
        if (window.getParent() != null) {
            window = window.getParent();
        }
        super.detach();
    }

    /**
     * This event is sent when login form is submitted.
     */
    public class LoginEvent extends Event {

        private Map<String, String> params;

        private LoginEvent(Map<String, String> params) {
            super(LoginForm.this);
            this.params = params;
        }

        /**
         * Access method to form values by field names.
         * 
         * @param name
         * @return value in given field
         */
        public String getLoginParameter(String name) {
            if (params.containsKey(name)) {
                return params.get(name);
            } else {
                return null;
            }
        }
    }

    /**
     * Login listener is a class capable to listen LoginEvents sent from
     * LoginBox
     */
    public interface LoginListener extends Serializable {
        /**
         * This method is fired on each login form post.
         * 
         * @param event
         */
        public void onLogin(LoginForm.LoginEvent event);
    }

    private static final Method ON_LOGIN_METHOD;

    private static final String UNDEFINED_HEIGHT = "140px";
    private static final String UNDEFINED_WIDTH = "200px";

    static {
        try {
            ON_LOGIN_METHOD = LoginListener.class.getDeclaredMethod("onLogin",
                    new Class[] { LoginEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in LoginForm");
        }
    }

    /**
     * Adds LoginListener to handle login logic
     * 
     * @param listener
     */
    public void addListener(LoginListener listener) {
        addListener(LoginEvent.class, listener, ON_LOGIN_METHOD);
    }

    /**
     * Removes LoginListener
     * 
     * @param listener
     */
    public void removeListener(LoginListener listener) {
        removeListener(LoginEvent.class, listener, ON_LOGIN_METHOD);
    }

    @Override
    public void setWidth(float width, int unit) {
        super.setWidth(width, unit);
        if (iframe != null) {
            if (width < 0) {
                iframe.setWidth(UNDEFINED_WIDTH);
            } else {
                iframe.setWidth("100%");
            }
        }
    }

    @Override
    public void setHeight(float height, int unit) {
        super.setHeight(height, unit);
        if (iframe != null) {
            if (height < 0) {
                iframe.setHeight(UNDEFINED_HEIGHT);
            } else {
                iframe.setHeight("100%");
            }
        }
    }

}
