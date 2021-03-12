package com.zack.peojects.chatapp.userservice.repository;

import com.zack.peojects.chatapp.userservice.template.UserResponseTemplate;
import com.zack.peojects.chatapp.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    UserResponseTemplate findUserByUsername(String username);

    List<UserResponseTemplate> findByUsernameStartingWith(String username);

    @Modifying
    @Transactional
    @Query("update User u set u.isOnline = :onlineStatus where u.username = :username")
    int updateUserOnlineStatus(String username, boolean onlineStatus);

    @Modifying
    @Transactional
    @Query("update User u set u.availability = :availability where u.username = :username")
    int updateUserAvailability(String username, String availability);

}