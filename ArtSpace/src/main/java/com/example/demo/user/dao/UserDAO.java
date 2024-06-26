package com.example.demo.user.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.user.dto.UserDTO;

@Repository
public class UserDAO {

	@Autowired
	SqlSessionTemplate sqlSession;

	public void insert(UserDTO userDTO) {
		sqlSession.insert("user.insert", userDTO);
	}

	// 로그인 후 이름 뱉어내기
	public UserDTO login(UserDTO userDTO) {
		return sqlSession.selectOne("user.login", userDTO);
	}

	public int emailCheck(String email) {
		return sqlSession.selectOne("user.email_check", email);
	}

	public int phoneCheck(String phone) {
		return sqlSession.selectOne("user.phone_check", phone);
	}

	
}
