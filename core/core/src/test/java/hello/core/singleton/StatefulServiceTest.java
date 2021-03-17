package hello.core.singleton;

import hello.core.beanfind.ApplicationContextExtendsFindTest.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StatefulServiceTest {

    @Test
    void statefulServiceSingleton() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);
        StatefulService statefulService1 = ac.getBean(StatefulService.class);
        StatefulService statefulService2 = ac.getBean(StatefulService.class);
        // ThreadA: A 사용자 10000원 주문
        // ThreadB: B 사용자 20000원 주문
        // 지역 변수이므로 userAprice, userBprice의 값이 다름
        int userAprice = statefulService1.order("userA", 10000);
        int userBprice = statefulService2.order("userB", 20000);

        // ThreadA: A 사용자 주문 금액 조회
//        int userAprice = statefulService1.getPrice();
//        // 10000원이 나오길 기대했지만 20000원 출력
        System.out.println("A price = " + userAprice);

        assertThat(statefulService1).isSameAs(statefulService2);
    }
    // TestConfig 스프링 컨테이너는 StatefulService Bean하나만 생성해서 사용
    static class TestConfig {

        @Bean
        public StatefulService statefulService() {
            return new StatefulService();
        }
    }
}