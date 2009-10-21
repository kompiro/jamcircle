package org.kompiro.jamcircle.kanban.ui.editpart;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

public interface ExtendedEditPartFactory extends EditPartFactory {

	public SupportedClassPair[] supportedClasses();
	
	class SupportedClassPair{
		
		private Class<?> model;
		private Class<? extends EditPart> context;

		public SupportedClassPair(Class<? extends EditPart> context,Class<?> model) {
			this.context = context;
			this.model = model;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((context == null) ? 0 : context.hashCode());
			result = prime * result + ((model == null) ? 0 : model.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SupportedClassPair other = (SupportedClassPair) obj;
			if (context == null) {
				if (other.context != null)
					return false;
			} else if (!context.equals(other.context))
				return false;
			if (model == null) {
				if (other.model != null)
					return false;
			} else if (!model.equals(other.model))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "SupportedClassPair [context=" + context + ", model="
					+ model + "]";
		}
		
		
		
		
	}
	
}
