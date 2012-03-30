package org.burgers.file.transactions

import org.apache.commons.transaction.file.FileResourceManager
import org.apache.log4j.Logger
import org.apache.commons.transaction.util.LoggerFacade
import org.apache.commons.transaction.util.Log4jLogger

class FileTransactionManager {
  LoggerFacade logger = new Log4jLogger(Logger.getLogger(this.class))
  String workingDirectory

  void doInTransaction(File file, Closure closure) {
      def storeDirectory = file.parentFile.absolutePath
      FileResourceManager frm = new FileResourceManager(storeDirectory, storeDirectory + "/working", false, logger)

    String transactionId
    OutputStream stream

    try{
      frm.start()
      transactionId = frm.generatedUniqueTxId()
      frm.startTransaction transactionId
      stream = frm.writeResource(transactionId, file.name, true)
      closure stream
      frm.commitTransaction(transactionId)
    } catch (e){
      e.printStackTrace()
      frm.rollbackTransaction transactionId
    } finally {
      stream.flush()
      stream.close()
    }
  }
}
