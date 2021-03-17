package hello.core.common;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.UUID;

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
