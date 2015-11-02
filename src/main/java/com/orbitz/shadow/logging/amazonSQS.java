package com.orbitz.shadow.logging;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

public class amazonSQS {
	AmazonSQS sqs;
	
	public amazonSQS(){
		DefaultAWSCredentialsProviderChain dChain = new DefaultAWSCredentialsProviderChain();
		
		System.out.println(dChain.getCredentials().getAWSAccessKeyId());
		
		this.sqs = new AmazonSQSClient(dChain.getCredentials());
		System.out.println("created queue client");
	}
	
	//validate queue exists, add results to queue
	public SendMessageResult sendMessage(String results) {
		
		System.out.println(sqs.listQueues().toString());
		
		//System.out.println(sqs.getQueueUrl("resultQueue").getQueueUrl());
		
		SendMessageRequest smr = new SendMessageRequest()
				.withQueueUrl("https://sqs.us-west-2.amazonaws.com/037156751156/resultQueue").withMessageBody(results);
		return sqs.sendMessage(smr);
		
	}
		
}
