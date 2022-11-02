package com.xzkj.operatorService.controller;

import com.xzkj.operatorService.entity.User;
import com.xzkj.utils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @PostMapping("/xiuzhi/bj_mobile/MsgSync/user/login")
    public R login(@RequestBody User user){
        if (user!=null){
            return R.ok().data("token","admin");
        }else {
            return R.error().message("不存在");
        }
    }
    //info
    @GetMapping("/xiuzhi/bj_mobile/MsgSync/user/info")
    public R info(){
        return R.ok().data("roles","[admin]")
                .data("name","林")
                .data("avatar","../../assets/login_images/bg.png");
    }
    @PostMapping("/xiuzhi/bj_mobile/MsgSync/user/logout")
    public R logout(){
        return R.ok();
    }
}
