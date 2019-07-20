package com.atguigu.gmall.logger.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.constant.GmallConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController  // @RestController = @Controller + @ResponseBody
@Slf4j
public class LoggerController {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate; //可以认为kafkaTemplate就是一个producer

    //@RequestMapping(path="test",method = RequestMethod.GET)
    //@ResponseBody
    @GetMapping("test")
    public String getTest() {

        return "success";
    }

    @PostMapping("log")
    public String doLog(@RequestParam("logString") String logString) {

        //System.out.println(logString);

        // 1.日志加上时间戳
        JSONObject jsonObject = JSON.parseObject(logString);
        jsonObject.put("ts", System.currentTimeMillis());

        // 2.日志落盘
        String jsonString = jsonObject.toJSONString();
        log.info(jsonString);

        // 3.发送日志到kafka不同的主题
        if ("startup".equals(jsonObject.getString("type"))) {
            kafkaTemplate.send(GmallConstant.KAFKA_TOPIC_STARUP, jsonString);
        } else {
            kafkaTemplate.send(GmallConstant.KAFKA_TOPIC_EVENT, jsonString);
        }
        return "success";
    }
}
