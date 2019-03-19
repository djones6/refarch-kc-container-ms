/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ibm.labs.kc.containermgr.streams;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;

import com.google.gson.Gson;

import ibm.labs.kc.model.Order;
import ibm.labs.kc.model.events.OrderEvent;
import ibm.labs.kc.utils.ApplicationConfig;

/**
 * In this example, we implement a simple LineSplit program using the high-level Streams DSL
 * that reads from a source topic "streams-plaintext-input", where the values of messages represent lines of text;
 * the code split each text line in string into words and then write back into a sink topic "streams-linesplit-output" where
 * each record represents a single word.
 */
public class ContainerOrderAssignment {

	public static Topology buildProcessFlow() {
		 final StreamsBuilder builder = new StreamsBuilder();
	        Gson parser = new Gson();
	        
	        builder.stream("orders")
	        		.foreach((key,value) -> {
	        			Order order = parser.fromJson((String)value, OrderEvent.class).getPayload();
	        			// TODO do something to the order
	        			System.out.println("received order " + key + " " + value);
	        		});

	        return builder.build();
	}
	
    public static void main(String[] args) throws Exception {

        Properties props = ApplicationConfig.getStreamsProperties("order-streams");

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");


        final Topology topology = buildProcessFlow();
        System.out.println(topology.describe());
        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
        	streams.cleanUp(); // delete the app local state
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }
}