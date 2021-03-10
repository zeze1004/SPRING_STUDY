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

- 애플리케이션 전체 동작 방식 구성 위해`AppConfig`로 **구현 객체**를 생성하고 **연결**하는 책임을 가지는 클래스를 만들어야 함

  

  ##### MemberServiceImpl

  - 생성자 주입

  ```java
  // 기존에는 MemberServiceImpl()에서 MemoryMemberRepository()를 만들어 
  // MemberServiceImpl가 인터페이스인 MemberRepository에도 의존, 구현체인 MemoryMemberRepository에도 의존
  package hello.core.member;
  
  // BAD
  // MemberServiceImpl가 인터페이스인 MemberRepository에도 의존, 구현체인 MemoryMemberRepository에도 의존
  public class MemberServiceImpl implements MemberService {
      // 인터페이스와 구현객체 연결
      private  final MemberRepository memberRepository;
  	// AppConfig에서 memberRepository를 불러야 생성되므로 MemberServiceImpl에는 인터페이스만 존재, 추상화에만 의존(DIP) 성공^^!
      public MemberServiceImpl(MemberRepository memberRepository) {
          this.memberRepository = memberRepository;
      }
  
      @Override
      public void join(Member member) {
          memberRepository.save(member);
      }
  
      @Override
      public Member findMember(Long memberId) {
          return memberRepository.findById(memberId);
      }
  }
  // MemberServiceImpl()에서 MemoryMemberRepository() 삭제, AppConfig에서 만듦
  ```

  - 이제 `MemberServiceImpl`은 `MemoryMemberRepository` 의존 x

    오직 `MemberRepository` 인터페이스에 의존

    따라서 `MemberServiceImpl`는 생성자를 통해 어떤 구현 객체가 들어올지(주입)될 지 알 수 x

    또한, 생성자를 통해 어떤 구현 객체가 주입될지는 `AppConfig`에 의해 결정

  - 의존 관계 고민?은 외부에 맡기고 실행에만 집중할 수 있음

  ##### OrderServiceImpl

  ```java
  package hello.core.order;
  
  import hello.core.discount.DiscountPolicy;
  import hello.core.discount.FixDiscountPolicy;
  import hello.core.discount.RateDiscountPolicy;
  import hello.core.member.Member;
  import hello.core.member.MemberRepository;
  import hello.core.member.MemoryMemberRepository;
  
  public class OrderServiceImpl implements OrderService {
      private final MemberRepository memberRepository = new MemoryMemberRepository();
      // 고정 할인 정책(FixDiscountPolicy())을 없애고 정률 할인 정책으로 바꿈
      // private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
      // private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
  
      // 클라이언트 코드 OrderServiceImpl()가 인터페이스 DiscountPolicy()만 의존하도록 수정
      // OrderServiceImpl()이 discountPolicy 객체에 FixDiscountPolicy()를 직접 할당
      private DiscountPolicy discountPolicy = new FixDiscountPolicy();
  
      // OrderService와 discountPolicy가 분리되어 서로 영향x => 할인 정책 수정시 discountPolicy만 수정하면 됨
      // 단일 체계 원칙을 잘 지킴
     @Override
      public Order createOrder(Long memberId, String itemName, int itemPrice) {
          // 회원 정보 조회
          Member member = memberRepository.findById(memberId);
          // 할인 정책에 적용할 수 있게 멤버와 아이템 가격 전달
          int discountPrice = discountPolicy.discount(member, itemPrice);
  
          // 주문 시 회원id, 주문 메뉴, 가격, 할인된 가격 반환
         return new Order(memberId, itemName, itemPrice, discountPrice);
      }
  }
  
  ```

  ```java
  ...
  public class OrderServiceImpl implements OrderService {
      // OrderService를 이용하기 위해서는 두 필드 memberRepository, discountPolicy가 필요함
      // final은 생성자를 통해 할당
      private final MemberRepository memberRepository;
      private final DiscountPolicy discountPolicy;
      
      // 생성자
      public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
          this.memberRepository = memberRepository;
          this.discountPolicy = discountPolicy;
      }
      ...
  }
  ```

  ##### AppConfig

  - 애플리케이션 실제 동작에 필요한 **구현 객체를 생성**

    `MemberServiceImpl` 			**// 인터페이스 아닌가...**

    `MemoryMemberRepository`   **// 인터페이스 내의 뭐라 해야지...**

    `OrderServiceImpl`				

    `MemoryMemberRepository`

  - 생성한 객체 인스턴스의 참조(레퍼런스)를 **생성자를 통해서 주입(연결)**

    `MemberServiceImpl` -> `MemoryMemberRepository`

    `OrderServiceImpl` -> `MemoryMemberRepository`, `FixDiscountPolicy`

  

  ​	**동작 과정이 잘 이해가 안 감 ㅠㅠ**

  - AppConfig가 선언되면 필드들 내부의 구현체들이 return 되어

    구현체들의 인터페이스에 값이 할당...

    MemberService, OrderService입장에서는 어떤 값이 들어올지 전혀 모르고 값이 들어오면 자기 로직만 실행하면 된다

    => 어느 배우든 같은 배역 소화할 수 있듯이?

    추상화 DIP 위반 ㄴㄴ...?

  ```java
  // application 전체 설정, 구성
  public class AppConfig {
      // 사용하기 전 생성자 주입 필요
  
      public MemberService memberService() {
          return new MemberServiceImpl(new MemoryMemberRepository());
      }
  
      public OrderService orderService() {
          return new OrderServiceImpl(new MemoryMemberRepository(), new FixDiscountPolicy());
      }
  }
  ```

  - AppConfig가 `MemberServiceImpl`, `MemoryMemberRepository` 객체 생성

  - `MemberServiceImpl`의 인터페이스인 `MemberService`와 `MemoryMemberRepository`의 인터페이스인 `MemberRepository`는 구체 클라스 몰라도 되고 추상에만 의존하면 된다

    => **DIP 완성^^!!!**

    

    **이게 맞낭...? **

    => 관심사 분리: 객체 생성하고 연결하는 역할(AppConfig)와 실행(인터페이스 내의 필드 ex.`MemberServiceImpl`, `MemberService`)가 명확히 분리

​		

##### 의존관계 주입(DI)

- `AppConfig`객체가 `memoryMemberRepository` 객체 생성하고  참조값을 `MemberServiceImpl` 생성하면서 생성자로 전달
- 클라이언트인 `MemberServiceImpl` 입장에서는 의존관계를 외부에서 주입되는 것과 같다고 하여 의존관계 주입이라 함 



















