package com.jocoos.mybeautip.admin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.goods.TimeSale;
import com.jocoos.mybeautip.goods.TimeSaleOption;
import com.jocoos.mybeautip.goods.TimeSaleOptionRepository;
import com.jocoos.mybeautip.goods.TimeSaleRepository;

@Slf4j
@RestController
@RequestMapping("/api/admin/manual")
public class AdminGoodsController {


  private final TimeSaleRepository timeSaleRepository;
  private final TimeSaleOptionRepository timeSaleOptionRepository;

  public AdminGoodsController(TimeSaleRepository timeSaleRepository,
                              TimeSaleOptionRepository timeSaleOptionRepository) {
    this.timeSaleRepository = timeSaleRepository;
    this.timeSaleOptionRepository = timeSaleOptionRepository;
  }

  @GetMapping("/timeSales")
  public ResponseEntity<Page<TimeSaleInfo>> getTimeSales(
     @RequestParam(defaultValue = "false") boolean isDeleted,
     @RequestParam(defaultValue = "0") int page,
     @RequestParam(defaultValue = "20") int size) {

    PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
    Page<TimeSale> list = null;
    log.debug("isDeleted: {}", isDeleted);

    if(isDeleted) {
      list = timeSaleRepository.findByDeletedAtIsNotNull(pageable);
    } else {
      list = timeSaleRepository.findByDeletedAtIsNull(pageable);
    }

    if (list != null) {
      Page<TimeSaleInfo> result = list.map(t -> {
        TimeSaleInfo info = new TimeSaleInfo(t);
        List<TimeSaleOption> options = timeSaleOptionRepository.findByGoodsNoAndBroker(Integer.valueOf(t.getGoodsNo()), t.getBroker());
        if (options != null && options.size() > 0) {
          info.setOptions(options);
        }
        return info;
      });

      return new ResponseEntity<>(result, HttpStatus.OK);
    }

    return new ResponseEntity<>(Page.empty(), HttpStatus.OK);
  }
}
