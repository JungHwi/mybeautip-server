package com.jocoos.mybeautip.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.revenue.Revenue;
import com.jocoos.mybeautip.member.revenue.RevenueInfo;
import com.jocoos.mybeautip.member.revenue.RevenueRepository;
import com.jocoos.mybeautip.member.revenue.RevenueService;

@Slf4j
@RestController
@RequestMapping("/api/admin/manual/revenues")
public class AdminRevenueController {
  
  private final RevenueService revenueService;
  private final RevenueRepository revenueRepository;
  
  public AdminRevenueController(RevenueService revenueService,
                                RevenueRepository revenueRepository) {
    this.revenueService = revenueService;
    this.revenueRepository = revenueRepository;
  }
  
  @PatchMapping("/{id:.+}/confirm")
  public ResponseEntity<RevenueInfo> confirmRevenue(@PathVariable Long id) {
    log.debug("request: {}", id);
    
    Revenue revenue = revenueRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("revenue_not_found", "Revenue not found: " + id));
    
    if (revenue.getConfirmedAt() != null) {
      throw new BadRequestException("already_confirmed", "Revenue is already confirmed: " + revenue.getConfirmedAt());
    }
    
    RevenueInfo revenueInfo = new RevenueInfo(revenueService.confirm(revenue));
    return new ResponseEntity<>(revenueInfo, HttpStatus.OK);
  }
}
