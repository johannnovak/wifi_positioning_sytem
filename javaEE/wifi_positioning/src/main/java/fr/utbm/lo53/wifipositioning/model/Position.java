package fr.utbm.lo53.wifipositioning.model;


public class Position
{

	private int		m_id;
	private float	m_x;
	private float	m_y;

	public Position()
	{
	}

	public Position(final float _x, final float _y)
	{
		this.m_x = _x;
		this.m_y = _y;
	}

	/****************************************/
	/********** GETTERS and SETTERS *********/
	/****************************************/
	public int getId()
	{
		return m_id;
	}

	public void setId(
			final int id)
	{
		this.m_id = id;
	}

	public float getX()
	{
		return m_x;
	}

	public void setX(
			final float x)
	{
		this.m_x = x;
	}

	public float getY()
	{
		return m_y;
	}

	public void setY(
			final float y)
	{
		this.m_y = y;
	}

	@Override
	public String toString()
	{
		String s = "";
		s += "ID: " + m_id;
		s += "\nx : " + m_x;
		s += "\ny : " + m_y;
		return s.toString();
	}
}
