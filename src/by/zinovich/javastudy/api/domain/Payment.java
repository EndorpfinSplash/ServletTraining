package by.zinovich.javastudy.api.domain;

import java.util.Date;

public class Payment {

    private Integer groupOfPaymentId;
    private Integer cost;
    private Date date;
    private String description;
    private Integer paymentId;
    private String personId;


    public Payment(Integer groupOfPaymentId, Integer cost, String description, String personId) {
        this.groupOfPaymentId = groupOfPaymentId;
        this.cost = cost;
        this.description = description;
        this.personId = personId;
    }

    public Payment(Integer groupOfPaymentId, Integer cost, String description, Integer paymentId) {
        this.groupOfPaymentId = groupOfPaymentId;
        this.cost = cost;
        this.description = description;
        this.paymentId = paymentId;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Payment(Integer groupOfPaymentId, Integer cost, String description) {
        this.groupOfPaymentId = groupOfPaymentId;
        this.cost = cost;
        this.description = description;
    }

    public Payment() {
    }

    public Integer getGroupOfPaymentId() {
        return groupOfPaymentId;
    }

    public void setGroupOfPaymentId(Integer groupOfPaymentId) {
        this.groupOfPaymentId = groupOfPaymentId;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "groupOfPaymentId=" + groupOfPaymentId +
                ", cost=" + cost +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", paymentId=" + paymentId +
                ", personId='" + personId + '\'' +
                '}';
    }
}
/////////////////////////////////////////////////////////////