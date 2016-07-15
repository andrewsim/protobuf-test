package protobuftest


import akka.remote.testkit.MultiNodeSpec
import akka.testkit.ImplicitSender
import protobuftest.actor.TestPersistentActor
import protobuftest.proto.actor.TestPersistentActorMessages.MyCommand.GetMyState
import protobuftest.proto.actor.TestPersistentActorMessages._

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
//        val remoteActor = system.actorSelection(node(node2) / "user" / "test-persistent-actor")
        val remoteActor2 = system.actorSelection(node(node2) / "user" / "test---persistent")
        remoteActor2 ! MyCommand().withGetState(GetMyState(1))
//        expectMsg(3 seconds, MyStateData("", OrderPhase(OrderPhase.Phase.StateA(MyFSMStates.StateA()), OrderPhase.State.PhaseA(PhaseA.PhaseAAlpha))))
        expectMsg(3 seconds, MyCommand().withGetState(GetMyState(1)))
        remoteActor2 ! GetMyState(1)
        expectMsg(3 seconds, GetMyState(1))
      }

      runOn(node2) {
//        system.actorOf(MyFSMActor.props, "test-persistent-actor")
        system.actorOf(TestPersistentActor.props, "test---persistent")
        enterBarrier("deployed")
        Thread.sleep(1000)
      }

      enterBarrier("finished")
    }

  }

}
