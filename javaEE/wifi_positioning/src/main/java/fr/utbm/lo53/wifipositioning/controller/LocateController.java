package fr.utbm.lo53.wifipositioning.controller;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.io.IOUtils;

import fr.utbm.lo53.wifipositioning.service.LocateService;

public class LocateController
{
	private final ServerSocket	m_socket;
	private final LocateService	m_locateService;

	public LocateController(final int _port) throws IOException
	{
		m_socket = new ServerSocket(_port);
		m_locateService = LocateService.getInstance();
	}

	public boolean listen()
	{
		Socket clientSocket = null;

		String macAddress = "";
		float x = -1.0f;
		float y = -1.0f;
		float rssi = -1.0f;

		try
		{
			while (true)
			{
				try
				{
					clientSocket = m_socket.accept();

					BufferedInputStream inputStream = new BufferedInputStream(
							clientSocket.getInputStream());
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					byte bytes[] = IOUtils.toByteArray(inputStreamReader, "utf8");

					parseData(bytes, macAddress, x, y, rssi);

					// locate(macAddress, rssi, x, y);

					sendResponse(clientSocket, "200".getBytes());
				} catch (IOException e)
				{
					e.printStackTrace();
				} finally
				{
					if (clientSocket != null)
					{
						try
						{
							clientSocket.close();
						} catch (IOException e)
						{
							System.out
									.println("Error when closing the client socket when calibrating.");
							e.printStackTrace();
						}
					}
				}
			}
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

	private void parseData(
			final byte[] _bytes,
			String _macAddress,
			final float _x,
			final float _y,
			final float _rssi)
	{
		_macAddress = "dfsdfqsdfqdfqsdf";
	}

	private void sendResponse(
			final Socket _socket,
			final byte[] _data) throws IOException
	{
		PrintWriter out = new PrintWriter(_socket.getOutputStream(), true);
		out.println(_data);
	}

}
