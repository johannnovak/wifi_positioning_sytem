package fr.utbm.lo53.wifipositioning.controller.runnable;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.IOUtils;

import fr.utbm.lo53.wifipositioning.model.Measurement;
import fr.utbm.lo53.wifipositioning.model.Position;
import fr.utbm.lo53.wifipositioning.service.CalibrateService;

public class CalibrateSocketRunnable implements Runnable
{
	private static AtomicLong		s_threadIDCounter	= new AtomicLong();

	private final String			m_threadID			= createID();

	private final Socket			m_clientSocket;

	private final CalibrateService	m_calibrateService;

	private final int				m_packetOffset;
	private final int				m_macAddressByteLength;
	private final int				m_positionByteLength;
	private final int				m_rssiByteLength;

	public CalibrateSocketRunnable(final Socket _clientSocket, final int _packetOffset,
			final int _macAddressByteLength, final int _positionByteLength,
			final int _rssiByteLength)
	{
		m_clientSocket = _clientSocket;

		m_calibrateService = CalibrateService.getInstance();

		m_packetOffset = _packetOffset;
		m_macAddressByteLength = _macAddressByteLength;
		m_positionByteLength = _positionByteLength;
		m_rssiByteLength = _rssiByteLength;
	}

	@Override
	public void run()
	{
		System.out.println("Running thread for client-" + m_threadID);
		try
		{
			String macAddress = "";
			float x = -1.0f;
			float y = -1.0f;
			float rssi = -1.0f;

			byte bytes[] = IOUtils.toByteArray(m_clientSocket.getInputStream());

			List<Object> data = parseData(bytes, m_packetOffset);
			macAddress = (String) data.get(0);
			x = (float) data.get(1);
			y = (float) data.get(2);
			rssi = (float) data.get(3);

			calibrate(macAddress, rssi, x, y);

			sendResponse(m_clientSocket, "200");
		} catch (IOException e)
		{
			System.out.println("An error occured when handling calibrate client socket.");
			e.printStackTrace();
		} finally
		{
			if (m_clientSocket != null)
			{
				try
				{
					m_clientSocket.close();
				} catch (IOException e)
				{
					System.out.println("Error when closing the client socket when calibrating.");
					e.printStackTrace();
				}
			}
			System.out.println("Thread over");
		}
	}

	private List<Object> parseData(
			final byte[] _bytes,
			final int _offset)
	{
		int offset = _offset;
		ArrayList<Object> list = new ArrayList<Object>();

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

		return list;
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
	public String calibrate(
			final String _macAddress,
			final float _rssi,
			final float _x,
			final float _y) throws IOException, IllegalArgumentException
	{
		// Verify that the data sent are not null or empty
		if ((_macAddress == null) || _macAddress.isEmpty())
		{
			throw new IllegalArgumentException("ID's Access Point is invalid! ");
		}
		if (_rssi == 0)
		{
			throw new IllegalArgumentException("The strength of the signal is invalid! ");
		}
		if (_x < 0)
		{
			throw new IllegalArgumentException("The cordonates x is invalid! ");
		}
		if (_y < 0)
		{
			throw new IllegalArgumentException("The cordonates y is invalid! ");
		}

		System.out.println("Thread-" + m_threadID + "\t: ID's AP : " + _macAddress + "\tRSSI: "
				+ _rssi + "\tX = " + _x + "\tY = " + _y);

		insertIntoDatabase(_macAddress, _x, _y, _rssi);

		return "OK";
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
		Position position = new Position(_x, _y);
		Measurement measurement = new Measurement(_rssi, _macAddress, position);
		// m_calibrateService.insertMeasurement(measurement);
	}

	private void sendResponse(
			final Socket _socket,
			final String _msg) throws IOException
	{
		PrintWriter out = new PrintWriter(_socket.getOutputStream(), true);
		out.println(_msg);
	}

	private synchronized static String createID()
	{
		return String.valueOf(s_threadIDCounter.getAndIncrement());
	}
}
