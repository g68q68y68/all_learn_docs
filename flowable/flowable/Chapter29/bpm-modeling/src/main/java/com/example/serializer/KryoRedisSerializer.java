package com.example.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoRedisSerializer<T> implements RedisSerializer<T> {
    private static final ThreadLocal<Kryo> kryoLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        //优先使用无参构造器，也支持无默认构造方法类实例的序列化
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(
                new StdInstantiatorStrategy()));
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) throws SerializationException {
        if (obj == null) {
            return null;
        }
        Kryo kryo = kryoLocal.get();
        //支持循环引用
        kryo.setReferences(true);
        //不要序列化的类先注册
        kryo.setRegistrationRequired(false);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();) {
            Output output = new Output(byteArrayOutputStream);
            kryo.writeClassAndObject(output, obj);
            output.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception ex) {
            throw new SerializationException("序列化异常", ex);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null) {
            return null;
        }
        Kryo kryo = kryoLocal.get();
        //支持循环引用
        kryo.setReferences(true);
        //不要序列化的类先注册
        kryo.setRegistrationRequired(false);
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            Input input = new Input(byteArrayInputStream);
            input.close();
            return (T) kryo.readClassAndObject(input);
        } catch (Exception ex) {
            throw new SerializationException("反序列化异常", ex);
        }
    }
}
