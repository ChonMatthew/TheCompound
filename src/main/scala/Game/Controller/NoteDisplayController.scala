package Game.Controller

import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.{Image, ImageView}
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml

@sfxml
class NoteDisplayController(val noteContent: ImageView, val closeButton: Button) {
  var dialogStage: Stage = _

  def updateNoteContent(content: Image): Unit = {
    noteContent.image = content
  }

  def handleCloseButton(event: ActionEvent): Unit = {
    if (dialogStage != null) {
      dialogStage.close() // Close the Note stage
    }
  }
}
