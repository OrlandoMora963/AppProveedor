package com.example.appproveedorgas.util;

import java.util.Date;

public class Register {
    private long id;
    private Date date;
    private String price;
    private String status;
    private String companyID;
    private long productID;

    public long getID() { return id; }
    public void setID(long value) { this.id = value; }

    public Date getDate() { return date; }
    public void setDate(Date value) { this.date = value; }

    public String getPrice() { return price; }
    public void setPrice(String value) { this.price = value; }

    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }

    public String getCompanyID() { return companyID; }
    public void setCompanyID(String value) { this.companyID = value; }

    public long getProductID() { return productID; }
    public void setProductID(long value) { this.productID = value; }
}