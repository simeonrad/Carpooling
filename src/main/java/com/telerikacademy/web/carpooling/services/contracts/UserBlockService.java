package com.telerikacademy.web.carpooling.services.contracts;

import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.models.UserBlock;

import java.time.LocalDateTime;

public interface UserBlockService {
     void create(User user);
     void create(User user, LocalDateTime expireTimestamp);
     void delete(User user);
     boolean isUserBlocked(User user);
     boolean isUserBlocked(int userId);

}
