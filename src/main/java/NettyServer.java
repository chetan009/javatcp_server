import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class NettyServer {
    private static final Logger logger = LogManager.getLogger(NettyServer.class);
    public static void main(String [] args) {
        try {
            final Properties props = KafkaAvroProducer.loadConfig("src/main/resources/application.properties");
            new NettyServer().run( Integer.parseInt(props.get("tcp_server_port").toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(int port) {
        logger.info("TCP Server starting....");
        //Configure server thread pool
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_REUSEADDR, true); //fast multiplexing

        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel socketChannel) {
                socketChannel.pipeline().addFirst(new TcpCountHandler());
                socketChannel.pipeline().addLast(new ServerHandler());
            }
        });
        serverBootstrap.bind(port).addListener((ChannelFutureListener) future -> {logger.info("Server binded port = " + port);});
    }
}
