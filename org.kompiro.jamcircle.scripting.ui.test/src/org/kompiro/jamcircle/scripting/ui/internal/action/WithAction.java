package org.kompiro.jamcircle.scripting.ui.internal.action;

import java.lang.annotation.*;

import org.eclipse.jface.action.Action;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface WithAction {
	Class<? extends Action> value();

}
