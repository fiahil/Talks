package ES

import com.sksamuel.elastic4s.ElasticClient
import org.elasticsearch.common.settings.Settings

object ESClient {

  val settings = Settings.settingsBuilder()
                 .put("cluster.name", "elasticsearch_fiahil")
                 .build()

  val c = ElasticClient.transport(settings, "elasticsearch://localhost:9300")
}
