package ba.pehli.activiti.service;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;

import ba.pehli.activiti.domain.Budget;
import ba.pehli.activiti.domain.Order;
import ba.pehli.activiti.standalone.ExecutionError;


public class TestBean {
	private BudgetDao budgetDao;
	
	public void kontrolaBudzeta(DelegateExecution execution) throws Exception{
		System.out.println("Kontrola budžeta");
		Order order = (Order)execution.getVariable("nar");

		try {
			Thread.sleep(30000);
		} catch (InterruptedException e){
			e.printStackTrace();
		}
		
		Budget budget = budgetDao.findByAccount(order.getAccount());
		
		if (budget.getAmount() >= order.getAmount()){
			order.setControl(true);
			execution.setVariable("nar", order);
			System.out.println("Postoji dovoljno sredstava u budzetu");
		} else {
			System.out.println("Ne postoji dovoljno sredstava u budzetu");
			ExecutionError error = new ExecutionError("ServiceException","Rezervacija budzeta nije uspjela. Nema dovoljno sredstava.");
			execution.setVariable("executionError",error);
		}
		
		
		System.out.println("Kontrola budžeta završena");
	}
	
	public void rezervacija(DelegateExecution execution) throws Exception{
		System.out.println("Rezervacija budžeta");
		Order order = (Order)execution.getVariable("nar");
		Budget budget = budgetDao.findByAccount(order.getAccount());
		
		budget.setAmount(budget.getAmount() - order.getAmount());
		budgetDao.save(budget);

		try {
			Thread.sleep(40000);
		} catch (InterruptedException e){
			e.printStackTrace();
		}
		
		System.out.println("Rezervacija budzeta zavrsena");
		
	}
	
	public void kreiranjeUlaza(DelegateExecution execution){
		System.out.println("Kreiranje ulaza");
		System.out.println("Kreiranje ulaza završeno");
	}
	
	public void executeShortTask(){
		System.out.println("*** TestBean.executeShortTask start");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e){
			e.printStackTrace();
		}
		System.out.println("*** TestBean.executeShortTask end");
	}
	
	public void print(DelegateExecution execution) throws Exception{
		System.out.println("Print...");
	}
	
	// execution listener postavljen na start eventu
	public void listener(DelegateExecution execution){
		System.out.println("Započinjem proces " + execution.getProcessInstanceId());
	}
	
	@Autowired
	public void setBudgetDao(BudgetDao budgetDao) {
		this.budgetDao = budgetDao;
	}
	
	
}
