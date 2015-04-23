import app.restlike.rim._
import org.scalatest.{MustMatchers, WordSpec}

class RimCommandSpec extends WordSpec with MustMatchers {

//TODO: start with happy path
//  case In(Some(""), Nil) => onShowBoard(currentModel)
//  case In(Some("help"), Nil) => onHelp(who, currentModel)
//  case In(Some("?"), Nil) => onQueryIssues(currentModel, None)
//  case In(Some("?"), List(query)) => onQueryIssues(currentModel, Some(query))
//  case In(Some(ref), List("-")) => onRemoveIssue(ref, currentModel)
//  case In(Some(ref), args) if args.nonEmpty && args.head == "=" => onEditIssue(ref, args.drop(1), currentModel)
//  case In(Some(ref), List("/")) => onForwardIssue(who, ref, currentModel)
//  case In(Some(ref), List("/!")) => onFastForwardIssue(who, ref, currentModel)
//  case In(Some(ref), List(".")) => onBackwardIssue(who, ref, currentModel)
//  case In(Some(ref), List(".!")) => onFastBackwardIssue(who, ref, currentModel)
//  case In(Some(ref), List("@")) => onOwnIssue(who, ref, currentModel)
//  case In(Some(ref), args) if args.nonEmpty && args.size > 1 && args.head == ":" => onTagIssue(ref, args.drop(1), currentModel)
//  case In(Some(ref), args) if args.nonEmpty && args.size > 1 && args.head == ":-" => onDetagIssue(ref, args.drop(1), currentModel)
//  case In(Some("release"), List(tag)) => onRelease(tag, currentModel)
//  case In(Some("releases"), Nil) => onShowReleases(currentModel)
//  case In(head, tail) => onUnknownCommand(head, tail)

  private val next = "next"
  private val doing = "doing"
  private val done = "done"
  private val workflowStates = List(next, doing, done)
  private val aka = "A"
  private val aka2 = "B"
  private val usersToAka = Map("anon" -> aka, "anon2" -> aka2)
  private val emptyModelWithWorkflow = Model(workflowStates, usersToAka, Nil, Nil)

  "set aka" in {
    val current = Model(Nil, Map("anon2" -> aka2), Nil, Nil)
    val expected = current.copy(userToAka = usersToAka)
    runAndExpect("aka a", current, expected)
  }

  //adding

  "add issue" in {
    val current = emptyModelWithWorkflow
    val expected = current.copy(issues = List(Issue("1", "an item", None, None)))
    runAndExpect("+ an item", current, expected)
  }

  "add issue (ignoring surplus noise)" in {
    val current = emptyModelWithWorkflow
    val expected = current.copy(issues = List(Issue("1", "an item", None, None)))
    runAndExpect("+ an   item  ", current, expected)
  }

  //TODO: by should be None
  "add and move forward to begin state" in {
    val current = emptyModelWithWorkflow
    val expected = current.copy(issues = List(Issue("1", "an item", Some(next), Some(aka))))
    runAndExpect("+/ an item", current, expected)
  }

  "add and move forward to end state" in {
    val current = emptyModelWithWorkflow
    val expected = current.copy(issues = List(Issue("1", "an item", Some(done), Some(aka))))
    runAndExpect("+/! an item", current, expected)
  }

  "add with tags" in {
    val current = emptyModelWithWorkflow
    val expected = current.copy(issues = List(Issue("1", "an item", None, None, Set("tag1", "tag2"))))
    runAndExpect("+ an item : tag1 tag2", current, expected)
  }

  //TODO: by should be None
  "add and move forward to begin state with tags" in {
    val current = emptyModelWithWorkflow
    val expected = current.copy(issues = List(Issue("1", "an item", Some(next), Some(aka), Set("tag1", "tag2"))))
    runAndExpect("+/ an item : tag1 tag2", current, expected)
  }

