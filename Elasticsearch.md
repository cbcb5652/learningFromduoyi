# Elasticsearch

## Elasticsearch聚合分析

> Es的聚合分析，让es注入了统计分析的血脉，使用户在面对大数据提取统计指标时变得游刃有余。在mongodb中你必须用大段的mapreduce脚本，而在es中仅仅调用一个API就能够实现了。

### **关于Aggregations聚合**

> Aggregations部分特性类似于SQL语言中的group by,avg,sum 等函数。但Aggregations API还提供了更加复杂的统计分析接口，需要理解以下两个概念
>
> - 桶(Buckets): 符合条件的文档的集合，相当于SQL的group by。比如按地区聚合，一个人将被分到北京桶/上海桶。按性别聚合，一个人将被分到男桶/女桶
> - 指标(Metrics): 基于Buckets的基础上进行统计分析，相当于SQL中的count,avg,sum等，比如，按地区聚合，计算每个地区的人数，平均数，年龄等

对照一条SQL来加深我们的理解:

```sql
SELECT COUNT(color) FROM table GROUP BY color
```

GROUP BY相当于做了分桶的工作，COUNT是统计指标

### **常用的Aggregations API。**

Metrics

- AVG
- Cardinality
- Stats
- Extended Stats
- Percentiles
- Percentile Ranks

Bucket

- Filter
- Range
- Missing
- Terms
- Date Range
- Global Aggregation
- Histogram
-  Date Histogram
-  IPv4 range
- Return only aggregation results

### 聚合缓存

> ES经常用到的聚合结果可以被缓存起来，以便快速的系统响应。第一次聚合的条件与结果缓存起来后，ES会判断你后续使用的聚合条件，如果聚合条件不变，并且检索的数据块未更新，ES自动返回缓存的结果。
>
> ==聚合结果只针对size=0的请求   还有使用动态参数的ES同样不会缓存结果，因为聚合条件是动态的，即使缓存了结果也没用了==

###  聚合测试

```json
# 添加聚合
PUT /testindex/orders/1?pretty
{
	 "zone_id": "1",
    "user_id": "100008",
    "try_deliver_times": 102,
    "trade_status": "TRADE_FINISHED",
    "trade_no": "xiaomi.21142736250938334726",
    "trade_currency": "CNY",
    "total_fee": 100,
    "status": "paid",
    "sdk_user_id": "69272363",
    "sdk": "xiaomi",
    "price": 1,
    "platform": "android",
    "paid_channel": "unknown",
    "paid_at": 1427370289,
    "market": "unknown",
    "location": "local",
    "last_try_deliver_at": 1427856948,
    "is_guest": 0,
    "id": "fa6044d2fddb15681ea2637335f3ae6b7f8e76fef53bd805108a032cb3eb54cd",
    "goods_name": "一小堆元宝",
    "goods_id": "ID_001",
    "goods_count": "1",
    "expires_in": 25,
    "delivered_at": 0,
    "debug_mode": true,
    "created_at": 1427362509,
    "cp_result": "exception encountered",
    "cp_order_id": "cp.order.id.test",
    "client_id": "9c98152c6b42c7cb3f41b53f18a0d868",
    "app_user_id": "fvu100006"，
	"gender" : "男"
}

PUT /testindex/orders/2?pretty
{
	 "zone_id": "2",
    "user_id": "100008",
    "try_deliver_times": 102,
    "trade_status": "TRADE_FINISHED",
    "trade_no": "xiaomi.21142736250938334726",
    "trade_currency": "CNY",
    "total_fee": 100,
    "status": "paid",
    "sdk_user_id": "69272363",
    "sdk": "xiaomi",
    "price": 1,
    "platform": "android",
    "paid_channel": "unknown",
    "paid_at": 1427370289,
    "market": "unknown",
    "location": "local",
    "last_try_deliver_at": 1427856948,
    "is_guest": 0,
    "id": "fa6044d2fddb15681ea2637335f3ae6b7f8e76fef53bd805108a032cb3eb54cd",
    "goods_name": "一小堆元宝",
    "goods_id": "ID_001",
    "goods_count": "1",
    "expires_in": 30,
    "delivered_at": 0,
    "debug_mode": true,
    "created_at": 1427362509,
    "cp_result": "exception encountered",
    "cp_order_id": "cp.order.id.test",
    "client_id": "9c98152c6b42c7cb3f41b53f18a0d868",
    "app_user_id": "fvu100006",
	"gender" : "女"
}
```

