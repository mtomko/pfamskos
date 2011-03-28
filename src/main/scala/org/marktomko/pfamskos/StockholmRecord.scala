package org.marktomko.pfamskos

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map

class StockholmRecord(val id: String, val r: Map[String, ListBuffer[String]], val memberFamilies: List[String], val memberProteins: List[String]) {
  private val record = r
  
  def getFields(): Set[String] = {
    Set() ++ record.keys
  }

  def getValues(field: String): List[String] = {
    record(field).toList
  }
}