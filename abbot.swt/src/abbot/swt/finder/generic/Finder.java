package abbot.swt.finder.generic;

public interface Finder<Node> {

	Node find(Matcher<Node> matcher) throws NotFoundException, MultipleFoundException;

	Node find(Node node, Matcher<Node> matcher) throws NotFoundException, MultipleFoundException;

}
