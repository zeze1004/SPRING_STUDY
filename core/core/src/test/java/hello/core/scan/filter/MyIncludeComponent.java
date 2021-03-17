package hello.core.scan.filter;

import java.lang.annotation.*;

// @MyIncludeComponent가 붙으면 componentScan에 등록
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyIncludeComponent {
}
