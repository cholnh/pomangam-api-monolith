package kr.nzzi.msa.pmg.pomangamapimonilith.global.util.bootpay.model.response.callback;

import kr.nzzi.msa.pmg.pomangamapimonilith.global.util.bootpay.model.response.verify.PaymentData;
import lombok.Data;

@Data
public class CallbackResponse {
    String receipt_id;
    String pg;
    String pg_name;
    String method;
    String method_name;
    String receipt_url;
    String application_id;
    String name;
    String private_key;
    String order_id;
    PaymentData payment_data;
    Integer price;
    Integer retry_count;
    Integer status;
}
