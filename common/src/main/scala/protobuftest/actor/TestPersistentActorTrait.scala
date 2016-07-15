package protobuftest.actor

import akka.actor.ActorLogging
import akka.persistence.{RecoveryCompleted, PersistentActor}

import scala.reflect.ClassTag

case object GetMessages
case object Stop

trait TestPersistentActorTrait { _: PersistentActor with ActorLogging =>

  type Message
  val tag: ClassTag[Message]

  var messages = List.empty[Message]

  def persistenceId: String = java.util.UUID.randomUUID().toString

  def receiveRecover: Receive = {
    case RecoveryCompleted =>
      println("Recovery completed.")

    case tag(message) =>
      messages = message :: messages

    case unknown =>
      println(s"Unknown message arrived to ${getClass.getName}: $unknown (${unknown.getClass.getCanonicalName})")
  }

  def receiveCommand: Receive = {
    case GetMessages =>
      sender() ! messages.reverse

    case Stop =>
      context.stop(self)

    case tag(message) =>
      println(s"message: $message @ ${self.path}")
      persist(message) { _ =>
        messages = message :: messages
        sender() ! 111
      }

    case a =>
      println(s"+++++++++++++++MESSAGE: $a++++++++++++++++++++++")
      sender() ! a

    case 111 =>
      // do nothing
  }

}
