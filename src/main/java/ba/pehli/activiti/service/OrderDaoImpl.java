package ba.pehli.activiti.service;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ba.pehli.activiti.domain.Order;

/**
 * Database operations for Order class
 * @author almir
 *
 */
@Service("orderDao")
@Transactional
public class OrderDaoImpl implements OrderDao{
	
	private SessionFactory sessionFactory;

	@Override
	public Order findOrderById(int id) {
		return (Order) sessionFactory.getCurrentSession().createQuery("select o from Order o where o.id=:id").setParameter("id", id).uniqueResult();
	}

	@Override
	public List<Order> findAllOrders() {
		return sessionFactory.getCurrentSession().createQuery("select o from Order o").list();
	}

	@Override
	public Order save(Order order) {
		sessionFactory.getCurrentSession().saveOrUpdate(order);
		return order;
	}

	@Override
	public void delete(Order order) {
		sessionFactory.getCurrentSession().delete(order);	
	}


	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	

}
