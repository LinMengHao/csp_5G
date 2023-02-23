package com.xzkj.apiController.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "demo-service",contextId = "apiService")
public interface IApiService {

    /**
     * 视频短信提交
     * @return
     */
    @RequestMapping(value = "/apiService/mmsSend", method = RequestMethod.GET)
    String mmsSend(@RequestParam String body,@RequestParam String ipAddress);

    /**
     * 视频短信状态报告查询
     * @return
     */
    @RequestMapping(value = "/apiService/mmsList", method = RequestMethod.GET)
    String mmsList(@RequestParam String body,@RequestParam String ipAddress);

    /**
     * 视频短信模板提交
     * @return
     */
    @RequestMapping(value = "/apiService/modelSubmit", method = RequestMethod.POST)
    String modelSubmit(String body);

    /**
     * 视频短信模板查询
     * @return
     */
    @RequestMapping(value = "/apiService/modelOne", method = RequestMethod.GET)
    String modelOne(@RequestParam String body,@RequestParam String ipAddress);

    /**
     * 视频短信状态报告重推
     * @return
     */
    @RequestMapping(value = "/apiService/mmsReport", method = RequestMethod.GET)
    String mmsReport(@RequestParam String table,@RequestParam String ids,@RequestParam String ipAddress);

    @RequestMapping(value = "/apiService/modelSubmitToChannel", method = RequestMethod.POST)
    String modelSubmitToChannel(String body);

    @RequestMapping(value = "/apiService/signSubmitToChannel", method = RequestMethod.POST)
    String signSubmitToChannel(String body);

    @RequestMapping(value = "/apiService/signSubmit", method = RequestMethod.POST)
    String signSubmit(String body);
    @RequestMapping(value = "/apiService/signOne", method = RequestMethod.GET)
    String signOne(@RequestParam String body,@RequestParam String ipAddress);
    @RequestMapping(value = "/apiService/modelNewSubmit", method = RequestMethod.POST)
    String modelNewSubmit(String body);
    @RequestMapping(value = "/apiService/modelNewOne", method = RequestMethod.GET)
    String modelNewOne(@RequestParam String body,@RequestParam String ipAddress);

}
