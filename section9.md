# section9





### 빈 스코프

빈이 존재할 수 있는 범위

- 싱글톤: 컨테이너의 시작과 끝 동안 빈을 유지시키는 가장 넓은 범위의 스코프, 기본 스코프
- 프로토타입: 빈의 생성과 의존관계 주입만 관여하고 더는 관리x, 따로 종료 메서드`@PreDestroy` 호출 x
- 웹 관련 스코프
  - request: 클라이언트 요청이 들어올 때 생성되고 고객 요청이 나갈 때 close 되는 스코프
  - session: 웹 세션이 생성되고 종료될 때까지 유지
  - application: 웹의 서블릿 컨텍스와 같은 범위로 유지





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

1.을 사용하면 스프링 컨테이너에 종속적인 코드가 되고 테스트 어려워짐

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

2의 `ObjectProvider`은 스프링에 의존하지만 `Provider`는 자바 표준을 사용하는 법

gradle에 추가 `implementation 'javax.inject:javax.inject:1'` 후 사용 가능





### 웹 스코프

- 웹스코 종류
  1. request
  2. session
  3. application
  4. websocket



##### request 스코프 예제 만들기

+ 웹 환경 추가

  gadle에 web 라이브러리 추가

  `implementation 'org.springframework.boot:spring-boot-starter-web'`

+ http 요청 로그를 찍어보자





































