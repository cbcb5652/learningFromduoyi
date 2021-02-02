package com.duoyi.business.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取ALS算法下的用户推荐矩阵
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserCFRequest {

    private int uid;

    private int sum;

}
