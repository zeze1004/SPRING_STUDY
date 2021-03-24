# section9





### 빈 스코프

빈이 존재할 수 있는 범위

- **싱글톤**: 컨테이너의 시작과 끝 동안 빈을 유지시키는 가장 넓은 범위의 스코프, 기본 스코프
- 프로토타입: 빈의 생성과 의존관계 주입만 관여하고 더는 관리x, 따로 종료 메서드`@PreDestroy` 호출 x
- **웹 관련 스코프**
  - `request`: 클라이언트 요청이 들어올 때 생성되고 고객 요청이 나갈 때 close 되는 스코프
  - `session`: 웹 세션이 생성되고 종료될 때까지 유지
  - `application`: 웹의 서블릿 컨텍스와 같은 범위로 유지





### 프로토타입 스코프

싱글톤 스코프와 반대로 스프링 컨테이너에 조회시 새로운 인스턴스 생성해서 반환

클라이언트가 요청하면 프로토타입 빈 생성하고 클라이언트에게 반환



##### 싱글톤 스코프

```java
public class SingletonTest {
    @Test
    void singletonBeanFind() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(SingletonBean.class);
        SingletonBean singletonBean1 = ac.getBean(SingletonBean.class);
        SingletonBean singletonBean2 = ac.getBean(SingletonBean.class);
        System.out.println("singletonBean1 = " + singletonBean1);
        System.out.println("singletonBean2 = " + singletonBean2);
        assertThat(singletonBean1).isSameAs(singletonBean2);
        // 종료
        ac.close();
    }
    // singleton 기본값이어서 굳이 안적어도 됨
    @Scope("singleton")
    static class SingletonBean {
        @PostConstruct
        public void init() {
            System.out.println("SingletonBean.init");
        }
        @PreDestroy
        public void destroy() {
            System.out.println("SingletonBean.destroy");
        }
    }
}
```

- 빈 생성, 종료 확인할 수 있음



##### 프로토타입 스코프

```java
public class PrototypeTest {
    @Test
    void singletonBeanFind() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class);
        System.out.println("find bean1");
        PrototypeBean bean1 = ac.getBean(PrototypeBean.class);
        System.out.println("find bean2");
        PrototypeBean bean2 = ac.getBean(PrototypeBean.class);
        System.out.println("bean1 = " + bean1);
        System.out.println("bean2 = " + bean2);
        assertThat(bean1).isNotSameAs(bean2);

        ac.close();
    }

    @Scope("prototype")
    static class PrototypeBean {
        @PostConstruct
        public void init() {
            System.out.println("prototype.init");
        }
        @PreDestroy
        public void destroy() {
            System.out.println("prototype.destroy");
        }
    }
}
```

- 종료 메서드 실행되지 x
- 생성 할 때마다 다른 컨테이너로 생성
- 프로토타입 빈은 스프링 컨테이너가 관리하지 않고 빈을 조회한 클라이언트가 관리해야 함
- `@PreDestory` 종료 메서드 호출되지 x





### 프로토타입, 싱글톤 스코프와 함께 쓸 시 문제점

- 기본 스코프가 싱글톤이므로 싱글톤 빈이 프로토타입 빈을 사용하게 됨

  프로토타입 빈이 새로 생기지만 싱글톤 빈과 함께 유지되는 문제점이 있음

- 가령 싱글톤 스코프 안에 프로토타입 생성시

  첫 클라이언트가 프로토타입 빈 호출하면 그 때 새로 생성,

  다음 클라이언트가 프로토타입 빈 호출해도 싱글톤 스코프에 계속 존재하므로 새로 생성x

  => 클라이언트들은 같은 프로토타입 빈을 호출하게 됨



### @Provider로 해결하자!

싱글톤 빈과 프로토타입 빈을 함께 사용시 클라이언트가 요청할 때마다 새로운 프로토타입 빈을 생성하는 방법은 무엇일까🤔



##### 1. 스프링 컨테이너에 요청

싱글톤 빈이 프로토타입 스코프를 사용할 때 마다 스프링 컨테이너에 새로 요청하는 것

- `Dependency Lookup(DL)`: 의존관계를 외부에서 주입(DI) 받는 것이 아니라 직접 필요한 의존관계 찾는 것



##### 2. ObjectFactory, ObjectProvider

(1.)을 사용하면 스프링 컨테이너에 종속적인 코드가 되고 테스트 어려워짐

- `ObjectProvider`:  지정한 빈을 컨테이너에서 대신 찾아주는`DL` 서비스 제공

  과거의 `ObjectFactory`의 업그레이드 버전

  ```java
  public class PrototypeProviderTest {
      @Test
      void providerTest() {
          AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ClientBean.class, PrototypeBean.class);
  
          ClientBean clientBean1 = ac.getBean(ClientBean.class);
          int count1 = clientBean1.logic();
  
          ClientBean clientBean2 = ac.getBean(ClientBean.class);
          int count2 = clientBean2.logic();
      }
      static class ClientBean {
          @Autowired
          private ObjectProvider<PrototypeBean> prototypeBeanProvider;
  
          public int logic() {
              PrototypeBean prototypeBean = prototypeBeanProvider.getObject();
              prototypeBean.addCount();
              int count = prototypeBean.getCount();
              return count;
          }
          @Scope("prototype")
          static class PrototypeBean {
              private int count = 0;
              public void addCount() {
                  count++;
              }
              public int getCount() {
                  return count;
              }
  
              @PostConstruct
              public void init() {
                  System.out.println("PrototypeBean.init");
              }
              @PreDestroy
              public void destroy() {
                  System.out.println("PrototypeBean.destroy");
              }
          }
      }
  }
  ```



