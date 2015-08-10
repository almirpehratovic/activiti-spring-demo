package ba.pehli.activiti.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
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
import ba.pehli.activiti.utils.CustomProcessDiagramGenerator;

/**
 * Main controller in aplication. Can start bpmn process and enables user task processing.
 * @author almir
 *
 */

@Controller
@RequestMapping("/orders")
public class OrdersController implements ApplicationContextAware{
	public static final String PROCESS_KEY = "helloProcess";
	private ApplicationContext ctx;
	
	private OrderDao orderDao;
	private UserDao userDao;
	private BudgetDao budgetDao;
	
	
	/**
	 * Show main page with all orders and budgets
	 * @param model
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)
	public String showOrders(Model model){
		model.addAttribute("orders", orderDao.findAllOrders());
		model.addAttribute("budgets", budgetDao.findAll());
		return "orders/list";
	}
	
	
	/**
	 * Starts bpmn process
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/start/{id}",method=RequestMethod.GET)
	public String startProcess(@PathVariable("id") int id ,Model model){
		
		ProcessEngine pe = ctx.getBean("processEngine", ProcessEngine.class);
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
	
	/**
	 * Just shows page with image of process
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/view/{id}", method=RequestMethod.GET)
	public String showPocessImage(@PathVariable("id") int id ,Model model) {
		Order order = orderDao.findOrderById(id);
		model.addAttribute("order", order);
		return "orders/view";
	}
	
	/**
	 * Generates process image
	 * @param processInstanceId
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/image/{processInstanceId}", method=RequestMethod.GET)
	@ResponseBody
	public byte[] generatePocessImage(@PathVariable("processInstanceId") String processInstanceId ,Model model) {
		ProcessEngine pe = ctx.getBean("processEngine", ProcessEngine.class);
		RepositoryService repositoryService = ctx.getBean("repositoryService", RepositoryService.class);
		RuntimeService runtimeService = ctx.getBean("runtimeService", RuntimeService.class);
		HistoryService historyService = ctx.getBean("historyService", HistoryService.class);
		
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionKey(PROCESS_KEY).singleResult();
		ProcessDefinitionEntity pde = (ProcessDefinitionEntity) pd;
		
		boolean isFinished = false;
		List<String> activeActivities = new ArrayList<String>();
		try {
			activeActivities = runtimeService.getActiveActivityIds(processInstanceId);
		} catch (ActivitiObjectNotFoundException e){
			// process is finished, search for it in history
			isFinished = true;
			List<HistoricActivityInstance> haiList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
			for (HistoricActivityInstance hai : haiList){
				activeActivities.add(hai.getActivityId());
			}
		}
		
		System.out.println(activeActivities);
			
		ProcessDiagramGenerator pdg = null;
		if (isFinished){
			pdg = new CustomProcessDiagramGenerator();	// GREEN
		} else {
			pdg = new DefaultProcessDiagramGenerator(); // RED
		}
		
		InputStream is = pdg.generateDiagram(repositoryService.getBpmnModel(pd.getId()),
				"png",activeActivities);
		
		byte[] bytes = null;
		try {
			bytes = IOUtils.toByteArray(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return bytes;
	}
	
	/**
	 * Shows page with all user tasks for authenticated user and his role
	 * @param model
	 * @return
	 */
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
	
	/**
	 * Approving of user task
	 * @param taskId
	 * @param model
	 * @return
	 */
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
