package com.zack.projects.chatapp.security.service.dao;

import com.zack.projects.chatapp.security.service.dto.ApplicationUser;

import java.util.Optional;

public interface ApplicationUserDao {

	Optional<ApplicationUser> selectChatappApplicationUserByUserName(String username);
	
}
