package com.duoyi.business.utils;

/**
 * 定义整个业务系统的常量
 */
public class Constant {

    /**
     * 定义mongodb 中的表名
     */

    // 数据库名字
    public static final String MONGO_DATABASE = "recommender";

    // 电影表名
    public static final String MONGO_MOVIE_COLLECTION ="Movie";

    //电影评分的表明
    public static final String MONGO_RATING_COLLECTION = "Rating";

    // 电影标签的表名
    public static final String MONGO_TAG_COLLECTION = "Tag";

    // 定义用户表
    public static final String MONGO_USER_COLLECTION = "User";

    // 电影的平均评分表
    public static final String MONGO_AVERAGE_MOVIES = "AverageMovies";

    // 电影类别的Top10表
    public static final String MONGO_GENRES_TOP_MOVIES = "GenresTopMovies";

    // 优质电影表
    public static final String MONGO_RATE_MORE_MOVIES = "RateMoreMovies";

    // 最热电影表
    public static final String MONGO_RATE_MORE_RECENTLY_MOVIES = "RateMoreRencentlyMovies";

    // 用户的推荐矩阵
    public static final String MONGO_USER_RECS_COLLECTION = "UserRecs";

    // 电影的相似度矩阵
    public static final String MONGO_MOVIE_RECS_COLLECTION = "MovieRecs";

    //实时推荐电影表
    public static final String MONGO_STREAM_RECS_COLLECTION = "StreamRecs";


    /**
     * ES
     */
    // 使用index
    public static final String ES_INDEX = "recommender";

    // 使用的Type
    public static final String ES_TYPE = "Movie";

    /**
     * Redis
     */
    public static final int USER_RATING_QUEUE_SIZE = 20;

    /**
     * LOG
     */
    public static final String USER_RATING_LOG_PREFIX = "USER_RATING_LOG_PREFIX";


}
