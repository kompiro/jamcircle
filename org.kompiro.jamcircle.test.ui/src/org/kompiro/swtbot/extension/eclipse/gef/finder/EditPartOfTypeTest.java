package org.kompiro.swtbot.extension.eclipse.gef.finder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.gef.*;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.junit.Test;

public class EditPartOfTypeTest {

	public class FigureEditPart extends AbstractEditPart {

		public DragTracker getDragTracker(Request arg0) {
			return null;
		}

		@Override
		protected void addChildVisual(EditPart arg0, int arg1) {
		}

		@Override
		protected void createEditPolicies() {
		}

		@Override
		protected void removeChildVisual(EditPart arg0) {
		}

	}

	public class DummyFigureEditPart extends AbstractEditPart {

		public DragTracker getDragTracker(Request arg0) {
			return null;
		}

		@Override
		protected void addChildVisual(EditPart arg0, int arg1) {
		}

		@Override
		protected void createEditPolicies() {
		}

		@Override
		protected void removeChildVisual(EditPart arg0) {
		}

	}

	@Test
	public void isMatch() throws Exception {
		EditPartOfType<EditPart> matcher = new EditPartOfType<EditPart>(FigureEditPart.class);
		assertThat(matcher.doMatch(new FigureEditPart()), is(true));
		assertThat(matcher.doMatch(new DummyFigureEditPart()), is(false));
	}

}
