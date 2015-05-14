package fr.utbm.lo53.wifipositioning.model;

public class Measurement
{

	private int			id;
	private float		rssi;
	private String		macAddress;
	private Position	position;

	public Measurement()
	{
	}

	public Measurement(final float _rssi, final String _macAddress)
	{
		rssi = _rssi;
		macAddress = _macAddress;
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

	public float getRssi()
	{
		return rssi;
	}

	public void setRssi(
			final float rssi)
	{
		this.rssi = rssi;
	}

	public String getMacAddress()
	{
		return macAddress;
	}

	public void setMacAddress(
			final String mac_address)
	{
		this.macAddress = mac_address;
	}

	public Position getPosition()
	{
		return position;
	}

	public void setPosition(
			final Position position)
	{
		this.position = position;
	}

	@Override
	public String toString()
	{
		String s = "";
		s += "|" + id + "," + macAddress + "," + rssi;
		if (position != null)
			s += "," + position.getX() + ", " + position.getY();
		return s.toString();
	}

	@Override
	public boolean equals(
			final Object _m)
	{
		Measurement measurement = (Measurement) _m;
		return ((rssi == measurement.getRssi()) && macAddress.equals(measurement.getMacAddress()));
	}

	public boolean equals(
			final Measurement _m)
	{
		return ((rssi == _m.getRssi()) && macAddress.equals(_m.getMacAddress()));
	}
}
