package com.pxkj.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.pxkj.entity.User;
import com.pxkj.service.UserService;

/**
 * spring boot redis test
 * 
 * @author Administrator
 *
 */
@RestController
public class TestController {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private UserService userService;

	/**
	 * spring RedisTemplate set test
	 * 
	 * @param user
	 * @return
	 */
	@GetMapping("/test")
	public String test(User user) {
		redisTemplate.opsForValue().set("user", user);
		return "success";
	}

	/**
	 * spring RedisTemplate get test
	 * 
	 * @param user
	 * @return
	 */
	@GetMapping("/getUser")
	public Object getUser() {
		String str = JSON.toJSONString(redisTemplate.opsForValue().get("user"));
		return JSON.parseObject(str, User.class);
	}

	/**
	 * spring cache Redis test
	 * 
	 * @return
	 */
	@GetMapping("/get")
	public Object get() {
		Object user = userService.getUser(2);
		return user;
	}

	/**
	 * spring session test
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/login")
	public String login(HttpServletRequest request) {
		String name = request.getParameter("name");
		HttpSession session = request.getSession();
		User user = new User();
		user.setId(55);
		user.setName(name);
		session.setAttribute("user", user);
		return "hello:" + name;
	}

}
