package ba.pehli.activiti.standalone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.context.support.GenericXmlApplicationContext;

import ba.pehli.activiti.domain.Order;
import ba.pehli.activiti.domain.AppUser;
import ba.pehli.activiti.service.OrderDao;
import ba.pehli.activiti.service.UserDao;

/**
 * Starts standalone example of process. Every finished process activity will generate 
 * process picture in root folder of project.
 * @author almir
 *
 */
public class BpmnExplorer {
	public static final String PROCESS_KEY = "helloProcess";
	public static void main(String[] args){
		GenericXmlApplicationContext ctx = new GenericXmlApplicationContext("classpath:spring/app-context-standalone.xml");
		ProcessEngine pe = ctx.getBean("processEngine", ProcessEngine.class);
		RepositoryService repositoryService = ctx.getBean("repositoryService", RepositoryService.class);
		IdentityService identityService = ctx.getBean("identityService",IdentityService.class);
		TaskService taskService = ctx.getBean("taskService", TaskService.class);
		RuntimeService runtimeService = ctx.getBean("runtimeService", RuntimeService.class);
		
		UserDao userDao = ctx.getBean("userDao", UserDao.class);
		OrderDao orderDao = ctx.getBean("orderDao", OrderDao.class);
		
		repositoryService.createDeployment().addClasspathResource("bpmn/HelloProcess.bpmn20.xml").deploy().getId();
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		AppUser user = userDao.findByUsername("mujo.mujic");
		Order nar = orderDao.findOrderById(1);
		
		
		params.put("nar", nar); 
		params.put("user", user);
		
		List<String> roles = new ArrayList<String>();
		roles.add("kontrolor");
		roles.add("menadzer");
		params.put("roles", roles);
		
		String processInstanceId = runtimeService.startProcessInstanceByKey(PROCESS_KEY, params).getId();
		
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		

		UserSimulation userTask = new UserSimulation("kontrolor", "Nizama Buljubušić", identityService, taskService);
		executor.scheduleAtFixedRate(userTask, 20, 3600, TimeUnit.SECONDS);
		
		UserSimulation userTask2 = new UserSimulation("menadzer", "Dževad Alihođić", identityService, taskService);
		executor.scheduleAtFixedRate(userTask2, 50, 3600, TimeUnit.SECONDS);
		
	}
}
