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

import com.vaadin.terminal.gwt.client.ui.VAccordion;

/**
 * An accordion is a component similar to a {@link TabSheet}, but with a
 * vertical orientation and the selected component presented between tabs.
 * 
 * Closable tabs are not supported by the accordion.
 * 
 * The {@link Accordion} can be styled with the .v-accordion, .v-accordion-item,
 * .v-accordion-item-first and .v-accordion-item-caption styles.
 * 
 * @see TabSheet
 */
@SuppressWarnings("serial")
@ClientWidget(VAccordion.class)
public class Accordion extends TabSheet {

}
