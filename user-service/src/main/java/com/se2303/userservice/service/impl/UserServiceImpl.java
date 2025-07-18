package com.se2303.userservice.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.se2303.userservice.entity.Auth;
import com.se2303.userservice.entity.User;
import com.se2303.userservice.entity.UserPreferences;
import com.se2303.userservice.repository.AuthRepository;
import com.se2303.userservice.repository.UserPreferencesRepository;
import com.se2303.userservice.repository.UserRepository;
import com.se2303.userservice.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public boolean updateProfile(Long userId, String username, String nickname, String avatar, String gender, LocalDate birthday, String location, String bio, String tags, String profileVisibility) {
        System.out.println("UserServiceImpl.updateProfile 被调用:");
        System.out.println("userId: " + userId);
        System.out.println("profileVisibility: " + profileVisibility);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("找到用户: " + user.getUsername());
            System.out.println("更新前 profileVisibility: " + user.getProfileVisibility());
            
            // 更新User表字段
            if (username != null) {
                user.setUsername(username);
            }
            if (nickname != null) {
                user.setNickname(nickname);
            }
            if (avatar != null) {
                user.setAvatar(avatar);
            }
            if (gender != null) {
                user.setGender(gender);
            }
            if (birthday != null) {
                user.setBirthday(birthday);
            }
            if (location != null) {
                user.setLocation(location);
            }
            if (bio != null) {
                user.setBio(bio);
            }
            if (tags != null) {
                user.setTags(tags);
            }
            if (profileVisibility != null) {
                System.out.println("设置 profileVisibility 为: " + profileVisibility);
                user.setProfileVisibility(profileVisibility);
            }
            
            System.out.println("更新后 profileVisibility: " + user.getProfileVisibility());
            // 保存User（会级联保存Auth的修改）
            userRepository.save(user);
            System.out.println("用户资料已保存");
            return true;
        }
        System.out.println("未找到用户 ID: " + userId);
        return false;
    }

    @Override
    @Transactional
    public User createUser(String sjtuId, String name, String jaccount, String telephone) {
        Boolean isUser = !(sjtuId.equals("900000000438")); // 在这里设置管理员
        
        Auth auth = new Auth(jaccount, sjtuId, name, telephone, isUser, true);
        
        // 保存Auth实体
        Auth savedAuth = authRepository.save(auth);
    
        // 创建User实体并设置关联
        User user = new User();
        user.setAuth(savedAuth);
        
        // 设置默认用户名：用户+id
        user.setUsername("用户" + savedAuth.getId());
        
        // 设置默认头像
        user.setAvatar("default_avatar.png");
        
        // 设置默认个人资料可见性
        user.setProfileVisibility("public");
        
        // 设置注册时间为当前日期
        user.setRegistrationDate(LocalDate.now());
        
        // 保存User实体
        User savedUser = userRepository.save(user);
        
        // 创建UserPreferences实体并设置关联
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setAuth(savedAuth);
        
        // 保存UserPreferences实体
        userPreferencesRepository.save(userPreferences);
        
        return savedUser;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getAllNormalUsers() {
        return userRepository.findByAuthIsUserTrue();
    }

    @Override
    @Transactional
    public boolean updateUserStatus(Long userId, boolean valid) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Auth auth = user.getAuth();
            auth.setValid(valid);
            authRepository.save(auth);
            return true;
        }
        return false;
    }

    @Override
    public int getNormalUserCount() {
        return userRepository.findByAuthIsUserTrue().size();
    }

}