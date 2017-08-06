package gov.usgs.volcanoes.wwsclient.handler;

import java.io.IOException;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;

/**
 * Receive response from winston and append it to something. Since there's no way to know when the
 * full response has been received, I will always hang waiting for more. You'll have to force me to
 * exit somehow.
 *
 * @author Tom Parker
 */
public class StdoutHandler extends AbstractCommandHandler {
  @SuppressWarnings("unused")
  private static final Logger LOGGER = LoggerFactory.getLogger(StdoutHandler.class);

  private final Appendable out;


  /**
   * Constructor
   * 
   * @param out where to put the server response
   */
  public StdoutHandler(Appendable out) {
    super();
    this.out = out;
  }

  @Override
  public void handle(Object msg) throws IOException {
    ByteBuf msgBuf = (ByteBuf) msg;
    out.append(msgBuf.toString(Charset.forName("US-ASCII")));
  }
}
