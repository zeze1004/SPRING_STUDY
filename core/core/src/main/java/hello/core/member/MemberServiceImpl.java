package hello.core.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
// BAD
// MemberServiceImpl가 인터페이스인 MemberRepository에도 의존, 구현체인 MemoryMemberRepository에도 의존
public class MemberServiceImpl<get> implements MemberService {
    // 인터페이스와 구현객체 연결
    private  final MemberRepository memberRepository;

    @Autowired // MemberRepository 타입에 맞는 애를 자동으로 연결시켜줌 자동으로 ac.getBean(MemberRepository.class) 코드가 들어간다고 생각하면 됨
    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }

    // 테스트 용도: 직접 꺼내보기
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}
