package services.ES

import java.util.concurrent.Executors

import com.sksamuel.elastic4s.ElasticClient
import org.elasticsearch.common.settings.Settings

import scala.concurrent.ExecutionContext

object ESClient {

  val config = Settings.settingsBuilder()
                 .put("cluster.name", "elasticsearch_fiahil")
                 .build()

  // Our execution context handling ES index and queries
  implicit val ESContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(5))

  val c = ElasticClient.transport(config, "elasticsearch://localhost:9300")
}
