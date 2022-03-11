import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import org.apache.kafka.clients.producer.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import testRecord.DataRecordAvro;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Properties;


public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LogManager.getLogger(ServerHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {

        ByteBuf inBuffer = (ByteBuf) msg;
        String received = inBuffer.toString(CharsetUtil.UTF_8);
        String clientIP = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();

        final Properties props = KafkaAvroProducer.loadConfig("src/main/resources/kafka.properties");

        // Create topic if needed
        final String topic = "test";
        KafkaAvroProducer.createTopic(topic, props);

        // Add additional properties.
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        Producer<String, DataRecordAvro> producer = new KafkaProducer<>(props);

        DataRecordAvro record = new DataRecordAvro(received, clientIP, 1L);
        logger.info("Producing record: "+ new Date()+ " - " + clientIP + " - " + record);
        producer.send(new ProducerRecord<>(topic, clientIP, record), new Callback() {
            @Override
            public void onCompletion(RecordMetadata m, Exception e) {
                if (e != null) {
                    e.printStackTrace();
                } else {
                    logger.info("Produced record with " +m.serializedValueSize()+ "Bytes data to topic "+ m.topic()+", partition ["+m.partition()+"] @ offset "+m.offset()+ "%n");
                }
            }
        });
        producer.flush();
        producer.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        logger.error("Exception raised Closing connection");
        cause.printStackTrace();
        ctx.close();
    }
}

