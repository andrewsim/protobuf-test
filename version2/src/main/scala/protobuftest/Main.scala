package protobuftest

import java.time.OffsetDateTime
import java.util.UUID

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import protobuftest.actor.{TestPersistentActor, Stop, GetMessages}
import protobuftest.extensions.commontypes._

import scala.concurrent.duration._

object Main extends App {

  val conf = ConfigFactory.load()

  implicit val system = ActorSystem("protobuftest", conf)
  implicit val executionContext = system.dispatcher
  implicit val askTimeout: Timeout = 10.seconds

  println("\nCreating V2 actor...")
  val actor2 = system.actorOf(TestPersistentActor.props)

  println("\nSending V2 messages...")
//  sendVersion2Messages(actor2)

  (actor2 ? GetMessages).mapTo[List[TestPersistentActor.Command]]
    .foreach { result =>
      println("\nRetrieving V2 messages...")
      println(s"Received messages: $result")
      actor2 ! Stop
    }

  Thread.sleep(5000)
  system.terminate()

  def sendVersion2Messages(actorRef: ActorRef): Unit = {
    import protobuftest.proto.actor.TestPersistentActorMessages._

    actorRef ! MessageA()
//    actorRef ! MessageB(1, 1L, UUID.randomUUID(), "1", Seq(UUID.randomUUID(), UUID.randomUUID()))
    actorRef ! MessageC(Embedded(BigDecimal("1"), Some(Embedded(BigDecimal("2"), None))))
    actorRef ! MessageD(UUID.randomUUID(), SomeMap(Seq(
      SomeMapItem(UUID.randomUUID(), Item(UUID.randomUUID(), Embedded(BigDecimal("1"), None), field3 = Some("some"))),
      SomeMapItem(UUID.randomUUID(), Item(UUID.randomUUID(), Embedded(BigDecimal("2"), None), field3 = Some("last")))
    )))
  }

}
