package abbot.swt.tester;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.Result;

/**
 * A tester for {@link Caret}s.
 * 
 * @author gjohnsto
 */
public class CaretTester extends WidgetTester {

	/**
	 * Factory method.
	 */
	public static CaretTester getCaretTester() {
		return (CaretTester) getTester(Caret.class);
	}

	/**
	 * Constructs a new {@link CaretTester} associated with the specified {@link abbot.swt.Robot}.
	 */
	CaretTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * Proxy for {@link Caret#getBounds()}.
	 */
	public Rectangle getBounds(final Caret caret) {
		checkWidget(caret);
		return (Rectangle) syncExec(new Result() {
			public Object result() {
				return caret.getBounds();
			}
		});
	}

	/**
	 * Proxy for {@link Caret#getFont()}.
	 */
	public Font getFont(final Caret caret) {
		checkWidget(caret);
		return (Font) syncExec(new Result() {
			public Object result() {
				return caret.getFont();
			}
		});
	}

	/**
	 * Proxy for {@link Caret#getImage()}.
	 */
	public Image getImage(final Caret caret) {
		checkWidget(caret);
		return (Image) syncExec(new Result() {
			public Object result() {
				return caret.getImage();
			}
		});
	}

	/**
	 * Proxy for {@link Caret#getLocation()}.
	 */
	public Point getLocation(final Caret caret) {
		checkWidget(caret);
		return (Point) syncExec(new Result() {
			public Object result() {
				return caret.getLocation();
			}
		});
	}

	/**
	 * Proxy for {@link Caret#getParent()}.
	 */
	public Canvas getParent(final Caret caret) {
		checkWidget(caret);
		return (Canvas) syncExec(new Result() {
			public Object result() {
				return caret.getParent();
			}
		});
	}

	/**
	 * Proxy for {@link Caret#getSize()}.
	 */
	public Point getSize(final Caret caret) {
		checkWidget(caret);
		return (Point) syncExec(new Result() {
			public Object result() {
				return caret.getSize();
			}
		});
	}

	/**
	 * Proxy for {@link Caret#getVisible()}.
	 */
	public boolean getVisible(final Caret caret) {
		checkWidget(caret);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return caret.getVisible();
			}
		});
	}

	/**
	 * Proxy for {@link Caret#isVisible()}.
	 */
	public boolean isVisible(final Caret caret) {
		checkWidget(caret);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return caret.isVisible();
			}
		});
	}
	
	public boolean isVisible(Widget widget) {
		if (widget instanceof Caret)
			return isVisible((Caret) widget);
		return super.isVisible(widget);
	}

	/**
	 * Proxy for {@link Caret#setBounds (int x, int y, int width, int height)}.
	 */
	public void setBounds(final Caret caret, final int x, final int y, final int width,
			final int height) {
		checkWidget(caret);
		syncExec(new Runnable() {
			public void run() {
				caret.setBounds(x, y, width, height);
			}
		});
	}

	/**
	 * Proxy for {@link Caret#setBounds (Rectangle rectangle)}.
	 */
	public void setBounds(final Caret caret, final Rectangle rectangle) {
		checkWidget(caret);
		syncExec(new Runnable() {
			public void run() {
				caret.setBounds(rectangle);
			}
		});
	}

	/**
	 * Proxy for {@link Caret#setFont (Font font)}.
	 */
	public void setFont(final Caret caret, final Font font) {
		checkWidget(caret);
		syncExec(new Runnable() {
			public void run() {
				caret.setFont(font);
			}
		});
	}

	/**
	 * Proxy for {@link Caret#setImage(Image)}.
	 */
	public void setImage(final Caret caret, final Image image) {
		checkWidget(caret);
		syncExec(new Runnable() {
			public void run() {
				caret.setImage(image);
			}
		});
	}

	/**
	 * Proxy for {@link Caret#setLocation (int x, int y)}.
	 */
	public void setLocation(final Caret caret, final int x, final int y) {
		checkWidget(caret);
		syncExec(new Runnable() {
			public void run() {
				caret.setLocation(x, y);
			}
		});
	}

	/**
	 * Proxy for {@link Caret#setLocation (Point p)}.
	 */
	public void setLocation(final Caret caret, final Point p) {
		checkWidget(caret);
		syncExec(new Runnable() {
			public void run() {
				caret.setLocation(p);
			}
		});
	}

	/**
	 * Proxy for {@link Caret#setSize (int x, int y)}.
	 */
	public void setSize(final Caret caret, final int x, final int y) {
		checkWidget(caret);
		syncExec(new Runnable() {
			public void run() {
				caret.setSize(x, y);
			}
		});
	}

	/**
	 * Proxy for {@link Caret#setLocation (Point p)}.
	 */
	public void setSize(final Caret caret, final Point p) {
		checkWidget(caret);
		syncExec(new Runnable() {
			public void run() {
				caret.setSize(p);
			}
		});
	}

	/**
	 * Proxy for {@link Caret#setVisible (boolean visible)}.
	 */
	public void setVisible(final Caret caret, final boolean visible) {
		checkWidget(caret);
		syncExec(new Runnable() {
			public void run() {
				caret.setVisible(visible);
			}
		});
	}
}
