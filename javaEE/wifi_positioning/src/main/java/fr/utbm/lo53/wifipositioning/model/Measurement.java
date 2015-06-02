package fr.utbm.lo53.wifipositioning.model;

/**
 * Database table associated class.<br>
 * Class designed to store the RSSI concerning a mobile phone's macAddress, a
 * {@link Position} and its own AP macAddress.
 * 
 * @author jnovak
 *
 */
public class Measurement
{
	/** Database ID of the object. DO NOT SET THE ATTRBIUTE. */
	private int			id;

	/** AP macAddress */
	private String		macAddress;

	/** RSSI linked to a position. */
	private float		rssi;

	/** {@link Position} where the RSSI has been measured. */
	private Position	position;

	/* --------------------------------------------------------------------- */

	/**
	 * Default empty Constructor.
	 */
	public Measurement()
	{
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Constructor used to create a {@link Measurement} with its RSSI and its
	 * macAddress.
	 * 
	 * @param _rssi
	 *            RSSI value to set to its associated attribute.
	 * @param _macAddress
	 *            AP macAddress to set to its associated attribute.
	 */
	public Measurement(final float _rssi, final String _macAddress)
	{
		rssi = _rssi;
		macAddress = _macAddress;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * @return Database ID of the {@link Measurement}.
	 */
	public int getId()
	{
		return id;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Sets the ID of the {@link Measurement}. DO NOT USE THIS METHOD.
	 * 
	 * @param id
	 *            Database ID to set to its associated attribute.
	 */
	public void setId(
			final int id)
	{
		this.id = id;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * @return RSSI of this {@link Measurement}.
	 */
	public float getRssi()
	{
		return rssi;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Sets the RSSI of this {@link Measurement}.
	 * 
	 * @param rssi
	 *            RSSI value to set its associated attribute.
	 */
	public void setRssi(
			final float rssi)
	{
		this.rssi = rssi;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * @return macAddress of this {@link Measurement}.
	 */
	public String getMacAddress()
	{
		return macAddress;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Sets the macAddress of this {@link Measurement}.
	 * 
	 * @param mac_address
	 *            MacAddress to set its associated attribute.
	 */
	public void setMacAddress(
			final String mac_address)
	{
		this.macAddress = mac_address;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * @return {@link Position} of this {@link Measurement}.
	 */
	public Position getPosition()
	{
		return position;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Sets the {@link Position} of this {@link Measurement}.
	 * 
	 * @param position
	 *            Position to set to its associated attribute.
	 */
	public void setPosition(
			final Position position)
	{
		this.position = position;
	}

	/**
	 * Overriden 'toString' method.<br>
	 * Prints the different attributes on a line.
	 */
	@Override
	public String toString()
	{
		String s = "";
		s += "|" + id + "," + macAddress + "," + rssi;
		if (position != null)
			s += "," + position.getX() + ", " + position.getY();
		return s.toString();
	}

	/**
	 * Overriden 'equals' method.<br>
	 * 
	 * @param _m
	 *            Object to test. It needs to be a {@link Measurement}.
	 * 
	 * @return True if the RSSI and the MacAddress are equals.<br>
	 *         False otherwise.
	 */
	@Override
	public boolean equals(
			final Object _m)
	{
		Measurement measurement = (Measurement) _m;
		return ((rssi == measurement.getRssi()) && macAddress.equals(measurement.getMacAddress()));
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to test if two {@link Measurement} are equal.
	 * 
	 * @param _m
	 *            Mesurement to test.
	 * 
	 * @return True if the RSSI and the MacAddress are equals.<br>
	 *         False otherwise.
	 */
	public boolean equals(
			final Measurement _m)
	{
		return ((rssi == _m.getRssi()) && macAddress.equals(_m.getMacAddress()));
	}
}
