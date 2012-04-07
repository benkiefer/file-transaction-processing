package org.burgers.file.transactions

import java.nio.channels.FileChannel

class FileCopier {
  void copy(File from, File to){
    FileInputStream fromStream
    FileOutputStream toStream

    try{
      toStream = new FileOutputStream(to)
      fromStream = new FileInputStream(from)

      FileChannel toChannel = toStream.channel
      FileChannel fromChannel = fromStream.channel

      fromChannel.transferTo(0, fromChannel.size(), toChannel)

    } finally {
      if (toStream) toStream.close()
      if (fromStream) fromStream.close()
    }
  }
}
