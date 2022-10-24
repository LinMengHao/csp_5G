package com.xzkj.accessService.client;

import com.xzkj.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.Duration;
import java.util.List;
@Component
@FeignClient(value = "service-sms")
public interface StudentClient {
    @PostMapping(value = "/base/smsService/t-student/findAll",consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    R test();
}
