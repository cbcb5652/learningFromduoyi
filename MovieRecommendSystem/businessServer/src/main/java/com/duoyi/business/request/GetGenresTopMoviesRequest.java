package com.duoyi.business.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取电影类别的Top电影
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetGenresTopMoviesRequest {

    private String genres;

    private int num;
}
