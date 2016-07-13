package protobuftest.actor

import akka.actor.{ActorLogging, Props}
import akka.persistence.PersistentActor

import scala.reflect._

object TestPersistentActor {

  trait Command

  def props: Props = Props(new TestPersistentActor)

}

class TestPersistentActor extends PersistentActor with ActorLogging with TestPersistentActorTrait {

  type Message = TestPersistentActor.Command
  val tag = classTag[Message]

}
