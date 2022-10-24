package com.xzkj.accessService.service;


import com.xzkj.accessService.entity.xmlToModel.Messages;
import com.xzkj.accessService.entity.xmlToModel.Multimedia;
import com.xzkj.utils.R;

public interface IReceiveService {
    /**
     * 解析报文，转发给机器人
     * @param messages 状态报告报文
     * @return
     */
    public abstract R notifyStatus(Messages messages);

    /**
     * 解析用于5G消息接入层向Chatbot发送上行消息
     * @param messages
     * @return
     */
    public abstract R receiveMsg(Messages messages);

    /**
     *  撤回消息结果通知
     * @param messages
     * @return
     */
    public abstract R withdrawNotify(Messages messages);

    /**
     *  媒体文件审核通知
     * @param multimedia
     * @return
     */
    public abstract R auditFile(Multimedia multimedia);


}
