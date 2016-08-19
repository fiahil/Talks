package Kafka

import ES.IndexInto
import Pokemon.Pokemon
import kafka.consumer.KafkaStream
import kafka.message.MessageAndMetadata

import scala.concurrent.ExecutionContext

object Message {

  def display(stream: KafkaStream[Array[Byte], Array[Byte]])(implicit ec: ExecutionContext) = {
    val it = stream.iterator()

    it.forEachRemaining(new java.util.function.Consumer[MessageAndMetadata[Array[Byte], Array[Byte]]] {
      override def accept(m: MessageAndMetadata[Array[Byte], Array[Byte]]): Unit = {

        Pokemon.fromMessage(m.message()).foreach { pk =>
          println(pk)
          IndexInto.pokemon(pk)
        }
      }
    })
  }
}
