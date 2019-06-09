package com.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mapper.UserMapper;
import com.pojo.User;
import com.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserMapper userMapper;

	@Override
	public List<User> getUserList() {
		List<User> list = userMapper.selectByExample(null);
		if (list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				System.out.println(list.get(i).toString());
			}
			return list;
		}
		return null;
	}

	@Override
	public User selectUserById(int id) {
		return userMapper.selectByPrimaryKey(id);
	}

	@Override
	public void updataUserByKey(User user) {
		userMapper.updateByPrimaryKey(user);
	}

	@Override
	public void insertUser(User user) {
		userMapper.insert(user);
	}

}
