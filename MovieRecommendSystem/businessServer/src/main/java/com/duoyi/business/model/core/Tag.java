package com.duoyi.business.model.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tag {

    @JsonIgnore
    private int _id;

    private int uid;

    private int mid;

    private String tag;

    private long timestamp;

    public Tag(int uid, int mid, String tag, long timestamp) {
        this.uid = uid;
        this.mid = mid;
        this.tag = tag;
        this.timestamp = timestamp;
    }
}
