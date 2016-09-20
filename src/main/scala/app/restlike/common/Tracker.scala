package app.restlike.common

import java.nio.file.Paths

import im.mange.little.file.Filepath
import org.joda.time.DateTime

//TODO: probably what to move this out into rim .. as the 'what' bit is not going to be generic
//actually, can have generic History, but extned/wrap for the rim specific bits
case class History(content: String) {
  private val contentBits = content.split("\\|")
  private val what = contentBits.lift(3)
  private val whatBits = what.map(_.split(" "))

//  try {
    val ref = whatBits.flatMap(_.lift(0))
    val action = whatBits.flatMap(_.lift(1))
    val when = contentBits.lift(1).map(ms => new DateTime(ms.toLong))
    val who = contentBits.lift(2)
    val token = contentBits.lift(0)

    val printable = contentBits.drop(1).mkString("|")
//    println(s"$content - $ref $email $who $action")

//  } catch {
//    case e: Exception => println(s"${e.getMessage} in $content")
//      val ref = ""
//      val email = ""
//  }
}

case class Tracker(filename: String) {
  private val file = Paths.get(filename)

  def track(who: String, what: String, token: String) {
    val content = List(token, DateTime.now.getMillis, who, what).mkString("|") + "\n"
    Filepath.append(content, file)
  }

  def view(token: String) = Filepath.load(file).split("\n").filterNot(_.isEmpty).reverse.map(History(_)).filter(_.token == Some(token)).toSeq
}
