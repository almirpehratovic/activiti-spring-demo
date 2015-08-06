package ba.pehli.activiti.service;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ba.pehli.activiti.domain.AppUser;

/**
 * Database operations for AppUser class
 * @author almir
 *
 */

@Service("userDao")
@Transactional
public class UserDaoImpl implements UserDao{
	private SessionFactory sessionFactory;
	
	@Autowired
	public UserDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	@Transactional(readOnly=true)
	public AppUser findByUsername(String username) {
		return (AppUser)sessionFactory.getCurrentSession().getNamedQuery("User.findByUsername").setParameter("username",username).uniqueResult();
	}
	
	/**
	 * Returns authenticated user in web app.
	 */
	@Override
	public AppUser getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// i anonymous je autentikovan pa moramo raditi dodatnu kontrolu 
		if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
			org.springframework.security.core.userdetails.User authUser = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
			AppUser user = findByUsername(authUser.getUsername());
			return user;
		}
		return null;
	}

	@Override
	public List<AppUser> findAll() {
		return sessionFactory.getCurrentSession().createQuery("select u from AppUser u").list();
	}
	
}
