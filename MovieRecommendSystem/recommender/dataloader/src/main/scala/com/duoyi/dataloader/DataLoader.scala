package com.duoyi.dataloader

import java.net.InetAddress

import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.transport.client.PreBuiltTransportClient

/**
  * Copyright (c) 2018-2028 尚硅谷 All Rights Reserved 
  *
  * Project: MovieRecommendSystem
  * Package: com.atguigu.recommender
  * Version: 1.0
  *
  * Created by wushengran on 2019/4/1 15:51
  */

/**
  * Movie 数据集
  *
  * 260                                         电影ID，mid
  * Star Wars: Episode IV - A New Hope (1977)   电影名称，name
  * Princess Leia is captured and held hostage  详情描述，descri
  * 121 minutes                                 时长，timelong
  * September 21, 2004                          发行时间，issue
  * 1977                                        拍摄时间，shoot
  * English                                     语言，language
  * Action|Adventure|Sci-Fi                     类型，genres
  * Mark Hamill|Harrison Ford|Carrie Fisher     演员表，actors
  * George Lucas                                导演，directors
  *
  * tag1|tag2|tag3|....                         电影的Tag
  */
case class Movie(mid: Int, name: String, descri: String, timelong: String, issue: String,
                 shoot: String, language: String, genres: String, actors: String, directors: String)

/**
  * Rating数据集
  *
  * 1,  		    用户的ID
  * 31,		    电影的ID
  * 2.5,		    用户对于电影的评分
  * 1260759144	    用户对于电影评分的时间
  */

case class Rating(uid: Int, mid: Int, score: Double, timestamp: Int )

/**
  * Tag数据集  用户对于电影的标签
  *
  * 15,		用户的ID
  * 1955,		电影的ID
  * dentist,		标签的具体内容
  * 1193435061	用户对于电影打标签的时间
  */
case class Tag(uid: Int, mid: Int, tag: String, timestamp: Int)

// 把mongo和es的配置封装成样例类

/**
  * MongoDB的连接配置
  * @param uri MongoDB连接
  * @param db  MongoDB数据库
  */
case class MongoConfig(uri:String, db:String)

/**MongoConfig
  * Elasticsearch的连接配置
  * @param httpHosts       http主机列表，逗号分隔
  * @param transportHosts  transport主机列表
  * @param index            需要操作的索引
  * @param clustername      集群名称，默认elasticsearch
  */
case class ESConfig(httpHosts:String, transportHosts:String, index:String, clustername:String)

//  数据的主加载服务
object DataLoader {

  // 定义常量
  val MOVIE_DATA_PATH = "D:\\Projects\\BigData\\MovieRecommendSystem\\recommender\\DataLoader\\src\\main\\resources\\movies.csv"
  val RATING_DATA_PATH = "D:\\Projects\\BigData\\MovieRecommendSystem\\recommender\\DataLoader\\src\\main\\resources\\ratings.csv"
  val TAG_DATA_PATH = "D:\\Projects\\BigData\\MovieRecommendSystem\\recommender\\DataLoader\\src\\main\\resources\\tags.csv"

  // 表
  val MONGODB_MOVIE_COLLECTION = "Movie"
  val MONGODB_RATING_COLLECTION = "Rating"
  val MONGODB_TAG_COLLECTION = "Tag"
  val ES_MOVIE_INDEX = "Movie"

// 数据的主加载服务
  def main(args: Array[String]): Unit = {

    val config = Map(
      "spark.cores" -> "local[*]",
      "mongo.uri" -> "mongodb://localhost:27017/recommender",
      "mongo.db" -> "recommender",
      "es.httpHosts" -> "localhost:9200",
      "es.transportHosts" -> "localhost:9300",
      "es.index" -> "recommender",
      "es.cluster.name" -> "elasticsearch"      //  -->  进入es的conf中看elasticsearch.yml 配置文件里面的cluster.name为啥对应的就是这个elasticsearch (es-cluster)
    )

    // 创建一个sparkConf配置
    val sparkConf = new SparkConf().setMaster(config("spark.cores")).setAppName("DataLoader")

    // 创建一个SparkSession
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()




    import spark.implicits._    // 导入这个是为了toDF方法
    //------------------------------------------
    // 加载数据  根据这个路径MOVIE_DATA_PATH  加载数据类型
    val movieRDD = spark.sparkContext.textFile(MOVIE_DATA_PATH)
    // 将MovieRDD转换为DataFrame
    val movieDF = movieRDD.map(
      item => {
        val attr = item.split("\\^")     // 以^ 为分隔符切分数据
        Movie(attr(0).toInt, attr(1).trim, attr(2).trim, attr(3).trim, attr(4).trim, attr(5).trim, attr(6).trim, attr(7).trim, attr(8).trim, attr(9).trim)
      }
    ).toDF()
    // --------------------------------------
    // 加载数据 根据这个路径RATING_DATA_PATH  加载数据类型
    val ratingRDD = spark.sparkContext.textFile(RATING_DATA_PATH)
    // 将RattingRDD转换为DataFrame
    val ratingDF = ratingRDD.map(item => {
      val attr = item.split(",")        // 以, 分割
      Rating(attr(0).toInt,attr(1).toInt,attr(2).toDouble,attr(3).toInt)
    }).toDF()
    // --------------------------------------
    // 加载数据 TAG_DATA_PATH  加载数据类型
    val tagRDD = spark.sparkContext.textFile(TAG_DATA_PATH)
    //将tagRDD装换为DataFrame
    val tagDF = tagRDD.map(item => {
      val attr = item.split(",")
      Tag(attr(0).toInt,attr(1).toInt,attr(2).trim,attr(3).toInt)
    }).toDF()

    implicit val mongoConfig = MongoConfig(config("mongo.uri"), config("mongo.db"))

    // 将数据保存到MongoDB
    storeDataInMongoDB(movieDF, ratingDF, tagDF)

    // 数据预处理，把movie对应的tag信息添加进去，加一列 tag1|tag2|tag3...
    import org.apache.spark.sql.functions._

    /**
      *  MID , Tags
      *   1     tag1|tag2|tag3....
     *    2     tag1|tag2...
      */
    val newTag = tagDF.groupBy($"mid")                                        // 按mid排序
      .agg( concat_ws( "|", collect_set($"tag") ).as("tags") )         //  以 | 为分割线聚合tag 新的名字为tags
      .select("mid", "tags")

    // newTag和movie做join，数据合并在一起，左外连接
    val movieWithTagsDF = movieDF.join(newTag, Seq("mid","mid"), "left")         // 连接 两个mid一样的数据,left 左连接--> 因为有些电影没有标签，不可能就不显示电影吧

    implicit val esConfig = ESConfig(config("es.httpHosts"), config("es.transportHosts"), config("es.index"), config("es.cluster.name"))

    // 保存数据到ES
    storeDataInES(movieWithTagsDF)
    // 关闭spark
    spark.stop()
  }

