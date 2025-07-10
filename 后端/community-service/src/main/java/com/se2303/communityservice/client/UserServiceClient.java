package com.se2303.communityservice.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-service")
public class UserServiceClient {

}
