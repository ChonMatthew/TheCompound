package Game.Util

import Game.Model.Wall

import scala.io.Source

object WallLoader {

  /*****************************
   *    Title: 4 Nice Ways to Read Files in Scala - Read Files Like a Boss | Rock the JVM
   *    Author: Rock the JVM
   *    Date: 02/05/2020
   *    Code version: Scala 2
   *    Availability: https://www.youtube.com/watch?v=gniSPKoYGDQ
   *
   *****************************/

  // Using the Source, to load data from CSV files
  def loadWallsFromFile(filename: String): List[Wall] = {
    val source = Source.fromFile(filename)
    try {
      val lines = source.getLines().toList
      lines.tail.map { line =>
        val Array(x, y, width, height) = line.split(",").map(_.toDouble)
        Wall(x, y, width, height)
      }
    } finally {
      source.close()
    }
  }
}

