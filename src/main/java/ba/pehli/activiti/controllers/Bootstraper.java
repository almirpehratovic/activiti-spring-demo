package ba.pehli.activiti.controllers;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import ba.pehli.activiti.domain.AppUser;
import ba.pehli.activiti.service.UserDao;


/**
 * Listener that create users and user groups exactly upon engine creation.
 * @author almir
 *
 */
public class Bootstraper implements ActivitiEventListener,ApplicationContextAware {
	
	private ApplicationContext ctx;

	@Override
	public boolean isFailOnException() {
		return false;
	}

	@Override
	public void onEvent(ActivitiEvent event) {
		switch (event.getType()) {
	      case ENGINE_CREATED:
	    	  insertUsers();
	    	  break;
	      default:
	    }
		
	}
	
	/**
	 * Reads all users from database and inserts in ProcessEngine with the help
	 * of IdentityService
	 */
	private void insertUsers() {
		IdentityService identityService = ctx.getBean("identityService",IdentityService.class);
		UserDao userDao = ctx.getBean("userDao", UserDao.class);
		Map<String, Group> createdGroups = new HashMap<String, Group>();
		
		for (AppUser appUser : userDao.findAll()){
			
			Group group = null;
			if (!createdGroups.containsKey(appUser.getRole())){
				group = identityService.newGroup(appUser.getRole());
				identityService.saveGroup(group);
				createdGroups.put(appUser.getRole(), group);
			} else {
				group = createdGroups.get(appUser.getRole());
			}
			User user = identityService.newUser(appUser.getUsername());
			
			identityService.saveUser(user);
			identityService.createMembership(user.getId(), group.getId());
		}
		
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;
		
	}
	
	
}
