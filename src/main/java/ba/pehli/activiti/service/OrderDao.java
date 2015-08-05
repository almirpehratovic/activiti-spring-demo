package ba.pehli.activiti.service;

import java.util.List;

import ba.pehli.activiti.domain.Order;

public interface OrderDao {
	Order findOrderById(int id);
	List<Order> findAllOrders();
	Order save(Order order);
	void delete(Order order);
}
