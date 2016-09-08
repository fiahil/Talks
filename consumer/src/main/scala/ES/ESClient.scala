package ES

import java.util.concurrent.Executors

import com.sksamuel.elastic4s.ElasticClient
import org.elasticsearch.common.settings.Settings

import scala.concurrent.ExecutionContext

object ESClient {

  val settings = Settings.settingsBuilder()
                 .put("cluster.name", "elasticsearch_fiahil")
                 .build()

  implicit val ESExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(5))

  val c = ElasticClient.transport(settings, "elasticsearch://localhost:9300")
}
