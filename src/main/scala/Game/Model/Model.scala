package Game.Model

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.image.Image
import scalafx.scene.paint.Color

/*****************************
 *    Title: How to Make a 2D Game in Java #1 - The Mechanism of 2D Games
 *    Author: RyiSnow
 *    Date: 13/10/2021
 *    Code version: Java JDK 12
 *    Availability: https://www.youtube.com/watch?v=om59cwR7psI&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq&index=1
 *
 *****************************/

// Class for common functions of the Enemy and Player
abstract class Model(var x: Double, var y: Double, var size: Double, var health: Int) {
  var images: Map[Direction, Image]
  var maxHealth: Int

  var facingDirection: Direction = Right

  def takeDamage(): Unit = health -= 1
  def isAlive: Boolean = health > 0

  // function to determine the image of the Entity if they are going in a specific direction
  def move(dx: Double, dy: Double): Unit = {
    if (dx > 0) facingDirection = Right
    else if (dx < 0) facingDirection = Left
    else if (dy > 0) facingDirection = Down
    else if (dy < 0) facingDirection = Up

    x += dx
    y += dy
  }

  def draw(gc: GraphicsContext, cameraX: Double, cameraY: Double): Unit = {
    val imageToDraw = images(facingDirection)
    gc.drawImage(imageToDraw, x - cameraX, y - cameraY, size, size)
    drawHealthBar(gc, cameraX, cameraY)
  }

  def drawHealthBar(gc: GraphicsContext, cameraX: Double, cameraY: Double): Unit = {
    val barWidth = size
    val barHeight = 5
    val healthBarWidth = (barWidth * health) / maxHealth

    gc.fill = if (this.isInstanceOf[Player]) Color.Red else Color.Green
    gc.fillRect(x - cameraX, y - cameraY - barHeight - 2, healthBarWidth, barHeight)

    gc.stroke = Color.White
    gc.strokeRect(x - cameraX, y - cameraY - barHeight - 2, barWidth, barHeight)
  }
}

trait Direction
case object Right extends Direction
case object Left extends Direction
case object Up extends Direction
case object Down extends Direction

