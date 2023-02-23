package com.xzkj.flowStore.controller.admin;



import com.xzkj.flowStore.utils.AliyunUtil;
import com.xzkj.flowStore.utils.MsgBean;
import com.xzkj.flowStore.vo.PolicyVo;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 班级表 前端控制器
 * </p>
 *
 * @author dashan
 * @since 2019-03-31
 */

@Api(tags = "000.图片信息")
@RestController
@RequestMapping("/admin/image")
public class ImageAction {

    @GetMapping("/getPolicy")
    @ApiOperation("获取上传凭证")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "获取上传凭证", response = MsgBean.class)})
    public MsgBean getPolicy(
            @ApiParam("token") @RequestHeader(value = "token", required = false) String token) {
        String dir = "images";
        PolicyVo vo = AliyunUtil.getPolicy(dir);
        if (vo == null) {
            return MsgBean.error("生成凭证失败，请稍后重试！！");
        }
        MsgBean msg = new MsgBean();
        msg.put("data", vo);
        return msg;
    }


}

