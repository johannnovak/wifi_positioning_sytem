package wifi_positioning;

import java.util.Random;

import ch.qos.logback.core.joran.spi.JoranException;

public class Test
{
	public static void main(
			final String[] args) throws JoranException
	{
		int nb = 3;
		Thread[] t = new Thread[nb];
		Runnable[] r = new MyRunnable[nb];

		Random random = new Random();
		for (int i = 0; i < nb; ++i)
		{
			r[i] = new MyRunnable("" + random.nextInt());
			t[i] = new Thread(r[i]);
			t[i].start();
		}

		for (Thread th : t)
		{
			try
			{
				th.join();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("OK !");
		}
	}

	public static class MyRunnable implements Runnable
	{
		String	t;

		public MyRunnable(final String t)
		{
			this.t = t;
		}

		@Override
		public void run()
		{
			System.out.println(t + "\n");
			Random r = new Random();
			// try
			// {
			// Thread.wait(4000);
			// } catch (InterruptedException e)
			// {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}
	}
}
