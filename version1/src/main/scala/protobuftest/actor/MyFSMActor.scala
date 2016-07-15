package protobuftest.actor

import java.util.UUID

import akka.actor.Props
import akka.persistence.fsm.PersistentFSM
import protobuftest.proto.actor.TestPersistentActorMessages.MyFSMStates.StateA
import protobuftest.proto.actor.TestPersistentActorMessages.OrderPhase.PhaseA
import protobuftest.proto.actor.TestPersistentActorMessages._

import scala.reflect.ClassTag

object MyFSMActor {

  object StateNames {
    val myStateA: String = "myStateA"
//    val myStateB: String = "myStateB"
//    val myStateC: String = "myStateC"
  }
  sealed trait MyFSMState extends PersistentFSM.FSMState
  trait MyStateA extends MyFSMState { val identifier = StateNames.myStateA }
//  trait MyStateB extends MyFSMState { val identifier = StateNames.myStateB }
//  trait MyStateC extends MyFSMState { val identifier = StateNames.myStateC }

  def props = Props(new MyFSMActor)

}

class MyFSMActor(implicit val domainEventClassTag: ClassTag[MyDomainEvent]) extends PersistentFSM[MyFSMActor.MyFSMState, MyStateData, MyDomainEvent] {

  override def persistenceId: String = UUID.randomUUID().toString

  object OrderEventExtractor {
    import com.trueaccord.scalapb.GeneratedMessage

    import scala.reflect.ClassTag
    def extractOneof(x: GeneratedMessage, fieldNumbers: Seq[Int]): Option[Any] = {
      x.companion.descriptor.fields
        .filter(field ⇒ fieldNumbers.contains(field.number))
        .map(field ⇒ x.getField(field))
        .filter(value ⇒ value.isInstanceOf[Option[Any]])
        .map(_.asInstanceOf[Option[Any]])
        .collectFirst { case Some(value) ⇒ value }
    }
    def extractOneofTyped[T](x: GeneratedMessage, fieldNumbers: Seq[Int])(implicit classTag: ClassTag[T]): Option[T] =
      extractOneof(x, fieldNumbers).flatMap(classTag.unapply)
    def unapply(x: MyDomainEvent): Option[Any] = extractOneof(x, 1 to 2)
  }
  object OrderCommandExtractor {
    import com.trueaccord.scalapb.GeneratedMessage

    import scala.reflect.ClassTag
    def extractOneof(x: GeneratedMessage, fieldNumbers: Seq[Int]): Option[Any] = {
      x.companion.descriptor.fields
        .filter(field ⇒ fieldNumbers.contains(field.number))
        .map(field ⇒ x.getField(field))
        .filter(value ⇒ value.isInstanceOf[Option[Any]])
        .map(_.asInstanceOf[Option[Any]])
        .collectFirst { case Some(value) ⇒ value }
    }
    def extractOneofTyped[T](x: GeneratedMessage, fieldNumbers: Seq[Int])(implicit classTag: ClassTag[T]): Option[T] =
      extractOneof(x, fieldNumbers).flatMap(classTag.unapply)
    def unapply(x: MyCommand): Option[Any] = extractOneof(x, 1 to 3)
  }

  startWith(StateA(), MyStateData("", OrderPhase(OrderPhase.Phase.StateA(MyFSMStates.StateA()), OrderPhase.State.PhaseA(PhaseA.PhaseAAlpha))))

  override def applyEvent(domainEvent: MyDomainEvent, currentData: MyStateData): MyStateData =  currentData
//    domainEvent match {
//    case (OrderEventExtractor(ApplyInitialize(id))) ⇒
//      currentData.copy(id = id, phase = OrderPhase(OrderPhase.Phase.StateB(MyFSMStates.StateB()), OrderPhase.State.PhaseB(PhaseB.PhaseBBeta)))
//    case (OrderEventExtractor(ApplyOrderPhase(orderPhase))) =>
//      currentData.copy(phase = orderPhase)
//  }

  when(StateA()) {
//    case Event(OrderCommandExtractor(Initialize()), data) =>
//      goto(StateB()) applying MyDomainEvent().withApplyInitialize(ApplyInitialize(UUID.randomUUID().toString)) replying data
//    case Event(OrderCommandExtractor(GetMyState()), data) =>
//      stay() replying 111
    case _ => stay()
  }

//  when(StateB()) {
//    case Event(OrderCommandExtractor(SetOrderPhase(orderPhase)), data) =>
//      stay() applying MyDomainEvent().withApplyOrderPhase(ApplyOrderPhase(orderPhase)) replying data
//
//    case Event(OrderCommandExtractor(GetMyState()), data) =>
//      stay() replying data
//  }

}
