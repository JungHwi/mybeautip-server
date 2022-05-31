package com.jocoos.mybeautip.goods;

import com.jocoos.mybeautip.member.cart.Cart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TimeSaleService {
    private final TimeSaleRepository timeSaleRepository;
    private final TimeSaleOptionRepository timeSaleOptionRepository;

    public TimeSaleService(TimeSaleRepository timeSaleRepository, TimeSaleOptionRepository timeSaleOptionRepository) {
        this.timeSaleRepository = timeSaleRepository;
        this.timeSaleOptionRepository = timeSaleOptionRepository;
    }

    public void applyTimeSale(Goods goods, TimeSaleCondition timeSaleCondition) {
        Date now = new Date();
        Slice<TimeSale> timeSales = timeSaleRepository
                .getTopTimeSale(goods.getGoodsNo(), now, now, timeSaleCondition.getBroker(), PageRequest.of(0, 1));
        if (timeSales.hasContent()) {
            TimeSale timeSale = timeSales.getContent().get(0);
            updateGoods(goods, timeSale);
        }
    }

    public void applyTimeSaleOptions(int goodsNo, List<GoodsOption> goodsOptions, TimeSaleCondition timeSaleCondition) {
        Date now = new Date();
        List<TimeSaleOption> timeSaleOptions =
                timeSaleOptionRepository.getTimeSaleOptionByGoodsNo(goodsNo, now, now, timeSaleCondition.getBroker());
        if (timeSaleOptions.size() <= 0) {
            return;
        }

        for (GoodsOption element : goodsOptions) {
            timeSaleOptions.forEach(tso -> {
                if (element.getOptionNo().equals(tso.getOptionNo())) {
                    updateGoodsOption(element, tso);
                }
            });
        }
    }

    public void applyTimeSaleForCart(List<Cart> carts, TimeSaleCondition timeSaleCondition) {
        Date now = new Date();
        List<TimeSale> timeSales = getTimeSales(now, timeSaleCondition);
        List<TimeSaleOption> timeSaleOptions = getTimeSaleOptions(now, timeSaleCondition);

        carts.forEach(cart -> {
            timeSales.forEach(ts -> {
                Goods goods = cart.getGoods();
                if (goods.getGoodsNo().equals(ts.getGoodsNo())) {
                    updateGoods(goods, ts);
                }
            });
            timeSaleOptions.forEach(tso -> {
                GoodsOption goodsOption = cart.getOption();
                if (goodsOption != null) {
                    if (goodsOption.getGoodsNo().equals(tso.getGoodsNo()) &&
                            goodsOption.getOptionNo().equals(tso.getOptionNo())) {
                        updateGoodsOption(goodsOption, tso);
                    }
                }

            });
        });
    }

    private List<TimeSale> getTimeSales(Date now, TimeSaleCondition timeSaleCondition) {
        return timeSaleRepository.getTimeSaleList(now, now, timeSaleCondition.getBroker());
    }

    private List<TimeSaleOption> getTimeSaleOptions(Date now, TimeSaleCondition timeSaleCondition) {
        return timeSaleOptionRepository.getTimeSaleOptionList(now, now, timeSaleCondition.getBroker());
    }

    private void updateGoods(Goods goods, TimeSale timeSale) {
        goods.setFixedPrice(timeSale.getFixedPrice());
        goods.setGoodsPrice(timeSale.getGoodsPrice());
    }

    private void updateGoodsOption(GoodsOption goodsOption, TimeSaleOption timeSaleOption) {
        goodsOption.setOptionPrice(timeSaleOption.getOptionPrice());
    }
}
