package abbot.swt.tester;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Caret;

import abbot.swt.utilities.Displays;
import abbot.swt.utilities.Displays.Result;

/**
 * Provides widget-specific actions, assertions, and getter methods for widgets of type Canvas.
 */
public class CanvasTester extends CompositeTester {

	/**
	 * Factory method.
	 */
	public static CanvasTester getCanvasTester() {
		return (CanvasTester) getTester(Canvas.class);
	}

	/**
	 * Constructs a new {@link CanvasTester} associated with the specified {@link abbot.swt.Robot}.
	 */
	public CanvasTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/*
	 * These getter methods return a particular property of the given widget.
	 * 
	 * @see the corresponding member function in class Widget
	 */
	/* Begin getters */
	/**
	 * Proxy for {@link Canvas#getCaret()}. <p/>
	 * 
	 * @param canvas
	 *            the canvas under test.
	 * @return the caret.
	 */
	public Caret getCaret(final Canvas canvas) {
		return (Caret) Displays.syncExec(canvas.getDisplay(), new Result() {
			public Object result() {
				return canvas.getCaret();
			}
		});
	}

	/* End getters */

	/**
	 * Proxy for
	 * {@link Canvas#scroll(int, int, int, int, int, int, boolean)}.
	 */
	public void scroll(final Canvas c, final int destX, final int destY, final int x, final int y,
			final int width, final int height, final boolean all) {
		syncExec(new Runnable() {
			public void run() {
				c.scroll(destX, destY, x, y, width, height, all);
			}
		});
	}

	/**
	 * Proxy for {@link Canvas#setCaret(Caret caret)}.
	 */
	public void setCaret(final Canvas c, final Caret caret) {
		syncExec(new Runnable() {
			public void run() {
				c.setCaret(caret);
			}
		});
	}

	/**
	 * Proxy for {@link Canvas#setFont(Font font)}
	 */
	public void setFont(final Canvas c, final Font font) {
		syncExec(new Runnable() {
			public void run() {
				c.setFont(font);
			}
		});
	}
}
