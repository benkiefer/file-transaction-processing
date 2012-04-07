package org.burgers.file.transactions

class SimpleFileTransactionManager implements FileTransactionManager{
  File backupFile
  File originalFile
  FileCopier fileCopier = new FileCopier()

  void doInTransaction(File file, Closure closure) {
    setUp(file)
    try{
      closure originalFile
      backupFile.delete()
    } catch (e){
      rollback()
      throw e
    }
  }

  private void setUp(File file){
    originalFile = file
    if (originalFile.exists()){
      backupFile = new File(file.parentFile, System.currentTimeMillis().toString())
      fileCopier.copy originalFile, backupFile
    }
  }

  private void rollback(){
    originalFile.delete()
    backupFile.renameTo originalFile
  }

}
