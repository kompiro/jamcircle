package abbot.swt.utilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tracker;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.WidgetLocator;
import abbot.swt.finder.generic.FormatterImpl;
import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;

public class WidgetFormatter extends FormatterImpl<Widget> {

	public WidgetFormatter(Appendable appendable) {
		super(appendable);
	}

	public WidgetFormatter() {
		super();
	}

	public void format(Widget widget, int indent) {
		indent(indent);
		printf("%s", widget);
		printStyle(widget);
		printData(widget);
		printLocation(WidgetLocator.getBounds(widget, false));
		println();
	}

	private void printData(Widget widget) {
		Object data = getData(widget);
		if (data != null)
			printf(" <%s>", data);
	}

	private Object getData(final Widget widget) {
		return Displays.syncExec(widget.getDisplay(), new Result<Object>() {
			public Object result() {
				return widget.getData();
			}
		});
	}

	private void printStyle(Widget widget) {
		printf(" [");
		int style = getStyle(widget);
		boolean first = true;
		for (int styleBit = 0, styleMask = 1; styleBit < 32; styleBit++, styleMask <<= 1) {
			if ((style & styleMask) != 0) {
				if (!first)
					print(',');
				else
					first = false;
				printf(getStyleString(widget, styleMask));
			}
		}
		print(']');
	}

	private int getStyle(final Widget widget) {
		return Displays.syncExec(widget.getDisplay(), new IntResult() {
			public int result() {
				return widget.getStyle();
			}
		});
	}

