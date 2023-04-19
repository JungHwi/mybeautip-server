package com.jocoos.mybeautip.domain.company.converter;

import com.jocoos.mybeautip.domain.company.persistence.domain.CompanyAccount;
import com.jocoos.mybeautip.global.vo.AccountVo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompanyAccountConverter {

    CompanyAccount converts(AccountVo account);

    List<CompanyAccount> converts(List<AccountVo> accountList);
}
