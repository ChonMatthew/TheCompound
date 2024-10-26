package Game

import Game.Controller.{GameController, GameLoop, RootLayoutController}
import Game.Util.GameUtils.InputHandler
import javafx.{scene => jfxs}
import scalafx.Includes._
import scalafx.animation.PauseTransition
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{JFXApp, Platform}
import scalafx.scene.Scene
import scalafx.scene.input.{KeyEvent, MouseEvent}
import scalafx.scene.layout.BorderPane
import scalafx.scene.media.{Media, MediaPlayer}
import scalafx.util.Duration
import scalafxml.core.{FXMLLoader, NoDependencyResolver}

import java.net.URL

object MainApp extends JFXApp {

  /*****************************
   *    Title:  Practical 4: ScalaFX Address App
   *    Author: Dr.Chin Teck Min
   *    Date: 20/08/2024
   *    Code version: Scala 2.12.19
   *    Availability: Practical Document
   *
   *****************************/

  // Initialize root items
  private val rootResource: URL = getClass.getResource("/View/RootLayout.fxml")
  private val loader: FXMLLoader = new FXMLLoader(rootResource, NoDependencyResolver)
  private val roots: BorderPane = loader.load[jfxs.layout.BorderPane]()
  private val control = loader.getController[RootLayoutController#Controller]

  // Initialize inputHandler for the gameScene
  private val inputHandler = new InputHandler

  // Death Sound Effect for the Death Screen
  private val deathSound: Media = new Media(getClass.getResource("/Assets/Audios/death.mp3").toString)

  // Initialize 2 separate Scenes for Game and Main Menu
  private var originalScene: Scene = _
  var gameScene: Scene = _

  // Initialize Stage for all elements
  stage = new PrimaryStage {
    title = "The Compound"
    minWidth = 1000
    minHeight = 700
    originalScene = new Scene(roots)
    scene = originalScene
  }

  // Function to load a pane to be used
  private def loadPane(fxmlPath: String): jfxs.layout.Pane = {
    val resource: URL = getClass.getResource(fxmlPath)
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load[jfxs.layout.Pane]()
  }

  // Function to replace the current pane with a new pane
  private def updateCenterPane(newPane: jfxs.layout.Pane): Unit = {
    Platform.runLater {
      roots.setCenter(newPane)
    }
  }

  private def pauseTransition(callback: () => Unit): Unit = {
    val pauseTransition = new PauseTransition(Duration(5000)) {
      onFinished = _ => {
        Platform.runLater {
          callback()
        }
      }
    }
    pauseTransition.play()
  }

  //==========================
  // Various Panes
  //==========================

  private def showCongratulations(callback: () => Unit): Unit = {
    val congratulationsPane = loadPane("/View/Congratulations.fxml").asInstanceOf[jfxs.layout.BorderPane]
    updateCenterPane(congratulationsPane)

    pauseTransition(callback: () => Unit)
  }

  private def showDeath(callback: () => Unit): Unit = {
    val deathPane = loadPane("/View/Death.fxml").asInstanceOf[jfxs.layout.BorderPane]
    updateCenterPane(deathPane)

    // Plays the sound effect once this pane is called
    new MediaPlayer(deathSound) {
      autoPlay = true
      cycleCount = 1
    }

    pauseTransition(callback: () => Unit)
  }

  def showHome(): Unit = {
    val homePane = loadPane("/View/Home.fxml").asInstanceOf[jfxs.layout.AnchorPane]
    updateCenterPane(homePane)
    control.showMenuBar()
  }

  def showHelp(): Unit = {
    val helpPane = loadPane("/View/Help.fxml").asInstanceOf[jfxs.layout.AnchorPane]
    updateCenterPane(helpPane) // Update center pane without changing the root
    control.showMenuBar()
  }

  def showGame(): Unit = {
    println("Show")
    control.removeMenuBar()

    // Initialize a new Pane and Canvas for the displaying of the game
    val gamePane = loadPane("/View/Game.fxml").asInstanceOf[jfxs.layout.BorderPane]
    val gameLoader = new FXMLLoader(getClass.getResource("/View/Game.fxml"), NoDependencyResolver)
    gameLoader.load()
    val controller = gameLoader.getController[GameController#Controller]
    val gameCanvas = controller.getCanvas

    // Set up the game loop with a callback for when the game ends
    val gameLoop = new GameLoop(gameCanvas, () => {
      Platform.runLater {
        switchToOriginalScene()  // Switch back to the original scene when the game ends
      }
    })

    // Create and set the game scene
    gameScene = new Scene(gamePane) {
      onKeyPressed = (event: KeyEvent) => inputHandler.handleKeyPressed(event)
      onKeyReleased = (event: KeyEvent) => inputHandler.handleKeyReleased(event)
      onMouseMoved = (event: MouseEvent) => inputHandler.handleMouseMoved(event)
      onMouseClicked = () => gameLoop.shoot()
    }

    // Switch to the game scene from the original scene set in the stage
    stage.scene = gameScene

    // Initialize and start the game loop
    gameLoop.startGame()
  }

  def switchToOriginalScene(): Unit = {
    stage.scene = originalScene
    showCongratulations(() => {
        showHome()
    })
  }

  def switchToDeath(): Unit = {
    stage.scene = originalScene
    showDeath(() => {
        showHome()
    })
  }

  showHome()
}
