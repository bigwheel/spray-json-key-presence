import org.scalatest.{FunSpec, Matchers}
import spray.json._
import spray.json.member._

class MemberPresenceSpec extends FunSpec with Matchers {

  private[this] val _1_normalValue = """{ "a": 1 }""".parseJson
  private[this] val _2_nullValue = """{ "a": null }""".parseJson
  private[this] val _3_noMember = """{}""".parseJson

  describe("in plain spray-json") {

    describe("with case class `CaseClass(a: Int)`") {
      case class CaseClass(a: Int)
      object CaseClass extends DefaultJsonProtocol {
        implicit val caseClassFormat = jsonFormat1(CaseClass.apply)
      }

      it("`{ \"a\": 1 }`    is parsed As `CaseClass(1)`") {
        _1_normalValue.convertTo[CaseClass] should be(CaseClass(1))
      }

      it("`{ \"a\": null }` throws Exception") {
        assertThrows[DeserializationException] { _2_nullValue.convertTo[CaseClass] }
      }

      it("`{}`            throws Exception") {
        assertThrows[DeserializationException] { _3_noMember.convertTo[CaseClass] }
      }
    }

    describe("with case class `CaseClass(a: Option[Int])`") {
      case class CaseClass(a: Option[Int])
      object CaseClass extends DefaultJsonProtocol {
        implicit val caseClassFormat = jsonFormat1(CaseClass.apply)
      }

      it("`{ \"a\": 1 }`    is parsed As `CaseClass(Some(1))`") {
        _1_normalValue.convertTo[CaseClass] should be(CaseClass(Some(1)))
      }

      it("`{ \"a\": null }` is parsed As `CaseClass(None)`") {
        _2_nullValue.convertTo[CaseClass] should be(CaseClass(None))
      }

      it("`{}`            is parsed As `CaseClass(None)`") {
        _3_noMember.convertTo[CaseClass] should be(CaseClass(None))
      }
    }

    describe(
      "with case class `CaseClass(a: Option[Option[Int]])`" +
        " (as below, doesn't work as I expected)"
    ) {
      case class CaseClass(a: Option[Option[Int]])
      object CaseClass extends DefaultJsonProtocol {
        implicit val caseClassFormat = jsonFormat1(CaseClass.apply)
      }

      it("`{ \"a\": 1 }`    is parsed As `CaseClass(Some(Some(1)))`") {
        _1_normalValue.convertTo[CaseClass] should be(CaseClass(Some(Some(1))))
      }

      it(
        "`{ \"a\": null }` is parsed As `CaseClass(None)`" +
          " but I expected `CaseClass(Some(None))`"
      ) {
        _2_nullValue.convertTo[CaseClass] should be(CaseClass(None))
      }

      it("`{}`            is parsed As `CaseClass(None)`") {
        _3_noMember.convertTo[CaseClass] should be(CaseClass(None))
      }
    }

  }

  describe("in spray-json with spray-json-member-presence") {

    describe("with case class `CaseClass(a: MemberOption[Int])`") {
      case class CaseClass(a: MemberOption[Int])
      object CaseClass extends MemberPresenceJsonProtocol {
        implicit val caseClassFormat = jsonFormat1(CaseClass.apply)
      }

      it("`{ \"a\": 1 }`    is parsed As `CaseClass(MemberSome(1))`") {
        _1_normalValue.convertTo[CaseClass] should be(CaseClass(MemberSome(1)))
      }

      it("`{ \"a\": null }` throws Exception") {
        assertThrows[DeserializationException] { _2_nullValue.convertTo[CaseClass] }
      }

      it("`{}`            is parsed As `CaseClass(MemberNone)`") {
        _3_noMember.convertTo[CaseClass] should be(CaseClass(MemberNone))
      }
    }

    describe("with case class `CaseClass(a: MemberOption[Option[Int]])`") {
      case class CaseClass(a: MemberOption[Option[Int]])
      object CaseClass extends MemberPresenceJsonProtocol {
        implicit val caseClassFormat = jsonFormat1(CaseClass.apply)
      }

      it("`{ \"a\": 1 }`    is parsed As `CaseClass(MemberSome(Some(1)))`") {
        _1_normalValue.convertTo[CaseClass] should be(CaseClass(MemberSome(Some(1))))
      }

      it("`{ \"a\": null }` is parsed As `CaseClass(MemberSome(None))`") {
        _2_nullValue.convertTo[CaseClass] should be(CaseClass(MemberSome(None)))
      }

      it("`{}`            is parsed As `CaseClass(MemberNone)`") {
        _3_noMember.convertTo[CaseClass] should be(CaseClass(MemberNone))
      }
    }

  }

}