**单值聚合**

sum求和

```json
GET /testindex/orders/_search?pretty
{
	"size": 0,
   "aggs": {
     "return_expires_in": {
       "sum": {
         "field": "expires_in"
       }
     }
   }
}
# 返回expires_in之和，其中size=0 表示不需要返回参与查询的文档。  value 为总数
{
  "took" : 3,
  "timed_out" : false,
  "_shards" : {
    "total" : 5,
    "successful" : 5,
    "failed" : 0
  },
  "hits" : {
    "total" : 2,
    "max_score" : 0.0,
    "hits" : [ ]
  },
  "aggregations" : {
    "return_expires_in" : {
      "value" : 55.0
    }
  }
}
```

Min求最小值

```json
GET /testindex/orders/_search?pretty
{
	"size": 0,
   "aggs": {
     "return_expires_in": {
       "min": {
         "field": "expires_in"
       }
     }
   }
}
# 返回expires_in之和，其中size=0 表示不需要返回参与查询的文档。  value 为总数
{
  "took" : 2,
  "timed_out" : false,
  "_shards" : {
    "total" : 5,
    "successful" : 5,
    "failed" : 0
  },
  "hits" : {
    "total" : 2,
    "max_score" : 0.0,
    "hits" : [ ]
  },
  "aggregations" : {
    "return_expires_in" : {
      "value" : 25.0
    }
  }
}
```

Max求最大值

```json
GET /testindex/orders/_search?pretty
{
	"size": 0,
   "aggs": {
     "return_expires_in": {
       "min": {
         "field": "expires_in"
       }
     }
   }
}
# 返回expires_in最大值，其中size=0 表示不需要返回参与查询的文档。  value 为总数
{
  "took" : 2,
  "timed_out" : false,
  "_shards" : {
    "total" : 5,
    "successful" : 5,
    "failed" : 0
  },
  "hits" : {
    "total" : 2,
    "max_score" : 0.0,
    "hits" : [ ]
  },
  "aggregations" : {
    "return_expires_in" : {
      "value" : 30.0
    }
  }
}
```

求平均值

```json
GET /testindex/orders/_search?pretty
{
	"size": 0,
   "aggs": {
     "return_expires_in": {
       "min": {
         "field": "expires_in"
       }
     }
   }
}
# 返回expires_in最小值，其中size=0 表示不需要返回参与查询的文档。  value 为总数
{
  "took" : 2,
  "timed_out" : false,
  "_shards" : {
    "total" : 5,
    "successful" : 5,
    "failed" : 0
  },
  "hits" : {
    "total" : 2,
    "max_score" : 0.0,
    "hits" : [ ]
  },
  "aggregations" : {
    "return_expires_in" : {
      "value" : 27.5
    }
  }
}
```

Cardinality 求基数(如下所示，查找性别的基数M,F 共两个)

```json
GET /testindex/orders/_search?pretty
{
  "size": 0,
  "aggs": {
    "return_cardinality": {
      "cardinality": {
        "field": "gender"
      }
    }
  }
}
# 求字段的基数  存在男女 两种
{
  "took" : 2,
  "timed_out" : false,
  "_shards" : {
    "total" : 5,
    "successful" : 5,
    "failed" : 0
  },
  "hits" : {
    "total" : 2,
    "max_score" : 0.0,
    "hits" : [ ]
  },
  "aggregations" : {
    "return_cardinality" : {
      "value" : 2
    }
  }
}
```

**多值聚合**

