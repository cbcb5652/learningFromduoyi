package com.duoyi.business.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 混合推荐
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetHybridRecommendationRequest {


    // 离线推荐中的结果占比
    private double cfShare;

    private int mid;

    private int num;

}
