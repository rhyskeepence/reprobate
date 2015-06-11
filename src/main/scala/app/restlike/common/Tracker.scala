package app.restlike.common

import java.nio.file.Paths

import im.mange.little.file.Filepath
import org.joda.time.DateTime

case class Tracker(filename: String) {
  private val file = Paths.get(filename)

  def track(who: String, what: String, email: String) {
    val content = List(DateTime.now, who, what, email).mkString("|") + "\n"
    Filepath.append(content, file)
  }

  def view = Filepath.load(file).split("\n").reverse.toList
}
