### section5

---

싱글톤



### 웹 애플리케이션과 싱글톤

```java
public class SingletonTest {

    @Test
    @DisplayName("스프링 없는 순수 DI 컨테이너")
    void pureContainer() {
        AppConfig appConfig = new AppConfig();
        // 1. 조회: 호출할 때 마다 객체 생성
        MemberService memberService1 = appConfig.memberService();
        // 2. 조회: 호출할 때 마다 객체 생성
        MemberService memberService2 = appConfig.memberService();

        // 참조값이 다른 것을 확인
        System.out.println("memberService1 = " + memberService1);
        System.out.println("memberService2 = " + memberService2);
        
        // 서로 다른 것임을 검증하는 방법, 같지 않으면 테스트 성공
        Assertions.assertThat(memberService1).isNotSameAs(memberService2);
    }
}
// 출력값
memberService1 = hello.core.member.MemberServiceImpl@36ebc363 // 서로 다른 객체
memberService2 = hello.core.member.MemberServiceImpl@45752059  
```

- `appConfig.memberService()` 호출할 때 마다 객체 생성 되어 JVM 메모리에 적재

- `appConfig.memberService()` 호출 -> return 값으로 `MemberServiceImpl()` 호출되고 -> `MemberServiceImpl()` 의 인자인 `MemoryMemberRepository()` 객체 생성 된다

  => 총 네 개의 객체 생성

  => **메모리 낭비 심각!**

  ----

  정확히 무슨 객체 네 개인거지?

  `memberService1`,`memberService2`,`MemoryMemberRepository()``MemoryMemberRepository()` 인 것인가

  -----

- 해결 방안:

  객체 딱 1개만 생성하고 공유(**싱글톤 패턴**)





### 싱글톤 패턴

- 쿨래스의 인스턴스가 딱 1개만 생성되는 것을 보장하는 디자인 패턴

  => 객체 인스턴스가 2개 이상 생성되지 못하게 해야함

  **HOW?**

  `private`생성자를 사용해 외부에서 임의로 `new` 키워드를 사용하지 못하도록 막아야 함

  => 싱글톤 패턴을 구현하는 방법은 많으나 아래 코드는 객체를 미리 생성해두는 가장 단순하고 안전한 방법

- 싱글톤 패턴 코드

  ```java
  public class SingletonService {
      // 자기 자신을 내부에 pivate static으로 가지게 됨
      // 1. static 영역에 객체를 딱 1개만 생성해둔다
      private static final SingletonService instance = new SingletonService();
  
      // 2. 조회
      public  static SingletonService getInstance() {
          return instance;
      }
  
      private SingletonService() {
  
      }
  
      public void logic() {
          System.out.println("싱글톤 객체 로직 호출");
      }
  
  }
  ```

  - 내부에서 자기 자신 호출해서 인스턴스에 저장된다.

  - 조회할려면 `SingletonService.getInstance()` 호출해야 함

  - 외부에서 `new SingletonService()` 선언시 컴파일 에러

    ```
    SingletonService() has private access in hello.core.singleton.SingletonService
    ```

    

- 싱글턴 패턴 테스트 코드 

  ```java
      @Test
      @DisplayName("싱글톤 패턴을 적용한 객체 사용")
      void singletonService() {
          // new SingletonService(); // private로 생성해서 외부에서 new로 객체 생성x
          SingletonService singletonService1 = SingletonService.getInstance();
          SingletonService singletonService2 = SingletonService.getInstance();
          // 객체 출력
          System.out.println("singletonService1 = " + singletonService1);
          System.out.println("singletonService2 = " + singletonService2);
          
          // SingletonService1, SingletonService2가 같은가
          assertThat(singletonService1).isSameAs(singletonService2);
      }
  
  // 출력값
  singletonService1 = hello.core.singleton.SingletonService@34e9fd99
  singletonService2 = hello.core.singleton.SingletonService@34e9fd99
  ```

  - 같은 객체 인스턴스로 반환

  - `.isSameAs`와 `.isEqualTo` 차이

    **내가 알아보자...^^**

    

