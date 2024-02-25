package com.telerikacademy.web.carpooling.repositories;

import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.models.UserBlock;

public interface UserBlocksRepository {
    void create(UserBlock userBlock);

    void delete(UserBlock userBlock);

    void update(UserBlock userBlock);

    boolean isUserBlocked(int userId);
    UserBlock get(int userId);
}
