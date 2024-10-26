package Game.Util

import Game.Model.{Player, Wall}
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.image.Image
import scalafx.scene.paint.Color

// Class to render/draw all the elements of the GateState
class Renderer(val canvas: Canvas, val graphics: GraphicsContext) {
  private val backgroundImage: Image = new Image("file:src/main/resources/Assets/Background/Floor.png")
  private val imageWidth: Double = backgroundImage.width.value
  private val imageHeight: Double = backgroundImage.height.value
  private val lineLength: Double = 100

  // Drawing all elements
  def draw(gameState: GameState, cameraX: Double, cameraY: Double): Unit = {
    clearCanvas(cameraX, cameraY)
    drawGameElements(gameState, cameraX, cameraY)
    drawDarkOverlay(gameState.player, cameraX, cameraY, gameState)
    gameState.inventory.draw(graphics, canvas.width.value, canvas.height.value)
    gameState.pauseMenu.draw(graphics, canvas.width.value, canvas.height.value)
    gameState.pauseMenu.drawInstructions(graphics, canvas.width.value, canvas.height.value)
  }

  // Function to clear the entire canvas with a black screen (reset the canvas)
  private def clearCanvas(cameraX: Double, cameraY: Double): Unit = {
    val cols = Math.ceil(canvas.width.value / imageWidth).toInt
    val rows = Math.ceil(canvas.height.value / imageHeight).toInt

    graphics.fill = Color.Black
    graphics.fillRect(0, 0, canvas.width.value, canvas.height.value)

    // Applies the background image to the canvas
    for (row <- 0 until rows; col <- 0 until cols) {
      val drawX = col * imageWidth - cameraX
      val drawY = row * imageHeight - cameraY
      if (drawX + imageWidth > -cameraX && drawY + imageHeight > -cameraY && drawX < canvas.width.value - cameraX && drawY < canvas.height.value - cameraY) {
        graphics.drawImage(backgroundImage, drawX, drawY)
      }
    }
  }

  private def drawGameElements(gameState: GameState, cameraX: Double, cameraY: Double): Unit = {
    drawWalls(gameState.walls, cameraX, cameraY)
    drawPlayerAndItems(gameState, cameraX, cameraY)
    drawBulletsAndEnemies(gameState, cameraX, cameraY)
  }

  private def drawPlayerAndItems(gameState: GameState, cameraX: Double, cameraY: Double): Unit = {
    drawTrajectoryLine(gameState, cameraX, cameraY)
    gameState.player.draw(graphics, cameraX, cameraY)
    gameState.gun.draw(graphics, cameraX, cameraY, gameState.player, gameState.flashlight)
    if (gameState.inventory.isNote3PickedUp) {
      gameState.key.draw(graphics, cameraX, cameraY, gameState.player, gameState.flashlight)
    }
    gameState.buttonItem.draw(graphics, cameraX, cameraY)
    gameState.bulletItems.foreach(_.draw(graphics, cameraX, cameraY, gameState.player, gameState.flashlight))
    gameState.flashlight.draw(graphics, cameraX, cameraY, gameState.player, gameState.flashlight)
    gameState.notes.foreach(_.draw(graphics, cameraX, cameraY, gameState.player, gameState.flashlight))
  }

  private def drawBulletsAndEnemies(gameState: GameState, cameraX: Double, cameraY: Double): Unit = {
    gameState.bulletManager.draw(cameraX, cameraY)
    gameState.enemyManager.draw(graphics, cameraX, cameraY, gameState.player, gameState.flashlight)
  }

  /*****************************
   *    Title: ChatGPT
   *    Author: OpenAI
   *    Date: 22/08/2024
   *    Code version: Scala 2.12.19
   *    Availability: https://chatgpt.com
   *
   *****************************/

  private def drawDarkOverlay(player: Player, cameraX: Double, cameraY: Double, gameState: GameState): Unit = {
    val baseRadius = if (gameState.flashlight.isActive) gameState.flashlight.fieldOfViewRadius else 80.0
    val centerX = player.x - cameraX + player.size / 2
    val centerY = player.y - cameraY + player.size / 2

    graphics.save()

    // Fill the entire canvas with a lowered opacity fill
    graphics.fill = Color.rgb(0, 0, 0, 0.7)
    graphics.fillRect(0, 0, canvas.width.value, canvas.height.value)

    // Create the circular area
    graphics.beginPath()
    graphics.arc(centerX, centerY, baseRadius, baseRadius, 0, 360)
    graphics.closePath()

    // Clip through the lowered opacity with the circular area
    graphics.clip()

    // Draw the elements again inside the circular area so that it is not lowered opacity
    clearCanvas(cameraX, cameraY)
    drawGameElements(gameState, cameraX, cameraY)

    graphics.restore()
  }

  private def drawWalls(walls: Seq[Wall], cameraX: Double, cameraY: Double): Unit = {
    walls.foreach(_.draw(graphics, cameraX, cameraY))
  }

  // Draw a trajectory line coming out the Player model
  private def drawTrajectoryLine(gameState: GameState, cameraX: Double, cameraY: Double): Unit = {
    gameState.inputHandler.foreach { ih =>
      val angle = Math.atan2(ih.mouseY - (gameState.player.y - cameraY), ih.mouseX - (gameState.player.x - cameraX))
      val lineStartX = gameState.player.x - cameraX + gameState.player.size / 2
      val lineStartY = gameState.player.y - cameraY + gameState.player.size / 2
      val lineEndX = lineStartX + lineLength * Math.cos(angle)
      val lineEndY = lineStartY + lineLength * Math.sin(angle)
      graphics.stroke = Color.White
      graphics.strokeLine(lineStartX, lineStartY, lineEndX, lineEndY)
    }
  }
}

