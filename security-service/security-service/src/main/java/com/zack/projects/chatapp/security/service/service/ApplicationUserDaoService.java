package com.zack.projects.chatapp.security.service.service;

import com.google.common.collect.Lists;
import com.zack.projects.chatapp.security.service.Repository.UserRepository;
import com.zack.projects.chatapp.security.service.dao.ApplicationUserDao;
import com.zack.projects.chatapp.security.service.dto.ApplicationUser;
import com.zack.projects.chatapp.security.service.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static com.zack.projects.chatapp.security.service.custom.UserRole.*;

@Repository("fake")
@Slf4j
public class ApplicationUserDaoService implements ApplicationUserDao {

	private final String NOTIFY_USERS_URL = "http://NOTIFICATION-SERVICE/notifications/notifyUsers/";
	private final String CHANGE_USER_STATUS_TO_ONLINE_URL = "http://USER-SERVICE/users/status/";

	@Autowired
	private final PasswordEncoder passwordEncoder;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CircuitBreakerFactory circuitBreakerFactory;

	public ApplicationUserDaoService(UserRepository userRepository,
									 PasswordEncoder passwordEncoder) {
		super();
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Optional<ApplicationUser> selectChatappApplicationUserByUserName(String username) {

		log.info(String.format("Find the user with username [%s]", username));
		Optional<ApplicationUser> applicationUser = getApplicationUsers().stream()
				.filter(appUser -> username.equals(appUser.getUsername()))
				.findFirst();

		log.info(String.format("Check if the user with username [%s] exists", username));
		if(applicationUser.isPresent()) {
			log.info("Retrieve the user object");
			User user = userRepository.findById(username).get();

			log.info("Create path variable to send with the put notification methods");
			Map< String, String > params = new HashMap< String, String >();
			params.put("username", username);

			CircuitBreaker circuitBreaker;

			log.info("Calling the notification service to notify all users");
			circuitBreaker = circuitBreakerFactory.create("changeStatus");
			log.info(String.format("Set the user [%s] status to online", username));
			circuitBreaker
					.run(changeUserStatusSupplier(username, "online"), t -> {
						log.warn("Notifying users failed", t);
						return false;
					});

			log.info("Calling the notification service to notify all users");
			circuitBreaker = circuitBreakerFactory.create("notifyUsers");
			log.info(String.format("Notify all users that user [%s] changed status to online", username));
			circuitBreaker
					.run(notifyUsersSupplier(username), t -> {
						log.warn("Notifying users failed", t);
						return false;
					});
		}
		
		return applicationUser;
	}

	private Supplier<Boolean> changeUserStatusSupplier(String username, String status) {
		return () -> changeUserStatus(username, status);
	}

	private boolean changeUserStatus(String username, String status) {
		Map< String, String > params = new HashMap< String, String >();
		params.put("username", username);

		HttpEntity<Map> request = new HttpEntity<>(params);
		ResponseEntity<Boolean> response =
				restTemplate.exchange(CHANGE_USER_STATUS_TO_ONLINE_URL + username
						+ "/" + status, HttpMethod.PUT, request, Boolean.class);

		return response.getBody();
	}

	private Supplier<Boolean> notifyUsersSupplier(String username) {
		return () -> notifyUsersBy(username);
	}

	private boolean notifyUsersBy(String username) {
		Map< String, String > params = new HashMap< String, String >();
		params.put("username", username);

		HttpEntity<Map> request = new HttpEntity<>(params);
		ResponseEntity<Boolean> response =
				restTemplate.exchange(NOTIFY_USERS_URL + username, HttpMethod.PUT, request, Boolean.class);

		return response.getBody();
	}

	private List<ApplicationUser> getApplicationUsers() {
		
		List<ApplicationUser> applicationUsers = Lists.newArrayList();

		log.info("Retrieve the list of existing users.");
		userRepository.findAll()
					.stream()
					.forEach(user
							-> {
									applicationUsers.add(new ApplicationUser(
											USER.getGrantedAuthorities(),
											user.getPassword(),
											user.getUsername(),
											user.isAccountNonExpired(),
											user.isAccountNonLocked(),
											user.isCredentialsNonExpired(),
											user.isEnabled()));
							});
		
		return applicationUsers;
							
	}

}
