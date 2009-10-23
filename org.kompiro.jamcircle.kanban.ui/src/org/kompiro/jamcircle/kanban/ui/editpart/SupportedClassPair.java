package org.kompiro.jamcircle.kanban.ui.editpart;

import org.eclipse.gef.EditPart;
	public class SupportedClassPair{
		
		private Class<?> modelClass;
		private Class<? extends EditPart> contextClass;

		public SupportedClassPair(Class<? extends EditPart> context,Class<?> model) {
			this.contextClass = context;
			this.modelClass = model;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((contextClass == null) ? 0 : contextClass.hashCode());
			result = prime * result + ((modelClass == null) ? 0 : modelClass.hashCode());
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
			if (contextClass == null) {
				if (other.contextClass != null)
					return false;
			} else if (!contextClass.equals(other.contextClass))
				return false;
			if (modelClass == null) {
				if (other.modelClass != null)
					return false;
			} else if (!modelClass.equals(other.modelClass))
				return false;
			return true;
		}
		
		public boolean isSupported(EditPart context,Object model){
			return this.contextClass.isInstance(context) && this.modelClass.isInstance(model);
		}

		@Override
		public String toString() {
			return "SupportedClassPair [context=" + contextClass + ", model="
					+ modelClass + "]";
		}
		
		
		
		
	}
