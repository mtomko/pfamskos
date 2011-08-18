package org.marktomko.pfamskos.stockholm

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map

class StockholmRecord(val id: String,
                      val r: Map[String, ListBuffer[String]],
                      val memberFamilies: List[String],
                      val memberProteins: List[String],
                      val proteinAccesionMap: Map[String, String],
                      val proteinSequenceMap: Map[(String, (Int, Int)), String]) {
  private val record = r.toMap
  
  def getFields(): Set[String] = {
    record.keySet
  }

  def getValues(field: String): List[String] = {
    record(field).toList
  }
}