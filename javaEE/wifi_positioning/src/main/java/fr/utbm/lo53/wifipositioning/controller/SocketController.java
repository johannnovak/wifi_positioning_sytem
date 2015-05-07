package fr.utbm.lo53.wifipositioning.controller;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.utbm.lo53.wifipositioning.controller.runnable.SocketRunnable;

public abstract class SocketController<R extends SocketRunnable>
{
	/** Logger of the class */
	private final static Logger	s_logger	= LoggerFactory.getLogger(SocketController.class);

	protected ServerSocket		m_socket;

	protected String			m_controllerName;

	public SocketController(final String portSystemProperty)
	{
		s_logger.debug("Creating SocketController...");
		int port = Integer.parseInt(System.getProperty(portSystemProperty));
		try
		{
			m_socket = new ServerSocket(port);
		} catch (IOException e)
		{
			s_logger.error(
					String.format("An error occured when creating socket with port '%s'.", port), e);
		}
	}

	public boolean listen()
	{
		s_logger.info("Controller '{}' is listening on : '{}:{}'  ...", m_controllerName,
				m_socket.getInetAddress(), m_socket.getLocalPort());

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
				s_logger.error("An error occured when accepting the socket.", e);
			} finally
			{
				try
				{
					m_socket.close();
				} catch (IOException e)
				{
					s_logger.error(String
							.format("Error when closing the server socket when %s.",
									m_controllerName.equals(CalibrateController.class
											.getSimpleName()) ? "calibrating" : "locating"), e);
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
			s_logger.error("", e);
		} catch (IllegalAccessException e)
		{
			s_logger.error("", e);
		} catch (IllegalArgumentException e)
		{
			s_logger.error("", e);
		} catch (InvocationTargetException e)
		{
			s_logger.error("", e);
		} catch (NoSuchMethodException e)
		{
			s_logger.error("", e);
		}
		return null;
	}
}
