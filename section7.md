# section7





### 다양한 의존관계 주입 방법

1. 생성자 주입
2. 수정자 주입(setter 주입)
3. 필드 주입
4. 일반 메서드 주입



##### 1. 생성자 주입

- 생성자 호출시점에 딱 1번만 호출되는 것이 보장

  => 두 번 호출될 수 x

- 불변, 필수 의존관계에 사용

  1) 불변

  ​	=> 배우를 지정할 수 없음(데이터 수정x)

  2) 필수

  ​	=> 생성자에 들어가는 인자에는 꼭 값을 넣어야 함(꼭 그런 것음 아님)

- 생성자가 딱 1개만 있으면 @Autowired를 생략해도 자동 주입 됨

  ```java
  @Component
  public class OrderServiceImpl implements OrderService {
      // OrderService를 이용하기 위해서는 두 필드 memberRepository, discountPolicy가 필요함
      // final은 생성자를 통해 할당
      private final MemberRepository memberRepository;
      private final DiscountPolicy discountPolicy;
  
      @Autowired
      // 생성자 하나 있으므로 @Autowired 없어도 괜찮음
      public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
          this.memberRepository = memberRepository;
          this.discountPolicy = discountPolicy;
      }
      // 생성자 둘 이상이면 @Autowired 필수
      public OrderServiceImpl2(){
          ...
      }
  ```





##### 2. 수정자 주입

- 선택,변경 가능한 의존관계

  `MemberRepository`가 빈에 없어도 의존관계 사용 가능

  인스턴스 바꾸고 싶을 때 외부에서 다른 인스턴스 호출 가능

```java
@Component
public class OrderServiceImpl implements OrderService {
    // OrderService를 이용하기 위해서는 두 필드 memberRepository, discountPolicy가 필요함
    // final은 생성자를 통해 할당
    private MemberRepository memberRepository;
    private DiscountPolicy discountPolicy;

    @Autowired
    public void setMemberRepository(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    @Autowired
    public void setDiscountPolicy(DiscountPolicy discountPolicy) {
        this.discountPolicy = discountPolicy;
    }
```

- 생성자에서 주입하므로 기존 생성자 코드 삭제

- 값도 변경 가능하도록 final 삭제

- `@Autowired(required = false)`

  주입할 대상이 없어도 동작 되게 할려면 위처럼 지정





##### 3. 필드 주입

```java
@Component
public class OrderServiceImpl implements OrderService {

    @Autowired private MemberRepository memberRepository;
    @Autowired private DiscountPolicy discountPolicy;
    
    public void setMemberRepository(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void setDiscountPolicy(DiscountPolicy discountPolicy) {
        this.discountPolicy = discountPolicy;
    }
...
```

- 필드에서 바로 주입하는 방법

- 외부 변경이 불가능해 테스트 하기 힘들다!

  => 쓰지말자!

  => 필드 말고 수정자 주입으로 쓰는걸 권장

- 사용하는 곳은?

  테스트 코드에서는 사용 가능

  `@Configuration`은 스프링에서만 사용하니 써도 갠춘(?)



##### 4.일반 메서드 주입

- 일반 메서드에 주입

  수정자 주입과 유사

  - 한 번에 여러 필드를 주입 받을 수 있음

  - 잘 사용하지 x

    ```java
    @Component
    public class OrderServiceImpl implements OrderService {
    
        private MemberRepository memberRepository;
        private DiscountPolicy discountPolicy;
        
        @Autowired
        public void method(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
            this.memberRepository = memberRepository;
            this.discountPolicy = discountPolicy;
        }
    ...
    ```

    





### 옵션 처리

스프링 빈이 없어도 동작해야 할 때는 어떻게 할까



- `@Autowired`는 주입 대상이 없으면 오류 발생

  `@Autowired(required = false)` 으로 설정시 자동 주입 대싱 없어도 오류x



```java
public class AutowiredTest {
    @Test
    void AutowiredOption() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(TestBean.class);
    }
    static class TestBean {
		// #1
        @Autowired(required = true)
        // Member는 스프링 빈에서 관리하는 컨테이너 x
        public void setNoBean1(Member member) {
            System.out.println("setNoBean1 = " + member);
        }
		// #2
        @Autowired
        public void setNoBean2(@Nullable Member member) {
            System.out.println("setNoBean2 = " + member);
        }
		// #3
        @Autowired(required = false)
        public void setNoBean3(Optional<Member> member) {
            System.out.println("setNoBean3 = " + member);
        }
    }
}
```

- #1

  Member는 스프링 빈에서 관리하는 컨테이너가 아니므로 test 실패

- #2

  호출은 되나 member에 null 값이 들어옴

- #3

  `Optional<>`: java8 문법으므로 자동 주입할 대상 없으면 `Optional.empty` 입력





### 생성자 주입을 권장하는 이유

왜 생성자 주입을 주로 사용할까?



- **불변**

  - 대부분의 의존관계 주입은 애플리케이션 시작, 종료까지 의존관계 변경할 일 x

    => 공연 시작 전에 배우들의 배역이 다 정해진 상태

  - 수정자 주입 사용시 문제점

    `setXxx` 메서드를 public으로 열어둬야 하므로 실수로 변경할 수 있음

    계속 호출 될 수 있으므로 실수할 가능성이 높음

    반면 생성자 주입은 1번만 호출됨

    

