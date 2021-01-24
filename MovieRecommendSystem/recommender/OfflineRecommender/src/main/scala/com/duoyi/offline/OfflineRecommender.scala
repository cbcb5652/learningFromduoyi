package com.duoyi.offline

import org.apache.spark.SparkConf
import org.apache.spark.mllib.recommendation.{ALS, Rating}
import org.apache.spark.sql.SparkSession

case class Movie(mid: Int, name: String, descri: String, timelong: String, issue: String,
                 shoot: String, language: String, genres: String, actors: String, directors: String)

case class MovieRating(uid: Int, mid: Int, score: Double, timestamp: Int )

case class MongoConfig(uri:String, db:String)

// 定义一个基准推荐对象
case class Recommendation( mid: Int, score: Double )

// 定义电影类别top10推荐对象
case class GenresRecommendation( genres: String, recs: Seq[Recommendation] )


// 用户的推荐
case class UserRecs(uid:Int,recs:Seq[Recommendation])

// 电影的相似度
case class MovieRecs(uid:Int,recs:Seq[Recommendation])

object OfflineRecommender {

  val MONGODB_RATING_COLLECTION = "Rating";
  val MONGODB_MOVIE_COLLECTION = "Movie";

  val USER_MAX_RECOMMENDATION = 20;

  val USER_RECS = "UserRecs";

  // 入口方法
  def main(args: Array[String]): Unit = {

    //mongoDB配置参数
    val config = Map(
      "spark.cores" -> "local[*]",
      "mongo.uri" -> "mongodb://linux:27017/recommender",
      "mongo.db" -> "recommender"
    )

    // 创建一个SparkConf配置
    val sparkConf = new SparkConf().setAppName("OfflineRecommender").setMaster(config("spark.cores"))
      .set("spark.executor.memory","6G").set("spark.driver.memory","3G");

    // 基于SparkConf创建一个SparkSession
    val  spark = SparkSession.builder().config(sparkConf).getOrCreate();

    // 创建一个MongoDBConfig
    val mongoConfig = MongoConfig(config("mongo.uri"),config("mongo.db"))

    import spark.implicits._

    // 读取mongoDB中的业务数据  ratingRDD是一个三元组类型 int int double
    val ratingRDD = spark
      .read
      .option("uri",mongoConfig.uri)
      .option("colletion",MONGODB_RATING_COLLECTION)
      .format("com.mongodb.spark.sql")
      .load()
      .as[MovieRating]
      .rdd
      .map(rating=> (rating.uid,rating.mid,rating.score))

    // 用户的数据集 RDD[Int]    去重
    val userRDD = ratingRDD.map(_._1).distinct()

    // 电影的数据集  RDD[Int]
    val movieRDD = spark
      .read
      .option("uri",mongoConfig.uri)
      .option("collection",MONGODB_RATING_COLLECTION)
      .format("com.mongodb.spark.sql")
      .load()
      .as[Movie]
      .rdd
      .map(_.mid)

    // 训练ALS模型
    val trainData = ratingRDD.map(x => Rating(x._1,x._2,x._3));

    // 50个数据迭代10次
    val (rank,iterations,lambda) = (50,10,0.01)

    // 传入参数:训练模型,迭代的数据,迭代次数,迭代频率
    val model = ALS.train(trainData,rank, iterations,lambda)

    // 计算用户推荐矩阵

    // 需要构造一个usersProducts RDD[(Int,Int)]
    val userMovies = userRDD.cartesian(movieRDD);

    val preRatings = model.predict(userMovies)

    // 排序厚，获取前20个
    val userRecs = preRatings.map(rating => (rating.user,(rating.product,rating.rating)))
      .groupByKey()
      .map{
        case (uid,recs) => UserRecs(uid,recs.toList.sortWith(_._2 > _._2).take(USER_MAX_RECOMMENDATION).map(x => Recommendation(x._1,x._2)))
      }.toDF()


    userRecs.write
      .option("uri",mongoConfig.uri)
      .option("collection",USER_RECS)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    // 计算电影相似度


    // 关闭spark
    spark.close()

  }

}
