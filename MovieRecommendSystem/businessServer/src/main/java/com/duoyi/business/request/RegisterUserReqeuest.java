package com.duoyi.business.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新建用户的请求封装
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserReqeuest {

    private String username;

    private String password;


}
