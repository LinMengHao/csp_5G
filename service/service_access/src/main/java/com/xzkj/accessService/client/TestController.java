package com.xzkj.accessService.client;

import com.xzkj.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
public class TestController {
    @Autowired
    private StudentClient client;

    @RequestMapping("test")
    public void test(){
        log.info("access.............");
        R test = client.test();
        log.info("数据："+test.getData().toString());
    }
}
