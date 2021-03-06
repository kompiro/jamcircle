package org.kompiro.swtbot.extension.jface;

import java.lang.annotation.*;

import org.eclipse.jface.wizard.IWizard;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface WithWizard {
	Class<? extends IWizard> value();

}
