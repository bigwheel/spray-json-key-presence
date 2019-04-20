import org.scalatest.{FunSpec, Matchers}
import spray.json._
import tekito._

class KeyPresenceSpec extends FunSpec with Matchers {

  private[this] val _1_notNullValue = """{ "a": 1 }""".parseJson
  private[this] val _2_nullValue = """{ "a": null }""".parseJson
  private[this] val _3_noKeyPresence = """{}""".parseJson

  describe("in plain spray-json") {

    describe("with case class `CaseClass(a: Int)`") {
      case class CaseClass(a: Int)
      object CaseClass extends DefaultJsonProtocol {
        implicit val playSprayFormat = jsonFormat1(CaseClass.apply)
      }

      it("`{ \"a\": 1 }`    is parsed As `CaseClass(1)`") {
        _1_notNullValue.convertTo[CaseClass] should be(CaseClass(1))
      }

      it("`{ \"a\": null }` throws Exception") {
        assertThrows[DeserializationException] { _2_nullValue.convertTo[CaseClass] }
      }

      it("`{}`            throws Exception") {
        assertThrows[DeserializationException] { _3_noKeyPresence.convertTo[CaseClass] }
      }
    }

    describe("with case class `CaseClass(a: Option[Int])`") {
      case class CaseClass(a: Option[Int])
      object CaseClass extends DefaultJsonProtocol {
        implicit val playSprayFormat = jsonFormat1(CaseClass.apply)
      }

      it("`{ \"a\": 1 }`    is parsed As `CaseClass(Some(1))`") {
        _1_notNullValue.convertTo[CaseClass] should be(CaseClass(Some(1)))
      }

      it("`{ \"a\": null }` is parsed As `CaseClass(None)`") {
        _2_nullValue.convertTo[CaseClass] should be(CaseClass(None))
      }

      it("`{}`            is parsed As `CaseClass(None)`") {
        _3_noKeyPresence.convertTo[CaseClass] should be(CaseClass(None))
      }
    }

    describe(
      "with case class `CaseClass(a: Option[Option[Int]])`" +
        " (as below, doesn't work as I expected)"
    ) {
      case class CaseClass(a: Option[Option[Int]])
      object CaseClass extends DefaultJsonProtocol {
        implicit val playSprayFormat = jsonFormat1(CaseClass.apply)
      }

      it("`{ \"a\": 1 }`    is parsed As `CaseClass(Some(Some(1)))`") {
        _1_notNullValue.convertTo[CaseClass] should be(CaseClass(Some(Some(1))))
      }

      it(
        "`{ \"a\": null }` is parsed As `CaseClass(None)`" +
          " but I expected `CaseClass(Some(None))`"
      ) {
        _2_nullValue.convertTo[CaseClass] should be(CaseClass(None))
      }

      it("`{}`            is parsed As `CaseClass(None)`") {
        _3_noKeyPresence.convertTo[CaseClass] should be(CaseClass(None))
      }
    }

  }

  describe("in spray-json with this library") {

    describe("with case class `CaseClass(a: KeyPresence[Int])`") {
      case class CaseClass(a: KeyPresence[Int])
      object CaseClass extends KeyPresenceJsonProtocol {
        implicit val keyPresenceSprayFormat = jsonFormat1(CaseClass.apply)
      }

      it("`{ \"a\": 1 }`    is parsed As `CaseClass(KeyExist(1))`") {
        _1_notNullValue.convertTo[CaseClass] should be(CaseClass(KeyExist(1)))
      }

      it("`{ \"a\": null }` throws Exception") {
        assertThrows[DeserializationException] { _2_nullValue.convertTo[CaseClass] }
      }

      it("`{}`            is parsed As `CaseClass(KeyNotExist)`") {
        _3_noKeyPresence.convertTo[CaseClass] should be(CaseClass(KeyNotExist))
      }
    }

    describe("with case class `CaseClass(a: KeyPresence[Option[Int]])`") {
      case class CaseClass(a: KeyPresence[Option[Int]])
      object CaseClass extends KeyPresenceJsonProtocol {
        implicit val keyPresenceSprayFormat = jsonFormat1(CaseClass.apply)
      }

      it("`{ \"a\": 1 }`    is parsed As `CaseClass(KeyExist(Some(1)))`") {
        _1_notNullValue.convertTo[CaseClass] should be(CaseClass(KeyExist(Some(1))))
      }

      it("`{ \"a\": null }` is parsed As `CaseClass(KeyExist(None))`") {
        _2_nullValue.convertTo[CaseClass] should be(CaseClass(KeyExist(None)))
      }

      it("`{}`            is parsed As `CaseClass(KeyNotExist)`") {
        _3_noKeyPresence.convertTo[CaseClass] should be(CaseClass(KeyNotExist))
      }
    }

  }

}
