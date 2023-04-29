package com.seeleaf.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeleaf.admin.model.entity.User;
import com.seeleaf.admin.model.request.user.UserLoginRequest;
import com.seeleaf.admin.model.request.user.UserQueryRequest;
import com.seeleaf.admin.model.vo.user.LoginUserVO;
import com.seeleaf.admin.model.vo.user.UserPageVo;
import com.seeleaf.admin.model.vo.user.UserVo;
import com.seeleaf.parent.common.BaseResponse;
import com.seeleaf.parent.common.ErrorCode;
import com.seeleaf.parent.common.ResultUtils;
import com.seeleaf.parent.exception.BusinessException;
import com.seeleaf.admin.model.request.user.UserRegisterRequest;
import com.seeleaf.admin.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    // 注册
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

    @ApiOperation("用户登出接口")
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if (request== null){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @ApiOperation("获取当前用户接口")
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        User currentUser = userService.getLoginUser(request);
        return ResultUtils.success(currentUser);
    }

    // TODO 仅在测试阶段能够一次获取所有
    @ApiOperation("一次性获取所有用户list")
    @GetMapping("/list")
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
    @PostMapping("/list/page")
    public BaseResponse<UserPageVo> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest,
                                                     HttpServletRequest request) {
        return ResultUtils.success(userService.pageQuery(userQueryRequest,request));
    }
}
