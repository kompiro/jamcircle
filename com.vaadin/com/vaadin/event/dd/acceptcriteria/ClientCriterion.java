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
package com.vaadin.event.dd.acceptcriteria;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.terminal.gwt.client.ui.dd.VAcceptCriterion;
import com.vaadin.ui.ClientWidget;

/**
 * An annotation type used to point the client side counterpart for server side
 * a {@link AcceptCriterion} class. Usage is pretty similar to
 * {@link ClientWidget} which is used with Vaadin components that have a
 * specialized client side counterpart.
 * <p>
 * Annotations are used at GWT compilation phase, so remember to rebuild your
 * widgetset if you do changes for {@link ClientCriterion} mappings.
 * 
 * @since 6.3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ClientCriterion {
    /**
     * @return the client side counterpart for the annotated criterion
     */
    Class<? extends VAcceptCriterion> value();
}
