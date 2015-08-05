package ba.pehli.activiti.service;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ba.pehli.activiti.domain.Budget;

@Transactional
@Service("budgetDao")
public class BudgetDaoImpl implements BudgetDao{
	
	private SessionFactory sessionFactory;

	@Override
	public Budget findByAccount(String account) {
		return (Budget) sessionFactory.getCurrentSession().getNamedQuery("Budget.findByAccount").setParameter("account", account).uniqueResult();
	}
	
	
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}



	@Override
	public Budget save(Budget budget) {
		sessionFactory.getCurrentSession().saveOrUpdate(budget);
		return null;
	}



	@Override
	public List<Budget> findAll() {
		return sessionFactory.getCurrentSession().createQuery("select b from Budget b").list();
	}

}
