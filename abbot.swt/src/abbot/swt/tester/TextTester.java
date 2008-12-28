package abbot.swt.tester;

import junit.framework.Assert;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
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
import abbot.swt.utilities.Displays.CharResult;
import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;
import abbot.swt.utilities.Displays.StringResult;

/**
 * A tester for {@link Text}s.
 */
public class TextTester extends ScrollableTester implements Textable {

	/**
	 * Factory method.
	 */
	public static TextTester getTextTester() {
		return (TextTester) WidgetTester.getTester(Text.class);
	}

	/**
	 * Constructs a new {@link TextTester} associated with the specified {@link abbot.swt.Robot}.
	 */
	public TextTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * Proxy for {@link Text#getCaretLineNumber()}. <p/>
	 * 
	 * @param text
	 *            the text under test.
	 * @return the caret line number.
	 */
	public int getCaretLineNumber(final Text text) {
		checkWidget(text);
		return syncExec(new IntResult() {
			public int result() {
				return text.getCaretLineNumber();
			}
		});
	}

	/**
	 * Proxy for {@link Text#getCaretLocation()}. <p/> Currently returns null. There is a bug with
	 * SWT 3.0 that causes all sorts of trouble when text.getCaretLocation() is called.
	 * 
	 * @param text
	 *            the text under test.
	 * @return The caret location.
	 */
	public Point getCaretLocation(final Text text) {
		checkWidget(text);
		return (Point) syncExec(new Result() {
			public Object result() {
				return text.getCaretLocation();
			}
		});
	}

	/**
	 * Proxy for {@link Text#getCaretPosition()}. <p/>
	 * 
	 * @param text
	 *            the text under test.
	 * @return the caret position.
	 */
	public int getCaretPosition(final Text text) {
		checkWidget(text);
		return syncExec(new IntResult() {
			public int result() {
				return text.getCaretPosition();
			}
		});
	}

	/**
	 * Proxy for {@link Text#getCharCount()}. <p/>
	 * 
	 * @param text
	 *            the text under test.
	 * @return the number of characters entered.
	 */
	public int getCharCount(final Text text) {
		checkWidget(text);
		return syncExec(new IntResult() {
			public int result() {
				return text.getCharCount();
			}
		});
	}

	/**
	 * Proxy for {@link Text#getDoubleClickEnabled()}. <p/>
	 * 
	 * @param text
	 *            the text under test.
	 * @return true if double click default selection is enabled.
	 */
	public boolean getDoubleClickEnabled(final Text text) {
		checkWidget(text);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return text.getDoubleClickEnabled();
			}
		});
	}

	/**
	 * Proxy for {@link Text#getEchoChar()}. <p/>
	 * 
	 * @param text
	 *            the text under test.
	 * @return teh echo character.
	 */
	public char getEchoChar(final Text text) {
		checkWidget(text);
		return syncExec(new CharResult() {
			public char result() {
				return text.getEchoChar();
			}
		});
	}

	/**
	 * Proxy for {@link Text#getEditable()}. <p/>
	 * 
	 * @param text
	 *            the text under test.
	 * @return true if the text is editable.
	 */
	public boolean getEditable(final Text text) {
		checkWidget(text);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return text.getEditable();
			}
		});
	}

	/**
	 * Proxy for {@link Text#getLineCount()}. <p/>
	 * 
	 * @param text
	 *            the text under test.
	 * @return the number of lines entered.
	 */
	public int getLineCount(final Text text) {
		checkWidget(text);
		return syncExec(new IntResult() {
			public int result() {
				return text.getLineCount();
			}
		});
	}

	/**
	 * Proxy for {@link Text#getLineDelimiter()}. <p/>
	 * 
	 * @param text
	 *            the text under test.
	 * @return the line delimiter.
	 */
	public String getLineDelimiter(final Text text) {
		checkWidget(text);
		return syncExec(new StringResult() {
			public String result() {
				return text.getLineDelimiter();
			}
		});
	}

	/**
	 * Proxy for {@link Text#getLineHeight()}. <p/>
	 * 
	 * @param text
	 *            the text under test.
	 * @return the line height.
	 */
	public int getLineHeight(final Text text) {
		checkWidget(text);
		return syncExec(new IntResult() {
			public int result() {
				return text.getLineHeight();
			}
		});
	}

	/**
	 * Proxy for {@link Text#getSelection()}. <p/>
	 * 
	 * @param text
	 *            the text under test.
	 * @return the start and the end of the selection.
	 */
	public Point getSelection(final Text text) {
		checkWidget(text);
		return (Point) syncExec(new Result() {
			public Object result() {
				return text.getSelection();
			}
		});
	}

	/**
	 * Proxy for {@link Text#getSelectionCount()}. <p/>
	 * 
	 * @param text
	 *            the text under test.
	 * @return the number of selected characters.
	 */
	public int getSelectionCount(final Text text) {
		checkWidget(text);
		return syncExec(new IntResult() {
			public int result() {
				return text.getSelectionCount();
			}
		});
	}

	/**
	 * Proxy for {@link Text#getSelectionText()}. <p/>
	 * 
	 * @param text
	 *            the text under test.
	 * @return the selected text.
	 */
	public String getSelectionText(final Text text) {
		checkWidget(text);
		return syncExec(new StringResult() {
			public String result() {
				return text.getSelectionText();
			}
		});
	}

	/**
	 * Proxy for {@link Text#getTabs()}. <p/>
	 * 
	 * @param text
	 *            the text under test.
	 * @return the number of tab characters.
	 */
	public int getTabs(final Text text) {
		checkWidget(text);
		return syncExec(new IntResult() {
			public int result() {
				return text.getTabs();
			}
		});
	}

	/**
	 * Proxy for {@link Text#getText()}. <p/>
	 * 
	 * @param text
	 *            the text under test.
	 * @return the text entered.
	 */
	public String getText(final Text text) {
		checkWidget(text);
		return syncExec(new StringResult() {
			public String result() {
				return text.getText();
			}
		});
	}

	/**
	 * @see Textable#getText(Widget)
	 */
	public String getText(Widget widget) {
		return getText((Text) widget);
	}

	public boolean isTextEditable(Widget widget) {
		return getEditable((Text) widget);
	}

	/**
	 * Proxy for {@link Text#getText(int, int)}. <p/>
	 * 
	 * @param text
	 *            the text under test.
	 * @param start
	 *            the start of the range.
	 * @param end
	 *            the end of the range.
	 * @return the text between start and end.
	 */
	public String getText(final Text text, final int start, final int end) {
		checkWidget(text);
		return syncExec(new StringResult() {
			public String result() {
				return text.getText(start, end);
			}
		});
	}

	/**
	 * Proxy for {@link Text#getTextLimit()}. <p/>
	 * 
	 * @param text
	 *            the text under test.
	 * @return the text limit.
	 */
	public int getTextLimit(final Text text) {
		checkWidget(text);
		return syncExec(new IntResult() {
			public int result() {
				return text.getTextLimit();
			}
		});
	}

	/**
	 * Proxy for {@link Text#getTopIndex()}. <p/>
	 * 
	 * @param text
	 *            the text under test.
	 * @return the index of the top line.
	 */
	public int getTopIndex(final Text text) {
		checkWidget(text);
		return syncExec(new IntResult() {
			public int result() {
				return text.getTopIndex();
			}
		});
	}

	/**
	 * Proxy for {@link Text#getTopPixel()}. <p/>
	 * 
	 * @param text
	 *            the text under test.
	 * @return the pixel position of the top line.
	 */
	public int getTopPixel(final Text text) {
		checkWidget(text);
		return syncExec(new IntResult() {
			public int result() {
				return text.getTopPixel();
			}
		});
	}

	/**
	 * @deprecated Cheater!  Use {@link #actionKeyString(Text, String)}.
	 * @param text
	 *            a Text
	 * @param string
	 *            a String
	 */
	public void actionEnterText(final Text text, final String string) {
		checkWidget(text);
		actionFocus(text);
		syncExec(new Runnable() {
			public void run() {
				text.setText(string);
			}
		});
		actionWaitForIdle();
	}

	public void actionKeyString(Text text, String string) {
		actionClick(text);
		actionKeyString(string);
	}

	public void actionSelect(final Text text, final int start, final int end) {
		checkWidget(text);
		actionFocus(text);
		syncExec(new Runnable() {
			public void run() {
				text.setSelection(start, end);
			}
		});
		actionWaitForIdle();
	}

	//	
	// public synchronized String getText(final Text widget){
	// objT = null;
	// widget.getDisplay().syncExec(new Runnable(){
	// public void run(){
	// objT = widget.getText();
	// }
	// });
	//		
	// return (String)objT;
	// }
	//	
	// public String getText(Text widget, int start, int end){
	// String text = getText(widget);
	// if(text==null)
	// return null;
	// return text.substring(start,end);
	// }
	//	
	public boolean assertTextEquals(Text text, String expectedString) {
		checkWidget(text);
		String widgetString = getText(text);
		if (widgetString == null)
			return expectedString == null;
		return widgetString.equals(expectedString);
	}

	// Did NOT implement click(Text,int)...
	// TODO_Kevin: MAYBE HAVE AN ASSERTION FOR A RANGE OF TEXT???

	/**
	 * Get an instrumented <code>Text</code> from its <code>id</code> Because we instrumented
	 * it, we assume it not only can be found, but is unique, so we don'text even try to catch the
	 * *Found exceptions. CONTRACT: instrumented <code>Text</code> must be unique and findable
	 * with param.
	 */
	public static Text getInstrumentedText(String id) {
		return getInstrumentedText(id, null);
	}

	/**
	 * Get an instrumented <code>Text</code> from its <code>id</code> and the <code>title</code>
	 * of its shell (e.g. of the wizard containing it). Because we instrumented it, we assume it not
	 * only can be found, but is unique, so we don'text even try to catch the *Found exceptions.
	 * CONTRACT: instrumented <code>Text</code> must be unique and findable with param.
	 */
	public static Text getInstrumentedText(String id, String title) {
		return getInstrumentedText(id, title, null);
	}

	/**
	 * Get an instrumented <code>Text</code> from its
	 * <ol>
	 * <li><code>id</code></li>
	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
	 * </ol>
	 * Because we instrumented it, we assume it not only can be found, but is unique, so we don'text
	 * even try to catch the *Found exceptions. CONTRACT: instrumented <code>Text</code> must be
	 * unique and findable with param.
	 */
	public static Text getInstrumentedText(String id, String title, String text) {
		return getInstrumentedText(id, title, text, null);
	}

	/**
	 * Get an instrumented <code>Text</code> from its
	 * <ol>
	 * <li><code>id</code></li>
	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
	 * <li><code>shell</code> that contains it</li>
	 * </ol>
	 * Because we instrumented it, we assume it not only can be found, but is unique, so we don'text
	 * even try to catch the *Found exceptions. CONTRACT: instrumented <code>Text</code> must be
	 * unique and findable with param.
	 */
	public static Text getInstrumentedText(String id, String title, String text, Shell shell) {
		Text ret = null;
		try {
			ret = catchInstrumentedText(id, title, text, shell);
		} catch (NotFoundException nf) {
			Assert.fail("no instrumented Text \"" + id + "\" found");
		} catch (MultipleFoundException mf) {
			Assert.fail("many instrumented Texts \"" + id + "\" found");
		}
		Assert.assertNotNull("ERROR: null instrumented Text", ret);
		return ret;
	}

	/**
	 * Get an instrumented <code>Text</code>. Look in its
	 * <ol>
	 * <li><code>id</code></li>
	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
	 * <li><code>shell</code> that contains it</li>
	 * </ol>
	 * but don'text assume it can only be found!
	 */
	public static Text catchInstrumentedText(String id, String title, String text, Shell shell)
			throws NotFoundException, MultipleFoundException {
		// WidgetReference ref = new InstrumentedTextReference(id, null, title, text);
		Text ret = null;

		WidgetFinder finder = WidgetFinderImpl.getDefault();
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
		WidgetMatcher textMatcher;
		if (id != null) {
			textMatcher = new NameMatcher(id);
		} else {
			textMatcher = new WidgetTextMatcher(text);
		}
		try {
			if (shell == null) {
				ret = (Text) finder.find(textMatcher);
			} else {
				ret = (Text) finder.find(shell, textMatcher);
			}
		} catch (NotFoundException nf) {
			Assert.fail("no instrumented Text \"" + id + "\" found");
		} catch (MultipleFoundException mf) {
			Assert.fail("many instrumented Texts \"" + id + "\" found");
		}

		// if (shell == null) {
		// ret = DefaultWidgetFinder.findText(ref);
		// } else {
		// ret = DefaultWidgetFinder.findTextInShell(ref, shell);
		// }
		return ret;
	}

}
