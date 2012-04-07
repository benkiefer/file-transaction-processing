package org.burgers.file.transactions

import org.junit.After
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.fail

class SimpleFileTransactionManagerTest {
  SimpleFileTransactionManager manager
  File file

  @Before
  void setUp() {
    manager = new SimpleFileTransactionManager()
    file = File.createTempFile("myBaseFile", ".txt")
  }

  @Test
  void happyPath() {
    manager.doInTransaction(file) {File myFile ->
      1000.times {
        myFile << 'hi\n'
      }
    }
    assert file.readLines().size() == 1000
  }

  @Test
  void rollBackOnFailure_new_file() {
    try{
      manager.doInTransaction(file) {File myFile ->
        1000.times {
          myFile << 'hi\n'
          if (it == 100) {
            throw new KaboomException()
          }
        }
      }
    } catch (KaboomException e){
      assert file.readLines().size() == 0
    }
  }

  @Test
  void rollBackOnFailure_existing_file() {
    def myText = "I'm a little teapot.\n"
    file << myText
    try{
      manager.doInTransaction(file) {File myFile ->
        1000.times {
          myFile << 'hi\n'
          if (it == 100) {
            throw new KaboomException()
          }
        }
      }
      fail()
    } catch (KaboomException e){
      assert file.text.contains(myText)
    }
  }

  @Test
  void rollBackOnFailure_multiple_batches_new_file() {
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
    manager.doInTransaction(file) {File myFile ->
      1000.times {
        myFile << 'hi\n'
      }
    }
  }

  @After
  void tearDown() {
    assert !manager.backupFile.exists()
    file.delete()
  }

}
