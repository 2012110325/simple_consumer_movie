package com.example.microservicesimpleconsumermovie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class MovieController {

    @Autowired
    private RestTemplate restTemplate;

    // 可以用于获取用户微服务的各种信息
    @Autowired
    private DiscoveryClient discoveryClient;

    private static Logger log = LoggerFactory.getLogger(MovieController.class);

    @Value("${user.userServiceUrl}")
    private String url;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @GetMapping("/user/{id}")
    // microservice-provider-user 是用户微服务的虚拟主机名，Ribbon和Eureka配合使用时，会自动将虚拟主机名 映射成微服务的网络地址
    public String test(@PathVariable Long id) {
        return restTemplate.getForObject("http://microservice-provider-user/" + id, String.class);
    }

    @GetMapping("/user-instance")
    public List<ServiceInstance> showInfo() {
        return discoveryClient.getInstances("microservice-provider-user");
    }

    @GetMapping("/log-instance")
    public void logUserInstance() {
        // getForObject方法和 choose方法不能写在同一个方法中，两者会有冲突  TODO  取不到值
        ServiceInstance serviceInstance = loadBalancerClient.choose("microservice-provider-user");
        log.info("{}:{}:{}", serviceInstance.getServiceId(), serviceInstance.getHost(), serviceInstance.getPort());
    }

}
