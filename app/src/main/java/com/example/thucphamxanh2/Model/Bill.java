package com.example.thucphamxanh2.Model;

public class Bill {
    private int idBill,total;
    private String dayOut,status,idClient,timeOut,idPartner;

    public Bill() {
    }

    public int getTotal() {
        return total;
    }

    public String getIdPartner() {
        return idPartner;
    }

    public void setIdPartner(String idPartner) {
        this.idPartner = idPartner;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public int getIdBill() {
        return idBill;
    }

    public void setIdBill(int idBill) {
        this.idBill = idBill;
    }

    public String getDayOut() {
        return dayOut;
    }

    public void setDayOut(String dayOut) {
        this.dayOut = dayOut;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "idBill=" + idBill +
                ", total=" + total +
                ", dayOut='" + dayOut + '\'' +
                ", status='" + status + '\'' +
                ", idClient='" + idClient + '\'' +
                ", timeOut='" + timeOut + '\'' +
                ", idPartner='" + idPartner + '\'' +
                '}';
    }
}
