package fr.utbm.lo53.wifipositioning.controller.runnable;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;

import fr.utbm.lo53.wifipositioning.service.LocateService;

public class LocateRunnable extends SocketRunnable
{
	private final LocateService	m_locateService;

	public LocateRunnable(final Socket _clientSocket)
	{
		super(_clientSocket);

		m_locateService = LocateService.getInstance();

		m_packetOffset = Integer.parseInt(System.getProperty("locate.packet.offset"));

		m_runnableName = this.getClass().getSimpleName();
	}

	@Override
	protected void socketHandler() throws IOException
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

		locate();

		sendResponse(m_clientSocket, "200");
	}

	@Override
	protected List<Object> parseData(
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
	public void locate()
	{
	}
}