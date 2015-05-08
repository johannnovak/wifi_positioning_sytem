package fr.utbm.lo53.wifipositioning.model;

import java.util.HashSet;
import java.util.Set;

public class Position
{

	private int					id;
	private float				x;
	private float				y;
	private Set<Measurement>	measurements	= new HashSet<Measurement>(0);

	public Position()
	{
	}

	public Position(final float _x, final float _y, final Measurement _measurement)
	{
		this.x = _x;
		this.y = _y;
		measurements.add(_measurement);
	}

	/****************************************/
	/********** GETTERS and SETTERS *********/
	/****************************************/
	public int getId()
	{
		return id;
	}

	public void setId(
			final int id)
	{
		this.id = id;
	}

	public float getX()
	{
		return x;
	}

	public void setX(
			final float x)
	{
		this.x = x;
	}

	public float getY()
	{
		return y;
	}

	public void setY(
			final float y)
	{
		this.y = y;
	}

	public Set<Measurement> getMeasurements()
	{
		return measurements;
	}

	public void setMeasurements(
			final Set<Measurement> measurements)
	{
		this.measurements = measurements;
	}

	@Override
	public String toString()
	{
		String s = "";
		s += "ID : " + id;
		s += "\t| x : " + x;
		s += "\t| y : " + y;
		s += "\t| Associated Measurements : [";
		for (Measurement m : measurements)
			s += m.toString() + " | ";

		s += "]";
		return s.toString();
	}
}
