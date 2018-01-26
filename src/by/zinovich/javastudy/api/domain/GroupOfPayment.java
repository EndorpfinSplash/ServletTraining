package by.zinovich.javastudy.api.domain;

public class GroupOfPayment {

    private int groupId;
    private String groupOfPaymentName;
    private int personId;

    public GroupOfPayment(int groupId, String newName) {
        this.groupId = groupId;
        this.groupOfPaymentName = newName;
    }

    public GroupOfPayment(int groupId, String groupOfPaymentName, int personId) {
        this.groupId = groupId;
        this.groupOfPaymentName = groupOfPaymentName;
        this.personId = personId;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public GroupOfPayment(String groupOfPaymentName, int personId) {
        this.groupOfPaymentName = groupOfPaymentName;
        this.personId = personId;
    }

    public GroupOfPayment() {
    }

    public GroupOfPayment(String groupOfPayment) {
        this.groupOfPaymentName = groupOfPayment;
    }

    public String getGroupOfPaymentName() {
        return groupOfPaymentName;
    }

    public void setGroupOfPaymentName(String groupOfPaymentName) {
        this.groupOfPaymentName = groupOfPaymentName;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "GroupOfPayment{" +
                "groupId=" + groupId +
                ", groupOfPaymentName='" + groupOfPaymentName + '\'' +
                ", personId=" + personId +
                '}';
    }
}
//////////////////////////////////////////////