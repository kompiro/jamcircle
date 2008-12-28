package org.kompiro.jamcircle.kanban.ui.widget;

import org.eclipse.gef.dnd.SimpleObjectTransfer;

public class CardObjectTransfer extends
		SimpleObjectTransfer {
	private static final CardObjectTransfer INSTANCE = new CardObjectTransfer();
	private static final String TYPE_NAME = "Card transfer"//$NON-NLS-1$
		+ System.currentTimeMillis()
		+ ":" + INSTANCE.hashCode();//$NON-NLS-1$
	private static final int TYPEID = registerType(TYPE_NAME);
	
	public static CardObjectTransfer getTransfer(){
		return INSTANCE;
	}
	
	@Override
	protected int[] getTypeIds() {
		return new int[]{TYPEID};
	}

	@Override
	protected String[] getTypeNames() {
		return new String[]{TYPE_NAME};
	}
}
