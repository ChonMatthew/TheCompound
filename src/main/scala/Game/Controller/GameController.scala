package Game.Controller

import scalafx.scene.canvas.Canvas
import scalafxml.core.macros.sfxml
import Game.MainApp
import Game.Model.{Bullet, Note}
import Game.Util.{GameState, Renderer}
import Game.Util.GameUtils.{Collision, InputHandler}
import scalafx.animation.AnimationTimer
import scalafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import scalafx.scene.layout.BorderPane
import scalafx.Includes._
import scalafx.scene.media.{Media, MediaPlayer}

@sfxml
class GameController(private val gameCanvas: Canvas) {

  def getCanvas: Canvas = gameCanvas
}

// The entire GameLoop logic that allows checks for the GameState and Renderer
class GameLoop(val canvas: Canvas, onGameEnd: () => Unit) {
  private val graphics = canvas.graphicsContext2D
  val gameState = new GameState(graphics)
  private val renderer = new Renderer(canvas, graphics)
  private var cameraX: Double = 0
  private var cameraY: Double = 0

  // input handler conditions
  private var wasIKeyPressed: Boolean = false
  private var wasFKeyPressed: Boolean = false
  private var wasEscKeyPressed: Boolean = false

  // condition to check for the running game
  private var timer: Option[AnimationTimer] = None

  // Initializing the audio effects
  private val gunShotEffectAudio: Media = new Media(getClass.getResource("/Assets/Audios/gunshot.mp3").toString)
  private val walkingEffectAudio: Media = new Media(getClass.getResource("/Assets/Audios/footstep.mp3").toString)
  private val ambientSound: Media = new Media(getClass.getResource("/Assets/Audios/ambience.mp3").toString)
  private var walkingMediaPlayer: Option[MediaPlayer] = None
  private var ambientSoundPlayer: MediaPlayer = _

  val notes: List[Note] = gameState.notes

  private def setInputHandler(handler: InputHandler): Unit = {
    gameState.inputHandler = Some(handler)
    val gameScene = MainApp.gameScene

    gameScene.setOnKeyPressed((event: KeyEvent) => handler.handleKeyPressed(event))
    gameScene.setOnKeyReleased((event: KeyEvent) => handler.handleKeyReleased(event))
    gameScene.setOnMouseMoved((event: MouseEvent) => handler.handleMouseMoved(event))
    gameScene.setOnMouseClicked(_ => shoot())
  }

  private def setupScene(): Unit = {
    val inputHandler = new InputHandler()
    setInputHandler(inputHandler)

    val gamePane = new BorderPane {
      center = canvas }
    MainApp.gameScene.setRoot(gamePane)
    canvas.requestFocus()
  }

  /*****************************
   *    Title: Animations and the AnimationTimer (in ScalaFX)
   *    Author: Mark Lewis
   *    Date: 04/11/2015
   *    Code version: Scala 2
   *    Availability: https://www.youtube.com/watch?v=zojzE67cjj8
   *
   *****************************/

  private def initializeGameLoop(): Unit = {
    timer = Some(AnimationTimer(_ => {
      update()
      draw()
    }))
    timer.foreach(_.start())
  }

  def draw(): Unit = {
    renderer.draw(gameState, cameraX, cameraY)
  }

  def startGame(): Unit = {
    ambientSoundPlayer = new MediaPlayer(ambientSound) {
      autoPlay = true
      cycleCount = MediaPlayer.Indefinite
    }

    setupScene()
    initializeGameLoop()
  }

  private def stopGame(): Unit = {
    timer.foreach(_.stop())
    ambientSoundPlayer.stop()
    MainApp.switchToOriginalScene()
  }

  private def die(): Unit = {
    timer.foreach(_.stop())
    ambientSoundPlayer.stop()
    MainApp.switchToDeath()
  }

  // The GameLoop logic
  def update(): Unit = {
    canvas.onMouseClicked = (event: MouseEvent) => {
      handleMouseClick(event)
    }
    handlePlayerMovement()
    updateCamera()
    handlePickups()
    handleToggles()
    gameState.bulletManager.update()
    gameState.enemyManager.update()
    checkCollisions()

    if (!gameState.player.isAlive) {
      die()
    }

    gameState.buttonItem.checkInteraction(gameState.player.x, gameState.player.y, gameState.inventory, gameState.inputHandler)
    if (gameState.buttonItem.isActive) {
      stopGame()
      onGameEnd()
    }
  }

  // Function to draw bullets once the Player "shoots"
  def shoot(): Unit = {
    if (
      gameState.inventory.getBulletCount > 0 &&
        !gameState.inventory.isVisible &&
        !gameState.pauseMenu.isVisible
    ) {
      gameState.gun.shoot(gameState.player, gameState.bulletManager, gameState.inputHandler, cameraX, cameraY)
      gameState.inventory.decrementBulletCount()

      new MediaPlayer(gunShotEffectAudio) {
        autoPlay = true
        cycleCount = 1
      }
    }
  }

