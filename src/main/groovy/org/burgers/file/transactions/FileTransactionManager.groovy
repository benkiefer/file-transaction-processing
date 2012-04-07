package org.burgers.file.transactions

interface FileTransactionManager {
  void doInTransaction(File file, Closure closure)
}