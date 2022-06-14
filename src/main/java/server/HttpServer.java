package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.prometheus.client.Counter;
import io.prometheus.client.exporter.HTTPServer;

import java.io.IOException;


/**
 * @author: ming
 * @date: 2022/6/13 16:46
 */
public class HttpServer {

    static final Counter requestsTotal = Counter.build()
            .name("requests_total").help("Total requests.").register();

    private Integer port = 8081;

    public HttpServer() {
    }

    public HttpServer(int port) {
        this.port = port;
    }

    public void start() {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new MyServerChannelInitializer(requestsTotal))
                    .bind(port)
                    .sync();
            channelFuture.channel()
                    .closeFuture()
                    .sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws IOException {
        HTTPServer server = new HTTPServer.Builder()
                .withPort(1234)
                .build();

        new HttpServer().start();
    }
}
