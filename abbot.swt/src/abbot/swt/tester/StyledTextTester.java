package abbot.swt.tester;

import junit.framework.Assert;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.WidgetFinderImpl;
import abbot.swt.finder.WidgetFinder;
import abbot.swt.finder.generic.MultipleFoundException;
import abbot.swt.finder.generic.NotFoundException;
import abbot.swt.finder.matchers.WidgetClassMatcher;
import abbot.swt.finder.matchers.NameMatcher;
import abbot.swt.finder.matchers.WidgetTextMatcher;
import abbot.swt.finder.matchers.WidgetMatcher;
import abbot.swt.tester.WidgetTester.Textable;
import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;
import abbot.swt.utilities.Displays.StringResult;

/**
 * A tester for {@link StyledText}styledText.
 * 
 * @author nntp_ds@fastmail.fm
 */
public class StyledTextTester extends CanvasTester implements Textable {
	/**
	 * Factory method.
	 */
	public static StyledTextTester getStyledTextTester() {
		return (StyledTextTester) getTester(StyledText.class);
	}

	/**
	 * Constructs a new {@link StyledTextTester} associated with the specified
	 * {@link abbot.swt.Robot}.
	 */
	public StyledTextTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * Proxy for {@link StyledText#addSelectionListener(SelectionListener listener)}.
	 */
	public void addSelectionListener(final StyledText styledText, final SelectionListener listener) {
		checkWidget(styledText);
		syncExec(new Runnable() {
			public void run() {
				styledText.addSelectionListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link StyledText#getCaretOffset()}.
	 */
	public int getCaretOffset(final StyledText styledText) {
		checkWidget(styledText);
		return syncExec(new IntResult() {
			public int result() {
				return styledText.getCaretOffset();
			}
		});
	}

	/**
	 * Proxy for {@link StyledText#getCharCount()}.
	 */
	public int getCharCount(final StyledText styledText) {
		checkWidget(styledText);
		return syncExec(new IntResult() {
			public int result() {
				return styledText.getCharCount();
			}
		});
	}

	/**
	 * Proxy for {@link StyledText#getDoubleClickEnabled()}.
	 */
	public boolean getDoubleClickEnabled(final StyledText styledText) {
		checkWidget(styledText);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return styledText.getDoubleClickEnabled();
			}
		});
	}

	/**
	 * Proxy for {@link StyledText#getEditable()}.
	 */
	public boolean getEditable(final StyledText styledText) {
		checkWidget(styledText);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return styledText.getEditable();
			}
		});
	}

	/**
	 * Proxy for {@link StyledText#getEnabled()}.
	 */
	public boolean getEnabled(final StyledText styledText) {
		checkWidget(styledText);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return styledText.getEnabled();
			}
		});
	}

	/**
	 * Proxy for {@link StyledText#getLineCount()}.
	 */
	public int getLineCount(final StyledText styledText) {
		checkWidget(styledText);
		return syncExec(new IntResult() {
			public int result() {
				return styledText.getLineCount();
			}
		});
	}

	/**
	 * Proxy for {@link StyledText#getLineDelimiter()}.
	 */
	public String getLineDelimiter(final StyledText styledText) {
		checkWidget(styledText);
		return syncExec(new StringResult() {
			public String result() {
				return styledText.getLineDelimiter();
			}
		});
	}

	/**
	 * Proxy for {@link StyledText#getLineHeight()}.
	 */
	public int getLineHeight(final StyledText styledText) {
		checkWidget(styledText);
		return syncExec(new IntResult() {
			public int result() {
				return styledText.getLineHeight();
			}
		});
	}

	/**
	 * Proxy for {@link StyledText#getSelection()}.
	 */
	public Point getSelection(final StyledText styledText) {
		checkWidget(styledText);
		return (Point) syncExec(new Result() {
			public Object result() {
				return styledText.getSelection();
			}
		});
	}

	/**
	 * Proxy for {@link StyledText#getSelectionCount()}.
	 */
	public int getSelectionCount(final StyledText styledText) {
		checkWidget(styledText);
		return syncExec(new IntResult() {
			public int result() {
				return styledText.getSelectionCount();
			}
		});
	}

	/**
	 * Proxy for {@link StyledText#getSelectionText()}.
	 */
	public String getSelectionText(final StyledText styledText) {
		checkWidget(styledText);
		return syncExec(new StringResult() {
			public String result() {
				return styledText.getSelectionText();
			}
		});
	}

	/**
	 * Proxy for {@link StyledText#getTabs()}.
	 */
	public int getTabs(final StyledText styledText) {
		checkWidget(styledText);
		return syncExec(new IntResult() {
			public int result() {
				return styledText.getTabs();
			}
		});
	}

	/**
	 * Proxy for {@link StyledText#getText()}.
	 */
	public String getText(final StyledText styledText) {
		checkWidget(styledText);
		return syncExec(new StringResult() {
			public String result() {
				return styledText.getText();
			}
		});
	}

	/**
	 * @see Textable#getText(Widget)
	 */
	public String getText(Widget widget) {
		return getText((StyledText) widget);
	}

	public boolean isTextEditable(Widget widget) {
		return getEditable((StyledText) widget);
	}

	/**
	 * Proxy for {@link StyledText#getText(int,int)}.
	 */
	public String getText(final StyledText styledText, final int start, final int end) {
		checkWidget(styledText);
		return syncExec(new StringResult() {
			public String result() {
				return styledText.getText(start, end);
			}
		});
	}

	/**
	 * Proxy for {@link StyledText#getTextLimit()}.
	 */
	public int getTextLimit(final StyledText styledText) {
		checkWidget(styledText);
		return syncExec(new IntResult() {
			public int result() {
				return styledText.getTextLimit();
			}
		});
	}

	/**
	 * Proxy for {@link StyledText#getTopIndex()}.
	 */
	public int getTopIndex(final StyledText styledText) {
		checkWidget(styledText);
		return syncExec(new IntResult() {
			public int result() {
				return styledText.getTopIndex();
			}
		});
	}

	/**
	 * Proxy for {@link StyledText#getTopPixel()}.
	 */
	public int getTopPixel(final StyledText styledText) {
		checkWidget(styledText);
		return syncExec(new IntResult() {
			public int result() {
				return styledText.getTopPixel();
			}
		});
	}

	/**
	 * Proxy for {@link StyledText#removeSelectionListener(SelectionListener listener)}.
	 */
	public void removeSelectionListener(final StyledText styledText,
			final SelectionListener listener) {
		checkWidget(styledText);
		syncExec(new Runnable() {
			public void run() {
				styledText.removeSelectionListener(listener);
			}
		});
	}

	public void actionEnterText(final StyledText styledText, final String text) {
		checkWidget(styledText);
		// @todo: actionEnterTest should use keystrokes
		actionFocus(styledText);
		syncExec(new Runnable() {
			public void run() {
				styledText.setText(text);
			}
		});
		actionWaitForIdle();
	}

	public void actionSelect(final StyledText styledText, final int start, final int end) {
		checkWidget(styledText);
		actionFocus(styledText);
		syncExec(new Runnable() {
			public void run() {
				styledText.setSelection(start, end);
			}
		});
		actionWaitForIdle();
	}

	public boolean assertTextEquals(StyledText styledText, String expectedString) {
		checkWidget(styledText);
		String string = getText(styledText);
		if (string == null)
			return expectedString == null;
		return string.equals(expectedString);
	}

	// Did NOT implement click(StyledText,int)...
	// TODO_Kevin: MAYBE HAVE AN ASSERTION FOR A RANGE OF TEXT???

	// TODO_TOM: copy/mod of method in TextTester
	/**
	 * Get an instrumented <code>StyledText</code> from its <code>id</code> Because we
	 * instrumented it, we assume it not only can be found, but is unique, so we don't even try to
	 * catch the *Found exceptions. CONTRACT: instrumented <code>StyledText</code> must be unique
	 * and findable with param.
	 */
	public static StyledText getInstrumentedStyledText(String id) {
		return getInstrumentedStyledText(id, null);
	}

	/**
	 * Get an instrumented <code>StyledText</code> from its <code>id</code> and the
	 * <code>title</code> of its shell (e.g. of the wizard containing it). Because we instrumented
	 * it, we assume it not only can be found, but is unique, so we don't even try to catch the
	 * *Found exceptions. CONTRACT: instrumented <code>StyledText</code> must be unique and
	 * findable with param.
	 */
	public static StyledText getInstrumentedStyledText(String id, String title) {
		return getInstrumentedStyledText(id, title, null);
	}

	/**
	 * Get an instrumented <code>StyledText</code> from its
	 * <ol>
	 * <li><code>id</code></li>
	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
	 * </ol>
	 * Because we instrumented it, we assume it not only can be found, but is unique, so we don't
	 * even try to catch the *Found exceptions. CONTRACT: instrumented <code>StyledText</code>
	 * must be unique and findable with param.
	 */
	public static StyledText getInstrumentedStyledText(String id, String title, String text) {
		return getInstrumentedStyledText(id, title, text, null);
	}

	/**
	 * Get an instrumented <code>StyledText</code> from its
	 * <ol>
	 * <li><code>id</code></li>
	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
	 * <li><code>shell</code> that contains it</li>
	 * </ol>
	 * Because we instrumented it, we assume it not only can be found, but is unique, so we don't
	 * even try to catch the *Found exceptions. CONTRACT: instrumented <code>StyledText</code>
	 * must be unique and findable with param.
	 */
	// Ported to new-style by tlroche
	public static StyledText getInstrumentedStyledText(String id, String title, String text,
			Shell shell) {
		// WidgetReference ref =
		// new InstrumentedStyledTextReference(id, null, title, text);
		WidgetFinder finder = WidgetFinderImpl.getDefault();
		StyledText t = null;
		if (shell == null) {
			try {
				/* try to find the shell */
				shell = (Shell) finder.find(new WidgetTextMatcher(title));
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
		WidgetMatcher stMatcher;
		if (id != null) {
			stMatcher = new NameMatcher(id);
		} else {
			stMatcher = new WidgetTextMatcher(text);
		}
		try {
			if (shell == null) {
				t = (StyledText) finder.find(stMatcher);
			} else {
				t = (StyledText) finder.find(shell, stMatcher);
			}
		} catch (NotFoundException nf) {
			Assert.fail("no instrumented StyledText \"" + id + "\" found");
		} catch (MultipleFoundException mf) {
			Assert.fail("many instrumented StyledTexts \"" + id + "\" found");
		}
		Assert.assertNotNull("ERROR: null StyledText", t);
		return t;
	}

}
