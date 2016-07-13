package protobuftest


import java.util.UUID

import akka.actor.Props
import akka.remote.testkit.MultiNodeSpec
import akka.testkit.ImplicitSender
import protobuftest.actor.TestPersistentActor
import protobuftest.proto.actor.TestPersistentActorMessages._
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
      runOn(node1) {
        enterBarrier("deployed")
        val messageA = MessageA()
        val messageA2 = MessageA()
        val messageB = MessageB(111, 222, UUID.randomUUID(), "string")
        val messageTest = MessageTest().withMsgA2(messageA2)
        val messageTest2 = MessageTest().withMsgB(messageB)
        val messageWrapX = WrapX(messageTest)
        val messageWrapX2 = WrapX(messageTest2)
        val remoteActor = system.actorSelection(node(node2) / "user" / "test-persistent-actor")
//        remoteActor ! messageTest
//        expectMsg(3 seconds, 111)
//        remoteActor ! messageTest2
//        expectMsg(3 seconds, 111)
        remoteActor ! messageWrapX
        expectMsg(3 seconds, 111)
        remoteActor ! messageWrapX2
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
