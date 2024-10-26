package Game.Model

import scalafx.scene.image.Image

case class Player(_x: Double, _y: Double, _size: Double, _health: Int = 6) extends Model(_x, _y, _size, _health) {
  override var images: Map[Direction, Image] = Map(
    Right -> new Image("file:src/main/resources/Assets/Models/player_right.png"),
    Left  -> new Image("file:src/main/resources/Assets/Models/player_left.png"),
    Up    -> new Image("file:src/main/resources/Assets/Models/player_up.png"),
    Down  -> new Image("file:src/main/resources/Assets/Models/player_down.png")
  )

  override var maxHealth: Int = 6
}

