package Game.Model

import Game.Util.GameUtils.{InputHandler, Inventory}
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.input.KeyCode
import scalafx.scene.image.Image
import scalafx.scene.paint.Color

// Common functionality for All Item subclasses
abstract class Item(var x: Double, var y: Double, var size: Double) extends Range {
  var isPickedUp: Boolean = false
  val itemImage: Image

  def draw(
            graphics: GraphicsContext,
            cameraX: Double,
            cameraY: Double,
            player: Player,
            flashlight: Flashlight
          ): Unit = {
    if (!isPickedUp && isWithinRange(player, flashlight)) {
      graphics.drawImage(itemImage, x - cameraX, y - cameraY, size, size)
    }
  }
}

case class Key(_x: Double, _y: Double, _size: Double) extends Item (_x: Double, _y: Double, _size: Double)
  with canBePickedUp {
  override val itemImage: Image = new Image("file:src/main/resources/Assets/Items/key.png")

  override def pickUp(inventory: Inventory): Unit = {
    isPickedUp = true
    inventory.addKey(this)
  }
}

case class Gun(_x: Double, _y: Double, _size: Double) extends Item (_x: Double, _y: Double, _size: Double)
  with canBePickedUp {
  override val itemImage: Image = new Image("file:src/main/resources/Assets/Items/gun.png")

  override def pickUp(inventory: Inventory): Unit = {
    isPickedUp = true
    inventory.addGun(this)
  }

  // Function to draw and update the canvas with a bullet when shot
  def shoot(
             player: Player,
             bulletManager: BulletManager,
             inputHandler: Option[InputHandler],
             cameraX: Double, cameraY: Double
           ): Unit = {
    if (isPickedUp) {
      inputHandler.foreach { ih =>
        val mouseWorldX = ih.mouseX + cameraX
        val mouseWorldY = ih.mouseY + cameraY
        val angle = Math.atan2(mouseWorldY - player.y, mouseWorldX - player.x)
        bulletManager.addBullet(Bullet(player.x + player.size / 2, player.y + player.size / 2, angle))
      }
    }
  }
}

case class Flashlight(_x: Double, _y: Double, _size: Double) extends Item (_x: Double, _y: Double, _size: Double)
  with canBePickedUp {
  var isActive = false
  var fieldOfViewRadius: Double = 80.0
  override val itemImage: Image = new Image("file:src/main/resources/Assets/Items/flashlight.png")

  override def pickUp(inventory: Inventory): Unit = {
    isPickedUp = true
    inventory.addFlashlight(this)
  }

  // Changes the radius of which entities are drawn
  def toggleFlashlight(): Unit = {
    if (isPickedUp) {
      isActive = !isActive
      fieldOfViewRadius = if (isActive) 140.0 else 80.0
    }
  }

  // Triggers the Key to be drawn on the canvas once it is picked up
  override def checkPickup(player: Player, inputHandler: Option[InputHandler], inventory: Inventory): Unit = {
    inputHandler.foreach { ih =>
      val distance = Math.sqrt(Math.pow(x - player.x, 2) + Math.pow(y - player.y, 2))
      if (distance < size && ih.keysPressed.contains(KeyCode.E)) {
        pickUp(inventory)
        inventory.isNote3PickedUp = true
      }
    }
  }
}

case class BulletItem(_x: Double, _y: Double, _size: Double) extends Item (_x: Double, _y: Double, _size: Double)
  with canBePickedUp {
  override val itemImage: Image = new Image("file:src/main/resources/Assets/Items/bullet.png")

  override def pickUp(inventory: Inventory): Unit = {
    isPickedUp = true
  }
}

case class Note(_x: Double, _y: Double, _size: Double, content: Image, addToInventory: (Inventory, Note) => Unit)
  extends Item(_x: Double, _y: Double, _size: Double) with NoteBase with canBePickedUp {
  override val itemImage: Image = new Image("file:src/main/resources/Assets/Items/note.png")

  override def checkPickup(player: Player, inputHandler: Option[InputHandler], inventory: Inventory): Unit = {
    inputHandler.foreach { ih =>
      val distance = Math.sqrt(Math.pow(x - player.x, 2) + Math.pow(y - player.y, 2))
      if (distance < 40 && ih.keysPressed.contains(KeyCode.E)) {
        if (!isPickedUp) {
          pickUp(inventory)
          ih.clearKeys()
        }
      }
    }
  }

  override def pickUp(inventory: Inventory): Unit = {
    isPickedUp = true
    addToInventory(inventory, this)
    showDisplay(content)
  }
}

case class Button(_x: Double, _y: Double, _size: Double, var isActive: Boolean, player: Player, flashlight: Flashlight)
  extends Item(_x: Double, _y: Double, _size: Double) with Range {

  def draw(graphics: GraphicsContext, cameraX: Double, cameraY: Double): Unit = {
    if (isWithinRange(player, flashlight)) {
      graphics.fill = if (isActive) Color.Green else Color.Red
      graphics.fillRect(x - cameraX, y - cameraY, size, size)
    }
  }

  def checkInteraction(playerX: Double, playerY: Double, inventory: Inventory, inputHandler: Option[InputHandler]): Unit = {
    inputHandler.foreach { ih =>
      val distance = Math.sqrt(Math.pow(x - playerX, 2) + Math.pow(y - playerY, 2))
      if (distance < 40 && ih.keysPressed.contains(KeyCode.E)) {
        if (inventory.hasKey) {
          isActive = !isActive
          println(s"Button interacted. isActive: $isActive")
          ih.clearKeys()
        }
      }
    }
  }

  override val itemImage: Image = null
}
