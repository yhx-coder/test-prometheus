package server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.prometheus.client.Counter;


/**
 * @author: ming
 * @date: 2022/6/13 17:15
 */
public class MyChannelHandler extends SimpleChannelInboundHandler<HttpObject> {
    private final Counter counter;

    public MyChannelHandler(Counter counter) {
        this.counter = counter;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        counter.inc();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        // 为什么一次请求会打印两次 aaa ?
        // Netty的设计中把Request分为了HttpRequest和HttpContent两个部分。
        // 而由于担心HttpContent内容过长(例如上传文件这种场景)，
        // HttpContent又被分成了普通的HttpContent和LastHttpContent两个部分
//        System.out.println("aaa");
        if (msg instanceof HttpRequest) {
            ByteBuf response = Unpooled.copiedBuffer("Hello", CharsetUtil.UTF_8);
            DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK, response);
            defaultFullHttpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf8");
            defaultFullHttpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.readableBytes());
            ctx.channel().writeAndFlush(defaultFullHttpResponse);
        }
    }
}
