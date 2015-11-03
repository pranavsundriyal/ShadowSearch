package com.orbitz.shadow.logging;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

public class amazonSQS {
	AmazonSQS sqs;
	String queueUrl;
	
	public amazonSQS(){
		DefaultAWSCredentialsProviderChain dChain = new DefaultAWSCredentialsProviderChain();		
		ClientConfiguration cc = new ClientConfiguration();
		Region region = Region.getRegion(Regions.US_WEST_2);
		
		//validating default credential chain is using correct credentials
		System.out.println(dChain.getCredentials().getAWSAccessKeyId());
		
		
		this.sqs = new AmazonSQSClient(dChain, cc);
		sqs.setRegion(region);
		
		System.out.println("created queue client");
		
		//print existing queues
		System.out.println(sqs.listQueues().getQueueUrls());
		
		queueUrl = sqs.createQueue("resultQueue").getQueueUrl();
		
		System.out.println("created/got queue " + queueUrl);
	}
	
	//validate queue exists, add results to queue
	public SendMessageResult sendMessage(String results) {
				
		SendMessageRequest smr = new SendMessageRequest()
				.withQueueUrl(queueUrl).withMessageBody(results);
		
		return sqs.sendMessage(smr);
		
	}
		
}
