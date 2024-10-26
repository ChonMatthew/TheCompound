package Game.Util

import Game.MainApp
import Game.Model.{Bullet, Enemy, Flashlight, Gun, Key, Note, Player, Wall}
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.image.Image
import scalafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import scalafx.scene.paint.Color

import scala.collection.mutable.ListBuffer

object GameUtils {

  // Collision Utilities
  object Collision {
    // Collision for entities with uneven sides
    def isCollidingWithWalls(x: Double, y: Double, width: Double, height: Double, walls: ListBuffer[Wall]): Boolean = {
      walls.exists(_.intersects(x, y, width, height))
    }

    // Collision for entities with even sides
    def isCollidingWithWall(x: Double, y: Double, size: Double, walls: ListBuffer[Wall]): Boolean = {
      walls.exists(_.intersects(x, y, size, size))
    }

    /*****************************
     *    Title: SpaceShooter
     *    Author: Avery Choke Kar Sing
     *    Date: 23/07/2017
     *    Code version: Scala 2.10
     *    Availability: https://github.com/ginsan95/SpaceShooter
     *
     *****************************/
    def isCollidingWithEnemy(bullet: Bullet, enemies: List[Enemy]): Option[Enemy] = {
      enemies.find { enemy =>
        val distance = Math.sqrt(Math.pow(bullet.x - enemy.x - 20, 2) + Math.pow(bullet.y - enemy.y - 20, 2))
        distance < 35
      }
    }

    def isPlayerCollidingWithEnemy(player: Player, enemy: Enemy): Boolean = {
      val distance = Math.sqrt(Math.pow(player.x - enemy.x, 2) + Math.pow(player.y - enemy.y, 2))
      distance < 35
    }
  }

  // Input Utilities
  class InputHandler {
    var keysPressed: Set[KeyCode] = Set()
    var mouseX: Double = 0
    var mouseY: Double = 0

    def handleKeyPressed(event: KeyEvent): Unit = {
      keysPressed += event.code
    }

    def handleKeyReleased(event: KeyEvent): Unit = {
      keysPressed -= event.code
    }

    def handleMouseMoved(event: MouseEvent): Unit = {
      mouseX = event.x
      mouseY = event.y
    }

    def handleMouseClicked(): Unit = {}

    def isKeyPressed(keyCode: KeyCode): Boolean = keysPressed.contains(keyCode)

    def clearKeys(): Unit = {
      keysPressed = Set()
    }
  }

  // Inventory Utilities
  class Inventory {
    private var bullets: Int = 0
    private var gun: Option[Gun] = None
    private var flashlight: Option[Flashlight] = None
    private var note: Option[Note] = None
    private var key: Option[Key] = None
    var isVisible: Boolean = false

    var isNote3PickedUp: Boolean = false

    private var items: List[Key] = List()

    // Images of the inventory items
    private val flashlightImage: Image = new Image("file:src/main/resources/Assets/Items/flashlight.png")
    private val gunImage: Image = new Image("file:src/main/resources/Assets/Items/gun.png")
    private val noteImage: Image = new Image("file:src/main/resources/Assets/Items/note.png")
    private val keyImage: Image = new Image("file:src/main/resources/Assets/Items/key.png")
    private val playerImage: Image = new Image("file:src/main/resources/Assets/Models/player.png")

    def addKey(k: Key): Unit = {
      items = items :+ k
      key = Some(k)
    }

    def hasKey: Boolean = key.isDefined

    def addBullet(): Unit = {
      bullets += 8
    }

    def getBulletCount: Int = bullets

    def decrementBulletCount(): Unit = {
      if (bullets > 0) bullets -= 1
    }

    def toggleInventory(): Unit = {
      isVisible = !isVisible
    }

    def addGun(g: Gun): Unit = {
      gun = Some(g)
    }

    def addFlashlight(f: Flashlight): Unit = {
      flashlight = Some(f)
    }

    def addNote1(n: Note): Unit = {
      note = Some(n)
    }

    // draws the inventory on top the canvas
    def draw(graphics: GraphicsContext, canvasWidth: Double, canvasHeight: Double): Unit = {
      if (isVisible) {
        val inventoryWidth = 200
        val inventoryHeight = 300
        val x = (canvasWidth - inventoryWidth) / 2
        val y = (canvasHeight - inventoryHeight) / 2

        graphics.fill = Color.White
        graphics.fillRect(x, y, inventoryWidth, inventoryHeight)
        graphics.drawImage(playerImage, x + 80, y + 50, 110, 150)
        graphics.fill = Color.Black
        graphics.fillText(s"Bullets: $bullets", x + 10, y + 20)
        gun.foreach { _ =>
          graphics.drawImage(gunImage, x + 10, y + 50, 50, 50)
        }
        flashlight.foreach { _ =>
          graphics.drawImage(flashlightImage, x + 10, y + 100, 50, 50)
        }
        note.foreach { _ =>
          graphics.drawImage(noteImage, x + 10, y + 150, 50, 50)
        }
        key.foreach { _ =>
          graphics.drawImage(keyImage, x + 10, y + 200, 50, 50)
        }
      }
    }
  }

