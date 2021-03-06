package gov.usgs.volcanoes.wwsclient.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usgs.volcanoes.core.Zip;
import gov.usgs.volcanoes.core.data.RSAMData;
import gov.usgs.volcanoes.wwsclient.ClientUtils;
import io.netty.buffer.ByteBuf;

/**
 * Receive and process response from a winston GETSCNLRSAMRAW request.
 *
 * @author Tom Parker
 */
public class GetScnlRsamRawHandler extends AbstractCommandHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(GetScnlRsamRawHandler.class);

  private final RSAMData rsam;
  private int length;
  private final boolean isCompressed;
  private ByteArrayOutputStream buf;

  /**
   * Constructor.
   * 
   * @param rsam object to be populated. Existing data, if any, will be disarded.
   * @param isCompressed if true,request that data be compressed before sending it over the network.
   */
  public GetScnlRsamRawHandler(RSAMData rsam, boolean isCompressed) {
    this.rsam = rsam;
    this.isCompressed = isCompressed;
    length = -Integer.MAX_VALUE;
    buf = null;
  }

  @Override
  public void handle(ByteBuf msgBuf) throws IOException {
    if (length < 0) {
      String header = ClientUtils.readResponseHeader(msgBuf);
      if (header == null) {
        LOGGER.debug("Still waiting for full response line.");
        return;
      } else {
        String[] parts = header.split(" ");
        length = Integer.parseInt(parts[1]);
        buf = new ByteArrayOutputStream(length);
        LOGGER.debug("Response length: {}", length);
        LOGGER.debug("" + buf);
      }
    }

    msgBuf.readBytes(buf, msgBuf.readableBytes());
    if (buf.size() == length) {
      LOGGER.debug("Received all bytes.");
      byte[] bytes = buf.toByteArray();
      if (isCompressed) {
        bytes = Zip.decompress(bytes);
      }
      rsam.fromBinary(ByteBuffer.wrap(bytes));
      sem.release();
    } else {
      LOGGER.debug("Still waiting for bytes. {}/{}", buf.size(), length);
    }

  }

}
