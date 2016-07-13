package protobuftest

import akka.actor.ExtendedActorSystem
import akka.persistence.journal.{EventSeq, EventAdapter}
import com.trueaccord.scalapb.GeneratedMessage
import protobuftest.proto.actor.TestPersistentActorMessages.TestMessage

class JavaToProtobufEventAdapter(system: ExtendedActorSystem) extends EventAdapter {

  override def manifest(event: Any): String = ""

  override def toJournal(event: Any): Any = event

  override def fromJournal(event: Any, manifest: String): EventSeq = event match {
    case event@ TestMessage(a, b, c) =>
      println(s"original event: $event")
      EventSeq(TestMessage(a, Option(b).flatten, Option(c).flatten))
    case _ =>
      println("_____________")
      EventSeq()
  }

}
