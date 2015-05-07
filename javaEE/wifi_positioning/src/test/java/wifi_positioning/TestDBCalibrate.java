package wifi_positioning;

import fr.utbm.lo53.wifipositioning.model.Measurement;
import fr.utbm.lo53.wifipositioning.model.Position;
import fr.utbm.lo53.wifipositioning.service.CalibrateService;

public class TestDBCalibrate
{
	public static void main(
			final String[] args)
	{
		Measurement m = new Measurement(66666666, "00:00:00:00:00:01");
		Position p = new Position(1, 1, m);
		m.setPosition(p);
		CalibrateService.getInstance().insertSample(p, m);
	}
}
