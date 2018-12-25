package com.pxkj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.pxkj.entity.User;
import com.pxkj.service.UserService;

@RestController
public class TestController {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private UserService userService;

	@GetMapping("/test")
	public String test(User user) {
		redisTemplate.opsForValue().set("user", user);
		return "success";
	}

	@GetMapping("/getUser")
	public Object getUser() {
		String str = JSON.toJSONString(redisTemplate.opsForValue().get("user"));
		return JSON.parseObject(str, User.class);
	}

	@GetMapping("/get")
	public Object get() {
		User user = userService.getUser(2);
		return user;
	}

}
