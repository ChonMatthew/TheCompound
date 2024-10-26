package Game.Model

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

case class Wall(x: Double, y: Double, width: Double, height: Double, isInteractable: Boolean = false) {

  // Function to determine if the entity is within the coordinates of the walls
  def intersects(otherX: Double, otherY: Double, otherWidth: Double, otherHeight: Double): Boolean = {
    otherX < x + width && otherX + otherWidth > x &&
      otherY < y + height && otherY + otherHeight > y
  }

  def draw(gc: GraphicsContext, cameraX: Double, cameraY: Double): Unit = {
    gc.fill = Color.Gray
    gc.fillRect(x - cameraX, y - cameraY, width, height)
  }
}

