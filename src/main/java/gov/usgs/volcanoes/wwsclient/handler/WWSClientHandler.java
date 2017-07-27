package gov.usgs.volcanoes.wwsclient.handler;

import java.io.IOException;

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

  /** handler key */
  public static final AttributeKey<AbstractCommandHandler> handlerKey =
      AttributeKey.valueOf("commandHandler");

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
    AbstractCommandHandler handler = ctx.channel().attr(handlerKey).get();
    handler.handle(msg);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
