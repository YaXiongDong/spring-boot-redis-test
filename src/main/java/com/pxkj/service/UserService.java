package com.pxkj.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.pxkj.entity.User;

@Service
public class UserService {

	@Cacheable(cacheNames = "user", key = "#id.toString()")
	public Object getUser(Integer id) {
		System.out.println("从数据库查询user");
		User user = new User();
		user.setId(id);
		user.setName("xiaohei");
		return user;
	}

}
