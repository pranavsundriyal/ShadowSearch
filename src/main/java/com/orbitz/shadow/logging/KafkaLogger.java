package com.orbitz.shadow.logging;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaLogger {
	
	public KafkaLogger(){
	}
	
	public void log(String message) {
		String topic = "results";
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("retries", "0");
		props.put("acks", "1");
		try {
			KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
			ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, message);
			producer.send(record);
			producer.close();
		} catch (Exception e){
			System.out.println(e.getMessage());
			return;
		}
	}

}
