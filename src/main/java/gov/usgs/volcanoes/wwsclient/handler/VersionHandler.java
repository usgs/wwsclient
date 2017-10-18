package gov.usgs.volcanoes.wwsclient.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usgs.volcanoes.wwsclient.ClientUtils;
import gov.usgs.volcanoes.wwsclient.VersionHolder;
import io.netty.buffer.ByteBuf;

/**
 * Receive and process response from a winston GETMENU request.
 *
 * @author Tom Parker
 */
public class VersionHandler extends AbstractCommandHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(VersionHandler.class);

  private final VersionHolder version;

  /**
   * Constructor.
   * 
   * @param version object to hold the version number
   */
  public VersionHandler(VersionHolder version) {
    super();
    this.version = version;
  }

  @Override
  public void handle(ByteBuf msgBuf) throws IOException {
    LOGGER.debug("Listening for version.");

    String header = ClientUtils.readResponseHeader(msgBuf);
    if (header == null) {
      LOGGER.debug("Still waiting for full response line.");
      return;
    } else {
      version.version = Integer.parseInt(header.split(" ")[1]);
      sem.release();
      msgBuf.release();
    }
  }

}
