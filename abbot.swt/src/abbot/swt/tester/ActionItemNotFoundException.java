package abbot.swt.tester;

public class ActionItemNotFoundException extends ActionFailedException {

	private static final long serialVersionUID = 435730086372064725L;

	public ActionItemNotFoundException(String name) {
		super("could not find \"" + name + "\"");
	}
}
