package hello.core.scope;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.security.Provider;

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
        private ApplicationContext ac;

        public int logic() {
            PrototypeBean prototypeBean.addCount();
            int count = prototypeBean.getCount();
            return count;
        }
    }

    static class PrototypeBean {
        @Autowired
        private Provider<PrototypeBean> Provider;

        public int logic() {
            PrototypeBean prototypeBean = Provider.get();
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
