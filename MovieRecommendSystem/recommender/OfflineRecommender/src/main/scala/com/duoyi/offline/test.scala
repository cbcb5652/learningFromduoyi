package com.duoyi.offline

import com.duoyi.offline.OfflineRecommender.MONGODB_RATING_COLLECTION
import com.mongodb.spark.MongoSpark
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * ${DESCRIPTION}
 *
 * @author chenbin
 * @datetime 2021-01-28 0:42
 */
object test {

  def main(args: Array[String]): Unit = {
    //mongoDB配置参数
    val config = Map(
      "spark.cores" -> "local[*]",
      "mongo.uri" -> "mongodb://linux:27017/recommender",
      "mongo.db" -> "recommender"
    )
    val sparkConf = new SparkConf().setAppName("OfflineRecommender").setMaster(config("spark.cores"))
    val  spark = SparkSession.builder().config(sparkConf).getOrCreate();


    val ratingRDD = spark.read
      .option("uri","mongodb://linux:27017/recommender")
      .option("collection","Rating")
      .format("com.mongodb.spark.sql")
      .load()
//      .as[MovieRating]
//      .rdd
//      .map(rating=> (rating.uid,rating.mid,rating.score))


  }

}
