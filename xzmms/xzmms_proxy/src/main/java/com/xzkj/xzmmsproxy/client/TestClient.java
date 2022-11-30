package com.xzkj.xzmmsproxy.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@FeignClient(value = "xzmms-base")
public interface TestClient {
    /**
     * 远程调用
     * @return
     */
    @RequestMapping("test6")
    public String test6();

    @RequestMapping("test7")
    public String test7(String name);
}
