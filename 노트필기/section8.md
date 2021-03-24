# section8



### 빈 생명주기 콜백 시작

```java
public class NetworkClient {
    private String url;

    public NetworkClient() {
        System.out.println("url = " + url);
        connect();
        call("초기화 연결 메세지");
    }

    public void setUrl(String url) {
        this.url = url;
    }
    // 서비스 시작시 호출
    public void connect() {
        System.out.println("connect: " + url);
    }
    public void call(String message) {
        System.out.println("call: " + url +"message = " + message);
    }
    // 서비스 종료시 호출
    public void disconnect() {
        System.out.println("close: " + url);
    }
}
```

##### `NetworkClient` 테스트 코드

```java
public class BeanLifeCycleTest {

    @Test
    public void lifeCycleTest() {
        ConfigurableApplicationContext ac = new AnnotationConfigApplicationContext(LifeCycleConfig.class);
        NetworkClient client = ac.getBean(NetworkClient.class);
    }
    @Configuration
    static class LifeCycleConfig {
        @Bean()
        // return 값이 networkClient이란 이름의 bean이 된다
        public NetworkClient networkClient() {
            NetworkClient networkClient = new NetworkClient();
            networkClient.setUrl("http://zeze.zeze");
            return networkClient;
        }
    }
}
```



### IntitializingBean, DispoableBean 인테페이스란

- 인터페이스를 사용해서 bean 생성, 스프링 종료 시점을 알아보자

  => 스프링 초기에 만들어진 방법이므로 요즘에는 거의 사용 x

```java
package hello.core.lifecycle;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class NetworkClient implements InitializingBean, DisposableBean {
    private String url;

    public NetworkClient() {
        System.out.println("url = " + url);
        connect();
        call("초기화 연결 메세지");
    }

    public void setUrl(String url) {
        this.url = url;
    }
    // 서비스 시작시 호출
    public void connect() {
        System.out.println("connect: " + url);
    }
    public void call(String message) {
        System.out.println("call: " + url +"message = " + message);
    }
    // 서비스 종료시 호출
    public void disconnect() {
        System.out.println("close: " + url);
    }

    // 의존관계 주입이 끝나면 호출
   @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("NetworkClient.afterPropertiesSet");
        connect();
        call("초기화 연결 메세지");
    }

    // 스프링 종료 전에 호출
    @Override
    public void destroy() throws Exception {
        System.out.println("NetworkClient.destroy");
        disconnect();
    }
}

```

- InitializingBean 은 afterPropertiesSet() 메서드로 초기화를 지원
- DisposableBean 은 destroy() 메서드로 소멸을 지원



### 빈 등록 초기화, 소멸 메소드

```java
public class NetworkClient {
    ...
    public void init() throws Exception {
        System.out.println("NetworkClient.afterPropertiesSet");
        connect();
        call("초기화 연결 메세지");
    }

    public void close() throws Exception {
        System.out.println("NetworkClient.destroy");
        disconnect();
    }
}
```



##### 설정 정보에 초기화 소멸 메서드 지정

```java
public class BeanLifeCycleTest {

    @Test
    public void lifeCycleTest() {
        ConfigurableApplicationContext ac = new AnnotationConfigApplicationContext(LifeCycleConfig.class);
        NetworkClient client = ac.getBean(NetworkClient.class);
    }
    @Configuration
    static class LifeCycleConfig {
        @Bean(initMethod = "init", destroyMethod = "close")
        // return 값이 networkClient이란 이름의 bean이 된다
        public NetworkClient networkClient() {
            NetworkClient networkClient = new NetworkClient();
            networkClient.setUrl("http://zeze.zeze");
            return networkClient;
```

- `@Bean(initMethod = "init", destroyMethod = "close")`

- `@Bean의 destroyMethod` 는 기본값이 `inferred(추론)`으로 등록

  `close`, `shutdown` 이름의 메서드를 종료 메서드로 추론해서 호출

  => 실제로 주로 `close` 를 메서드 이름으로 사용

  



### 애노테이션 @PostConstruct, @PreDestory

**이 방법을 쓰자!**

`@PostConstruct`: 생성되기 전에

`@PreDestory`: 소멸되기 전에

```java
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class NetworkClient {
	...
        
    @PostConstruct
    public void init() throws Exception {
        System.out.println("NetworkClient.afterPropertiesSet");
        connect();
        call("초기화 연결 메세지");
    }

    @PreDestroy
    public void close() throws Exception {
        System.out.println("NetworkClient.destroy");
        disconnect();
    }
}

// 설정코드
@Configuration
    static class LifeCycleConfig {
        @Bean()
        ...
```

`javax`: 스프링에만 종속되는게 아니라 모든 자바 코드에서 사용 가능

외부 라이브러리에 적용x

=> 코드를 고칠 수 없는 외부라이브러리를 사용할 때는 `initMethod` , `destroyMethod` 사용하자

