package com.jocoos.mybeautip.domain.delivery.persistence.domain;

import com.jocoos.mybeautip.domain.delivery.code.DeliveryCompanyStatus;
import com.jocoos.mybeautip.domain.delivery.dto.CreateDeliveryCompanyRequest;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.tika.utils.StringUtils;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "delivery_company")
public class DeliveryCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @Column
    private String code;

    @Enumerated(EnumType.STRING)
    private DeliveryCompanyStatus status;

    @Column
    private String name;

    @Column
    private String url;

    public DeliveryCompany(CreateDeliveryCompanyRequest request) {
        validCreate(request);
        this.status = request.status();
        this.name = request.name();
        this.url = request.url();
    }

    public void generateCode() {
        this.code = String.format("%04d", id);
    }

    private void validCreate(CreateDeliveryCompanyRequest request) {
        validName(request.name());
        validUrl(request.url());
    }

    private void validName(String name) {
        if (StringUtils.isEmpty(name) || name.length() < 2 || name.length() > 20) {
            throw new BadRequestException("Delivery company names must be at least 2 and no more than 20 characters long.");
        }
    }

    private void validUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            throw new BadRequestException("Delivery company url must not be null.");
        }

        if (!url.toLowerCase().startsWith("http")) {
            throw new BadRequestException("Url must start with [http]");
        }
    }
}
