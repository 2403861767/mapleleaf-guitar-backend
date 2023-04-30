package com.seeleaf.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeleaf.admin.annotation.AuthCheck;
import com.seeleaf.admin.model.entity.User;
import com.seeleaf.admin.model.request.user.UserAddRequest;
import com.seeleaf.admin.model.request.user.UserLoginRequest;
import com.seeleaf.admin.model.request.user.UserQueryRequest;
import com.seeleaf.admin.model.vo.user.LoginUserVO;
import com.seeleaf.admin.model.vo.user.UserPageVo;
import com.seeleaf.admin.model.vo.user.UserVo;
import com.seeleaf.parent.common.BaseResponse;
import com.seeleaf.parent.common.ErrorCode;
import com.seeleaf.parent.common.ResultUtils;
import com.seeleaf.parent.constant.UserConstant;
import com.seeleaf.parent.exception.BusinessException;
import com.seeleaf.admin.model.request.user.UserRegisterRequest;
import com.seeleaf.admin.service.UserService;
import com.seeleaf.parent.exception.ThrowUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
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
    @ApiOperation("用户接口")
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
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if (request== null){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @ApiOperation("获取当前用户")
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
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
     * @param userQueryRequest
     * @param request
     * @return
     */
    @ApiOperation("分页查询")
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.SUPER_ADMIN_ROLE)
    public BaseResponse<UserPageVo> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest,
                                                     HttpServletRequest request) {
        return ResultUtils.success(userService.pageQuery(userQueryRequest,request));
    }

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @ApiOperation("添加")
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.SUPER_ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + "12345678").getBytes());
        user.setUserPassword(encryptPassword);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

}
