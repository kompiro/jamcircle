package org.kompiro.jamcircle.kanban.service.internal;

import net.java.ao.EntityManager;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class KanbanServiceEnvironment implements MethodRule {

	private KanbanServiceTestHelper helper;
	private EntityManager entityManager;

	public Statement apply(Statement base, FrameworkMethod method, Object target) {
		helper = new KanbanServiceTestHelper();
		helper.forceInitKanbanService();
		entityManager = helper.getEntityManager();

		return base;
	}

	public KanbanServiceTestHelper getHelper() {
		return helper;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

}
