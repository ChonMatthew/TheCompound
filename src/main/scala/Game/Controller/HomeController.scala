package Game.Controller

import scalafxml.core.macros.sfxml
import Game.MainApp

@sfxml
class HomeController() {

  def handlePlay(): Unit = {
    MainApp.showGame()
  }
  def handleHelp(): Unit = {
    MainApp.showHelp()
  }

  def handleExit(): Unit = {
    System.exit(0)
  }
}

