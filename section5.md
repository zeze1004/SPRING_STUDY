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



