- 순수 자바 코드로 테스트 가능

  ```java
  class OrderServiceImplTest {
  
      // 순수한 자바 코드로 테스트
      @Test
      void createOrder() {
          MemoryMemberRepository memberRepository = new MemoryMemberRepository();
          memberRepository.save(new Member(1L, "ZEZE", Grade.VIP));
  
          OrderServiceImpl orderService = new OrderServiceImpl(memberRepository, new FixDiscountPolicy());
          orderService.createOrder(1L, "itemA", 10000);
      }
  }
  ```

  

- `final` 키워드 사용 가능

  생성자에서 초기값이 정해지면 값이 변경되지 x

  생성자에서 값이 설정되지 않는 오류를 `final`이 잡아줌

  ```java
  public class OrderServiceImpl implements OrderService {
      // OrderService를 이용하기 위해서는 두 필드 memberRepository, discountPolicy가 필요함
      // final은 생성자를 통해 할당
      private final MemberRepository memberRepository;
      private final DiscountPolicy discountPolicy;
  
      @Autowired
      // 생성자
      public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
          this.memberRepository = memberRepository;
          // 코드 누락되면 final 때문에 컴파일 오류 생김
          // this.discountPolicy = discountPolicy;
      }
  ```

  

  ##### 결론: 생성자 주입를 쓰자





### 롬북과 최신 트렌드



##### 롬북 사용 전후

```java
// 전
public class HelloLombok {
    private String name;
    private int age;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }    

// 후
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ToString
public class HelloLombok {
    private String name;
    private int age;

    public static void main(String[] args) {
        HelloLombok helloLombok = new HelloLombok();
        helloLombok.setName("zeze");
		// @ToString이 자동으로 문자 만들어줌
        // String name = helloLombok.getName();
        // System.out.println("name = " + name);
        System.out.println("name = " + helloLombok);      
    }
}
```



- 생성자 작성X

  `@RequiredArgsConstructor`가 `final` 변수들로 생성자 메소드 자동 생성

  ```java
  @Component
  @RequiredArgsConstructor
  public class OrderServiceImpl implements OrderService {
      // OrderService를 이용하기 위해서는 두 필드 memberRepository, discountPolicy가 필요함
      // final은 생성자를 통해 할당
      private final MemberRepository memberRepository;
      private final DiscountPolicy discountPolicy;
  
      // 생성자 -> @RequiredArgsConstructor 때문에 지워도 ㅇㅋ
  //    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
  //        this.memberRepository = memberRepository;
  //        this.discountPolicy = discountPolicy;
  //    }
  
  ```

  - 필드 주입만큼 코드가 간결해짐
  - 의존관계 추가시 `private fianl xxx xxx`만 추가하면 됨





### 조회 빈이 2개 이상일 시 문제점

- `@Autowired`는 타입(type)으로 조회

  => `ac.getBean()`과 유사하게 동장

- 타입으로 조회하면 같은 타입의 하위 타입이 2개 이상일 때 스프링 빈으로 선언시 문제 발생

  EX) `DiscountPolicy`의 하위 타입인 `FixDiscountPolicy`, `RateDiscountPolicy` 둘 다 빈 등록시

  `NoUniqueBeanDefinitionException` 오류 발생





### 해결 방법

1. @Autowired 필드 명 매칭
2. @Qualifier => @Qualifier끼리 매칭 => 빈 이름 매칭
3. @Primary 사용



##### 1. @Autowired 필드 명 매칭

- `@Autowired`: 타입 매칭 시도 후 하위 타입에 여러 빈 있으면 필드 이름, 파라미터 이름을 빈 이름에 추가 매칭

  - 타입이 하나면은 바로 의존관계 주입

  - 타입 매칭 결과가 2개 이상이면 필드명, 파라미터 명으로 빈 이름 매칭

    => 같은 이름이 있으면 의존관계 주입



##### 2. @Quilifier 사용

- 추가 구분자를 붙여주는 방식

- 빈 등록시 `@Qualifier` 붙여 줌

  ```java
  @Component
  @Qualifier("mainDiscountPolicy")
  public class RateDiscountPolicy implements DiscountPolicy {....}
  ```

- `@Qualifier("명칭")`의 "명칭"끼리 매칭

- "명칭"을 가진 `@Qualifier()`가 없을 시 "명칭"을 가진 빈 이름에 매칭

- 그래도 없을 때에는 `NoSuchBeanDefinitionException` 예외 발생

 

##### 3. @Primary 사용✨

- `@Autowired` 시 여러 빈 매칭되면 `@Primary`가 우선권을 가짐

- `rateDiscountPolicy`가 우선권을 가지게 만들기

  ```java
  @Component
  @Primary
  public class RateDiscountPolicy implements DiscountPolicy {...}
  @Component
  public class FixDiscountPolicy implements DiscountPolicy {...}
  ```



##### @Primary, @Qualifier 활용

- 메인 데이터베이스 커넥션은  `@Primary`

- 서브 데이터베이스 커넥션을 획득하는 스프링 빈에는 `@Qualifier` 적용

- 우선순위: `@Qualifier`  > `@Primary`

  `@Qualifier` 가 수동으로 직접 설정해야 하므로







### 애노테이션 직접 만들기



