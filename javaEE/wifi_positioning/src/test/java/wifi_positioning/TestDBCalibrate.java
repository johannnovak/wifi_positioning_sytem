package wifi_positioning;

import java.util.HashSet;
import java.util.Set;

import fr.utbm.lo53.wifipositioning.model.Measurement;
import fr.utbm.lo53.wifipositioning.model.Position;
import fr.utbm.lo53.wifipositioning.service.CalibrateService;

public class TestDBCalibrate
{
	public static void main(
			final String[] args)
	{
		double x = 0, y = 0;
		String[] macAdresses = { "00:73:8d:9e:cb:ab", "00:73:8d:9e:cb:ab", "00:73:8d:9e:cb:ab" };
		double[] xAp = { 3, 10, 20 };
		double[] yAp = { 1, 15, 8 };

		for (int i = 0; i < 25; ++i)
			for (int j = 0; j < 25; ++j)
			{
				Set<Measurement> set = new HashSet<Measurement>();
				Position p = new Position();
				p.setX(i);
				p.setY(j);
				p.setMeasurements(set);
				for (int k = 0; k < 3; ++k)
				{
					Measurement m = null;
					x = i;
					y = j;
					double rssi = Math.sqrt(((x - xAp[k]) * (x - xAp[k]))
							+ ((y - yAp[k]) * (y - yAp[k])));

					m = new Measurement((float) rssi, macAdresses[k]);
					m.setPosition(p);
					set.add(m);
				}

				CalibrateService.getInstance().insertSample(p);
			}
		// Measurement m = new Measurement(10.8f, "00:00:00:00:00:03");
		// Position p = new Position(1, 1, m);
		// m.setPosition(p);
		// CalibrateService.getInstance().insertSample(p);
	}
}
