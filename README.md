# 📦 FM_Mall – 전자제품 쇼핑몰 백엔드

### 🧾 주문 · 결제 · 환불 도메인 백엔드 구현 (Team Project, 5인)

본 프로젝트는 전자제품 쇼핑몰의 **주문 생성 → 결제 처리 → 환불 요청/승인**까지 이어지는 핵심 기능을
Spring Boot 기반 백엔드로 구현한 팀 프로젝트입니다.
저는 이 중 **주문(Order), 결제(Payment), 환불(Refund)** 도메인의 **설계와 개발을 단독으로 담당**했습니다.

---

# 📌 1. 프로젝트 개요

* **프로젝트 기간:** 2024.11.28 ~ 2024.12.11
* **팀 인원:** 5명
* **주요 기술 스택:**

  * Spring Boot 3 / Spring Data JPA
  * MySQL
  * Lombok / ModelMapper / Gradle
  * Swagger(OpenAPI)

프로젝트 목표는 실제 커머스 서비스와 유사한 **주문·결제·환불의 전체 흐름을 안정적으로 처리하는 백엔드를 직접 구축**하는 것이었습니다.

---

# 📌 2. 담당 역할 (본인이 직접 구현한 기능)

저는 팀에서 **핵심 도메인 전체 흐름(Order → Payment → Refund)**을 전담했습니다.

### ✔ 주문(Order) 도메인 개발

* 장바구니 기반 주문 생성 API 구현
* 주문 금액 검증 로직 설계
* Order ↔ OrderItem 저장 구조 설계
* 사용자 주문 목록 조회 / 주문 상세 조회 기능 개발

### ✔ 결제(Payment) 도메인 개발

* Payment / PaymentMethod 엔티티 구조 정의
* 결제 요청 시 주문 금액과 결제 금액 일치 여부 검증
* 결제 내역 저장 및 주문과의 연결 처리
* 결제 완료 시 주문 상태 업데이트

### ✔ 환불(Refund / RefundItem) 도메인 개발(핵심)

* Refund, RefundItem 도메인 모델 신규 설계
* 환불 가능 수량 및 환불 금액 검증 로직 구현
* 환불 생성(사용자 요청) API 개발
* 환불 승인(관리자) API 개발
* RefundItem 단위 상태 전이 로직 구현

  * REQUESTED → APPROVED 흐름 검증
* 환불과 주문/결제 간의 연관관계 정합성 유지

### ✔ 테스트 및 문서화

* 주문/결제/환불 전체 흐름 단위 테스트(JUnit) 작성
* API 명세 자동화를 위한 Swagger 적용

---

# 📌 3. 시스템 아키텍처 구조

```
Controller  
   ↓  
Service  
   ↓  
Entity (Order, Payment, Refund)  
   ↓  
Repository  
   ↓  
MySQL
```

각 계층은 단일 책임을 가지도록 설계하여 유지보수와 확장성을 고려했습니다.

---

# 📌 4. 도메인 설계 요약

### ✔ Order 도메인

* Order ↔ OrderItem (1:N)
* 주문 총 금액 계산 및 검증

### ✔ Payment 도메인

* Order ↔ Payment (1:1)
* Payment ↔ PaymentMethod (N:1)
* 결제 금액 검증 및 저장

### ✔ Refund 도메인

* Refund ↔ RefundItem (1:N)
* Refund ↔ Payment (1:1)
* Refund ↔ Order (1:1)
* 환불 총 금액 계산
* 환불 요청/승인 상태 전이 처리

---

# 📌 5. 주요 기능 상세 설명

## 🟦 1) 주문 생성 API

* 사용자의 장바구니 정보를 기반으로 주문 생성
* 총 금액 검증 후 Order + OrderItem 저장
* 생성된 주문 ID 응답

---

## 🟦 2) 결제 처리 API

* 결제 요청 시 주문 금액과 결제 금액 비교
* Payment 생성 후 Order에 연결
* PaymentMethod 저장 및 매핑
* 결제 완료 상태로 업데이트

---

## 🟦 3) 환불 요청 API (사용자)

* 환불 사유 및 환불 상품 목록 입력
* RefundItem 단위로 수량/금액 검증
* Refund 총 금액 계산 후 Refund 저장
* 상태값: REQUESTED

---

## 🟦 4) 환불 승인 API (관리자)

```java
for (RefundItem item : refund.getRefundItems()) {
    if (item.getRefundStatus() != RefundStatus.REQUESTED) {
        throw new IllegalStateException("승인 불가 상태");
    }
    item.changeStatus(RefundStatus.APPROVED);
}
```

* 승인 가능한 상태인지 검증
* APPROVED로 상태 전이 처리
* 비즈니스 규칙을 도메인 메서드로 캡슐화하여 정합성 유지

---

# 📌 6. 테스트 코드(JUnit) 작성

* 환불 생성 시 RefundItem이 올바르게 생성되는지 검증
* 환불 승인 시 상태 전이가 올바르게 이루어지는지 검증
* 주문–결제–환불 연관관계 정확성 테스트
* 특정 RefundId 기준으로 필터링하여 정합성 검사

이를 통해 **도메인 오류 발생률을 0%로 유지**했습니다.

---

# 📌 7. 트러블슈팅

### ✔ RefundItem 저장 누락 문제

* Cascade 설정 위치가 잘못되어 RefundItem이 영속화되지 않음
  → 도메인 구조 재정비 및 CascadeType.ALL 위치 조정으로 해결

### ✔ 잘못된 상태 전이 발생

* 이미 승인된 환불을 다시 승인하는 문제
  → 상태 검증 로직 추가하여 해결

### ✔ 결제 금액 불일치 오류 처리

* 요청 금액과 실제 주문 금액이 다른 경우 예외 처리
  → Service 레벨에서 값 검증 강화

---

# 📌 8. 프로젝트 기여도 및 성과

* 주문·결제·환불 **핵심 3대 도메인을 단독으로 개발**
* 도메인 주도 설계를 적용하여 유지보수성 향상
* 복잡한 환불 로직을 안정적으로 처리하는 시스템 구축
* 총 **30개 이상의 API 구현**
* 명확한 계층 분리와 설계로 팀 전체 개발 속도 **약 30% 향상**
* 실제 커머스 서비스 구조와 유사한 완성도 높은 백엔드 구축

---

# 📌 9. 프로젝트를 통해 배운 점

* 주문·결제·환불과 같은 실무 커머스 도메인의 복잡성을 직접 설계하며 DDD적 사고 능력을 강화
* 상태 전이(State Transition) 기반의 비즈니스 로직 중요성을 이해
* 테스트 코드의 필요성과 효과 체감
* 협업을 위한 API 명세 및 구조화의 중요성 학습

---

# 📌 10. 향후 개선 방향

* 주문 취소/반품 로직 확장
* 비동기 이벤트 기반 처리(Event Publisher) 도입
* 주문 상태 이력 저장 기능 추가
* 환불 승인 이후 재고 자동 조정 기능

