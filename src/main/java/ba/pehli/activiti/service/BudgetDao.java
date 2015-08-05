package ba.pehli.activiti.service;

import java.util.List;

import ba.pehli.activiti.domain.Budget;

public interface BudgetDao {
	List<Budget> findAll();
	Budget findByAccount(String account);
	Budget save(Budget budget);
}
