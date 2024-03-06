package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.models.UserBlock;
import com.telerikacademy.web.carpooling.repositories.UserBlocksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserBlockServiceImpl implements UserBlockService {
    private final UserBlocksRepository userBlocksRepository;
    @Autowired
    public UserBlockServiceImpl(UserBlocksRepository userBlocksRepository) {
        this.userBlocksRepository = userBlocksRepository;
    }


    @Override
    public void create(User user) {
        UserBlock userBlock = new UserBlock();
        userBlock.setBlockExpireTimestamp(LocalDateTime.now().plusYears(100));
        userBlock.setUser(user);
        userBlocksRepository.create(userBlock);
    }

    @Override
    public void create(User user, LocalDateTime expireTimestamp) {
        UserBlock userBlock = new UserBlock();
        userBlock.setUser(user);
        userBlock.setBlockExpireTimestamp(expireTimestamp);
        userBlocksRepository.create(userBlock);
    }

    @Override
    public void delete(User user) {
        userBlocksRepository.delete(userBlocksRepository.get(user.getId()));
    }

    @Override
    public boolean isUserBlocked(User user) {
        return userBlocksRepository.isUserBlocked(user.getId());
    }
    public boolean isUserBlocked(int userId) {
        return userBlocksRepository.isUserBlocked(userId);
    }

}
