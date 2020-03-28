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

public class ApiTokenCron extends CronJob {

	public ApiTokenCron(CronDateTime cdt) {
		super(cdt);
	}

	@Override
	public void run() {
		OutgoingRequest or = new OutgoingRequest("", null);
		Map<String, String> postQuery = new HashMap<String, String>();
		postQuery.put("apiKey", CephissusInfo.getApiKey());
		postQuery.put("apiSecret", CephissusInfo.getApiSecret());
		String rp = OutgoingRequestService.executePost(
				"https://"+CephissusInfo.getPrimaryHost()+":26723/account?action=login",
				OutgoingRequestService.postMapToString(postQuery),
				or);
		try {
			JSONObject jo = new JSONObject(rp);
			CephissusInfo.setApiToken(jo.getJSONObject("data").getString("api_token"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
