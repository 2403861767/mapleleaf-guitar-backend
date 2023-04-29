package com.seeleaf.parent.common;

import lombok.Data;

import java.util.List;

@Data
public class PageVo {
    long current = 1;
    long pageSize = 10;
    long total = 0;
}
