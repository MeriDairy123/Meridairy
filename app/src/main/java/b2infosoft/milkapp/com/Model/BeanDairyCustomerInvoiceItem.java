package b2infosoft.milkapp.com.Model;

/**
 * Created by u on 15-07-20.
 */

public class BeanDairyCustomerInvoiceItem {
    public String id = "", customerId = "", uniqCustomerId = "", userGroupId = "", name = "", status = "", month = "", startDate = "", endDate = "", type = "", details = "";
    public double weight = 0, amount = 0;
    public int srNo = 0;

    public BeanDairyCustomerInvoiceItem(String id, String customerId, String uniqCustomerId, String userGroupId,
                                        String name, String status, String month, String startDate, String endDate,
                                        String type, String details, double weight, double amount, int srNo) {
        this.id = id;
        this.customerId = customerId;
        this.uniqCustomerId = uniqCustomerId;
        this.userGroupId = userGroupId;
        this.name = name;
        this.status = status;
        this.month = month;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.details = details;
        this.weight = weight;
        this.amount = amount;
        this.srNo = srNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getUniqCustomerId() {
        return uniqCustomerId;
    }

    public void setUniqCustomerId(String uniqCustomerId) {
        this.uniqCustomerId = uniqCustomerId;
    }

    public String getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(String userGroupId) {
        this.userGroupId = userGroupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getSrNo() {
        return srNo;
    }

    public void setSrNo(int srNo) {
        this.srNo = srNo;
    }
}
