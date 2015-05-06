package fr.utbm.lo53.wifipositioning.controller;

import java.io.IOException;
import java.net.ServerSocket;

import fr.utbm.lo53.wifipositioning.controller.runnable.CalibrateSocketRunnable;

/**
 * Class designed to control the information given as parameters in the browser
 * by the user<br>
 * There is one entry point : /calibrate <br>
 * After controlling the parameters, it sends the response "OK" if all the
 * informations are informed
 */
public class CalibrateController
{
	private final ServerSocket	m_socket;

	private final int			m_macAddressByteLength;
	private final int			m_positionByteLength;
	private final int			m_rssiByteLength;
	private final int			m_packetOffset;

	public CalibrateController(final int _calibratePort, final int _packetOffset,
			final int _macAddressByteLength, final int _positionByteLength,
			final int _rssiByteLength) throws IOException
	{
		m_socket = new ServerSocket(_calibratePort);

		m_packetOffset = _packetOffset;
		m_macAddressByteLength = _macAddressByteLength;
		m_positionByteLength = _positionByteLength;
		m_rssiByteLength = _rssiByteLength;
	}

	public void listen()
	{
		try
		{
			while (true)
			{
				new Thread(new CalibrateSocketRunnable(m_socket.accept(), m_packetOffset,
						m_macAddressByteLength, m_positionByteLength, m_rssiByteLength)).start();

			}
		} catch (IOException e)
		{
			System.out.println("An error occured when accepting the connection.");
			e.printStackTrace();
		} finally
		{
			try
			{
				m_socket.close();
			} catch (IOException e)
			{
				System.out.println("Error when closing the server cocket when calibrating.");
				e.printStackTrace();
			}
		}
	}
}