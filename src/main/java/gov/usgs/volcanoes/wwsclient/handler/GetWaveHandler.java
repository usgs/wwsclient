package gov.usgs.volcanoes.wwsclient.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usgs.plot.data.Wave;
import gov.usgs.volcanoes.core.Zip;
import gov.usgs.volcanoes.wwsclient.ClientUtils;
import io.netty.buffer.ByteBuf;

/**
 * Receive and process response from a winston GETWAVE request.
 *
 * @author Tom Parker
 */
public class GetWaveHandler extends AbstractCommandHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(GetWaveHandler.class);

  private final Wave wave;
  private int length;
  private final boolean isCompressed;
  private ByteArrayOutputStream buf;

  /**
   * Constructor.
   * 
   * @param wave object to be populated. Any existing data will be discarded.
   * @param isCompressed if true,request that data be compressed before sending it over the network.
   */
  public GetWaveHandler(Wave wave, boolean isCompressed) {
    this.wave = wave;
    this.isCompressed = isCompressed;
    length = -Integer.MAX_VALUE;
    buf = null;
  }

  @Override
  public void handle(Object msg) throws IOException {
    ByteBuf msgBuf = (ByteBuf) msg;
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
      }
    }

    msgBuf.readBytes(buf, msgBuf.readableBytes());
    if (buf.size() == length && length > 0) {
      LOGGER.debug("Received all bytes.");
      byte[] bytes = buf.toByteArray();

      if (isCompressed) {
        System.err.println("decompressing bytes: " + bytes + " : " + bytes.length);
        bytes = Zip.decompress(bytes);
      }
      wave.fromBinary(ByteBuffer.wrap(bytes));

      sem.release();
    } else if (length == 0) {
      sem.release();
    } else {
      LOGGER.debug("Received {} of {} bytes.", buf.size(), length);
    }

  }

}
