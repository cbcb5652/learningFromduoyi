package com.duoyi.streaming

import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import kafka.Kafka
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import redis.clients.jedis.Jedis

object StreamingRecommender {

  // 入口方法
  def main(args: Array[String]): Unit = {

    val config = Map(
      "spark.cores" -> "local[*]",
      "mongo.uri" -> "mongodb://linux:27017/recommender",
      "mongo.db" -> "recommender",
      "kafka.topic" -> "recommender"
    )
    // 创建一个sparkConf配置
    val sparkConf = new SparkConf().setAppName("StreamingRecommender").setMaster(config("spark.cores"))

    // 创建spark的对象
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    val sc = spark.sparkContext
    val ssc = new StreamingContext(sc,Seconds(2))

    //创建kafka的连接
    val kafkaPara = Map(
      "bootstrap.servers" -> "linux:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "recommender",
      "auto.offset.reset" -> "latest"
    )

    val kafkaStream = KafkaUtils.createDirectStream[String,String](ssc,LocationStrategies.PreferConsistent,ConsumerStrategies.Subscribe[String,String](Array(config("kafka.topic")),kafkaPara))

    // UID|MID|SCORE|TIMESTAMP
    val ratingStream = kafkaStream.map{case msg =>
      val attr = msg.value().split("\\|")
      (attr(0).toInt,attr(1).toInt,attr(2).toDouble,attr(3).toInt)
    }

    ratingStream.foreachRDD{rdd =>
      rdd.map{case (uid,mid,score,timestamp) =>
        // 联通的时候这行数据会打印
        println(">>>>>>>>>>>>>>>")


      }.count()
    }

    //启动Streaming程序
    ssc.start()
    ssc.awaitTermination()

  }

}
