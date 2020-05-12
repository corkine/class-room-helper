import org.scalatest.Matchers
import org.scalatest.FlatSpec

class ClassRoomTest extends FlatSpec {
  "Empty Student list" should "work well" in new ClassRoom {
    override val students: Array[String] = Array()
    reset()
    assertThrows[IllegalStateException] {
      choose(false)
    }
    assertThrows[IllegalStateException] {
      choose(true)
    }
  }
  "One Student Class" should "work well in non history" in  new ClassRoom {
    override val students: Array[String] = Array("Marvin")
    (1 to 100) foreach { _ =>
      assert(choose(false) == "Marvin")
    }
  }
  it should "work well in history model" in  new ClassRoom {
    override val students: Array[String] = Array("Marvin")
    assert(choose(true) == "Marvin")
    (1 to 100) foreach { _ =>
      assertThrows[IllegalStateException] {
        choose(true)
      }
    }
  }
  it should "work well in history model with reset" in  new ClassRoom {
    override val students: Array[String] = Array("Marvin")
    assert(choose(true) == "Marvin")
    (1 to 100) foreach { _ =>
      reset()
      assert {
        choose(true) == "Marvin"
      }
    }
  }
  "Normal Student Class" should "work well in non history" in new ClassRoom {
    override val students: Array[String] = (0 to 100).map(i => s"Student$i").toArray
    (1 to 100) foreach { _ =>
      assert(choose(false).nonEmpty)
    }
  }
  it should "work well in history mode" in new ClassRoom {
    override val students: Array[String] = (0 to 100).map(i => s"Student$i").toArray
    val res: Seq[String] = (1 to 99) map { _ => choose(true) }
    assert(res.size == res.distinct.size)
    assert(res.size < students.length)
  }
}