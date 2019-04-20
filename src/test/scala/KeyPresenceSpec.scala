import org.scalatest.{FunSpec, Matchers}
import spray.json._
import tekito._

class KeyPresenceSpec extends FunSpec with Matchers {

  private[this] val nullValue = """{"a": null}"""
  private[this] val notNullValue = """{"a": 1}"""
  private[this] val noKeyPresence = """{}"""

  describe("play spray-json") {

    describe("with not option case class") {
      case class PlainSpray(a: Int)
      import DefaultJsonProtocol._
      implicit val playSprayFormat = jsonFormat1(PlainSpray)

      it("null value throws Exception") {
        assertThrows[DeserializationException] {
          nullValue.parseJson.convertTo[PlainSpray]
        }
      }

      it("not null value is parsed As 1") {
        notNullValue.parseJson.convertTo[PlainSpray] should be(PlainSpray(1))
      }

      it("no key presence throws Exception") {
        assertThrows[DeserializationException] {
          noKeyPresence.parseJson.convertTo[PlainSpray]
        }
      }
    }

    describe("with option case class") {
      case class PlainSpray(a: Option[Int])
      import DefaultJsonProtocol._
      implicit val playSprayFormat = jsonFormat1(PlainSpray)

      it("null value is parsed as None") {
        nullValue.parseJson.convertTo[PlainSpray] should be(PlainSpray(None))
      }

      it("not null value is parsed as Some(1)") {
        notNullValue.parseJson.convertTo[PlainSpray] should be(PlainSpray(Some(1)))
      }

      it("no key presence is parsed as None") {
        noKeyPresence.parseJson.convertTo[PlainSpray] should be(PlainSpray(None))
      }
    }

    describe("with double option case class (as followings, this doesn't work as I expected)") {
      case class PlainSpray(a: Option[Option[Int]])
      import DefaultJsonProtocol._
      implicit val playSprayFormat = jsonFormat1(PlainSpray)

      it("null value is parsed as None") {
        nullValue.parseJson.convertTo[PlainSpray] should be(PlainSpray(None))
      }

      it("not null value is parsed as Some(1)") {
        notNullValue.parseJson.convertTo[PlainSpray] should be(PlainSpray(Some(Some(1))))
      }

      it("no key presence is parsed as None") {
        noKeyPresence.parseJson.convertTo[PlainSpray] should be(PlainSpray(None))
      }
    }

    describe("with KeyPresence case class") {
      case class KeyPresenceSpray(a: KeyPresence[Int])
      import KeyPresenceJsonProtocol._
      implicit val keyPresenceSprayFormat = jsonFormat1(KeyPresenceSpray)

      it("null value throws Exception") {
        assertThrows[DeserializationException] {
          nullValue.parseJson.convertTo[KeyPresenceSpray]
        }
      }

      it("not null value is parsed as KeyExist(1)") {
        notNullValue.parseJson.convertTo[KeyPresenceSpray] should be(
          KeyPresenceSpray(KeyExist(1))
        )
      }

      it("no key presence is parsed as KeyNotExist") {
        noKeyPresence.parseJson.convertTo[KeyPresenceSpray] should be(KeyPresenceSpray(KeyNotExist))
      }
    }

    describe("with KeyPresence option case class") {
      case class KeyPresenceSpray(a: KeyPresence[Option[Int]])
      import KeyPresenceJsonProtocol._
      implicit val keyPresenceSprayFormat = jsonFormat1(KeyPresenceSpray)

      it("null value is parsed as KeyExist(None)") {
        nullValue.parseJson.convertTo[KeyPresenceSpray] should be(KeyPresenceSpray(KeyExist(None)))
      }

      it("not null value is parsed as KeyExist(Some(1))") {
        notNullValue.parseJson.convertTo[KeyPresenceSpray] should be(
          KeyPresenceSpray(KeyExist(Some(1)))
        )
      }

      it("no key presence is parsed as KeyNotExist") {
        noKeyPresence.parseJson.convertTo[KeyPresenceSpray] should be(KeyPresenceSpray(KeyNotExist))
      }
    }

  }

}
