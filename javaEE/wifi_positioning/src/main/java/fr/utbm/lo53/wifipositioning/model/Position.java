package fr.utbm.lo53.wifipositioning.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="position")
public class Position {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "position_id")
	private int id ;
	
	@Column(name="x")
	private float x; 
	
	@Column(name="y")
	private float y; 
	
	public Position() {
	}
	
	public Position (float _x, float _y){
		this.x = _x; 
		this.y = _y; 
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

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	@Override
	public String toString() {
		String s = "";
		s += "ID: " + id;
		s += "\nx : " + x;
		s += "\ny : " + y;
		return s.toString();
	}
}
