package com.se2303.userservice.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.se2303.userservice.entity.User;

public interface UserService {

    /**
     * 根据用户ID获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    Optional<User> getUserById(Long id);

    /**
     * 更新用户资料
     * @param userId 用户ID
     * @param username 用户名
     * @param nickname 昵称
     * @param avatar 头像
     * @param gender 性别
     * @param birthday 生日
     * @param location 位置
     * @param bio 个人简介
     * @param tags 标签
     * @param profileVisibility 资料可见性
     * @return 是否更新成功
     */
    boolean updateProfile(Long userId, String username, String nickname, String avatar, String gender, LocalDate birthday, String location, String bio, String tags, String profileVisibility);

    /**
     * 根据学工号、姓名、jaccount创建用户
     * @param sjtuId 学工号
     * @param name 姓名
     * @param jaccount jaccount
     * @param telephone 电话
     * @return 用户信息
     */
    User createUser(String sjtuId, String name, String jaccount, String telephone);

    /**
     * 管理员功能：获取所有用户列表
     * @return 用户列表
     */
    List<User> getAllUsers();

    /**
     * 管理员功能：获取所有普通用户列表（isUser=true）
     * @return 普通用户列表
     */
    List<User> getAllNormalUsers();

    /**
     * 管理员功能：启用或禁用用户
     * @param userId 用户ID
     * @param valid 是否有效
     * @return 是否操作成功
     */
    boolean updateUserStatus(Long userId, boolean valid);

    /**
     * 获取普通用户数量
     * @return 普通用户数量
     */
    int getNormalUserCount();
}