##### 3. Provider

(2.)의 `ObjectProvider`은 스프링에 의존하지만 `Provider`는 자바 표준을 사용하는 법

gradle에 `implementation 'javax.inject:javax.inject:1'` 추가 후 사용 가능





### 웹 스코프

- 웹스코 종류
  1. **request**

     - HTTP 요청 하나가 들어오고 나갈 때 까지 유지되는 스코프

     - HTTP 요청마다 별도의 빈인스턴스가 생성되고 관리

       => HTTP 요청이 같으면 같은 객체 할당

  2. **session**

     - HTTP Session과 동일한 생명주기를 가짐

  3. **application**

     - `서블릿 컨텍스트`와 동일한 생명주기를 가지는 스콮

  4. **websocket**

     - 웹 소켓과 동일한 생명주기를 가짐



##### `request` 스코프 예제 만들기

+ 웹 환경 추가

  `gadle`에 web 라이브러리 추가

  `implementation 'org.springframework.boot:spring-boot-starter-web'`

+ **`request` 스코프는 언제 사용할까?**

  - http 요청 로그를 찍을 때 유용함!

    동시에 여러 HTTP 요청이 오면 어떤 요청인지 구분하기 어렵지만 request 스코프가 HTTP별로 구분해서 ⭐**같은 HTTP 요청이면 같은 스프링 빈이 반환**⭐

- 바로 request를 사용할 수 x

  => 싱글톤 빈이 먼저 생성되고 HTTP 요청이 와야 request 스코프가 생성된다

  ​		**그럼 어떻게 싱글톤 스코프와 request 스코프를 함께 사용할 수 있을까?**





### Provider를 사용해서 싱글톤 스코프와 request 스코프를 함게 사용하자

- `ObjectProvider`로 request 빈의 생성을 지연할 수 있음

- `ObjectProvider.getObject()`를 호출하는 시점까지 request 스코프 빈의 생성이 지연되고

  HTTP 요청을 받아 request 스코프 빈의 생성된다

  - `ObjectProvider.getObject()`이 생성되는 시점에서 HTTP 요청이 진행 중

- 코드

  1. MyLogger

     ```java
     @Component
     @Scope(value = "request")
     public class MyLogger {
         private String uuid;
         private String requestURL;
     
         public void setRequestURL(String requestURL) {
             this.requestURL = requestURL;
         }
         public void log(String message) {
             System.out.println(uuid + " " + requestURL + " " + message);
         }
         // 고객요청 들어올 때
         @PostConstruct
         public void init() {
             uuid = UUID.randomUUID().toString();
             System.out.println("MyLogger.init");
         }
         // 고객요청 끝날 때
         @PreDestroy
         public void close() {
             System.out.println("MyLogger.close");
         }
     }
     ```

  2. LogDemoService

     ```java
     @Service
     @RequiredArgsConstructor
     public class LogDemoService {
         private final ObjectProvider<MyLogger> myLoggerProvider;
         private  final MyLogger myLogger;
     
         public void logic(String id) {
             MyLogger myLogger = myLoggerProvider.getObject();
             myLogger.log("service id = " + id);
         }
     }
     ```

  

  

### Provider보다 간편한 프록시 방법을 알아보자

처음에 바로 request 스코프를 사용할려니 싱글톤 빈이 먼저 생성되어 오류가 났다

프록시를 사용하면 처음 코드 그대로 사용 가능하다

- `@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)`

  **proxyMode 추가**

- `ObjectProvider`를 사용하지 않아도 동작!

  - MyLogger에 `@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)` 추가

  - LogDemoService

    ```java
    @Service
    @RequiredArgsConstructor
    public class LogDemoService {
    
        private  final MyLogger myLogger;
    
        public void logic(String id) {
            myLogger.log("service id = " + id);
        }
    }
    ```

    ✨깔끔✨



- 무슨 원리일까?

  **CGLIB** 라이브러리로 MyLogger를 상속 받은 가짜 프록시 객체를 만들어서 주입

  - `MyLogger`를 상속 받은 가짜 프록시 객체를 생성, HTTP request와 상관 없이 가짜 프록시 클래스를 다른 빈에 미리 주입해둔다

  - 스프링 컨테이너에는 `MyLogger`를 상속 받은 가짜 프록시 객체를 등록

    => 따라서 의존관계 주입도 가짜 프록시 객체가 주입되는 것임

  - HTTP 요청이 오면 가짜 프록시 객체가 상속 받은 진짜 빈인 `MyLogger`를 호출함

    => 따라서 **request scope와 상관없이**, 호출되면 진짜 빈을 부르는 위임 로직만 있어 싱글톤처럼 	동작함

  - 클라이언트는 가짜 프록시 객체를 호출 했지만 클라이언트는 원본인지 가짜인지 알 수 없다

    => **다형성의 힘💪**



- 주의점

  - 싱글톤처럼 동작하지만 싱글톤과 다르게 동작하므로 유지보수가 어려움

    => 이러한 특별한 scope는 필요한 곳에 최소로 사용하자!















































