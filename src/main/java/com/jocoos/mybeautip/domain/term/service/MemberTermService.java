package com.jocoos.mybeautip.domain.term.service;

import com.jocoos.mybeautip.domain.term.code.TermType;
import com.jocoos.mybeautip.domain.term.code.TermTypeGroup;
import com.jocoos.mybeautip.domain.term.converter.MemberTermConverter;
import com.jocoos.mybeautip.domain.term.converter.TermConverter;
import com.jocoos.mybeautip.domain.term.dto.MemberTermResponse;
import com.jocoos.mybeautip.domain.term.dto.TermResponse;
import com.jocoos.mybeautip.domain.term.dto.TermTypeResponse;
import com.jocoos.mybeautip.domain.term.persistence.domain.MemberTerm;
import com.jocoos.mybeautip.domain.term.persistence.domain.Term;
import com.jocoos.mybeautip.domain.term.persistence.repository.MemberTermRepository;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.domain.term.code.TermTypeGroup.validTypeOptional;
import static com.jocoos.mybeautip.global.util.CollectionConvertUtil.toMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberTermService {

    private final TermService termService;
    private final TermConverter termConverter;
    private final TermHistoryService termHistoryService;
    private final MemberTermRepository memberTermRepository;
    private final MemberTermConverter memberTermConverter;


    @Transactional(readOnly = true)
    public List<TermResponse> getChangedTerms(long memberId) {

        if (!isTermsUpdate(memberId))
            return Collections.emptyList();

        List<MemberTerm> memberTerms = memberTermRepository.findAllByMemberId(memberId);
        Map<Long, Term> terms = toMap(termService.getAllTerms(), Term::getId);

        List<Long> changedTermIds = getChangedTermIds(memberTerms, terms);

        List<Long> requiredChangeTermIds = termHistoryService.getRequiredChangeTermIds(changedTermIds);
        return termConverter.convertToListResponse(termService.getTerms(requiredChangeTermIds));
    }

    @Transactional
    public List<MemberTermResponse> chooseTerms(List<Long> requestTermIds) {
        List<Term> terms = termService.getTerms(requestTermIds);


        Map<Long, Term> termMap = toMap(terms, Term::getId);
        List<MemberTerm> memberTerms = createMemberTerms(requestTermIds, termMap);

        // TODO JDBC 벌크 쿼리 설정
        return memberTermConverter.convertToListResponse(memberTermRepository.saveAll(memberTerms));
    }

    @Transactional
    public List<MemberTermResponse> chooseUpdateTerms(List<Long> requestTermIds, long memberId) {

        Map<Long, Float> termVersionMap = toMap(termService.getTerms(requestTermIds), Term::getId, Term::getVersion);
        List<MemberTerm> memberTerms = memberTermRepository.findAllByMemberIdAndTermIdIn(memberId, requestTermIds);
        Map<Long, MemberTerm> memberTermMap = toMap(memberTerms, (MemberTerm mt) -> mt.getTerm().getId());
        List<Long> newTermsIds = updateMemberTermsAndFindNewTerms(requestTermIds, memberTermMap, termVersionMap);

        saveNewMemberTermsIfExists(newTermsIds);
        return memberTermConverter.convertToListResponse(memberTerms);
    }

    @Transactional
    public MemberTermResponse changeOptionalTerm(long termId, long memberId, boolean isAccept) {
        MemberTerm memberTerm = getOrCreateMemberTerm(termId, memberId);
        memberTerm.changeAcceptStatus(isAccept);
        memberTermRepository.save(memberTerm);
        return memberTermConverter.convertToResponse(memberTerm);
    }


    private boolean isTermsUpdate(long memberId) {
        MemberTerm memberTerm = getLastUpdateTermOf(memberId);
        Term term = termService.getLastUpdateTerm();
        return memberTerm.isTermDiff(term.getId(), term.getVersion());
    }


    private MemberTerm getLastUpdateTermOf(long memberId) {
        return memberTermRepository.findFirstByMemberIdOrderByModifiedAtDesc(memberId)
                .orElseThrow(() -> new NotFoundException("member term not found, memberId : " + memberId));
    }

    private List<Long> getChangedTermIds(List<MemberTerm> memberTerms, Map<Long, Term> terms) {
        return memberTerms.stream()
                .filter(mt -> {
                    Term term = terms.get(mt.getTerm().getId());
                    if (term == null) return false;
                    else return mt.getVersion() != term.getVersion();
                })
                .map(mt -> mt.getTerm().getId())
                .collect(Collectors.toList());
    }

    private List<Long> updateMemberTermsAndFindNewTerms(
            List<Long> requestTermIds,
            Map<Long, MemberTerm> memberTermMap,
            Map<Long, Float> termVersionMap) {

        List<Long> newTermsIds = new ArrayList<>();

        // TODO JDBC 벌크 쿼리 설정
        for (Long termId : requestTermIds) {
            MemberTerm memberTerm = memberTermMap.get(termId);
            if (memberTerm == null) newTermsIds.add(termId);
            else memberTerm.updateVersion(termVersionMap.get(termId));
        }
        return newTermsIds;
    }

    private void saveNewMemberTermsIfExists(List<Long> newTermIds) {
        if (!CollectionUtils.isEmpty(newTermIds)) {
            List<Term> newTerms = termService.getTerms(newTermIds);
            memberTermRepository.saveAll(createMemberTerms(newTermIds, toMap(newTerms, Term::getId)));
        }
    }

    private List<MemberTerm> createMemberTerms(List<Long> termIds, Map<Long, Term> termMap) {
        return termIds.stream()
                .map(termId -> {
                    Term term = termMap.get(termId);
                    return MemberTerm.builder()
                            .term(term)
                            .version(term.getVersion())
                            .build();
                }).collect(Collectors.toList());
    }

    private MemberTerm getOrCreateMemberTerm(long termId, long memberId) {
        return memberTermRepository
                .findByTermIdAndMemberId(termId, memberId).orElseGet(
                        () -> {
                            Term term = termService.getTerm(termId);
                            return MemberTerm.builder().term(term).version(term.getVersion()).build();
                        }
                );
    }


    // 2021-07-28 현재 서버에서 약관 관리하지 않기 때문에 임시로 만듦, 추후 서버에서 약관 관리한다면 삭제
    @Transactional
    public TermTypeResponse changeOptionalTermByType(TermType type, Long memberId, boolean isAccept) {
        validTypeOptional(type);
        Term term = termService.getTermByType(type);
        MemberTerm memberTerm = getOrCreateMemberTerm(term.getId(), memberId);
        memberTerm.changeAcceptStatus(isAccept);
        memberTermRepository.save(memberTerm);
        return TermTypeResponse.builder().termType(type).isAccept(memberTerm.getIsAccept()).build();
    }

    @Transactional
    public void chooseTermsByTermType(Set<TermType> requestTermTypes, Member member) {
        List<Term> terms = termService.getTermsByType(requestTermTypes);

        Map<TermType, Term> termMap = toMap(terms, Term::getType);
        List<MemberTerm> memberTerms = createMemberTermsByType(requestTermTypes, termMap, member);

        memberTermRepository.saveAll(memberTerms);
    }

    public List<TermTypeResponse> getOptionalTermAcceptStatus(long memberId) {
        List<Term> optionTerms = termService.getTermsByType(TermTypeGroup.OPTIONAL.getTypes());
        List<MemberTerm> memberTerms =
                memberTermRepository.findAllByMemberIdAndTermIn(memberId, optionTerms);
        Map<Long, MemberTerm> memberTermMap = toMap(memberTerms, memberTerm -> memberTerm.getTerm().getId());

        return optionTerms.stream().map(term -> {
            MemberTerm memberTerm = memberTermMap.get(term.getId());
            boolean isAccept = memberTerm != null && memberTerm.getIsAccept();
            return TermTypeResponse.builder().termType(term.getType()).isAccept(isAccept).build();
        }).collect(Collectors.toList());

    }

    private List<MemberTerm> createMemberTermsByType(Set<TermType> termTypes,
                                                     Map<TermType, Term> termMap,
                                                     Member member) {
        return termTypes.stream()
                .map(termType -> {
                    Term term = termMap.get(termType);
                    return MemberTerm.builderWithMember()
                            .term(term)
                            .member(member)
                            .version(term.getVersion())
                            .build();
                }).collect(Collectors.toList());
    }
}
