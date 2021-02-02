package com.duoyi.business.model.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 电影类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movie {

    @JsonIgnore
    private int _id;

    private int mid;

    private String name;

    private String descri;

    private String timelong;

    private String issue;

    private String shoot;

    private String language;

    private String genres;

    private String actors;

    private String directors;


    private double score;

}
