package Game.Util

import Game.Model._
import Game.Util.GameUtils.{InputHandler, Inventory, PauseMenu}
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.image.Image

class GameState(graphics: GraphicsContext) {
  private val note1Image: Image = new Image("file:src/main/resources/Assets/Background/note1.png")
  private val note2Image: Image = new Image("file:src/main/resources/Assets/Background/note2.png")
  private val note3Image: Image = new Image("file:src/main/resources/Assets/Background/note3.png")
  private val note4Image: Image = new Image("file:src/main/resources/Assets/Background/note4.png")
  private val note5Image: Image = new Image("file:src/main/resources/Assets/Background/note5.png")

  val player = Player(50, 600, 40)
  val gun = Gun(140, 450, 30)
  val flashlight = Flashlight(920, 550, 40)
  val bulletManager = new BulletManager(graphics, 1500, 800)
  val walls: scala.collection.mutable.ListBuffer[Wall] = scala.collection.mutable.ListBuffer(
    WallLoader.loadWallsFromFile("src/main/resources/Data/walls.csv"): _*
  )
  val key: Key = Key(920, 80, 40)
  val buttonItem = Button(750, 20, 30, isActive = false, player, flashlight)
  val enemyManager = new EnemyManager(player, walls)
  val inventory = new Inventory()
  val pauseMenu = new PauseMenu()
  val bulletItems: scala.collection.mutable.ListBuffer[BulletItem] = scala.collection.mutable.ListBuffer(
    BulletItem(50, 420, 30),
    BulletItem(80, 60, 30),
    BulletItem(800, 500, 30)
  )

  private val note1 = Note(_x = 140, _y = 600, _size = 30, content = note1Image, (inventory, note) => inventory.addNote1(note))
  private val note2 = Note(_x = 250, _y = 340, _size = 30, content = note2Image, (inventory, note) => inventory.addNote1(note))
  private val note3 = Note(_x = 50, _y = 470, _size = 30, content = note3Image, (inventory, note) => inventory.addNote1(note))
  private val note4 = Note(_x = 320, _y = 370, _size = 30, content = note4Image, (inventory, note) => inventory.addNote1(note))
  private val note5 = Note(_x = 900, _y = 600, _size = 30, content = note5Image, (inventory, note) => inventory.addNote1(note))

  val notes: List[Note] = List(note1, note2, note3, note4, note5)
  var inputHandler: Option[InputHandler] = None
}


