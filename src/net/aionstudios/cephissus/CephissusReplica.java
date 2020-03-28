package net.aionstudios.cephissus;

import java.io.File;

import net.aionstudios.aionlog.AnsiOut;
import net.aionstudios.aionlog.Logger;
import net.aionstudios.aionlog.StandardOverride;
import net.aionstudios.cephissus.cron.CronDateTime;
import net.aionstudios.cephissus.cron.CronManager;
import net.aionstudios.cephissus.crons.ApiDeltasCron;
import net.aionstudios.cephissus.crons.ApiHeartbeatCron;
import net.aionstudios.cephissus.crons.ApiTokenCron;

public class CephissusReplica {
	
	public static void main(String[] args) {
		File f = new File("./logs/");
		f.mkdirs();
		Logger.setup();
		AnsiOut.initialize();
		AnsiOut.setStreamPrefix("Cephissus Replica");
		StandardOverride.enableOverride();
		CephissusInfo.readConfigsAtStart();
		
		System.setProperty("javax.net.ssl.trustStore", "./certs/cert.jks");
		System.setProperty("java.net.preferIPv4Stack" , "true");
		
		/*Cron*/
		CronDateTime quarters = new CronDateTime();
		quarters.setMinuteRange(0, 0);
		quarters.appendMinuteRange(15, 15);
		quarters.appendMinuteRange(30, 30);
		quarters.appendMinuteRange(45, 45);
		
		CronDateTime fourcdt = new CronDateTime();
		fourcdt.setMinuteRange(0, 0);
		for(int i = 1; i < 15; i++) fourcdt.appendMinuteRange(i*4, i*4);
		
		ApiTokenCron atc = new ApiTokenCron(quarters);
		atc.run();
		
		CronManager.addJob(atc);
		CronManager.addJob(new ApiHeartbeatCron(new CronDateTime()));
		CronManager.addJob(new ApiDeltasCron(fourcdt));
		CronManager.startCron();
	}

}
