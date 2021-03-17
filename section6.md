# section6





### 컴포넌트 스캔과 의존관계 자동 주입

- bean 일일이 적지 않아도 자동으로 스프링 빈 등록하는 컴포넌트 스캔 기능 있음

  `@Component`가 붙으면 bean으로 자동 등록

- 의존관계 자동으로 주입하는 `@Autowired`기능도 제공





### 탐색 위치와 기본 스캔 대상

- 탐색 시작 위치 지정: `basePackages`

  ```java
  package hello.core;
  
  @Configuration
  // 자동으로 스프링 빈 등록
  @ComponentScan(
          // 탐색할 패키지 시작 위치 지정
          basePackages = "hello.core.member",
          // 빈 등록 제외할 것들 설정
          // @Configuration이 붙은 설정 정보도 자동으로 등록되어 AppConfig도 빈으로 등록되어 제외
          // 보통 @Configuration를 컴포넌트 스캔에서 제외하지는 않지만 AppConfig에 bean을 일일이 등록해놨기 때문에 제외한 것
          excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class))
  
  ```

  - 지정하지 않으면 모든 자바 파일을 탐색하므로 오래걸림

  - 지정하지 않을 때:

    `@ComponentScan`이 붙은 설정 정보 클래스의 패키지(`hello.core`)가 시작 위치



- 권장하는 방법

  탐색 위치 지정하지 않고 설정 정보 클래스의 위치를 프로젝트 최상단에 두는 것

  => 처음 스프링부트  프로젝트 생성할 때 `@SpringBootApplication`에 `@Component`있어서

  ​	 `@Component` 안 써도 됨...?



##### 컴포넌트 스캔 기본 대상

`@Component` : 컴포넌트 스캔에서 사용

`@Controlller` : 스프링 MVC 컨트롤러에서 사용

`@Service` : 스프링 비즈니스 로직에서 사용

`@Repository` : 스프링 데이터 접근 계층에서 사용

`@Configuration` : 스프링 설정 정보에서 사용



##### 컴포넌트 스캔 부가 기능

- 애노테이션은 메타정보로, 애노테이션이 있으면 부가 기능도 수행함

  `@Controller` : 스프링 MVC 컨트롤러로 인식

  `@Repository` : 스프링 데이터 접근 계층으로 인식하고, 데이터 계층의 예외를 스프링 예외로 변환

  ​	=> 다른 디비로 바꿔도 예외처리를 따로 하지 않도로 스프링 예외로 변환 시킴

  `@Configuration` : 스프링 설정 정보 인식하고, 스프링 빈이 싱글톤을 유지하도록 처리
  `@Service` : @Service는 따로 처리 x

  ​	=>  대신 개발자들이 여기가 핵심 비즈니스 로직임을 인식하는데 도움을 줌





### 필터

- `includeFilters`: `@ComponentScan` 사용시 bean 등록
- `excludeFilters`: bean 등록x

=> 어노테이션을 직접 등록할 수 있는 기능



##### 어노테이션 직접 설정

```java
// @MyIncludeComponent가 붙으면 componentScan에 등록
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyIncludeComponent {
}
```

```java
// BeanA
@MyIncludeComponent
public class BeanA {
}
// BeanB는 @MyExcludeComponent 붙인 채 클래스 생성
```

##### 생성한 어노테이션 테스트 코드

```java
// 나만의 컴포넌트 스캔 필터 기능 생성
public class ComponentFilterAppConfigTest {
    @Test
    void filterScan() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(ComponentFilterAppConfig.class);
        BeanA beanA = ac.getBean("beanA", BeanA.class);
        // BeanA는 @MyIncludeComponent 어노테이션이 붙어서 includeFilters 되므로 BEAN 등록
        assertThat(beanA).isNotNull();
        // BeanB는 @MyExcludeComponent 붙어서 bean에 등록 되지 않았으므로 예외처리
        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> ac.getBean("beanB", BeanB.class)
        );
    }
    @Configuration
    @ComponentScan(
            // @Filter(type = FilterType.ANNOTATION, ...) 기본값이어서 지워도 됨
            includeFilters = @Filter(classes = MyIncludeComponent.class),
            excludeFilters = @Filter(type = FilterType.ANNOTATION, classes = MyExcludeComponent.class)
    )
    static class ComponentFilterAppConfig {

    }
}
```

- `@Component`로 대부분 처리하므로 필터를 쓸 일이 많지 x





### 중복 등록과 충돌

컴포넌트 스캔에서 같은 빈 이름 등록시 생기는 일

1. 자동 빈 등록 vs 자동 빈 등록
2. 수동 빈 등록 vs 자동 빈 등록



##### 1. 자동 빈 등록 vs 자동 빈 등록

컴포넌트 스캔에서 자동으로 빈 등록시 빈 이름이 서로 같은 경우 `ConflictingBeanDefinitionExcepion` 예외 발생

=> 자주 없는 상황



##### 2. 수동 빈 등록 vs 자동 빈 등록

수동 빈 등록이 우선권을 가짐

그러나 이러한 상황이 여러 번 생기면 설정들이 꼬이므로

최근 스프링 부트는 수동 빈 등록과 자동 빈 등록 충돌시 오류가 발생하도록 기본 값 바꿈

```
// 에러 로그
Consider renaming one of the beans or enabling overriding by setting
spring.main.allow-bean-definition-overriding=true
```



















