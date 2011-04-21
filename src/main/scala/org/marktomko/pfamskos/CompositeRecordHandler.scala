package org.marktomko.pfamskos

class CompositeRecordHandler(val handlers: List[StockholmRecordHandler]) extends StockholmRecordHandler {
  override def apply(record: StockholmRecord) {
    handlers.foreach(_.apply(record))
  }
}