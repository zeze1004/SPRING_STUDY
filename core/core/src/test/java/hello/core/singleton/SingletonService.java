package hello.core.singleton;

public class SingletonService {
    // 자기 자신을 내부에 pivate static으로 가지게 됨
    // 1. static 영역에 객체를 딱 1개만 생성해둔다
    private static final SingletonService instance = new SingletonService();

    // 2. 조회
    public  static SingletonService getInstance() {
        return instance;
    }

    private SingletonService() {

    }

    public void logic() {
        System.out.println("싱글톤 객체 로직 호출");
    }

}
