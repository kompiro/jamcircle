package abbot.swt.gef.tester;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;

/**
 * A tester for GEF {@link EditPartViewer}s.
 * 
 * @author Gary Johnston
 */
public class ViewerTester {
	
	private static ViewerTester Default = new ViewerTester();
	
	public static ViewerTester getDefault() {
		return Default;
	}
	
	public EditPart getFocusEditPart(EditPartViewer viewer) {
		return viewer.getFocusEditPart();
	}

}
