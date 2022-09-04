/*
 * Copyright [2020] [MaxKey of copyright http://www.maxkey.top]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 

package org.maxkey.provision;

import java.util.UUID;

import org.maxkey.configuration.ApplicationConfig;
import org.maxkey.provision.thread.ProvisioningThread;
import org.maxkey.util.DateUtils;
import org.maxkey.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProvisionService {
    private static final Logger _logger = LoggerFactory.getLogger(ProvisionService.class);
    
    @Autowired
    protected ApplicationConfig applicationConfig;
    

    public void setApplicationConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }


    public ApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }

    /**
     * send  msg to jdbc
     * @param topic TOPIC
     * @param content msg Object
     * @param actionType CREATE UPDATE DELETE
     */
    public void send(String topic,Object content,String actionType) {
        //maxkey.server.message.queue , if not none
        if(applicationConfig.isMessageQueueSupport()) {
            ProvisionMessage message = 
            		new ProvisionMessage(
            				UUID.randomUUID().toString(),	//message id as uuid
            				topic,	//TOPIC
            				actionType,	//action of content
            				DateUtils.getCurrentDateTimeAsString(),	//send time
            				content 	//content Object to json message content
            				);
            String msg = JsonUtils.gson2Json(message);
            //sand msg to provision topic
            Thread thread = null;
            if(applicationConfig.getMessageQueue().equalsIgnoreCase("provision")) {
            	_logger.trace("message...");
            	thread = new  ProvisioningThread(topic,msg);
            }else{
            	_logger.trace("no send message...");
            }
            thread.start();
        }
    }
}