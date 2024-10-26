package Game.Controller

import Game.MainApp
import scalafxml.core.macros.sfxml

@sfxml
class HelpController() {

  def handleBackButton(): Unit = {
    MainApp.showHome()
  }

}
