package gov.usgs.volcanoes.wwsclient.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usgs.volcanoes.wwsclient.WWSClient;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;

/**
 * Inbound handler.
 * 
 * @author Tom Parker
 *
 */
public class WWSClientHandler extends ChannelInboundHandlerAdapter {
  @SuppressWarnings("unused")
  private static final Logger LOGGER = LoggerFactory.getLogger(WWSClient.class);

  /** handler key */
  public static final AttributeKey<AbstractCommandHandler> handlerKey =
      AttributeKey.valueOf("commandHandler");

  private ByteBuf buf;

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
    try {
      buf.writeBytes((ByteBuf) msg);
      AbstractCommandHandler handler = ctx.channel().attr(handlerKey).get();
      handler.handle(buf);
    } finally {
      ReferenceCountUtil.release(msg);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }

  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    buf = ctx.alloc().buffer();
  }

  public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    buf.release();
    LOGGER.debug("TOMP SAYS CHANNEL UNREGISTERED");
  }
}
