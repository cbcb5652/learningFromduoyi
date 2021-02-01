package com.duoyi.business.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户类
 */
@Data
public class User {

    @JsonIgnore
    private int _id;

    private Integer uid;
    private String username;

    private String password;

    // 用于记录用户是否第一次登录
    private boolean first;

    // 用于保存电影的类别
    private List<String> genres = new ArrayList<>();

    public void setUsername(String username){
        this.uid = username.hashCode();
        this.username = username;
    }


}
