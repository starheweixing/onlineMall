package com.heweixing.config;

import com.heweixing.service.OrderService;
import com.heweixing.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderJob {

    @Autowired
    private OrderService orderService;

    /**
     * 弊端
     * 1.有时间差
     * 2.不支持集群      使用集群后有多个定时任务 解决方案：只使用一台计算机节点，单独用来运行所有定时任务
     * 3.会对数据库进行全表搜索，极其影响数据库性能。
     *
     * 定时任务：仅仅只是用于小型轻量级项目，传统项目
     *
     * 可以使用MQ消息队列
     *  10:12下单，11:12检查，如果当前状态还是10，则直接关闭订单.
     *
     */

    @Scheduled(cron = "0 0 0/1 * * ?")
//    @Scheduled(cron = "0/3 * * * * ?")
    public void autoCloseOrder(){
        orderService.closeOrder();
        System.out.println("执行定时任务，当前时间为："
                + DateUtil.getCurrentDateString(DateUtil.DATETIME_PATTERN));
    }

}
