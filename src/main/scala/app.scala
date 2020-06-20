import java.io.File
import java.util

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.{BooleanProperty, IntegerProperty, ObjectProperty, StringProperty}
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control._
import scalafx.scene.layout.StackPane

import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import scala.util.Random

trait ClassRoom {
  private val history = ArrayBuffer[String]()
  protected val students: Array[String]
  protected def chooseSimple:String = {
    if (students.length == 0) throw new IllegalStateException("数组为空")
    students(Random.nextInt(students.length))
  }
  protected def chooseHistory:String = {
    if (students.length == 0) throw new IllegalStateException("数组为空")
    if (history.length == students.length) throw new IllegalStateException("没有新数据")
    val stu = students(Random.nextInt(students.length))
    if (!history.contains(stu)) {
      history.append(stu); stu
    } else chooseHistory
  }
  def reset():Unit = history.clear()
  def status():(Int,Int) = students.length -> history.length
  def choose(useHistory:Boolean):String = if (useHistory) chooseHistory else chooseSimple
}

object SimpleApp extends JFXApp with Controller {
  stage = new PrimaryStage {
    title <== appTitle
    scene = new Scene(400,300) {
      root = new StackPane {
        id = "stackPane"
        children = Seq(new StackPane {
            val label: Label = new Label {
              text <==> nowLabel
              tooltip = "Click to Choose, Drag to add student list"
              styleClass.append("info_label")
              contextMenu = new ContextMenu(
                new MenuItem("Reset Passed People") { onAction = _ => status.set(2) },
                new CheckMenuItem("Use History Mode") { selected <==> useHistory }
              )
            }
            children = Seq(label)
            onMouseEntered = _ =>  label.id = "onMouse_label"
            onMouseExited = _ => label.id = ""
            onMouseClicked = e => {
              if (!e.isPopupTrigger) {
                if (nowLabel() != "???") chooseOne()
                else showAlert(content = "请先添加数据，任意可读的文本文件，按照行分割，行第一个字符为 # 表示忽略此行。")()
              }
            }
            onDragEntered = _ => {
              label.id = "onDragEntered_label"
              nowLabel.set("+")
            }
            onDragOver = e => e.acceptTransferModes(e.getTransferMode)
            onDragDropped = e => {
              val dragboard = e.getDragboard
              if (dragboard.hasFiles) {
                readFiles(dragboard.getFiles) match {
                  case Left(e) => showAlert(content = e) {
                      label.id = ""
                      status.set(0)
                    }
                  case Right(value) =>
                    classRoom.set(value)
                    status.set(1)
                }
              }
              e.setDropCompleted(true)
            }
          })
      }
      onShown = _ => { stylesheets.add("app.css") }
    }
  }
}

trait Controller {
  val classRoom = new ObjectProperty[ClassRoom]()

  val status: IntegerProperty = IntegerProperty(0) //0 NODATA 1 NORMAL 2 CHANGED
  val useHistory: BooleanProperty = BooleanProperty(true)
  
  val nowLabel: StringProperty = StringProperty("???")

  val currentHeader: StringProperty = StringProperty("")
  val appTitle = when(status === 0) choose "拖拽数据开始抽取" otherwise
        (when(status === 2) choose "数据发生变化，点击抽取" otherwise currentHeader)

  useHistory.onChange(status.set(2))
  status.addListener { (_,_,c) =>
    if (c == 2) { if (classRoom.get() != null) classRoom.get().reset() }
    else if (c == 1) { nowLabel.set("开始") }
    else if (c == 0) { nowLabel.set("???") }
  }

  def readFiles(files:util.List[File]): Either[String,ClassRoom] = {
    try {
      val ab = new ArrayBuffer[String]()
      files.forEach { i =>
        val source = Source.fromFile(i,"utf-8")
        source.getLines().toArray.foreach(i => {
          if (!i.trim.startsWith("#") && i.nonEmpty) ab.append(i)
        })
        source.close()
      }
      Right(new ClassRoom {
        override val students: Array[String] = ab.toArray
      })
    } catch {
      case e: Throwable =>
        println(e.getMessage)
        Left(e.getMessage)
    }
  }

  @inline def showAlert(head:String = "注意",content:String = "注意")(op: => Unit): Unit = {
    new Alert(AlertType.Warning) {
      headerText = head
      contentText = content
    }.showAndWait()
    op
  }

  @inline def chooseOne(): Unit = try {
    status.set(1)
    val room = classRoom.get()
    nowLabel.set(room.choose(useHistory()))
    val (all, now) = room.status()
    if (useHistory()) {
       currentHeader.set(s"Passed $now / Total $all [History Mode]")
    } else currentHeader.set(s"Total $all [Simple Mode]")
  } catch {
    case illegalStateException: IllegalStateException =>
      new Alert(AlertType.Error) {
        headerText = "错误"
        contentText = illegalStateException.getMessage + ", 是否重置计数器？"
        buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
      }.showAndWait() match {
        case Some(ButtonType.OK) =>
          classRoom.get().reset(); status.set(2)
        case _ =>
      }
  }
}

