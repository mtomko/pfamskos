package org.marktomko.pfamskos

import java.io.FileInputStream

class PrintIdHandler extends RecordHandler {
  override def apply(record: StockholmRecord) {
    println(record.id)
  }
}

object StockholmRecordReaderApp {
  def main(args: Array[String]): Unit = {
    val file = args(0)

    StockholmRecordReader.read(new FileInputStream(file), new PrintIdHandler())
  }
}