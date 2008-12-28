package abbot.swt.tester;

import org.eclipse.swt.widgets.TableColumn;

import abbot.swt.Robot;

public class TableColumnTester extends ItemTester {

	/**
	 * Factory method.
	 */
	public static TableColumnTester getTableColumnTester() {
		return (TableColumnTester) WidgetTester.getTester(TableColumn.class);
	}

	/**
	 * Constructs a new {@link TableColumnTester} associated with the specified
	 * {@link abbot.swt.Robot}.
	 */
	public TableColumnTester(Robot swtRobot) {
		super(swtRobot);
	}

}
