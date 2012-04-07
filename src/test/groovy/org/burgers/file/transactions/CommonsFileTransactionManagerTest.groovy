package org.burgers.file.transactions

import org.junit.After
import org.junit.Before
import org.junit.Test

class CommonsFileTransactionManagerTest {
  FileTransactionManager manager
  File file

  @Before
  void setUp() {
    manager = new CommonsFileTransactionManager()
    file = File.createTempFile("test", ".txt")
  }

  @After
  void tearDown() {
    assert !new File(file.parentFile.absolutePath + "/working").exists()
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

  @Test
  void rollBackOnFailure() {
    try {
      manager.doInTransaction(file) {OutputStream stream ->
        1000.times {
          stream.write('hi\n'.bytes)
          if (it == 100) {
            throw new KaboomException()
          }
        }
      }
    } catch (KaboomException e) {
      assert file.readLines().size() == 0
    }
  }

  @Test
  void test_rollBackOnFailure_multiple_batches() {
    try {
      5.times {
        if (it == 3) throw new KaboomException()
        processABatch()
      }
    } catch (KaboomException e) {
      assert file.readLines().size() == 3000
    }
  }

  private void processABatch() {
    manager.doInTransaction(file) {OutputStream stream ->
      1000.times {
        stream.write('hi\n'.bytes)
      }
    }
  }

}
