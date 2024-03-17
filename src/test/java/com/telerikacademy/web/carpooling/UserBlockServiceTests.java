package com.telerikacademy.web.carpooling;

import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.models.UserBlock;
import com.telerikacademy.web.carpooling.repositories.contracts.UserBlocksRepository;
import com.telerikacademy.web.carpooling.services.UserBlockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserBlockServiceTests {
    @Mock
    private UserBlocksRepository userBlocksRepository;

    @InjectMocks
    private UserBlockServiceImpl userBlockService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUserBlock() {
        User user = new User();
        userBlockService.create(user);
        verify(userBlocksRepository, times(1)).create(any(UserBlock.class));
    }

    @Test
    public void testCreateUserBlockWithExpireTimestamp() {
        User user = new User();
        LocalDateTime expireTimestamp = LocalDateTime.now().plusDays(1);
        userBlockService.create(user, expireTimestamp);
        verify(userBlocksRepository, times(1)).create(any(UserBlock.class));
    }

    @Test
    public void testDeleteUserBlock() {
        User user = new User();
        UserBlock userBlock = new UserBlock();
        when(userBlocksRepository.get(user.getId())).thenReturn(userBlock);

        userBlockService.delete(user);

        verify(userBlocksRepository, times(1)).delete(userBlock);
    }

    @Test
    public void testIsUserBlocked() {
        User user = new User();
        when(userBlocksRepository.isUserBlocked(user.getId())).thenReturn(true);

        boolean result = userBlockService.isUserBlocked(user);

        assertTrue(result);
        verify(userBlocksRepository, times(1)).isUserBlocked(user.getId());
    }

    @Test
    public void testIsUserBlockedById() {
        int userId = 123;
        when(userBlocksRepository.isUserBlocked(userId)).thenReturn(true);

        boolean result = userBlockService.isUserBlocked(userId);

        assertTrue(result);
        verify(userBlocksRepository, times(1)).isUserBlocked(userId);
    }
}