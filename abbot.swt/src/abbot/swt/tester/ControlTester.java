package abbot.swt.tester;

import org.eclipse.swt.accessibility.Accessible;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;

/**
 * A tester for {@link Control}s.
 */
public class ControlTester extends WidgetTester {

	/**
	 * Factory method.
	 */
	public static ControlTester getControlTester() {
		return (ControlTester) getTester(Control.class);
	}

	/**
	 * Constructs a new {@link ControlTester} associated with the specified {@link abbot.swt.Robot}.
	 */
	public ControlTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/* Actions */

	/* Actions for invoking a menu item from a control's pop-up menu. */
	
	/**
	 * @see WidgetTester#getMenu(Widget)
	 */
	protected Menu getMenu(Widget widget) {
		if (widget instanceof Control)
			return getMenu((Control) widget);
		return super.getMenu(widget);
	}

	/* Proxies */
	
	public Point computeSize(final Control control, final int wHint, final int hHint) {
		return (Point) syncExec(new Result<Point>() {
			public Point result() {
				return control.computeSize(wHint, hHint);
			}
		});
	}

	/**
	 * Proxy for {@link Control#getAccessible()}.
	 */
	public Accessible getAccessible(final Control control) {
		checkWidget(control);
		checkWidget(control);
		return (Accessible) syncExec(new Result() {
			public Object result() {
				return control.getAccessible();
			}
		});
	}

	/**
	 * Proxy for {@link Control#getBackground()}. <p/>
	 * 
	 * @param control
	 *            the control under test.
	 * @return the background color.
	 */
	public Color getBackground(final Control control) {
		checkWidget(control);
		return (Color) syncExec(new Result() {
			public Object result() {
				return control.getBackground();
			}
		});
	}

	/**
	 * Proxy for {@link Control#getBorderWidth()}. <p/>
	 * 
	 * @param control
	 *            the control under test.
	 * @return the border width.
	 */
	public int getBorderWidth(final Control control) {
		checkWidget(control);
		return syncExec(new IntResult() {
			public int result() {
				return control.getBorderWidth();
			}
		});
	}

	/**
	 * Proxy for {@link Control#getBounds()}. <p/>
	 * 
	 * @param control
	 *            the control under test.
	 * @return the bounds of the widget.
	 */
	public Rectangle getBounds(final Control control) {
		checkWidget(control);
		return (Rectangle) syncExec(new Result() {
			public Object result() {
				return control.getBounds();
			}
		});
	}

