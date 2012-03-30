package org.burgers.file.transactions

import org.junit.Test
import org.junit.Before
import org.junit.After

class FileTransactionManagerTest {
  FileTransactionManager manager
  File file

  @Before
  void setUp() {
    manager = new FileTransactionManager()
    file = File.createTempFile("test", ".txt")
    manager.workingDirectory = file.parentFile.absolutePath + "/working"
  }

  @After
  void tearDown() {
    file.delete()
  }


  @Test
  void happyPath() {
    manager.doInTransaction(file) {OutputStream stream ->
      1000.times {
        stream.write('hi\n'.bytes)
      }
    }
    assert file.readLines().size() == 1000
  }

}
