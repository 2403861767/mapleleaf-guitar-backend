package com.seeleaf.admin.controller;

import com.seeleaf.admin.annotation.AuthCheck;
import com.seeleaf.admin.model.entity.User;
import com.seeleaf.admin.model.request.user.*;
import com.seeleaf.admin.model.vo.user.LoginUserVO;
import com.seeleaf.admin.model.vo.user.UserPageVo;
import com.seeleaf.admin.model.vo.user.UserVo;
import com.seeleaf.admin.service.UserService;
import com.seeleaf.parent.common.BaseResponse;
import com.seeleaf.parent.common.ErrorCode;
import com.seeleaf.parent.common.ResultUtils;
import com.seeleaf.parent.constant.UserConstant;
import com.seeleaf.parent.exception.BusinessException;
import com.seeleaf.parent.exception.ThrowUtils;
import com.seeleaf.parent.utils.BeanCopyUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

import static com.seeleaf.parent.constant.UserConstant.SALT;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    // 注册
    @ApiOperation("注册用户")
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    // 登录
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    @ApiOperation("用户登出")
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @ApiOperation("获取当前用户")
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        User currentUser = userService.getLoginUser(request);
        return ResultUtils.success(currentUser);
    }

    @ApiOperation("一次性获取所有用户list")
    @GetMapping("/list")
    @AuthCheck(mustRole = UserConstant.SUPER_ADMIN_ROLE)
    public BaseResponse<List<User>> getAllUsers() {
        return ResultUtils.success(userService.list());
    }

    /**
     * 分页获取用户封装列表
     *
     * @param userQueryRequest 分页查询参数
     * @param request 请求的信息
     * @return 分页包装类
     */
    @ApiOperation("分页查询")
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.SUPER_ADMIN_ROLE)
    public BaseResponse<UserPageVo> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest,
                                                     HttpServletRequest request) {
        return ResultUtils.success(userService.pageQuery(userQueryRequest, request));
    }

    /**
     * 创建用户（仅管理员）
     *
     * @param userAddRequest 添加用户参数
     * @return 添加的用户的id
     */
    @ApiOperation("添加")
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.SUPER_ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        // 2. 默认密码
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + "12345678").getBytes());
        user.setUserPassword(encryptPassword);
        // 默认用户名
        user.setUserName(userAddRequest.getUserAccount());
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户（仅管理员）
     *
     * @param deleteRequest 删除参数
     * @return 是否删除成功
     */
    @ApiOperation("删除用户")
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.SUPER_ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户（仅管理员和自己）
     *
     * @param userUpdateRequest 参数
     * @param request 当前的请求信息
     * @return 是否修改成功
     */
    @ApiOperation("修改用户")
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
                                            HttpServletRequest request) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // 如果用户不是管理员也不是自己直接抛出错误
        if (!Objects.equals(loginUser.getId(), userUpdateRequest.getId()) && !userService.isSuperAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     *
     * @param id 传入id
     * @return 用户包装类
     */
    @ApiOperation("根据id拿用户")
    @GetMapping("/get/{id}")
    @AuthCheck(mustRole = UserConstant.SUPER_ADMIN_ROLE)
    public BaseResponse<UserVo> getUserById(@PathVariable long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        UserVo userVo = BeanCopyUtils.copyBean(user, UserVo.class);
        return ResultUtils.success(userVo);
    }
}
