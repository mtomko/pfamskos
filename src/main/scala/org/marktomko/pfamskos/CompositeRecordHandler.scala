package org.marktomko.pfamskos

class CompositeRecordHandler(val handlers: List[RecordHandler]) extends RecordHandler {
  override def apply(record: StockholmRecord) {
    handlers.foreach(_.apply(record))
  }
}