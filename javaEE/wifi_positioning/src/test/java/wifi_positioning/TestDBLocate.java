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

		Position p1 = new Position();
		p1.setX(1.0f);
		p1.setY(2.0f);

		Position p2 = new Position();
		p2.setX(1.0f);
		p2.setY(2.0f);

		Position p3 = new Position();
		p3.setX(1.0f);
		p3.setY(2.0f);

		Measurement m1 = new Measurement(10000.0f, "00:00:00:00:00:01");
		m1.setPosition(p1);
		p1.setMeasurements(ms);

		Measurement m2 = new Measurement(20000.0f, "00:00:00:00:00:02");
		m2.setPosition(p2);
		p2.setMeasurements(ms);

		Measurement m3 = new Measurement(30000.0f, "00:00:00:00:00:03");
		m3.setPosition(p3);
		p3.setMeasurements(ms);

		ms.add(m1);
		ms.add(m2);
		ms.add(m3);

		LocateService.getInstance().queryPositionFromMeasurements(ms, 1000.0f);
	}
}
