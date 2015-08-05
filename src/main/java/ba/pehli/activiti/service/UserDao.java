package ba.pehli.activiti.service;

import java.util.List;

import ba.pehli.activiti.domain.AppUser;

public interface UserDao {
	AppUser findByUsername(String username);
	AppUser getAuthenticatedUser();
	List<AppUser> findAll();
}
