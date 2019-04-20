import org.scalatest.{FunSpec, Matchers}
import spray.json._

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
      type JF[T] = JsonFormat[T] // simple alias for reduced verbosity

      implicit def keyPresenceFormat[T: JF]: JF[KeyPresence[T]] = new KeyPresenceFormat[T]

      class KeyPresenceFormat[T: JF] extends JF[KeyPresence[T]] {
        def write(option: KeyPresence[T]) = option match {
          case KeyExist(x) => x.toJson
          case KeyNotExist => JsNull
        }
        def read(value: JsValue) = KeyExist(value.convertTo[T])
      }

      case class KeyPresenceSpray(a: KeyPresence[Int])
      object TekitoScope extends DefaultJsonProtocol {
        override protected def fromField[T](value: JsValue, fieldName: String)(
            implicit reader: JsonReader[T]
        ) = value match {
          case x: JsObject
              if reader.isInstanceOf[KeyPresenceFormat[_]] &
                !x.fields.contains(fieldName) =>
            KeyNotExist.asInstanceOf[T]
          case x: JsObject =>
            try reader.read(x.fields(fieldName))
            catch {
              case e: NoSuchElementException =>
                deserializationError(
                  "Object is missing required member '" + fieldName + "'",
                  e,
                  fieldName :: Nil
                )
              case DeserializationException(msg, cause, fieldNames) =>
                deserializationError(msg, cause, fieldName :: fieldNames)
            }
          case _ =>
            deserializationError(
              "Object expected in field '" + fieldName + "'",
              fieldNames = fieldName :: Nil
            )
        }

        implicit val keyPresenceSprayFormat = jsonFormat1(KeyPresenceSpray)
      }

      import TekitoScope.keyPresenceSprayFormat

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

  }

}
