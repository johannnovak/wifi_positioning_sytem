package fr.utbm.lo53.wifipositioning.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Database table associated class.<br>
 * Class designed to store Position of a mobile phone. It its also linked to a
 * Set of {@link Measurement}.
 * 
 * @author jnovak
 *
 */
public class Position
{
	/** ID of the database table. DO NOT SET. */
	private int					id;

	/** x coordinate of the {@link Position} */
	private float				x;

	/** y coordinate of the {@link Position} */
	private float				y;

	/** Associated {@link Measurement} for this {@link Position} */
	private Set<Measurement>	measurements	= new HashSet<Measurement>(0);

	/* --------------------------------------------------------------------- */

	/**
	 * Default empty constructor.
	 */
	public Position()
	{
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Constructor that creates a new {@link Position} with a (x,y) coordinate a
	 * {@link Measurement} that is added to the set.
	 * 
	 * @param _x
	 *            x coordinate of the {@link Position}.
	 * @param _y
	 *            y coordinate of the {@link Position}.
	 * @param _measurement
	 *            {@link Measurement} added to the Set.
	 */
	public Position(final float _x, final float _y, final Measurement _measurement)
	{
		this.x = _x;
		this.y = _y;
		if (measurements == null)
			measurements = new HashSet<Measurement>();
		measurements.add(_measurement);
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Constructor that creates a {@link Position} from (x,y) coordinates and a
	 * set of {@link Measurement}.
	 * 
	 * @param _x
	 *            x coordinate of the {@link Position}.
	 * @param _y
	 *            y coordinate of the {@link Position}.
	 * @param _measurements
	 *            Set of {@link Measurement} to set to its associated attribute.
	 */
	public Position(final float _x, final float _y, final Set<Measurement> _measurements)
	{
		this.x = _x;
		this.y = _y;
		measurements = _measurements;
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
	 * @return x coordinate of this {@link Position}.
	 */
	public float getX()
	{
		return x;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Sets the x coordinate of this {@link Position}.
	 * 
	 * @param x
	 *            x coordinate to sets to its associated attribute.
	 */
	public void setX(
			final float x)
	{
		this.x = x;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * @return y coordinate of this {@link Position}.
	 */
	public float getY()
	{
		return y;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Sets the y coordinate of this {@link Position}.
	 * 
	 * @param y
	 *            y coordinate to sets to its associated attribute.
	 */
	public void setY(
			final float y)
	{
		this.y = y;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * @return Set of {@link Measurement} containing the associated
	 *         {@link Measurement} linked to this {@link Position}.
	 */
	public Set<Measurement> getMeasurements()
	{
		return measurements;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Sets the Set of {@link Measurement}.
	 * 
	 * @param measurements
	 *            Set of {@link Measurement} set to its associated attributes.
	 */
	public void setMeasurements(
			final Set<Measurement> measurements)
	{
		this.measurements = measurements;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Overriden 'toString' method.<br>
	 * 
	 * @return String definition of the object.
	 */
	@Override
	public String toString()
	{
		String s = "";
		s += id + "," + x + "," + y;
		return s.toString();
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to test 2 {@link Position}.
	 * 
	 * @param _p
	 *            {@link Position} to test.
	 * @return True if the (x,y) coordinates are equal.<br>
	 *         False otherwise.
	 */
	public boolean equals(
			final Position _p)
	{
		return ((x == _p.getX()) && (y == _p.getY()));
	}
}