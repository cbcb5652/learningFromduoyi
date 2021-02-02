package com.duoyi.business.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实时推荐请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetStreamRecsRequest {

    private int uid;

    private int num;

}
