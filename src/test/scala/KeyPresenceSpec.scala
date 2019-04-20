import org.scalatest.{FunSpec, Matchers}
import spray.json._
import tekito._

class KeyPresenceSpec extends FunSpec with Matchers {

  private[this] val nullValue = """{"a": null}""".parseJson
  private[this] val notNullValue = """{"a": 1}""".parseJson
  private[this] val noKeyPresence = """{}""".parseJson

  describe("play spray-json") {

    describe("with not option case class") {
      case class PlainSpray(a: Int)
      object PlainSpray extends DefaultJsonProtocol {
        implicit val playSprayFormat = jsonFormat1(PlainSpray.apply)
      }

      it("null value throws Exception") {
        assertThrows[DeserializationException] {
          nullValue.convertTo[PlainSpray]
        }
      }

      it("not null value is parsed As 1") {
        notNullValue.convertTo[PlainSpray] should be(PlainSpray(1))
      }

      it("no key presence throws Exception") {
        assertThrows[DeserializationException] {
          noKeyPresence.convertTo[PlainSpray]
        }
      }
    }

    describe("with option case class") {
      case class PlainSpray(a: Option[Int])
      object PlainSpray extends DefaultJsonProtocol {
        implicit val playSprayFormat = jsonFormat1(PlainSpray.apply)
      }

      it("null value is parsed as None") {
        nullValue.convertTo[PlainSpray] should be(PlainSpray(None))
      }

      it("not null value is parsed as Some(1)") {
        notNullValue.convertTo[PlainSpray] should be(PlainSpray(Some(1)))
      }

      it("no key presence is parsed as None") {
        noKeyPresence.convertTo[PlainSpray] should be(PlainSpray(None))
      }
    }

    describe("with double option case class (as followings, this doesn't work as I expected)") {
      case class PlainSpray(a: Option[Option[Int]])
      object PlainSpray extends DefaultJsonProtocol {
        implicit val playSprayFormat = jsonFormat1(PlainSpray.apply)
      }

      it("null value is parsed as None") {
        nullValue.convertTo[PlainSpray] should be(PlainSpray(None))
      }

      it("not null value is parsed as Some(1)") {
        notNullValue.convertTo[PlainSpray] should be(PlainSpray(Some(Some(1))))
      }

      it("no key presence is parsed as None") {
        noKeyPresence.convertTo[PlainSpray] should be(PlainSpray(None))
      }
    }

  }

  describe("play spray-json") {

    describe("with KeyPresence case class") {
      case class KeyPresenceSpray(a: KeyPresence[Int])
      object KeyPresenceSpray extends KeyPresenceJsonProtocol {
        implicit val keyPresenceSprayFormat = jsonFormat1(KeyPresenceSpray.apply)
      }

      it("null value throws Exception") {
        assertThrows[DeserializationException] {
          nullValue.convertTo[KeyPresenceSpray]
        }
      }

      it("not null value is parsed as KeyExist(1)") {
        notNullValue.convertTo[KeyPresenceSpray] should be(
          KeyPresenceSpray(KeyExist(1))
        )
      }

      it("no key presence is parsed as KeyNotExist") {
        noKeyPresence.convertTo[KeyPresenceSpray] should be(KeyPresenceSpray(KeyNotExist))
      }
    }

    describe("with KeyPresence option case class") {
      case class KeyPresenceSpray(a: KeyPresence[Option[Int]])
      object KeyPresenceSpray extends KeyPresenceJsonProtocol {
        implicit val keyPresenceSprayFormat = jsonFormat1(KeyPresenceSpray.apply)
      }

      it("null value is parsed as KeyExist(None)") {
        nullValue.convertTo[KeyPresenceSpray] should be(KeyPresenceSpray(KeyExist(None)))
      }

      it("not null value is parsed as KeyExist(Some(1))") {
        notNullValue.convertTo[KeyPresenceSpray] should be(
          KeyPresenceSpray(KeyExist(Some(1)))
        )
      }

      it("no key presence is parsed as KeyNotExist") {
        noKeyPresence.convertTo[KeyPresenceSpray] should be(KeyPresenceSpray(KeyNotExist))
      }
    }

  }

}
