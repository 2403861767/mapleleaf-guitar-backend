package com.seeleaf.admin.model.request.user;

import com.seeleaf.parent.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 5157077648341748415L;
    private String searchText;
}
