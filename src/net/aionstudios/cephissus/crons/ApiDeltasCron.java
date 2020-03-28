package net.aionstudios.cephissus.crons;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.aionstudios.cephissus.CephissusInfo;
import net.aionstudios.cephissus.cron.CronDateTime;
import net.aionstudios.cephissus.cron.CronJob;
import net.aionstudios.cephissus.service.OutgoingRequest;
import net.aionstudios.cephissus.service.OutgoingRequestService;

public class ApiDeltasCron extends CronJob {
	
	public ApiDeltasCron(CronDateTime cdt) {
		super(cdt);
	}

	@Override
	public void run() {
		if(CephissusInfo.getLastDelta()!=0) {
			OutgoingRequest or = new OutgoingRequest("", null);
			Map<String, String> postQuery = new HashMap<String, String>();
			postQuery.put("apiToken", CephissusInfo.getApiToken());
			String rp = OutgoingRequestService.executePost(
					"https://"+CephissusInfo.getPrimaryHost()+":26723/fileSystem?action=deltas&deltaTime="+CephissusInfo.getLastDelta(),
					OutgoingRequestService.postMapToString(postQuery),
					or);
			try {
				JSONObject jo = new JSONObject(rp);
				JSONArray deltas = jo.getJSONObject("data").getJSONArray("deltas");
				for(int i = 0; i < deltas.length(); i++) {
					JSONObject delta = deltas.getJSONObject(i);
					String path = fixFileName(delta.getString("path"));
					String kind = delta.getString("kind");
					long time = delta.getLong("time");
					if(time>CephissusInfo.getLastDelta()) CephissusInfo.setLastDelta(time);
					boolean isDir = delta.getBoolean("dir");
					File f = new File("replicate/"+path);
					if(kind.equals("DELETE")) {
						deleteRecursive(f);
					} else {
						if(!f.exists()) {
							f.getParentFile().mkdirs();
							if(isDir) {
								f.mkdir();
							} else {
								try {
									f.createNewFile();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						if(!isDir) {
							resolveDelta(f, path);
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void deleteRecursive(File f) {
		if (f.exists()) {
			if (f.isDirectory()) {
				for(String child : f.list()) {
					File c = new File(fixFileName(f.toPath().toString())+"/"+fixFileName(child));
					deleteRecursive(c);
				}
			}
			System.out.println("removing "+fixFileName(f.getPath()));
			f.delete();
		}
	}
	
	private String fixFileName(String s) {
		while(s.contains("\\\\")) {
			s = s.replace("\\\\", "\\");
		}
		return s.replace("\\", "/");
	}
	
	private void resolveDelta(File f, String path) {
		OutgoingRequest or = new OutgoingRequest("", null);
		Map<String, String> postQuery = new HashMap<String, String>();
		postQuery.put("apiToken", CephissusInfo.getApiToken());
		String rp = OutgoingRequestService.executePost(
				"https://"+CephissusInfo.getPrimaryHost()+":26723/fileSystem?action=retrieve&file="+URLEncoder.encode(fixFileName(path), StandardCharsets.UTF_8),
				OutgoingRequestService.postMapToString(postQuery),
				or);
		try {
			JSONObject jo = new JSONObject(rp);
			if(jo.has("error")) {
				return;
			}
			System.out.println("writing "+path);
			f.getParentFile().mkdirs();
			if(!f.exists()) {
				f.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(f);
			byte[] decoder = Base64.getDecoder().decode(jo.getJSONObject("data").getString("blob"));
			fos.write(decoder);
			fos.close();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