- ##### 싱글톤 패턴 문제점

  1. 싱글톤 패턴 구현 코드 자체가 많음

     `logic()`을 호출하기 위해서 그 위 코드를 다 작성해야 함(싱글톤 코드 참조)

  2. 의존관계상 클라이언트가 구체 클래스 의존

     클라이언트코드.getinstance() => `.getinstance()` 는 구체 클래스

     => `DIP` 위반

     => `OCP` 위반할 가능성 높음

  3. 테스트 어렵다

     => 인스턴스를 미리 지정해서 유연하게 테스트하기 어렵다

  4. 유연성이 떨어진다

     - 내부속성 변경하기 힘듬

     - `private` 생성자로 자식 클래스 만들기 어려움

       

- 그러나 스프링은 스프링 컨테이너를 이용해 싱글톤 패턴의 문제점을 해결해줌



### 싱글톤 컨테이너

- 스프링의 기본 빈 등록 방식은 싱글톤이지만 여러 방식을 지원, but, 99% 싱글톤만 사용

- 스프링 컨테이너는 싱글턴 패턴을 적용하지 않아도, 객체 인스턴스를 싱글톤으로 관리해줌

  - 스프링 컨테이너는 객체를 하나만 생성, 관리

    => 스프링 컨테이너가 싱글톤 컨테이너의 역할

- ##### 싱글톤 레지스터:

  싱글톤 객체를 생성, 관리하는 기능

- 싱글톤 패턴으로 코드 짜지 않아도 스프링 컨테이너는 자동으로 같은 객체 참조시켜줌

  ```java
      @Test
      @DisplayName("스프링 컨테이너와 싱글톤")
      void springContainer() {
          ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
          MemberService memberService1 = ac.getBean("memberService", MemberService.class);
          MemberService memberService2 = ac.getBean("memberService", MemberService.class);
  
          // 참조값이 같은 것을 확인
          System.out.println("memberService1 = " + memberService1);
          System.out.println("memberService2 = " + memberService2);
  
          // 조회할 때마다 같은 값 참조
          assertThat(memberService1).isSameAs(memberService2);
      }
  ```





### ✨싱글톤 방식의 주의점✨

매우매우 중요함!

- 싱글톤 객체는 무상태(stateless)로 설계해야 함

  - why?

    여러 클라이언트가 하나의 객체 인스턴스 하나만 공유하므로 싱글톤 객체는 상태를 유지(stateful)하게 설계하면 x

  - how

    1. 특정 클라이언트에 의존적인 필드 x

    2. 특정 클라이언트가 값 변경할 수 있는 필드 x

    3. 가급적 읽기만 가능하게 해야함

    4. 필드 대신에 자바에서 공유되지 않는, 지역변수, 파라미터, ThreadLocal 등을 사용해야

       

- ##### 테스트 코드

  ```java
  public class StatefulService {
      private int price; // 상태 유지하는 필드
  
      public void order(String name, int price) {
          System.out.println("name = " + name + " price = " + price);
          this.price = price; // 여기서 문제 발생
      }
  
      public int getPrice() {
          return price;
      }
  }
  ```

  1. stateful 테스트 코드

  ```java
      @Test
      void statefulServiceSingleton() {
          ApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);
          StatefulService statefulService1 = ac.getBean(StatefulService.class);
          StatefulService statefulService2 = ac.getBean(StatefulService.class);
          // ThreadA: A 사용자 10000원 주문
          // ThreadA: B 사용자 20000원 주문
          statefulService1.order("userA", 10000);
          statefulService2.order("userB", 20000);
  
          // ThreadA: A 사용자 주문 금액 조회
          int price = statefulService1.getPrice();
          // 10000원이 나오길 기대했지만 20000원 출력
          System.out.println("price = " + price);
                  	 assertThat(statefulService1).isSameAs(statefulService2);
      }
      // TestConfig 스프링 컨테이너는 StatefulService Bean하나만 생성해서 사용
      static class TestConfig {
  
          @Bean
          public StatefulService statefulService() {
              return new StatefulService();
          }
      }
  ```

  - 같은 인스턴스를 사용하므로 사용자 B가 price 갱신하여 `statefulService1` 출력해도 20000만이 출력 됨
  - `statefulService1` = `statefulService2`
  - `StatefulService()`의 `price` 는 공유되는 필드인데 특정 클라이언트가 값을 변경함
  - => 공유 필드는 이러한 문제점을 만들 수 있으므로 조심해야함!
  - 스프링 빈은 항상 무상태로 설계하자
    - 필드 대신에 자바에서 공유되지 않는, 지역변수, 파라미터, ThreadLocal 등을 사용해야 함

  ##### 지역변수를 사용해서 수정한 테스트 코드

  ```java
  public class StatefulService {
  //    private int price; // 공유 필드 삭제
  	// order 함수에서 바로 price int를 return 하므로 getPrice() 삭제
      public int order(String name, int price) {
          System.out.println("name = " + name + " price = " + price);
          return price;
      }
  }
  ```

  2. stateless 테스트코드

     ```java
     ...
         // 지역 변수이므로 userAprice, userBprice의 값이 다름
             int userAprice = statefulService1.order("userA", 10000);
             int userBprice = statefulService2.order("userB", 20000);
     ...
     ```

     - 지역변수 생성으로 멀티쓰레딩 문제 해결!





