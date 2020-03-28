package net.aionstudios.cephissus.crons;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import net.aionstudios.cephissus.CephissusInfo;
import net.aionstudios.cephissus.cron.CronDateTime;
import net.aionstudios.cephissus.cron.CronJob;
import net.aionstudios.cephissus.service.OutgoingRequest;
import net.aionstudios.cephissus.service.OutgoingRequestService;

public class ApiHeartbeatCron extends CronJob {

	public ApiHeartbeatCron(CronDateTime cdt) {
		super(cdt);
	}

	@Override
	public void run() {
		OutgoingRequest or = new OutgoingRequest("", null);
		Map<String, String> postQuery = new HashMap<String, String>();
		postQuery.put("apiToken", CephissusInfo.getApiToken());
		String rp = OutgoingRequestService.executePost(
				"https://"+CephissusInfo.getPrimaryHost()+":26723/peer?action=heartbeat",
				OutgoingRequestService.postMapToString(postQuery),
				or);
		try {
			if(CephissusInfo.getLastDelta()==0) {
				JSONObject jo = new JSONObject(rp);
				CephissusInfo.setLastDelta(jo.getJSONObject("data").getLong("time_now"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

}
