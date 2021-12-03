package Actors

import akka.actor.{Actor, Props}
import constants.{AppConstants, KafkaConstants, ShellCommands}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import service.ExtractorService
import sun.util.resources.cldr.kam.CalendarData_kam_KE

import java.io.File
import java.util.Properties
import java.util.logging.Logger
import scala.sys.process._

object Extractor {
  def props(file: File): Props = Props(new Extractor(file))
}

class Extractor(file: File) extends Actor {
  val props: Properties = new Properties()
  props.put(KafkaConstants.bootstrapServers_k,  KafkaConstants.bootstrapServers_v)
  props.put(KafkaConstants.keySerializer_k,     KafkaConstants.keySerializer_v)
  props.put(KafkaConstants.valueSerializer_k,   KafkaConstants.valueSerializer_v)
  props.put(KafkaConstants.acks_k,              KafkaConstants.acks_v)

  val producer = new KafkaProducer[String, String](props)
  val logger: Logger = Logger.getLogger(this.getClass.getName)
  val topic: String = KafkaConstants.topicName

  override def receive: Receive = {
    case str: String =>
                  logger.info("Extracting for file: \t" + self.path.name)
                  new ExtractorService().kafkaTry(str, file)
  }
}
