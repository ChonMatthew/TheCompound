package Game.Model

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

case class Bullet(var x: Double, var y: Double, angle: Double, speed: Double = 10)

/*****************************
 *    Title: Help with Bullet hell projectiles
 *    Author: _Zonko_
 *    Date: 2021
 *    Code version: Python Forum
 *    Availability: https://www.reddit.com/r/pygame/comments/mgsgo1/help_with_bullet_hell_projectiles/
 *
 *****************************/

class BulletManager(val gc: GraphicsContext, val canvasWidth: Double, val canvasHeight: Double) {
  private var bullets: List[Bullet] = List()

  // updates the canvas with the bullet's properties
  def update(): Unit = {
    bullets = bullets.map { bullet =>
      bullet.x += bullet.speed * Math.cos(bullet.angle)
      bullet.y += bullet.speed * Math.sin(bullet.angle)
      bullet
    }.filter(bullet => bullet.x >= 0 && bullet.x <= canvasWidth && bullet.y >= 0 && bullet.y <= canvasHeight)
  }

  def draw(cameraX: Double, cameraY: Double): Unit = {
    bullets.foreach { bullet =>
      gc.fill = Color.Red
      gc.fillOval(bullet.x - cameraX, bullet.y - cameraY, 5, 5)
    }
  }

  def addBullet(bullet: Bullet): Unit = {
    bullets = bullets :+ bullet
  }

  def getBullets: List[Bullet] = bullets
}
