package abbot.swt;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;

/**
 * This class adapts SWT to Abbot, e.g. where SWT methods are not public.
 * 
 * @author Gary Johnston
 * @author Steve Northover
 */
public class SWTWorkarounds {

	/*************************** COMMON *****************************/	
	public static Rectangle getBounds (Object object) {
		Rectangle result = new Rectangle (0, 0, 0, 0);
		try {
			Method method = object.getClass().getDeclaredMethod ("getBounds", (Class[]) null);
			method.setAccessible(true);
			result = (Rectangle) method.invoke (object, (Object[]) null);
		} catch (Throwable th) {
			// TODO - decide what should happen when the method is unavailable
		}
		return result;
	}
	
	public static Rectangle getBounds(MenuItem menuItem) {
		Rectangle itemRect = getBounds ((Object)menuItem);
		Rectangle menuRect = getBounds (menuItem.getParent ());
		if ((menuItem.getParent ().getStyle() & SWT.RIGHT_TO_LEFT) != 0) {
			itemRect.x = menuRect.x + menuRect.width - itemRect.width - itemRect.x;
		} else {
			itemRect.x += menuRect.x;
		}
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=38436#c143
		itemRect.y += menuRect.y;
		return itemRect;
	}

	public static Rectangle getBounds (Menu menu) {
		return getBounds ((Object)menu);
	}

	public static Rectangle getBounds (ScrollBar bar) {
		
		// Set x,y to the location of the bar relative to its parent.
		Scrollable parent = bar.getParent();
		Point parentSize = parent.getSize();
		Point size = bar.getSize();
		int x, y;
		if ((bar.getStyle() & SWT.HORIZONTAL) != 0) {
			x = 0;
			y = parentSize.y - size.y;
		} else {
			x = parentSize.x - size.x;
			y = 0;
		}
		
		// Return the bar's bounds in display coordinates.
		return bar.getDisplay().map(parent, null, x, y, size.x, size.y);
	}
	
	/*************************** WIN32 *****************************/
	static int SendMessage (int hWnd, int Msg, int wParam, int [] lParam) {
		int result = 0;
		try {
			Class clazz = Class.forName ("org.eclipse.swt.internal.win32.OS");
			Class [] params = new Class [] {
				Integer.TYPE,
				Integer.TYPE,
				Integer.TYPE,
				lParam.getClass (),
			};
			Method method = clazz.getMethod ("SendMessage", params);
			Object [] args = new Object [] {
				new Integer (hWnd),
				new Integer (Msg),
				new Integer (wParam),
				lParam,
			};
			result = ((Integer) method.invoke (clazz, args)).intValue ();
		} catch (Throwable e) {
			// TODO - decide what should happen when the method is unavailable
		}
		return result;
	}

	static Rectangle win32_getBounds(TabItem tabItem) {
		TabFolder parent = tabItem.getParent();
		int index = parent.indexOf (tabItem);
		if (index == -1) return new Rectangle (0, 0, 0, 0);
		int [] rect = new int [4];
		SendMessage (parent.handle, /*TCM_GETITEMRECT*/ 0x130a, index, rect);
		int width = rect [2] - rect[0];
		int height = rect [3] - rect [1];
		Rectangle bounds = new Rectangle (rect [0], rect [1], width, height);
		return tabItem.getDisplay().map (tabItem.getParent (), null, bounds);
	}

	static Rectangle win32_getBounds(TableColumn tableColumn) {
		Table parent = tableColumn.getParent ();
		int index = parent.indexOf (tableColumn);
		if (index == -1) return new Rectangle (0, 0, 0, 0); 
		int hwndHeader = SendMessage (parent.handle, /*LVM_GETHEADER*/ 0x101f, 0, new int [0]);		
		int [] rect = new int [4];
		SendMessage (hwndHeader, /*HDM_GETITEMRECT*/ 0x1200 + 7, index, rect);
		int width = rect [2] - rect[0];
		int height = rect [3] - rect [1];
		Rectangle bounds = new Rectangle (rect [0], rect [1], width, height);
		// TODO - oordinate system may change when the API is added to SWT
		return tableColumn.getDisplay().map (parent, null, bounds);
	}
	
	/*************************** GTK *****************************/
	static void gtk_getBounds (int handle, Rectangle bounds) {	
		try {
			Class clazz = Class.forName ("org.eclipse.swt.internal.gtk.OS");
			Class [] params = new Class [] {Integer.TYPE};
			Object [] args = new Object [] {new Integer (handle)};
			Method method = clazz.getMethod ("GTK_WIDGET_X", params);
			bounds.x = ((Integer) method.invoke (clazz, args)).intValue ();
			method = clazz.getMethod ("GTK_WIDGET_Y", params);
			bounds.y = ((Integer) method.invoke (clazz, args)).intValue ();
			method = clazz.getMethod ("GTK_WIDGET_WIDTH", params);
			bounds.width = ((Integer) method.invoke (clazz, args)).intValue ();
			method = clazz.getMethod ("GTK_WIDGET_HEIGHT", params);
			bounds.height = ((Integer) method.invoke (clazz, args)).intValue ();
		} catch (Throwable e) {
			// TODO - decide what should happen when the method is unavailable
		}
	}
	
