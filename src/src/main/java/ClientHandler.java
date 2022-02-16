import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

public class ClientHandler extends SimpleChannelInboundHandler {

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext){
        channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer("000100010040007CDB0841534D30313130397130000001200930FB3434EDB40B3B8DFEED5503103E1" +
                "8E4B33B2E69ADA7E5024920CBBBCF1072D1D3F24EDE3FCAC8E36956C1853E77B3614F13E88384850" +
                "EFA5ECD9C0F91D3D0845AD459B7B4FD40248D2793C96F19B532025E64B42D544000B04627CE376C" +
                "3BE24A746A116D2500F4E160", CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause){
        cause.printStackTrace();
        channelHandlerContext.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        ByteBuf inBuffer = (ByteBuf) o;
        String received = inBuffer.toString(CharsetUtil.UTF_8);
        System.out.println("Client received: " + received);
    }
}