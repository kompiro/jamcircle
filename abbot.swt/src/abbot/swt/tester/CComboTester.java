package abbot.swt.tester;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.script.Condition;
import abbot.swt.tester.WidgetTester.Textable;
import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;
import abbot.swt.utilities.Displays.StringResult;

/**
 * A tester for {@link CCombo}s.
 * <p>
 * Created on 12.04.2005 by Richard Birenheide (D035816) Copyright SAP AG 2005
 * 
 * @author Richard Birenheide
 * @author Gary Johnston
 */
public class CComboTester extends CompositeTester implements Textable {

	/**
	 * Factory method.
	 */
	public static CComboTester getCComboTester() {
		return (CComboTester) getTester(CCombo.class);
	}

	/**
	 * Constructs a new {@link CComboTester} associated with the specified
	 * {@link abbot.swt.Robot}.
	 */
	public CComboTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * Proxy for {@link CCombo#add(String)}. <p/>
	 */
	public void add(final CCombo combo, final String string) {
		checkWidget(combo);
		syncExec(new Runnable() {
			public void run() {
				combo.add(string);
			}
		});
	}

	/**
	 * Proxy for {@link CCombo#add(String, int)}. <p/>
	 */
	public void add(final CCombo combo, final String string, final int index) {
		checkWidget(combo);
		syncExec(new Runnable() {
			public void run() {
				combo.add(string, index);
			}
		});
	}

	/**
	 * Proxy for {@link CCombo#addModifyListener(ModifyListener)}. <p/>
	 */
	public void addModifyListener(final CCombo combo,
			final ModifyListener listener) {
		checkWidget(combo);
		syncExec(new Runnable() {
			public void run() {
				combo.addModifyListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link CCombo#addSelectionListener(SelectionListener)}. <p/>
	 */
	public void addSelectionListener(final CCombo combo,
			final SelectionListener listener) {
		checkWidget(combo);
		syncExec(new Runnable() {
			public void run() {
				combo.addSelectionListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link CCombo#clearSelection()}. <p/>
	 * 
	 * @param combo
	 *            the CCombo to clear the selection for.
	 */
	public void clearSelection(final CCombo combo) {
		checkWidget(combo);
		syncExec(new Runnable() {
			public void run() {
				combo.clearSelection();
			}
		});
	}

	/**
	 * Proxy for {@link CCombo#getEditable()}. <p/>
	 * 
	 * @param combo
	 *            the CCombo to check the editable state for.
	 * @return true if the CCombo is editable.
	 */
	public boolean getEditable(final CCombo combo) {
		checkWidget(combo);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return combo.getEditable();
			}
		});
	}

	public int getItemCount(final CCombo combo) {
		checkWidget(combo);
		return getItemCountPrim(combo);
	}

	private int getItemCountPrim(final CCombo combo) {
		return syncExec(new IntResult() {
			public int result() {
				return combo.getItemCount();
			}
		});
	}

	/**
	 * Proxy for {@link CCombo#getItem(int)}. <p/>
	 * 
	 * @param combo
	 *            the CCombo for which the item to get.
	 * @param index
	 *            the index of the item to get.
	 * @return the item value.
	 */
	public String getItem(final CCombo combo, final int index) {
		checkWidget(combo);
		return syncExec(new StringResult() {
			public String result() {
				return combo.getItem(index);
			}
		});
	}

	/**
	 * Proxy for {@link CCombo#getItems()}.
	 */
	public String[] getItems(final CCombo combo) {
		checkWidget(combo);
		return (String[]) syncExec(new Result<String[]>() {
			public String[] result() {
				return combo.getItems();
			}
		});
	}

	/**
	 * @see CCombo#getSelectionIndex()
	 */
	public int getSelectionIndex(final CCombo combo) {
		checkWidget(combo);
		return getSelectionIndexPrim(combo);
	}

	private int getSelectionIndexPrim(final CCombo combo) {
		return syncExec(new IntResult() {
			public int result() {
				return combo.getSelectionIndex();
			}
		});
	}

	/**
	 * @see CCombo#getSelectionIndex()
	 */
	public String getSelectedItem(final CCombo combo) {
		checkWidget(combo);
		return syncExec(new StringResult() {
			public String result() {
				int index = combo.getSelectionIndex();
				if (index != -1)
					return combo.getItem(index);
				return null;
			}
		});
	}

	/**
	 * This will select a item from a combobox by using ARROW_DWON and ARROW_UP.
	 * This is to actually select the item. Just calling CComboBox.Select() will
	 * only set the text in the CComboBox and therefor not fire the
	 * SelectionChanged event. When testing a actual select should happen. This
	 * should eventually be replaced with a version that uses the mouse to click
	 * an item because that is really what a user does in most cases. WARNING:
	 * This is only tested for a SWT.DROPDOWN style CComboBox. See Question
	 * below also. <p/>
	 * 
	 * @param combo
	 *            the CCombo for which the item to get.
	 * @param index
	 *            the index of the item to get.
	 */
	private void selectIndex(final CCombo combo, final int index) {
		checkWidget(combo);

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

		/*
		 * Determine the starting index (i.e., what index we'll effectively be at initially).
		 */
		int current = getSelectionIndex(combo);

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

		// Select/dismiss.
		actionKeyChar(SWT.CR);

		// Wait for the selection to complete.
		waitSelection(combo, index);
	}
	
	private void waitSelection(final CCombo combo, final int index) {
		wait(new Condition() {
			private int selected = Integer.MAX_VALUE;
			public boolean test() {
				selected = getSelectionIndexPrim(combo);
				return selected == index;
			}
			public String toString() {
				return String.format("selected %s item %d (got %d)", combo, index, selected);
			}
		}, 2000);
	}

	/**
	 * Select the item from the Combo at the given index.
	 * 
	 * @param combo
	 *            Combo from which to select
	 * @param index
	 *            Index of item to select
	 */
	public void actionSelectIndex(final CCombo combo, final int index) {
		checkWidget(combo);
		if (index >= getItemCountPrim(combo))
			throw new IllegalArgumentException("index out of range: " + index);
		selectIndex(combo, index);
	}

	/**
	 * Select the given item from the Combo.
	 * 
	 * @param combo
	 *            Combo from which to select
	 * @param item
	 *            String to select
	 */
	public void actionSelectItem(CCombo combo, String item) {
		int index = indexOf(combo, item);
		if (index == -1)
			throw new RuntimeException("\"" + item + "\" not found");
		selectIndex(combo, index);
	}
	
	/**
	 * @see CCombo#indexOf(String)
	 */
	public int indexOf(final CCombo combo, final String string) {
		checkWidget(combo);
		return syncExec(new IntResult() {
			public int result() {
				return combo.indexOf(string);
			}
		});
	}

	/**
	 * @see CCombo#indexOf(String, int)
	 */
	public int indexOf(final CCombo combo, final String string, final int start) {
		checkWidget(combo);
		return syncExec(new IntResult() {
			public int result() {
				return combo.indexOf(string, start);
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
	public boolean assertIndexSelected(final CCombo combo, int index) {
		checkWidget(combo);
		return getSelectionIndex(combo) == index;
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
	public boolean assertItemSelected(final CCombo combo, String item) {
		checkWidget(combo);
		String selectedItem = getSelectedItem(combo);
		if (selectedItem == null)
			return item == null;
		return selectedItem.equals(item);
	}

	/**
	 * Proxy for {@link CCombo#getText()}
	 */
	public String getText(final CCombo combo) {
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
		return getText((CCombo) widget);
	}

	public boolean isTextEditable(Widget widget) {
		return getEditable((CCombo) widget);
	}

	/**
	 * Proxy for {@link CCombo#setText(String)}
	 */
	public void setText(final CCombo combo, final String string) {
		syncExec(new Runnable() {
			public void run() {
				combo.setText(string);
			}
		});
	}

}