  private def handlePlayerMovement(): Unit = {
    if (gameState.inventory.isVisible || gameState.pauseMenu.isVisible) return

    gameState.inputHandler.foreach { ih =>
      val (dx, dy) = calculateMovement(ih)

      if (dx != 0 || dy != 0) {
        if (walkingMediaPlayer.isEmpty || walkingMediaPlayer.exists(_.status.value == MediaPlayer.Status.Stopped)) {
          val newMediaPlayer = new MediaPlayer(walkingEffectAudio) {
            autoPlay = true
            cycleCount = 1
            onEndOfMedia = () => {
              walkingMediaPlayer = None
            }
          }
          walkingMediaPlayer = Some(newMediaPlayer)
        }
      }

      movePlayerIfNoCollision(dx, dy)
    }
  }

  private def calculateMovement(ih: InputHandler): (Double, Double) = {
    val playerSpeed = 2
    var dx = 0.0
    var dy = 0.0

    if (ih.isKeyPressed(KeyCode.W)) dy -= playerSpeed
    if (ih.isKeyPressed(KeyCode.A)) dx -= playerSpeed
    if (ih.isKeyPressed(KeyCode.S)) dy += playerSpeed
    if (ih.isKeyPressed(KeyCode.D)) dx += playerSpeed

    (dx, dy)
  }

  private def movePlayerIfNoCollision(dx: Double, dy: Double): Unit = {
    val newX = gameState.player.x + dx
    val newY = gameState.player.y + dy

    if (!Collision.isCollidingWithWall(newX, newY, gameState.player.size, gameState.walls)) {
      gameState.player.move(dx, dy)
    }
  }

  private def handlePickups(): Unit = {
    gameState.flashlight.checkPickup(gameState.player, gameState.inputHandler, gameState.inventory)
    gameState.gun.checkPickup(gameState.player, gameState.inputHandler, gameState.inventory)
    gameState.key.checkPickup(gameState.player, gameState.inputHandler, gameState.inventory)
    notes.foreach(_.checkPickup(gameState.player, gameState.inputHandler, gameState.inventory))
    checkItemPickup()
  }

  private def handleToggles(): Unit = {
    gameState.inputHandler.foreach { ih =>
      val isIKeyPressed = ih.isKeyPressed(KeyCode.I)
      if (isIKeyPressed && !wasIKeyPressed && !gameState.pauseMenu.isVisible) {
        gameState.inventory.toggleInventory()
      }
      wasIKeyPressed = isIKeyPressed
      val isFKeyPressed = ih.isKeyPressed(KeyCode.F)
      if (isFKeyPressed && !wasFKeyPressed) {
        gameState.flashlight.toggleFlashlight()
      }
      wasFKeyPressed = isFKeyPressed
      val isEscKeyPressed = ih.isKeyPressed(KeyCode.Escape)

      if (isEscKeyPressed && !wasEscKeyPressed) {
        if (!gameState.pauseMenu.isInstructionsVisible) {
          gameState.pauseMenu.toggleMenu()
        }
        ih.clearKeys()
      }
      wasEscKeyPressed = isEscKeyPressed
    }
  }

  private def handleMouseClick(event: MouseEvent): Unit = {
    if (gameState.pauseMenu.isVisible) {
      gameState.pauseMenu.handleClick(event, canvas.width.value, canvas.height.value)
    }
  }

  private def checkCollisions(): Unit = {
    gameState.bulletManager.getBullets.foreach { bullet =>
      if (!Collision.isCollidingWithWall(bullet.x, bullet.y, 5, gameState.walls)) {
        Collision.isCollidingWithEnemy(bullet, gameState.enemyManager.getEnemies).foreach { enemy =>
          enemy.takeDamage()
          bullet.x = -100
          bullet.y = -100
        }
      }else {
        bullet.x = -100
        bullet.y = -100
      }
    }
  }

  private def checkItemPickup(): Unit = {
    gameState.inputHandler.foreach { ih =>
      gameState.bulletItems.filter { bulletItem =>
        val distance = Math.sqrt(Math.pow(bulletItem.x - gameState.player.x, 2) + Math.pow(bulletItem.y - gameState.player.y, 2))
        distance < 40
      }.foreach { bulletItem =>
        if (ih.keysPressed.contains(KeyCode.E)) {
          gameState.inventory.addBullet()
          gameState.bulletItems -= bulletItem
        }
      }
    }
  }

  /*****************************
   *    Title: World and Camera - How to Make a 2D Game in Java #5
   *    Author: RyiSnow
   *    Date: 21/10/2021 Oct 21, 2021
   *    Code version: JavaFX
   *    Availability: https://www.youtube.com/watch?v=Ny_YHoTYcxo&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq&index=7
   *
   *****************************/

  private def updateCamera(): Unit = {
    val mapWidth = 1000
    val mapHeight = 700

    cameraX = Math.max(0, Math.min(gameState.player.x - canvas.width.value / 2, mapWidth - canvas.width.value))
    cameraY = Math.max(0, Math.min(gameState.player.y - canvas.height.value / 2, mapHeight - canvas.height.value))
  }
}

