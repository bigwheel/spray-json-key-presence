# Object Member Presence Check library for spray-json

## What is this ?

Default JsonReader of spray-json maps both JSON's null value and
no presense of [object member](https://tools.ietf.org/html/rfc8259#section-4) to `None`.
Therefore, we cannot determine null or no presence of key if `None` comes
(See [Sample Code](#Sample Code)!).

This library introduces an type to check member presence and
`None` becomes to be used only for JSON's null.

In another words, this library could be thought as a counterpart of
[NullOptions](https://github.com/spray/spray-json#nulloptions).
NullOptions works in writing json, this library works in reading json too.

## Installation

Available from maven central.

If you use SBT you can include in your project with

```scala
libraryDependencies += "com.github.bigwheel" %% "spray-json-member-presence" % "<any-version>"
```

## Sample Code

```scala
val nullValue = """{ "a": null }""".parseJson
val noKeyPresence = """{}""".parseJson

case class CaseClassA(a: Option[Int])
object CaseClassA extends DefaultJsonProtocol {
  implicit val caseClassAFormat = jsonFormat1(CaseClassA.apply)
}

println(nullValue.convertTo[CaseClassA]) // CaseClassA(None)
println(noKeyPresence.convertTo[CaseClassA]) // CaseClassA(None)
println("Cannot distinguish 😞")

case class CaseClassB(a: KeyPresence[Option[Int]])
object CaseClassB extends KeyPresenceJsonProtocol {
  implicit val caseClassBFormat = jsonFormat1(CaseClassB.apply)
}

println(nullValue.convertTo[CaseClassB]) // CaseClassB(KeyExist(None))
println(noKeyPresence.convertTo[CaseClassB]) // CaseClassB(KeyNotExist)
println("Now we can, yeah ☺️")
```

Complete code is [Main.scala](./src/main/scala/Main.scala).