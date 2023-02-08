package com.hoang2001giang.UserService.command.api.projection;

import com.hoang2001giang.CommonService.models.CardDetails;
import com.hoang2001giang.CommonService.models.User;
import com.hoang2001giang.CommonService.query.GetUserPaymentDetailsQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class UserProjection {

    @QueryHandler
    public User getUserPaymentDetails(GetUserPaymentDetailsQuery query) {
        //Ideally Get the details from the DB
        CardDetails cardDetails
                = CardDetails.builder()
                .name("Pham Hoang Giang")
                .validUntilYear(2022)
                .validUntilMonth(01)
                .cardNumber("123456789")
                .cvv(111)
                .build();

        return User.builder()
                .userId(query.getUserId())
                .firstName("Frank")
                .lastName("Castle")
                .cardDetails(cardDetails)
                .build();
    }
}
