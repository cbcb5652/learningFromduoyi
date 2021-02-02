package com.duoyi.business.model.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rating {

    @JsonIgnore
    private int _id;

    private int uid;

    private int mid;

    private double score;

    private long timestamp;

    public Rating(int uid, int mid, double score, long timestamp) {
        this.uid = uid;
        this.mid = mid;
        this.score = score;
        this.timestamp = timestamp;
    }
}
