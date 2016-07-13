package protobuftest

import akka.remote.testkit.MultiNodeConfig
import com.typesafe.config.{ConfigFactory, Config}

object ProtobufMultiNodeConfig extends MultiNodeConfig {

  val node1 = role("node1")
  val node2 = role("node2")

  commonConfig(ConfigFactory.parseString(
    s"""
       |akka {
       |  debug {
       |    unhandled = on
       |    lifecycle = on
       |    log-sent-messages = on
       |    log-received-messages = on
       |  }
       |  remote {
       |    log-sent-messages = on
       |    log-received-messages = on
       |  }
       |  actor {
       |    serializers {
       |      proto = "akka.remote.serialization.ProtobufSerializer"
       |    }
       |    serialization-bindings {
       |      "com.trueaccord.scalapb.GeneratedMessage" = proto
       |    }
       |  }
       |  persistence {
       |    journal {
       |      plugin = "akka.persistence.journal.leveldb"
       |      leveldb.dir = "target/journal"
       |    }
       |    snapshot-store {
       |      plugin = "akka.persistence.snapshot-store.local"
       |      local.dir = "target/snapshots"
       |    }
       |  }
       |}
     """.stripMargin
  ))

  debugConfig(true)


}
