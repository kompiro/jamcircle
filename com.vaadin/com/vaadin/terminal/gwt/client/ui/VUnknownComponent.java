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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VUIDLBrowser;

public class VUnknownComponent extends Composite implements Paintable {

    com.google.gwt.user.client.ui.Label caption = new com.google.gwt.user.client.ui.Label();;
    Tree uidlTree;
    private VerticalPanel panel;
    private String serverClassName;

    public VUnknownComponent(String serverClassName) {
        this.serverClassName = serverClassName;
        panel = new VerticalPanel();
        panel.add(caption);
        initWidget(panel);
        setStyleName("vaadin-unknown");
        caption.setStyleName("vaadin-unknown-caption");
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (client.updateComponent(this, uidl, false)) {
            return;
        }
        setCaption("Widgetset does not contain implementation for "
                + serverClassName
                + ". Check its @ClientWidget mapping, widgetsets "
                + "GWT module description file and re-compile your"
                + " widgetset. In case you have downloaded a vaadin"
                + " add-on package, you might want to refer to "
                + "<a href='http://vaadin.com/using-addons'>add-on "
                + "instructions</a>. Unrendered UIDL:");
        if (uidlTree != null) {
            uidlTree.removeFromParent();
        }

        uidlTree = new VUIDLBrowser(uidl, client.getConfiguration());
        panel.add(uidlTree);
    }

    public void setCaption(String c) {
        caption.getElement().setInnerHTML(c);
    }
}
