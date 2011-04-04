package org.marktomko.pfamskos

import scala.collection.mutable.ListBuffer

import org.junit.Test
import org.junit.Assert.assertEquals

import java.io.ByteArrayInputStream

class ListRecordHandler extends RecordHandler {
  val records = new ListBuffer[StockholmRecord]
  override def apply(record: StockholmRecord) {
    records += record
  }
}

@Test
class StockholmRecordReaderTest {
  val record = """
# STOCKHOLM 1.0
#=GF ID CBS
#=GF AC PF00571
#=GF DE CBS domain
#=GF AU Bateman A
#=GF CC CBS domains are small intracellular modules mostly found
#=GF CC in 2 or four copies within a protein.
#=GF SQ 5
#=GS O31698/18-71 AC O31698
#=GS O83071/192-246 AC O83071
#=GS O83071/259-312 AC O83071
#=GS O31698/88-139 AC O31698
#=GS O31698/88-139 OS Bacillus subtilis
O83071/192-246          MTCRAQLIAVPRASSLAEAIACAQKMRVSRVPVYERS
#=GR O83071/192-246 SA  9998877564535242525515252536463774777
O83071/259-312          MQHVSAPVFVFECTRLAYVQHKLRAHSRAVAIVLDEY
#=GR O83071/259-312 SS  CCCCCHHHHHHHHHHHHHEEEEEEEEEEEEEEEEEEE
O31698/18-71            MIEADKVAHVQVGNNLEHALLVLTKTGYTAIPVLDPS
#=GR O31698/18-71 SS    CCCHHHHHHHHHHHHHHHEEEEEEEEEEEEEEEEHHH
O31698/88-139           EVMLTDIPRLHINDPIMKGFGMVINN..GFVCVENDE
#=GR O31698/88-139 SS   CCCCCCCHHHHHHHHHHHHEEEEEEEEEEEEEEEEEH
#=GC SS_cons            CCCCCHHHHHHHHHHHHHEEEEEEEEEEEEEEEEEEH
O31699/88-139           EVMLTDIPRLHINDPIMKGFGMVINN..GFVCVENDE
#=GR O31699/88-139 AS   ________________*____________________
#=GR O31699/88-139 IN   ____________1____________2______0____
//
"""

  @Test
  def testOK() {
    val stream = new ByteArrayInputStream(record.getBytes("UTF-8"))
    
    val handler = new ListRecordHandler
    
    StockholmRecordReader.read(stream, handler)
    assertEquals(1, handler.records.size)
    
    val record0 = handler.records(0)
    
    assertEquals("CBS", record0.id)
    assertEquals(List("PF00571"), record0.getValues("AC"))
    assertEquals(List("CBS domains are small intracellular modules mostly found", "in 2 or four copies within a protein."), record0.getValues("CC"))
  }
}

