package com.seeleaf.admin.model.enums;

import cn.hutool.core.util.ObjectUtil;

//user/genealogist/sharer/admin/superAdmin
public enum UserRoleEnum {
    USER("普通用户", "user"),
    GENEALOGIST("制谱师", "genealogist"),
    SHARER("分享者", "sharer"),
    ADMIN("管理员", "admin"),
    SUPERADMIN("超级管理员", "superAdmin"),
    ;

    private final String text;

    private final String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    /**
     * 根据value 获取枚举值
     * @param value
     * @return
     */
    public static UserRoleEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (UserRoleEnum anEnum : UserRoleEnum.values()) {
            if (anEnum.value.equals(value)){
                return anEnum;
            }
        }
        return null;
    }
}