按照性别(F,M) 查看工资范围内的百分比

```json
GET /testindex/orders/_search?pretty
{
  "size": 0,
  "aggs": {
    "states": {
      "terms": {
        "field": "gender"
      },
      "aggs": {
        "banlances": {
          "percentile_ranks": {
            "field": "balance",
            "values": [
              20000,
              40000
            ]
          }
        }
      }
    }
  }
# 统计两个性别分别在这工资区间内的人数
{
  "took" : 2,
  "timed_out" : false,
  "_shards" : {
    "total" : 5,
    "successful" : 5,
    "failed" : 0
  },
  "hits" : {
    "total" : 2,
    "max_score" : 0.0,
    "hits" : [ ]
  },
  "aggregations" : {
     "states":{
         "doc_count_error_upper_bound":0,
         "sum_other_doc_count":0,
         "buckets":[
             {
                 "key":"m",
                 "doc_count":507,
                 "banlances":{
                     "values":{
                         "2000.0":50.000000,
                         "4000.0":50.000000
                     }
                 }
             },
             {
                 "key":"f",
                 "doc_count":409,
                 "banlances":{
                     "values":{
                         "2000.0":50.000000,
                         "4000.0":50.000000
                     }
                 }
             }
         ]
     }
  }
}
```

stats 统计 查看balance的统计情况（一次性返回所有统计的结果）

```json
GET /testindex/orders/_search?pretty
{
  "size": 0,
  "aggs": {
    "states": {
      "terms": {
        "field": "gender"
      },
      "aggs": {
        "banlances": {
           "stats":{
               "field":"balance"
           }
        }
      }
    }
  }
# 统计balance字段的 总数 最小值  最大值  平均值 总值
{
  "took" : 2,
  "timed_out" : false,
  "_shards" : {
    "total" : 5,
    "successful" : 5,
    "failed" : 0
  },
  "hits" : {
    "total" : 2,
    "max_score" : 0.0,
    "hits" : [ ]
  },
  "aggregations" : {
     "balance_stats":{
         "count":1000,
         "min":500,
         "max":50000,
         "avg": 5000,
         "sum":  1000000
     }
  }
}
```

extended_stats 扩展统计

```json
GET /testindex/orders/_search?pretty
{
  "size": 0,
  "aggs": {
    "states": {
      "terms": {
        "field": "gender"
      },
      "aggs": {
        "banlances": {
           "extended_stats":{
               "field":"balance"
           }
        }
      }
    }
  }
# 统计balance字段的 总数 最小值  最大值  平均值 总值   扩展.....
{
  "took" : 2,
  "timed_out" : false,
  "_shards" : {
    "total" : 5,
    "successful" : 5,
    "failed" : 0
  },
  "hits" : {
    "total" : 2,
    "max_score" : 0.0,
    "hits" : [ ]
  },
  "aggregations" : {
     "balance_stats":{
         "count":1000,
         "min":500,
         "max":50000,
         "avg": 5000,
         "sum":  1000000,
         "sum_of_squares":10101.111,
         "variance": 110000.22,
         "std_deviation_bounds":{
             "supper":53812.80602257749,
             "lower": -2383.13200
         }
     }
  }
}
```

**Terms聚合**

```json
GET /testindex/orders/_search?pretty
{
  "size": 0,
  "aggs": {
    "genders": {
      "terms": {
        "field": "gender"
      }
    }
  }
}

# 返回结果如下：m记录507条  f记录493条
"aggregations":{
    "genders":{
        "doc_count_error_upper_bound":0,
        "sum_other_doc_count":0,
        "buckets":[
            {
                "key":"m",
                "doc_count":507
            },
            {
                "key":"f",
                "doc_count":493
            }
        ]
    }
}
```

**数据的不确定性:**

使用terms聚合，结果可能带有一定的偏差与错误性。

比如：

我们想要获取name字段中出现频率最高的前5个。

