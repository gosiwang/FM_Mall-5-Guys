package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.Order.OrderCreateRequest;
import com.sesac.fmmall.DTO.Order.OrderItemCreateRequest;
import com.sesac.fmmall.DTO.Order.OrderItemResponse;
import com.sesac.fmmall.DTO.Order.OrderResponse;
import com.sesac.fmmall.DTO.Refund.RefundSummaryResponse;
import com.sesac.fmmall.DTO.Settlement.PaymentSummaryResponse;
import com.sesac.fmmall.Entity.*;
import com.sesac.fmmall.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;

    private final ModelMapper modelMapper;

    /* Order/insert/{userId} - 주문 생성 (주문 + 결제 동시 처리) */

}
