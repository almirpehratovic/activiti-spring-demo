package ba.pehli.activiti.standalone;

import org.activiti.engine.IdentityService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.task.Task;

public class UserSimulation implements Runnable{
	private IdentityService identityService;
	private TaskService taskService;
	private String role;
	private String username;
	
	public UserSimulation(String role,String username,IdentityService identityService,TaskService taskService){
		this.role = role;
		this.username = username;
		this.identityService = identityService;
		this.taskService = taskService;
	}

	@Override
	public void run() {
		Group group = identityService.newGroup(this.role);
		User user = identityService.newUser(this.username);
		identityService.saveGroup(group);
		identityService.saveUser(user);
		identityService.createMembership(user.getId(), group.getId());
		identityService.setAuthenticatedUserId(user.getId());
		
		System.out.println("   [User " + username + " iz role " + role + " se logovao na sistem]");
		for (Task task : taskService.createTaskQuery().taskCandidateGroup(group.getId()).list()){
			System.out.println("   [User primjetio task " + task.getName() + "]");
			taskService.claim(task.getId(), user.getId());
			System.out.println("   [User preuzeo task " + task.getName() + "]");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e){
				e.printStackTrace();
			}
			taskService.complete(task.getId());
			System.out.println("   [User završio task " + task.getName() + "]");
			break;
		}
	}
}