此时，客户端向ES发送聚合请求，主节点接收到请求后，会向每个独立的分片发送该请求。
分片独立的计算自己分片上的前5个name，然后返回。当所有的分片结果都返回后，在主节点进行结果的合并，再求出频率最高的前5个，返回给客户端。

这样就会造成一定的误差，比如最后返回的前5个中，有一个叫A的，有50个文档；B有49。 但是由于每个分片独立的保存信息，信息的分布也是不确定的。 有可能第一个分片中B的信息有2个，但是没有排到前5，所以没有在最后合并的结果中出现。 这就导致B的总数少计算了2，本来可能排到第一位，却排到了A的后面。

**size与shard_size**

为了改善上面的问题，就可以使用size和shard_size参数。

- size参数规定了最后返回的term个数(默认是10个)
- shard_size参数规定了每个分片上返回的个数
- 如果shard_size小于size，那么分片也会按照size指定的个数计算

通过这两个参数，如果我们想要返回前五个，size=5,shard_size可以设置大于5，这个每个分片返回的词条信息就会增多，相应的几率也会减小。

-----

order排序

```json
# order指定了最后返回结果的排序方式，默认是按照doc_count排序
GET /testindex/orders/_search?pretty
{
    "aggs" : {
        "genders" : {
            "terms" : {
                "field" : "gender",
                "order" : { "_count" : "asc" }
            }
        }
    }
}

# 也可以按照字典的方式排序
GET /testindex/orders/_search?pretty
{
    "aggs" : {
        "genders" : {
            "terms" : {
                "field" : "gender",
                "order" : { "_term" : "asc" }
            }
        }
    }
}

# 也可以通过order指定一个单值聚合，来排序
GET /testindex/orders/_search?pretty
{
    "aggs" : {
        "genders" : {
            "terms" : {
                "field" : "gender",
                "order" : { "avg_balance" : "desc" }
            },
            "aggs" : {
                "avg_balance" : { "avg" : { "field" : "balance" } }
            }
        }
    }
}

# 也可以支持多值聚合，不过要指定使用的多值字段
GET /testindex/orders/_search?pretty
{
    "aggs" : {
        "genders" : {
            "terms" : {
                "field" : "gender",
                "order" : { "balance_stats.avg" : "desc" }
            },
            "aggs" : {
                "balance_stats" : { "stats" : { "field" : "balance" } }
            }
        }
    }
}
```

**min_doc_count和shard_min_doc_count**

聚合的字段可能存在一些频率很低的词条，如果这些词条数目比例很大，那么就会造成很多不必要的计算。

因此可以通过设置min_doc_count和shard_min_doc_count来规定最小的文档数目，只有满足了这个参数的个数的词条才会被记录返回

- min_doc_count: 规定了最终结果的筛选
- shard_min_doc_count: 规定了分片中计算返回的筛选

**script**

```json
# 桶聚合也支持脚本的使用
GET /testindex/orders/_search?pretty
{
    "aggs" : {
        "genders" : {
            "terms" : {
                "script" : "doc['gender'].value"
            }
        }
    }
}

# 以及外部脚本的使用
{
    "aggs" : {
        "genders" : {
            "terms" : {
                "script" : {
                    "file": "my_script",
                    "params": {
                        "field": "gender"
                    }
                }
            }
        }
    }
}
```

**filter**

> filer字段提供了过滤的功能，使用两种方式：include可以匹配出包含该值的文档，exclude则排除包含该值的文档。

```json
# 以下字段筛选tags  包括sport 但不包括water
{
    "aggs" : {
        "tags" : {
            "terms" : {
                "field" : "tags",
                "include" : ".*sport.*",
                "exclude" : "water_.*"
            }
        }
    }
}

# 也支持数组的方式，定义包含与排序的信息
{
    "aggs" : {
        "JapaneseCars" : {
             "terms" : {
                 "field" : "make",
                 "include" : ["mazda", "honda"]
             }
         },
        "ActiveCarManufacturers" : {
             "terms" : {
                 "field" : "make",
                 "exclude" : ["rover", "jensen"]
             }
         }
    }
}
```