	/**
	 * Proxy for {@link Control#getEnabled()}. <p/>
	 * 
	 * @param control
	 *            the control under test.
	 * @return true if the Control is enabled.
	 */
	public boolean getEnabled(final Control control) {
		checkWidget(control);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return control.getEnabled();
			}
		});
	}

	/**
	 * Proxy for {@link Control#getFont()}. <p/>
	 * 
	 * @param control
	 *            the control under test.
	 * @return the font associated with the control.
	 */
	public Font getFont(final Control control) {
		checkWidget(control);
		return (Font) syncExec(new Result() {
			public Object result() {
				return control.getFont();
			}
		});
	}

	/**
	 * Proxy for {@link Control#getForeground()}. <p/>
	 * 
	 * @param control
	 *            the control under test.
	 * @return the foreground color.
	 */
	public Color getForeground(final Control control) {
		checkWidget(control);
		return (Color) syncExec(new Result() {
			public Object result() {
				return control.getForeground();
			}
		});
	}

	/**
	 * Proxy for {@link Control#getLayoutData()}. <p/>
	 * 
	 * @param control
	 *            the control under test.
	 * @return the layout data.
	 */
	public Object getLayoutData(final Control control) {
		checkWidget(control);
		return syncExec(new Result() {
			public Object result() {
				return control.getLayoutData();
			}
		});
	}

	/**
	 * Proxy for {@link Control#getLocation()}. <p/>
	 * 
	 * @param control
	 *            the control under test.
	 * @return the location of the control.
	 */
	public Point getLocation(final Control control) {
		checkWidget(control);
		return (Point) syncExec(new Result() {
			public Object result() {
				return control.getLocation();
			}
		});
	}

	/**
	 * @see Control#getMenu()
	 */
	public Menu getMenu(final Control control) {
		checkWidget(control);
		return (Menu) syncExec(new Result() {
			public Object result() {
				return control.getMenu();
			}
		});
	}

	/**
	 * Proxy for {@link Control#getParent()}. <p/>
	 * 
	 * @param control
	 *            the control under test.
	 * @return the control's parent.
	 */
	public Composite getParent(final Control control) {
		checkWidget(control);
		return (Composite) syncExec(new Result() {
			public Object result() {
				return control.getParent();
			}
		});
	}

	/**
	 * Proxy for {@link Control#getShell()}. <p/>
	 * 
	 * @param control
	 *            the control under test.
	 * @return the control's shell.
	 */
	public Shell getShell(final Control control) {
		checkWidget(control);
		return (Shell) syncExec(new Result() {
			public Object result() {
				return control.getShell();
			}
		});
	}

	/**
	 * Proxy for {@link Control#getSize()}. <p/>
	 * 
	 * @param control
	 *            the control under test.
	 * @return the size of the control.
	 */
	public Point getSize(final Control control) {
		checkWidget(control);
		return (Point) syncExec(new Result() {
			public Object result() {
				return control.getSize();
			}
		});
	}

	/**
	 * Proxy for {@link Control#getToolTipText()}. <p/>
	 * 
	 * @param control
	 *            the control under test.
	 * @return the tool tip associated with the control.
	 */
	public String getToolTipText(final Control control) {
		checkWidget(control);
		return (String) syncExec(new Result() {
			public Object result() {
				return control.getToolTipText();
			}
		});
	}

	/**
	 * Proxy for {@link Control#getVisible()}. <p/>
	 * 
	 * @param control
	 *            the control under test.
	 * @return true if this control is visible.
	 */
	public boolean getVisible(final Control control) {
		checkWidget(control);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return control.getVisible();
			}
		});
	}

	/**
	 * Proxy for {@link Control#isVisible()}.
	 * 
	 * @param control
	 *            the control under test.
	 * @return true if this control and all of its ancestor's are visible.
	 */
	public boolean isVisible(final Control control) {
		checkWidget(control);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return control.isVisible();
			}
		});
	}

	/**
	 * @see WidgetTester#isVisible(Widget)
	 */
	public boolean isVisible(Widget widget) {
		if (widget instanceof Control)
			return isVisible((Control) widget);
		return super.isVisible(widget);
	}

	/**
	 * Proxy for {@link Control#isEnabled()}.
	 */
	public boolean isEnabled(final Control control) {
		checkWidget(control);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return control.isEnabled();
			}
		});
	}

	/**
	 * Proxy for {@link Control#isFocusControl()}. <p/>
	 * 
	 * @param control
	 *            the control under test.
	 * @return true if this control has focus.
	 */
	public boolean isFocusControl(final Control control) {
		checkWidget(control);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return control.isFocusControl();
			}
		});
	}

	/**
	 * Proxy for {@link Control#isReparentable()}. <p/>
	 * 
	 * @param control
	 *            the control under test.
	 * @return true if this Control is reparentable.
	 */
	public boolean isReparentable(final Control control) {
		checkWidget(control);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return control.isReparentable();
			}
		});
	}

	/**
	 * Proxy for {@link Control#toControl(int, int)}. <p/>
	 * 
	 * @param control
	 *            the control under test.
	 * @param x
	 *            the x coordinate to be translated.
	 * @param y
	 *            the y coordinate to be translated.
	 * @return the translated coordinates.
	 */
	public Point toControl(final Control control, final int x, final int y) {
		checkWidget(control);
		return (Point) syncExec(new Result() {
			public Object result() {
				return control.toControl(x, y);
			}
		});
	}

	/**
	 * Proxy for {@link Control#toControl(org.eclipse.swt.graphics.Point)}. <p/>
	 * 
	 * @param control
	 *            the control under test.
	 * @param point
	 *            the point to be translated.
	 * @return the translated coordinates.
	 */
	public Point toControl(final Control control, final Point point) {
		checkWidget(control);
		return (Point) syncExec(new Result() {
			public Object result() {
				return control.toControl(point);
			}
		});
	}

	/**
	 * Proxy for {@link Control#toDisplay(int, int)}. <p/>
	 * 
	 * @param control
	 *            the control under test.
	 * @param x
	 *            the x coordinate to be translated.
	 * @param y
	 *            the y coordinate to be translated.
	 * @return the translated coordinates.
	 */
	public Point toDisplay(final Control control, final int x, final int y) {
		checkWidget(control);
		return (Point) syncExec(new Result() {
			public Object result() {
				return control.toDisplay(x, y);
			}
		});
	}

	/**
	 * Proxy for {@link Control#toDisplay(org.eclipse.swt.graphics.Point)}. <p/>
	 * 
	 * @param control
	 *            the control under test.
	 * @param point
	 *            the point to be translated.
	 * @return the translated coordinates.
	 */
	public Point toDisplay(final Control control, final Point point) {
		checkWidget(control);
		return (Point) syncExec(new Result() {
			public Object result() {
				return control.toDisplay(point);
			}
		});
	}

	/* End getters */
	/*
	 * Add and remove listeners. This is mainly intended for adding listeners to enable JUnit checks
	 * that certain events are issued.
	 */
	/**
	 * Proxy for {@link Control#addControlListener(org.eclipse.swt.events.ControlListener)}. <p/>
	 * 
	 * @param control
	 *            the control to add the listener to.
	 * @param listener
	 *            the listener to add.
	 */
	public void addControlListener(final Control control, final ControlListener listener) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.addControlListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Control#removeControlListener(org.eclipse.swt.events.ControlListener)}.
	 * <p/>
	 * 
	 * @param control
	 *            the control to remove the listener from.
	 * @param listener
	 *            the listener to remove
	 */
	public void removeControlListener(final Control control, final ControlListener listener) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.removeControlListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Control#addFocusListener(org.eclipse.swt.events.FocusListener)}. <p/>
	 * 
	 * @param control
	 *            the control to add the listener to.
	 * @param listener
	 *            the listener to add.
	 */
	public void addFocusListener(final Control control, final FocusListener listener) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.addFocusListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Control#removeFocusListener(org.eclipse.swt.events.FocusListener)}. <p/>
	 * 
	 * @param control
	 *            the control to remove the listener from.
	 * @param listener
	 *            the listener to remove
	 */
	public void removeFocusListener(final Control control, final FocusListener listener) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.removeFocusListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Control#addHelpListener(org.eclipse.swt.events.HelpListener)}. <p/>
	 * 
	 * @param control
	 *            the control to add the listener to.
	 * @param listener
	 *            the listener to add.
	 */
	public void addHelpListener(final Control control, final HelpListener listener) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.addHelpListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Control#removeHelpListener(org.eclipse.swt.events.HelpListener)}. <p/>
	 * 
	 * @param control
	 *            the control to remove the listener from.
	 * @param listener
	 *            the listener to remove
	 */
	public void removeHelpListener(final Control control, final HelpListener listener) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.removeHelpListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Control#addKeyListener(org.eclipse.swt.events.KeyListener)}. <p/>
	 * 
	 * @param control
	 *            the control to add the listener to.
	 * @param listener
	 *            the listener to add.
	 */
	public void addKeyListener(final Control control, final KeyListener listener) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.addKeyListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Control#removeKeyListener(org.eclipse.swt.events.KeyListener)}. <p/>
	 * 
	 * @param control
	 *            the control to remove the listener from.
	 * @param listener
	 *            the listener to remove
	 */
	public void removeKeyListener(final Control control, final KeyListener listener) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.removeKeyListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Control#addMouseListener(org.eclipse.swt.events.MouseListener)}. <p/>
	 * 
	 * @param control
	 *            the control to add the listener to.
	 * @param listener
	 *            the listener to add.
	 */
	public void addMouseListener(final Control control, final MouseListener listener) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.addMouseListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Control#removeMouseListener(org.eclipse.swt.events.MouseListener)}. <p/>
	 * 
	 * @param control
	 *            the control to remove the listener from.
	 * @param listener
	 *            the listener to remove
	 */
	public void removeMouseListener(final Control control, final MouseListener listener) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.removeMouseListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Control#addMouseMoveListener(org.eclipse.swt.events.MouseMoveListener)}.
	 * <p/>
	 * 
	 * @param control
	 *            the control to add the listener to.
	 * @param listener
	 *            the listener to add.
	 */
	public void addMouseMoveListener(final Control control, final MouseMoveListener listener) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.addMouseMoveListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Control#removeMouseMoveListener(org.eclipse.swt.events.MouseMoveListener)}.
	 * <p/>
	 * 
	 * @param control
	 *            the control to remove the listener from.
	 * @param listener
	 *            the listener to remove
	 */
	public void removeMouseMoveListener(final Control control, final MouseMoveListener listener) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.removeMouseMoveListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Control#addMouseTrackListener(org.eclipse.swt.events.MouseTrackListener)}.
	 * <p/>
	 * 
	 * @param control
	 *            the control to add the listener to.
	 * @param listener
	 *            the listener to add.
	 */
	public void addMouseTrackListener(final Control control, final MouseTrackListener listener) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.addMouseTrackListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Control#removeMouseTrackListener(org.eclipse.swt.events.MouseTrackListener)}.
	 * <p/>
	 * 
	 * @param control
	 *            the control to remove the listener from.
	 * @param listener
	 *            the listener to remove
	 */
	public void removeMouseTrackListener(final Control control, final MouseTrackListener listener) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.removeMouseTrackListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Control#addPaintListener(org.eclipse.swt.events.PaintListener)}. <p/>
	 * 
	 * @param control
	 *            the control to add the listener to.
	 * @param listener
	 *            the listener to add.
	 */
	public void addPaintListener(final Control control, final PaintListener listener) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.addPaintListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Control#removePaintListener(org.eclipse.swt.events.PaintListener)}. <p/>
	 * 
	 * @param control
	 *            the control to remove the listener from.
	 * @param listener
	 *            the listener to remove
	 */
	public void removePaintListener(final Control control, final PaintListener listener) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.removePaintListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Control#addTraverseListener(org.eclipse.swt.events.TraverseListener)}. <p/>
	 * 
	 * @param control
	 *            the control to add the listener to.
	 * @param listener
	 *            the listener to add.
	 */
	public void addTraverseListener(final Control control, final TraverseListener listener) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.addTraverseListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Control#removeTraverseListener(org.eclipse.swt.events.TraverseListener)}.
	 * <p/>
	 * 
	 * @param control
	 *            the control to remove the listener from.
	 * @param listener
	 *            the listener to remove.
	 */
	public void removeTraverseListener(final Control control, final TraverseListener listener) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.removeTraverseListener(listener);
			}
		});
	}

	/*
	 * End add and remove listeners.
	 */

	/**
	 * Proxy for {@link Control#setBackground(Color color)}.
	 */
	public void setBackground(final Control control, final Color color) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.setBackground(color);
			}
		});
	}

	/**
	 * Proxy for {@link Control#setBounds(Rectangle bounds)}.
	 */
	public void setBounds(final Control control, final Rectangle bounds) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.setBounds(bounds);
			}
		});
	}

	/**
	 * Proxy for {@link Control#setBounds(int x, int y, int width, int height)}.
	 */
	public void setBounds(final Control control, final int x, final int y, final int width,
			final int height) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.setBounds(x, y, width, height);
			}
		});
	}

	/**
	 * Proxy for {@link Control#setCursor(Cursor cursor)}.
	 */
	public void setCursor(final Control control, final Cursor cursor) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.setCursor(cursor);
			}
		});
	}

	/**
	 * Proxy for {@link Control#setCapture(boolean capture)}.
	 */
	public void setCapture(final Control control, final boolean capture) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.setCapture(capture);
			}
		});
	}

	/**
	 * Proxy for {@link Control#setEnabled(boolean enabled)}.
	 */
	public void setEnabled(final Control control, final boolean enabled) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.setEnabled(enabled);
			}
		});
	}

	/**
	 * Proxy for {@link Control#setFocus()}.
	 */
	public void setFocus(final Control control) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.setFocus();
			}
		});
	}

	/**
	 * Proxy for {@link Control#setFont(Font font)}.
	 */
	public void setFont(final Control control, final Font font) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.setFont(font);
			}
		});
	}

	/**
	 * Proxy for {@link Control#setForeground(Color color)}.
	 */
	public void setForeground(final Control control, final Color color) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.setForeground(color);
			}
		});
	}

	/**
	 * Proxy for {@link Control#setLayoutData(Object layoutData)}.
	 */
	public void setLayoutData(final Control control, final Object layoutData) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.setLayoutData(layoutData);
			}
		});
	}

	/**
	 * Proxy for {@link Control#setLocation(Point location)}.
	 */
	public void setLocation(final Control control, final Point location) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.setLocation(location);
			}
		});
	}

	/**
	 * Proxy for {@link Control#setLocation(int x, int y)}.
	 */
	public void setLocation(final Control control, final int x, final int y) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.setLocation(x, y);
			}
		});
	}

	/**
	 * Proxy for {@link Control#setMenu(Menu menu)}.
	 */
	public void setMenu(final Control control, final Menu menu) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.setMenu(menu);
			}
		});
	}

	/**
	 * Proxy for {@link Control#setParent(Composite composite)}.
	 */
	public void setParent(final Control control, final Composite composite) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.setParent(composite);
			}
		});
	}

	/**
	 * Proxy for {@link Control#setRedraw(boolean redraw)}.
	 */
	public void setRedraw(final Control control, final boolean redraw) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.setRedraw(redraw);
			}
		});
	}

	/**
	 * Proxy for {@link Control#setSize(Point size)}.
	 */
	public void setSize(final Control control, final Point size) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.setSize(size);
			}
		});
	}

	/**
	 * Proxy for {@link Control#setSize(int x, int y)}.
	 */
	public void setSize(final Control control, final int x, final int y) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.setSize(x, y);
			}
		});
	}

	/**
	 * Proxy for {@link Control#setToolTipText(String string)}.
	 */
	public void setToolTipText(final Control control, final String string) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.setToolTipText(string);
			}
		});
	}

	/**
	 * Proxy for {@link Control#setVisible(boolean visible)}.
	 */
	public void setVisible(final Control control, final boolean visible) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.setVisible(visible);
			}
		});
	}

	/**
	 * Proxy for {@link Control#traverse(int traversal)}.
	 */
	public void traverse(final Control control, final int traversal) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.traverse(traversal);
			}
		});
	}

	/**
	 * Proxy for {@link Control#update()}.
	 */
	public void update(final Control control) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.update();
			}
		});
	}
}
