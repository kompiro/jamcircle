package abbot.swt.tester;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.Log;
import abbot.swt.utilities.Cache;

public class WidgetTesterFactory implements WidgetTester.Factory {

	/**
	 * A {@link List} of packages to be searched for {@link WidgetTester} {@link Class}es.
	 */
	private final Map<String, ClassLoader> packages;

	/**
	 * A cache of {@link WidgetTester}s that can be looked up by {@link Widget} {@link Class}.
	 */
	private final Cache<Class, WidgetTester> testers;

	/**
	 * A cache of {@link WidgetTester} {@link Class}es that can be looked up by {@link Widget}
	 * {@link Class}.
	 */
	private final Cache<Class, Class> classes;

	private final abbot.swt.Robot swtRobot;

	public WidgetTesterFactory(abbot.swt.Robot swtRobot) {

		// AbstractTester.
		this.swtRobot = swtRobot;

		// Tester package registry.
		packages = new HashMap<String, ClassLoader>();

		// Cached testers.
		testers = new Cache<Class, WidgetTester>() {
			protected WidgetTester newValue(Class widgetClass) {
				return createTester(widgetClass);
			}
		};

		// Cached tester classes.
		classes = new Cache<Class, Class>() {
			protected Class newValue(Class widgetClass) {
				return findTesterClass(widgetClass);
			}
		};
	}

	/**
	 * Adds a package name to the list of packages to be searched for {@link WidgetTester}s
	 * 
	 * @param packageName
	 * @see #packages
	 * @see #findTesterClass(Class)
	 */
	public synchronized void addPackage(String packageName, ClassLoader classLoader) {
		checkPackageName(packageName);
		packages.put(packageName, classLoader);
	}

	/**
	 * Removes a package name from the list of packages to be searched for {@link WidgetTester}s
	 * 
	 * @param packageName
	 * @see #packages
	 * @see #findTesterClass(Class)
	 */
	public synchronized void removePackage(String packageName) {
		checkPackageName(packageName);
		packages.remove(packageName);
	}

	private void checkPackageName(String packageName) {
		if (packageName == null)
			throw new IllegalArgumentException("packageName cannot be null");
		// TODO Validate package name.
	}

	/**
	 * Returns a {@link WidgetTester} for the specified {@link Widget} {@link Class}.
	 * 
	 * @param widgetClass
	 * @return the {@link WidgetTester}
	 */
	public synchronized WidgetTester getTester(Class widgetClass) {
		checkWidgetClass(widgetClass);
		return testers.get(widgetClass);
	}

	/**
	 * Returns a {@link WidgetTester} for the specified {@link Widget} {@link Class}.
	 * 
	 * @param widget
	 *            the {@link Widget} for which a {@link WidgetTester} is wanted
	 * @return a {@link WidgetTester}
	 */
	public WidgetTester getTester(Widget widget) {
		checkWidget(widget);
		return getTester(widget.getClass());
	}

	public synchronized void setTester(Class widgetClass, WidgetTester tester) {
		checkWidgetClass(widgetClass);
		checkWidgetTester(tester);
		testers.put(widgetClass, tester);
	}

	private void checkWidget(Widget widget) {
		if (widget == null)
			throw new IllegalArgumentException("widget cannot be null");
		checkDisplay(widget.getDisplay());
		// if (widget.isDisposed())
		// throw new IllegalArgumentException("widget is disposed: " + widget);
	}

	private void checkWidgetClass(Class widgetClass) {
		if (widgetClass == null)
			throw new IllegalArgumentException("widgetClass cannot be null");
		if (!Widget.class.isAssignableFrom(widgetClass))
			throw new IllegalArgumentException("invalid widget class: " + widgetClass);
	}

	private void checkWidgetTester(WidgetTester tester) {
		if (tester == null)
			throw new IllegalArgumentException("tester cannot be null");
		checkDisplay(tester.getDisplay());
	}

	private void checkDisplay(Display display) {
		if (display == null)
			throw new IllegalArgumentException("display cannot be null");
		if (display != swtRobot.getDisplay())
			throw new IllegalArgumentException("invalid display");
	}

	/**
	 * @param widgetClass
	 * @return a {@link WidgetTester}
	 * @see #testers
	 */
	private WidgetTester createTester(Class widgetClass) {
		Class testerClass = getTesterClass(widgetClass);
		if (testerClass != null) {
			Constructor constructor = getConstructor(
					testerClass,
					new Class[] { abbot.swt.Robot.class });
			if (constructor != null)
				return newInstance(constructor, new Object[] { swtRobot });
		}
		return null;
	}

	private Constructor getConstructor(Class testerClass, Class[] parameterTypes) {
		try {
			return testerClass.getDeclaredConstructor(parameterTypes);
		} catch (NoSuchMethodException e) {
			Log.warn(e);
		}
		return null;
	}

	private WidgetTester newInstance(Constructor constructor, Object[] arguments) {
		try {
			return (WidgetTester) constructor.newInstance(arguments);
		} catch (IllegalArgumentException e) {
			Log.warn(e);
		} catch (InstantiationException e) {
			Log.warn(e);
		} catch (IllegalAccessException e) {
			Log.warn(e);
		} catch (InvocationTargetException e) {
			Log.warn(e);
		}
		return null;
	}

	private Class getTesterClass(Class widgetClass) {
		checkWidgetClass(widgetClass);
		return classes.get(widgetClass);
	}

	/**
	 * @param widgetClass
	 * @return the {@link WidgetTester} {@link Class} (or <code>null</code> if there isn't one)
	 * @see #classes
	 */
	private synchronized Class findTesterClass(Class widgetClass) {

		String testerClassName = widgetClass.getSimpleName() + "Tester";
		for (Entry<String, ClassLoader> entry : packages.entrySet()) {
			String fullTesterClassName = entry.getKey() + "." + testerClassName;
			Class testerClass = resolveClass(fullTesterClassName, entry.getValue());
			if (testerClass != null)
				return testerClass;
		}

		// Try superclass.
		if (widgetClass != Widget.class)
			return findTesterClass(widgetClass.getSuperclass());

		return null;
	}

	private Class resolveClass(String fullClassName, ClassLoader classLoader) {
		try {
			if (classLoader == null)
				return Class.forName(fullClassName);
			return Class.forName(fullClassName, true, classLoader);
		} catch (ClassNotFoundException e) {
			// Empty block intended.
		}
		return null;
	}

}
