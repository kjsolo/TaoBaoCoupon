package cn.soloho.taobaocoupon.model

import com.google.gson.annotations.SerializedName

/**
 * Created by kjsolo on 2017/12/25.
 */
data class Goods(val code: Int, val msg: String? = null, val data: Data) {

    /*
    "coupon_apply_amount": "237.00", //优惠券满 float
        "coupon_amount": "150.00", //优惠券减  float
     */
    data class Data(@field:SerializedName("goods_pic") val goodsPic: String,
                    @field:SerializedName("goods_short_title") val goodsShortTitle: String,
                    @field:SerializedName("coupon_apply_amount") val couponApplyAmount: String,
                    @field:SerializedName("coupon_amount") val couponAmount: String) {

    }

}