package Game.Model

import Game.Controller.NoteDisplayController
import Game.MainApp
import Game.Util.GameUtils.{InputHandler, Inventory}
import scalafx.scene.Scene
import scalafx.scene.image.Image
import scalafx.scene.input.KeyCode
import scalafx.stage.Stage
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import javafx.{scene => jfxs}
import scalafx.Includes._

// Trait Used to determine the draw distance of the Entities with/without the Flashlight
trait Range {
  var x: Double
  var y: Double
  def isWithinRange(player: Player, flashlight: Flashlight): Boolean = {
    val fieldOfViewRadius = if (flashlight.isPickedUp && flashlight.isActive) flashlight.fieldOfViewRadius else 80
    val distance = Math.sqrt(Math.pow(x - player.x, 2) + Math.pow(y - player.y, 2))
    distance <= fieldOfViewRadius
  }
}

// Trait to determine which items can be picked up and added to the inventory
trait canBePickedUp {
  var x: Double
  var y: Double
  var size: Double
  def pickUp(inventory: Inventory): Unit

  def checkPickup(player: Player, inputHandler: Option[InputHandler], inventory: Inventory): Unit = {
    inputHandler.foreach { ih =>
      val distance = Math.sqrt(Math.pow(x - player.x, 2) + Math.pow(y - player.y, 2))
      if (distance < size && ih.keysPressed.contains(KeyCode.E)) {
        pickUp(inventory)
      }
    }
  }
}

/*****************************
 *    Title: ChatGPT
 *    Author: OpenAI
 *    Date: 22/08/2024
 *    Code version: Scala 2.12.19
 *    Availability: https://chatgpt.com
 *
 *****************************/

// Trait to support the displaying of the Note Stages
trait NoteBase extends Item{
  def x: Double
  def y: Double
  private var noteStage: Option[Stage] = None

  def showDisplay(content: Image): Unit = {
    if (noteStage.isEmpty) {
      val resource = getClass.getResource("/View/NoteDisplay.fxml")
      val loader = new FXMLLoader(resource, NoDependencyResolver)
      val notePane = loader.load[jfxs.layout.VBox]()
      val controller = loader.getController[NoteDisplayController#Controller]
      controller.updateNoteContent(content)

      val stage = new Stage {
        title = "Note"
        scene = new Scene(notePane)
        initOwner(MainApp.stage)
      }
      controller.dialogStage = stage
      stage.setOnCloseRequest(_ => noteStage = None)
      stage.show()
      noteStage = Some(stage)
    }
  }
}