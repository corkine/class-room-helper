import java.io.File
import java.nio.file.Files
import java.util

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.{BooleanProperty, ObjectProperty}
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, ButtonType, CheckMenuItem, ContextMenu, Label, Menu, MenuItem}
import scalafx.scene.layout.StackPane

import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import scala.util.Random

trait ClassRoom {
  private val history = ArrayBuffer[String]()
  val students: Array[String]
  def reset():Unit = history.clear()
  def chooseSimple:String = {
    if (students.length == 0) throw new IllegalStateException("数组为空")
    students(Random.nextInt(students.length))
  }
  def status():(Int,Int) = {
    students.length -> history.length
  }
  def chooseHistory:String = {
    if (students.length == 0) throw new IllegalStateException("数组为空")
    if (history.length == students.length) throw new IllegalStateException("没有新数据")
    val stu = students(Random.nextInt(students.length))
    if (!history.contains(stu)) {
      history.append(stu); stu
    } else chooseHistory
  }
  def choose(useHistory:Boolean):String = if (useHistory) chooseHistory else chooseSimple
}

object SimpleApp extends JFXApp with Controller {
  val useHistory: BooleanProperty = BooleanProperty(true)
  val classRoom = new ObjectProperty[ClassRoom]()
  lazy val label: Label = new Label("???") {
    tooltip = "Click to Choose, Drag to add student list"
    styleClass.append("info_label")
    contextMenu = new ContextMenu(
      new MenuItem("Reset Passed People") {
        onAction = _ => {
          stage.title = "数据发生变化，点击重新抽取"
          classRoom.get().reset()
        }
      },
      new CheckMenuItem("Use History Mode") {
        selected <==> useHistory
      }
    )
  }
  stage = new PrimaryStage { st =>
    scene = new Scene(400,300) {
      root = new StackPane {
        id = "stackPane"
        children = Seq(new StackPane {
            children = Seq(label)
            onMouseEntered = _ =>  label.id = "onMouse_label"
            onMouseExited = _ => label.id = ""
            onMouseClicked = e => {
              println("C1")
              if (!e.isPopupTrigger) {
                if (label.text() != "???") chooseOne(st) else
                  new Alert(AlertType.Warning) {
                    headerText = "注意"
                    contentText = "请先添加数据，任意可读的文本文件，按照行分割，行第一个字符为 # 表示忽略此行。"
                  }.showAndWait()
              }
            }
            onDragEntered = _ => {
              label.id = "onDragEntered_label"
              label.text = "+"
            }
            onDragOver = e => e.acceptTransferModes(e.getTransferMode)
            onDragDropped = e => {
              val dragboard = e.getDragboard
              if (dragboard.hasFiles) {
                readFiles(dragboard.getFiles) match {
                  case Left(e) =>
                    new Alert(AlertType.Error) {
                      headerText = "注意"
                      contentText = e
                    }.showAndWait()
                    label.id = ""
                    label.text = "???"
                  case Right(value) =>
                    classRoom.set(value)
                    label.text = "开始"
                    st.title = "数据准备就绪"
                }
              }
              e.setDropCompleted(true)
            }
          })
      }
      onShown = _ => {
        stylesheets.add("app.css")
        stage.title = "拖拽数据到面板以开始抽取"
        useHistory.onChange {
          stage.title = "模式发生变化，点击重新抽取"
        }
      }
    }
  }
}

trait Controller {
  val label: Label
  val useHistory: BooleanProperty
  val classRoom: ObjectProperty[ClassRoom]
  def chooseOne(stage:PrimaryStage): Unit = try {
    val room = classRoom.get()
    label.text = room.choose(useHistory())
    val (all, now) = room.status()
    if (useHistory()) {
      stage.title = s"Passed $now / Total $all [History Mode]"
    } else stage.title = s"Total $all [Simple Mode]"
  } catch {
    case illegalStateException: IllegalStateException =>
      new Alert(AlertType.Error) {
        headerText = "错误"
        contentText = illegalStateException.getMessage + ", 是否重置计数器？"
        buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
      }.showAndWait() match {
        case Some(ButtonType.OK) =>
          stage.title = "数据发生变化，点击重新抽取"
          classRoom.get().reset();
        case _ =>
      }
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
}