	private String getStyleString(Widget widget, int style) {
		switch (style) {
		case SWT.BAR:
			if (widget instanceof Menu)
				return "bar";
			// case SWT.SEPARATOR:
			if (widget instanceof Label || widget instanceof MenuItem
					|| widget instanceof ToolItem)
				return "separator";
			// case SWT.TOGGLE:
			if (widget instanceof Button)
				return "toggle";
			// case SWT.MULTI:
			if (widget instanceof Text || widget instanceof List
					|| ((Object) widget) instanceof FileDialog)
				return "multi";
			// case SWT.INDETERMINATE:
			if (widget instanceof ProgressBar)
				return "indeterminate";
			break;

		case SWT.DROP_DOWN:
			if (widget instanceof Menu || widget instanceof ToolItem
					|| widget instanceof CoolItem || widget instanceof Combo)
				return "drop-down";
			// case SWT.ARROW:
			if (widget instanceof Button)
				return "arrow";
			// case SWT.SINGLE:
			if (widget instanceof Text || widget instanceof List
					|| widget instanceof Table || widget instanceof Tree)
				return "single";
			// case SWT.SHADOW_IN:
			if (widget instanceof Label || widget instanceof Group)
				return "shadow-in";
			// case SWT.TOOL:
			if (widget instanceof Decorations)
				return "tool";
			break;

		case SWT.POP_UP:
			if (widget instanceof Menu)
				return "pop-up";
			// case SWT.PUSH:
			if (widget instanceof Button || widget instanceof MenuItem
					|| widget instanceof ToolItem)
				return "push";
			// case SWT.READ_ONLY:
			if (widget instanceof Combo || widget instanceof Text)
				return "read-only";
			// case SWT.SHADOW_OUT:
			if (widget instanceof Label || widget instanceof Group
					|| widget instanceof ToolBar)
				return "shadow-out";
			// case SWT.NO_TRIM:
			if (widget instanceof Decorations)
				return "no-trip";
			break;

		case SWT.RADIO:
			if (widget instanceof Button || widget instanceof MenuItem
					|| widget instanceof ToolItem)
				return "radio";
			// case SWT.SHADOW_ETCHED_IN:
			if (widget instanceof Group)
				return "shadow-etched-in";
			// case SWT.RESIZE:
			if (widget instanceof Decorations || widget instanceof Tracker)
				return "resize";
			break;

		case SWT.CHECK:
			if (widget instanceof Button || widget instanceof MenuItem
					|| widget instanceof ToolItem || widget instanceof Table
					|| widget instanceof Tree)
				return "check";
			// case SWT.SHADOW_NONE:
			if (widget instanceof Label || widget instanceof Group)
				return "shadow-none";
			// case SWT.TITLE:
			if (widget instanceof Decorations)
				return "title";
			break;

		case SWT.CASCADE:
			if (widget instanceof MenuItem)
				return "cascade";
			// case SWT.WRAP:
			if (widget instanceof Label || widget instanceof Text
					|| widget instanceof ToolBar || widget instanceof Spinner)
				return "wrap";
			// case SWT.SIMPLE:
			if (widget instanceof Combo)
				return "simple";
			// case SWT.SHADOW_ETCHED_OUT:
			if (widget instanceof Group)
				return "shadow-etched-out";
			// case SWT.CLOSE:
			if (widget instanceof Decorations)
				return "close";
			break;

		case SWT.MIN:
			if (widget instanceof Decorations)
				return "min";
			// case SWT.UP:
			if (widget instanceof Button || widget instanceof Tracker)
				return "up";
			break;

		case SWT.H_SCROLL:
			if (widget instanceof Scrollable)
				return "h-scroll";
			// case SWT.HORIZONTAL:
			if (widget instanceof Label || widget instanceof ProgressBar
					|| widget instanceof Sash || widget instanceof Scale
					|| widget instanceof ScrollBar || widget instanceof Slider
					|| widget instanceof ToolBar
					|| ((Object) widget) instanceof FillLayout
					|| ((Object) widget) instanceof RowLayout)
				return "horizontal";
			break;

		case SWT.V_SCROLL:
			if (widget instanceof Scrollable)
				return "v-scroll";
			// case SWT.VERTICAL:
			if (widget instanceof Label || widget instanceof ProgressBar
					|| widget instanceof Sash || widget instanceof Scale
					|| widget instanceof ScrollBar || widget instanceof Slider
					|| widget instanceof ToolBar || widget instanceof CoolBar
					|| ((Object) widget) instanceof FillLayout
					|| ((Object) widget) instanceof RowLayout)
				return "vertical";
			break;

		case SWT.MAX:
			if (widget instanceof Decorations)
				return "max";
			// case SWT.DOWN:
			if (widget instanceof Button || widget instanceof Tracker)
				return "down";
			break;

		case SWT.BORDER:
			if (widget instanceof Control)
				return "border";
			break;

		case SWT.CLIP_CHILDREN:
			if (widget instanceof Control)
				return "clip-children";
			break;

		case SWT.CLIP_SIBLINGS:
			if (widget instanceof Control)
				return "clip-siblings";
			break;

		case SWT.ON_TOP:
			if (widget instanceof Shell)
				return "on-top";
			// case SWT.LEAD:
			if (widget instanceof Button || widget instanceof Label
					|| widget instanceof TableColumn
					|| widget instanceof Tracker
					|| ((Object) widget) instanceof FormAttachment)
				return "lead";
			break;

		case SWT.PRIMARY_MODAL:
			if (((Object) widget) instanceof Dialog || widget instanceof Shell)
				return "primary-modal";
			// case SWT.HIDE_SELECTION:
			if (widget instanceof Table)
				return "hide-selection";
			break;

		case SWT.APPLICATION_MODAL:
			if (((Object) widget) instanceof Dialog || widget instanceof Shell)
				return "application-modal";
			// case SWT.FULL_SELECTION:
			if (widget instanceof StyledText || widget instanceof Table
					|| widget instanceof Tree)
				return "full-selection";
			// case SWT.SMOOTH:
			if (widget instanceof ProgressBar || widget instanceof Sash)
				return "smooth";
			break;

		case SWT.SYSTEM_MODAL:
			if (((Object) widget) instanceof Dialog || widget instanceof Shell)
				return "system-modal";
			// case SWT.TRAIL:
			if (widget instanceof Button || widget instanceof Label
					|| widget instanceof TableColumn
					|| widget instanceof Tracker
					|| ((Object) widget) instanceof FormAttachment)
				return "trail";
			break;

		case SWT.NO_BACKGROUND:
			if (widget instanceof Composite)
				return "no-background";
			break;

		case SWT.NO_FOCUS:
			if (widget instanceof Composite)
				return "no-focus";
			break;

		case SWT.NO_REDRAW_RESIZE:
			if (widget instanceof Composite)
				return "no-redraw-resize";
			break;

		case SWT.NO_MERGE_PAINTS:
			if (widget instanceof Composite)
				return "no-merge-paints";
			break;

		case SWT.PASSWORD:
			if (widget instanceof Text)
				return "password";
			// case SWT.NO_RADIO_GROUP:
			if (widget instanceof Composite)
				return "no-radio-group";
			break;

		case SWT.FLAT:
			if (widget instanceof Button || widget instanceof ToolBar)
				return "flat";
			break;

		case SWT.EMBEDDED:
			if (widget instanceof Composite)
				return "embedded";
			// case SWT.CENTER:
			if (widget instanceof Button || widget instanceof Label
					|| widget instanceof TableColumn
					|| ((Object) widget) instanceof FormAttachment)
				return "center";
			break;

		case SWT.LEFT_TO_RIGHT:
			if (widget instanceof Control || widget instanceof Menu
					|| ((Object) widget) instanceof GC)
				return "left-to-right";
			break;

		case SWT.RIGHT_TO_LEFT:
			if (widget instanceof Control || widget instanceof Menu
					|| ((Object) widget) instanceof GC)
				return "right-to-right";
			break;

		case SWT.MIRRORED:
			if (widget instanceof Control || widget instanceof Menu)
				return "mirrored";
			break;

		case SWT.VIRTUAL:
			if (widget instanceof Table || widget instanceof Tree)
				return "virtual";
			break;

		case SWT.DOUBLE_BUFFERED:
			if (widget instanceof Control)
				return "double-buffered";
			break;
		}
		return String.format("%08x", style);
	}

}
