package org.kompiro.jamcircle.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;

public class Servlet extends AbstractApplicationServlet {

	private static final long serialVersionUID = -3262864888085488528L;

	@Override
	protected Class<? extends Application> getApplicationClass() {
		return MyprojectApplication.class;
	}

	@Override
	protected Application getNewApplication(HttpServletRequest request) throws ServletException {
		return new MyprojectApplication();
	}

}