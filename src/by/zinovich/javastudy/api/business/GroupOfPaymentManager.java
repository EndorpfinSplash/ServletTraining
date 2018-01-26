package by.zinovich.javastudy.api.business;

import by.zinovich.javastudy.api.domain.GroupOfPayment;

import java.util.List;

public interface GroupOfPaymentManager {

    List<GroupOfPayment> getGroupOfPaymentList();
    void addGroupOfPayment();
    void updateGroupOfPayment();

}
