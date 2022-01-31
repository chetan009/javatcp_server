import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
    public static void main(String [] args){
        new NettyServer().run(Config.BEGIN_PORT, Config.END_PORT);
    }
    public void run(int beginPort, int endPort){
        System.out.println("Server staring...");
        //Configure server thread pool
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_REUSEADDR, true); //fast multiplexing
        serverBootstrap.childHandler( new TcpCountHandler());
        for(; beginPort < endPort; beginPort++){
            int port = beginPort;
            serverBootstrap.bind(port).addListener((ChannelFutureListener) future->{
                System.out.println("Server binded port = "+port);
            });
        }
    }
}