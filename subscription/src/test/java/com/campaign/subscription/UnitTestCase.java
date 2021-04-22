package com.campaign.subscription;

import com.campaign.subscription.campaignenum.StateType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

 class UnitTestCase {

    @Test
     void whenConvertedIntoEnum_thenGetsConvertedCorrectly() {
        String state = "Registered".toUpperCase();
        StateType stateType
                = StateType.valueOf(state);
        assertTrue(stateType == StateType.REGISTERED);
    }

    @Test
     void whenConvertedIntoEnum_thenThrowsException() {
        String state = "ReGISTERered";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> StateType.valueOf(state));
        assertEquals("No enum constant com.campaign.subscription.campaignenum.StateType.ReGISTERered",
                exception.getMessage());
    }

}
