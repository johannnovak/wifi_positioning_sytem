package fr.utbm.lo53.wifipositioning.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiscUtils
{
	/** Logger of the class */
	private final static Logger	s_logger	= LoggerFactory.getLogger(MiscUtils.class);

	public synchronized static InetAddress getHostIP4Address()
	{
		Enumeration<NetworkInterface> ifaces = null;
		try
		{
			ifaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e)
		{
			s_logger.error("An error occured when obtaining the local IPv4 address.", e);
			return null;
		}
		while (ifaces.hasMoreElements())
		{
			NetworkInterface iface = ifaces.nextElement();
			Enumeration<InetAddress> addresses = iface.getInetAddresses();

			while (addresses.hasMoreElements())
			{
				InetAddress addr = addresses.nextElement();
				if ((addr instanceof Inet4Address) && !addr.isLoopbackAddress())
				{
					return addr;
				}
			}
		}
		return null;
	}
}
