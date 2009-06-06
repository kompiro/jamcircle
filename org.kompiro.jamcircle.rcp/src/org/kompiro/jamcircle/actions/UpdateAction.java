package org.kompiro.jamcircle.actions;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.update.search.BackLevelFilter;
import org.eclipse.update.search.EnvironmentFilter;
import org.eclipse.update.search.UpdateSearchRequest;
import org.eclipse.update.search.UpdateSearchScope;
import org.eclipse.update.ui.UpdateJob;
import org.eclipse.update.ui.UpdateManagerUI;

public class UpdateAction extends Action {

	private IWorkbenchWindow window;

	public UpdateAction(IWorkbenchWindow window){
		super("&Update JAM Circle");
		setId("org.kompiro.jamcircle.addextensions");
		setToolTipText("Update JAMCircle");
		this.window = window;
	}
	
	@Override
	public void run() {
		BusyIndicator.showWhile(window.getShell().getDisplay(), new Runnable(){
			public void run() {
				UpdateJob job = new UpdateJob("Update JAM Circle",getSearchRequest());
				UpdateManagerUI.openInstaller(window.getShell(),job);
			}

			private UpdateSearchRequest getSearchRequest() {
				UpdateSearchRequest result = new UpdateSearchRequest(UpdateSearchRequest.createDefaultSiteSearchCategory(),new UpdateSearchScope());
				result.addFilter(new BackLevelFilter());
				result.addFilter(new EnvironmentFilter());
				UpdateSearchScope scope = new UpdateSearchScope();
				try{
					String homebase = System.getProperty("jamcircle.homebase","http://dl.getdropbox.com/u/221122/JAMCircle/updatesite/site.xml");
					URL url = new URL(homebase);
					scope.addSearchSite("JAMCircle site",url,null);
				}catch(MalformedURLException e){
					
				}result.setScope(scope);
				return result;
			}
		});
	}
	
}
