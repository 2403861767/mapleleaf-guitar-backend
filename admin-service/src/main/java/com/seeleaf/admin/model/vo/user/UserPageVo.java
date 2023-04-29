package com.seeleaf.admin.model.vo.user;

import com.seeleaf.parent.common.PageVo;
import lombok.Data;

import java.util.List;

@Data
public class UserPageVo extends PageVo {
    private List<UserVo> userList;
}
