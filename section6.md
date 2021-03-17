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

@Component : 컴포넌트 스캔에서 사용
@Controlller : 스프링 MVC 컨트롤러에서 사용
@Service : 스프링 비즈니스 로직에서 사용
@Repository : 스프링 데이터 접근 계층에서 사용
@Configuration : 스프링 설정 정보에서 사용



##### 컴포넌트 스캔 부가 기능

- 애노테이션은 메타정보로, 애노테이션이 있으면 부가 기능도 수행함

@Controller : 스프링 MVC 컨트롤러로 인식
@Repository : 스프링 데이터 접근 계층으로 인식하고, 데이터 계층의 예외를 스프링 예외로 변환

=> 다른 디비로 바꿔도 예외처리를 따로 하지 않도로 스프링 예외로 변환 시킴

@Configuration : 앞서 보았듯이 스프링 설정 정보로 인식하고, 스프링 빈이 싱글톤을 유지하도록 추가 처리를 한다.
@Service : @Service는 따로 처리 x

​	=>  대신 개발자들이 핵심 비즈니스 로직이 여기에 있겠구나 라고 비즈니스 계층을 인식하는데 도움을 줌







### 필터

