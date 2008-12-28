package abbot.swt.script;

/**
 * This is just a clone of <code>abbot.script.Condition</code>. It exists just to remove a
 * dependency on abbot.plain.
 * 
 * @see abbot.script.Condition
 */
public interface Condition {

	boolean test();

}