**多字段聚合**

通常情况，terms聚合都是仅针对于一个字段的聚合。因为该聚合是需要把词条放入一个哈希表中，如果多个字段就会造成n^2的内存消耗。

不过对于多字段，es也提供了下面两种方式 ：

- 使用脚本合并字段
- 使用copy_to 方法，合并两个字段，创建出一个新的字段，对新字段执行单个字段的绝活

**collect模式**

> 对于子聚合的计算，有两种方式

- depth_first 直接进行子聚合的计算
- breadth_first   先计算出当前聚合的结果，针对这个结果在对子聚合进行计算

默认情况下Es会使用深度优先，不过可以手动设置成广度优先 比如:

```json
{
    "aggs" : {
        "actors" : {
             "terms" : {
                 "field" : "actors",
                 "size" : 10,
                 "collect_mode" : "breadth_first"
             },
            "aggs" : {
                "costars" : {
                     "terms" : {
                         "field" : "actors",
                         "size" : 5
                     }
                 }
            }
         }
    }
}
```

**缺省值Missing value**

> 缺省值指定了缺省的字段的处理方式

```json
{
    "aggs" : {
        "tags" : {
             "terms" : {
                 "field" : "tags",
                 "missing": "N/A" 
             }
         }
    }
}
```



## 聚合查询的优化

> Elasticsearch 里面的桶的叫法类似于SQL里面的分组，但是这里仅仅是概念上的，底层实现原理是不一样的。

terms桶基于我们的数据动态构建桶；它并不知道到底生成了多少桶。大多数的时候对单个字段的聚合查询还是非常快的，但是要是同时聚合多个字段时，就可能会产生大量的分组，最终结果就是占用es大量内存，从而导致OOM的情况发生。

==例子:==

假设我们现在有一些关于电影的数据集，每条数据里面会有一个数组类型的字段存储表演该电影的所有演员的名字。

```json
{
  "actors" : [
    "Fred Jones",
    "Mary Jane",
    "Elizabeth Worthing"
  ]
}
```

如果我们想要查询出演影片最多的十个演员以及与他们合作最多的演员，使用聚合是非常简单的：

```js
{
  "aggs" : {
    "actors" : {
      "terms" : {
         "field" : "actors",
         "size" :  10
      },
      "aggs" : {
        "costars" : {
          "terms" : {
            "field" : "actors",
            "size" :  5
          }
        }
      }
    }
  }
}
```

这会返回前十位出演最多的演员，以及与他们合作最多的五位演员。这看起来是一个简单的聚合查询，最终只返回 50 条数据！

但是， 这个看上去简单的查询可以轻而易举地消耗大量内存，我们可以通过在内存中构建一个树来查看这个 `terms` 聚合。 `actors` 聚合会构建树的第一层，每个演员都有一个桶。然后，内套在第一层的每个节点之下， `costar` 聚合会构建第二层，每个联合出演一个桶。这意味着每部影片会生成 n2 个桶！

