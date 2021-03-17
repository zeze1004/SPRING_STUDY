package hello.core.scan.filter;

import java.lang.annotation.*;

// @MyExcludeComponent가 붙으면 componentScan에서 제외
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyExcludeComponent {
}
