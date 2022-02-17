import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
//import org.json.JSONObject;

// kafka producer demo
//import com.inspiry.skyline.kafka.KafKaProducerDemo;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.serialization.StringSerializer;

import java.net.InetSocketAddress;
import java.util.Properties;


public class HelloServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf inBuffer = (ByteBuf) msg;
        Properties props = new Properties();
        props.put("bootstrap.servers", Config.KakfaURI);
        props.put("transactional.id", "my-transactional-id");
        Producer<String, String> producer = new KafkaProducer<>(props,
                new StringSerializer(),
                new StringSerializer());
        try {
            // Prepare Data to send to Kafka
            String received = inBuffer.toString(CharsetUtil.UTF_8);
            String clientIP = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
            //JSONObject obj = new JSONObject();
            //obj.put(clientIP, received);
            System.out.println("Kakfa message: " + clientIP + ": " + received);

            producer.initTransactions();
            producer.beginTransaction();
            producer.send(new ProducerRecord<>(Config.KafkaTopic, clientIP, received
                                              ));
            producer.commitTransaction();
        } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
            // We can't recover from these exceptions, so our only option is to close the producer and exit.
           System.out.println("Known Exception");
            producer.close();
        } catch (KafkaException e) {
            // For all other exceptions, just abort the transaction and try again.
            producer.abortTransaction();
            System.out.println("Timeout Exception");
            producer.close();
        }
        finally {
            producer.close();
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        System.out.println("Exeception raised Closing connection");
        cause.printStackTrace();
        ctx.close();
    }
}

