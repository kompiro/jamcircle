package abbot.swt.gef.finder.matchers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.draw2d.IFigure;

import abbot.swt.finder.generic.ClassMatcher;
import abbot.swt.utilities.ExtendedComparator;

/**
 * An implementation of {@link ClassMatcher} that provides matching of {@link IFigure}s that have
 * text.
 */
public class FigureTextMatcher extends ClassMatcher<IFigure> {

	/**
	 * An empty array of Classes.
	 * 
	 * @see #getText(IFigure)
	 */
	private static final Class[] EMPTY_CLASS_ARRAY = {};

	/**
	 * An empty array of Objects.
	 * 
	 * @see #getText(IFigure)
	 */
	private static final Object[] EMPTY_OBJECT_ARRAY = {};

	private final String text;

	/**
	 * Constructs a new {@link FigureTextMatcher} that will match a specified IFigure subclass that
	 * has the specified text.
	 * 
	 * @param theClass
	 *            the class to match
	 * @param text
	 *            the text to match
	 */
	public FigureTextMatcher(Class theClass, String text) {
		super(theClass);
		this.text = text;
	}

	/**
	 * Constructs a new {@link FigureTextMatcher} that will match an IFigure that has the specified
	 * text.
	 * 
	 * @param text
	 *            the text to match
	 */
	public FigureTextMatcher(String text) {
		this(IFigure.class, text);
	}

	/**
	 * @see abbot.swt.finder.generic.ClassMatcher#matches(java.lang.Object)
	 */
	public boolean matches(IFigure figure) {
		if (super.matches(figure)) {
			String figureText = getText(figure);
			if (figureText != null)
				return ExtendedComparator.stringsMatch(text, figureText);
		}
		return false;
	}

	/**
	 * Gets an {@link IFigure}'s text, if it can.
	 * 
	 * @param figure
	 *            the IFigure whose text to get
	 * @return the {@link IFigure}'s text (or null if it hasn't any).
	 */
	private String getText(IFigure figure) {
		try {
			Method method = figure.getClass().getMethod("getText", EMPTY_CLASS_ARRAY);
			return (String) method.invoke(figure, EMPTY_OBJECT_ARRAY);
		} catch (SecurityException e) {
			// Empty block intended.
		} catch (NoSuchMethodException e) {
			// Empty block intended.
		} catch (IllegalArgumentException e) {
			// Empty block intended.
		} catch (IllegalAccessException e) {
			// Empty block intended.
		} catch (InvocationTargetException e) {
			// Empty block intended.
		}
		return null;
	}
}
