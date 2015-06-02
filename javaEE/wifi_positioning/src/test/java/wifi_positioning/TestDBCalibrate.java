package wifi_positioning;

import fr.utbm.lo53.wifipositioning.model.Measurement;
import fr.utbm.lo53.wifipositioning.model.Position;
import fr.utbm.lo53.wifipositioning.service.CalibrateService;

public class TestDBCalibrate
{
	public static void main(
			final String[] args)
	{
		double x = 0, y = 0;
		String[] macAdresses = { "00:00:00:00:00:00", "11:11:11:11:11:11", "22:22:22:22:22:22" };
		double[] xAp = { 600, 800, 40000 };
		double[] yAp = { 200, 30000, 15000 };

		for (int i = 0; i < 25; ++i)
			for (int j = 0; j < 25; ++j)
				for (int k = 0; k < 3; ++k)
				{
					Measurement m = null;
					x = 200 * i;
					y = 200 * j;
					double rssi = Math.sqrt(((x - xAp[k]) * (x - xAp[k]))
							+ ((y - yAp[k]) * (y - yAp[k])));

					m = new Measurement((float) rssi, macAdresses[k]);
					Position p = new Position(i, j, m);
					m.setPosition(p);
					CalibrateService.getInstance().insertSample(p);
				}
		// Measurement m = new Measurement(10.8f, "00:00:00:00:00:03");
		// Position p = new Position(1, 1, m);
		// m.setPosition(p);
		// CalibrateService.getInstance().insertSample(p);
	}
}
