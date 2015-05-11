package fr.utbm.lo53.wifipositioning.controller.runnable;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.utbm.lo53.wifipositioning.model.Measurement;
import fr.utbm.lo53.wifipositioning.model.Position;
import fr.utbm.lo53.wifipositioning.service.CalibrateService;

public class CalibrateRunnable extends SocketRunnable
{
	/** Logger of the class */
	private final static Logger		s_logger	= LoggerFactory.getLogger(CalibrateRunnable.class);

	private final CalibrateService	m_calibrateService;

	public CalibrateRunnable(final Socket _clientSocket)
	{
		super(_clientSocket);

		m_calibrateService = CalibrateService.getInstance();

		m_packetOffset = Integer.parseInt(System.getProperty("calibrate.packet.offset"));

		m_runnableName = this.getClass().getSimpleName();

		s_logger.debug("CalibrateRunnable created.");
	}

	@Override
	protected void socketHandler() throws IOException
	{
		s_logger.debug("Handling client socket connection for calibration.");
		String macAddress = "";
		float x = -1.0f;
		float y = -1.0f;
		float rssi = -1.0f;

		byte bytes[] = IOUtils.toByteArray(m_clientSocket.getInputStream());

		try
		{
			List<Object> data = parseData(bytes, m_packetOffset);
			if ((data == null) || data.isEmpty())
				sendResponse(m_clientSocket, "500".getBytes());
			macAddress = (String) data.get(0);
			x = (float) data.get(1);
			y = (float) data.get(2);
			rssi = (float) data.get(3);

			if (calibrate(macAddress, rssi, x, y))
				sendResponse(m_clientSocket, "200".getBytes());
			else
				sendResponse(m_clientSocket, "500".getBytes());
		} finally
		{
			s_logger.debug("Response sent back to the client.");
		}
	}

	@Override
	protected List<Object> parseData(
			final byte[] _bytes,
			final int _offset)
	{
		s_logger.debug("Parsing data from byte[]...");
		int offset = _offset;
		ArrayList<Object> list = new ArrayList<Object>();

		try
		{
			byte[] macAddressByteArray = Arrays.copyOfRange(_bytes, offset, offset
					+ m_macAddressByteLength);
			list.add(new String(macAddressByteArray));

			offset += m_macAddressByteLength;
			byte[] xByteArray = Arrays.copyOfRange(_bytes, offset, offset + m_positionByteLength);
			list.add(ByteBuffer.wrap(xByteArray).order(ByteOrder.LITTLE_ENDIAN).getFloat());

			offset += m_positionByteLength;
			byte[] yByteArray = Arrays.copyOfRange(_bytes, offset, offset + m_positionByteLength);
			list.add(ByteBuffer.wrap(yByteArray).order(ByteOrder.LITTLE_ENDIAN).getFloat());

			offset += m_positionByteLength;
			byte[] rssiByteArray = Arrays.copyOfRange(_bytes, offset, offset + m_rssiByteLength);
			list.add(ByteBuffer.wrap(rssiByteArray).order(ByteOrder.LITTLE_ENDIAN).getFloat());

			s_logger.debug("Parsing of data completed.");

			return list;
		} catch (Exception e)
		{
			s_logger.error("Failed to parse data for calibration.", e);
		}
		return null;
	}

	/**
	 * Allows you to
	 * 
	 * @param request
	 * @return "OK" if all the parameters are informed else it returns a
	 *         exception
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public boolean calibrate(
			final String _macAddress,
			final float _rssi,
			final float _x,
			final float _y) throws IOException, IllegalArgumentException
	{
		// Verify that the data sent are not null or empty
		try
		{
			if ((_macAddress == null) || _macAddress.isEmpty())
			{
				throw new IllegalArgumentException("Access Point's mac address is invalid! ");
			}
			if (_rssi == 0)
			{
				throw new IllegalArgumentException("The RSSI is invalid! ");
			}
			if (_x < 0)
			{
				throw new IllegalArgumentException("The coordonate x is invalid! ");
			}
			if (_y < 0)
			{
				throw new IllegalArgumentException("The coordonate y is invalid! ");
			}
		} catch (IllegalArgumentException e)
		{
			s_logger.error("Illegal argument.", e);
			return false;
		}
		s_logger.debug("Thread-{}\tAP : '{}'\tRSSI: '{}'\tx : '{}'\ty : '{}'", m_threadID,
				_macAddress, _rssi, _x, _y);

		insertIntoDatabase(_macAddress, _x, _y, _rssi);

		return true;
	}

	/**
	 * Insert the parameters into the database
	 * 
	 * @param _tel_id
	 * @param _ap_id
	 * @param _x
	 * @param _y
	 * @param _strength
	 */
	public void insertIntoDatabase(
			final String _macAddress,
			final float _x,
			final float _y,
			final float _rssi)
	{
		Measurement measurement = new Measurement(_rssi, _macAddress);
		Position position = new Position(_x, _y, measurement);
		measurement.setPosition(position);
		s_logger.debug("Inserting following Measurement in the database : {}", measurement);
		s_logger.debug("With associated Position : {}", position);
		m_calibrateService.insertSample(position, measurement);
	}
}
