package com.service;

import java.util.List;

import com.pojo.User;

public interface UserService {
	/**
	 * 获取所有用户信息
	 * 
	 * @return
	 */
	public List<User> getUserList();

	/**
	 * 根据 id 查询用户信息
	 * 
	 * @param id
	 * @return
	 */
	public User selectUserById(int id);

	/**
	 * 更新用户信息
	 * 
	 * @param user
	 */
	public void updataUserByKey(User user);

	/**
	 * 插入用戶信息
	 * 
	 * @param user
	 */
	public void insertUser(User user);
}
