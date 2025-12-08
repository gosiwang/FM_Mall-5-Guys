package com.sesac.fmmall.Config;

import com.sesac.fmmall.DTO.Order.OrderResponse;
import com.sesac.fmmall.Entity.Order;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public ModelMapper modelMapper() {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldAccessLevel(
                        org.modelmapper.config.Configuration.AccessLevel.PRIVATE
                )
                .setFieldMatchingEnabled(true)
                // 🔹 여러 경로가 보여도(ambiguous) 예외 던지지 말고 그냥 무시해
                .setAmbiguityIgnored(true);

        // 🔹 Order -> OrderResponse 매핑 시 userId는 자동 매핑하지 마
        //    (우리가 Service에서 직접 dto.setUserId(...) 해줄 거라서)
        modelMapper.typeMap(Order.class, OrderResponse.class)
                .addMappings(mapper -> mapper.skip(OrderResponse::setUserId));

        return modelMapper;
    }
}
