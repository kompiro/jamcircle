package abbot.swt.tester;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.script.Condition;
import abbot.swt.tester.WidgetTester.Textable;
import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;
import abbot.swt.utilities.Displays.StringResult;

/**
 * A tester for {@link Combo}s.
 */
public class ComboTester extends CompositeTester implements Textable {

	/**
	 * Factory method.
	 */
	public static ComboTester getComboTester() {
		return (ComboTester) getTester(Combo.class);
	}

	/**
	 * Constructs a new {@link ComboTester} associated with the specified {@link abbot.swt.Robot}.
	 */
	public ComboTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * Proxy for {@link Combo#getItemCount()}. <p/>
	 * 
	 * @param combo
	 *            the combo under test.
	 * @return the number of items.
	 */
	public int getItemCount(final Combo combo) {
		checkWidget(combo);
		return getItemCountPrim(combo);
	}

	private int getItemCountPrim(final Combo combo) {
		return syncExec(new IntResult() {
			public int result() {
				return combo.getItemCount();
			}
		});
	}

	/**
	 * Proxy for {@link Combo#getItemHeight()}. <p/>
	 * 
	 * @param combo
	 *            the combo under test.
	 * @return the height of one item.
	 */
	public int getItemHeight(final Combo combo) {
		checkWidget(combo);
		return syncExec(new IntResult() {
			public int result() {
				return combo.getItemHeight();
			}
		});
	}

	/**
	 * Proxy for {@link Combo#getItems()}. <p/>
	 * 
	 * @param combo
	 *            the combo under test.
	 * @return the items in the combo's list.
	 */
	public String[] getItems(final Combo combo) {
		checkWidget(combo);
		return (String[]) syncExec(new Result<String[]>() {
			public String[] result() {
				return combo.getItems();
			}
		});
	}

	/**
	 * Proxy for {@link Combo#getSelection()}. <p/>
	 * 
	 * @param combo
	 *            the combo under test.
	 * @return a point representing the selection start and end.
	 */
	public Point getSelection(final Combo combo) {
		checkWidget(combo);
		return (Point) syncExec(new Result<Point>() {
			public Point result() {
				return combo.getSelection();
			}
		});
	}

	/**
	 * Proxy for {@link Combo#getSelectionIndex()}. <p/>
	 * 
	 * @param combo
	 *            the combo under test.
	 * @return the selected index.
	 */
	public int getSelectionIndex(final Combo combo) {
		checkWidget(combo);
		return syncExec(new IntResult() {
			public int result() {
				return combo.getSelectionIndex();
			}
		});
	}

	/**
	 * @see Combo#getVisibleItemCount()
	 */
	public int getVisibleItemCount(final Combo combo) {
		checkWidget(combo);
		return syncExec(new IntResult() {
			public int result() {
				return combo.getVisibleItemCount();
			}
		});
	}

	/**
	 * Proxy for {@link Combo#getText()}. <p/>
	 * 
	 * @param combo
	 *            the combo under test.
	 * @return the contents of the text field.
	 */
	public String getText(final Combo combo) {
		checkWidget(combo);
		return syncExec(new StringResult() {
			public String result() {
				return combo.getText();
			}
		});
	}

	/**
	 * @see Textable#getText(Widget)
	 */
	public String getText(Widget widget) {
		return getText((Combo) widget);
	}

	public boolean isTextEditable(Widget widget) {
		int style = getStyle(widget);
		return (style & SWT.READ_ONLY) == 0;
	}

	/**
	 * Proxy for {@link Combo#getTextHeight()}. <p/>
	 * 
	 * @param combo
	 *            the combo under test.
	 * @return the text height.
	 */
	public int getTextHeight(final Combo combo) {
		checkWidget(combo);
		return syncExec(new IntResult() {
			public int result() {
				return combo.getTextHeight();
			}
		});
	}

	/**
	 * Proxy for {@link Combo#getTextLimit()}. <p/>
	 * 
	 * @param combo
	 *            the combo under test.
	 * @return the text limit.
	 */
	public int getTextLimit(final Combo combo) {
		checkWidget(combo);
		return syncExec(new IntResult() {
			public int result() {
				return combo.getTextLimit();
			}
		});
	}

	/**
	 * @see Combo#indexOf(String)
	 */
	public int indexOf(final Combo combo, final String item) {
		return (int) syncExec(new IntResult() {
			public int result() {
				return combo.indexOf(item);
			}
		});
	}

	/**
	 * @see Combo#indexOf(String, int)
	 */
	public int indexOf(final Combo combo, final String item, final int start) {
		return (int) syncExec(new IntResult() {
			public int result() {
				return combo.indexOf(item, start);
			}
		});
	}

	/* End getters */

	/**
	 * Returns the effective "type" of combo, taking into account any platform quirks.
	 * 
	 * @return {@link SWT#SIMPLE} or {@link SWT#DROP_DOWN}
	 */
	// private int getType(Combo combo) {
	//
	// int type = getStyle(combo) & (SWT.SIMPLE | SWT.DROP_DOWN);
	//
	// // On GTK SWT.SIMPLE combos are the same as SWT.DROP_DOWNs.
	// // if (SWT.getPlatform().equals("gtk") && (type & SWT.SIMPLE) != 0)
	// // return SWT.DROP_DOWN;
	//		
	// return type;
	// }
	/**
	 * Drop down the menu for the given Combo box WARNING: This method is platform-dependent.
	 */
	protected void dropDownCombo(Combo combo) {
		checkWidget(combo);

		final int BUTTON_SIZE = 16;
		Rectangle bounds = getGlobalBounds(combo);
		int x = bounds.x + bounds.width - BUTTON_SIZE / 2;
		int y = bounds.y + bounds.height - BUTTON_SIZE / 2;
		robot.mouseClick(x, y, SWT.BUTTON1);
	}

	/** Move the mouse pointer over the item with the given index */
	public void mouseMoveIndex(Combo combo, int index) {
		checkWidget(combo);
		int style = getStyle(combo);
		if ((style & SWT.DROP_DOWN) == SWT.DROP_DOWN) {
			// TODO Add code to scroll down and move the mouse pointer;
			// may not be possible b/c combo.getVerticalBar() returns null even when the
			// bar on the drop-down is visible
		} else {// SWT.SIMPLE
			// TODO Add code to scroll so item is visible and move
			// pointer over item
		}
	}

	/**
	 * Select the item from the Combo at the given index.
	 * 
	 * @param combo
	 *            Combo from which to select
	 * @param index
	 *            Index of item to select
	 */
	public void actionSelectIndex(Combo combo, int index) {
		checkWidget(combo);
		if (index >= getItemCountPrim(combo))
			throw new IllegalArgumentException("index out of range: " + index);
		selectIndex(combo, index);
	}

	private void selectIndex(final Combo combo, final int index) {

		/*
		 * If it's a drop-down, drop it down. Otherwise, give it focus by clicking its text box's
		 * center. Note that on GTK all Combos are drop-downs (even if they were created as simple).
		 */
		int style = getStyle(combo);
		boolean isGTK = SWT.getPlatform().equals("gtk");
		boolean isDropDown = (style & SWT.DROP_DOWN) != 0 || isGTK;
		if (isDropDown) {
			
			// Click on its drop-down button.
			Rectangle ca = getClientArea(combo);
			int y = ca.y + ca.height / 2;
			int x = ca.x + ca.width - ca.height / 2;
			Point p = toDisplay(combo, x, y);
			click(p.x, p.y);

			/*
			 * Wait for it to drop-down (i.e., for the list to become visible). As of Eclipse 3.3
			 * there isn't API or event support for finding out if/when a Combo's list has become
			 * visible. See https://bugs.eclipse.org/bugs/show_bug.cgi?id=21619. So for now we
			 * lamely wait a bit and hope it has dropped-down by then.
			 */
			sleep(250);
		} else {
			
			// Click on its text field.
			Rectangle ca = getClientArea(combo);
			int h = getTextHeight(combo);
			int y = ca.y + h / 2;
			int x = ca.x + ca.width / 2;
			Point p = toDisplay(combo, x, y);
			click(p.x, p.y);
		}

		/*
		 * Determine the starting index (i.e., what index we'll effectively be at initially).
		 */
		int current = getSelectionIndex(combo);
		if (isGTK && isDropDown) {
			boolean isReadOnly = (style & SWT.READ_ONLY) != 0;
			if (isReadOnly) {
				current = getSelectionIndex(combo);
				if (current == -1)
					current = 0;
			} else {
				current = -1;
			}
		} else {
			current = getSelectionIndex(combo);
		}

		// Do up or down arrows to get to the desired index.
		int n = index - current;
		int arrow = SWT.ARROW_DOWN;
		if (n < 0) {
			n = -n;
			arrow = SWT.ARROW_UP;
		}
		while (n-- > 0) {
			actionKey(arrow);
		}

		// Select.
		actionKeyChar(SWT.CR);

		// Wait for the selection to complete.
		wait(new Condition() {
			private int selected = Integer.MAX_VALUE;

			public boolean test() {
				selected = getSelectionIndex(combo);
				return selected == index;
			}

			public String toString() {
				return String.format("expected %d (got %d)", index, selected);
			}
		}, 5000, 200);
	}

	/**
	 * Select the given item from the Combo.
	 * 
	 * @param combo
	 *            Combo from which to select
	 * @param item
	 *            String to select
	 */
	public int actionSelectItem(final Combo combo, String item) {
		checkWidget(combo);

		int index = indexOf(combo, item);
		if (index != -1)
			actionSelectIndex(combo, index);
		return index;
	}

	/**
	 * Returns the item at the given index.
	 * 
	 * @param combo
	 *            Combo from which to obtain the item
	 * @param index
	 *            Index of the item
	 * @return the item at the given index, or null if index is out-of-bounds
	 */
	public String getItem(final Combo combo, final int index) {
		checkWidget(combo);
		return syncExec(new StringResult() {
			public String result() {
				return combo.getItem(index);
			}
		});
	}

	/**
	 * Indicates if the given index is the index of the currently selected item.
	 * 
	 * @param combo
	 *            Combo to check
	 * @param index
	 *            Index of the item to check
	 * @return whether the item at the given index is selected
	 */
	public boolean assertIndexSelected(final Combo combo, int index) {
		checkWidget(combo);
		int selected = getSelectionIndex(combo);
		return selected == index;
	}

	/**
	 * Indicates if the given item is currently selected.
	 * 
	 * @param combo
	 *            Combo to check
	 * @param item
	 *            Item to check
	 * @return whether the given item is selected
	 */
	public boolean assertItemSelected(final Combo combo, String item) {
		checkWidget(combo);
		int selected = getSelectionIndex(combo);
		if (selected != -1) {
			String selectedItem = getItem(combo, selected);
			return item.equals(selectedItem);
		}
		return false;
	}

	/**
	 * Proxy for {@link Combo#setText(String)}.
	 */
	public void setText(final Combo combo, final String string) {
		checkWidget(combo);
		syncExec(new Runnable() {
			public void run() {
				combo.setText(string);
			}
		});
	}
}
