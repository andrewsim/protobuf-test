package protobuftest.extensions

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

import protobuftest.proto.{CommonTypes => ProtoBuf}

package object commontypes {

  import scala.language.implicitConversions

  implicit def uuidToProtoBuf(x: UUID): ProtoBuf.UUID =
    ProtoBuf.UUID(x.getLeastSignificantBits, x.getMostSignificantBits)

  implicit def offsetDateTimeToProtoBuf(x: OffsetDateTime): ProtoBuf.OffsetDateTime =
    ProtoBuf.OffsetDateTime(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(x))

  implicit def bigDecimalToProtoBuf(x: BigDecimal): ProtoBuf.BigDecimal =
    ProtoBuf.BigDecimal(x.toString)

}
