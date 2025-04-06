package com.bpm.example.demo2.mule.transformer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bpm.example.demo2.mule.entity.IpInfo;
import lombok.extern.slf4j.Slf4j;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;
import org.mule.transformer.types.DataTypeFactory;

@Slf4j
public class JsonToObject extends AbstractTransformer {
    public JsonToObject() {
        super();
        this.registerSourceType(DataTypeFactory.STRING);
        this.setReturnDataType(DataTypeFactory.create(IpInfo.class));
    }

    @Override
    protected Object doTransform(Object src, String outputEncoding) throws TransformerException {
        String responseString = (String) src;
        log.info("Mule调用结果为：{}", responseString);
        IpInfo ipInfo = JSON.parseObject(responseString, new TypeReference<IpInfo>() {});
        return ipInfo;
    }
}
