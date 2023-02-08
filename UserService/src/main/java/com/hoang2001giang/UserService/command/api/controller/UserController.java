package com.hoang2001giang.UserService.command.api.controller;

import com.hoang2001giang.CommonService.models.User;
import com.hoang2001giang.CommonService.query.GetUserPaymentDetailsQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private transient QueryGateway queryGateway;

    public UserController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping("{userId}")
    public User getUserPaymentDetails(@PathVariable String userId){
        GetUserPaymentDetailsQuery getUserPaymentDetailsQuery
                = new GetUserPaymentDetailsQuery(userId);
        User user =
                queryGateway.query(getUserPaymentDetailsQuery,
                        ResponseTypes.instanceOf(User.class)).join();

        return user;
    }
}
