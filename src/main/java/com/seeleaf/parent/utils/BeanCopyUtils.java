package com.seeleaf.parent.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class BeanCopyUtils {
    private BeanCopyUtils(){}

    /** 单个对象拷贝
     *
     * @param source 传入被拷贝的对象
     * @param clazz 传入vo
     * @param <V> 传入的vo类型
     * @return
     */
    public static <V> V copyBean(Object source, Class<V> clazz){
        // 创建目标对象
        V result = null;
        try {
           result = clazz.newInstance();
            // 实现属性copy
            BeanUtils.copyProperties(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 返回结果
        return result;
    }

    public static <O, V> List<V> copyBeanList(List<O> list, Class<V> clazz) {
        /** 创建目标对象 实现属性拷贝*/
        return list.stream()
                .map(o -> copyBean(o, clazz))
                .collect(Collectors.toList());
    }

}
