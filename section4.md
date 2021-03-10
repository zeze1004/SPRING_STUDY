### section4

----



### 스프링 컨테이너 생성

```java
// 스프링 컨테이너 생성
ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
```

- `ApplicationContext`를 스프링 컨테이너이며 인터페이스이다

  - 애노테이션 기반으로 자바 설정 클래스로 스프링 컨테이너 생성

  - `new AnnotationConfigApplicationContext(AppConfig.class)`: 

    `ApplicationContext` 인터페이스 구현체이다



- Bean 이름 

  1. 메서드 이름 사용

  2. 항상 다른 이름 부여해야 함



##### 스프링 빈 의존관계 설정

1. 준비

   ```java
   public class AppConfig {
   
       @Bean   // 스프링 빈 등록
       public MemberService memberService() {
           return new MemberServiceImpl(memberRepository());
       }
   ...
   ```

   [스프링 컨테이너에 적재]

   memberService 	  🤦‍♀️

   memberRepository 💌

   orderService

   discountPolicy		  💎

2. 완료

   ```java
   	@Bean   // 메서드들이 스프링 컨테이너에 등록
       public MemberService memberService🤦‍♀️() {
           return new MemberServiceImpl(💌);
       }
       @Bean
       public MemberRepository memberRepository💌 {
           return new MemoryMemberRepository();
       }
       @Bean
       public OrderService orderService() {
           return new OrderServiceImpl(💌, 💎);
       }
   
       // DiscountPolicy 추가 -> orderService()의 반환값 인자로 선언해서 한 눈에 역할이 보이게 리팩토링
       @Bean
       public DiscountPolicy discountPolicy💎() {
           return new RateDiscountPolicy();
       }
   ```

   - 스프링 컨테이너는 설정 정보 참고해 의존관계 주입





### 컨테이너에 등록된 모든 빈 조회

- 모든 빈 출력

  ```java
  class ApplicationContextInfoTest {
      AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
  
      @Test
      @DisplayName("모든 빈 출력하기")
      void findAllBean() {
          String[] beanDefinitionNames = ac.getBeanDefinitionNames();
          // iter + tab: 반복문 자동 생성
          for (String beanDefinitionName : beanDefinitionNames) {
              // ac.getBean(): bean 이름으로 bean 객체(인스턴스) 조회
              Object bean = ac.getBean(beanDefinitionName);
              System.out.println("name = " + beanDefinitionNames
              + " object = " + bean);
          }
      }
  ```

- 직접 등록한 빈 출력

  ```java
      @Test
      @DisplayName("애플리케이션 빈 출력하기")
      // 내가 넣은 bean 출력
      void findApplicationBean() {
          String[] beanDefinitionNames = ac.getBeanDefinitionNames();
          // iter + tab: 반복문 자동 생성
          for (String beanDefinitionName : beanDefinitionNames) {
              // getBeanDefinition(beanDefinitionName): bean의 정보를 얻을 수 있음
              BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);
  
              // BeanDefinition.ROLE_APPLICATION: 직접 등록한 애플리케이션 BEAN 혹은 외부 라이브러리리
              // BeanDefinition.ROLE_INFRASTRUCTURE: 스프링 내부에서 사용하는 빈
             if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) {
                 Object bean = ac.getBean(beanDefinitionName);
                 System.out.println("name = " + beanDefinitionNames
                         + " object = " + bean);
              }
          }
  ```

  



### 스프링 빈 조회 - 기본 방식

- `ac.getBean(bean이름, 타입)`

  ```java
  AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
      @Test
      @DisplayName("빈 이름으로 조회")
      void findBeanByName() {
          MemberService memberService = ac.getBean("memberService", MemberService.class);
  //        System.out.println("memberService = " + memberService);
  //        System.out.println("memberService.getClass() = " + memberService.getClass());
          // 검증은 Assertions로!
          // memberService가 MemberServiceImpl의 instance면 테스트 성공
          Assertions.assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
      }
  ```

- `ac.getBean(타입)`

  ```java
  @Test
      @DisplayName("이름 없이 타입으로만 조회")
      void findBeanByType() {
          MemberService memberService = ac.getBean(MemberService.class);
  
          // 검증은 Assertions로!
          // memberService가 MemberServiceImpl의 instance면 테스트 성공
          Assertions.assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
      }
  ```

- bean이 없을 시 test

  ```java
      @Test
      @DisplayName("빈 이름으로 조회X")
      void findBeanByNameX() {
  //        MemberService xxxx = ac.getBean("xxxx", MemberService.class);
          // xxxx란 bean이 없으면 NoSuchBeanDefinitionException에 의해 테스트 성공
          org.junit.jupiter.api.Assertions.assertThrows(NoSuchBeanDefinitionException.class,
                  () -> ac.getBean("xxxx", MemberService.class));
      }
  ```

  



### 스프링 빈 조회 - 동일한 타입이 둘 이상

- 타입으로 빈 조회시 동일 타입의 빈이 둘 이상이면 오류 발생

  => 빈 이름을 지정하자!

- `ac.getBeanOfType()`을 사용하면 해당 타입의 모든 빈 조회 가능

  

