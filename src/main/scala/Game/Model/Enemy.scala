package Game.Model

import Game.Util.GameUtils.{Collision, Movement}
import scalafx.scene.image.Image
import scala.collection.mutable.ListBuffer

case class Enemy(_x: Double, _y: Double, _size: Double, _health: Int = 4, trackingRange: Double = 100) extends Model(_x, _y, _size, _health) with Range {
  override var images: Map[Direction, Image] = Map(
    Right -> new Image("file:src/main/resources/Assets/Models/enemy_right.png"),
    Left  -> new Image("file:src/main/resources/Assets/Models/enemy_left.png"),
    Up    -> new Image("file:src/main/resources/Assets/Models/enemy_up.png"),
    Down  -> new Image("file:src/main/resources/Assets/Models/enemy_down.png")
  )

  override var maxHealth: Int = 4

  private var isTrackingPlayer: Boolean = false

  // movement logic for tracking the player
  def moveTowards(targetX: Double, targetY: Double, speed: Double, walls: ListBuffer[Wall]): Unit = {
    if (!isTrackingPlayer && isWithinTrackingRange(targetX, targetY)) {
      isTrackingPlayer = true
    }

    if (isTrackingPlayer) {
      val (moveX, moveY) = Movement.moveTowards(x, y, targetX, targetY, speed)
      if (moveX > 0) facingDirection = Right
      else if (moveX < 0) facingDirection = Left
      else if (moveY > 0) facingDirection = Down
      else if (moveY < 0) facingDirection = Up

      tryMove(moveX, moveY, walls)
    }
  }

  // movement logic for collision with walls
  private def tryMove(moveX: Double, moveY: Double, walls: ListBuffer[Wall]): Unit = {
    var newX = x + moveX
    var newY = y + moveY

    if (!Collision.isCollidingWithWalls(newX, newY, size, size, walls)) {
      x = newX
      y = newY
    }
  }

  private def isWithinTrackingRange(targetX: Double, targetY: Double): Boolean = {
    val distance = Math.sqrt(Math.pow(x - targetX, 2) + Math.pow(y - targetY, 2))
    distance <= trackingRange
  }
}

