package com.xzkj.platform.khd.controller;


import com.xzkj.platform.common.core.controller.BaseController;
import com.xzkj.platform.khd.service.DataStatisticAppChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @author WHY
 * @date 2022年03月04日 16:56
 */
@Controller
@RequestMapping("/khd/dataStatisticAppChannel")
public class DataStatisticAppChannelController extends BaseController {

    @Autowired
    private DataStatisticAppChannelService dataStatisticAppChannelService;

    @GetMapping("/sendStatistics")
    @ResponseBody
    public Map sendStatistics() {
        return dataStatisticAppChannelService.getMonthStatistics();
    }
}
