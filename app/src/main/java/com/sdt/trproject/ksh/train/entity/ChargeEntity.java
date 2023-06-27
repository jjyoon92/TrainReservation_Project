package com.sdt.trproject.ksh.train.entity;

public class ChargeEntity {
    private Integer charge;
    private Integer chargeVip;

    public ChargeEntity(Integer charge, Integer chargeVip) {
        this.charge = charge;
        this.chargeVip = chargeVip;
    }

    public Integer getCharge() {
        return charge;
    }

    public void setCharge(Integer charge) {
        this.charge = charge;
    }

    public Integer getChargeVip() {
        return chargeVip;
    }

    public void setChargeVip(Integer chargeVip) {
        this.chargeVip = chargeVip;
    }
}
