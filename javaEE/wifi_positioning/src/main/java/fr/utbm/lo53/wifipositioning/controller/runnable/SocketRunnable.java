package fr.utbm.lo53.wifipositioning.controller.runnable;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public abstract class SocketRunnable implements Runnable
{
	protected Socket			m_clientSocket;

	protected int				m_packetOffset;
	protected int				m_macAddressByteLength;
	protected int				m_positionByteLength;
	protected int				m_rssiByteLength;

	protected static AtomicLong	s_threadIDCounter	= new AtomicLong();
	protected final String		m_threadID			= createID();
	protected String			m_runnableName;

	public SocketRunnable(final Socket _clientSocket)
	{
		m_clientSocket = _clientSocket;

		m_macAddressByteLength = Integer.parseInt(System.getProperty("mac.address.byte.length"));
		m_positionByteLength = Integer.parseInt(System.getProperty("position.byte.length"));
		m_rssiByteLength = Integer.parseInt(System.getProperty("rssi.byte.length"));
	}

	@Override
	public void run()
	{
		System.out.println(m_runnableName + " - Running thread for client-" + m_threadID);
		try
		{
			socketHandler();
		} catch (IOException e)
		{
			System.out.println("An error occured when handling client socket.");
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
					System.out.println("Error when closing the client socket.");
					e.printStackTrace();
				}
			}
			System.out.println("Thread over");
		}
	}

	protected abstract void socketHandler() throws IOException;

	protected abstract List<Object> parseData(
			final byte[] _bytes,
			final int _offset);

	protected void sendResponse(
			final Socket _socket,
			final byte[] _msg) throws IOException
	{
		PrintWriter out = new PrintWriter(_socket.getOutputStream(), true);
		out.println(_msg);
	}

	private synchronized static String createID()
	{
		return String.valueOf(s_threadIDCounter.getAndIncrement());
	}
}
