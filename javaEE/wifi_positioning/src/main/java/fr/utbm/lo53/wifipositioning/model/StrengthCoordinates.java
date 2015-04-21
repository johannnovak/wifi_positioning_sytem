package fr.utbm.lo53.wifipositioning.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="STRENGTH_COORDINATES")
public class StrengthCoordinates {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	private int	id;
	
	@Column(name="TEL_ID")
	private String tel_id; 
	@Column(name="AP_ID")
	private String ap_id; 
	@Column(name="X")
	private int x;
	@Column(name="Y")
	private int y;
	@Column(name="STRENGTH")
	private double strength;
	
	public StrengthCoordinates() {
	}
	
	public StrengthCoordinates( String _tel_id, String _ap_id, int _x, int _y, double _strength){
//		this.id = _id;
		this.tel_id = _tel_id;
		this.ap_id = _ap_id;
		this.x = _x; 
		this.y = _y; 
		this.strength = _strength; 
	}

	public String getTel_id() {
		return tel_id;
	}

	public void setTel_id(String tel_id) {
		this.tel_id = tel_id;
	}

	public String getAp_id() {
		return ap_id;
	}

	public void setAp_id(String ap_id) {
		this.ap_id = ap_id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public double getStrength() {
		return strength;
	}

	public void setStrength(double strength) {
		this.strength = strength;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		String s = "";
		s += "id : " + id;
		s += "\ntel_id : " + tel_id;
		s += "\nap_id : " + ap_id;
		s += "\nx : " + x;
		s += "\ny : " + y;
		s += "\nstrength : " + strength;

		return super.toString();
	}
}
