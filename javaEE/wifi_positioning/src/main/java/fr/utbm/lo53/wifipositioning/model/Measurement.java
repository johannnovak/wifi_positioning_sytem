package fr.utbm.lo53.wifipositioning.model;

public class Measurement
{

	private int			m_id;
	private float		m_rssi;
	private String		m_macAddress;
	private Position	m_position;

	public Measurement()
	{
	}

	public Measurement(final float _rssi, final String _macAddress, final Position _position)
	{
		m_rssi = _rssi;
		m_macAddress = _macAddress;
		m_position = _position;
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

	public float getRssi()
	{
		return m_rssi;
	}

	public void setRssi(
			final float rssi)
	{
		this.m_rssi = rssi;
	}

	public String getMac_address()
	{
		return m_macAddress;
	}

	public void setMac_address(
			final String mac_address)
	{
		this.m_macAddress = mac_address;
	}

	public Position getM_position()
	{
		return m_position;
	}

	public void setM_position(
			final Position m_position)
	{
		this.m_position = m_position;
	}

	@Override
	public String toString()
	{
		String s = "";
		s += "ID: " + m_id;
		s += "\nRSSI: " + m_rssi;
		s += "\nMAC ADDRESS: " + m_macAddress;
		return s.toString();
	}
}
