package abbot.swt.tester;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.tester.WidgetTester.Textable;
import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.Result;
import abbot.swt.utilities.Displays.StringResult;

/**
 * A tester for {@link Decorations}.
 * <p>
 * TODO Make this class abstract. Clients should never directly access a {@link Decorations}. In
 * fact, {@link Decorations} was intended to be abstract (see its class comments). Consequently,
 * {@link DecorationsTester} should also be abstract.
 */
public class DecorationsTester extends CanvasTester implements Textable {

	/**
	 * Factory method.
	 */
	public static DecorationsTester getDecorationsTester() {
		return (DecorationsTester) getTester(Decorations.class);
	}

	/**
	 * Constructs a new {@link DecorationsTester} associated with the specified
	 * {@link abbot.swt.Robot}.
	 */
	public DecorationsTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * Proxy for {@link Decorations#getBounds()}. <p/>
	 * 
	 * @param decorations
	 *            the decorations under test.
	 * @return the bounds.
	 */
	public Rectangle getBounds(final Decorations decorations) {
		checkWidget(decorations);
		return (Rectangle) syncExec(new Result() {
			public Object result() {
				return decorations.getBounds();
			}
		});
	}

	/**
	 * Proxy for {@link Decorations#getClientArea()}. <p/>
	 * 
	 * @param decorations
	 *            the decorations under test.
	 * @return the client area bounds.
	 */
	public Rectangle getClientArea(final Decorations decorations) {
		checkWidget(decorations);
		return (Rectangle) syncExec(new Result() {
			public Object result() {
				return decorations.getClientArea();
			}
		});
	}

	/**
	 * Proxy for {@link Decorations#getDefaultButton()}. <p/>
	 * 
	 * @param decorations
	 *            the decorations under test.
	 * @return the default button.
	 */
	public Button getDefaultButton(final Decorations decorations) {
		checkWidget(decorations);
		return (Button) syncExec(new Result() {
			public Object result() {
				return decorations.getDefaultButton();
			}
		});
	}

	/**
	 * Proxy for {@link Decorations#getImage()}. <p/>
	 * 
	 * @param decorations
	 *            the decorations under test.
	 * @return the image.
	 */
	public Image getImage(final Decorations decorations) {
		checkWidget(decorations);
		return (Image) syncExec(new Result() {
			public Object result() {
				return decorations.getImage();
			}
		});
	}

	/**
	 * Proxy for {@link Decorations#getImages()}. <p/>
	 * 
	 * @param decorations
	 *            the decorations under test.
	 * @return the images.
	 */
	public Image[] getImages(final Decorations decorations) {
		checkWidget(decorations);
		return (Image[]) syncExec(new Result() {
			public Object result() {
				return decorations.getImages();
			}
		});
	}

	/**
	 * Proxy for {@link Decorations#getLocation()}. <p/>
	 * 
	 * @param decorations
	 *            the decorations under test.
	 * @return the loacation.
	 */
	public Point getLocation(final Decorations decorations) {
		checkWidget(decorations);
		return (Point) syncExec(new Result() {
			public Object result() {
				return decorations.getLocation();
			}
		});
	}

	/**
	 * Proxy for {@link Decorations#getMaximized()}. <p/>
	 * 
	 * @param decorations
	 *            the decorations under test.
	 * @return the maximized state.
	 */
	public boolean getMaximized(final Decorations decorations) {
		checkWidget(decorations);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return decorations.getMaximized();
			}
		});
	}

	/**
	 * Proxy for {@link Decorations#getMenuBar()}. <p/>
	 * 
	 * @param decorations
	 *            the decorations under test.
	 * @return the menu bar.
	 */
	public Menu getMenuBar(final Decorations decorations) {
		checkWidget(decorations);
		return (Menu) syncExec(new Result() {
			public Object result() {
				return decorations.getMenuBar();
			}
		});
	}

	/**
	 * Proxy for {@link Decorations#getMinimized()}. <p/>
	 * 
	 * @param decorations
	 *            the decorations under test.
	 * @return the minimized state.
	 */
	public boolean getMinimized(final Decorations decorations) {
		checkWidget(decorations);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return decorations.getMinimized();
			}
		});
	}

	/**
	 * Proxy for {@link Decorations#getSize()}. <p/>
	 * 
	 * @param decorations
	 *            the decorations under test.
	 * @return the size of the decorations.
	 */
	public Point getSize(final Decorations decorations) {
		checkWidget(decorations);
		return (Point) syncExec(new Result() {
			public Object result() {
				return decorations.getSize();
			}
		});
	}

	/**
	 * Proxy for {@link Decorations#getText()}. <p/>
	 * 
	 * @param decorations
	 *            the decorations under test.
	 * @return the text.
	 */
	public String getText(final Decorations decorations) {
		checkWidget(decorations);
		return syncExec(new StringResult() {
			public String result() {
				return decorations.getText();
			}
		});
	}

	/**
	 * @see Textable#getText(Widget)
	 */
	public String getText(Widget widget) {
		return getText((Decorations) widget);
	}

	public boolean isTextEditable(Widget widget) {
		return false;
	}
}
