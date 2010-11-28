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
package com.vaadin.terminal.gwt.widgetsetutils;

import java.util.Collection;
import java.util.HashSet;

import com.vaadin.terminal.Paintable;
import com.vaadin.ui.ClientWidget;
import com.vaadin.ui.ClientWidget.LoadStyle;

/**
 * An abstract helper class that can be used to easily build a widgetset with
 * customized load styles for each components. In three abstract methods one can
 * override the default values given in {@link ClientWidget} annotations.
 * 
 * @see WidgetMapGenerator
 * 
 */
public abstract class CustomWidgetMapGenerator extends WidgetMapGenerator {

    private Collection<Class<? extends Paintable>> eagerPaintables = new HashSet<Class<? extends Paintable>>();
    private Collection<Class<? extends Paintable>> lazyPaintables = new HashSet<Class<? extends Paintable>>();
    private Collection<Class<? extends Paintable>> deferredPaintables = new HashSet<Class<? extends Paintable>>();

    @Override
    protected LoadStyle getLoadStyle(Class<? extends Paintable> paintableType) {
        if (eagerPaintables == null) {
            init();
        }
        if (eagerPaintables.contains(paintableType)) {
            return LoadStyle.EAGER;
        }
        if (lazyPaintables.contains(paintableType)) {
            return LoadStyle.LAZY;
        }
        if (deferredPaintables.contains(paintableType)) {
            return LoadStyle.DEFERRED;
        }
        return super.getLoadStyle(paintableType);
    }

    private void init() {
        Class<? extends Paintable>[] eagerComponents = getEagerComponents();
        if (eagerComponents != null) {
            for (Class<? extends Paintable> class1 : eagerComponents) {
                eagerPaintables.add(class1);
            }
        }
        Class<? extends Paintable>[] lazyComponents = getEagerComponents();
        if (lazyComponents != null) {
            for (Class<? extends Paintable> class1 : lazyComponents) {
                lazyPaintables.add(class1);
            }
        }
        Class<? extends Paintable>[] deferredComponents = getEagerComponents();
        if (deferredComponents != null) {
            for (Class<? extends Paintable> class1 : deferredComponents) {
                deferredPaintables.add(class1);
            }
        }
    }

    /**
     * @return an array of components whose load style should be overridden to
     *         {@link LoadStyle#EAGER}
     */
    protected abstract Class<? extends Paintable>[] getEagerComponents();

    /**
     * @return an array of components whose load style should be overridden to
     *         {@link LoadStyle#LAZY}
     */
    protected abstract Class<? extends Paintable>[] getLazyComponents();

    /**
     * @return an array of components whose load style should be overridden to
     *         {@link LoadStyle#DEFERRED}
     */
    protected abstract Class<? extends Paintable>[] getDeferredComponents();

}
