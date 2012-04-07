package org.burgers.file.transactions

import org.junit.Before
import org.junit.Test
import org.junit.After

class FileCopierTest {
  FileCopier copier
  File toFile
  File fromFile

  @Before
  void setUp(){
    copier = new FileCopier()
    toFile = File.createTempFile("goingTo", ".txt")
    fromFile = File.createTempFile("comingFrom", ".txt")
  }

  @Test
  void copy(){
    def fileText = "This is a test."
    fromFile.text = fileText
    copier.copy(fromFile, toFile)

    assert fromFile.text == fileText
    assert toFile.text == fromFile.text
  }

  @After
  void tearDown(){
    toFile.delete()
    fromFile.delete()
  }


}
