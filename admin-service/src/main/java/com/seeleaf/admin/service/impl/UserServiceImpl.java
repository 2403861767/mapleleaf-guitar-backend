package com.seeleaf.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeleaf.admin.model.enums.UserRoleEnum;
import com.seeleaf.admin.model.request.user.UserQueryRequest;
import com.seeleaf.admin.model.vo.user.LoginUserVO;
import com.seeleaf.admin.model.vo.user.UserPageVo;
import com.seeleaf.admin.model.vo.user.UserVo;
import com.seeleaf.parent.common.ErrorCode;
import com.seeleaf.parent.constant.UserConstant;
import com.seeleaf.parent.exception.BusinessException;
import com.seeleaf.admin.service.UserService;
import com.seeleaf.admin.mapper.UserMapper;
import com.seeleaf.admin.model.entity.User;


import com.seeleaf.parent.utils.BeanCopyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.seeleaf.admin.model.enums.UserRoleEnum.SUPERADMIN;
import static com.seeleaf.parent.constant.UserConstant.SALT;
import static com.seeleaf.parent.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author 24038
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2023-04-26 19:53:08
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {



    // 注册
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号有非法字符!");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                log.info("账号重复");
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                log.info("发生未知错误");
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    // 登录
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号有非法字符!");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("用户登录失败，用户名不存在");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        // 4. 封装返回vo
        LoginUserVO loginUserVO = null;
        try {
            loginUserVO = new LoginUserVO();
            BeanUtils.copyProperties(user, loginUserVO);
        } catch (Exception e) {
            log.error("封装bean失败");
        }
        return loginUserVO;
    }

    // 获取当前用户
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
// 从数据库查询（追求性能的话可以注释，直接走缓存）
//        long userId = currentUser.getId();
//        currentUser = this.getById(userId);
//        if (currentUser == null) {
//            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
//        }
        return currentUser;
    }

    // 登出
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public boolean isSuperAdmin(HttpServletRequest request) {
        User loginUser = this.getLoginUser(request);
        String userRole = loginUser.getUserRole();
        if (StringUtils.isNotBlank(userRole) && (SUPERADMIN.getValue().equals(userRole))){
            return true;
        }
        return false;
    }

    // 分页查询
    @Override
    public UserPageVo pageQuery(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        if (userQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数错误");
        }
        if (!isSuperAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有权限");
        }

        String searchText = userQueryRequest.getSearchText();
        long pageSize = userQueryRequest.getPageSize();
        long current = userQueryRequest.getCurrent();
        LambdaQueryWrapper<User> qWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(searchText)){
            qWrapper.like(User::getUserAccount,searchText).or().like(User::getUserName,searchText)
                    .or().like(User::getAge,searchText)
                    .or().like(User::getGender,searchText);
        }
        Page<User> userPage = new Page<>(current, pageSize);
        Page<User> page = page(userPage, qWrapper);
        List<User> userList = page.getRecords();
        List<UserVo> userVoList = BeanCopyUtils.copyBeanList(userList, UserVo.class);
        UserPageVo userPageVo = new UserPageVo();
        userPageVo.setUserList(userVoList);
        userPageVo.setPageSize(pageSize);
        userPageVo.setTotal(page.getTotal());
        userPageVo.setCurrent(current);
        return userPageVo;
    }
}




