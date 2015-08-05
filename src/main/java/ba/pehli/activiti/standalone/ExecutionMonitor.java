package ba.pehli.activiti.standalone;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;



public class ExecutionMonitor implements ActivitiEventListener,ApplicationContextAware,ExecutionListener {
	
	private ApplicationContext ctx;
	private ImageGenerator imageTask;

	@Override
	public boolean isFailOnException() {
		return false;
	}

	@Override
	public void onEvent(ActivitiEvent event) {
		switch (event.getType()) {
	      case ACTIVITY_STARTED:
	        createImage(event.getProcessInstanceId());
	        break;
	      default:
	    }
		
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;
		
	}
	
	private void createImage(String processInstanceId){
		if (imageTask == null){
			RepositoryService repositoryService = ctx.getBean("repositoryService", RepositoryService.class);
			RuntimeService runtimeService = ctx.getBean("runtimeService", RuntimeService.class);
			HistoryService historyService = ctx.getBean("historyService", HistoryService.class);
			imageTask = new ImageGenerator(repositoryService, runtimeService, historyService, BpmnExplorer.PROCESS_KEY, processInstanceId);
		}
		imageTask.createImage();
	}

	@Override
	public void notify(DelegateExecution arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
