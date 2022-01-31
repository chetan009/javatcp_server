import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
    private static final String SERVER = "10.0.0.82";
    private Channel channel;
    public static void main(String [] args){
        new NettyClient().run(Config.BEGIN_PORT, Config.END_PORT);
    }
    public void run(int beginPort, int endPort){
        System.out.println("Client starting...");
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                    }
                });
        int index = 0 ;
        int finalPort ;
        while (true){
            finalPort = beginPort + index;
            try {
                /*ChannelFuture f = bootstrap.connect(SERVER, finalPort);
                channel = f.sync().channel();
                ChannelFuture cf = null;

                try {
                    cf = channel.writeAndFlush("Testing..").sync();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                             */
                bootstrap.connect(SERVER, finalPort).addListener((ChannelFutureListener)future ->{
                    if(!future.isSuccess()){
                        System.out.println("Connection failed " );
                    }
                }).get();


            } catch (Exception e) {
                //e.printStackTrace();
            }
            ++index;
            if(index == (endPort - beginPort)){
                index = 0 ;
            }
        }
    }
    public void send(String message) {
        ChannelFuture cf = null;
        try {
            cf = channel.writeAndFlush(message).sync();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        channel.flush();
        if (!cf.isSuccess()) {
            System.out.println("Send failed: " + cf.cause());
        }
    }
}