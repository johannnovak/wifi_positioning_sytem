package fr.utbm.lo53.wifipositioning.controller.runnable;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
	protected List<Object> parseMobileRequestHandler() throws IOException
	{
		s_logger.debug("Thread-{} : Parsing mobile socket connection data for calibration.",
				m_threadID);

		byte bytes[] = IOUtils.toByteArray(m_clientSocket.getInputStream());

		try
		{
			List<Object> requestData = parseRequestData(bytes, m_packetOffset);
			if ((requestData == null) || requestData.isEmpty())
				handleResponse(m_clientSocket, "500".getBytes());
			return requestData;
		} finally
		{
			s_logger.debug("Thread-{} : Response sent back to the client.", m_threadID);
		}
	}

	@Override
	protected List<Object> parseRequestData(
			final byte[] _bytes,
			final int _offset)
	{
		s_logger.debug("Thread-{} : Parsing data from byte[]...", m_threadID);
		int offset = _offset;
		ArrayList<Object> list = new ArrayList<Object>();

		try
		{
			/* Parses the byte array. */
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

		} catch (Exception e)
		{
			s_logger.error(String.format(
					"Thread-%s : Failed to parse mobile data for calibration.", m_threadID), e);
			return null;
		}

		s_logger.debug("Thread-{} : Parsing of data completed.", m_threadID);

		/* Verify that the data sent are not null or empty */
		if (((String) list.get(0) == null) || ((String) list.get(0) == ""))
		{
			s_logger.error("Thread-{} : The mobile mac address is empty! ", m_threadID);
			return null;
		}
		if ((int) list.get(1) < 0)
		{
			s_logger.error("Thread-{} : The coordonate x is invalid! ", m_threadID);
			return null;
		}
		if ((int) list.get(2) < 0)
		{
			s_logger.error("Thread-{} : The coordonate y is invalid! ", m_threadID);
			return null;
		}

		return list;
	}

	@Override
	protected boolean accessDatabaseHandler(
			final List<Object> _mobileRequestData,
			final Set<Measurement> _measurements)
	{
		int x = (int) _mobileRequestData.get(1);
		int y = (int) _mobileRequestData.get(2);

		return calibrate(x, y, _measurements);
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
			final float _x,
			final float _y,
			final Set<Measurement> _measurements)
	{

		Position position = new Position(_x, _y, _measurements);
		for (Measurement m : _measurements)
			m.setPosition(position);
		s_logger.debug("Thread-{} : Inserting following Position : {}", m_threadID, position);
		return m_calibrateService.insertSample(position);
	}
}