![Build full depth tree](https://www.elastic.co/guide/cn/elasticsearch/guide/current/images/300_120_depth_first_1.svg)



用真实点的数据，设想平均每部影片有 10 名演员，每部影片就会生成 102 == 100 个桶。如果总共有 20，000 部影片，粗率计算就会生成 2，000，000 个桶。

现在，记住，聚合只是简单的希望得到前十位演员和与他们联合出演者，总共 50 条数据。为了得到最终的结果，我们创建了一个有 2，000，000 桶的树，然后对其排序，取 top10。 图 [Figure 43, “Sort tree”](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_preventing_combinatorial_explosions.html#depth-first-2) 和图 [Figure 44, “Prune tree”](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_preventing_combinatorial_explosions.html#depth-first-3) 对这个过程进行了阐述。

![Sort tree](https://www.elastic.co/guide/cn/elasticsearch/guide/current/images/300_120_depth_first_2.svg)

![Prune tree](https://www.elastic.co/guide/cn/elasticsearch/guide/current/images/300_120_depth_first_3.svg)

这时我们一定非常抓狂，在 2 万条数据下执行任何聚合查询都是毫无压力的。如果我们有 2 亿文档，想要得到前 100 位演员以及与他们合作最多的 20 位演员，作为查询的最终结果会出现什么情况呢？

可以推测聚合出来的分组数非常大，会使这种策略难以维持。世界上并不存在足够的内存来支持这种不受控制的聚合查询。

**深度优先与广度优先**

Elasticsearch 允许我们改变聚合的 *集合模式* ，就是为了应对这种状况。 我们之前展示的策略叫做 *深度优先* ，它是默认设置， 先构建完整的树，然后修剪无用节点。 *深度优先* 的方式对于大多数聚合都能正常工作，但对于如我们演员和联合演员这样例子的情形就不太适用。

> 为了应对这些特殊的应用场景，我们应该使用另一种集合策略叫做 *广度优先* 。这种策略的工作方式有些不同，它先执行第一层聚合， *再* 继续下一层聚合之前会先做修剪。

在我们的示例中， `actors` 聚合会首先执行，在这个时候，我们的树只有一层，但我们已经知道了前 10 位的演员！这就没有必要保留其他的演员信息，因为它们无论如何都不会出现在前十位中。

**Figure 45. Build first level**

![Build first level](https://www.elastic.co/guide/cn/elasticsearch/guide/current/images/300_120_breadth_first_1.svg)

**Figure 46. Sort first level**

![Sort first level](https://www.elastic.co/guide/cn/elasticsearch/guide/current/images/300_120_breadth_first_2.svg)

**Figure 47. Prune first level**

![Prune first level](https://www.elastic.co/guide/cn/elasticsearch/guide/current/images/300_120_breadth_first_3.svg)

因为我们已经知道了前十名演员，我们可以安全的修剪其他节点。修剪后，下一层是基于 *它的* 执行模式读入的，重复执行这个过程直到聚合完成， 这种场景下，广度优先可以大幅度节省内存。

**Figure 48. Populate full depth for remaining nodes**

![Step 4: populate full depth for remaining nodes](https://www.elastic.co/guide/cn/elasticsearch/guide/current/images/300_120_breadth_first_4.svg)

要使用广度优先，只需简单 的通过参数 `collect` 开启：

```js
{
  "aggs" : {
    "actors" : {
      "terms" : {
         "field" :        "actors",
         "size" :         10,
         "collect_mode" : "breadth_first" 
      },
      "aggs" : {
        "costars" : {
          "terms" : {
            "field" : "actors",
            "size" :  5
          }
        }
      }
    }
  }
}
```

 按聚合来开启 `breadth_first`

广度优先仅仅适用于每个组的聚合数量远远小于当前总组数的情况下，因为广度优先会在内存中缓存裁剪后的仅仅需要缓存的每个组的所有数据，以便于它的子聚合分组查询可以复用上级聚合的数据。

广度优先的内存使用情况与裁剪后的缓存分组数据量是成线性的。对于很多聚合来说，每个桶内的文档数量是相当大的。 想象一种按月分组的直方图，总组数肯定是固定的，因为每年只有12个月，这个时候每个月下的数据量可能非常大。这使广度优先不是一个好的选择，这也是为什么深度优先作为默认策略的原因。

针对上面演员的例子，如果数据量越大，那么默认的使用深度优先的聚合模式生成的总分组数就会非常多，但是预估二级的聚合字段分组后的数据量相比总的分组数会小很多所以这种情况下使用广度优先的模式能大大节省内存，从而通过优化聚合模式来大大提高了在某些特定场景下聚合查询的成功率。



聚合 被分词处理： https://www.elastic.co/guide/cn/elasticsearch/guide/current/aggregations-and-analysis.html

指定对应的字段不分词就行了：为 `state` 定义 multifield 并且设置成 `not_analyzed`

