  "add and move forward to end state with tags" in {
    val current = emptyModelWithWorkflow
    val expected = current.copy(issues = List(Issue("1", "an item", Some(done), Some(aka), Set("tag1", "tag2"))))
    runAndExpect("+/! an item : tag1 tag2", current, expected)
  }

  //moving

  "move forward one state" in {
    val issue = Issue("1", "an item", Some(doing), None)
    val current = modelWithIssue(issue)
    val expected = current.copy(issues = List(issue.copy(status = Some(done), by = Some(aka))))
    runAndExpect("1 /", current, expected)
  }

  //TODO: by should be None
  "move forward one state (from backlog to begin)" in {
    val issue = Issue("1", "an item", None, None)
    val current = modelWithIssue(issue)
    val expected = current.copy(issues = List(issue.copy(status = Some(next), by = Some(aka))))
    runAndExpect("1 /", current, expected)
  }

  "move forward two states" in {
    (pending)
    val issue = Issue("1", "an item", None, None)
    val current = modelWithIssue(issue)
    val expected = current.copy(issues = List(issue.copy(status = Some(doing), by = Some(aka))))
    runAndExpect("1 //", current, expected)
  }

  "move forward to end state" in {
    val issue = Issue("1", "an item", None, None)
    val current = modelWithIssue(issue)
    val expected = current.copy(issues = List(issue.copy(status = Some(done), by = Some(aka))))
    runAndExpect("1 /!", current, expected)
  }

  "move back a state" in {
    val issue = Issue("1", "an item", Some(doing), None)
    val current = modelWithIssue(issue)
    val expected = current.copy(issues = List(issue.copy(status = Some(next), by = Some(aka))))
    runAndExpect("1 .", current, expected)
  }

  //TODO: by should be None
  "move back a state (into backlog)" in {
    val issue = Issue("1", "an item", Some(next), None)
    val current = modelWithIssue(issue)
    val expected = current.copy(issues = List(issue.copy(status = None, by = Some(aka))))
    runAndExpect("1 .", current, expected)
  }

  "move back to begin state (into backlog)" in {
    val issue = Issue("1", "an item", Some(done), None)
    val current = modelWithIssue(issue)
    val expected = current.copy(issues = List(issue.copy(status = None, by = None)))
    runAndExpect("1 .!", current, expected)
  }

  // owning

  "own" in {
    val issue = Issue("1", "an item", Some(next), None)
    val current = modelWithIssue(issue)
    val expected = current.copy(issues = List(issue.copy(by = Some(aka))))
    runAndExpect("1 @", current, expected)
  }

  "disown" in {
    val issue = Issue("1", "an item", Some(next), Some(aka))
    val current = modelWithIssue(issue)
    val expected = current.copy(issues = List(issue.copy(by = None)))
    runAndExpect("1 @-", current, expected)
  }

  "assign" in {
    val issue = Issue("1", "an item", Some(next), Some(aka))
    val current = modelWithIssue(issue)
    val expected = current.copy(issues = List(issue.copy(by = Some(aka2))))
    runAndExpect("1 @= b", current, expected)
  }

  "assign (invalid aka)" in {
    (pending) //TODO: TODO: need to start asserting the Out().messages
    val issue = Issue("1", "an item", Some(next), Some(aka))
    val current = modelWithIssue(issue)
    val expected = current.copy(issues = List(issue.copy(by = Some(aka2))))
    runAndExpect("1 @= c", current, expected)
  }

  // show

  "show board" in {
    (pending) //TODO: need to start asserting the Out().messages
    val issue = Issue("1", "an item", Some(done), None)
    val current = modelWithIssue(issue)
    val expected = current.copy(issues = List(issue.copy(status = None, by = None)))
    runAndExpect("", current, expected)
  }


  private def runAndExpect(in: String, current: Model, expected: Model) {
    run(s"$in", current).updatedModel.mustEqual(Some(expected))
  }

  private def run(in: String, current: Model) = Commander.process(in, "anon", current, RefProvider(0))

  private def modelWithIssue(issue: Issue) = Model(workflowStates, usersToAka, List(issue), Nil)
}
