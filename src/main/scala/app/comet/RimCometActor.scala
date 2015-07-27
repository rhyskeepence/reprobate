package app.comet

import app.ServiceFactory.{systemClock, rimServerActor}
import app.restlike.rim.Persistence
import app.server.ModelChanged
import im.mange.jetboot.comet._
import im.mange.jetboot.page.CometPage
import im.mange.jetboot.{Html, Js, R, Renderable}
import net.liftweb.actor.LiftActor
import net.liftweb.common.Loggable
import net.liftweb.http.S
import net.liftweb.http.js.JsCmd
import net.liftweb.sitemap.Loc

case class AppPage(override val path: String, override val params: Loc.LocParam[Any]*) extends CometPage[AppCometActor]

case class RimPage(override val path: String, override val params: Loc.LocParam[Any]*) extends CometPage[RimCometActor]

case class RimAgent(subscriber: im.mange.jetboot.comet.Subscriber) extends Renderable {
  import Html._

  println("refresh")
  private val params: Map[String, List[String]] = S.request.get.params
  println(s"params: ${params}")

  private val holder = div(Some("rimHolder"), R(s"hello ${systemClock().dateTime}"))

  def render = holder.render

  def onModelChanged(changed: ModelChanged) = holder.fill(present(changed))

  private def present(modelChanged: ModelChanged): R = {
    R(s"update ${systemClock().dateTime} - $modelChanged - $params")
    R(modelChanged.updated.issues.map(i => div(None, R(i.description))))
    //TODO: this is essectially groupByStatus in disguise - we should share it ..
    modelChanged.updated.issues.groupBy(_.status)

    val includeBacklog = true
    val includeReleased = true
    val compressEmptyStates = false
    val hideBy = true
    val hideTags = false
    val aka = None
    val changed = Seq()
    val currentModel = modelChanged.updated
    val model = modelChanged.updated

    val stateToIssues = model.issues.groupBy(_.status.getOrElse("backlog"))
    val interestingStates = (if (includeBacklog) List("backlog") else Nil) ::: currentModel.workflowStates ::: (if (includeReleased) List("released") else Nil)
    val r = interestingStates.map(s => {
      val issuesForState = stateToIssues.getOrElse(s, Nil)
      val issues = issuesForState.map(i => s"\n  ${
        i.render(model, hideStatus = true, hideBy = hideBy, hideTags = hideTags, highlight = changed.contains(i.ref), highlightAka = aka)
      }").mkString
      if (issuesForState.isEmpty && compressEmptyStates) None else Some(s"$s: (${issuesForState.size})" + issues + "\n")
    }).flatten
    R(r)
  }
}

class RimCometActor extends RefreshableCometActor with MessageCapturingCometActor with Subscriber with Loggable {
  override def onCapturedMessage(message: Any, actor: LiftActor) {}

  //TODO: this forces refresh
  this ! ModelChanged(Persistence.load.modelFor("test").get)

  private var rootAgent: RimAgent = _

  def beforeRefresh() {
    println("beforeRefresh")
    //root.cleanup()
    rimServerActor() ! Unsubscribe(this)
  }

  def doRefresh() {
    println("doRefresh")
    rootAgent = new RimAgent(this)
  }

  def afterRefresh(): Unit = {
    println("afterRefresh")
    rimServerActor() ! Subscribe(this)//; this ! Init()
  }

  def doRender = {
    println("doRender")
    rootAgent.render
  }

  override def localShutdown() {
    println("localShutdown")
    //root.cleanup()
    rimServerActor() ! Unsubscribe(this)
    super.localShutdown()
  }

  override def onMessage = handleMessage :: super.onMessage

  //TODO: pull out commands for all these
  private def handleMessage: PartialFunction[Any, Unit] = {
    case m:ModelChanged => println(m); partialUpdate(rootAgent.onModelChanged(m))
    case e => logger.error(s"${getClass.getSimpleName}: unexpected message received: $e")
  }
}