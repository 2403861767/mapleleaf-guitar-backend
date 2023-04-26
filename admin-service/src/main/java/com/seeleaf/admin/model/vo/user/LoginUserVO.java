package com.seeleaf.admin.model.vo.user;



import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class LoginUserVO implements Serializable {

    private static final long serialVersionUID = -1742749272478654898L;
    private Long id;

    /**
     * 账号
     */
    private String userAccount;


    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 性别
     */
    private String gender;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户状态 0表示正常,1表示会员，2表示封号
     */
    private Integer userStatus;

    /**
     * 用户角色：user/genealogist/sharer/admin/superAdmin
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
