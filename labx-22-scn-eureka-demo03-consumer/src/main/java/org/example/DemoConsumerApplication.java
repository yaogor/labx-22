package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootApplication
//@EnableDiscoveryClient
public class DemoConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoConsumerApplication.class, args);
    }
    @Configuration
    public class RestTemplateConfiguration{
        @Bean
        public RestTemplate restTemplate(){
            return new RestTemplate();
        }
    }

    @RestController
    static class TestController {

        @Autowired
        private DiscoveryClient discoveryClient;
        @Autowired
        private RestTemplate restTemplate;
        @Autowired
        private LoadBalancerClient loadBalancerClient;

        @GetMapping("/hello")
        public String hello(String name) {
            // 获得服务'demo-provider' 的一个实例
            ServiceInstance instance;
            if(true){
                List<ServiceInstance> instanceList = discoveryClient.getInstances("demo-provider");
                instance = instanceList.size()>0?instanceList.get(0):null;
            }else{
                instance = loadBalancerClient.choose("demo-provider");
            }
            //发起调用
            if(instance == null){
                throw  new IllegalStateException("获取不到实例");
            }
            String targetUrl = instance.getUri()+"/echo?name="+name;
            String response = restTemplate.getForObject(targetUrl,String.class);
            return "consumer:" + response;
        }

    }

}
