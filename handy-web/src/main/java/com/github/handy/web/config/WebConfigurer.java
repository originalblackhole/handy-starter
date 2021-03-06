package com.github.handy.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import java.math.BigInteger;
import java.util.List;

/**
 * <p> 处理Long型字段，避免前端接收时出现精度丢失问题</p>
 *
 * @author rui.zhou
 * @date 2018/12/4 18:39
 */
@Configuration
public class WebConfigurer extends WebMvcConfigurerAdapter {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                ObjectMapper mapper = (( MappingJackson2HttpMessageConverter ) converter).getObjectMapper();
                SimpleModule simpleModule = new SimpleModule();
                simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
                simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
                simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
                mapper.registerModule(simpleModule);
            }
        }
    }
}
