package com.zack.projects.chatapp.cloud.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gateway")
public class CloudGatewayController {

    @GetMapping("/alive")
    public String alive() {
        return "CLOUD-GATEWAY: (ok)";
    }

}