- `MemberRepository()` 인스턴스 타입을 `memberRepository1,2`가 중복해서 쓰므로 오류가 남

  == 부모 타입으로 조회시, 자식이 둘 이상 있으면 중복 오류 발생

  ```java
  public class ApplicationContextSameBeanFindTest {
  	AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(SameBeanConfig.class);
  
  	// 오류 발생 TEST
      @Test
      @DisplayName("타입으로 조회시 같이 타입이 둘 이상이면 중복 오류 발생")
      // MemberRepository 타입의 bean을 찾을 것
      void findBeanByTypeDuplicate() {
          MemberRepository bean = ac.getBean(MemberRepository.class);
      }
  
      @Configuration
      // static: ApplicationContextSameBeanFindTest class scope에서만 사용하겠다
      static class SameBeanConfig {
          // 들어가는 인자를 달리해서 Bean 이름은 다르지만 인스턴스가 같은 경우 있음
          @Bean
          public MemberRepository memberRepository1() {
              return new MemoryMemberRepository();
          }
          @Bean
          public MemberRepository memberRepository2() {
              return new MemoryMemberRepository();
          }
      }
  }
  ```

- 중복 오류시 TEST 성공할려면 `NoUniqueBeanDefinitionException` 사용

  ```java
      @Test
      @DisplayName("타입으로 조회시 같이 타입이 둘 이상이면 중복 오류 발생")
      // MemberRepository 타입의 bean을 찾을 것
      void findBeanByTypeDuplicate() {
          assertThrows(NoUniqueBeanDefinitionException.class,
                  () -> ac.getBean(MemberRepository.class));
      }
  ```

  테스트 성공!

- 특정 타입 모두 조회하기

  ```java
      @Test
      @DisplayName("특정 타입 모두 조회하기")
      void findAllBeanByType() {
          Map<String, MemberRepository> beansOfType = ac.getBeansOfType(MemberRepository.class);
          for (String key : beansOfType.keySet()) {
              System.out.println("key  = " + key + " value = " + beansOfType.get(key));
          }
          System.out.println("beansOfType = " + beansOfType);
          // beansOfType에 memberRepository1, memberRepository2 들어있어서 size 2
          assertThat(beansOfType.size()).isEqualTo(2);
      }
  ```

  

### 스프링 빈 조회 - 상속관계

- 부모타입으로 조회하면 자식 타입도 함께 조회

  => 자바 객체의 최고 부모인 `Object`타입으로 조회하면 모든 bean 조회 됨

  ```java
      @Test
      @DisplayName("부모 타입으로 모두 조회")
      void findAllBeanByParentType() {
          Map<String, DiscountPolicy> beansOfType = ac.getBeansOfType(DiscountPolicy.class);
          assertThat(beansOfType.size()).isEqualTo(2);
          // 출력
          for (String key : beansOfType.keySet()) {
              System.out.println("key = " + key + " value = " + beansOfType.get(key));
          }
      }
  
      @Configuration
      static class TestConfig {
          @Bean
          public DiscountPolicy rateDiscountPolicy() {
              return new RateDiscountPolicy();
          }
          @Bean
          public DiscountPolicy fixDiscountPolicy() {
              return new FixDiscountPolicy();
          }
      }
  ```

- `Object` 타입으로 조회

  => 스프링에 등록한 모든 Bean 조회

  ```java
      @Test
      @DisplayName("Object 타입으로 모두 조회")
      void findAllBeanObjectType() {
          Map<String, Object> beansOfType = ac.getBeansOfType(Object.class);
          for (String key : beansOfType.keySet()) {
              System.out.println("key = " + key + " value = " + beansOfType.get(key));
          }
      }
  ```

  

- 실제 개발할 때는 bean을 조회할 일이 없지만 기본 지식이므로 알아두자!





### BeanFactory와 ApplicationContext

두 개를 스프링 컨테이너라고 함

- `BeanFactory`

  - 스프링 컨테이너 최상위 인터페이스

- `ApplicationContext`

  - 빈팩토리 기능 모두 상속받아 제공

    => 빈팩토리를 직접 사용할 일 거의 X

  - 빈팩토리에 없는 부가기능 있음

    1. 메세지소스를 활용한 국제화 기능

       : 들어오는 국가에 따라 언어를 바꿔서 출력

    2. 환경변수

       : 로컬, 개발(테스트 서버), 운영(실제 프로덕션 서버) 등을 구분해서 처리

    3. 애플리케이션 이벤트

       : 이벤트 발행하고 구독하는 모델을 지원

    4. 편리한 리소스 조회

       : 파일, 클래스 패스 등 리소스를 편하게 조회





### 자바가 아닌 XML로 스프링 설정 형식

자바 외에도 XML, Groovy 등으로 설정 형식 만들 수 있음

- 애노테이션 기반 자바 코드 설정

  ```java
  new AnnotationConfigApplicationContext(AppConfig.class);
  ```

- XML

  - 자바 파일이 아닌 모든 파일은 `resources`에 저장

    ##### appConfig.xml

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
        <bean id = "memberService" class="hello.core.member.MemberServiceImpl">
            <!--   생성자도 넘겨줘야 함     -->
            <constructor-arg name="memberRepository" ref = "memberRepository" />
        </bean>
    
        <bean id = "memberRepository" class="hello.core.member.MemoryMemberRepository" />
        <bean id = "orderService" class="hello.core.order.OrderServiceImpl">
            <constructor-arg name="memberRepository" ref = "memberRepository" />
            <constructor-arg name="discountPolicy" ref = "discountPolicy" />
        </bean>
        
        <bean id = "discountPolicy" class="hello.core.discount.RateDiscountPolicy" />
    </beans>
    ```

  - XML 테스트 코드

    ```java
    public class XmlAppContext {
    
        @Test
        void xmlAppContext() {
            ApplicationContext ac = new GenericXmlApplicationContext("appConfig.xml");
            MemberService memberService = ac.getBean("memberService", MemberService.class);
            assertThat(memberService).isInstanceOf(MemberService.class);
        }
    }
    ```

    



### 스프링 빈 설정 메타 정보 - BeanDefinition



























































