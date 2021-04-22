package com.campaign.subscription.constraint.validator;

import com.campaign.subscription.entity.User;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.function.Predicate;

@Service
public class UserValidator {

    public static final String ACTIVE = "Active";

    public void validateUser(User user) {
        verifyState(user.getState());
    }

    private void verifyState(String userState) {
        Predicate<String> isActive = state -> state.equalsIgnoreCase(ACTIVE);
        if(isActive.negate().test(userState)) {
            throw new IllegalArgumentException("Subscription not possible - User is not Active");
        }
    }

    public Integer verifyAge(int age) {
        Function<Integer, Integer> isAdult = ageOfUser -> ageOfUser.compareTo(18);
       /* if(isAdult.apply((Integer) age) < 0) {
            throw new IllegalArgumentException("Subscription not possible - User's age is not more than 18");
        }*/
        return isAdult.apply((Integer) age);
    }
}
