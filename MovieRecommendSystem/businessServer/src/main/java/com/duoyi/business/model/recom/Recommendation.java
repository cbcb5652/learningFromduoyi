package com.duoyi.business.model.recom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 推荐的单个电影的包装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation {

    private int mid;

    private Double score;

}
