package gov.usgs.volcanoes.wwsclient.handler;

import gov.usgs.volcanoes.core.util.UtilException;
import gov.usgs.volcanoes.winston.Channel;
import gov.usgs.volcanoes.wwsclient.ClientUtils;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;

/**
 * Receive and process response from a winston GETMENU request.
 *
 * @author Tom Parker
 */
public class GetMetadataHandler extends AbstractCommandHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(GetMetadataHandler.class);

  private int linesTotal;
  private int linesRead;
  private final List<gov.usgs.volcanoes.winston.Channel> channels;
  private StringBuffer metadata;

  /**
   * Constructor
   * 
   * @param channels List to be populated with channels
   */
  public GetMetadataHandler(List<gov.usgs.volcanoes.winston.Channel> channels) {
    super();
    linesTotal = -Integer.MAX_VALUE;
    linesRead = 0;
    metadata = new StringBuffer();
    this.channels = channels;
  }

  @Override
  public void handle(ByteBuf msgBuf) throws IOException {
    if (linesTotal < 0) {
      String header = ClientUtils.readResponseHeader(msgBuf);
      if (header == null) {
        LOGGER.debug("Still waiting for full response line.");
        return;
      } else {
        String lines = header.split(" ")[1].trim();
        linesTotal = Integer.parseInt(lines);
        LOGGER.debug("Server has {} channels.", linesTotal);
      }
    }

    byte[] bytes = new byte[msgBuf.readableBytes()];
    msgBuf.readBytes(bytes);
    String chunk = new String(bytes);
    linesRead += countLines(chunk);
    metadata.append(chunk);
    if (linesRead == linesTotal) {
      for (String line : metadata.toString().split("\n")) {
        System.out.println(line);
        try {
          channels.add(new Channel.Builder().parse(line).build());
        } catch (UtilException e) {
          LOGGER.error("Unable to parse server response: {}", line);
        }
      }
      sem.release();
    } else {
      LOGGER.debug("Read {} of {} channels", linesRead, linesTotal);
    }
  }


  private int countLines(String buf) {
    int lines = 0;
    for (int pos = 0; pos < buf.length(); pos++) {
      if (buf.charAt(pos) == '\n') {
        lines++;
      }
    }
    return lines;
  }
}
