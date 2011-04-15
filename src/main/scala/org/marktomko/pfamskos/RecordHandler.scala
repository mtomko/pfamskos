package org.marktomko.pfamskos

trait RecordHandler {
  /**
   * Applies the record handler to the provided record. This trait must
   * have side effects, as it does not allow any return values.
   * @param record
   */
  def apply(record: StockholmRecord): Unit

}

object RecordHandler {
  val ACCESSION = """([A-Z0-9]+).?([^\n])*""".r
  def getRawAccession(record: StockholmRecord): String = record.getValues("AC")(0)

  def getAccession(record: StockholmRecord): String = {
    val ac = getRawAccession(record)
    val ACCESSION(accession, revision) = ac
    accession
  }

  def getType(record: StockholmRecord): String = {
    if (record.getFields.contains("TP")) {
      record.getValues("TP")(0)
    }
    else {
      "Clan"
    }
  }    
}