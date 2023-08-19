package akka_akka_streams.homework

import akka.actor.ActorSystem
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source, ZipWith}
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.{Done, NotUsed}
import ch.qos.logback.classic.{Level, Logger}
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContextExecutor, Future}


object homeworktemplate {
  implicit val system = ActorSystem("fusion")
  implicit val materializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  LoggerFactory
    .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
    .asInstanceOf[Logger]
    .setLevel(Level.ERROR)

  private val config = ConfigFactory.load()
  private val producerConfig = config.getConfig("akka.kafka.producer")
  private val producerSettings = ProducerSettings(producerConfig, new StringSerializer, new StringSerializer)

  private val topic = "test"

  private val producerFlow: Flow[String, ProducerRecord[String, String], NotUsed] = Flow[String].map(msg =>
    new ProducerRecord[String, String](topic, msg))
  private val kafkaSink: Sink[ProducerRecord[String, String], Future[Done]] = Producer.plainSink(producerSettings)

  private val producerGraph = GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._
    val messageSource: Source[String, NotUsed] = Source(1 to 50).map(_.toString)
    messageSource ~> producerFlow ~> kafkaSink
    ClosedShape
  }

  private val consumerConfig = config.getConfig("akka.kafka.consumer")
  private val consumerSettings = ConsumerSettings(consumerConfig, new StringDeserializer, new StringDeserializer)

  private val kafkaSource: Source[ConsumerRecord[String, String], Consumer.Control] = {
    Consumer.plainSource(consumerSettings, Subscriptions.topics(topic))
  }
  private val consumerFlow: Flow[ConsumerRecord[String, String], Int, NotUsed] = {
    Flow[ConsumerRecord[String, String]].map { consumerRecord =>
      consumerRecord.value().toInt
    }
  }

  private val consumerGraph = GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._
    val multiplyByTen = builder.add(Flow[Int].map(x => x * 10))
    val multiplyByTwo = builder.add(Flow[Int].map(x => x * 2))
    val multiplyByThree = builder.add(Flow[Int].map(x => x * 3))
    val output = builder.add(Sink.foreach[(Int, Int, Int)](x => println(x._1 + x._2 + x._3)))

    val broadcast = builder.add(Broadcast[Int](3))
    val zip = builder.add(ZipWith[Int, Int, Int, (Int, Int, Int)]((a, b, c) => (a, b, c)))

    kafkaSource ~> consumerFlow ~> broadcast
    broadcast.out(0) ~> multiplyByTen ~> zip.in0
    broadcast.out(1) ~> multiplyByTwo ~> zip.in1
    broadcast.out(2) ~> multiplyByThree ~> zip.in2
    zip.out ~> output
    ClosedShape
  }

  def main(args: Array[String]): Unit = {
    RunnableGraph.fromGraph(producerGraph).run()
    Thread.sleep(2000)
    RunnableGraph.fromGraph(consumerGraph).run()
  }
}