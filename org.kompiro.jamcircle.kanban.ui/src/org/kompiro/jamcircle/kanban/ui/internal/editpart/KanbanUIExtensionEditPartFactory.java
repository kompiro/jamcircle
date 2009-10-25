package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import static java.lang.String.format;

import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.kompiro.jamcircle.kanban.ui.editpart.ExtendedEditPartFactory;
import org.kompiro.jamcircle.kanban.ui.editpart.SupportedClassPair;

public class KanbanUIExtensionEditPartFactory implements EditPartFactory {
	static final String POINT_CALLBACK = "org.kompiro.jamcircle.kanban.ui.editPartFactory";
	static final String ATTR_HANDLER_CLASS = "class";

	private static final String PLUGIN_ID = KanbanUIActivator.ID_PLUGIN;
	private Map<SupportedClassPair, ExtendedEditPartFactory> factories = new HashMap<SupportedClassPair, ExtendedEditPartFactory>();
	private IExtensionRegistry registry = RegistryFactory.getRegistry();

	public KanbanUIExtensionEditPartFactory() throws IllegalStateException{
	}

	void initialize() throws IllegalStateException{
		IExtensionPoint point = registry.getExtensionPoint(POINT_CALLBACK);
		if(point == null) return;
		IExtension[] extensions = point.getExtensions();
		if(extensions == null) return;
		MultiStatus statuses = new MultiStatus(PLUGIN_ID, Status.ERROR, "error has occured when initializing editpart factories.", null);
		for (IExtension extension:extensions) {
			IConfigurationElement[] confElements = extension.getConfigurationElements();
			for(IConfigurationElement element : confElements){
				ExtendedEditPartFactory factory = null;
				try {
					factory = (ExtendedEditPartFactory) element.createExecutableExtension(ATTR_HANDLER_CLASS);
				} catch (Exception e) {
					statuses.add(KanbanUIActivator.createErrorStatus(e));
				}
				if(factory != null){
					for(SupportedClassPair pair : factory.supportedClasses()){
						if(factories.containsKey(pair)){
							String message = format("class:'%s' is already registered.",pair.toString());
							IllegalStateException e = new IllegalStateException(message);
							statuses.add(KanbanUIActivator.createErrorStatus(e));
							continue;
						}
						factories.put(pair, factory);
					}
				}
			}
		}
		if(!statuses.isOK()){
			throw new IllegalStateException(statuses.getException());
		}
	}

	public EditPart createEditPart(EditPart context, Object model) {
		if(context == null) throw new IllegalArgumentException("Unsupported Object");
		EditPartFactory factory = null;
		Set<SupportedClassPair> set = factories.keySet();
		for(SupportedClassPair pair : set){
			if(pair.isSupported(context, model)){
				factory = factories.get(pair);			
			}
		}
		if(factory == null) throw new IllegalArgumentException("Unsupported Object");
		return factory.createEditPart(context, model);
	}
	
	void setRegistry(IExtensionRegistry registry) {
		this.registry = registry;
	}
	
	IExtensionRegistry getRegistry(){
		return this.registry;
	}


}
