package ba.pehli.activiti.standalone;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;

/**
 * Utility class for process image generation.
 * @author almir
 *
 */
public class ImageGenerator implements Runnable{
	private RepositoryService repositoryService;
	private RuntimeService runtimeService;
	private HistoryService historyService;
	private String processName;
	private String processInstanceId;
	
	public ImageGenerator(){
		
	}
	
	public ImageGenerator(RepositoryService repositoryService,RuntimeService runtimeService,HistoryService historyService,String processName, String processInstanceId){
		this.repositoryService = repositoryService;
		this.runtimeService = runtimeService;
		this.historyService = historyService;
		this.processName = processName;
		this.processInstanceId = processInstanceId;
	}

	@Override
	public void run() {
		
		createImage();
		//monitor();
	}
	
	public void createImage(){
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processName).singleResult();
		DefaultProcessDiagramGenerator pdg = new DefaultProcessDiagramGenerator();
		
		/*for (HistoricActivityInstance hi : historyService.createHistoricActivityInstanceQuery().executionId(processInstanceId).list()){
			System.out.println("završen " + hi.getActivityId() + "/" + hi.getActivityName());
		}*/
		List<String> activeActivities = runtimeService.getActiveActivityIds(processInstanceId);
		
		InputStream is = pdg.generateDiagram(repositoryService.getBpmnModel(pd.getId()),
				"png",runtimeService.getActiveActivityIds(processInstanceId));

		try {
			SimpleDateFormat format = new SimpleDateFormat("HHmmss");
			Date now = new Date();
			
			FileOutputStream fos = new FileOutputStream("proces-"+format.format(now)+".png");
			int c=-1;
			while ( (c=is.read()) != -1){
				fos.write(c);
			}
			fos.close();
			is.close();
		} catch (Exception e){
			e.printStackTrace();
		}	
	}
	
	private void monitor(){
		List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
		for (ProcessInstance pi : list){
			/*System.out.println("[Izvršavam proces " + pi.getProcessDefinitionKey() 
					+ " (" + pi.getProcessDefinitionId() + ")" + "]");*/
			for (String str : runtimeService.getActiveActivityIds(pi.getId())){
				System.out.println("   [Aktivni id:" + str + "]");
			}
		}
	}

	public RepositoryService getRepositoryService() {
		return repositoryService;
	}

	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	public RuntimeService getRuntimeService() {
		return runtimeService;
	}

	public void setRuntimeService(RuntimeService runtimeService) {
		this.runtimeService = runtimeService;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	
	
	
}
