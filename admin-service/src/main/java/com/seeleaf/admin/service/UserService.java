package com.seeleaf.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.seeleaf.admin.model.entity.User;
import com.seeleaf.admin.model.request.user.UserQueryRequest;
import com.seeleaf.admin.model.vo.user.LoginUserVO;
import com.seeleaf.admin.model.vo.user.UserPageVo;
import com.seeleaf.admin.model.vo.user.UserVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 24038
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-04-26 19:53:08
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前用户
     * @param request http请求
     * @return 当前用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户登出
     * @param request http请求
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 是否是超级管理员
     * @param request http请求
     * @return
     */
    boolean isSuperAdmin(HttpServletRequest request);

    UserPageVo pageQuery(UserQueryRequest userQueryRequest, HttpServletRequest request);
}
