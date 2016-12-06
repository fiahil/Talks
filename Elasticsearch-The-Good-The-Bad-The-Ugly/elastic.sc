import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{ElasticClient, ElasticsearchClientUri}
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.shield.ShieldPlugin

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Your ESCloud cluster ID
  */
val clusterId = "<< MY CLUSTER ID >>"

/** Optional
  * Creating a client execute queries
  */
val client = ElasticClient.transport(
  Settings.builder()
    .put("cluster.name", clusterId)
    .put("request.headers.X-Found-Cluster", clusterId)
    .put("transport.sniff", false)
    .put("shield.transport.ssl", true)
    .put("transport.ping_schedule", "5s")
    .build(),
  ElasticsearchClientUri(s"elasticsearch://$clusterId.eu-west-1.aws.found.io", 9343),
  classOf[ShieldPlugin]
)

/**
  * Build your queries interactively!
  */
val v = search in "MY_INDEX" / "MY_TYPE" query {
  termQuery("name", "naroon")
}

/**
  * You have to use Await to see results in a WorkSheet
  */
Await.result(client.execute(v), Duration.Inf)
