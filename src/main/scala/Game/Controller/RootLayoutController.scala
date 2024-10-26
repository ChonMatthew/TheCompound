package Game.Controller

import Game.MainApp
import scalafx.scene.control.MenuBar
import scalafx.scene.layout.BorderPane
import scalafxml.core.macros.sfxml

@sfxml
class RootLayoutController(val menuBar: MenuBar, val borderPane: BorderPane) {
  def handleClose(): Unit = {
    System.exit(0)
  }

  def handleHelp(): Unit = {
    MainApp.showHelp
  }

  def removeMenuBar(): Unit = {
    borderPane.setTop(null)
  }

  def showMenuBar(): Unit = {
    borderPane.setTop(menuBar)
  }
}
