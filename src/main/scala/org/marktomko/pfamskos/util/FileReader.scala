package org.marktomko.pfamskos

import scala.io.Source

object FileReader {
  def main(args : Array[String]) : Unit = {
    var count = 0
    for (line <- Source.fromFile(args(0)).getLines) {
      count += 1
    }
    println(count)
  }
}
