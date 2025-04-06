package com.bpm.example.demo1.camel.processor;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ResultProcessor implements Processor {
    public void process(Exchange exchange) {
        //获取camel调用获取结果
        String callResult = exchange.getIn().getBody(String.class);
        //打印camel调用结果
        log.info("Camel调用结果为：{}", callResult);
        //转换成Map
        Map<String, String> callResultMap = JSON.parseObject(callResult, Map.class);
        //过滤得到需要的键值对
        Map<String, String> resultMap = callResultMap.entrySet().stream()
            .filter(map -> "ip".equals(map.getKey()) || "pro".equals(map.getKey())
                || "city".equals(map.getKey()) || "addr".equals(map.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        //将Camel消息体作为Map回传
        exchange.getMessage().setBody(resultMap, Map.class);
    }
}