package com.duoyi.business.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取当前最热电影
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetHotRecommendationRequest {

    private int sum;

}
