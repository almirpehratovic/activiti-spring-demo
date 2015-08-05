package ba.pehli.activiti.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ba.pehli.activiti.domain.Order;
import ba.pehli.activiti.domain.AppUser;
import ba.pehli.activiti.domain.UserTask;
import ba.pehli.activiti.service.BudgetDao;
import ba.pehli.activiti.service.OrderDao;
import ba.pehli.activiti.service.UserDao;

@Controller
@RequestMapping("/orders")
public class OrdersController implements ApplicationContextAware{
	public static final String PROCESS_KEY = "helloProcess";
	private ApplicationContext ctx;
	
	private OrderDao orderDao;
	private UserDao userDao;
	private BudgetDao budgetDao;
	

	@RequestMapping(method=RequestMethod.GET)
	public String showOrders(Model model){
		model.addAttribute("orders", orderDao.findAllOrders());
		model.addAttribute("budgets", budgetDao.findAll());
		return "orders/list";
	}
	
	@RequestMapping(value="/start/{id}",method=RequestMethod.GET)
	public String startProcess(@PathVariable("id") int id ,Model model){
		
		ProcessEngine pe = ctx.getBean("processEngine", ProcessEngine.class);
		/*RepositoryService repositoryService = ctx.getBean("repositoryService", RepositoryService.class);
		IdentityService identityService = ctx.getBean("identityService",IdentityService.class);
		TaskService taskService = ctx.getBean("taskService", TaskService.class);*/
		RuntimeService runtimeService = ctx.getBean("runtimeService", RuntimeService.class);
		
		try {
			Order order = orderDao.findOrderById(id);
			
			Map<String, Object> params = new HashMap<String, Object>();
			AppUser user = userDao.getAuthenticatedUser();
			params.put("user", user);
			
			List<String> roles = new ArrayList<String>();
			roles.add("kontrolor");
			roles.add("menadzer");
			params.put("roles", roles);
			params.put("nar", order);

			String processInstanceId = runtimeService.startProcessInstanceByKey(PROCESS_KEY, params).getId();
			order.setExecutionId(processInstanceId);
			order = orderDao.save(order);
			
			params.put("nar", order);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return "redirect:/orders";
	}
	
	@RequestMapping(value="/view/{id}", method=RequestMethod.GET)
	public String showPocessImage(@PathVariable("id") int id ,Model model) {
		Order order = orderDao.findOrderById(id);
		model.addAttribute("order", order);
		return "orders/view";
	}
	
	@RequestMapping(value="/image/{processInstanceId}", method=RequestMethod.GET)
	@ResponseBody
	public byte[] generatePocessImage(@PathVariable("processInstanceId") String processInstanceId ,Model model) {
		ProcessEngine pe = ctx.getBean("processEngine", ProcessEngine.class);
		RepositoryService repositoryService = ctx.getBean("repositoryService", RepositoryService.class);
		RuntimeService runtimeService = ctx.getBean("runtimeService", RuntimeService.class);
		
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionKey(PROCESS_KEY).singleResult();
		
		List<String> activeActivities = runtimeService.getActiveActivityIds(processInstanceId);
		
		DefaultProcessDiagramGenerator pdg = new DefaultProcessDiagramGenerator();
		InputStream is = pdg.generateDiagram(repositoryService.getBpmnModel(pd.getId()),
				"png",runtimeService.getActiveActivityIds(processInstanceId));
		
		byte[] bytes = null;
		try {
			bytes = IOUtils.toByteArray(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return bytes;
	}
	
	@RequestMapping(value="/tasks", method=RequestMethod.GET)
	public String showTasks(Model model) {
		ProcessEngine pe = ctx.getBean("processEngine", ProcessEngine.class);
		IdentityService identityService = ctx.getBean("identityService", IdentityService.class);
		TaskService taskService = ctx.getBean("taskService",TaskService.class);
		RuntimeService runtimeService = ctx.getBean("runtimeService", RuntimeService.class);
		
		AppUser appUser = userDao.getAuthenticatedUser();
		Group group = identityService.createGroupQuery().groupId(appUser.getRole()).singleResult();
		
		List<UserTask> tasks = new ArrayList<UserTask>();
		for (Task activitiTask : taskService.createTaskQuery().taskCandidateGroup(group.getId()).list()){
			UserTask userTask = new UserTask();
			userTask.setId(activitiTask.getId());
			userTask.setName(activitiTask.getName());
			userTask.setPriority(activitiTask.getPriority());
			userTask.setDateCreated(activitiTask.getCreateTime());
			userTask.setDateDue(activitiTask.getDueDate());
			tasks.add(userTask);
			
			Map<String, Object> variables = new HashMap<String, Object>();
			for (String key : runtimeService.getVariables(activitiTask.getExecutionId()).keySet()){
				Object var = runtimeService.getVariables(activitiTask.getExecutionId()).get(key);
				if (var.getClass().getName().startsWith("ba.pehli")){
					variables.put(key, var);
				}
			}
			userTask.setVariables(variables);
		}
		model.addAttribute("tasks", tasks);
		
		return "orders/tasks";
	}
	
	@RequestMapping(value="/tasks/{taskId}", method=RequestMethod.GET)
	public String completeTask(@PathVariable("taskId") String taskId,Model model) {
		TaskService taskService = ctx.getBean("taskService",TaskService.class);
		taskService.complete(taskId);
		return "redirect:/orders/tasks";
	}
	
	
	@Autowired
	public void setOrderDao(OrderDao orderDao) {
		this.orderDao = orderDao;
	}
	
	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	
	@Autowired
	public void setBudgetDao(BudgetDao budgetDao) {
		this.budgetDao = budgetDao;
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;
		
	}
	
	
	
	
}
