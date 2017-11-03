package gov.usgs.volcanoes.wwsclient.handler;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usgs.volcanoes.core.data.Scnl;
import gov.usgs.volcanoes.core.time.TimeSpan;
import gov.usgs.volcanoes.core.util.UtilException;
import gov.usgs.volcanoes.winston.Channel;
import gov.usgs.volcanoes.wwsclient.ClientUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

/**
 * Receive and process response from a winston GETMENU request.
 *
 * @author Tom Parker
 */
public class GetChannelsHandler extends AbstractCommandHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(GetChannelsHandler.class);
  private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

  private int linesTotal;
  private int linesRead;
  private final List<gov.usgs.volcanoes.winston.Channel> channels;
  private StringBuffer menu;

  /**
   * Constructor
   * 
   * @param channels List to be populated with channels
   */
  public GetChannelsHandler(List<gov.usgs.volcanoes.winston.Channel> channels) {
    super();
    linesTotal = -Integer.MAX_VALUE;
    linesRead = 0;
    menu = new StringBuffer();
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
        linesTotal = Integer.parseInt(header.split(" ")[1]);
        LOGGER.debug("Server has {} channels.", linesTotal);
      }
    }

    byte[] bytes = new byte[msgBuf.readableBytes()];
    msgBuf.readBytes(bytes);
    String chunk = new String(bytes);
    linesRead += countLines(chunk);
    menu.append(chunk);
    if (linesRead == linesTotal) {
      for (String line : menu.toString().split("\n")) {
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
