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
/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.richtextarea;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.impl.RichTextAreaImplSafari;

/**
 * TODO remove me when GWT RichTextArea is fixed. See #4279 (vaadin trac)
 * 
 */
class CustomWebkitRichTextArea extends RichTextAreaImplSafari {
    public CustomWebkitRichTextArea() {
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                hookBlur(getElement());
            }
        });
    }

    private native void hookBlur(Element iframe)
    /*-{

        iframe.contentDocument.documentElement.onblur = function(evt) {
          if (iframe.__listener) {
            iframe.__listener.@com.google.gwt.user.client.ui.Widget::onBrowserEvent(Lcom/google/gwt/user/client/Event;)(evt);
          }
        };
        
        
    }-*/;
}