  // 将数据保存到MongoDB中的方法
  def storeDataInMongoDB(movieDF: DataFrame, ratingDF: DataFrame, tagDF: DataFrame)(implicit mongoConfig: MongoConfig): Unit ={
    // 新建一个mongodb的连接
    val mongoClient = MongoClient(MongoClientURI(mongoConfig.uri))

    // 如果mongodb中已经有相应的数据库，先删除
    mongoClient(mongoConfig.db)(MONGODB_MOVIE_COLLECTION).dropCollection()       // 获取表并删除
    mongoClient(mongoConfig.db)(MONGODB_RATING_COLLECTION).dropCollection()
    mongoClient(mongoConfig.db)(MONGODB_TAG_COLLECTION).dropCollection()

    // 将DF数据写入对应的mongodb表中
    movieDF.write
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_MOVIE_COLLECTION)
      .mode("overwrite")                                    // 指明 复写还是追加
      .format("com.mongodb.spark.sql")
      .save()

    ratingDF.write
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_RATING_COLLECTION)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    tagDF.write
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_TAG_COLLECTION)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    //对数据表建索引
    mongoClient(mongoConfig.db)(MONGODB_MOVIE_COLLECTION).createIndex(MongoDBObject("mid" -> 1))
    mongoClient(mongoConfig.db)(MONGODB_RATING_COLLECTION).createIndex(MongoDBObject("uid" -> 1))
    mongoClient(mongoConfig.db)(MONGODB_RATING_COLLECTION).createIndex(MongoDBObject("mid" -> 1))
    mongoClient(mongoConfig.db)(MONGODB_TAG_COLLECTION).createIndex(MongoDBObject("uid" -> 1))
    mongoClient(mongoConfig.db)(MONGODB_TAG_COLLECTION).createIndex(MongoDBObject("mid" -> 1))

    // 关闭mongoDBclient
    mongoClient.close()

  }

  // 将数据保存到ES中的方法
  def storeDataInES(movieDF: DataFrame)(implicit eSConfig: ESConfig): Unit ={
    // 新建es配置
    val settings: Settings = Settings.builder().put("cluster.name", eSConfig.clustername).build()

    // 新建一个es客户端
    val esClient = new PreBuiltTransportClient(settings)

    // 需要将TransportHosts 添加到esClient中
    val REGEX_HOST_PORT = "(.+):(\\d+)".r           //.r 转换为正则表达式
    eSConfig.transportHosts.split(",").foreach{
      case REGEX_HOST_PORT(host: String, port: String) => {
        esClient.addTransportAddress(new InetSocketTransportAddress( InetAddress.getByName(host), port.toInt ))
      }
    }

    // 先清理遗留的数据
    if( esClient.admin().indices().exists( new IndicesExistsRequest(eSConfig.index) )
        .actionGet()
        .isExists
    ){
      esClient.admin().indices().delete( new DeleteIndexRequest(eSConfig.index) )
    }

    esClient.admin().indices().create( new CreateIndexRequest(eSConfig.index) )

    movieDF.write
      .option("es.nodes", eSConfig.httpHosts)
      .option("es.http.timeout", "100m")
      .option("es.mapping.id", "mid")
      .mode("overwrite")
      .format("org.elasticsearch.spark.sql")
      .save(eSConfig.index + "/" + ES_MOVIE_INDEX)
  }

}