package org.marktomko.pfamskos

import scala.collection.mutable.Map

class UniprotNameHandler(val proteinNameDB: Map[String, String]) extends UniprotRecordHandler {
  override def apply(accession: String, name: String): Unit = {
    proteinNameDB += accession -> name
  }
}