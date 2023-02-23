package com.xzkj.apiController.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "demo-service",contextId = "backService")
public interface IBackService {

    /**
     * 北京移动视频短信通道-10693878--01--状态报告
     * @return
     */
    @RequestMapping(value = "/backService/mt01", method = RequestMethod.GET)
    String mt01(@RequestParam String body,@RequestParam String channelId,@RequestParam String ipAddress);

    /**
     * 联通视频短信通道-10693878--01--状态报告
     * @return
     */
    @RequestMapping(value = "/backService/mt03", method = RequestMethod.GET)
    String mt03(@RequestParam String body,@RequestParam String channelId,@RequestParam String ipAddress);

    /**
     * 物朗视频短信通道-10693878--01--状态报告
     * @return
     */
    @RequestMapping(value = "/backService/mt04", method = RequestMethod.GET)
    String mt04(@RequestParam String body,@RequestParam String channelId,@RequestParam String ipAddress);

    /**
     * 上海电信视频短信通道-10693878--01--状态报告
     * @return
     */
    @RequestMapping(value = "/backService/mt05", method = RequestMethod.GET)
    String mt05(@RequestParam String body,@RequestParam String channelId,@RequestParam String ipAddress);

    /**
     * 联通视频短信通道-10693878--01--用户上行
     * @return
     */
    @RequestMapping(value = "/backService/mo03", method = RequestMethod.GET)
    String mo03(@RequestParam String body,@RequestParam String channelId,@RequestParam String ipAddress);

}
