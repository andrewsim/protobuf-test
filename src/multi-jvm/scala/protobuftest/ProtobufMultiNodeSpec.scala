package protobuftest


import java.util.UUID

import akka.actor.Props
import akka.remote.testkit.MultiNodeSpec
import akka.testkit.ImplicitSender
import protobuftest.actor.TestPersistentActor
import protobuftest.proto.actor.TestPersistentActorMessages.{MessageB, TestMessage}
import protobuftest.extensions.commontypes._
import scala.concurrent.duration._

class ProtobufMultiJvmNode1 extends ProtobufMultiNodeSpec
class ProtobufMultiJvmNode2 extends ProtobufMultiNodeSpec

class ProtobufMultiNodeSpec extends MultiNodeSpec(ProtobufMultiNodeConfig)
  with MultiNodeBaseSpec with ImplicitSender {
  import ProtobufMultiNodeConfig._

  def initialParticipants = roles.size

  "A ProtobufMultiNode" must {

    "wait for all nodes to enter a barrier" in {
      enterBarrier("startup")
    }

    "send to and receive from a remote node" in {
      val message = MessageB(111, 222, UUID.randomUUID(), "string")
      runOn(node1) {
        enterBarrier("deployed")
        val remoteActor = system.actorSelection(node(node2) / "user" / "test-persistent-actor")
        remoteActor ! message
        expectMsg(3 seconds, 111)
      }

      runOn(node2) {
        system.actorOf(TestPersistentActor.props, "test-persistent-actor")
        enterBarrier("deployed")
      }

      enterBarrier("finished")
    }

  }

}
