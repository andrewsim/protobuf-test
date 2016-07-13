package protobuftest

import java.time.OffsetDateTime
import java.util.UUID

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.serialization._
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import protobuftest.actor.{Stop, GetMessages, TestPersistentActor}
import protobuftest.extensions.commontypes._

import scala.concurrent.duration._

object Main extends App {

  val conf = ConfigFactory.load()

  implicit val system = ActorSystem("protobuftest", conf)
  implicit val executionContext = system.dispatcher
  implicit val askTimeout: Timeout = 10.seconds

  println("\nCreating V1 actor...")
  val actor1 = system.actorOf(TestPersistentActor.props)

  println("\nSending V1 messages...")
  sendVersion1Messages(actor1)

  (actor1 ? GetMessages).mapTo[List[TestPersistentActor.Command]]
    .foreach { result =>
      println("\nRetrieving V1 messages...")
      println(s"Received messages: $result")
      actor1 ! Stop
    }

  Thread.sleep(5000)

  system.terminate()

  def sendVersion1Messages(actorRef: ActorRef): Unit = {
    import protobuftest.proto.actor.TestPersistentActorMessages._

    // actorRef ! MessageA()
    // val messageB = MessageB(1, 1L, UUID.randomUUID(), "1")
    val testMessage = TestMessage("a", None)
    val serialization = SerializationExtension(system)
    val serializer = serialization.findSerializerFor(testMessage)
    println(s"serialization: ${serializer.toString}")
    actorRef ! testMessage
    // actorRef ! MessageC(OffsetDateTime.now, Embedded(Some(BigDecimal("1")), Some(Embedded(None, None))))
  }

}
