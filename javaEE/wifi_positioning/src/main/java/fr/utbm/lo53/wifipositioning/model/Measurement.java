package fr.utbm.lo53.wifipositioning.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="accessPoint")
public class Measurement {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id")
	private int id; 
	
	@Column(name="rssi")
	private float rssi;
	
	@Column(name="mac_address")
	private float mac_address;

	public Measurement() {
	}

	public Measurement(float _rssi) {
		this.rssi = _rssi;
	}
	
	/****************************************/
	/********** GETTERS and SETTERS *********/
	/****************************************/
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getRssi() {
		return rssi;
	}

	public void setRssi(float rssi) {
		this.rssi = rssi;
	} 
	
	public float getMac_address() {
		return mac_address;
	}

	public void setMac_address(float mac_address) {
		this.mac_address = mac_address;
	}

	@Override
	public String toString() {
		String s = "";
		s += "ID: " + id;
		s += "\nRSSI: " + rssi;
		s += "\nMAC ADDRESS: " + mac_address;
		return s.toString();
	}
}
