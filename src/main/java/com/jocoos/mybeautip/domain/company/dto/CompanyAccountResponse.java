package com.jocoos.mybeautip.domain.company.dto;

public record CompanyAccountResponse(long id,
                                     String bankName,
                                     String accountNumber,
                                     String ownerName) {
}
