package fr.utbm.lo53.wifipositioning.controller;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.utbm.lo53.wifipositioning.controller.runnable.SocketRunnable;

/**
 * Abstract generic class. The generic class has to extend from
 * {@link SocketRunnable}.<br>
 * Class designed to create a {@link ServerSocket} which will listen to a
 * specified port given in parameter by its child class (such as
 * {@link CalibrateController} or {@link LocateController})<br>
 * It contains only one method that will be called after being constructed :
 * 'listen'. It will then listen to any entering connections to the socket. Then
 * it will create a new Instance of it generic class. It will finally start a
 * new thread taking as parameter the freshly created runnable and will listen
 * again to any entering connections.
 * 
 * @author jnovak
 *
 * @param <R>
 *            {@link SocketRunnable} extending class. {@link SocketRunnable}'s
 *            'run' method launched when a client has established a new
 *            connection to the server.
 */
public abstract class SocketController<R extends SocketRunnable>
{
	/** Logger of the class */
	private final static Logger	s_logger	= LoggerFactory.getLogger(SocketController.class);

	/**
	 * {@link ServerSocket} that will listen to entering connections through a
	 * specific port
	 */
	protected ServerSocket		m_socket;

	/** Name of the controller. */
	protected String			m_controllerName;

	/* --------------------------------------------------------------------- */

	/**
	 * Default consstructor.<br>
	 * Mainly used just to create the {@link ServerSocket} attribute from the
	 * String property given in parameter.
	 * 
	 * @param portSystemProperty
	 *            String System Property to use to get the associated integer
	 *            which is the port to listen to.
	 */
	public SocketController(final String portSystemProperty)
	{
		s_logger.debug("Creating SocketController...");

		/* Gets the port from the System's properties. */
		int port = Integer.parseInt(System.getProperty(portSystemProperty));
		try
		{
			/* Creates a new ServerSocket. */
			m_socket = new ServerSocket(port);
		} catch (IOException e)
		{
			s_logger.error(
					String.format("An error occured when creating socket with port '%s'.", port), e);
		}
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to make the {@link ServerSocket} listen to any entering
	 * connections through its associated port. It is an infinite loop waiting
	 * for connections. When a client has established a connection, it creates a
	 * new Instance of Extending Class of {@link SocketRunnable} that will be
	 * run inside a new {@link Thread}. Finally it will listen again to any
	 * entering connections.
	 * 
	 * @return
	 */
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
					/*
					 * Listens to any entering connections and returns a new
					 * Runnable.
					 */
					runnable = getInstanceOfGeneric();

					/* Launch a new Thread handling this client. */
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

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to listen to any entering connections from clients. When
	 * connected, it creates a new extended class of {@link SocketRunnable} that
	 * is returned.
	 * 
	 * @return Runnable constructed from the client's socket.
	 * 
	 * @throws IOException
	 */
	private R getInstanceOfGeneric() throws IOException
	{
		try
		{
			/* Gets the constructor of the runnable class. */
			@SuppressWarnings("unchecked")
			Constructor<R> runnableConstructor = ((Class<R>) ((ParameterizedType) this.getClass()
					.getGenericSuperclass()).getActualTypeArguments()[0])
					.getConstructor(Socket.class);

			/* Listens and creates the reunnable. */
			R runnable = runnableConstructor.newInstance(new Object[] { m_socket.accept() });
			return runnable;
		} catch (Exception e)
		{
			s_logger.error(String.format(
					"An error occured when creating the extended SocketRunnable class (%s).",
					m_controllerName), e);
		}
		return null;
	}
}