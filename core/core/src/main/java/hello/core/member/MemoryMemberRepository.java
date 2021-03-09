package hello.core.member;

import java.util.HashMap;
import java.util.Map;

public class MemoryMemberRepository implements MemberRepository {
    
    // id 넣으면 찾기
    private static Map<Long, Member> store = new HashMap<>();
    // HashMap은 동시성 이슈가 있어서 실무에서 사용 x
    
    @Override
    public void save(Member member) {
        store.put(member.getId(), member);
    }

    @Override
    public Member findById(Long memberId) {
        return store.get(memberId);
    }
}