### @Configuration과 싱글톤



##### AppConfig

```java
@Configuration

// application 전체 설정, 구성
public class AppConfig {

    @Bean   // 메서드들이 스프링 컨테이너에 등록
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }
    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }
    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    // DiscountPolicy 추가 -> orderService()의 반환값 인자로 선언해서 한 눈에 역할이 보이게 리팩토링
    @Bean
    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy();
    }
}
```

1. MemeverService 스프링 빈 생성

   => return 되어 memberRepository() 호출 

2. memberRepository()의 return 값인 **new** MemoryMemberRepository() 호출                   #1

3. MemberRepository 스프링 빈 생성

   =>   **new** MemoryMemberRepository() 호출																			#2

4. OrderService 스프링 빈 생성

   => return으로 memberRepository() 호출 되어 **new** MemoryMemberRepository() 호출   #3



=> 3번의 memberRepository() 호출이 있어서 3개의 컨테이너가 생성될거 같지만 memberRepository는 하나만 생성됨

- memberRepository 인스턴스는 하나의 인스턴스가 공유되어 사용

##### 테스트 코드

```java

```



직접 출력해서 확인해보자

```java
public class AppConfig {

    @Bean   // 메서드들이 스프링 컨테이너에 등록
    public MemberService memberService() {
        System.out.println("call AppConfig.memberService");
        return new MemberServiceImpl(memberRepository());
    }
    @Bean
    public MemberRepository memberRepository() {
        System.out.println("call AppConfig.memberRepository");
        return new MemoryMemberRepository();
    }
    @Bean
    public OrderService orderService() {
        System.out.println("call AppConfig.orderService");
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }
```

- 예상
  1. call AppConfig.memberService -> return memberRepository()
  2. call AppConfig.memberRepository
  3. call AppConfig.memberRepository -> bean 생성
  4. call AppConfig.orderService -> bean 생성 + return memberRepository() 
  5. call AppConfig.memberRepository 

- 실제 출력
  1. call AppConfig.memberService
  2. call AppConfig.memberRepository
  3. call AppConfig.orderService





### @Configuration과 바이트코드 조작의 마법



##### AppConfig 출력

```java
@Test
void configurationDeep() {
    // AppConfig도 AnnotationConfigApplicationContext() 인자로 넘기면 bean으로 등록
    ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
    AppConfig bean = ac.getBean(AppConfig.class);
    // AppConfig 조회
    System.out.println("bean = " + bean.getClass());
    }
```

- 출력값: `bean = class hello.core.AppConfig$$EnhancerBySpringCGLIB$$fb31b756`

- 순수한 클래스라면 `bean = class hello.core.AppConfig`로 출력되야 함

  - 왜 다르게 출력될까?

    `@Configuration`이 `AppConfig` 클래스를 상속받은 임의의 클래스를 스프링 빈에 등록했기 때문임

    => 임의의 다른 클래스가 싱글톤을 보장되게 함

- 정리

  @Bean이 붙은 메서드마다 이미 스프링 빈에 존재하면 존재하는 빈 반환

  스프링 빈이 없으면 생성해서 스프링 빈으로 등록하고 반환

  => 싱글톤 보장



##### @Configuration을 빼버린다면?

- `bean = class hello.core.AppConfig`로 출력

- 위에서 예상한데로 `MemberRepository`가 3번 호출되어 아래와 같이 출력

  call AppConfig.memberService
  call AppConfig.memberRepository
  call AppConfig.orderService
  call AppConfig.memberRepository
  call AppConfig.memberRepository

  => 각각 다른 인스턴스가 되어 싱글톤 패턴이 깨진다

























































