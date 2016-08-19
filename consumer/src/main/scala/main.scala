import Kafka.KafkaClient
import Web.Server


object main extends App {

  KafkaClient.start
  Server.start
}
