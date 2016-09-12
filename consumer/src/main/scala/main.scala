import services.Kafka.KafkaClient
import web.Server


object main extends App {

  KafkaClient.start
  Server.start
}
