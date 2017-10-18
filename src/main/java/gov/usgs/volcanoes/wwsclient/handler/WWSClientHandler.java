package gov.usgs.volcanoes.wwsclient.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usgs.volcanoes.wwsclient.WwsClient;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

/**
 * Inbound handler.
 * 
 * @author Tom Parker
 *
 */
public class WWSClientHandler extends ChannelInboundHandlerAdapter {
  @SuppressWarnings("unused")
  private static final Logger LOGGER = LoggerFactory.getLogger(WwsClient.class);

  /** handler key */
  public static final AttributeKey<AbstractCommandHandler> handlerKey =
      AttributeKey.valueOf("commandHandler");

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
    AbstractCommandHandler handler = ctx.channel().attr(handlerKey).get();
    if (msg instanceof ByteBuf) {
      handler.handle((ByteBuf) msg);      
    } else {
      LOGGER.error("Unknown message from network. ({})", msg.getClass().getName());
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
