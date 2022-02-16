import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


public class NettyClient {
    private static final String SERVER = "127.0.0.1";

    public static void main(String [] args){
        new NettyClient().run(Config.BEGIN_PORT, Config.END_PORT);
    }
    public void run(int beginPort, int endPort) {
        System.out.println("Client starting...");
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new ClientHandler());
                    }
                });
        int index = 0;
        int finalPort;

        while (true) {
            finalPort = beginPort + index;

            try {
                bootstrap.connect(SERVER, finalPort).addListener((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        System.out.println("Connection failed ");
                    }
                }).get();
            } catch (Exception e) {
                //e.printStackTrace();
            }
            ++index;
            if (index == (endPort - beginPort)) {
                index = 0;
            }
        }
    }
}
