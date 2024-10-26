package Game.Model

import Game.Util.GameUtils.Collision
import scalafx.scene.canvas.GraphicsContext

import scala.collection.mutable.ListBuffer

class EnemyManager(val player: Player, val walls: ListBuffer[Wall]) {
  private var enemies: List[Enemy] =
    List(
      Enemy(700, 500, 40),
      Enemy(250, 200, 40),
      Enemy(100, 50, 40),
      Enemy(900, 80, 40),
      Enemy(750, 80, 40),
    )
  // A variable to determine how long until the enemy can damage the player
  private var lastDamageTime: Long = System.currentTimeMillis()

  def update(): Unit = {
    enemies.foreach(_.moveTowards(player.x, player.y, 1.0, walls))
    checkPlayerCollisions()
    enemies = enemies.filter(_.isAlive)
  }

  def draw(gc: GraphicsContext, cameraX: Double, cameraY: Double, player: Player, flashlight: Flashlight): Unit = {
    enemies.foreach { enemy =>
      if (enemy.isWithinRange(player, flashlight) && enemy.isAlive) {
        enemy.draw(gc, cameraX, cameraY)
      }
    }
  }

  def getEnemies: List[Enemy] = enemies

  // collision detection for the enemy to player
  private def checkPlayerCollisions(): Unit = {
    val currentTime = System.currentTimeMillis()
    val damageInterval = 1000

    if (currentTime - lastDamageTime >= damageInterval) {
      enemies.foreach { enemy =>
        if (Collision.isPlayerCollidingWithEnemy(player, enemy)) {
          player.takeDamage()
          lastDamageTime = currentTime
        }
      }
    }
  }
}
