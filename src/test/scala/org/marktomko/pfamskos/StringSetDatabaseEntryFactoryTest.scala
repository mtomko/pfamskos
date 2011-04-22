package org.marktomko.pfamskos

import org.junit.Test
import org.junit.Assert.assertEquals

class StringSetDatabaseEntryFactoryTest {
  @Test
  def testRoundtrip() {
    val factory = new StringSetDatabaseEntryFactory
    val set0 = scala.collection.mutable.HashSet[String]()
    val set1 = scala.collection.mutable.HashSet("Q6E6F9", "Q8SUA0", "Q5KM85", "A5DN39")
    
    var result = factory.toValue(factory.toEntry(set0))
    assertEquals(set0, result)
    
    result = factory.toValue(factory.toEntry(set1))
    assertEquals(set1, result)
  }
}