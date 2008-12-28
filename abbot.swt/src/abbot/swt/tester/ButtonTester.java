package abbot.swt.tester;

import junit.framework.Assert;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.WidgetFinder;
import abbot.swt.finder.WidgetFinderImpl;
import abbot.swt.finder.generic.MultipleFoundException;
import abbot.swt.finder.generic.NotFoundException;
import abbot.swt.finder.matchers.NameMatcher;
import abbot.swt.finder.matchers.WidgetClassMatcher;
import abbot.swt.finder.matchers.WidgetMatcher;
import abbot.swt.finder.matchers.WidgetTextMatcher;
import abbot.swt.tester.WidgetTester.Textable;
import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;
import abbot.swt.utilities.Displays.StringResult;

/**
 * Provides widget-specific actions, assertions, and getter methods for widgets of type Button.
 */
public class ButtonTester extends ControlTester implements Textable {

	/**
	 * Factory method.
	 */
	public static ButtonTester getButtonTester() {
		return (ButtonTester) getTester(Button.class);
	}

	/**
	 * Constructs a new {@link ButtonTester} associated with the specified {@link abbot.swt.Robot}.
	 */
	ButtonTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * Proxy for {@link Button#getAlignment()}.
	 */
	public int getAlignment(final Button button) {
		checkWidget(button);
		return syncExec(new IntResult() {
			public int result() {
				return button.getAlignment();
			}
		});
	}

	/**
	 * Proxy for {@link Button#getImage()}. <p/>
	 * 
	 * @param button
	 *            the button under test.
	 * @return the image on the button.
	 */
	public Image getImage(final Button button) {
		checkWidget(button);
		return (Image) syncExec(new Result() {
			public Object result() {
				return button.getImage();
			}
		});
	}

	/**
	 * Proxy for {@link Button#getSelection()}. <p/>
	 * 
	 * @param button
	 *            the button under test.
	 * @return true if the button is selected
	 */
	public boolean getSelection(final Button button) {
		checkWidget(button);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return button.getSelection();
			}
		});
	}

	/**
	 * Proxy for {@link Button#getText()}. <p/>
	 * 
	 * @param button
	 *            the button under test.
	 * @return the text on the button.
	 */
	public String getText(final Button button) {
		checkWidget(button);
		return syncExec(new StringResult() {
			public String result() {
				return button.getText();
			}
		});
	}

	public String getText(Widget widget) {
		return getText((Button) widget);
	}

	public boolean isTextEditable(Widget widget) {
		return false;
	}

	/**
	 * Get an instrumented <code>Button</code> from its <code>id</code> Because we instrumented
	 * it, we assume it not only can be found, but is unique, so we don't even try to catch the
	 * *Found exceptions. CONTRACT: instrumented <code>Button</code> must be unique and findable
	 * with param.
	 * 
	 * @deprecated this method is moving or going away
	 */
	public static Button getInstrumentedButton(String id) {
		return getInstrumentedButton(id, null);
	}

	// TODO_TOM: copy/mod of method in TextTester
	/**
	 * Get an instrumented <code>Button</code> from its <code>id</code> and the
	 * <code>title</code> of its shell (e.g. of the wizard containing it). Because we instrumented
	 * it, we assume it not only can be found, but is unique, so we don't even try to catch the
	 * *Found exceptions. CONTRACT: instrumented <code>Button</code> must be unique and findable
	 * with param.
	 * 
	 * @deprecated this method is moving or going away
	 */
	public static Button getInstrumentedButton(String id, String title) {
		return getInstrumentedButton(id, title, null);
	}

	// TODO_TOM: copy/mod of method in TextTester
	/**
	 * Get an instrumented <code>Button</code> from its
	 * <ol>
	 * <li><code>id</code></li>
	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
	 * </ol>
	 * Because we instrumented it, we assume it not only can be found, but is unique, so we don't
	 * even try to catch the *Found exceptions. CONTRACT: instrumented <code>Button</code> must be
	 * unique and findable with param.
	 * 
	 * @deprecated this method is moving or going away
	 */
	public static Button getInstrumentedButton(String id, String title, String text) {
		return getInstrumentedButton(id, title, text, null);
	}

	/**
	 * Get an instrumented <code>Button</code> from its
	 * <ol>
	 * <li><code>id</code></li>
	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
	 * <li><code>shell</code> that contains it</li>
	 * </ol>
	 * Because we instrumented it, we assume it not only can be found, but is unique, so we don't
	 * even try to catch the *Found exceptions. CONTRACT: instrumented <code>Button</code> must be
	 * unique and findable with param. TODO: Clean this up.
	 * 
	 * @deprecated this method is moving or going away
	 */
	public static Button getInstrumentedButton(String buttonName, String shellText,
			String buttonText, Shell shell) {
		// WidgetReference ref =
		// new InstrumentedButtonReference(id, null, title, text);
		WidgetFinder finder = WidgetFinderImpl.getDefault();
		Button t = null;
		if (shell == null) {
			try {
				/* try to find the shell */
				shell = (Shell) finder.find(new WidgetTextMatcher(shellText));
			} catch (NotFoundException e) {
				shell = null;
			} catch (MultipleFoundException e) {
				try {
					shell = (Shell) finder.find(new WidgetClassMatcher(Shell.class));
				} catch (NotFoundException e1) {
					shell = null;
				} catch (MultipleFoundException e1) {
					shell = null;
				}
			}
		}
		/* Decide what to search on: first id, then text if id not available */
		WidgetMatcher buttonMatcher;
		if (buttonName != null) {
			buttonMatcher = new NameMatcher(buttonName);
		} else {
			buttonMatcher = new WidgetTextMatcher(buttonText);
		}
		try {
			if (shell == null) {
				t = (Button) finder.find(buttonMatcher);
			} else {
				t = (Button) finder.find(shell, buttonMatcher);
			}
		} catch (NotFoundException nf) {
			Assert.fail("no instrumented Button \"" + buttonName + "\" found");
		} catch (MultipleFoundException mf) {
			Assert.fail("many instrumented Buttons \"" + buttonName + "\" found");
		}
		Assert.assertNotNull("ERROR: null Button", t);
		return t;
	}

	/* End getters */

	/**
	 * Proxy for {@link Button#addSelectionListener(SelectionListener)}.
	 */
	public void addSelectionListener(final Button button, final SelectionListener listener) {
		checkWidget(button);
		syncExec(new Runnable() {
			public void run() {
				button.addSelectionListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Button#removeSelectionListener(SelectionListener)}.
	 */
	public void removeSelectionListener(final Button button, final SelectionListener listener) {
		checkWidget(button);
		syncExec(new Runnable() {
			public void run() {
				button.removeSelectionListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Button#setAlignment(int)}.
	 */
	public void setAlignment(final Button button, final int i) {
		checkWidget(button);
		syncExec(new Runnable() {
			public void run() {
				button.setAlignment(i);
			}
		});
	}

	/**
	 * Proxy for {@link Button#setImage(Image)}.
	 */
	public void setImage(final Button button, final Image i) {
		checkWidget(button);
		syncExec(new Runnable() {
			public void run() {
				button.setImage(i);
			}
		});
	}

	/**
	 * Proxy for {@link Button#setSelection(boolean)}.
	 */
	public void setSelection(final Button button, final boolean selected) {
		checkWidget(button);
		syncExec(new Runnable() {
			public void run() {
				button.setSelection(selected);
			}
		});
	}

	/**
	 * Proxy for {@link Button#setText(String)}.L
	 */
	public void setText(final Button button, final String text) {
		checkWidget(button);
		syncExec(new Runnable() {
			public void run() {
				button.setText(text);
			}
		});
	}
}
