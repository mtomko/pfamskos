package org.marktomko.collection

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

import java.io.File

class BDBMapTest {

    @Before
    def createEnv() {
      new File("testenv").mkdir()
    }
    
    @After
    def deleteEnv() {
      val env = new File("testenv")
      if (env.exists()) {
        env.listFiles.foreach(f => f.delete)
        env.delete
      }
    }
    
    @Test
    def testAdd() {
      val env = new BDBEnvironment("testenv")
      val map = new BDBMap(env, "testAdd")
      map += ("foo" -> "bar")
      
      assertTrue(map.contains("foo"))
      assertEquals("bar", map("foo"))
    }
    
    @Test
    def testAddMultiple() {
      val env = new BDBEnvironment("testenv")
      val map = new BDBMap(env, "testAdd")
      map += ("foo" -> "bar")
      map += ("baz" -> "quux")
      
      assertTrue(map.contains("foo"))
      assertEquals("bar", map("foo"))
      assertTrue(map.contains("baz"))
      assertEquals("quux", map("baz"))
    }
    
    @Test
    def testRemove() {
      val env = new BDBEnvironment("testenv")
      val map = new BDBMap(env, "testAdd")
      map += ("foo" -> "bar")
      map += ("baz" -> "quux")
      
      assertTrue(map.contains("foo"))
      assertEquals("bar", map("foo"))
      assertTrue(map.contains("baz"))
      assertEquals("quux", map("baz"))
      
      map -= ("foo")
      assertFalse(map.contains("foo"))
      assertTrue(map.contains("baz"))
      assertEquals("quux", map("baz"))
    }
    
    @Test
    def testIterate() {
      val env = new BDBEnvironment("testenv")
      val map = new BDBMap(env, "testAdd")
      map += ("foo" -> "bar")
      map += ("baz" -> "quux")
      map += ("wonky" -> "buttons")
      
      val expected = Map(("foo" -> "bar"), ("baz" -> "quux"), ("wonky" -> "buttons"))
     
      map foreach {case (key, value) => assertEquals(expected(key), value)}
    }
}