	static Rectangle gtk_getBounds(TableColumn tabColumn) {
		Rectangle bounds = new Rectangle (0, 0, 0, 0);
		try {
			Class c = tabColumn.getClass();
			Field f = c.getDeclaredField("buttonHandle");
			f.setAccessible(true);
			int handle = f.getInt(tabColumn);			
			gtk_getBounds(handle, bounds);
		} catch (Throwable e) {
			// TODO - decide what should happen when the method is unavailable
		}
		return tabColumn.getDisplay().map (tabColumn.getParent (), null, bounds);
	}
	
	static Rectangle gtk_getBounds(TabItem tabItem) {
		Rectangle bounds = new Rectangle (0, 0, 0, 0);  
		try {
			Class c = Class.forName ("org.eclipse.swt.widgets.Widget");
			Field f = c.getDeclaredField("handle");
			f.setAccessible(true);
			int handle = f.getInt(tabItem);
			gtk_getBounds(handle, bounds);
		} catch (Throwable e) {
			// TODO - decide what should happen when the method is unavailable
		}
		return tabItem.getDisplay().map (tabItem.getParent (), null, bounds);
	}
	
	/*************************** MOTIF  *****************************/
	static Rectangle motif_getBounds(TabItem tabItem) {
		Rectangle bounds = new Rectangle (0, 0, 0, 0);  
		try {
			Class c = tabItem.getClass();
			Method m = c.getDeclaredMethod("getBounds", (Class[]) null);
			m.setAccessible(true);
			bounds = (Rectangle)m.invoke(tabItem, (Object[]) null);
			int margin = 2;
			bounds.x +=margin;bounds.y+=margin;
			bounds.width -= 2*margin; bounds.height-=margin;
		} catch (Throwable e) {
			// TODO - decide what should happen when the method is unavailable
		}
		return tabItem.getDisplay().map (tabItem.getParent (), null, bounds);
	}
	
	static Rectangle motif_getBounds(TableColumn tableColumn) {
		Rectangle bounds = new Rectangle (0, 0, 0, 0);  
		try {
			Class c = tableColumn.getClass();
			Method m = c.getDeclaredMethod("getX", (Class[]) null);
			m.setAccessible(true);
			bounds.x = ((Integer)m.invoke(tableColumn, (Object[]) null)).intValue();
			bounds.width = tableColumn.getWidth() - 2;
			bounds.height = tableColumn.getParent().getHeaderHeight() - 2;
		} catch (Throwable e) {
			// TODO - decide what should happen when the method is unavailable
		}
		return tableColumn.getDisplay().map (tableColumn.getParent (), null, bounds);
	}
	
	/*************************** CARBON  *****************************/
	static Rectangle carbon_getBounds(TabItem tabItem) {
		return null;
	}	
	
	static Rectangle carbon_getBounds(TableColumn tableColumn) {
		return null;
	}

	public static Rectangle getBounds (TabItem tabItem){
		if (SWT.getPlatform().equals("win32")) {
			return win32_getBounds (tabItem);
		}
		if (SWT.getPlatform().equals("gtk")) {
			return gtk_getBounds (tabItem);
		}
		if (SWT.getPlatform().equals("motif")) {
			return motif_getBounds (tabItem);
		}
		if (SWT.getPlatform().equals("carbon")) {
			return carbon_getBounds (tabItem);
		}
		return null;
	}

	public static Rectangle getBounds (TableColumn tableColumn) {
		if (SWT.getPlatform().equals("win32")) {
			return win32_getBounds (tableColumn);
		}
		if (SWT.getPlatform().equals("gtk")) {
			return gtk_getBounds (tableColumn);
		}
		if (SWT.getPlatform().equals("motif")) {
			return motif_getBounds (tableColumn);
		}
		if (SWT.getPlatform().equals("carbon")) {
			return carbon_getBounds (tableColumn);
		}
		return null;
	}

	public static Rectangle getBounds (TableItem item) {
		return item.getDisplay().map (item.getParent (), null, item.getBounds (0));
	}

	public static Rectangle getBounds (TreeItem item) {
		return item.getDisplay().map (item.getParent (), null, item.getBounds ());
	}

	public static Rectangle getBounds (CTabItem item) {
		return item.getDisplay().map (item.getParent (), null, item.getBounds ());
	}

	public static Rectangle getBounds (ToolItem item) {
		return item.getDisplay().map (item.getParent (), null, item.getBounds ());
	}

	public static Rectangle getBounds (CoolItem item) {
		return item.getDisplay().map (item.getParent (), null, item.getBounds ());
	}
}


