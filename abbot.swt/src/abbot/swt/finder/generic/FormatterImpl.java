package abbot.swt.finder.generic;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.graphics.Rectangle;

import abbot.swt.finder.generic.HierarchyPrinter.Formatter;

public abstract class FormatterImpl<Node> implements Formatter<Node> {

	protected final java.util.Formatter formatter;

	protected FormatterImpl(Appendable appendable) {
		formatter = new java.util.Formatter(appendable);
	}
	
	protected FormatterImpl() {
		formatter = new java.util.Formatter();
	}
	
	public Appendable out() {
		return formatter.out();
	}
	
	protected void indent(int indent) {
		for (int i = 0; i < indent; i++) {
			try {
				out().append(' ');
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	protected void print(char c) {
		try {
			out().append(c);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected void println() {
		print('\n');
	}

	protected void printf(String format, Object... args) {
		formatter.format(format, args);
	}

	protected void printLocation(Rectangle bounds) {
		if (bounds == null)
			printf(" {unknown}");
		else if (bounds.isEmpty())
			printf(" {nowhere}");
		else {
			printf(" {%dx%d @ %d,%d} ", bounds.width, bounds.height, bounds.x, bounds.y);
		}
	}
	
	/* Methods for getting & printing useful text from things. */
	
	protected void printText(Object object) {
		
		String name = getName(object);
		if (name != null)
			printf(" name:{%s}", name);
		
		String label = getLabel(object);
		if (label != null)
			printf(" label:{%s}", label);
		
		String text = getText(object);
		if (text != null)
			printf(" text:{%s}", text);
	}



	protected String getText(Object object) {
		return getString(object, "getText");
	}

	protected String getLabel(Object object) {
		return getString(object, "getLabel");
	}

	protected String getName(Object object) {
		return getString(object, "getName");
	}

	protected String getString(Object object, String methodName) {
		Method method = getMethod(object.getClass(), methodName);
		if (method != null) {
			Object result = invoke(method, object);
			if (result instanceof String)
				return (String) result;
		}
		return null;
	}

	private Method getMethod(Class clazz, String methodName, Class... paramTypes) {
		try {
			return clazz.getMethod(methodName, paramTypes);
		} catch (SecurityException e) {
			return null;
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	private Object invoke(Method method, Object object, Object... args) {
		try {
			return method.invoke(object, args);
		} catch (IllegalAccessException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		}
	}

}
