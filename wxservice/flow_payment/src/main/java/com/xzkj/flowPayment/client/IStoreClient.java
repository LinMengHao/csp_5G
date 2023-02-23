package com.xzkj.flowPayment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "flow-store")
public interface IStoreClient {
    @GetMapping("store/test/{name}")
    public abstract String test(@PathVariable("name") String name);
}
