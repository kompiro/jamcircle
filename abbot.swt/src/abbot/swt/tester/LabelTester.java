package abbot.swt.tester;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Label;
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
import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;
import abbot.swt.utilities.Displays.StringResult;

/**
 * A tester for {@link Label}s.
 */
public class LabelTester extends ControlTester implements Textable {

	/**
	 * Factory method.
	 */
	public static LabelTester getLabelTester() {
		return (LabelTester) getTester(Label.class);
	}

	/**
	 * Constructs a new {@link LabelTester} associated with the specified {@link abbot.swt.Robot}.
	 */
	public LabelTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * Proxy for {@link Label#getAlignment()}.
	 */
	public int getAlignment(final Label label) {
		checkWidget(label);
		return syncExec(new IntResult() {
			public int result() {
				return label.getAlignment();
			}
		});
	}

	// /**
	// * Proxy for {@link Label#getParent()}. <p/>
	// *
	// * @param label
	// * the Label under test.
	// * @return the label's parent.
	// */
	// public Image getParent(final Label label) {checkWidget(label);
	// return (Image) syncExec(new Result() {
	// public Object result() {
	// return label.getParent();
	// }
	// });
	// }

	/**
	 * Proxy for {@link Label#getImage()}. <p/>
	 * 
	 * @param label
	 *            the Label under test.
	 * @return the image on the label.
	 */
	public Image getImage(final Label label) {
		checkWidget(label);
		return (Image) syncExec(new Result() {
			public Object result() {
				return label.getImage();
			}
		});
	}

	/**
	 * Proxy for {@link Label#getText()}. <p/>
	 * 
	 * @param label
	 *            the Label under test.
	 * @return the text on the label.
	 */
	public String getText(final Label label) {
		checkWidget(label);
		return syncExec(new StringResult() {
			public String result() {
				return label.getText();
			}
		});
	}

	/**
	 * @see Textable#getText(Widget)
	 */
	public String getText(Widget widget) {
		return getText((Label) widget);
	}

	public boolean isTextEditable(Widget widget) {
		return false;
	}

	/* End getters */

	public boolean assertTextEquals(Label label, String text) {
		return assertTextEquals(label, text, false);
	}

	/**
	 * Fixes problem observed running CCT under TestCollector: getText(label) returning text minus
	 * trailing whitespace
	 */
	public boolean assertTextEquals(Label label, String textToMatch, boolean trim) {
		checkWidget(label);
		String gotText = getText(label);
		if (gotText == null) {
			return (textToMatch == null);
		} else if (trim) {
			textToMatch = textToMatch.trim();
			gotText = gotText.trim();
		}
		return gotText.equals(textToMatch);
	}

	/**
	 * Get an instrumented <code>Label</code> from its <code>id</code> Because we instrumented
	 * it, we assume it not only can be found, but is unique, so we don't even try to catch the
	 * *Found exceptions. CONTRACT: instrumented <code>Label</code> must be unique and findable
	 * with param.
	 */
	public static Label getInstrumentedLabel(String id) {
		return getInstrumentedLabel(id, null);
	}

	/**
	 * Get an instrumented <code>Label</code> from its <code>id</code> and the
	 * <code>title</code> of its shell (e.g. of the wizard containing it). Because we instrumented
	 * it, we assume it not only can be found, but is unique, so we don't even try to catch the
	 * *Found exceptions. CONTRACT: instrumented <code>Label</code> must be unique and findable
	 * with param.
	 */
	public static Label getInstrumentedLabel(String id, String title) {
		return getInstrumentedLabel(id, title, null);
	}

	/**
	 * Get an instrumented <code>Label</code> from its
	 * <ol>
	 * <li><code>id</code></li>
	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
	 * </ol>
	 * Because we instrumented it, we assume it not only can be found, but is unique, so we don't
	 * even try to catch the *Found exceptions. CONTRACT: instrumented <code>Label</code> must be
	 * unique and findable with param.
	 */
	public static Label getInstrumentedLabel(String id, String title, String text) {
		return getInstrumentedLabel(id, title, text, null);
	}

	/**
	 * Get an instrumented <code>Label</code> from its
	 * <ol>
	 * <li><code>id</code></li>
	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
	 * <li><code>shell</code> that contains it</li>
	 * </ol>
	 * Because we instrumented it, we assume it not only can be found, but is unique, so we don't
	 * even try to catch the *Found exceptions. CONTRACT: instrumented <code>Label</code> must be
	 * unique and findable with param.
	 */
	public static Label getInstrumentedLabel(String id, String title, String text, Shell shell) {
		return catchInstrumentedLabel(id, title, text, shell);
	}

	/**
	 * Get an instrumented <code>Label</code>. Get it from its:
	 * <ol>
	 * <li><code>id</code></li>
	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
	 * <li><code>shell</code> that contains it</li>
	 * </ol>
	 * but don't assume it can only be found!
	 */
	public static Label catchInstrumentedLabel(String id, String title, String text, Shell shell) {

		// Try to find the shell if we don't already have it.
		WidgetFinder finder = WidgetFinderImpl.getDefault();
		if (shell == null) {
			try {
				shell = (Shell) finder.find(new WidgetTextMatcher(title));
			} catch (NotFoundException exception) {
				// Empty block intended.
			} catch (MultipleFoundException exception) {
				try {
					shell = (Shell) finder.find(new WidgetClassMatcher(Shell.class));
				} catch (NotFoundException e1) {
					// Empty block intended.
				} catch (MultipleFoundException e1) {
					// Empty block intended.
				}
			}
		}

		// Decide what to search on: first id, then text if id not available.
		WidgetMatcher labelMatcher = (id == null) ? new WidgetTextMatcher(text) : new NameMatcher(
				id);
		try {
			return (shell == null) ? (Label) finder.find(labelMatcher) : (Label) finder.find(
					shell,
					labelMatcher);
		} catch (NotFoundException nf) {
			// Empty block intended.
		} catch (MultipleFoundException mf) {
			// Empty block intended.
		}

		throw new ActionFailedException("no instrumented label found");
	}

	/**
	 * Proxy for {@link Label#setAlignment(int alignment)}.
	 */
	public void setAlignment(final Label label, final int alignment) {
		checkWidget(label);
		syncExec(new Runnable() {
			public void run() {
				label.setAlignment(alignment);
			}
		});
	}

	/**
	 * Proxy for {@link Label#setImage(Image image)}.
	 */
	public void setImage(final Label label, final Image image) {
		checkWidget(label);
		syncExec(new Runnable() {
			public void run() {
				label.setImage(image);
			}
		});
	}

	/**
	 * Proxy for {@link Label#setText(String string)}.
	 */
	public void setText(final Label label, final String string) {
		checkWidget(label);
		syncExec(new Runnable() {
			public void run() {
				label.setText(string);
			}
		});
	}

}
