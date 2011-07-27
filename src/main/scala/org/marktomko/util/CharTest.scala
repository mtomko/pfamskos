package org.marktomko.util

import java.io.FileInputStream

import scala.io.Source

object CharTest {
  def main(args : Array[String]) : Unit = {
    val stream = new FileInputStream(args(0))
    val transform = new SubstitutionStringTransform(new String(Array(0.toChar)), "")
    for (line <- Source.fromInputStream(stream, "UTF-8").getLines) {
      for(c <- line) {
        print(c.toInt)
        print(" ")
      }
      println("")
      println(line)
      println(transform(line))
      println("")
    }
  }
}
