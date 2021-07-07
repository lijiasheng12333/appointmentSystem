package com.ljs.appoitment.task.scheduled;

import com.ljs.appointment.common.rabbit.constant.MqConst;
import com.ljs.appointment.common.rabbit.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScheduledTask {

    @Autowired
    private RabbitService rabbitService;


    //每天8点执行此方法，就医提醒
    //cron 表达式 设置执行间隔
    @Scheduled(cron = "0/30 * * * * ?")
    public void taskPatient() {
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK, MqConst.ROUTING_TASK_8, "");
    }
}