  // Movement Utilities
  object Movement {
    def moveTowards(currentX: Double, currentY: Double, targetX: Double, targetY: Double, speed: Double): (Double, Double) = {
      val dx = targetX - currentX
      val dy = targetY - currentY
      val distance = Math.sqrt(dx * dx + dy * dy)

      if (distance > 0) {
        val moveX = (dx / distance) * speed
        val moveY = (dy / distance) * speed
        (moveX, moveY)
      } else {
        (0, 0)
      }
    }
  }

  /*****************************
   *    Title: Canvas (in ScalaFX)
   *    Author: Mark Lewis
   *    Date: 05/11/2015
   *    Code version: Scala 2
   *    Availability: https://www.youtube.com/watch?v=eBmxYQz9WzU&list=PLKUBmwYhjbwSxOjw5tBt8Lfit39tP1jyd&index=50
   *
   *****************************/

  // Pause Utility
  class PauseMenu() {
    var isVisible: Boolean = false
    var isInstructionsVisible: Boolean = false
    var exitFromGame: Boolean = false

    private val menuWidth = 300
    private val menuHeight = 200
    private val buttonWidth = 200
    private val buttonHeight = 40
    private val helpX = (menuWidth - buttonWidth) / 2
    private val helpY = 80
    private val exitX = (menuWidth - buttonWidth) / 2
    private val exitY = 140


    def toggleMenu(): Unit = {
      isVisible = !isVisible
    }

    def toggleInstructions(): Unit = {
      isInstructionsVisible = !isInstructionsVisible
    }

    def toggleExit(): Unit = {
      exitFromGame = true
      MainApp.switchToOriginalScene()
    }

    // draws the menu on top of the canvas
    def draw(graphics: GraphicsContext, canvasWidth: Double, canvasHeight: Double): Unit = {
      if (isVisible) {
        val x = (canvasWidth - menuWidth) / 2
        val y = (canvasHeight - menuHeight) / 2

        graphics.fill = Color.Gray
        graphics.fillRect(x, y, menuWidth, menuHeight)

        graphics.fill = Color.White
        graphics.fillText("Menu", x + menuWidth / 2 - 20, y + 50)

        graphics.fill = Color.DarkBlue
        graphics.fillRect(x + helpX, y + helpY, buttonWidth, buttonHeight)

        graphics.fill = Color.White
        graphics.fillText("Instructions", x + helpX + 50, y + helpY + 25)

        graphics.fill = Color.DarkRed
        graphics.fillRect(x + exitX, y + exitY, buttonWidth, buttonHeight)

        graphics.fill = Color.White
        graphics.fillText("Leave Game", x + exitX + 50, y + exitY + 25)
      }
    }

    // draw the instructions screen on the pause menu
    def drawInstructions(graphics: GraphicsContext, canvasWidth: Double, canvasHeight: Double): Unit = {
      if (isInstructionsVisible) {
        val x = (canvasWidth - menuWidth) / 2
        val y = (canvasHeight - menuHeight) / 2

        graphics.fill = Color.LightGray
        graphics.fillRect(x, y, menuWidth, menuHeight)

        graphics.fill = Color.Black
        graphics.fillText("Instructions:", x + 50, y + 30)
        graphics.fillText("1. Use WASD to Move.", x + 50, y + 60)
        graphics.fillText("2. Use E to Interact.", x + 50, y + 90)
        graphics.fillText("3. Use Mouse to Aim and Shoot.", x + 50, y + 120)
        graphics.fillText("4. Press F to toggle Flashlight.", x + 50, y + 150)
        graphics.fillText("5. Press I to open Inventory.", x + 50, y + 180)
      }
    }

    // Handle clicks to open/close the pause menu or instructions screen
    def handleClick(event: MouseEvent, canvasWidth: Double, canvasHeight: Double): Unit = {
      val x = (canvasWidth - menuWidth) / 2
      val y = (canvasHeight - menuHeight) / 2

      val mouseX = event.x
      val mouseY = event.y

      if (isInstructionsVisible) {
        if (mouseX >= x && mouseX <= x + menuWidth && mouseY >= y && mouseY <= y + menuHeight) {
          toggleInstructions()
        }
      } else if (isVisible) {
        if (mouseX >= x + helpX && mouseX <= x + helpX + buttonWidth &&
          mouseY >= y + helpY && mouseY <= y + helpY + buttonHeight) {
          toggleInstructions()
        } else if (mouseX >= x + exitX && mouseX <= x + exitX + buttonWidth &&
          mouseY >= y + exitY && mouseY <= y + exitY + buttonHeight) {
          toggleExit()
        }
      }
    }
  }
}
