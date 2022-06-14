package server;

import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.prometheus.client.Counter;

/**
 * @author: ming
 * @date: 2022/6/13 16:57
 */
public class MyServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final Counter counter;

    public MyServerChannelInitializer(Counter counter) {
        this.counter = counter;
    }

    @Override
    protected void initChannel(SocketChannel ch){
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new MyChannelHandler(counter));
    }
}
