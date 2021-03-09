### section3

---



##### 새로운 할인 정책

- 고정 할인이 아닌 정률 할인으로 바꿀 것

  => 계획에 따르지 말고 변화에 대응하자!

  - 기존 `DiscountPolicy` interface에 정률 할인을 할 수 있는 `RateDiscountPolicy` 구현체 class 추가



##### test 생성

test를 원하는 클래스 이름에 `ctrl + shift + t`를 누르면 자동으로 test 파일 생성



- 테스트 시 꼭 실패하는 테스트도 추가해야 함

  ```java
  class RateDiscountPolicyTest {
      RateDiscountPolicy discountPolicy = new RateDiscountPolicy();
      // 성공 케이스
      @Test
      @DisplayName("vip는 10% 할인이 적용되어야 함")
      void vip_o() {
          // given
          Member member = new Member(1L, "zeze", Grade.VIP);
          // when
          int discount = discountPolicy.discount(member, 10000);
          // then
          Assertions.assertThat(discount).isEqualTo(1000);
      }
      // 실패 케이스
      @Test
      @DisplayName("VIP가 아니면 할인이 적용되지 않아야 함")
      void vip_x() {
          // given
          Member member = new Member(1L, "zezeBASIC", Grade.BASIC);
          // when
          int discount = discountPolicy.discount(member, 10000);
          // then
          Assertions.assertThat(discount).isEqualTo(1000);    
          // BASIC 등급은 discount가 0이어야 함
      }
  
  }
  ```

  ![image-20210310010103186](C:\Users\thwjd\AppData\Roaming\Typora\typora-user-images\image-20210310010103186.png)





##### FixDiscountPolicy() -> RateDiscountPolicy() 변환 시의 문제 점

- 추상(인터페이스) 뿐만 아니라 구현 클래스에도 의존하고 있음

  - 인터페이스: `DiscountPolicy`
  - 구현클래스: `FixDiscountPolicy`, `RateDiscountPolicy`

- 클라이언트 코드인`OrderServiceImpl`가 `DiscountPolicy`와 `FixDiscountPolicy` 모두 의존하고 있음

  => **DIP 위반** (인터페이스에만 의존해야 함)

  `FixDiscountPolicy`를 의존하도록 코드 수정해야 함

  => **OCP 위반**



##### 어떻게 문제 해결할까

- DIP 위반하지 않도록(인터페이스에만 의존하도록) 의존관계 변경

  `OrderServiceImpl`이 `DiscountPolicy`만 의존하도록 수정

  ```java
  public class OrderServiceImpl implements OrderService {
      private final MemberRepository memberRepository = new MemoryMemberRepository();
      // 고정 할인 정책(FixDiscountPolicy())을 없애고 정률 할인 정책으로 바꿈
      // private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
      // private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
  
      // 클라이언트 코드 OrderServiceImpl()가 인터페이스 DiscountPolicy()만 의존하도록 수정
      private DiscountPolicy discountPolicy;
      ...
  }
  ```

  **DIP는 위반하지 않았으나 `discountPolicy`의 구현 클래스가 없으므로 `NullPointerError` 발생**





##### 관심사의 분리

- 각각의 인터페이스가 배역이라고 할 때, 여러 배우를 구현체라고 할 수 있다.
- 























