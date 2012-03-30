package org.burgers.file.transactions

import groovy.mock.interceptor.MockFor
import junit.framework.TestCase

class FileTransactionManagerTest extends GroovyTestCase{
    FileTransactionManager manager
    File file

    void setUp() {
        manager = new FileTransactionManager()
        file = File.createTempFile("test", ".txt")
    }

    void tearDown() {
        assert !new File(file.parentFile.absolutePath + "/working").exists()
        file.delete()
    }

    void test_happyPath() {
        manager.doInTransaction(file) {OutputStream stream ->
            1000.times {
                stream.write('hi\n'.bytes)
            }
        }
        assert file.readLines().size() == 1000
    }

    void test_rollBackOnFailure() {
        manager.doInTransaction(file) {OutputStream stream ->
            1000.times {
                stream.write('hi\n'.bytes)
                if (it == 100) {
                    throw new RuntimeException("test kaboom!!!")
                }
            }
        }
        assert file.readLines().size() == 0
    }

    void test_rollBackOnFailure_multiple_batches() {
        assert "test kaboom!!!" == shouldFail(RuntimeException){
            5.times {
                if (it == 3) throw new RuntimeException("test kaboom!!!")
                processABatch()
            }
        }
        assert file.readLines().size() == 3000
    }

    private void processABatch(){
        manager.doInTransaction(file) {OutputStream stream ->
            1000.times {
                stream.write('hi\n'.bytes)
            }
        }
    }

}
