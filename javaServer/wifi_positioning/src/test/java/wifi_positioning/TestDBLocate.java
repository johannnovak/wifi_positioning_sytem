package wifi_positioning;

import java.util.HashSet;
import java.util.Set;

import fr.utbm.lo53.wifipositioning.model.Measurement;
import fr.utbm.lo53.wifipositioning.model.Position;
import fr.utbm.lo53.wifipositioning.service.LocateService;

public class TestDBLocate
{
	public static void main(
			final String[] args)
	{
		Set<Measurement> ms = new HashSet<Measurement>();

		Position p = new Position();
		p.setX(1.0f);
		p.setY(2.0f);

		int apCount = 3;
		String[] macAdresses = { "00:00:00:00:00:01", "00:00:00:00:00:02", "00:00:00:00:00:03" };
		double[] xAp = { 3, 10, 20 };
		double[] yAp = { 1, 15, 8 };

		double currentX;
		double currentY;

		for (int i = 0; i < apCount; ++i)
		{
			Measurement m;

			currentX = p.getX();// + (t * 1000);
			currentY = p.getY();// + (t * 500);

			float rssi = (float) Math.sqrt(((currentX - xAp[i]) * (currentX - xAp[i]))
					+ ((currentY - yAp[i]) * (currentY - yAp[i])));

			m = new Measurement(rssi, macAdresses[i]);
			m.setPosition(p);
			p.setMeasurements(ms);
			ms.add(m);
		}

		LocateService.getInstance().queryPositionFromMeasurements(ms, 0.2f);
	}
}
