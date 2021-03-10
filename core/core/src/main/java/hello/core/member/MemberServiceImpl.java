package hello.core.member;

// BAD
// MemberServiceImpl가 인터페이스인 MemberRepository에도 의존, 구현체인 MemoryMemberRepository에도 의존
public class MemberServiceImpl implements MemberService {
    // 인터페이스와 구현객체 연결
    private  final MemberRepository memberRepository;

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
}
