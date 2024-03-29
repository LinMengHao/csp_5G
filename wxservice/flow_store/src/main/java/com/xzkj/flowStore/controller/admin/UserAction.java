package com.xzkj.flowStore.controller.admin;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.xzkj.flowStore.controller.admin.bo.UserLoginBo;
import com.xzkj.flowStore.controller.admin.bo.UserSearchBo;
import com.xzkj.flowStore.controller.admin.vo.HomeStatisticsVo;
import com.xzkj.flowStore.entity.Order;
import com.xzkj.flowStore.entity.User;
import com.xzkj.flowStore.entity.UserRights;
import com.xzkj.flowStore.service.OrderService;
import com.xzkj.flowStore.service.UserRightsService;
import com.xzkj.flowStore.service.UserService;
import com.xzkj.flowStore.utils.MD5Util;
import com.xzkj.flowStore.utils.MsgBean;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "004.用户")
@CrossOrigin
@RestController
@RequestMapping("/admin/user")
public class UserAction {


    @Autowired
    private UserService userService;

    @Autowired
    private UserRightsService userRightsService;

    @Autowired
    private OrderService orderService;


    //临时登录
    @PostMapping("/login")
    @ResponseBody
    public MsgBean login(@RequestBody UserLoginBo bo){
        return MsgBean.ok(200,"登陆成功").putData("token","admin");
    }
    //临时获取用户信息
    @GetMapping("/info")
    @ResponseBody
    public MsgBean getInfo(){
        Map<String,String> map=new HashMap<>();
        map.put("name","xzkj");
        map.put("roles","{admin}");
        map.put("avatar","https://edu-manager-lmh.oss-cn-beijing.aliyuncs.com/2023/03/06/1c536c5fd7f44e10a5fb253105d906f8BoONllM3VvBB39fee03799ec4c783767e9e3936e3fa6.jpeg");
        return MsgBean.ok(200,"获取成功").putData(map);
    }
    @PostMapping("/logout")
    public MsgBean logout(){
        return MsgBean.ok(200,"注销成功");
    }



    @PostMapping("/list")
    @ApiOperation("用户列表")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "用户列表", response = User.class)})
    public MsgBean list(
            @ApiParam("token") @RequestHeader(value = "token", required = false) String token,
            @RequestBody UserSearchBo bo) {
        if (bo == null) {
            bo = new UserSearchBo();
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(bo.getName()), "name", bo.getName());
        wrapper.like(StrUtil.isNotBlank(bo.getNick()), "nick", bo.getNick());
        IPage<User> page = new Page<>(bo.getPageNo(), bo.getPageSize());
        userService.page(page, wrapper);
        return MsgBean.ok().putData(page);
    }


    @GetMapping("/homeStatistics")
    @ApiOperation("首页统计数据")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "用户列表", response = HomeStatisticsVo.class)})
    public MsgBean homeStatistics(
            @ApiParam("token") @RequestHeader(value = "token", required = false) String token) {

        LocalDate localDate = LocalDate.now();
        //用户数
        int userCount = userService.count();

        QueryWrapper<UserRights> userRightsQueryWrapper = new QueryWrapper<>();
        userRightsQueryWrapper.eq("state", 0);
        userRightsQueryWrapper.ge("end_date", localDate);
        userRightsQueryWrapper.select("DISTINCT user_id");

        //会员数
        int vipCount = userRightsService.count(userRightsQueryWrapper);


        //总订单数据
        int totalOrder = orderService.count();

        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("state", 1);
        //已支付订单
        int paidOrder = orderService.count(orderQueryWrapper);

        HomeStatisticsVo vo = new HomeStatisticsVo();
        vo.setTotalOrder(totalOrder);
        vo.setTotalPaidOrder(paidOrder);
        vo.setTotalUser(userCount);
        vo.setTotalVipUser(vipCount);
        return MsgBean.ok().putData(vo);
    }

}
