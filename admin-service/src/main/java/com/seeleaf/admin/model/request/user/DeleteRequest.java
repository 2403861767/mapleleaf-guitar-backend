package com.seeleaf.admin.model.request.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeleteRequest implements Serializable {
    private static final long serialVersionUID = -7724449672937016334L;
    /**
     * id
     */
    private Long id;
}
