package gov.usgs.volcanoes.wwsclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.netty.buffer.ByteBuf;

/**
 * Utilities methods for working with winston client classes.
 *
 * @author Tom Parker
 */
public class ClientUtils {
  /**
   * Read a single line from a ByteBuf. If the buffer does not contain a full line, no change to the
   * buffer is made.
   * 
   * @param msgBuf Buffer to search
   * @return First line from bugger or null if buffer does not contain a full line 
   * @throws IOException when buffer cannot be read
   */
  public static String readResponseHeader(ByteBuf msgBuf) throws IOException {
    int eol = msgBuf.indexOf(msgBuf.readerIndex(), msgBuf.writerIndex(), (byte) '\n');
    if (eol == -1) {
      return null;
    } else {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      msgBuf.readBytes(bos, eol - msgBuf.readerIndex());

      @SuppressWarnings("unused")
      byte newLine = msgBuf.readByte();

      return bos.toString();
    }
  }
}
