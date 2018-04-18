package com.newland.wechat.model;

public class BussinessInfo {
	private int open_pay;
	private int open_shake;
	private int open_scan;
	private int open_store;
	private int open_card;
	public int getOpen_card() {
		return open_card;
	}
	public void setOpen_card(int open_card) {
		this.open_card = open_card;
	}
	@Override
	public String toString() {
		return "BussinessInfo [open_pay=" + open_pay + ", open_shake="
				+ open_shake + ", open_scan=" + open_scan + ", open_store="
				+ open_store + ", open_card=" + open_card + "]";
	}
	public int getOpen_pay() {
		return open_pay;
	}
	public void setOpen_pay(int open_pay) {
		this.open_pay = open_pay;
	}
	public int getOpen_shake() {
		return open_shake;
	}
	public void setOpen_shake(int open_shake) {
		this.open_shake = open_shake;
	}
	public int getOpen_scan() {
		return open_scan;
	}
	public void setOpen_scan(int open_scan) {
		this.open_scan = open_scan;
	}
	public int getOpen_store() {
		return open_store;
	}
	public void setOpen_store(int open_store) {
		this.open_store = open_store;
	}
	
}
