package fr.utbm.lo53.wifipositioning.controller;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.net.ServerSocket;
import java.net.Socket;

import fr.utbm.lo53.wifipositioning.controller.runnable.SocketRunnable;

public abstract class SocketController<R extends SocketRunnable>
{
	protected ServerSocket	m_socket;

	protected String		m_controllerName;

	public SocketController(final String portSystemProperty)
	{
		int port = Integer.parseInt(System.getProperty(portSystemProperty));
		try
		{
			m_socket = new ServerSocket(port);
		} catch (IOException e)
		{
			System.out.println("An error occured when creating socket with port " + port);
			e.printStackTrace();
		}
	}

	public boolean listen()
	{
		System.out.println("Controller " + m_controllerName + " is listening on : "
				+ m_socket.getInetAddress() + ":" + m_socket.getLocalPort() + " ...");

		R runnable;
		if (m_socket != null)
		{
			try
			{
				while (true)
				{
					runnable = getInstanceOfGeneric();

					if (runnable != null)
						new Thread(runnable).start();

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
			return true;
		}
		return false;
	}

	private R getInstanceOfGeneric() throws IOException
	{
		try
		{
			@SuppressWarnings("unchecked")
			Constructor<R> runnableConstructor = ((Class<R>) ((ParameterizedType) this.getClass()
					.getGenericSuperclass()).getActualTypeArguments()[0])
					.getConstructor(Socket.class);

			R runnable = runnableConstructor.newInstance(new Object[] { m_socket.accept() });
			return runnable;
		} catch (InstantiationException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			e.printStackTrace();
		} catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
